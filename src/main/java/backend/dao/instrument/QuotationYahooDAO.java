package backend.dao.instrument;

import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the finance API of Yahoo.
 * 
 * @author Michael
 */
public class QuotationYahooDAO implements QuotationDAO {

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		// TODO Auto-generated method stub
		//The functionality of the StockQuoteYahooDAO, method "getStockQuote" can be integrated into this method.
		return null;
	}

	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years)
			throws Exception {
		// TODO Auto-generated method stub
		//Query the Yahoo WebService to get historical data.
		return null;
	}

}
