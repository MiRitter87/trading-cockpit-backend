package backend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderYahooDAO;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;
import okhttp3.OkHttpClient;

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
	private QuotationProviderDAO quotationProviderDAO;
	
	/**
	 * DAO to access price alerts.
	 */
	private PriceAlertDAO priceAlertDAO;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(StockAlertThread.class);
	
	
	/**
	 * Initializes the stock alert thread.
	 * 
	 * @param startTime The start time of the process.
	 * @param endTime The end time of the process.
	 */
	public StockAlertThread(final LocalTime startTime, final LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		
		this.quotationProviderDAO = new QuotationProviderYahooDAO(new OkHttpClient());
		this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
	}
	
	
	/**
	 * The main method of the thread that is executed.
	 */
	public void run() {
		PriceAlert priceAlert = null;
		Quotation quotation;
		
		if(this.isWeekend() || !this.isTimeIntervalActive())
			return;
		
		try {
			priceAlert = this.getOldestPriceAlert();
		} catch (Exception e) {
			logger.error("Failed to determine price alerts for next checkup.", e);
		}
		
		if(priceAlert == null)
			return;
			
		//Get the Quotation of the stock defined in the price alert.
		try {
			quotation = this.quotationProviderDAO.getCurrentQuotation(
					priceAlert.getInstrument().getSymbol(), priceAlert.getInstrument().getStockExchange());
		} catch (Exception e) {
			logger.error("Failed to determine quotation for symbol: " +priceAlert.getInstrument().getSymbol(), e);
			return;
		}
			
		try {
			this.checkAndUpdatePriceAlert(priceAlert, quotation);
		} catch (Exception e) {
			logger.error("Failed to update price alert with ID: " +priceAlert.getId(), e);
		}
	}
	
	
	/**
	 * Checks if the current time is between the start time and the end time defined in the configuration file.
	 * 
	 * @return true, if current time is in defined interval; false, otherwise.
	 */
	private boolean isTimeIntervalActive() {
		LocalTime currentTime = LocalTime.now();
		
		if(currentTime.isAfter(this.startTime) && currentTime.isBefore(this.endTime))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Checks if the current day is at a weekend.
	 * 
	 * @return true, if current day is at weekend; false otherwise.
	 */
	private boolean isWeekend() {
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return true;
		else
			return false;
	}
	
	
	/**
	 * Gets the price alert with the oldest lastStockQuoteTime that has not been triggered yet.
	 * 
	 * @return The price alert.
	 * @throws Exception In case price alert determination failed.
	 */
	private PriceAlert getOldestPriceAlert() throws Exception {
		ArrayList<PriceAlert> priceAlerts = new ArrayList<PriceAlert>();
		
		priceAlerts.addAll(this.priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.LAST_STOCK_QUOTE_TIME, 
				TriggerStatus.NOT_TRIGGERED, ConfirmationStatus.NOT_CONFIRMED));
		
		if(priceAlerts.size() > 0)
			return priceAlerts.get(0);
		else
			return null;
	}
	
	
	/**
	 * Checks the price defined in the alert against the stock quote.
	 * Updates the price alert afterwards.
	 * 
	 * @param priceAlert The price alert.
	 * @param quotation The Quotation.
	 * @throws Exception In case the update failed.
	 */
	private void checkAndUpdatePriceAlert(PriceAlert priceAlert, final Quotation quotation) throws Exception {
		//If the trigger price has been reached, set the trigger time of the price alert.
		if(priceAlert.getAlertType() == PriceAlertType.GREATER_OR_EQUAL && quotation.getPrice().compareTo(priceAlert.getPrice()) >= 0)
			priceAlert.setTriggerTime(new Date());
		else if(priceAlert.getAlertType() == PriceAlertType.LESS_OR_EQUAL && quotation.getPrice().compareTo(priceAlert.getPrice()) <= 0)
			priceAlert.setTriggerTime(new Date());
		
		priceAlert.setLastStockQuoteTime(new Date());
		priceAlert.setTriggerDistancePercent(this.getTriggerDistancePercent(priceAlert, quotation));
		
		this.priceAlertDAO.updatePriceAlert(priceAlert);
	}
	
	
	/**
	 * Gets the distance between the current instrument price and the trigger price in percent.
	 * 
	 * @param priceAlert The PriceAlert defining the trigger price.
	 * @param quotation The Quotation containing the current price.
	 * @return The distance between current price and trigger price in percent.
	 */
	private float getTriggerDistancePercent(final PriceAlert priceAlert, final Quotation quotation) {
		BigDecimal percentDistance = new BigDecimal(0);
		
		percentDistance = quotation.getPrice().divide(priceAlert.getPrice(), 4, RoundingMode.HALF_UP);
		percentDistance = percentDistance.subtract(BigDecimal.valueOf(1));
		percentDistance = percentDistance.multiply(BigDecimal.valueOf(100));
		
		return percentDistance.floatValue();
	}
}
