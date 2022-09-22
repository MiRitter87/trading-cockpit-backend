package backend.model.priceAlert;

import java.math.BigDecimal;
import java.util.Date;

import backend.model.Currency;

/**
 * A lean version of a PriceAlert that is used by the WebService to transfer object data.
 * The main difference to the regular PriceAlert is that IDs are used instead of object references.
 * 
 * @author Michael
 */
public class PriceAlertWS {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The Instrument ID.
	 */
	private Integer instrumentId;
	
	/**
	 * The type of the price alert.
	 */
	private PriceAlertType alertType;
	
	/**
	 * The price at which the alert is activated.
	 */
	private BigDecimal price;
	
	/**
	 * The currency.
	 */
	private Currency currency;
	
	/**
	 * The distance between the current price and the trigger level in percent.
	 */
	private float triggerDistancePercent;
	
	/**
	 * The time at which the alert has been triggered.
	 */
	private Date triggerTime;
	
	/**
	 * The time at which the user as confirmed the alert.
	 */
	private Date confirmationTime;
	
	/**
	 * The time of the last stock quote query.
	 */
	private Date lastStockQuoteTime;
	
	
	/**
	 * Default Constructor.
	 */
	public PriceAlertWS() {
		
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
	 * @return the instrumentId
	 */
	public Integer getInstrumentId() {
		return instrumentId;
	}


	/**
	 * @param instrumentId the instrumentId to set
	 */
	public void setInstrumentId(Integer instrumentId) {
		this.instrumentId = instrumentId;
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


	/**
	 * @return the triggerDistancePercent
	 */
	public float getTriggerDistancePercent() {
		return triggerDistancePercent;
	}


	/**
	 * @param triggerDistancePercent the triggerDistancePercent to set
	 */
	public void setTriggerDistancePercent(float triggerDistancePercent) {
		this.triggerDistancePercent = triggerDistancePercent;
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


	/**
	 * @return the lastStockQuoteTime
	 */
	public Date getLastStockQuoteTime() {
		return lastStockQuoteTime;
	}


	/**
	 * @param lastStockQuoteTime the lastStockQuoteTime to set
	 */
	public void setLastStockQuoteTime(Date lastStockQuoteTime) {
		this.lastStockQuoteTime = lastStockQuoteTime;
	}
}
