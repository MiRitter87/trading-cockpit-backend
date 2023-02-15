package backend.controller;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

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

/**
 * Tests the InstrumentCheckController.
 * 
 * @author Michael
 */
public class InstrumentCheckControllerTest {
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
	 * Dummy test. Can be removed after first real functional test is implemented.
	 */
	public void testTest() {
		System.out.println(dmlQuotations.size());
	}
}
