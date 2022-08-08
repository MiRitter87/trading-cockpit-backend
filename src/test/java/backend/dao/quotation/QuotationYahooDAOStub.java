package backend.dao.quotation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the finance API of Yahoo.
 * A local JSON file is used instead of a live query to Yahoo finance.
 * 
 * @author Michael
 *
 */
public class QuotationYahooDAOStub extends QuotationYahooDAO {
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		String quotationHistoryJSON = Files.readString(Paths.get("src/test/resources/yahooTSEQuotationHistoryDML.json"));
		
		return this.convertJSONToQuotations(quotationHistoryJSON);
	}
}
