package nonprofitbookkeeping.ui.actions.scaledger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class CsvExporter {
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public void exportSplits(LedgerQuarter q, Path outFile, Charset enc, boolean includeDebitCredit, TypeMap types) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(outFile, enc)) {
            // Header
            w.write(String.join("," ,
                "Date","CheckNumber","ClearedBank","ToFrom","Memo","BudgetNotes","SheetRow",
                "SplitIndex","RawAssetLiability","RawIncomeCategory","RawExpenseCategory",
                "CanonicalCategory","Fund","Amount","Debit","Credit"
            ));
            w.write("\r\n");

            int splitIndex;
            for (LedgerRow row : q.getRows()) {
                splitIndex = 0;
                for (LedgerSplit s : row.getSplits()) {
                    splitIndex++;

                    String date = row.getDate() == null ? "" : DATE_FMT.format(row.getDate());
                    String checkNo = nullToEmpty(row.getCheckNumber());
                    String clr = nullToEmpty(row.getClearedBankTag());
                    String toFrom = nullToEmpty(row.getToFrom());
                    String memo = nullToEmpty(row.getMemo());
                    String budget = nullToEmpty(row.getBudgetNotes());
                    String sheetRow = row.getSheetRowNumber() == null ? "" : row.getSheetRowNumber().toString();

                    String rawAL = nullToEmpty(s.getAssetLiabilityAccount());
                    String rawInc = nullToEmpty(s.getIncomeCategory());
                    String rawExp = nullToEmpty(s.getExpenseCategory());
                    String canon = nullToEmpty(s.getCanonicalCategory());
                    String fund = nullToEmpty(s.getFund());
                    String amt = s.getAmount() == null ? "" : s.getAmount().toPlainString();

                    String debit = "";
                    String credit = "";
                    if (includeDebitCredit && types != null) {
                        CategoryType ct = types.typeOf(s.getCanonicalCategory());
                        if (ct != null && s.getAmount() != null) {
                            switch (ct) {
                                case ASSET:
                                case EXPENSE:
                                    debit = s.getAmount().toPlainString();
                                    break;
                                case LIABILITY:
                                case INCOME:
                                    credit = s.getAmount().toPlainString();
                                    break;
                            }
                        }
                    }

                    String[] fields = new String[] {
                        date, checkNo, clr, toFrom, memo, budget, sheetRow,
                        Integer.toString(splitIndex), rawAL, rawInc, rawExp,
                        canon, fund, amt, debit, credit
                    };
                    w.write(encodeCsv(fields));
                    w.write("\r\n");
                }
            }
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }

    private static String encodeCsv(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<fields.length;i++) {
            if (i>0) sb.append(',');
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    private static String escape(String v) {
        if (v == null) return "";
        boolean needsQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        String out = v.replace("\"", "\"\"");
        if (needsQuote) return "\"" + out + "\"";
        return out;
    }
}
