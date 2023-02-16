package backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
 * Tests the InstrumentCheckController.
 * 
 * @author Michael
 */
public class InstrumentCheckControllerTest {
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
	private static List<Quotation> dmlQuotations;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
		
		initializeDMLQuotations();
		initializeDMLIndicators();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderYahooDAO = null;
	}
	
	
	/**
	 * Initializes quotations of the DML stock.
	 */
	private static void initializeDMLQuotations() {
		try {
			dmlQuotations = quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the indicators of the DML stock.
	 */
	private static void initializeDMLIndicators() {
		IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
		List<Quotation> sortedQuotations;
		Instrument instrument = new Instrument();
		Quotation quotation;

		instrument.setQuotations(dmlQuotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		for(int i = 0; i < sortedQuotations.size(); i++) {
			quotation = sortedQuotations.get(i);
			quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
		}
	}
	
	
	@Test
	/**
	 * Tests the check if Instrument closed below SMA(50).
	 */
	public void checkCloseBelowSma50Test() {
		ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
		ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
		ProtocolEntry actualProtocolEntry;
		List<ProtocolEntry> protocolEntries;
		Calendar calendar = Calendar.getInstance();
		InstrumentCheckController controller = new InstrumentCheckController();
		
		//Define the expected protocol entries.
		calendar.set(2022, 3, 21);		//The day on which the price closed below the SMA(50).
		expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
		expectedProtocolEntry1.setCategory(ProtocolEntryCategory.VIOLATION);
		expectedProtocolEntry1.setText(this.resources.getString("protocol.closeBelowSma50"));
		
		calendar.set(2022, 6, 22);		//The day on which the price closed below the SMA(50).
		expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
		expectedProtocolEntry2.setCategory(ProtocolEntryCategory.VIOLATION);
		expectedProtocolEntry2.setText(this.resources.getString("protocol.closeBelowSma50"));
		
		//Call controller to perform check.
		calendar.set(2022, 3, 7);	//Begin check on 07.04.22
		try {
			protocolEntries = controller.checkCloseBelowSma50(calendar.getTime(), dmlQuotations);
			
			//Verify the check result
			assertEquals(2, protocolEntries.size());
			
			//Validate the protocol entries
			actualProtocolEntry = protocolEntries.get(0);
			assertEquals(expectedProtocolEntry1, actualProtocolEntry);
			
			actualProtocolEntry = protocolEntries.get(1);
			assertEquals(expectedProtocolEntry2, actualProtocolEntry);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
