package backend.controller;

import java.time.Instant;

/**
 * Controls the process, that cyclically queries stock quotes and updates the alerts accordingly.
 * 
 * @author Michael
 */
public class StockAlertController {
	/**
	 * The interval in seconds between each stock quote query.
	 */
	private int queryInterval;
	
	/**
	 * The start time of the trading session.
	 */
	private Instant startTime;
	
	/**
	 * The end time of the trading session.
	 */
	private Instant endTime;
	
	
	/**
	 * Initialization.
	 */
	public StockAlertController() {
		this.initializeQueryInterval();
		this.initializeStartTime();
		this.initializeEndTime();
	}
	
	
	/**
	 * Starts the query and update process.
	 */
	public void start() {
		//https://www.baeldung.com/java-start-thread
	}
	
	
	/**
	 * Stops the query and update process.
	 */
	public void stop() {
		
	}
	
	
	/**
	 * Initializes the query interval.
	 */
	private void initializeQueryInterval() {
		//TODO read from property file
	}
	
	
	/**
	 * Initializes the start time.
	 */
	private void initializeStartTime() {
		//TODO read from property file
		//https://docs.oracle.com/javase/tutorial/datetime/iso/period.html
	}
	
	
	/**
	 * Initializes the end time.
	 */
	private void initializeEndTime() {
		//TODO read from property file
		//https://docs.oracle.com/javase/tutorial/datetime/iso/period.html
	}
}
