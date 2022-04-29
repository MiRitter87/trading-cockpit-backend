package backend.controller;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controls the process, that cyclically queries stock quotes and updates the alerts accordingly.
 * 
 * @author Michael
 */
public class StockAlertController {
	/**
	 * Property Key: Query interval.
	 */
	protected static final String PROPERTY_QUERY_INTERVAL = "queryInterval";
	
	/**
	 * Property Key: Start Time - Hour.
	 */
	protected static final String PROPERTY_START_TIME_HOUR = "startTime.hour";
	
	/**
	 * Property Key: Start Time - Minute.
	 */
	protected static final String PROPERTY_START_TIME_MINUTE = "startTime.minute";
	
	/**
	 * Property Key: Start Time - Hour.
	 */
	protected static final String PROPERTY_END_TIME_HOUR = "endTime.hour";
	
	/**
	 * Property Key: Start Time - Minute.
	 */
	protected static final String PROPERTY_END_TIME_MINUTE = "endTime.minute";
	
	/**
	 * The interval in seconds between each stock quote query.
	 */
	private int queryInterval;
	
	/**
	 * The start time of the trading session.
	 */
	private LocalTime startTime;
	
	/**
	 * The end time of the trading session.
	 */
	private LocalTime endTime;
	
	/**
	 * Executes threads cyclically.
	 */
	private ScheduledExecutorService executorService;
	
	
	/**
	 * Initialization.
	 * 
	 * @throws Exception In case the initialization failed.
	 */
	public StockAlertController() throws Exception {
		this.initializeQueryInterval();
		this.initializeStartTime();
		this.initializeEndTime();
	}
	
	
	/**
	 * @return the queryInterval
	 */
	public int getQueryInterval() {
		return queryInterval;
	}


	/**
	 * @return the startTime
	 */
	public LocalTime getStartTime() {
		return startTime;
	}


	/**
	 * @return the endTime
	 */
	public LocalTime getEndTime() {
		return endTime;
	}


	/**
	 * Starts the query and update process.
	 */
	public void start() {
		this.executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(new StockAlertThread(this.startTime, this.endTime), 0, this.getQueryInterval(), TimeUnit.SECONDS);
	}
	
	
	/**
	 * Stops the query and update process.
	 */
	public void stop() {
		executorService.shutdown();
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
	 * Initializes the start time.
	 * 
	 * @Throws Exception In case the property could not be read or initialized.
	 */
	private void initializeStartTime() throws Exception {
		String startTimeHour, startTimeMinute;
		
		startTimeHour = MainController.getInstance().getConfigurationProperty(PROPERTY_START_TIME_HOUR);
		startTimeMinute = MainController.getInstance().getConfigurationProperty(PROPERTY_START_TIME_MINUTE);
				
		this.startTime = LocalTime.of(Integer.valueOf(startTimeHour), Integer.valueOf(startTimeMinute));
	}
	
	
	/**
	 * Initializes the end time.
	 * 
	 * @Throws Exception In case the property could not be read or initialized.
	 */
	private void initializeEndTime() throws Exception {
		String endTimeHour, endTimeMinute;
		
		endTimeHour = MainController.getInstance().getConfigurationProperty(PROPERTY_END_TIME_HOUR);
		endTimeMinute = MainController.getInstance().getConfigurationProperty(PROPERTY_END_TIME_MINUTE);
				
		this.endTime = LocalTime.of(Integer.valueOf(endTimeHour), Integer.valueOf(endTimeMinute));
	}
}
