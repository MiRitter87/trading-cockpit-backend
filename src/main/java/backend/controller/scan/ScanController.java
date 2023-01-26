package backend.controller.scan;

import backend.controller.DataProvider;
import backend.controller.MainController;
import backend.model.scan.Scan;
import backend.model.scan.ScanCompletionStatus;
import backend.model.scan.ScanExecutionStatus;

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
	 * Property Key: Data Provider.
	 */
	protected static final String PROPERTY_DATA_PROVIDER = "dataProvider.scan";
	
	/**
	 * The interval in seconds between each query of historical quotations.
	 */
	private int queryInterval;
	
	/**
	 * The DataProvider for historical quotation data.
	 */
	private DataProvider dataProvider;
	
	
	/**
	 * Initialization.
	 * 
	 * @throws Exception In case the initialization failed.
	 */
	public ScanController() throws Exception {
		this.initializeQueryInterval();
		this.initializeDataProvider();
	}
	
	
	/**
	 * @return the queryInterval
	 */
	public int getQueryInterval() {
		return this.queryInterval;
	}
	
	
	/**
	 * @return the dataProvider
	 */
	public DataProvider getDataProvider() {
		return this.dataProvider;
	}
	
	
	/**
	 * Checks if the execution of a scan is requested.
	 * Starts execution if requested.
	 * 
	 * @param scan The scan being updated.
	 * @param databaseScan The database state of the scan before the update has been performed.
	 */
	public void checkAndExecute(final Scan scan, final Scan databaseScan) {
		if(scan.getExecutionStatus() != ScanExecutionStatus.IN_PROGRESS || databaseScan.getExecutionStatus() != ScanExecutionStatus.FINISHED)
			return;
		
		if(scan.getCompletionStatus() == ScanCompletionStatus.COMPLETE)
			this.execute(scan, false);
		else if(scan.getCompletionStatus() == ScanCompletionStatus.INCOMPLETE)
			this.execute(scan, true);
	}
	
	/**
	 * Executes the given scan.
	 * 
	 * @param scan The scan to be executed.
	 * @param scanOnlyIncompleteIntruments Indication to only scan incomplete instruments of the scan.
	 */
	private void execute(final Scan scan, final boolean scanOnlyIncompleteIntruments) {		
		Thread scanThread = new ScanThread(this.queryInterval, this.dataProvider, scan, scanOnlyIncompleteIntruments);
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
	
	
	/**
	 * Initializes the data provider.
	 * 
	 * @throws Exception In case the property could not be read or initialized.
	 */
	private void initializeDataProvider() throws Exception {
		String queryInterval = MainController.getInstance().getConfigurationProperty(PROPERTY_DATA_PROVIDER);
		
		switch(queryInterval) {
			case "YAHOO":
				this.dataProvider = DataProvider.YAHOO;
				break;
			case "MARKETWATCH":
				this.dataProvider = DataProvider.MARKETWATCH;
				break;
			default:
				this.dataProvider = null;
				break;
		}
	}
}
