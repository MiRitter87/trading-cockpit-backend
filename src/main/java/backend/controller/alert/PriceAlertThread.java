package backend.controller.alert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.DataProvider;
import backend.controller.MailController;
import backend.dao.DAOManager;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderDAOFactory;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;

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
	 * DAO to access price alerts.
	 */
	private PriceAlertDAO priceAlertDAO;
	
	/**
	 * Controller used to send E-Mails.
	 */
	private MailController mailController;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(PriceAlertThread.class);
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	
	/**
	 * Initializes the price alert thread.
	 * 
	 * @param startTime The start time of the process.
	 * @param endTime The end time of the process.
	 * @param dataProviders Stock exchanges and their corresponding data providers.
	 * @throws Exception Failed to initialize PriceAlertThread.
	 */
	public PriceAlertThread(final LocalTime startTime, final LocalTime endTime, final Map<StockExchange, DataProvider> dataProviders) throws Exception {
		this.startTime = startTime;
		this.endTime = endTime;
		this.dataProviders = dataProviders;

		this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
		
		this.mailController = new MailController();
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
	 * An E-Mail is sent if requested.
	 * 
	 * @param priceAlert The price alert.
	 * @param quotation The Quotation.
	 * @throws Exception In case the update failed.
	 */
	private void checkAndUpdatePriceAlert(PriceAlert priceAlert, final Quotation quotation) throws Exception {
		String subject, body;
		
		priceAlert.setLastStockQuoteTime(new Date());
		priceAlert.setTriggerDistancePercent(this.getTriggerDistancePercent(priceAlert, quotation));
		
		//If the trigger price has been reached, set the trigger time of the price alert. Send Mail if requested.
		if(this.isAlertTriggered(priceAlert, quotation)) {
			priceAlert.setTriggerTime(new Date());
			
			if(priceAlert.isSendMail()) {
				subject = this.getMailSubject(priceAlert);
				body = this.getMailBody(priceAlert, quotation);
				this.mailController.sendMail(priceAlert.getAlertMailAddress(), subject, body);
				priceAlert.setMailTransmissionTime(new Date());				
			}
		}
		
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
		final QuotationProviderDAO quotationProviderDAO = this.getQuotationProviderDAO(instrument.getStockExchange());
		
		quotation = quotationProviderDAO.getCurrentQuotation(instrument);
		
		return quotation;
	}
	
	
	/**
	 * Gets the QuotationProviderDAO that is configured to be used for the given StockExchange.
	 * 
	 * @param stockExchange The StockExchange.
	 * @return The QuotationProviderDAO that is used for the given StockExchange.
	 * @throws Exception Failed to determine QuotationProviderDAO for the given StockExchange.
	 */
	private QuotationProviderDAO getQuotationProviderDAO(final StockExchange stockExchange) throws Exception {
		DataProvider dataProvider;
		QuotationProviderDAO quotationProviderDAO;
		
		dataProvider = this.dataProviders.get(stockExchange);
		
		if(dataProvider == null)
			throw new Exception("There is no data provider defined for the stock exchange: " + stockExchange.toString());
		
		quotationProviderDAO = QuotationProviderDAOFactory.getInstance().getQuotationProviderDAO(dataProvider);
		
		return quotationProviderDAO;
	}
	
	
	/**
	 * Checks if price alert has been triggered.
	 * @param priceAlert The Price Alert.
	 * @param quotation The Quotation containing the current price.
	 * @return True, if price alert has been triggered; false, if not.
	 */
	private boolean isAlertTriggered(PriceAlert priceAlert, final Quotation quotation) {
		if((priceAlert.getAlertType() == PriceAlertType.GREATER_OR_EQUAL && quotation.getClose().compareTo(priceAlert.getPrice()) >= 0) || 
				(priceAlert.getAlertType() == PriceAlertType.LESS_OR_EQUAL && quotation.getClose().compareTo(priceAlert.getPrice()) <= 0)) {
			
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Gets the subject of the PriceAlert mail.
	 * 
	 * @param priceAlert The PriceAlert.
	 * @return The mail subject.
	 */
	private String getMailSubject(final PriceAlert priceAlert) {
		String subject = "";
		
		if(priceAlert.getAlertType() == PriceAlertType.GREATER_OR_EQUAL) {
			subject = MessageFormat.format(this.resources.getString("priceAlert.mailSubjectGreatherThen"), priceAlert.getInstrument().getSymbol(), 
					priceAlert.getPrice(), priceAlert.getCurrency());
		} 
		else if(priceAlert.getAlertType() == PriceAlertType.LESS_OR_EQUAL) {
			subject = MessageFormat.format(this.resources.getString("priceAlert.mailSubjectLessThen"), priceAlert.getInstrument().getSymbol(), 
					priceAlert.getPrice(), priceAlert.getCurrency());
		}
		
		return subject;
	}
	
	
	/**
	 * Gets the body of the PriceAlert mail.
	 * 
	 * @param priceAlert The PriceAlert.
	 * @param quotation The Quotation containing the current price.
	 * @return The mail body.
	 */
	private String getMailBody(final PriceAlert priceAlert, final Quotation quotation) {
		String body = "";
		String datePattern = "dd.MM.yyyy HH:mm:ss";
		String formattedDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		
		formattedDate = dateFormat.format(priceAlert.getTriggerTime());
		
		body = MessageFormat.format(this.resources.getString("priceAlert.mailBody"), 
				formattedDate, quotation.getClose(), quotation.getCurrency(), priceAlert.getTriggerDistancePercent());
		
		return body;
	}
}
