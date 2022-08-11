package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the IndicatorCalculator.
 * 
 * @author Michael
 */
public class IndicatorCalculatorTest {
	/**
	 * The indicator calculator under test.
	 */
	private IndicatorCalculator indicatorCalculator;
	
	/**
	 * A trading instrument whose indicators are calculated.
	 */
	private Instrument dmlStock;
	
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationProviderDAO quotationProviderYahooDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		try {
			quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at the end of the test class.
	 */
	public static void tearDownClass() {
		quotationProviderYahooDAO = null;
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		try {
			this.indicatorCalculator = new IndicatorCalculator();
			this.initializeDummyInstrument();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.indicatorCalculator = null;
		this.dmlStock = null;
	}
	
	
	/**
	 * Initializes the dummy instrument.
	 */
	private void initializeDummyInstrument() {
		Set<Quotation> quotations = new HashSet<>();
		
		this.dmlStock = new Instrument();
		this.dmlStock.setSymbol("DML");
		this.dmlStock.setStockExchange(StockExchange.TSX);
		this.dmlStock.setType(InstrumentType.STOCK);
		this.dmlStock.setName("Denison Mines");
		
		try {
			quotations.addAll(quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, 1));
			this.dmlStock.setQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the calculation of the RS percentage sum.
	 */
	public void testGetRSPercentSum() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float expectedRSPercentSum, actualRSPercentSum;
		
		//Actual price: 1,36
		//Three months ago price: 1,75
		//Six months ago price: 1,49
		//Nine months ago price: 2,30
		//Twelve months ago price: 1,30
		//Expected rsPercentSum = 3 month performance + 3 month perf. + 6 month perf. + 9 month perf + 12 month perf.
		//= -22,29% -22,29% -8,72% -40,87% +4,62% = -89,55%
		expectedRSPercentSum = (float) -89.55;
		
		actualRSPercentSum = indicatorCalculator.getRSPercentSum(this.dmlStock,sortedQuotations.get(0));
		
		assertEquals(expectedRSPercentSum, actualRSPercentSum);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the RS percentage sum, if the quotation history is smaller than a year.
	 */
	public void testGetRSPercentSumOfIncompleteHistory() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float expectedRSPercentSum, actualRSPercentSum;
		
		//Remove the last quote from the instrument. The 12 month performance can't be calculated then.
		sortedQuotations.remove(sortedQuotations.size() - 1);
		this.dmlStock.setQuotations(new HashSet<>(sortedQuotations));
		
		//Actual price: 1,36
		//Three months ago price: 1,75
		//Six months ago price: 1,49
		//Nine months ago price: 2,30
		//Twelve months ago price: unknown
		//Expected rsPercentSum = 3 month performance + 3 month perf. + 6 month perf. + 9 month perf.
		//= -22,29% -22,29% -8,72% -40,87% = -94,17%
		expectedRSPercentSum = (float) -94.17;
		
		actualRSPercentSum = indicatorCalculator.getRSPercentSum(this.dmlStock,sortedQuotations.get(0));
		
		assertEquals(expectedRSPercentSum, actualRSPercentSum);
	}
}
