package backend.model.stockQuote;

import java.math.BigDecimal;

import backend.model.Currency;
import backend.model.StockExchange;

/**
 * The current state of stock data provided by a third party.
 * 
 * @author Michael
 */
public class StockQuote {
	/**
	 * The stock symbol.
	 */
	private String symbol;
	
	/**
	 * The exchange where the stock is listed.
	 */
	private StockExchange stockExchange;
	
	/**
	 * The current price of the stock.
	 */
	private BigDecimal price;
	
	/**
	 * The currency of the price.
	 */
	private Currency currency;

	
	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	
	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	
	/**
	 * @return the stockExchange
	 */
	public StockExchange getStockExchange() {
		return stockExchange;
	}

	
	/**
	 * @param stockExchange the stockExchange to set
	 */
	public void setStockExchange(StockExchange stockExchange) {
		this.stockExchange = stockExchange;
	}

	
	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	
	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	
	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
}
