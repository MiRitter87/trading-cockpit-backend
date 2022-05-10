package backend.model.priceAlert;

/**
 * The confirmation status of a price alert.
 * 
 * @author Michael
 */
public enum ConfirmationStatus {
	/**
	 * Either confirmed or not-confirmed.
	 */
	ALL,
	
	/**
	 * Confirmed only.
	 */
	CONFIRMED,
	
	/**
	 * Not-confirmed only.
	 */
	NOT_CONFIRMED
}
