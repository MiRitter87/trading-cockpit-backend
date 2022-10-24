package backend.dao.quotation;

import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the CSV download function of MarketWatch.
 * 
 * @author Michael
 */
public class QuotationProviderMarketWatchDAO implements QuotationProviderDAO {

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
