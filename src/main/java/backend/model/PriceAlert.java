package backend.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * An alert that a stock has reached a certain price at a stock exchange.
 * 
 * @author Michael
 */
public class PriceAlert {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The stock symbol.
	 */
	private String symbol;
	
	/**
	 * The exchange where the stock is listed.
	 */
	private StockExchange stockExchange;
	
	/**
	 * The type of the price alert.
	 */
	private PriceAlertType alertType;
	
	/**
	 * The price at which the alert is activated.
	 */
	private BigDecimal price;
	
	/**
	 * The time at which the alert has been triggered.
	 */
	private Date triggerTime;
	
	/**
	 * The time at which the user as confirmed the alert.
	 */
	private Date confirmationTime;
	
	
	/**
	 * Default constructor.
	 */
	public PriceAlert() {
		
	}


	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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


	/**
	 * @return the alertType
	 */
	public PriceAlertType getAlertType() {
		return alertType;
	}


	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(PriceAlertType alertType) {
		this.alertType = alertType;
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
	 * @return the triggerTime
	 */
	public Date getTriggerTime() {
		return triggerTime;
	}


	/**
	 * @param triggerTime the triggerTime to set
	 */
	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}


	/**
	 * @return the confirmationTime
	 */
	public Date getConfirmationTime() {
		return confirmationTime;
	}


	/**
	 * @param confirmationTime the confirmationTime to set
	 */
	public void setConfirmationTime(Date confirmationTime) {
		this.confirmationTime = confirmationTime;
	}
}
