package backend.model.scan;

/**
 * The status of a scan.
 * 
 * @author Michael
 */
public enum ScanStatus {
	/**
	 * The scan process is currently running.
	 */
	IN_PROGRESS,
	
	/**
	 * The scan is finished.
	 */
	FINISHED
}
