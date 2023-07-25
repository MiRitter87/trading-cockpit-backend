package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import backend.dao.quotation.provider.QuotationProviderMarketWatchDAO;
import backend.model.StockExchange;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the MarketWatch CSV downloader.
 * Local CSV files are used instead of a live query to MarketWatch.
 * 
 * @author Michael
 */
public class QuotationProviderMarketWatchDAOStub extends QuotationProviderMarketWatchDAO {
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, InstrumentType instrumentType, 
			Integer years) throws Exception {
		
		String csvPath = "";
		
		if(symbol.equals("DML") && stockExchange.equals(StockExchange.TSX))
			csvPath = "src/test/resources/MarketWatch/MarketWatchTSEQuotationHistoryDML.csv";
		else if(symbol.equals("RIO") && stockExchange.equals(StockExchange.LSE))
			csvPath = "src/test/resources/MarketWatch/MarketWatchLSEQuotationHistoryRIO.csv";
		else
			return null;
		
		String quotationHistoryCSV = Files.readString(Paths.get(csvPath));
		
		return this.convertCSVToQuotations(quotationHistoryCSV, stockExchange);
	}
}
