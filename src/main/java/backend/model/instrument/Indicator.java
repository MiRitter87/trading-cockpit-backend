package backend.model.instrument;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Indicator data that are calculated based on quotations.
 * 
 * @author Michael
 */
@Table(name="INDICATOR")
@Entity
public class Indicator {
	/**
	 * The ID.
	 */
	@Id
	@Column(name="INDICATOR_ID")
	private Integer id;
	
	/**
	 * The stage of an instrument.
	 */
	@Column(name="STAGE")
	private int stage;
	
	/**
	 * The distance in percent to the 52 week high of an instruments trading history.
	 */
	@Column(name="DISTANCE_TO_52_WEEK_HIGH")
	private float distanceTo52WeekHigh;
	
	/**
	 * The distance in percent to the 52 week low of an instruments trading history.
	 */
	@Column(name="DISTANCE_TO_52_WEEK_LOW")
	private float distanceTo52WeekLow;
	
	/**
	 * The Bollinger Band Width (10,2)
	 */
	@Column(name="BOLLINGER_BAND_WIDTH")
	private float bollingerBandWidth;
	
	/**
	 * The simple moving average price of the last 50 trading days.
	 */
	@Column(name="SMA50")
	private float sma50;
	
	/**
	 * The simple moving average price of the last 150 trading days.
	 */
	@Column(name="SMA150")
	private float sma150;
	
	/**
	 * The simple moving average price of the last 200 trading days.
	 */
	@Column(name="SMA200")
	private float sma200;
	
	/**
	 * The difference in percent between the average volume of the last 10 days compared to the SMA(30) of the volume.
	 */
	@Column(name="VOLUME_DIFFERENTIAL_10_DAYS")
	private float volumeDifferential10Days;
	
	/**
	 * The sum of an instruments performance in different time frames used for rsNumber calculation.
	 */
	@Column(name="RS_PERCENT_SUM")
	private float rsPercentSum;	
	
	/**
	 * The relative strength percentile of the instrument in relation to a set of other instruments.
	 */
	@Column(name="RS_NUMBER")
	private int rsNumber;
	
	/**
	 * The length of the most recent consolidation in weeks. Beginning at the most recent 52-week high.
	 */
	@Column(name="BASE_LENGTH_WEEKS")
	private int baseLengthWeeks;
	
	
	/**
	 * Default constructor.
	 */
	public Indicator() {
		
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
	 * @return the stage
	 */
	public int getStage() {
		return stage;
	}


	/**
	 * @param stage the stage to set
	 */
	public void setStage(int stage) {
		this.stage = stage;
	}


	/**
	 * @return the distanceTo52WeekHigh
	 */
	public float getDistanceTo52WeekHigh() {
		return distanceTo52WeekHigh;
	}


	/**
	 * @param distanceTo52WeekHigh the distanceTo52WeekHigh to set
	 */
	public void setDistanceTo52WeekHigh(float distanceTo52WeekHigh) {
		this.distanceTo52WeekHigh = distanceTo52WeekHigh;
	}


	/**
	 * @return the distanceTo52WeekLow
	 */
	public float getDistanceTo52WeekLow() {
		return distanceTo52WeekLow;
	}


	/**
	 * @param distanceTo52WeekLow the distanceTo52WeekLow to set
	 */
	public void setDistanceTo52WeekLow(float distanceTo52WeekLow) {
		this.distanceTo52WeekLow = distanceTo52WeekLow;
	}


	/**
	 * @return the bollingerBandWidth
	 */
	public float getBollingerBandWidth() {
		return bollingerBandWidth;
	}


	/**
	 * @param bollingerBandWidth the bollingerBandWidth to set
	 */
	public void setBollingerBandWidth(float bollingerBandWidth) {
		this.bollingerBandWidth = bollingerBandWidth;
	}


	/**
	 * @return the sma50
	 */
	public float getSma50() {
		return sma50;
	}


	/**
	 * @param sma50 the sma50 to set
	 */
	public void setSma50(float sma50) {
		this.sma50 = sma50;
	}


	/**
	 * @return the sma150
	 */
	public float getSma150() {
		return sma150;
	}


	/**
	 * @param sma150 the sma150 to set
	 */
	public void setSma150(float sma150) {
		this.sma150 = sma150;
	}


	/**
	 * @return the sma200
	 */
	public float getSma200() {
		return sma200;
	}


	/**
	 * @param sma200 the sma200 to set
	 */
	public void setSma200(float sma200) {
		this.sma200 = sma200;
	}


	/**
	 * @return the volumeDifferential10days
	 */
	public float getVolumeDifferential10Days() {
		return volumeDifferential10Days;
	}


	/**
	 * @param volumeDifferential10days the volumeDifferential10days to set
	 */
	public void setVolumeDifferential10Days(float volumeDifferential10Days) {
		this.volumeDifferential10Days = volumeDifferential10Days;
	}


	/**
	 * @return the rsPercentSum
	 */
	public float getRsPercentSum() {
		return rsPercentSum;
	}


	/**
	 * @param rsPercentSum the rsPercentSum to set
	 */
	public void setRsPercentSum(float rsPercentSum) {
		this.rsPercentSum = rsPercentSum;
	}


	/**
	 * @return the rsNumber
	 */
	public int getRsNumber() {
		return rsNumber;
	}


	/**
	 * @param rsNumber the rsNumber to set
	 */
	public void setRsNumber(int rsNumber) {
		this.rsNumber = rsNumber;
	}


	/**
	 * @return the baseLengthWeeks
	 */
	public int getBaseLengthWeeks() {
		return baseLengthWeeks;
	}


	/**
	 * @param baseLengthWeeks the baseLengthWeeks to set
	 */
	public void setBaseLengthWeeks(int baseLengthWeeks) {
		this.baseLengthWeeks = baseLengthWeeks;
	}


	@Override
	public int hashCode() {
		return Objects.hash(bollingerBandWidth, distanceTo52WeekHigh, distanceTo52WeekLow, rsNumber, rsPercentSum, 
				sma150, sma200, sma50, stage, volumeDifferential10Days, baseLengthWeeks);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Indicator other = (Indicator) obj;
		return Float.floatToIntBits(bollingerBandWidth) == Float.floatToIntBits(other.bollingerBandWidth)
				&& Float.floatToIntBits(distanceTo52WeekHigh) == Float.floatToIntBits(other.distanceTo52WeekHigh)
				&& Float.floatToIntBits(distanceTo52WeekLow) == Float.floatToIntBits(other.distanceTo52WeekLow)
				&& Objects.equals(id, other.id) && rsNumber == other.rsNumber
				&& Float.floatToIntBits(rsPercentSum) == Float.floatToIntBits(other.rsPercentSum)
				&& Float.floatToIntBits(sma150) == Float.floatToIntBits(other.sma150)
				&& Float.floatToIntBits(sma200) == Float.floatToIntBits(other.sma200)
				&& Float.floatToIntBits(sma50) == Float.floatToIntBits(other.sma50) 
				&& Float.floatToIntBits(volumeDifferential10Days) == Float.floatToIntBits(other.volumeDifferential10Days)
				&& stage == other.stage && baseLengthWeeks == other.baseLengthWeeks;
	}
}
