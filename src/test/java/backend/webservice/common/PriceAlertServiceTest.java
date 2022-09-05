package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertArray;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.tools.test.ValidationMessageProvider;

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
	 * DAO to access Instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * A price alert for the Apple stock.
	 */
	private PriceAlert appleAlert;
	
	/**
	 * A price alert for the Microsoft stock.
	 */
	private PriceAlert microsoftAlert;
	
	/**
	 * A price alert for the Netflix stock.
	 */
	private PriceAlert netflixAlert;
	
	/**
	 * A price alert for the Nvidia stock.
	 */
	private PriceAlert nvidiaAlert;
	
	/**
	 * Instrument of Apple stock.
	 */
	private Instrument appleInstrument;
	
	/**
	 * Instrument of Microsoft stock.
	 */
	private Instrument microsoftInstrument;
	
	/**
	 * Instrument of Netflix stock.
	 */
	private Instrument netflixInstrument;
	
	/**
	 * Instrument of NVidia stock.
	 */
	private Instrument nvidiaInstrument;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
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
		this.createDummyInstruments();
		this.createDummyPriceAlerts();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyPriceAlerts();
		this.deleteDummyInstruments();
	}
	
	
	/**
	 * Initializes the database with dummy price alerts.
	 */
	private void createDummyPriceAlerts() {
		this.appleAlert = this.getAppleAlert();
		this.microsoftAlert = this.getMicrosoftAlert();
		this.netflixAlert = this.getNetflixAlert();
		this.nvidiaAlert = this.getNvidiaAlert();
		
		try {
			priceAlertDAO.insertPriceAlert(this.appleAlert);
			priceAlertDAO.insertPriceAlert(this.microsoftAlert);
			priceAlertDAO.insertPriceAlert(this.netflixAlert);
			priceAlertDAO.insertPriceAlert(this.nvidiaAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy price alerts from the database.
	 */
	private void deleteDummyPriceAlerts() {
		try {
			priceAlertDAO.deletePriceAlert(this.nvidiaAlert);
			priceAlertDAO.deletePriceAlert(this.netflixAlert);
			priceAlertDAO.deletePriceAlert(this.microsoftAlert);
			priceAlertDAO.deletePriceAlert(this.appleAlert);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the database with dummy Instruments.
	 */
	private void createDummyInstruments() {
		this.appleInstrument = this.getAppleInstrument();
		this.microsoftInstrument = this.getMicrosoftInstrument();
		this.netflixInstrument = this.getNetflixInstrument();
		this.nvidiaInstrument = this.getNvidiaInstrument();
		
		try {
			instrumentDAO.insertInstrument(this.appleInstrument);
			instrumentDAO.insertInstrument(this.microsoftInstrument);
			instrumentDAO.insertInstrument(this.netflixInstrument);
			instrumentDAO.insertInstrument(this.nvidiaInstrument);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy Instruments from the database.
	 */
	private void deleteDummyInstruments() {
		try {
			instrumentDAO.deleteInstrument(this.nvidiaInstrument);
			instrumentDAO.deleteInstrument(this.netflixInstrument);
			instrumentDAO.deleteInstrument(this.microsoftInstrument);
			instrumentDAO.deleteInstrument(this.appleInstrument);
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
		
		alert.setInstrument(this.appleInstrument);
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
		
		alert.setInstrument(this.microsoftInstrument);
		alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		alert.setPrice(BigDecimal.valueOf(250.00));
		
		return alert;
	}
	
	
	/**
	 * Gets a price alert for the Netflix stock.
	 * 
	 * @return A price alert for the Netflix stock.
	 */
	private PriceAlert getNetflixAlert() {
		PriceAlert alert = new PriceAlert();
		
		alert = new PriceAlert();
		alert.setInstrument(this.netflixInstrument);
		alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		alert.setPrice(BigDecimal.valueOf(199.99));
		alert.setTriggerTime(new Date());
		alert.setConfirmationTime(null);
		
		return alert;
	}
	
	
	/**
	 * Gets a price alert for the Nvidia stock.
	 * 
	 * @return A price alert for the Nvidia stock.
	 */
	private PriceAlert getNvidiaAlert() {
		PriceAlert alert = new PriceAlert();
		
		alert = new PriceAlert();
		alert.setInstrument(this.nvidiaInstrument);
		alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		alert.setPrice(BigDecimal.valueOf(180.00));
		alert.setTriggerTime(new Date());
		alert.setConfirmationTime(new Date());
		
		return alert;
	}
	
	
	/**
	 * Gets the Instrument of the Apple stock.
	 * 
	 * @return The Instrument of the Apple stock.
	 */
	private Instrument getAppleInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AAPL");
		instrument.setName("Apple");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the Instrument of the Microsoft stock.
	 * 
	 * @return The Instrument of the Microsoft stock.
	 */
	private Instrument getMicrosoftInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("MSFT");
		instrument.setName("Microsoft");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the Instrument of the Netflix stock.
	 * 
	 * @return The Instrument of the Netflix stock.
	 */
	private Instrument getNetflixInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("NFLX");
		instrument.setName("Netflix");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the Instrument of the NVidia stock.
	 * 
	 * @return The Instrument of the NVidia stock.
	 */
	private Instrument getNvidiaInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("NVDA");
		instrument.setName("NVidia");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
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
		assertEquals(this.appleAlert.getInstrument(), priceAlert.getInstrument());
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
		getPriceAlertsResult = service.getPriceAlerts(TriggerStatus.ALL, ConfirmationStatus.ALL);
		priceAlerts = (PriceAlertArray) getPriceAlertsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getPriceAlertsResult) == false);
		
		//Check if four price alerts are returned.
		assertEquals(4, priceAlerts.getPriceAlerts().size());
		
		//Check all price alerts by each attribute
		priceAlert = priceAlerts.getPriceAlerts().get(0);
		assertEquals(this.appleAlert.getId(), priceAlert.getId());
		assertEquals(this.appleAlert.getInstrument(), priceAlert.getInstrument());
		assertTrue(this.appleAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.appleAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.appleAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		assertEquals(this.appleAlert.getLastStockQuoteTime(), priceAlert.getLastStockQuoteTime());
		
		priceAlert = priceAlerts.getPriceAlerts().get(1);
		assertEquals(this.microsoftAlert.getId(), priceAlert.getId());
		assertEquals(this.microsoftAlert.getInstrument(), priceAlert.getInstrument());
		assertTrue(this.microsoftAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.microsoftAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.microsoftAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		assertEquals(this.microsoftAlert.getLastStockQuoteTime(), priceAlert.getLastStockQuoteTime());
		
		priceAlert = priceAlerts.getPriceAlerts().get(2);
		assertEquals(this.netflixAlert.getId(), priceAlert.getId());
		assertEquals(this.netflixAlert.getInstrument(), priceAlert.getInstrument());
		assertTrue(this.netflixAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.netflixAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.netflixAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		assertEquals(this.netflixAlert.getLastStockQuoteTime(), priceAlert.getLastStockQuoteTime());
		
		priceAlert = priceAlerts.getPriceAlerts().get(3);
		assertEquals(this.nvidiaAlert.getId(), priceAlert.getId());
		assertEquals(this.nvidiaAlert.getInstrument(), priceAlert.getInstrument());
		assertTrue(this.nvidiaAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.nvidiaAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.nvidiaAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		assertEquals(this.nvidiaAlert.getLastStockQuoteTime(), priceAlert.getLastStockQuoteTime());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all price alerts that have been triggered but not confirmed yet.
	 */
	public void testGetAllPriceAlertsTriggeredNotConfirmed() {
		WebServiceResult getPriceAlertsResult;
		PriceAlertArray priceAlerts;
		PriceAlert priceAlert;
		
		//Get the price alerts.
		PriceAlertService service = new PriceAlertService();
		getPriceAlertsResult = service.getPriceAlerts(TriggerStatus.TRIGGERED, ConfirmationStatus.NOT_CONFIRMED);
		priceAlerts = (PriceAlertArray) getPriceAlertsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getPriceAlertsResult) == false);
		
		//Check if one price alert is returned.
		assertEquals(1, priceAlerts.getPriceAlerts().size());
		
		//Check if the correct price alert is returned
		priceAlert = priceAlerts.getPriceAlerts().get(0);
		assertEquals(this.netflixAlert.getId(), priceAlert.getId());
		assertEquals(this.netflixAlert.getInstrument(), priceAlert.getInstrument());
		assertTrue(this.netflixAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.netflixAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.netflixAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		assertEquals(this.netflixAlert.getLastStockQuoteTime(), priceAlert.getLastStockQuoteTime());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all price alerts that have been triggered and confirmed.
	 */
	public void testGetAllPriceAlertsTriggeredConfirmed() {
		WebServiceResult getPriceAlertsResult;
		PriceAlertArray priceAlerts;
		PriceAlert priceAlert;
		
		//Get the price alerts.
		PriceAlertService service = new PriceAlertService();
		getPriceAlertsResult = service.getPriceAlerts(TriggerStatus.TRIGGERED, ConfirmationStatus.CONFIRMED);
		priceAlerts = (PriceAlertArray) getPriceAlertsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getPriceAlertsResult) == false);
		
		//Check if one price alert is returned.
		assertEquals(1, priceAlerts.getPriceAlerts().size());
		
		//Check if the correct price alert is returned
		priceAlert = priceAlerts.getPriceAlerts().get(0);
		assertEquals(this.nvidiaAlert.getId(), priceAlert.getId());
		assertEquals(this.nvidiaAlert.getInstrument(), priceAlert.getInstrument());
		assertTrue(this.nvidiaAlert.getPrice().compareTo(priceAlert.getPrice()) == 0);
		assertEquals(this.nvidiaAlert.getTriggerTime(), priceAlert.getTriggerTime());
		assertEquals(this.nvidiaAlert.getConfirmationTime(), priceAlert.getConfirmationTime());
		assertEquals(this.nvidiaAlert.getLastStockQuoteTime(), priceAlert.getLastStockQuoteTime());
	}
	
	
	@Test
	/**
	 * Tests deletion of a price alert.
	 */
	public void testDeletePriceAlert() {
		WebServiceResult deletePriceAlertResult;
		PriceAlert deletedPriceAlert;
		
		try {
			//Delete Apple alert using the service.
			PriceAlertService service = new PriceAlertService();
			deletePriceAlertResult = service.deletePriceAlert(this.appleAlert.getId());
			
			//There should be no error messages
			assertTrue(WebServiceTools.resultContainsErrorMessage(deletePriceAlertResult) == false);
			
			//There should be a success message
			assertTrue(deletePriceAlertResult.getMessages().size() == 1);
			assertTrue(deletePriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.S);
			
			//Check if Apple alert is missing using the DAO.
			deletedPriceAlert = priceAlertDAO.getPriceAlert(this.appleAlert.getId());
			
			if(deletedPriceAlert != null)
				fail("Apple alert is still persisted but should have been deleted by the WebService operation 'deletePriceAlert'.");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Restore old database state by adding the price alert that has been deleted previously.
			try {
				this.appleAlert = this.getAppleAlert();
				priceAlertDAO.insertPriceAlert(this.appleAlert);
			} 
			catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests updating a price alert with valid data.
	 */
	public void testUpdateValidPriceAlert() {
		WebServiceResult updatePriceAlertResult;
		PriceAlert updatedPriceAlert;
		PriceAlertService service = new PriceAlertService();
		
		//Update the price.
		this.appleAlert.setPrice(BigDecimal.valueOf(186.30));
		updatePriceAlertResult = service.updatePriceAlert(this.appleAlert);
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(updatePriceAlertResult) == false);
		
		//There should be a success message
		assertTrue(updatePriceAlertResult.getMessages().size() == 1);
		assertTrue(updatePriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.S);
		
		//Retrieve the updated price alert and check if the changes have been persisted.
		try {
			updatedPriceAlert = priceAlertDAO.getPriceAlert(this.appleAlert.getId());
			assertTrue(this.appleAlert.getPrice().compareTo(updatedPriceAlert.getPrice()) == 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests updating a price alert with invalid data.
	 */
	public void testUpdateInvalidPriceAlert() {
		WebServiceResult updatePriceAlertResult;
		PriceAlertService service = new PriceAlertService();
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();
		String actualErrorMessage, expectedErrorMessage;
		
		//Remove the instrument.
		this.appleAlert.setInstrument(null);
		updatePriceAlertResult = service.updatePriceAlert(this.appleAlert);
		
		//There should be a return message of type E.
		assertTrue(updatePriceAlertResult.getMessages().size() == 1);
		assertTrue(updatePriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//A proper message should be provided.
		expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "instrument");
		actualErrorMessage = updatePriceAlertResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests updating a price alert without changing any data.
	 */
	public void testUpdateUnchangedPriceAlert() {
		WebServiceResult updatePriceAlertResult;
		PriceAlertService service = new PriceAlertService();
		String actualErrorMessage, expectedErrorMessage;
		
		//Update price alert without changing any data.
		updatePriceAlertResult = service.updatePriceAlert(this.appleAlert);
		
		//There should be a return message of type I
		assertTrue(updatePriceAlertResult.getMessages().size() == 1);
		assertTrue(updatePriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.I);
		
		//A proper message should be provided.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("priceAlert.updateUnchanged"), this.appleAlert.getId());
		actualErrorMessage = updatePriceAlertResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests adding of a new price alert.
	 */
	public void testAddValidPriceAlert() {
		PriceAlert newPriceAlert = new PriceAlert();
		PriceAlert adddedPriceAlert;
		WebServiceResult addPriceAlertResult;
		PriceAlertService service = new PriceAlertService();
		
		//Define the new price alert
		newPriceAlert.setInstrument(appleInstrument);
		newPriceAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		newPriceAlert.setPrice(BigDecimal.valueOf(149.99));
		
		//Add a new price alert to the database via WebService
		addPriceAlertResult = service.addPriceAlert(newPriceAlert);
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(addPriceAlertResult) == false);
		
		//There should be a success message
		assertTrue(addPriceAlertResult.getMessages().size() == 1);
		assertTrue(addPriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.S);
		
		//The ID of the newly created price alert should be provided in the data part of the WebService return.
		assertNotNull(addPriceAlertResult.getData());
		assertTrue(addPriceAlertResult.getData() instanceof Integer);
		newPriceAlert.setId((Integer) addPriceAlertResult.getData());
		
		//Read the persisted price alert via DAO
		try {
			adddedPriceAlert = priceAlertDAO.getPriceAlert(newPriceAlert.getId());
			
			//Check if the price alert read by the DAO equals the price alert inserted using the WebService in each attribute.
			assertEquals(newPriceAlert.getId(), adddedPriceAlert.getId());
			assertEquals(newPriceAlert.getInstrument(), adddedPriceAlert.getInstrument());
			assertTrue(newPriceAlert.getPrice().compareTo(adddedPriceAlert.getPrice()) == 0);
			assertEquals(newPriceAlert.getTriggerTime(), adddedPriceAlert.getTriggerTime());
			assertEquals(newPriceAlert.getConfirmationTime(), adddedPriceAlert.getConfirmationTime());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Delete the newly added price alert.
			try {
				priceAlertDAO.deletePriceAlert(newPriceAlert);
			} 
			catch (Exception e) {
				fail(e.getMessage());
			}
		}		
	}
	
	
	@Test
	/**
	 * Tests adding of an invalid price alert.
	 */
	public void testAddInvalidPriceAlert() {
		PriceAlert newPriceAlert = new PriceAlert();
		WebServiceResult addPriceAlertResult;
		PriceAlertService service = new PriceAlertService();
		
		//Define the new price alert without an Instrument.
		newPriceAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
		newPriceAlert.setPrice(BigDecimal.valueOf(149.99));
		
		//Add a new price alert to the database via WebService
		addPriceAlertResult = service.addPriceAlert(newPriceAlert);
		
		//There should be a return message of type E.
		assertTrue(addPriceAlertResult.getMessages().size() == 1);
		assertTrue(addPriceAlertResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//The new price alert should not have been persisted
		assertNull(newPriceAlert.getId());
	}
}
