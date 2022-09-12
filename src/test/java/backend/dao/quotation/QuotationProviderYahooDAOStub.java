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
public class QuotationProviderYahooDAOStub extends QuotationProviderYahooDAO {
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		String jsonPath = "";
		
		if(symbol.equals("DML") && stockExchange.equals(StockExchange.TSX))
			jsonPath = "src/test/resources/yahooTSEQuotationHistoryDML.json";
		else if(symbol.equals("BNCH") && stockExchange.equals(StockExchange.TSXV))
			jsonPath = "src/test/resources/yahooTSXVQuotationHistoryBNCH.json";
		else
			return null;
		
		String quotationHistoryJSON = Files.readString(Paths.get(jsonPath));
		
		return this.convertJSONToQuotations(quotationHistoryJSON);
	}
	
	
	@Override
	public Quotation getCurrentQuotation(final String symbol, final StockExchange stockExchange) throws Exception {
		String currentQuotationJSON = Files.readString(Paths.get("src/test/resources/yahooTSEQuoteDML.json"));
		
		return this.convertJSONToQuotation(currentQuotationJSON);
	}
}
