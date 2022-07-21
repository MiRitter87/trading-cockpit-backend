package backend.model.instrument;

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
	 * The Bollinger Band Width (20,2)
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
	 * The relative strength percentile of the instrument in relation to a set of other instruments.
	 */
	@Column(name="RS_NUMBER")
	private int rsNumber;
	
	
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
}
