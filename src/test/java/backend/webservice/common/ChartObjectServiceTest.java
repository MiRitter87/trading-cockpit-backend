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
import backend.dao.chart.ChartObjectDAO;
import backend.dao.instrument.InstrumentDAO;
import backend.model.StockExchange;
import backend.model.chart.HorizontalLine;
import backend.model.chart.HorizontalLineArray;
import backend.model.chart.HorizontalLineWS;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the ChartObjectService.
 * 
 * @author Michael
 */
public class ChartObjectServiceTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");	
	
	/**
	 * DAO to access chart object data.
	 */
	private static ChartObjectDAO chartObjectDAO;
	
	/**
	 * DAO to access Instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * Instrument of Apple stock.
	 */
	private Instrument appleInstrument;
	
	/**
	 * Instrument of Microsoft stock.
	 */
	private Instrument microsoftInstrument;
	
	/**
	 * A horizontal line of Apple.
	 */
	private HorizontalLine horizontalLine1;
	
	/**
	 * Another horizontal line of Apple.
	 */
	private HorizontalLine horizontalLine2;
	
	/**
	 * A horizontal line of Microsoft.
	 */
	private HorizontalLine horizontalLine3;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
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
		this.createDummyHorizontalLines();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyHorizontalLines();
		this.deleteDummyInstruments();
	}
	
	
	/**
	 * Initializes the database with dummy Instruments.
	 */
	private void createDummyInstruments() {
		this.appleInstrument = this.getAppleInstrument();
		this.microsoftInstrument = this.getMicrosoftInstrument();
		
		try {
			instrumentDAO.insertInstrument(this.appleInstrument);
			instrumentDAO.insertInstrument(this.microsoftInstrument);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy Instruments from the database.
	 */
	private void deleteDummyInstruments() {
		try {
			instrumentDAO.deleteInstrument(this.microsoftInstrument);
			instrumentDAO.deleteInstrument(this.appleInstrument);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the database with dummy horizontal lines.
	 */
	private void createDummyHorizontalLines() {
		this.horizontalLine1 = this.getHorizontalLine1();
		this.horizontalLine2 = this.getHorizontalLine2();
		this.horizontalLine3 = this.getHorizontalLine3();
		
		try {
			chartObjectDAO.insertHorizontalLine(this.horizontalLine1);
			chartObjectDAO.insertHorizontalLine(this.horizontalLine2);
			chartObjectDAO.insertHorizontalLine(this.horizontalLine3);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy horizontal lines from the database.
	 */
	private void deleteDummyHorizontalLines() {
		try {
			chartObjectDAO.deleteHorizontalLine(this.horizontalLine3);
			chartObjectDAO.deleteHorizontalLine(this.horizontalLine2);
			chartObjectDAO.deleteHorizontalLine(this.horizontalLine1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
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
	 * Gets the first horizontal line of the Apple stock.
	 * 
	 * @return The HorizontalLine.
	 */
	private HorizontalLine getHorizontalLine1() {
		HorizontalLine horizontalLine = new HorizontalLine();
		
		horizontalLine.setInstrument(this.appleInstrument);
		horizontalLine.setPrice(new BigDecimal("175.00"));
		
		return horizontalLine;
	}
	
	
	/**
	 * Gets the second horizontal line of the Apple stock.
	 * 
	 * @return The HorizontalLine.
	 */
	private HorizontalLine getHorizontalLine2() {
		HorizontalLine horizontalLine = new HorizontalLine();
		
		horizontalLine.setInstrument(this.appleInstrument);
		horizontalLine.setPrice(new BigDecimal("155.00"));
		
		return horizontalLine;
	}
	
	
	/**
	 * Gets the first horizontal line of the Microsoft stock.
	 * 
	 * @return The HorizontalLine.
	 */
	private HorizontalLine getHorizontalLine3() {
		HorizontalLine horizontalLine = new HorizontalLine();
		
		horizontalLine.setInstrument(this.microsoftInstrument);
		horizontalLine.setPrice(new BigDecimal("290.00"));
		
		return horizontalLine;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of a HorizontalLine.
	 */
	public void testGetHorizontalLine() {
		WebServiceResult getHorizontalLineResult;
		HorizontalLine horizontalLine;
		
		//Get the HorizontalLine.
		ChartObjectService service = new ChartObjectService();
		getHorizontalLineResult = service.getHorizontalLine(this.horizontalLine1.getId());
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getHorizontalLineResult) == false);
		
		//Assure that a HorizontalLine is returned
		assertTrue(getHorizontalLineResult.getData() instanceof HorizontalLine);
		
		horizontalLine = (HorizontalLine) getHorizontalLineResult.getData();
		
		//Check each attribute of the HorizontalLine.
		assertEquals(this.horizontalLine1, horizontalLine);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of a HorizontalLine with an id that is unknown.
	 */
	public void testGetHorizontalLineWithUnknownId() {
		WebServiceResult getHorizontalLineResult;
		Integer unknownHorizontalLineId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		//Get the HorizontalLine.
		ChartObjectService service = new ChartObjectService();
		getHorizontalLineResult = service.getHorizontalLine(unknownHorizontalLineId);
		
		//Assure that no HorizontalLine is returned
		assertNull(getHorizontalLineResult.getData());
				
		//There should be a return message of type E.
		assertTrue(getHorizontalLineResult.getMessages().size() == 1);
		assertTrue(getHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("horizontalLine.notFound"), unknownHorizontalLineId);
		actualErrorMessage = getHorizontalLineResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all horizontal lines.
	 */
	public void testGetAllHorizontalLines() {
		WebServiceResult getHorizontalLinesResult;
		HorizontalLineArray horizontalLines;
		HorizontalLine horizontalLine;
		
		//Get the horizontal lines.
		ChartObjectService service = new ChartObjectService();
		getHorizontalLinesResult = service.getHorizontalLines(null);
		horizontalLines = (HorizontalLineArray) getHorizontalLinesResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getHorizontalLinesResult) == false);
		
		//Check if three horizontal lines are returned.
		assertEquals(3, horizontalLines.getHorizontalLines().size());
		
		//Check all horizontal lines by each attribute
		horizontalLine = horizontalLines.getHorizontalLines().get(0);
		assertEquals(this.horizontalLine1, horizontalLine);
		
		horizontalLine = horizontalLines.getHorizontalLines().get(1);
		assertEquals(this.horizontalLine2, horizontalLine);
		
		horizontalLine = horizontalLines.getHorizontalLines().get(2);
		assertEquals(this.horizontalLine3, horizontalLine);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all horizontal lines of the Microsoft stock.
	 */
	public void testGetAllHorizontalLinesMicrosoft() {
		WebServiceResult getHorizontalLinesResult;
		HorizontalLineArray horizontalLines;
		HorizontalLine horizontalLine;
		
		//Get the horizontal lines of the Microsoft stock.
		ChartObjectService service = new ChartObjectService();
		getHorizontalLinesResult = service.getHorizontalLines(this.microsoftInstrument.getId());
		horizontalLines = (HorizontalLineArray) getHorizontalLinesResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getHorizontalLinesResult) == false);
		
		//Check if one HorizontalLine is returned.
		assertEquals(1, horizontalLines.getHorizontalLines().size());
		
		//Check if the expected HorizontalLine is returned.
		horizontalLine = horizontalLines.getHorizontalLines().get(0);
		assertEquals(this.horizontalLine3, horizontalLine);
	}
	
	
	@Test
	/**
	 * Tests deletion of a HorizontalLine.
	 */
	public void testDeleteHorizontalLine() {
		WebServiceResult deleteHorizontalLineResult;
		HorizontalLine deletedHorizontalLine;
		
		try {
			//Delete HorizontalLine of Microsoft stock using the service.
			ChartObjectService service = new ChartObjectService();
			deleteHorizontalLineResult = service.deleteHorizontalLine(this.horizontalLine3.getId());
			
			//There should be no error messages
			assertTrue(WebServiceTools.resultContainsErrorMessage(deleteHorizontalLineResult) == false);
			
			//There should be a success message
			assertTrue(deleteHorizontalLineResult.getMessages().size() == 1);
			assertTrue(deleteHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.S);
			
			//Check if previously deleted HorizontalLine is missing using the DAO.
			deletedHorizontalLine = chartObjectDAO.getHorizontalLine(this.horizontalLine3.getId());
			
			if(deletedHorizontalLine != null)
				fail("Microsoft horizontal line is still persisted but should have been deleted by the WebService operation 'deleteHorizontalLine'.");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Restore old database state by adding the HorizontalLine that has been deleted previously.
			try {
				this.horizontalLine3 = this.getHorizontalLine3();
				chartObjectDAO.insertHorizontalLine(this.horizontalLine3);
			} 
			catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests deletion of a HorizontalLine with an unknown ID.
	 */
	public void testDeleteHorizontalLineUnknownId() {
		WebServiceResult deleteHorizontalLineResult;
		Integer unknownHorizontalLineId = 0;
		String expectedErrorMessage, actualErrorMessage;
		
		ChartObjectService service = new ChartObjectService();
		deleteHorizontalLineResult = service.deleteHorizontalLine(unknownHorizontalLineId);
		
		//There should be a return message of type E.
		assertTrue(deleteHorizontalLineResult.getMessages().size() == 1);
		assertTrue(deleteHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//Verify the expected error message.
		expectedErrorMessage = MessageFormat.format(this.resources.getString("horizontalLine.notFound"), unknownHorizontalLineId);
		actualErrorMessage = deleteHorizontalLineResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests updating a HorizontalLine with valid data.
	 */
	public void testUpdateValidHorizontalLine() {
		WebServiceResult updateHorizontalLineResult;
		HorizontalLine updatedHorizontalLine;
		ChartObjectService service = new ChartObjectService();
		
		//Update the price.
		this.horizontalLine3.setPrice(new BigDecimal("300.00"));
		updateHorizontalLineResult = service.updateHorizontalLine(this.convertToWsHorizontalLine(this.horizontalLine3));
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(updateHorizontalLineResult) == false);
		
		//There should be a success message
		assertTrue(updateHorizontalLineResult.getMessages().size() == 1);
		assertTrue(updateHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.S);
		
		//Retrieve the updated HorizontalLine and check if the changes have been persisted.
		try {
			updatedHorizontalLine = chartObjectDAO.getHorizontalLine(this.horizontalLine3.getId());
			assertTrue(this.horizontalLine3.getPrice().compareTo(updatedHorizontalLine.getPrice()) == 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests updating a HorizontalLine with invalid data.
	 */
	public void testUpdateInvalidHorizontalLine() {
		WebServiceResult updateHorizontalLineResult;
		ChartObjectService service = new ChartObjectService();
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();
		String actualErrorMessage, expectedErrorMessage;
		
		//Remove the instrument.
		this.horizontalLine3.setInstrument(null);
		updateHorizontalLineResult = service.updateHorizontalLine(this.convertToWsHorizontalLine(this.horizontalLine3));
		
		//There should be a return message of type E.
		assertTrue(updateHorizontalLineResult.getMessages().size() == 1);
		assertTrue(updateHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.E);
		
		//A proper message should be provided.
		expectedErrorMessage = messageProvider.getNotNullValidationMessage("horizontalLine", "instrument");
		actualErrorMessage = updateHorizontalLineResult.getMessages().get(0).getText();
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	/**
	 * Converts a HorizontalLine to the lean WebService representation.
	 * 
	 * @param horizontalLine The HorizontalLine to be converted.
	 * @return The lean WebService representation of the HorizontalLine.
	 */
	private HorizontalLineWS convertToWsHorizontalLine(final HorizontalLine horizontalLine) {
		HorizontalLineWS horizontalLineWS = new HorizontalLineWS();
		
		//Simple attributes.
		horizontalLineWS.setId(horizontalLine.getId());
		horizontalLineWS.setPrice(horizontalLine.getPrice());
		
		//Object references.
		if(horizontalLine.getInstrument() != null)
			horizontalLineWS.setInstrumentId(horizontalLine.getInstrument().getId());
		
		return horizontalLineWS;
	}
}
