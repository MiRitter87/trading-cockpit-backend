package backend.webservice;

/**
 * A template that defines which parameters are applied to the Scan results.
 * 
 * @author Michael
 */
public enum ScanTemplate {
	/**
	 * Minervini Trend Template.
	 */
	MINERVINI_TREND_TEMPLATE,
	
	/**
	 * A contraction in price and volume within the last 10 trading days.
	 */
	VOLATILITY_CONTRACTION_10_DAYS,
	
	/**
	 * Instruments consolidating in Buyable Bases near the 52 week high.
	 */
	BREAKOUT_CANDIDATES
}