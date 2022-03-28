package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.PriceAlertDAO;
import backend.model.PriceAlert;
import backend.model.PriceAlertType;
import backend.model.StockExchange;

/**
 * Tests the price alert service.
 * 
 * @author Michael
 */
public class PriceAlertServiceTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");	
	
	/**
	 * DAO to access price alert data.
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
		this.appleAlert = this.getAppleAlert();
		this.microsoftAlert = this.getMicrosoftAlert();
		
		try {
			priceAlertDAO.insertPriceAlert(this.appleAlert);
			priceAlertDAO.insertPriceAlert(this.microsoftAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy price alerts from the database.
	 */
	private void deleteDummyPriceAlerts() {
		try {
			priceAlertDAO.deleteAccount(this.appleAlert);
			priceAlertDAO.deleteAccount(this.microsoftAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets a price alert for the Apple stock.
	 * 
	 * @return A price alert for the Apple stock.
	 */
	private PriceAlert getAppleAlert() {
		PriceAlert alert = new PriceAlert();
		
		alert.setSymbol("AAPL");
		alert.setStockExchange(StockExchange.NYSE);
		alert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
		alert.setPrice(BigDecimal.valueOf(185.50));
		
		return alert;
	}
	
	
	/**
	 * Gets a price alert for the Microsoft stock.
	 * 
	 * @return A price alert for the Microsoft stock.
	 */
	private PriceAlert getMicrosoftAlert() {
		PriceAlert alert = new PriceAlert();
		
		alert.setSymbol("MSFT");
		alert.setStockExchange(StockExchange.NYSE);
		alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		alert.setPrice(BigDecimal.valueOf(250.00));
		
		return alert;
	}
	
	
	@Test
	/**
	 * A dummy test.
	 */
	public void dummyTest() {
		int a, b;
		
		a = 1;
		b = 1;
		
		assertTrue(a == b);
	}
}
