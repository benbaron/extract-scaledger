# Ledger to CSV (Maven project)

Standalone Java CLI that reads your quarterly Excel ledger (`.xlsx`/`.xlsm`),
applies your Chart of Accounts translation map, and exports a **general CSV** (one row per split).

Package: `nonprofitbookkeeping.ui.actions.scaledger`

## Build
```bash
mvn -q -DskipTests package
```
This will produce a fat jar: `target/ledger-to-csv.jar`

## Run
```bash
java -jar target/ledger-to-csv.jar   --chart src/main/resources/chart-map.json   --workbook "CG Ledger 2024 Q4 v3.xlsm"   --sheet Ledger_Q4   --out out.csv   --encoding UTF-8   --include-debit-credit   --types src/main/resources/types.json
```

- `--chart`: JSON mapping of **literal spreadsheet dropdown text** → **canonical** strings (punctuation preserved).
- `--types` (optional): JSON mapping canonical strings → `ASSET|LIABILITY|INCOME|EXPENSE`.
  If present **and** `--include-debit-credit` is supplied, exporter fills `Debit` or `Credit` for each split (Amount is always present).

### CSV Columns
```
Date,CheckNumber,ClearedBank,ToFrom,Memo,BudgetNotes,SheetRow,
SplitIndex,RawAssetLiability,RawIncomeCategory,RawExpenseCategory,
CanonicalCategory,Fund,Amount,Debit,Credit
```

### Notes
- No punctuation normalization. Your canonical strings are used exactly.
- Supports up to 4 split legs per row (Amount + A/L + Income + Expense + Fund).
- CRLF line endings; proper CSV quoting.

### Java Version
Targets Java 21 by default. Adjust `pom.xml` `<release>` if needed.
