package nonprofitbookkeeping.ui.actions.scaledger;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.EncryptedDocumentException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LedgerSheetImporter {
    public LedgerQuarter importQuarter(Path workbookPath, String sheetName, ChartTranslationMap translation) throws IOException {
        try (InputStream in = Files.newInputStream(workbookPath);
             Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) throw new IOException("Sheet not found: " + sheetName);
            int headerRowIdx = findHeaderRow(sheet);
            if (headerRowIdx < 0) throw new IOException("Could not locate header row");
            ColumnIndex cols = mapColumns(sheet.getRow(headerRowIdx));

            LedgerQuarter quarter = new LedgerQuarter(sheetName);
            int startDataRow = headerRowIdx + 1;
            int lastRow = sheet.getLastRowNum();

            for (int r = startDataRow; r <= lastRow; r++) {
                Row poiRow = sheet.getRow(r);
                if (poiRow == null) continue;

                LedgerRow row = new LedgerRow();
                row.setSheetRowNumber(r);
                row.setDate(CellUtil.readDate(poiRow, cols.colDate));
                row.setCheckNumber(CellUtil.readString(poiRow, cols.colCheckNum));
                row.setClearedBankTag(CellUtil.readString(poiRow, cols.colClearBank));
                row.setToFrom(CellUtil.readString(poiRow, cols.colToFrom));
                row.setMemo(CellUtil.readString(poiRow, cols.colMemo));
                row.setBudgetNotes(CellUtil.readString(poiRow, cols.colBudgetNotes));

                addSplitFromGroup(poiRow, cols.group1, row, translation);
                addSplitFromGroup(poiRow, cols.group2, row, translation);
                addSplitFromGroup(poiRow, cols.group3, row, translation);
                addSplitFromGroup(poiRow, cols.group4, row, translation);

                if (!row.isEffectivelyBlank()) quarter.addRow(row);
            }
            return quarter;
        } catch (EncryptedDocumentException | InvalidFormatException e) {
            throw new IOException("Invalid Excel format: " + e.getMessage(), e);
        }
    }

    private int findHeaderRow(Sheet sheet) {
        int scanMax = Math.min(sheet.getLastRowNum(), 30);
        for (int r = sheet.getFirstRowNum(); r <= scanMax; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            boolean sawDate = false, sawToFrom = false;
            for (Cell c : row) {
                String v = CellUtil.readString(row, c.getColumnIndex());
                if (v == null) continue;
                String up = v.trim().toUpperCase();
                if (up.equals("DATE")) sawDate = true;
                if (up.startsWith("TO/FROM")) sawToFrom = true;
            }
            if (sawDate && sawToFrom) return r;
        }
        return -1;
    }

    private ColumnIndex mapColumns(Row headerRow) {
        ColumnIndex idx = new ColumnIndex();
        for (Cell c : headerRow) {
            String t = CellUtil.readString(headerRow, c.getColumnIndex());
            if (t == null) continue;
            String norm = t.trim().toUpperCase();
            if (norm.equals("DATE")) idx.colDate = c.getColumnIndex();
            else if (norm.equals("CHECK #") || norm.equals("CHECK#")) idx.colCheckNum = c.getColumnIndex();
            else if (norm.startsWith("CLEAR BANK")) idx.colClearBank = c.getColumnIndex();
            else if (norm.startsWith("TO/FROM")) idx.colToFrom = c.getColumnIndex();
            else if (norm.startsWith("MEMO/NOTES")) idx.colMemo = c.getColumnIndex();
            else if (norm.startsWith("BUDGET TRACKING")) idx.colBudgetNotes = c.getColumnIndex();
        }
        idx.group1 = detectSplitGroup(headerRow, 0);
        idx.group2 = detectSplitGroup(headerRow, (idx.group1 != null ? idx.group1.amountCol + 1 : 0));
        idx.group3 = detectSplitGroup(headerRow, (idx.group2 != null ? idx.group2.amountCol + 1 : 0));
        idx.group4 = detectSplitGroup(headerRow, (idx.group3 != null ? idx.group3.amountCol + 1 : 0));
        return idx;
    }

    private SplitGroup detectSplitGroup(Row headerRow, int startSearchCol) {
        int maxCol = headerRow.getLastCellNum();
        for (int c = startSearchCol; c < maxCol; c++) {
            String h0 = CellUtil.readString(headerRow, c);
            if (h0 == null || !h0.trim().equalsIgnoreCase("AMOUNT")) continue;
            String h1 = CellUtil.readString(headerRow, c+1);
            String h2 = CellUtil.readString(headerRow, c+2);
            String h3 = CellUtil.readString(headerRow, c+3);
            String h4 = CellUtil.readString(headerRow, c+4);
            if (h1 == null || h2 == null || h3 == null || h4 == null) continue;
            String u1=h1.trim().toUpperCase(), u2=h2.trim().toUpperCase(), u3=h3.trim().toUpperCase(), u4=h4.trim().toUpperCase();
            boolean looksRight = u1.startsWith("ASSET/LIABILITY ACCOUNT") && u2.startsWith("INCOME CATEGORY")
                               && u3.startsWith("EXPENSE CATEGORY") && u4.startsWith("GENERAL OR DEDICATED FUND");
            if (looksRight) {
                SplitGroup g = new SplitGroup();
                g.amountCol = c; g.assetLiabCol=c+1; g.incomeCatCol=c+2; g.expenseCatCol=c+3; g.fundCol=c+4;
                return g;
            }
        }
        return null;
    }

    private void addSplitFromGroup(Row poiRow, SplitGroup g, LedgerRow out, ChartTranslationMap tx) {
        if (g == null) return;
        LedgerSplit s = new LedgerSplit();
        s.setAmount(CellUtil.readAmount(poiRow, g.amountCol));
        s.setAssetLiabilityAccount(CellUtil.readString(poiRow, g.assetLiabCol));
        s.setIncomeCategory(CellUtil.readString(poiRow, g.incomeCatCol));
        s.setExpenseCategory(CellUtil.readString(poiRow, g.expenseCatCol));
        s.setFund(CellUtil.readString(poiRow, g.fundCol));
        String raw = s.getPrimaryRawCategory();
        s.setCanonicalCategory(tx != null ? tx.translate(raw) : null);
        if (!s.isEmpty()) out.addSplit(s);
    }

    private static class ColumnIndex {
        int colDate=-1, colCheckNum=-1, colClearBank=-1, colToFrom=-1, colMemo=-1, colBudgetNotes=-1;
        SplitGroup group1, group2, group3, group4;
    }
    private static class SplitGroup {
        int amountCol, assetLiabCol, incomeCatCol, expenseCatCol, fundCol;
    }
}
