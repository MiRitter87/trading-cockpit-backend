package backend.model.chart;

import java.math.BigDecimal;
import java.util.Objects;

import backend.model.instrument.Instrument;

/**
 * A horizontal line that is drawn on a chart.
 * 
 * @author Michael
 */
public class HorizontalLine {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The price at which the horizontal line is drawn.
	 */
	private BigDecimal price;
	
	/**
	 * The Instrument this line belongs to.
	 */
	private Instrument instrument;
	
	
	/**
	 * Default constructor.
	 */
	public HorizontalLine() {
		
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
	 * @return the instrument
	 */
	public Instrument getInstrument() {
		return instrument;
	}


	/**
	 * @param instrument the instrument to set
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, instrument, price);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		HorizontalLine other = (HorizontalLine) obj;
		
		if(price == null && other.price != null)
			return false;
		if(price != null && other.price == null)
			return false;
		if(price != null && other.price != null && price.compareTo(other.price) != 0)
			return false;
		
		return Objects.equals(id, other.id) && Objects.equals(instrument, other.instrument);
	}
}
