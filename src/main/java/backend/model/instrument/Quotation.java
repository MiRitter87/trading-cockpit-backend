package backend.model.instrument;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import backend.model.Currency;

/**
 * Quotation data of an instrument.
 * 
 * @author Michael
 */
@Table(name="QUOTATION")
@Entity
@SequenceGenerator(name = "quotationSequence", initialValue = 1, allocationSize = 1)
public class Quotation {
	/**
	 * The ID.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quotationSequence")
	@Column(name="QUOTATION_ID")
	private Integer id;
	
	/**
	 * The date.
	 */
	@Column(name="DATE")
	private Date date;
	
	/**
	 * The opening price.
	 */
	@Column(name="OPEN")
	private BigDecimal open;
	
	/**
	 * The high price.
	 */
	@Column(name="HIGH")
	private BigDecimal high;
	
	/**
	 * The low price.
	 */
	@Column(name="LOW")
	private BigDecimal low;
	
	/**
	 * The closing price.
	 */
	@Column(name="CLOSE")
	private BigDecimal close;
	
	/**
	 * The currency.
	 */
	@Column(name="CURRENCY", length = 3)
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	/**
	 * The number of instruments traded.
	 */
	@Column(name="VOLUME")
	private long volume;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="INSTRUMENT_ID")
	private Instrument instrument;
	
	/**
	 * Indicator data.
	 */
	@OneToOne(targetEntity = Indicator.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name="INDICATOR_ID")
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
	 * @return the open
	 */
	public BigDecimal getOpen() {
		return open;
	}


	/**
	 * @param open the open to set
	 */
	public void setOpen(BigDecimal open) {
		this.open = open;
	}


	/**
	 * @return the high
	 */
	public BigDecimal getHigh() {
		return high;
	}


	/**
	 * @param high the high to set
	 */
	public void setHigh(BigDecimal high) {
		this.high = high;
	}


	/**
	 * @return the low
	 */
	public BigDecimal getLow() {
		return low;
	}


	/**
	 * @param low the low to set
	 */
	public void setLow(BigDecimal low) {
		this.low = low;
	}


	/**
	 * @return the closing price
	 */
	public BigDecimal getClose() {
		return close;
	}


	/**
	 * @param close the closing price to set
	 */
	public void setClose(BigDecimal close) {
		this.close = close;
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


	@Override
	public int hashCode() {
		return Objects.hash(currency, date, id, indicator, instrument, open, high, low, close, volume);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quotation other = (Quotation) obj;
		
		if(open == null && other.open != null)
			return false;
		if(open != null && other.open == null)
			return false;
		if(open != null && other.open != null && open.compareTo(other.open) != 0)
			return false;
		
		if(high == null && other.high != null)
			return false;
		if(high != null && other.high == null)
			return false;
		if(high != null && other.high != null && high.compareTo(other.high) != 0)
			return false;
		
		if(low == null && other.low != null)
			return false;
		if(low != null && other.low == null)
			return false;
		if(low != null && other.low != null && low.compareTo(other.low) != 0)
			return false;
		
		if(close == null && other.close != null)
			return false;
		if(close != null && other.close == null)
			return false;
		if(close != null && other.close != null && close.compareTo(other.close) != 0)
			return false;
		
		return currency == other.currency && Objects.equals(date, other.date) && Objects.equals(id, other.id)
				&& Objects.equals(indicator, other.indicator) && Objects.equals(instrument, other.instrument)
				&& volume == other.volume;
	}
}
