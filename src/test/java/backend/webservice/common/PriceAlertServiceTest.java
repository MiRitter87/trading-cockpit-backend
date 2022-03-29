package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.PriceAlertDAO;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertArray;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.StockExchange;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

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
			priceAlertDAO.deletePriceAlert(this.appleAlert);
			priceAlertDAO.deletePriceAlert(this.microsoftAlert);
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
	 * Tests the retrieval of a price alert.
	 */
	public void testGetPriceAlert() {
		WebServiceResult getPriceAlertResult;
		PriceAlert priceAlert;
		
		//Get the price alert.
		PriceAlertService service = new PriceAlertService();
		getPriceAlertResult = service.getPriceAlert(this.appleAlert.getId());
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getPriceAlertResult) == false);
		
		//Assure that a price alert is returned
		assertTrue(getPriceAlertResult.getData() instanceof PriceAlert);
		
		priceAlert = (PriceAlert) getPriceAlertResult.getData();
		
		//Check each attribute of the price alert.
		assertEquals(this.appleAlert.getId(), priceAlert.getId());
		assertEquals(this.appleAlert.getSymbol(), priceAlert.getSymbol());
		assertEquals(this.appleAlert.getStockExchange(), priceAlert.getStockExchange());
		assertTrue(this.appleAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.appleAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.appleAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of a price alert with an id that is unknown.
	 */
	public void testGetPriceAlertWithUnknownId() {
		WebServiceResult getPriceAlertResult;
		Integer unknownPriceAlertId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		//Get the price alert.
		PriceAlertService service = new PriceAlertService();
		getPriceAlertResult = service.getPriceAlert(unknownPriceAlertId);
		
		//Assure that no price alert is returned
		assertNull(getPriceAlertResult.getData());
				
		//There should be a return message of type E.
		assertTrue(getPriceAlertResult.getMessages().size() == 1);
		assertTrue(getPriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("priceAlert.notFound"), unknownPriceAlertId);
		actualErrorMessage = getPriceAlertResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all price alerts.
	 */
	public void testGetAllPriceAlerts() {
		WebServiceResult getPriceAlertsResult;
		PriceAlertArray priceAlerts;
		PriceAlert priceAlert;
		
		//Get the price alerts.
		PriceAlertService service = new PriceAlertService();
		getPriceAlertsResult = service.getPriceAlerts();
		priceAlerts = (PriceAlertArray) getPriceAlertsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getPriceAlertsResult) == false);
		
		//Check if two accounts are returned.
		assertTrue(priceAlerts.getPriceAlerts().size() == 2);
		
		//Check both price alerts by each attribute
		priceAlert = priceAlerts.getPriceAlerts().get(0);
		assertEquals(this.appleAlert.getId(), priceAlert.getId());
		assertEquals(this.appleAlert.getSymbol(), priceAlert.getSymbol());
		assertEquals(this.appleAlert.getStockExchange(), priceAlert.getStockExchange());
		assertTrue(this.appleAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.appleAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.appleAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		
		priceAlert = priceAlerts.getPriceAlerts().get(1);
		assertEquals(this.microsoftAlert.getId(), priceAlert.getId());
		assertEquals(this.microsoftAlert.getSymbol(), priceAlert.getSymbol());
		assertEquals(this.microsoftAlert.getStockExchange(), priceAlert.getStockExchange());
		assertTrue(this.microsoftAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.microsoftAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.microsoftAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
	}
}
