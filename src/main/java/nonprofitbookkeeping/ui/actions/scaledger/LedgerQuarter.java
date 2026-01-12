package nonprofitbookkeeping.ui.actions.scaledger;

import java.util.ArrayList;
import java.util.List;

public class LedgerQuarter {
    private final String sheetName;
    private final List<LedgerRow> rows = new ArrayList<>();

    public LedgerQuarter(String sheetName) { this.sheetName = sheetName; }
    public String getSheetName() { return sheetName; }
    public List<LedgerRow> getRows() { return rows; }
    public void addRow(LedgerRow row) { if (row != null && !row.isEffectivelyBlank()) rows.add(row); }
}
