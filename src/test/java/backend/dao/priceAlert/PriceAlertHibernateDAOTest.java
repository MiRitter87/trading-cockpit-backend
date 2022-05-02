package backend.dao.priceAlert;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import backend.dao.DAOManager;
import backend.model.StockExchange;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;

/**
 * Tests the Hibernate DAO for price alerts.
 * 
 * @author Michael
 */
public class PriceAlertHibernateDAOTest {
	/**
	 * The DAO to access price alerts.
	 */
	private static PriceAlertDAO priceAlertHibernateDAO;
	
	/**
	 * A price alert for the Apple stock.
	 */
	private PriceAlert appleAlert;
	
	/**
	 * A price alert for the Microsoft stock.
	 */
	private PriceAlert microsoftAlert;
	
	/**
	 * A price alert for the NVIDIA stock.
	 */
	private PriceAlert nvidiaAlert;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		priceAlertHibernateDAO = DAOManager.getInstance().getPriceAlertDAO();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		try {
			DAOManager.getInstance().close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.createDummyPriceAlerts();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyPriceAlerts();
	}
	
	
	/**
	 * Initializes the database with dummy price alerts.
	 */
	private void createDummyPriceAlerts() {
		Calendar lastStockQuote = Calendar.getInstance();
		
		this.appleAlert = new PriceAlert();
		this.appleAlert.setSymbol("AAPL");
		this.appleAlert.setStockExchange(StockExchange.NYSE);
		this.appleAlert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
		this.appleAlert.setPrice(BigDecimal.valueOf(185.50));
		this.appleAlert.setLastStockQuoteTime(null);
		
		this.microsoftAlert = new PriceAlert();
		this.microsoftAlert.setSymbol("MSFT");
		this.microsoftAlert.setStockExchange(StockExchange.NYSE);
		this.microsoftAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		this.microsoftAlert.setPrice(BigDecimal.valueOf(250.00));
		lastStockQuote.add(Calendar.MINUTE, -1);
		this.microsoftAlert.setLastStockQuoteTime(lastStockQuote.getTime());
		
		this.nvidiaAlert = new PriceAlert();
		this.nvidiaAlert.setSymbol("NVDA");
		this.nvidiaAlert.setStockExchange(StockExchange.NYSE);
		this.nvidiaAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		this.nvidiaAlert.setPrice(BigDecimal.valueOf(180.00));
		lastStockQuote.add(Calendar.MINUTE, -2);
		this.nvidiaAlert.setLastStockQuoteTime(lastStockQuote.getTime());
		
		try {
			priceAlertHibernateDAO.insertPriceAlert(this.appleAlert);
			priceAlertHibernateDAO.insertPriceAlert(this.microsoftAlert);
			priceAlertHibernateDAO.insertPriceAlert(this.nvidiaAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy price alerts from the database.
	 */
	private void deleteDummyPriceAlerts() {
		try {
			priceAlertHibernateDAO.deletePriceAlert(this.nvidiaAlert);
			priceAlertHibernateDAO.deletePriceAlert(this.microsoftAlert);
			priceAlertHibernateDAO.deletePriceAlert(this.appleAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/*
	 * TODO testGetPriceAlertsNotTriggered	Get price alerts that have not been triggered yet sorted by lastStockQuoteTime
	 */
}
