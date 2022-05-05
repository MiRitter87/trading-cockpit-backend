package backend.model.priceAlert;

/**
 * The status of the triggerTime attribute of a price alert.
 * 
 * @author Michael
 */
public enum TriggerStatus {
	/**
	 * Either triggered or not-triggered.
	 */
	ALL,
	
	/**
	 * Triggered only.
	 */
	TRIGGERED,
	
	/**
	 * Not-triggered only.
	 */
	NOT_TRIGGERED
}
