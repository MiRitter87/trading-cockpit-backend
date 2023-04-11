package backend.model.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import backend.model.instrument.InstrumentType;

/**
 * Statistical data of all instruments of an InstrumentType at a certain point in time.
 * 
 * @author Michael
 */
@Table(name="STATISTIC")
@Entity
@SequenceGenerator(name = "statisticSequence", initialValue = 1, allocationSize = 1)
public class Statistic {
	/**
	 * The ID.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statisticSequence")
	@Column(name="STATISTIC_ID")
	private Integer id;
	
	/**
	 * The date.
	 */
	@Column(name="DATE")
	private Date date;
	
	/**
	 * The InstrumentType.
	 */
	@Column(name="INSTRUMENT_TYPE", length = 10)
	@Enumerated(EnumType.STRING)
	private InstrumentType instrumentType;
	
	/**
	 * The number of instruments which advanced since the last data point.
	 */
	@Column(name="NUMBER_ADVANCE")
	private int numberAdvance;
	
	/**
	 * The number of instruments which declined since the last data point.
	 */
	@Column(name="NUMBER_DECLINE")
	private int numberDecline;
	
	/**
	 * The number of advancing minus declining instruments since the last data point.
	 */
	@Column(name="ADVANCE_DECLINE_NUMBER")
	private int advanceDeclineNumber;
	
	/**
	 * The number of instruments which are trading above the 50-day Simple Moving Average.
	 */
	@Column(name="NUMBER_ABOVE_SMA50")
	private int numberAboveSma50;
	
	/**
	 * The number of instruments which are trading at or below the 50-day Simple Moving Average.
	 */
	@Column(name="NUMBER_AT_OR_BELOW_SMA50")
	private int numberAtOrBelowSma50;
	
	/**
	 * The percentage of instruments which are trading above the 50-day Simple Moving Average.
	 */
	@Column(name="PERCENT_ABOVE_SMA50")
	private float percentAboveSma50;
	
	/**
	 * The number of the "Ritter Market Trend" indicator.
	 */
	@Column(name="NUMBER_RITTER_MARKET_TREND")
	private int numberRitterMarketTrend;
	
	
	/**
	 * Default constructor.
	 */
	public Statistic() {
		
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
	 * @return the instrumentType
	 */
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}


	/**
	 * @param instrumentType the instrumentType to set
	 */
	public void setInstrumentType(InstrumentType instrumentType) {
		this.instrumentType = instrumentType;
	}


	/**
	 * @return the numberAdvance
	 */
	public int getNumberAdvance() {
		return numberAdvance;
	}


	/**
	 * @param numberAdvance the numberAdvance to set
	 */
	public void setNumberAdvance(int numberAdvance) {
		this.numberAdvance = numberAdvance;
		
		this.advanceDeclineNumber = this.numberAdvance - this.numberDecline;
	}


	/**
	 * @return the numberDecline
	 */
	public int getNumberDecline() {
		return numberDecline;
	}


	/**
	 * @param numberDecline the numberDecline to set
	 */
	public void setNumberDecline(int numberDecline) {
		this.numberDecline = numberDecline;
		
		this.advanceDeclineNumber = this.numberAdvance - this.numberDecline;
	}


	/**
	 * @return the advanceDeclineNumber
	 */
	public int getAdvanceDeclineNumber() {
		return advanceDeclineNumber;
	}


	/**
	 * @return the numberAboveSma50
	 */
	public int getNumberAboveSma50() {
		return numberAboveSma50;
	}


	/**
	 * @param numberAboveSma50 the numberAboveSma50 to set
	 */
	public void setNumberAboveSma50(int numberAboveSma50) {
		this.numberAboveSma50 = numberAboveSma50;
		this.updatePercentAboveSma50();
	}


	/**
	 * @return the numberAtOrBelowSma50
	 */
	public int getNumberAtOrBelowSma50() {
		return numberAtOrBelowSma50;
	}


	/**
	 * @param numberAtOrBelowSma50 the numberAtOrBelowSma50 to set
	 */
	public void setNumberAtOrBelowSma50(int numberAtOrBelowSma50) {
		this.numberAtOrBelowSma50 = numberAtOrBelowSma50;
		this.updatePercentAboveSma50();		
	}


	/**
	 * @return the percentAboveSma50
	 */
	public float getPercentAboveSma50() {
		return percentAboveSma50;
	}
	
	
	/**
	 * @return the numberRitterMarketTrend
	 */
	public int getNumberRitterMarketTrend() {
		return numberRitterMarketTrend;
	}


	/**
	 * @param numberRitterMarketTrend the numberRitterMarketTrend to set
	 */
	public void setNumberRitterMarketTrend(int numberRitterMarketTrend) {
		this.numberRitterMarketTrend = numberRitterMarketTrend;
	}


	/**
	 * Updates the percentage above SMA(50).
	 */
	private void updatePercentAboveSma50() {
		BigDecimal percentAboveSma50, totalNumber, numberAboveSma50;
		
		if((this.numberAboveSma50 + this.numberAtOrBelowSma50) != 0) {
			numberAboveSma50 = new BigDecimal(this.numberAboveSma50);
			totalNumber = new BigDecimal(this.numberAboveSma50 + this.numberAtOrBelowSma50);
			
			percentAboveSma50 = numberAboveSma50.multiply(new BigDecimal(100)).divide(totalNumber, 0, RoundingMode.HALF_UP);
			this.percentAboveSma50 = percentAboveSma50.floatValue();
		}
	}


	@Override
	public int hashCode() {
		return Objects.hash(advanceDeclineNumber, date, id, instrumentType, numberAboveSma50, numberAdvance,
				numberAtOrBelowSma50, numberDecline, percentAboveSma50);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statistic other = (Statistic) obj;
		if (date == null && other.date != null)
			return false;
		if (date != null && other.date == null)
			return false;
		if(date != null && other.date != null) {
			if (date.getTime() != other.date.getTime())
				return false;
		}
		
		return advanceDeclineNumber == other.advanceDeclineNumber 
				&& Objects.equals(id, other.id) && instrumentType == other.instrumentType
				&& numberAboveSma50 == other.numberAboveSma50 && numberAdvance == other.numberAdvance
				&& numberAtOrBelowSma50 == other.numberAtOrBelowSma50 && numberDecline == other.numberDecline
				&& Float.floatToIntBits(percentAboveSma50) == Float.floatToIntBits(other.percentAboveSma50);
	}
}
