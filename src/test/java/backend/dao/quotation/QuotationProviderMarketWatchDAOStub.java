package backend.dao.quotation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the MarketWatch CSV downloader.
 * Local CSV files are used instead of a live query to MarketWatch.
 * 
 * @author Michael
 */
public class QuotationProviderMarketWatchDAOStub extends QuotationProviderMarketWatchDAO {
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		String csvPath = "";
		
		if(symbol.equals("DML") && stockExchange.equals(StockExchange.TSX))
			csvPath = "src/test/resources/MarketWatchTSEQuotationHistoryDML.csv";
		else
			return null;
		
		String quotationHistoryCSV = Files.readString(Paths.get(csvPath));
		
		return this.convertCSVToQuotations(quotationHistoryCSV, stockExchange);
	}
}
