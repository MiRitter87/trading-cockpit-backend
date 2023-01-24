package backend.model.scan;

/**
 * The execution status of a Scan.
 * This is the runtime status of a Scan.
 * 
 * @author Michael
 */
public enum ScanExecutionStatus {
	/**
	 * The scan process is currently running.
	 */
	IN_PROGRESS,
	
	/**
	 * The scan process has finished.
	 */
	FINISHED
}
