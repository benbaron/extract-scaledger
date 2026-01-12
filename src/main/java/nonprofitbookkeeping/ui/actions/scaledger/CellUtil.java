package nonprofitbookkeeping.ui.actions.scaledger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

public final class CellUtil {
    private CellUtil() { }

    public static String readString(Row row, int colIdx) {
        if (row == null || colIdx < 0) return null;
        Cell cell = row.getCell(colIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        CellType type = cell.getCellType();
        if (type == CellType.STRING) return cell.getStringCellValue();
        if (type == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell)) return Double.toString(cell.getNumericCellValue());
        if (type == CellType.FORMULA) return cell.getCellFormula();
        return null;
    }

    public static LocalDate readDate(Row row, int colIdx) {
        if (row == null || colIdx < 0) return null;
        Cell cell = row.getCell(colIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static BigDecimal readAmount(Row row, int colIdx) {
        if (row == null || colIdx < 0) return java.math.BigDecimal.ZERO;
        Cell cell = row.getCell(colIdx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return java.math.BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case NUMERIC:
                return java.math.BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                String s = cell.getStringCellValue();
                if (s == null) return java.math.BigDecimal.ZERO;
                s = s.trim();
                if (s.isEmpty()) return java.math.BigDecimal.ZERO;
                s = s.replace(",", "");
                try { return new java.math.BigDecimal(s); }
                catch (NumberFormatException ex) { return java.math.BigDecimal.ZERO; }
            default:
                return java.math.BigDecimal.ZERO;
        }
    }
}
