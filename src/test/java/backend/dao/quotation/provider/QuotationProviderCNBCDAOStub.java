package backend.dao.quotation.provider;

import java.nio.file.Files;
import java.nio.file.Paths;

import backend.dao.quotation.provider.QuotationProviderCNBCDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the API of CNBC.
 * A local JSON file is used instead of a live query to CNBC.
 * 
 * @author Michael
 */
public class QuotationProviderCNBCDAOStub extends QuotationProviderCNBCDAO {
	@Override
	public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
		String jsonPath = "";
		
		if(instrument.getSymbol().equals("RIO") && instrument.getStockExchange().equals(StockExchange.LSE))
			jsonPath = "src/test/resources/cnbcLSEQuoteRIO.json";
		else
			return null;
		
		String currentQuotationJSON = Files.readString(Paths.get(jsonPath));
		
		return this.convertJSONToQuotation(currentQuotationJSON);
	}
}
