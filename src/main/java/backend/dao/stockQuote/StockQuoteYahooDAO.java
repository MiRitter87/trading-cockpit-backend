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
}
