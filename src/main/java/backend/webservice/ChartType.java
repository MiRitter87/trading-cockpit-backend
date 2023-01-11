package backend.webservice;

/**
 * The type of a chart.
 * 
 * @author Michael
 */
public enum ChartType {
	/**
	 * A cumulative chart of the advance/decline number.
	 */
	ADVANCE_DECLINE_NUMBER,
	
	/**
	 * A chart of the percentage of instruments trading above their SMA(50).
	 */
	INSTRUMENTS_ABOVE_SMA50
}
