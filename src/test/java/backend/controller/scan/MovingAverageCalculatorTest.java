package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Tests the MovingAvageCalculator.
 * 
 * @author Michael
 */
public class MovingAverageCalculatorTest {
	/**
	 * The MovingAverageCalculator under test.
	 */
	private MovingAverageCalculator movingAverageCalculator;
	
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
			this.movingAverageCalculator = new MovingAverageCalculator();
			this.initializeDmlInstrument();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.movingAverageCalculator = null;
		
		this.dmlStock = null;
	}
	
	
	/**
	 * Initializes the DML Instrument.
	 */
	private void initializeDmlInstrument() {
		List<Quotation> quotations = new ArrayList<>();
		
		this.dmlStock = new Instrument();
		this.dmlStock.setSymbol("DML");
		this.dmlStock.setStockExchange(StockExchange.TSX);
		this.dmlStock.setType(InstrumentType.STOCK);
		this.dmlStock.setName("Denison Mines");
		
		try {
			quotations.addAll(quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));
			this.dmlStock.setQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 50-day Simple Moving Average for the most recent Quotation.
	 */
	public void testGetSimpleMovingAverage50Days() {
		QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
		float actualSma50, expectedSma50 = (float) 1.416;
		
		actualSma50 = this.movingAverageCalculator.getSimpleMovingAverage(50, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedSma50, actualSma50);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 50-day Simple Moving Average for a historical Quotation.
	 */
	public void testGetSimpleMovingAverage50DaysHistorical() {
		QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
		float actualSma50, expectedSma50 = (float) 1.414;
		
		actualSma50 = this.movingAverageCalculator.getSimpleMovingAverage(50, sortedQuotations.getQuotations().get(2), sortedQuotations);
		
		assertEquals(expectedSma50, actualSma50);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 150-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage150Days() {
		QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
		float actualSma150, expectedSma150 = (float) 1.673;
		
		actualSma150 = this.movingAverageCalculator.getSimpleMovingAverage(150, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedSma150, actualSma150);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 200-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage200Days() {
		QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
		float actualSma200, expectedSma200 = (float) 1.788;
		
		actualSma200 = this.movingAverageCalculator.getSimpleMovingAverage(200, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedSma200, actualSma200);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 21-day Exponential Moving Average for the most recent Quotation.
	 */
	public void testGetExponentialMovingAverage21Days() {
		QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
		float actualEma21, expectedEma21 = (float) 1.385;
		
		actualEma21 = this.movingAverageCalculator.getExponentialMovingAverage(21, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedEma21, actualEma21);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 10-day Simple Moving Average Volume.
	 */
	public void testGetSimpleMovingAverageVolume() {
		QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
		long expectedVolume = 1440500, actualVolume;
		
		actualVolume = this.movingAverageCalculator.getSimpleMovingAverageVolume(10, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedVolume, actualVolume);
	}
}
