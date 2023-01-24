package backend.model.scan;

/**
 * The completion status of a Scan.
 * This status indicates if there occurred any problems during data retrieval.
 * 
 * @author Michael
 */
public enum ScanCompletionStatus {
	/**
	 * All items of the Scan have been executed completely.
	 */
	COMPLETE,
	
	/**
	 * Some items of the Scan have not been executed completely.
	 */
	INCOMPLETE
}
