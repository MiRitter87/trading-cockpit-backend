package backend.dao.instrument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import backend.model.Currency;
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
	
	
	/**
	 * Gets the currency from the Yahoo finance API.
	 * 
	 * @param apiCurrency The currency as provided by Yahoo finance.
	 * @return The currency as used by the backend.
	 */
	protected Currency getCurrency(String apiCurrency) {
		switch(apiCurrency) {
			case "USD":
				return Currency.USD;
			case "CAD":
				return Currency.CAD;
			default:
				return null;
		}
	}
	
	
	/**
	 * Gets the volume data from the Yahoo finance API.
	 * 
	 * @param volume A list of historical volume data.
	 * @param index The index at which the volume data are to be extracted.
	 * @return The volume.
	 */
	protected long getVolumeFromQuotationHistoryResponse(final ArrayList<?> volume, final int index) {
		return Long.valueOf((Integer)volume.get(index));
	}
	
	
	/**
	 * Gets the adjusted closing price from the Yahoo finance API.
	 * 
	 * @param adjustedClose A list of historical adjusted closing prices.
	 * @param index The index at which the adjusted closing price is to be extracted.
	 * @return The adjusted closing price.
	 */
	protected BigDecimal getAdjustedCloseFromQuotationHistoryResponse(final ArrayList<?> adjustedClose, final int index) {
		double adjustedCloseRaw = (double) adjustedClose.get(index);
		BigDecimal adjustedClosingPrice = BigDecimal.valueOf(adjustedCloseRaw);
		
		adjustedClosingPrice = adjustedClosingPrice.setScale(2, RoundingMode.HALF_UP);
		
		
		return adjustedClosingPrice;
	}
}
