package backend.model.statistic;

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
	 * The sum of advancing and declining instruments since the last data point.
	 */
	@Column(name="ADVANCE_DECLINE_SUM")
	private int advanceDeclineSum;
	
	
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
	}


	/**
	 * @return the advanceDeclineSum
	 */
	public int getAdvanceDeclineSum() {
		return advanceDeclineSum;
	}


	/**
	 * @param advanceDeclineSum the advanceDeclineSum to set
	 */
	public void setAdvanceDeclineSum(int advanceDeclineSum) {
		this.advanceDeclineSum = advanceDeclineSum;
	}


	@Override
	public int hashCode() {
		return Objects.hash(advanceDeclineSum, date, id, instrumentType, numberAdvance, numberDecline);
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
		return advanceDeclineSum == other.advanceDeclineSum && Objects.equals(date, other.date)
				&& Objects.equals(id, other.id) && instrumentType == other.instrumentType
				&& numberAdvance == other.numberAdvance && numberDecline == other.numberDecline;
	}
}
