package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Tests the instrument service.
 * 
 * @author Michael
 */
public class InstrumentServiceTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");	
	
	/**
	 * DAO to access instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * The stock of Apple.
	 */
	private Instrument appleStock;
	
	/**
	 * The stock of Microsoft.
	 */
	private Instrument microsoftStock;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
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
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyPriceAlerts();
	}
	
	
	/**
	 * Initializes the database with dummy instruments.
	 */
	private void createDummyInstruments() {
		this.appleStock = this.getAppleStock();
		this.microsoftStock = this.getMicrosoftStock();
		
		try {
			instrumentDAO.insertInstrument(this.appleStock);
			instrumentDAO.insertInstrument(this.microsoftStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy instruments from the database.
	 */
	private void deleteDummyPriceAlerts() {
		try {
			instrumentDAO.deleteInstrument(this.microsoftStock);
			instrumentDAO.deleteInstrument(this.appleStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets the instrument of the Apple stock.
	 * 
	 * @return The instrument of the Apple stock.
	 */
	private Instrument getAppleStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AAPL");
		instrument.setName("Apple");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the instrument of the Microsoft stock.
	 * 
	 * @return The instrument of the Microsoft stock.
	 */
	private Instrument getMicrosoftStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("MSFT");
		instrument.setName("Microsoft");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of an instrument.
	 */
	public void testGetInstrument() {
		WebServiceResult getInstrumentResult;
		Instrument instrument;
		
		//Get the instrument.
		InstrumentService service = new InstrumentService();
		getInstrumentResult = service.getInstrument(this.appleStock.getId());
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentResult) == false);
		
		//Assure that an instrument is returned
		assertTrue(getInstrumentResult.getData() instanceof Instrument);
		
		instrument = (Instrument) getInstrumentResult.getData();
		
		//Check each attribute of the price alert.
		assertEquals(this.appleStock.getId(), instrument.getId());
		assertEquals(this.appleStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.appleStock.getName(), instrument.getName());
		assertEquals(this.appleStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.appleStock.getType(), instrument.getType());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of an instrument with an id that is unknown.
	 */
	public void testGetInstrumentWithUnknownId() {
		WebServiceResult getInstrumentResult;
		Integer unknownInstrumentId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		//Get the instrument.
		InstrumentService service = new InstrumentService();
		getInstrumentResult = service.getInstrument(unknownInstrumentId);
		
		//Assure that no instrument is returned
		assertNull(getInstrumentResult.getData());
				
		//There should be a return message of type E.
		assertTrue(getInstrumentResult.getMessages().size() == 1);
		assertTrue(getInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.notFound"), unknownInstrumentId);
		actualErrorMessage = getInstrumentResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
}
