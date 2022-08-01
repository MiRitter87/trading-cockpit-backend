package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentArray;
import backend.model.instrument.InstrumentQuotationQueryParam;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.tools.test.ValidationMessageProvider;

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
	
	/**
	 * The first Quotation of the Apple stock.
	 */
	private Quotation appleQuotation1;
	
	/**
	 * The second Quotation of the Apple stock.
	 */
	private Quotation appleQuotation2;
	
	
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
		this.deleteDummyInstruments();
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
	private void deleteDummyInstruments() {
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
		Calendar calendar = Calendar.getInstance();
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AAPL");
		instrument.setName("Apple");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		calendar.setTime(new Date());
		this.appleQuotation1 = new Quotation();
		this.appleQuotation1.setDate(calendar.getTime());
		this.appleQuotation1.setPrice(BigDecimal.valueOf(78.54));
		this.appleQuotation1.setCurrency(Currency.USD);
		this.appleQuotation1.setVolume(6784544);
		instrument.addQuotation(this.appleQuotation1);
		
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		this.appleQuotation2 = new Quotation();
		this.appleQuotation2.setDate(calendar.getTime());
		this.appleQuotation2.setPrice(BigDecimal.valueOf(79.14));
		this.appleQuotation2.setCurrency(Currency.USD);
		this.appleQuotation2.setVolume(4584544);
		instrument.addQuotation(this.appleQuotation2);
		
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
		
		//Check each attribute of the instrument.
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
		final Integer unknownInstrumentId = 0;
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
	
	
	@Test
	/**
	 * Tests the retrieval of all instruments without quotations.
	 */
	public void testGetAllInstrumentsWithoutQuotations() {
		WebServiceResult getInstrumentsResult;
		InstrumentArray instruments;
		Instrument instrument;
		
		//Get the instruments.
		InstrumentService service = new InstrumentService();
		getInstrumentsResult = service.getInstruments(InstrumentQuotationQueryParam.NONE);
		instruments = (InstrumentArray) getInstrumentsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult) == false);
		
		//Check if two instruments are returned.
		assertEquals(2, instruments.getInstruments().size());
		
		//Check all instruments by each attribute.
		instrument = instruments.getInstruments().get(0);
		assertEquals(this.appleStock.getId(), instrument.getId());
		assertEquals(this.appleStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.appleStock.getName(), instrument.getName());
		assertEquals(this.appleStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.appleStock.getType(), instrument.getType());
		assertNull(instrument.getQuotations());
		
		instrument = instruments.getInstruments().get(1);
		assertEquals(this.microsoftStock.getId(), instrument.getId());
		assertEquals(this.microsoftStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.microsoftStock.getName(), instrument.getName());
		assertEquals(this.microsoftStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.microsoftStock.getType(), instrument.getType());
		assertNull(instrument.getQuotations());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all instruments with quotations.
	 */
	public void testGetAllInstrumentsWithQuotations() {
		WebServiceResult getInstrumentsResult;
		InstrumentArray instruments;
		Instrument instrument;
		Quotation actualQuotation;
		Iterator<Quotation> quotationIterator;
		
		//Get the instruments.
		InstrumentService service = new InstrumentService();
		getInstrumentsResult = service.getInstruments(InstrumentQuotationQueryParam.ALL);
		instruments = (InstrumentArray) getInstrumentsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult) == false);
		
		//Check if two instruments are returned.
		assertEquals(2, instruments.getInstruments().size());
		
		//Check all instruments by each attribute.
		//First instrument - Apple Stock
		instrument = instruments.getInstruments().get(0);
		assertEquals(this.appleStock.getId(), instrument.getId());
		assertEquals(this.appleStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.appleStock.getName(), instrument.getName());
		assertEquals(this.appleStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.appleStock.getType(), instrument.getType());
		assertEquals(this.appleStock.getQuotations().size(), instrument.getQuotations().size());
		
		//Check the quotations of the Apple stock.
		quotationIterator = instrument.getQuotations().iterator();
		while(quotationIterator.hasNext()) {
			actualQuotation = quotationIterator.next();
			
			if(actualQuotation.getId().equals(this.appleQuotation1.getId())) {
				assertEquals(this.appleQuotation1.getId() , actualQuotation.getId());
				assertEquals(this.appleQuotation1.getDate().getTime() , actualQuotation.getDate().getTime());
				assertTrue(this.appleQuotation1.getPrice().compareTo(actualQuotation.getPrice()) == 0);
				assertEquals(this.appleQuotation1.getCurrency() , actualQuotation.getCurrency());
				assertEquals(this.appleQuotation1.getVolume() , actualQuotation.getVolume());
			}
			else if(actualQuotation.getId().equals(this.appleQuotation2.getId())) {
				assertEquals(this.appleQuotation2.getId() , actualQuotation.getId());
				assertEquals(this.appleQuotation2.getDate().getTime() , actualQuotation.getDate().getTime());
				assertTrue(this.appleQuotation2.getPrice().compareTo(actualQuotation.getPrice()) == 0);
				assertEquals(this.appleQuotation2.getCurrency() , actualQuotation.getCurrency());
				assertEquals(this.appleQuotation2.getVolume() , actualQuotation.getVolume());
			}
			else {
				fail("The list contains an unrelated quotation.");
			}
		}
		
		//Second instrument - Microsoft stock
		instrument = instruments.getInstruments().get(1);
		assertEquals(this.microsoftStock.getId(), instrument.getId());
		assertEquals(this.microsoftStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.microsoftStock.getName(), instrument.getName());
		assertEquals(this.microsoftStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.microsoftStock.getType(), instrument.getType());
		assertEquals(this.microsoftStock.getQuotations().size(), instrument.getQuotations().size());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all instruments. Each instrument only has the most recent quotation attached.
	 */
	public void testGetAllInstrumentsWithMostRecentQuotation() {
		WebServiceResult getInstrumentsResult;
		InstrumentArray instruments;
		Instrument instrument;
		
		//Get the instruments.
		InstrumentService service = new InstrumentService();
		getInstrumentsResult = service.getInstruments(InstrumentQuotationQueryParam.MOST_RECENT);
		instruments = (InstrumentArray) getInstrumentsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult) == false);
		
		//Check if two instruments are returned.
		assertEquals(2, instruments.getInstruments().size());
		
		//Check all instruments by each attribute.
		//First instrument - Apple Stock
		instrument = instruments.getInstruments().get(0);
		assertEquals(this.appleStock.getId(), instrument.getId());
		assertEquals(this.appleStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.appleStock.getName(), instrument.getName());
		assertEquals(this.appleStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.appleStock.getType(), instrument.getType());
		assertEquals(1, instrument.getQuotations().size());		//Only one quotation expected - the most recent one.
		
		Quotation actualQuotation = instrument.getQuotations().iterator().next();
		assertEquals(this.appleQuotation2.getId() , actualQuotation.getId());
		assertEquals(this.appleQuotation2.getDate().getTime() , actualQuotation.getDate().getTime());
		assertTrue(this.appleQuotation2.getPrice().compareTo(actualQuotation.getPrice()) == 0);
		assertEquals(this.appleQuotation2.getCurrency() , actualQuotation.getCurrency());
		assertEquals(this.appleQuotation2.getVolume() , actualQuotation.getVolume());
		
		//Second instrument - Microsoft stock
		instrument = instruments.getInstruments().get(1);
		assertEquals(this.microsoftStock.getId(), instrument.getId());
		assertEquals(this.microsoftStock.getSymbol(), instrument.getSymbol());
		assertEquals(this.microsoftStock.getName(), instrument.getName());
		assertEquals(this.microsoftStock.getStockExchange(), instrument.getStockExchange());
		assertEquals(this.microsoftStock.getType(), instrument.getType());
		assertEquals(this.microsoftStock.getQuotations().size(), instrument.getQuotations().size());
	}
	
	
	@Test
	/**
	 * Tests deletion of an instrument.
	 */
	public void testDeleteInstrument() {
		WebServiceResult deleteInstrumentResult;
		Instrument deletedInstrument;
		
		try {
			//Delete Microsoft instrument using the service.
			InstrumentService service = new InstrumentService();
			deleteInstrumentResult = service.deleteInstrument(this.microsoftStock.getId());
			
			//There should be no error messages
			assertTrue(WebServiceTools.resultContainsErrorMessage(deleteInstrumentResult) == false);
			
			//There should be a success message
			assertTrue(deleteInstrumentResult.getMessages().size() == 1);
			assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);
			
			//Check if Apple alert is missing using the DAO.
			deletedInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId(), false);
			
			if(deletedInstrument != null)
				fail("Microsoft instrument is still persisted but should have been deleted by the WebService operation 'deleteInstrument'.");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Restore old database state by adding the instrument that has been deleted previously.
			try {
				this.microsoftStock = this.getMicrosoftStock();
				instrumentDAO.insertInstrument(this.microsoftStock);
			} 
			catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests deletion of an instrument with an unknown ID.
	 */
	public void testeDeleteInstrumentWithUnknownId() {
		WebServiceResult deleteInstrumentResult;
		final Integer unknownInstrumentId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		//Delete the instrument.
		InstrumentService service = new InstrumentService();
		deleteInstrumentResult = service.deleteInstrument(unknownInstrumentId);
		
		//There should be a return message of type E.
		assertTrue(deleteInstrumentResult.getMessages().size() == 1);
		assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.notFound"), unknownInstrumentId);
		actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests updating an instrument with valid data.
	 */
	public void testUpdateValidInstrument() {
		WebServiceResult updateInstrumentResult;
		Instrument updatedInstrument;
		InstrumentService service = new InstrumentService();
		
		//Update the name.
		this.appleStock.setName("Apple Inc.");
		updateInstrumentResult = service.updateInstrument(this.appleStock);
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(updateInstrumentResult) == false);
		
		//There should be a success message
		assertTrue(updateInstrumentResult.getMessages().size() == 1);
		assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);
		
		//Retrieve the updated instrument and check if the changes have been persisted.
		try {
			updatedInstrument = instrumentDAO.getInstrument(this.appleStock.getId(), false);
			assertEquals(this.appleStock.getName(), updatedInstrument.getName());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests updating an instrument with invalid data.
	 */
	public void testUpdateInvalidInstrument() {
		WebServiceResult updateInstrumentResult;
		InstrumentService service = new InstrumentService();
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();
		String actualErrorMessage, expectedErrorMessage;
		
		//Remove the symbol.
		this.microsoftStock.setSymbol("");
		updateInstrumentResult = service.updateInstrument(this.microsoftStock);
		
		//There should be a return message of type E.
		assertTrue(updateInstrumentResult.getMessages().size() == 1);
		assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//A proper message should be provided.
		expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "symbol", String.valueOf(this.microsoftStock.getSymbol().length()), "1", "6");
		actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests updating an instrument without changing any data.
	 */
	public void testUpdateUnchangedInstrument() {
		WebServiceResult updateInstrumentResult;
		InstrumentService service = new InstrumentService();
		String actualErrorMessage, expectedErrorMessage;
		
		//Update instrument without changing any data.
		updateInstrumentResult = service.updateInstrument(this.microsoftStock);
		
		//There should be a return message of type I
		assertTrue(updateInstrumentResult.getMessages().size() == 1);
		assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.I);
		
		//A proper message should be provided.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.updateUnchanged"), this.microsoftStock.getId());
		actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests updating an instrument where the update causes a duplicate instrument.
	 */
	public void testUpdateCreatingDuplicate() {
		Instrument databaseInstrument;
		WebServiceResult updateInstrumentResult;
		InstrumentService service = new InstrumentService();
		String actualErrorMessage, expectedErrorMessage;
		
		//Change an existing instrument in a way that a duplicate instrument will be created.
		this.microsoftStock.setSymbol("AAPL");
		
		//Update the instrument at the database via WebService.
		updateInstrumentResult = service.updateInstrument(this.microsoftStock);
		
		//There should be a return message of type E.
		assertTrue(updateInstrumentResult.getMessages().size() == 1);
		assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//A proper message should be provided.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.updateDuplicate"), 
				this.appleStock.getSymbol(), this.appleStock.getStockExchange());
		actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
		
		//The symbol change should not have been persisted.
		try {
			databaseInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId(), false);
			assertEquals("MSFT", databaseInstrument.getSymbol());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests adding of a new instrument.
	 */
	public void testAddValidInstrument() {
		Instrument newInstrument = new Instrument();
		Instrument adddedInstrument;
		WebServiceResult addInstrumentResult;
		InstrumentService service = new InstrumentService();
		
		//Define the new instrument.
		newInstrument.setSymbol("TSLA");
		newInstrument.setName("Tesla Inc.");
		newInstrument.setStockExchange(StockExchange.NYSE);
		newInstrument.setType(InstrumentType.STOCK);
		
		//Add the new instrument to the database via WebService
		addInstrumentResult = service.addInstrument(newInstrument);
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(addInstrumentResult) == false);
		
		//There should be a success message
		assertTrue(addInstrumentResult.getMessages().size() == 1);
		assertTrue(addInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);
		
		//The ID of the newly created instrument should be provided in the data part of the WebService return.
		assertNotNull(addInstrumentResult.getData());
		assertTrue(addInstrumentResult.getData() instanceof Integer);
		newInstrument.setId((Integer) addInstrumentResult.getData());
		
		//Read the persisted instrument via DAO
		try {
			adddedInstrument = instrumentDAO.getInstrument(newInstrument.getId(), false);
			
			//Check if the instrument read by the DAO equals the instrument inserted using the WebService in each attribute.
			assertEquals(newInstrument.getId(), adddedInstrument.getId());
			assertEquals(newInstrument.getSymbol(), adddedInstrument.getSymbol());
			assertEquals(newInstrument.getName(), adddedInstrument.getName());
			assertEquals(newInstrument.getStockExchange(), adddedInstrument.getStockExchange());
			assertEquals(newInstrument.getType(), adddedInstrument.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Delete the newly added price alert.
			try {
				instrumentDAO.deleteInstrument(newInstrument);
			} 
			catch (Exception e) {
				fail(e.getMessage());
			}
		}		
	}
	
	
	@Test
	/**
	 * Tests adding of an invalid instrument.
	 */
	public void testAddInvalidInstrument() {
		Instrument newInstrument = new Instrument();
		WebServiceResult addInstrumentResult;
		InstrumentService service = new InstrumentService();
		
		//Define the new instrument without a type.
		newInstrument.setSymbol("TSLA");
		newInstrument.setName("Tesla Inc.");
		newInstrument.setStockExchange(StockExchange.NYSE);
		
		//Add a new instrument to the database via WebService
		addInstrumentResult = service.addInstrument(newInstrument);
		
		//There should be a return message of type E.
		assertTrue(addInstrumentResult.getMessages().size() == 1);
		assertTrue(addInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//The new instrument should not have been persisted
		assertNull(newInstrument.getId());
	}
	
	
	@Test
	/**
	 * Tests adding an instrument which already exists (Symbol / Stock Exchange combination has to be distinct).
	 */
	public void testAddDuplicateInstrument() {
		Instrument newInstrument = new Instrument();
		WebServiceResult addInstrumentResult;
		InstrumentService service = new InstrumentService();
		String actualErrorMessage, expectedErrorMessage;
		
		//Define the new instrument without a type.
		newInstrument.setSymbol("AAPL");
		newInstrument.setName("Apple Computer");
		newInstrument.setStockExchange(StockExchange.NYSE);
		newInstrument.setType(InstrumentType.STOCK);
		
		//Add a new instrument to the database via WebService.
		addInstrumentResult = service.addInstrument(newInstrument);
		
		//There should be a return message of type E.
		assertTrue(addInstrumentResult.getMessages().size() == 1);
		assertTrue(addInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//A proper message should be provided.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.createDuplicate"), 
				this.appleStock.getSymbol(), this.appleStock.getStockExchange());
		actualErrorMessage = addInstrumentResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
		
		//The new instrument should not have been persisted.
		assertNull(newInstrument.getId());
	}
}
