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
	INSTRUMENTS_ABOVE_SMA50,
	
	/**
	 * A chart of an Instrument marked with Distribution Days.
	 */
	DISTRIBUTION_DAYS,
	
	/**
	 * A chart of an Instrument marked with Follow-Through Days.
	 */
	FOLLOW_THROUGH_DAYS,
	
	/**
	 * A chart of the Ritter Market Trend.
	 */
	RITTER_MARKET_TREND,
	
	/**
	 * A chart of the Ritter Pattern Indicator.
	 */
	RITTER_PATTERN_INDICATOR,
	
	/**
	 * A chart of an Instrument marked with Pocket Pivots.
	 */
	POCKET_PIVOTS
}
