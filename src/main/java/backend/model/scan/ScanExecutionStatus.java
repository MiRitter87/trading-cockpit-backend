package backend.model.scan;

/**
 * The execution status of a Scan.
 * 
 * @author Michael
 */
public enum ScanExecutionStatus {
	/**
	 * The scan process is currently running.
	 */
	IN_PROGRESS,
	
	/**
	 * The scan is finished.
	 */
	FINISHED
}
