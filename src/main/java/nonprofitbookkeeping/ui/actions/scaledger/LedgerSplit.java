package nonprofitbookkeeping.ui.actions.scaledger;

import java.math.BigDecimal;

public class LedgerSplit {
    private BigDecimal amount;
    private String assetLiabilityAccount;
    private String incomeCategory;
    private String expenseCategory;
    private String fund;
    private String canonicalCategory;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getAssetLiabilityAccount() { return assetLiabilityAccount; }
    public void setAssetLiabilityAccount(String v) { this.assetLiabilityAccount = v; }

    public String getIncomeCategory() { return incomeCategory; }
    public void setIncomeCategory(String v) { this.incomeCategory = v; }

    public String getExpenseCategory() { return expenseCategory; }
    public void setExpenseCategory(String v) { this.expenseCategory = v; }

    public String getFund() { return fund; }
    public void setFund(String fund) { this.fund = fund; }

    public String getCanonicalCategory() { return canonicalCategory; }
    public void setCanonicalCategory(String canonicalCategory) { this.canonicalCategory = canonicalCategory; }

    public String getPrimaryRawCategory() {
        if (assetLiabilityAccount != null && !assetLiabilityAccount.isBlank()) return assetLiabilityAccount;
        if (incomeCategory != null && !incomeCategory.isBlank()) return incomeCategory;
        if (expenseCategory != null && !expenseCategory.isBlank()) return expenseCategory;
        return null;
    }

    public boolean isEmpty() {
        boolean amountIsZero = (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) == 0);
        String primary = getPrimaryRawCategory();
        boolean noCategory = (primary == null || primary.isBlank());
        return amountIsZero && noCategory;
    }
}
