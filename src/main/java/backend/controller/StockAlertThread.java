package backend.controller;

import java.time.LocalTime;
import java.util.Date;

/**
 * Queries stock quote data and updates stock alerts if the trigger price has been reached.
 * 
 * @author Michael
 */
public class StockAlertThread extends Thread {
	/**
	 * The start time of the trading session.
	 */
	private LocalTime startTime;
	
	/**
	 * The end time of the trading session.
	 */
	private LocalTime endTime;
	
	
	/**
	 * Initializes the stock alert thread.
	 * 
	 * @param startTime The start time of the process.
	 * @param endTime The end time of the process.
	 */
	public StockAlertThread(final LocalTime startTime, final LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
	/**
	 * The main method of the thread that is executed.
	 */
	public void run() {
		Date currentDate = new Date();
		
		if(this.isTimeIntervalActive())
			System.out.println("Current date: " +currentDate.toString());
	}
	
	
	/**
	 * Checks if the current time is between the start time and the end time defined in the configuration file.
	 * 
	 * @return true, if current time is in defined interval; false, otherwise.
	 */
	private boolean isTimeIntervalActive() {
		LocalTime currentTime = LocalTime.now();
		
		if(currentTime.getHour() >= startTime.getHour() && currentTime.getMinute() >= startTime.getMinute() &&
				currentTime.getHour() <= endTime.getHour() && currentTime.getMinute() <= endTime.getMinute()) {
			return true;
		}
		else {			
			return false;
		}
	}
}
