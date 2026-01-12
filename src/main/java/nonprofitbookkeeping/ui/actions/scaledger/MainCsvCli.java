
package nonprofitbookkeeping.ui.actions.scaledger;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;

public class MainCsvCli
{
	public static void main(String[] args) throws Exception
	{
		
		if (args.length == 0 || Arrays.asList(args).contains("--help"))
		{
			System.out.println(
				"Usage: java -jar ledger-to-csv.jar --chart chart-map.json --workbook ledger.xlsm --sheet Ledger_Q4 --out out.csv [--types types.json] [--encoding UTF-8] [--include-debit-credit]");
			System.exit(0);
		}
		
		Path chart = null, workbook = null, out = null, types = null;
		String sheet = null;
		Charset enc = Charset.forName("UTF-8");
		boolean includeDrCr = false;
		
		for (int i = 0; i < args.length; i++)
		{
			
			switch(args[i])
			{
				case "--chart":
					chart = Path.of(args[++i]);
					break;
					
				case "--workbook":
					workbook = Path.of(args[++i]);
					break;
					
				case "--sheet":
					sheet = args[++i];
					break;
					
				case "--out":
					out = Path.of(args[++i]);
					break;
					
				case "--types":
					types = Path.of(args[++i]);
					break;
					
				case "--encoding":
					enc = Charset.forName(args[++i]);
					break;
					
				case "--include-debit-credit":
					includeDrCr = true;
					break;
			}
			
		}
		
		if (chart == null || workbook == null || sheet == null || out == null)
		{
			throw new IllegalArgumentException(
				"Missing required args. Use --help for usage.");
		}
		
		ChartTranslationMap tx = ChartTranslationMap.fromJsonFile(chart);
		LedgerSheetImporter importer = new LedgerSheetImporter();
		LedgerQuarter q = importer.importQuarter(workbook, sheet, tx);
		
		CsvExporter exporter = new CsvExporter();
		TypeMap tmap =
			(types != null ? TypeMap.fromJsonFile(types) : new TypeMap());
		exporter.exportSplits(q, out, enc, includeDrCr, tmap);
		
		System.out.println("Wrote CSV: " + out.toAbsolutePath());
		
	}
	
}
