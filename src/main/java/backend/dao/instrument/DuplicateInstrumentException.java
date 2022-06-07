package backend.dao.instrument;

import backend.model.StockExchange;

/**
 * Exception that indicates that an instrument already exists.
 * 
 * @author Michael
 */
public class DuplicateInstrumentException extends Exception {
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1117239627678518889L;

	/**
	 * The symbol of the duplicate.
	 */
	private String symbol;
	
	/**
	 * The stock exchange of the duplicate.
	 */
	private StockExchange stockExchange;
	
	
	/**
	 * Default Constructor.
	 */
	public DuplicateInstrumentException() {
		
	}
	
	
	/**
	 * Initializes the DuplicateInstrumentException.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stockExchange.
	 */
	public DuplicateInstrumentException(final String symbol, final StockExchange stockExchange) {
		this.symbol = symbol;
		this.stockExchange = stockExchange;
	}


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
}
