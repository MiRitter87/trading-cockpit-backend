package backend.model.chart;

import java.math.BigDecimal;

/**
 * A lean version of a HorizontalLine that is used by the WebService to transfer object data.
 * The main difference to the regular HorizontalLine is that IDs are used instead of object references.
 * 
 * @author Michael
 */
public class HorizontalLineWS {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The ID of the Instrument this line belongs to.
	 */
	private Integer instrumentId;
	
	/**
	 * The price at which the horizontal line is drawn.
	 */
	private BigDecimal price;

	
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
}
