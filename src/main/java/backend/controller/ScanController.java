package backend.controller;

import backend.model.scan.Scan;
import backend.model.scan.ScanStatus;

/**
 * Controls the whole scan process.
 * 
 * @author Michael
 */
public class ScanController {
	/**
	 * Property Key: Query interval.
	 */
	protected static final String PROPERTY_QUERY_INTERVAL = "queryInterval.scan";
	
	/**
	 * The interval in seconds between each query of historical quotations.
	 */
	private int queryInterval;
	
	
	/**
	 * Initialization.
	 * 
	 * @throws Exception In case the initialization failed.
	 */
	public ScanController() throws Exception {
		this.initializeQueryInterval();
	}
	
	
	/**
	 * @return the queryInterval
	 */
	public int getQueryInterval() {
		return queryInterval;
	}
	
	
	/**
	 * Checks if the execution of a scan is requested.
	 * Starts execution if requested.
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
		Thread scanThread = new ScanThread(this.queryInterval, scan);
		scanThread.start();
	}
	
	
	/**
	 * Initializes the query interval.
	 * 
	 * @Throws Exception In case the property could not be read or initialized.
	 */
	private void initializeQueryInterval() throws Exception {
		String queryInterval = MainController.getInstance().getConfigurationProperty(PROPERTY_QUERY_INTERVAL);
		this.queryInterval = Integer.valueOf(queryInterval);
	}
}
