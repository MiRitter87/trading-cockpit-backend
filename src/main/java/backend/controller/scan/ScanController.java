package backend.controller.scan;

import java.util.HashMap;
import java.util.Map;

import backend.controller.DataProvider;
import backend.controller.MainController;
import backend.model.StockExchange;
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
	 * Property Key: Data Provider for stock exchange NYSE.
	 */
	protected static final String PROPERTY_DATA_PROVIDER_NYSE = "dataProvider.scan.nyse";
	
	/**
	 * Property Key: Data Provider for stock exchange Nasdaq.
	 */
	protected static final String PROPERTY_DATA_PROVIDER_NASDAQ = "dataProvider.scan.ndq";
	
	/**
	 * Property Key: Data Provider for stock exchange TSX.
	 */
	protected static final String PROPERTY_DATA_PROVIDER_TSX = "dataProvider.scan.tsx";
	
	/**
	 * Property Key: Data Provider for stock exchange TSX/V.
	 */
	protected static final String PROPERTY_DATA_PROVIDER_TSXV = "dataProvider.scan.tsxv";
	
	/**
	 * Property Key: Data Provider for stock exchange CSE.
	 */
	protected static final String PROPERTY_DATA_PROVIDER_CSE = "dataProvider.scan.cse";
	
	/**
	 * Property Key: Data Provider for stock exchange LSE.
	 */
	protected static final String PROPERTY_DATA_PROVIDER_LSE = "dataProvider.scan.lse";
	
	/**
	 * The interval in seconds between each query of historical quotations.
	 */
	private int queryInterval;
	
	/**
	 * A Map of stock exchanges and their corresponding data providers.
	 */
	private Map<StockExchange, DataProvider> dataProviders;
	
	
	/**
	 * Initialization.
	 * 
	 * @throws Exception In case the initialization failed.
	 */
	public ScanController() throws Exception {
		this.initializeQueryInterval();
		this.initializeDataProviders();
	}
	
	
	/**
	 * @return the queryInterval
	 */
	public int getQueryInterval() {
		return this.queryInterval;
	}
	
	
	/**
	 * @return the dataProviders
	 */
	public Map<StockExchange, DataProvider> getDataProviders() {
		return this.dataProviders;
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
		//TODO Adapt ScanThread to Map of data providers
		//Thread scanThread = new ScanThread(this.queryInterval, this.dataProvider, scan, scanOnlyIncompleteIntruments);
		//scanThread.start();
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
	 * Initializes the relations between stock exchanges and their corresponding data providers.
	 * 
	 * @throws Exception In case a property could not be read or initialized.
	 */
	private void initializeDataProviders() throws Exception {
		this.dataProviders = new HashMap<>();
		
		this.dataProviders.put(StockExchange.NYSE, MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_NYSE));
		this.dataProviders.put(StockExchange.NDQ, MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_NASDAQ));
		this.dataProviders.put(StockExchange.TSX, MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_TSX));
		this.dataProviders.put(StockExchange.TSXV, MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_TSXV));
		this.dataProviders.put(StockExchange.CSE, MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_CSE));
		this.dataProviders.put(StockExchange.LSE, MainController.getInstance().getDataProviderForProperty(PROPERTY_DATA_PROVIDER_LSE));
	}
}
