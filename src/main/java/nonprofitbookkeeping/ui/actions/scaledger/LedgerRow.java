package nonprofitbookkeeping.ui.actions.scaledger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LedgerRow {
    private LocalDate date;
    private String checkNumber;
    private String clearedBankTag;
    private String toFrom;
    private String memo;
    private String budgetNotes;
    private Integer sheetRowNumber;
    private final List<LedgerSplit> splits = new ArrayList<>();

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCheckNumber() { return checkNumber; }
    public void setCheckNumber(String checkNumber) { this.checkNumber = checkNumber; }

    public String getClearedBankTag() { return clearedBankTag; }
    public void setClearedBankTag(String clearedBankTag) { this.clearedBankTag = clearedBankTag; }

    public String getToFrom() { return toFrom; }
    public void setToFrom(String toFrom) { this.toFrom = toFrom; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String getBudgetNotes() { return budgetNotes; }
    public void setBudgetNotes(String budgetNotes) { this.budgetNotes = budgetNotes; }

    public Integer getSheetRowNumber() { return sheetRowNumber; }
    public void setSheetRowNumber(Integer sheetRowNumber) { this.sheetRowNumber = sheetRowNumber; }

    public List<LedgerSplit> getSplits() { return splits; }
    public void addSplit(LedgerSplit split) { if (split != null && !split.isEmpty()) this.splits.add(split); }

    public boolean isEffectivelyBlank() {
        boolean noDate = (date == null);
        boolean allSplitsEmpty = splits.stream().allMatch(LedgerSplit::isEmpty);
        boolean noMemo = (memo == null || memo.isBlank());
        return noDate && noMemo && allSplitsEmpty;
    }
}
