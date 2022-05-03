package backend.dao.priceAlert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	private static PriceAlertDAO priceAlertDAO;
	
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
	
	/**
	 * A price alert for the Netflix stock.
	 */
	private PriceAlert netflixAlert;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
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
		
		this.netflixAlert = new PriceAlert();
		this.netflixAlert.setSymbol("NFLX");
		this.netflixAlert.setStockExchange(StockExchange.NYSE);
		this.netflixAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		this.netflixAlert.setPrice(BigDecimal.valueOf(199.99));
		this.netflixAlert.setLastStockQuoteTime(null);
		this.netflixAlert.setTriggerTime(new Date());
		
		try {
			priceAlertDAO.insertPriceAlert(this.appleAlert);
			priceAlertDAO.insertPriceAlert(this.microsoftAlert);
			priceAlertDAO.insertPriceAlert(this.nvidiaAlert);
			priceAlertDAO.insertPriceAlert(this.netflixAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy price alerts from the database.
	 */
	private void deleteDummyPriceAlerts() {
		try {
			priceAlertDAO.deletePriceAlert(this.netflixAlert);
			priceAlertDAO.deletePriceAlert(this.nvidiaAlert);
			priceAlertDAO.deletePriceAlert(this.microsoftAlert);
			priceAlertDAO.deletePriceAlert(this.appleAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests getting all price alerts that have not been triggered, sorted by lastStockQuoteTime.
	 */
	public void testGetPriceAlertsNotTriggered() {
		List<PriceAlert> priceAlerts;
		PriceAlert priceAlert;
		
		try {
			priceAlerts = priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.LAST_STOCK_QUOTE_TIME, true);
			
			//3 price alerts have not been triggered and therefore should be returned.
			assertEquals(3, priceAlerts.size());
			
			//Assure correct sorting
			priceAlert = priceAlerts.get(0);
			assertEquals(priceAlert.getId(), this.appleAlert.getId());
			priceAlert = priceAlerts.get(1);
			assertEquals(priceAlert.getId(), this.nvidiaAlert.getId());
			priceAlert = priceAlerts.get(2);
			assertEquals(priceAlert.getId(), this.microsoftAlert.getId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
