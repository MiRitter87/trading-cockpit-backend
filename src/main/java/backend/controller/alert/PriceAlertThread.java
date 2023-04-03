package backend.controller.alert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.DataProvider;
import backend.controller.MainController;
import backend.dao.DAOManager;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.dao.quotation.QuotationProviderCNBCDAO;
import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderGlobeAndMailDAO;
import backend.dao.quotation.QuotationProviderInvestingDAO;
import backend.dao.quotation.QuotationProviderYahooDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;
import okhttp3.OkHttpClient;

/**
 * Queries Instrument quote data and updates price alerts if the trigger price has been reached.
 * 
 * @author Michael
 */
public class PriceAlertThread extends Thread {
	/**
	 * The start time of the trading session.
	 */
	private LocalTime startTime;
	
	/**
	 * The end time of the trading session.
	 */
	private LocalTime endTime;
	
	/**
	 * A Map of stock exchanges and their corresponding data providers.
	 */
	private Map<StockExchange, DataProvider> dataProviders;
	
	/**
	 * DAO to access stock quotes using Yahoo.
	 */
	private QuotationProviderYahooDAO quotationProviderYahooDAO;
	
	/**
	 * DAO to access stock quotes using investing.com.
	 */
	private QuotationProviderInvestingDAO quotationProviderInvestingDAO;
	
	/**
	 * DAO to access stock quotes using TheGlobeAndMail.com.
	 */
	private QuotationProviderGlobeAndMailDAO quotationProviderGlobeAndMailDAO;
	
	/**
	 * DAO to access stock quotes using CNBC.
	 */
	private QuotationProviderCNBCDAO quotationProviderCNBCDAO;
	
	/**
	 * DAO to access price alerts.
	 */
	private PriceAlertDAO priceAlertDAO;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(PriceAlertThread.class);
	
	
	/**
	 * Initializes the price alert thread.
	 * 
	 * @param startTime The start time of the process.
	 * @param endTime The end time of the process.
	 * @param dataProviders Stock exchanges and their corresponding data providers.
	 */
	public PriceAlertThread(final LocalTime startTime, final LocalTime endTime, final Map<StockExchange, DataProvider> dataProviders) {
		OkHttpClient okHttpClient = MainController.getInstance().getOkHttpClient();	
		
		this.startTime = startTime;
		this.endTime = endTime;
		this.dataProviders = dataProviders;
		
		this.quotationProviderYahooDAO = new QuotationProviderYahooDAO(okHttpClient);
		this.quotationProviderInvestingDAO = new QuotationProviderInvestingDAO();
		this.quotationProviderGlobeAndMailDAO = new QuotationProviderGlobeAndMailDAO();
		this.quotationProviderCNBCDAO = new QuotationProviderCNBCDAO(okHttpClient);
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
			
		//Get the Quotation of the Instrument defined in the price alert.
		try {
			quotation = this.getCurrentQuotationOfInstrument(priceAlert.getInstrument());
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
		if(priceAlert.getAlertType() == PriceAlertType.GREATER_OR_EQUAL && quotation.getClose().compareTo(priceAlert.getPrice()) >= 0)
			priceAlert.setTriggerTime(new Date());
		else if(priceAlert.getAlertType() == PriceAlertType.LESS_OR_EQUAL && quotation.getClose().compareTo(priceAlert.getPrice()) <= 0)
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
		
		percentDistance = quotation.getClose().divide(priceAlert.getPrice(), 4, RoundingMode.HALF_UP);
		percentDistance = percentDistance.subtract(BigDecimal.valueOf(1));
		percentDistance = percentDistance.multiply(BigDecimal.valueOf(100));
		
		return percentDistance.floatValue();
	}
	
	
	/**
	 * Determines the current Quotation for the given Instrument.
	 * 
	 * @param instrument The Instrument.
	 * @return The current Quotation.
	 * @throws Exception Determination of current Quotation failed.
	 */
	private Quotation getCurrentQuotationOfInstrument(final Instrument instrument) throws Exception {
		Quotation quotation;
		final DataProvider dataProvider;
		final QuotationProviderDAO quotationProviderDAO;
		
		dataProvider = this.dataProviders.get(instrument.getStockExchange());
		
		if(dataProvider == null)
			throw new Exception("There is no data provider defined for the stock exchange: " + instrument.getStockExchange().toString());
		
		quotationProviderDAO = this.getDAOForDataProvider(dataProvider);
		quotation = quotationProviderDAO.getCurrentQuotation(instrument);
		
		return quotation;
	}
	
	
	/**
	 * Provides the QuotationProviderDAO for the given DataProvider.
	 * @param dataProvider The DataProvider.
	 * @return The corresponding QuotationProviderDAO.
	 * @throws Exception DataProvider could not be determined.
	 */
	private QuotationProviderDAO getDAOForDataProvider(final DataProvider dataProvider) throws Exception {
		switch(dataProvider) {
			case YAHOO:
				return this.quotationProviderYahooDAO;
			case INVESTING:
				return this.quotationProviderInvestingDAO;
			case GLOBEANDMAIL:
				return this.quotationProviderGlobeAndMailDAO;
			case CNBC:
				return this.quotationProviderCNBCDAO;
			default:
				throw new Exception("No DAO could be determined for the DataProvider: " +dataProvider.toString());
		}
	}
}
