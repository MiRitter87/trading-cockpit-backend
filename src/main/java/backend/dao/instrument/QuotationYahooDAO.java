package backend.dao.instrument;

import java.util.Date;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the finance API of Yahoo.
 * 
 * @author Michael
 */
public class QuotationYahooDAO implements QuotationDAO {
	/**
	 * Placeholder for the symbol used in a query URL.
	 */
	private static final String PLACEHOLDER_SYMBOL = "{symbol}";
	
	/**
	 * URL to quote API of Yahoo finance: Historical quotations.
	 */
	private static final String BASE_URL_QUOTATION_HISTORY = "https://query1.finance.yahoo.com/v7/finance/chart/"
			+ PLACEHOLDER_SYMBOL + "?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		// TODO Auto-generated method stub
		//The functionality of the StockQuoteYahooDAO, method "getStockQuote" can be integrated into this method.
		return null;
	}

	
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		// TODO Auto-generated method stub
		//Query the Yahoo WebService to get historical data.
		return null;
	}

	
	/**
	 * Gets the query URL for the quotation history of the given symbol and stock exchange.
	 * 
	 * @param symbol The symbol to be queried.
	 * @param stockExchange The stock exchange where the symbol is listed.
	 * @return The query URL.
	 */
	protected String getQueryUrlQuotationHistory(final String symbol, final StockExchange stockExchange) {
		StringBuilder symbolForUrl = new StringBuilder();
		String queryUrl = new String(BASE_URL_QUOTATION_HISTORY);
		
		symbolForUrl.append(symbol);
		symbolForUrl.append(this.getExchangeForQueryURL(stockExchange));
		
		queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, symbolForUrl.toString());
		
		return queryUrl;
	}
	
	
	/**
	 * Gets the stock exchange for construction of the query URL.
	 * 
	 * @param stockExchange The stock exchange of the internal data model.
	 * @return The stock exchange as used in the query URL.
	 */
	protected String getExchangeForQueryURL(final StockExchange stockExchange) {
		StringBuilder stockExchangeBuilder = new StringBuilder("");
		
		switch(stockExchange) {
			case TSX:
				stockExchangeBuilder.append(".");
				stockExchangeBuilder.append("TO");
				return stockExchangeBuilder.toString();
			case TSXV:
				stockExchangeBuilder.append(".");
				stockExchangeBuilder.append("V");
				return stockExchangeBuilder.toString();
			case NYSE:
			default:
				return stockExchangeBuilder.toString();
		}
	}
	
	
	/**
	 * Provides a date based on the given timestamp.
	 * 
	 * @param timestamp Integer value representing date in seconds since 01.01.1970.
	 * @return A date object.
	 */
	protected Date getDate(Object timestamp) {
		long timestampInSeconds = Long.valueOf((Integer)timestamp);
		long timestampInMilliseconds = timestampInSeconds * 1000;
		
		return new Date(timestampInMilliseconds);
	}
}
