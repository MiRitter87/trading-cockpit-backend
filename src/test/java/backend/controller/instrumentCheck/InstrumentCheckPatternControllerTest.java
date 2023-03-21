package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.IndicatorCalculator;
import backend.dao.quotation.QuotationProviderYahooDAO;
import backend.dao.quotation.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Tests the InstrumentCheckPatternController.
 * 
 * @author Michael
 */
public class InstrumentCheckPatternControllerTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");		
	
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationProviderYahooDAO quotationProviderYahooDAO;
	
	/**
	 * A list of quotations of the DML stock.
	 */
	private List<Quotation> dmlQuotations;
	
	/**
	 * The controller for Instrument checks.
	 */
	private InstrumentCheckPatternController instrumentCheckPatternController;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderYahooDAO = null;
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.instrumentCheckPatternController = new InstrumentCheckPatternController();
		
		this.initializeDMLQuotations();
		this.initializeDMLIndicators();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.instrumentCheckPatternController = null;
		this.dmlQuotations = null;
	}
	
	
	/**
	 * Initializes quotations of the DML stock.
	 */
	private void initializeDMLQuotations() {
		try {
			dmlQuotations = quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the indicators of the DML stock.
	 */
	private void initializeDMLIndicators() {
		IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
		List<Quotation> sortedQuotations;
		Instrument instrument = new Instrument();
		Quotation quotation;

		instrument.setQuotations(this.dmlQuotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		for(int i = 0; i < sortedQuotations.size(); i++) {
			quotation = sortedQuotations.get(i);
			
			//Calculate all Indicators only for most recent Quotation like in the ScanThread.
			if(i == 0)
				quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
			else
				quotation = indicatorCalculator.calculateIndicators(instrument, quotation, false);
		}
	}
	
	
	@Test
	/**
	 * Tests the check if Instrument has advanced a certain amount on above-average volume.
	 */
	public void testCheckUpOnVolume() {
		ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
		ProtocolEntry actualProtocolEntry;
		List<ProtocolEntry> protocolEntries;
		Calendar calendar = Calendar.getInstance();
		
		//Define the expected protocol entry.
		calendar.set(2022, 6, 14);		//Up on Volume day is 14.07.22
		expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
		expectedProtocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
		expectedProtocolEntry.setText(this.resources.getString("protocol.upOnVolume"));
		
		//Call controller to perform check.
		calendar.set(2022, 6, 8);	//Begin check on 08.07.22
		try {
			protocolEntries = this.instrumentCheckPatternController.checkUpOnVolume(calendar.getTime(), this.dmlQuotations);
			
			//Verify the check result.
			assertEquals(1, protocolEntries.size());
			
			//Validate the protocol entry.
			actualProtocolEntry = protocolEntries.get(0);
			assertEquals(expectedProtocolEntry, actualProtocolEntry);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the check if Instrument has declined a certain amount on above-average volume.
	 */
	public void testCheckDownOnVolume() {
		ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
		ProtocolEntry actualProtocolEntry;
		List<ProtocolEntry> protocolEntries;
		Calendar calendar = Calendar.getInstance();

		//Define the expected protocol entry.
		calendar.set(2022, 6, 22);		//Down on Volume day is 22.07.22
		expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
		expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
		expectedProtocolEntry.setText(this.resources.getString("protocol.downOnVolume"));

		//Call controller to perform check.
		calendar.set(2022, 5, 9);	//Begin check on 09.06.22
		try {
			protocolEntries = this.instrumentCheckPatternController.checkDownOnVolume(calendar.getTime(), this.dmlQuotations);

			//Verify the check result.
			assertEquals(1, protocolEntries.size());

			//Validate the protocol entry.
			actualProtocolEntry = protocolEntries.get(0);
			assertEquals(expectedProtocolEntry, actualProtocolEntry);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the check if Instrument is churning (price stalling on increased volume).
	 */
	public void testCheckChurning() {
		ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
		ProtocolEntry actualProtocolEntry;
		List<ProtocolEntry> protocolEntries;
		Calendar calendar = Calendar.getInstance();
		
		//Define the expected protocol entry.
		calendar.set(2022, 2, 10);		//Churning on 10.03.22
		expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
		expectedProtocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
		expectedProtocolEntry.setText(this.resources.getString("protocol.churning"));
		
		//Call controller to perform check.
		calendar.set(2022, 2, 10);	//Begin check on 10.03.22
		try {
			protocolEntries = this.instrumentCheckPatternController.checkChurning(calendar.getTime(), this.dmlQuotations);
			
			//Verify the check result.
			assertEquals(1, protocolEntries.size());
			
			//Validate the protocol entry.
			actualProtocolEntry = protocolEntries.get(0);
			assertEquals(expectedProtocolEntry, actualProtocolEntry);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
