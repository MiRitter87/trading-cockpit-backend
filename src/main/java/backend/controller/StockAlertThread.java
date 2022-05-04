package backend.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import backend.dao.DAOManager;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.dao.stockQuote.StockQuoteDAO;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.stockQuote.StockQuote;

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
	 * DAO to access stock quotes.
	 */
	private StockQuoteDAO stockQuoteDAO;
	
	/**
	 * DAO to access price alerts.
	 */
	private PriceAlertDAO priceAlertDAO;
	
	
	/**
	 * Initializes the stock alert thread.
	 * 
	 * @param startTime The start time of the process.
	 * @param endTime The end time of the process.
	 */
	public StockAlertThread(final LocalTime startTime, final LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		
		this.stockQuoteDAO = DAOManager.getInstance().getStockQuoteDAO();
		this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
	}
	
	
	/**
	 * The main method of the thread that is executed.
	 */
	public void run() {
		ArrayList<PriceAlert> priceAlerts = new ArrayList<PriceAlert>();
		PriceAlert priceAlert;
		StockQuote stockQuote;
		
		if(!this.isTimeIntervalActive())
			return;
		
		try {
			//Get the price alert with the oldest lastStockQuoteTime.
			priceAlerts.addAll(this.priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.LAST_STOCK_QUOTE_TIME, true));
			
			if(priceAlerts.size() > 0)
				priceAlert = priceAlerts.get(0);
			else
				return;
			
			//Get the quote of the stock defined in the price alert.
			stockQuote = this.stockQuoteDAO.getStockQuote(priceAlert.getSymbol(), priceAlert.getStockExchange());
			
			//If the trigger price has been reached, set the trigger time of the price alert.
			if(priceAlert.getAlertType() == PriceAlertType.GREATER_OR_EQUAL && stockQuote.getPrice().compareTo(priceAlert.getPrice()) >= 0)
				priceAlert.setTriggerTime(new Date());
			else if(priceAlert.getAlertType() == PriceAlertType.LESS_OR_EQUAL && stockQuote.getPrice().compareTo(priceAlert.getPrice()) <= 0)
				priceAlert.setTriggerTime(new Date());
			
			if(priceAlert.getTriggerTime() != null)
				this.priceAlertDAO.updatePriceAlert(priceAlert);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
