package backend.dao.stockQuote;

import backend.model.StockExchange;
import backend.model.stockQuote.StockQuote;

/**
 * Interface for stock quotes.
 * 
 * @author Michael
 */
public interface StockQuoteDAO {
	/**
	 * Retrieves stock data.
	 * 
	 * @param symbol The symbol of the stock.
	 * @param stockExchange The exchange where the stock is traded.
	 * @return The stock quote.
	 * @throws Exception In case an error occurred during data determination
	 */
	StockQuote getStockQuote(final String symbol, final StockExchange stockExchange) throws Exception;
}
