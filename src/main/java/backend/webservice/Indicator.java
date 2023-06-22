package backend.webservice;

/**
 * The Indicator that can optionally be display in a Price Volume Chart.
 * 
 * @author Michael
 */
public enum Indicator {
	/**
	 * No Indicator.
	 */
	NONE,
	
	/**
	 * The relative strength line showing the price ratio between two instruments.
	 */
	RS_LINE,
	
	/**
	 * Bollinger BandWidth.
	 */
	BBW
}
