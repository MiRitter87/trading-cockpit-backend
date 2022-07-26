package backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.model.scan.Scan;
import backend.model.scan.ScanStatus;

/**
 * Controls the whole scan process.
 * 
 * @author Michael
 */
public class ScanController {
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(ScanController.class);
	
	
	/**
	 * Checks if the execution of a scan is requested.
	 * Starts execution if necessary.
	 * 
	 * @param scan The scan being updated.
	 * @param databaseScan The database state of the scan before the update has been performed.
	 */
	public void checkAndExecute(final Scan scan, final Scan databaseScan) {
		if(scan.getStatus() == ScanStatus.IN_PROGRESS && databaseScan.getStatus() == ScanStatus.FINISHED)
			this.execute(scan);			
	}
	
	/**
	 * Executes the given scan.
	 * 
	 * @param scan The scan to be executed.
	 */
	public void execute(final Scan scan) {		
		Thread scanThread = new ScanThread(5, scan);
		scanThread.run();
	}
}
