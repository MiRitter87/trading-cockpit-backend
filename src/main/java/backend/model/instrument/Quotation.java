package backend.model.instrument;

import java.math.BigDecimal;
import java.util.Date;

import backend.model.Currency;

/**
 * Quotation data of an instrument.
 * 
 * @author Michael
 */
public class Quotation {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The date.
	 */
	private Date date;
	
	/**
	 * The price.
	 */
	private BigDecimal price;
	
	/**
	 * The currency.
	 */
	private Currency currency;
	
	/**
	 * The number of instruments traded.
	 */
	private long volume;
	
	/**
	 * Indicator data.
	 */
	private Indicator indicator;
	
	
	/**
	 * Default constructor.
	 */
	public Quotation() {
		
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
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
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
	 * @return the volume
	 */
	public long getVolume() {
		return volume;
	}


	/**
	 * @param volume the volume to set
	 */
	public void setVolume(long volume) {
		this.volume = volume;
	}


	/**
	 * @return the indicator
	 */
	public Indicator getIndicator() {
		return indicator;
	}


	/**
	 * @param indicator the indicator to set
	 */
	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
		
		if(this.indicator != null)
			this.indicator.setId(this.id);
	}
}
