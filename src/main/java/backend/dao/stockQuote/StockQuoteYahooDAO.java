package backend.dao.stockQuote;

import java.math.BigDecimal;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.stockQuote.StockQuote;

/**
 * Retrieves stock data using the finance API of Yahoo.
 * 
 * @author Michael
 *
 */
public class StockQuoteYahooDAO implements StockQuoteDAO {
	/**
	 * URL to quote API of Yahoo finance.
	 */
	private static final String BASE_URL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=";
	
	
	@Override
	public StockQuote getStockQuote(String symbol, StockExchange stockExchange) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Gets the exchange as used by the backend based on the exchange provided by Yahoo finance.
	 * 
	 * @param apiExchange The exchange as provided by Yahoo finance.
	 * @return The exchange as used by the backend
	 */
	protected StockExchange getExchange(String apiExchange) {
		switch(apiExchange) {
			case "VAN":
				return StockExchange.TSXV;
			case "TOR":
				return StockExchange.TSX;
			case "NYQ":	//NYSE
			case "NMS":	//Nasdaq
				return StockExchange.NYSE;
			default:
				return null;
		}
	}
	
	
	/**
	 * Gets the symbol from the Yahoo finance API.
	 * 
	 * @param apiSymbol The symbol as provided by Yahoo finance.
	 * @return Ther symbol as used by the backend.
	 */
	protected String getSymbol(String apiSymbol) {
		return apiSymbol.split("\\.")[0];
	}
	
	
	/**
	 * Gets the price from the Yahoo finance API.
	 * 
	 * @param apiPrice The price as provided by Yahoo finance.
	 * @return The price as used by the backend
	 */
	protected BigDecimal getPrice(double apiPrice) {
		return BigDecimal.valueOf(apiPrice);
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
	 * Gets the query URL for the given symbol and stock exchange.
	 * 
	 * @param symbol The symbol to be queried.
	 * @param stockExchange The stock exchange where the symbol is listed.
	 * @return The query URL.
	 */
	protected String getQueryUrl(final String symbol, final StockExchange stockExchange) {
		StringBuilder urlBuilder = new StringBuilder(BASE_URL);
		
		urlBuilder.append(symbol);
		urlBuilder.append(this.getExchangeForQueryURL(stockExchange));
		
		return urlBuilder.toString();
	}
}
