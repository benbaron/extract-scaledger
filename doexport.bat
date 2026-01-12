@echo off
setlocal ENABLEDELAYEDEXPANSION

REM Resolve project root (directory of this .bat)
set "SCRIPT_DIR=%~dp0"
REM Remove trailing backslash if present
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

set "JAR=%SCRIPT_DIR%\target\ledger-to-csv.jar"

if not exist "%JAR%" (
  echo [INFO] Fat jar not found: %JAR%
  echo [INFO] Building with Maven (mvn -q -DskipTests package)...
  pushd "%SCRIPT_DIR%"
  mvn -q -DskipTests package
  if errorlevel 1 (
    echo [ERROR] Maven build failed. Ensure Maven is installed and on PATH.
    popd
    exit /b 1
  )
  popd
)

if "%~1"=="" (
  echo Usage: %~nx0 --chart ^<chart-map.json^> --workbook ^<ledger.xlsm^> --sheet ^<SheetName^> --out ^<out.csv^> [--types ^<types.json^>] [--encoding UTF-8] [--include-debit-credit]
  echo.
  echo Example:
  echo   %~nx0 --chart src\main\resources\chart-map.json --workbook "CG Ledger 2024 Q4 v3.xlsm" --sheet Ledger_Q4 --out out.csv --encoding UTF-8 --include-debit-credit --types src\main\resources\types.json
  exit /b 0
)

java -jar "%JAR%" %*
endlocal
