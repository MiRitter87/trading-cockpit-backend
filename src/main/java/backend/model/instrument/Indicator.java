package backend.model.instrument;

/**
 * Indicator data that are calculated based on quotations.
 * 
 * @author Michael
 */
public class Indicator {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The stage of an instrument.
	 */
	private int stage;
	
	/**
	 * The distance in percent to the 52 week high of an instruments trading history.
	 */
	private float distanceTo52WeekHigh;
	
	/**
	 * The Bollinger Band Width (20,2)
	 */
	private float bollingerBandWidth;
	
	/**
	 * The simple moving average price of the last 50 trading days.
	 */
	private float sma50;
	
	/**
	 * The simple moving average price of the last 150 trading days.
	 */
	private float sma150;
	
	/**
	 * The simple moving average price of the last 200 trading days.
	 */
	private float sma200;
	
	/**
	 * The relative strength percentile of the instrument in relation to a set of other instruments.
	 */
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
