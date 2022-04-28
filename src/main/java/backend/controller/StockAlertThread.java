package backend.controller;

import java.util.Date;

/**
 * Queries stock quote data and updates stock alerts if the trigger price has been reached.
 * 
 * @author Michael
 */
public class StockAlertThread extends Thread {
	public void run() {
		Date currentDate = new Date();
		
		System.out.println("Current date: " +currentDate.toString());
	}
}
