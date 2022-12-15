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

import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
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
	 * A Quotation for testing.
	 */
	private Quotation quotation1;
	
	/**
	 * A Quotation for testing.
	 */
	private Quotation quotation2;
	
	/**
	 * A Quotation for testing.
	 */
	private Quotation quotation3;
	
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
			this.initializeDummyQuotations();
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
		this.quotation3 = null;
		this.quotation2 = null;
		this.quotation1 = null;
	}
	
	
	/**
	 * Initializes the dummy instrument.
	 */
	private void initializeDummyInstrument() {
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
	
	
	/**
	 * Initializes dummy quotations.
	 */
	private void initializeDummyQuotations() {
		this.quotation1 = new Quotation();
		this.quotation1.setIndicator(new Indicator());
		this.quotation1.getIndicator().setRsPercentSum((float) 34.5);
		
		this.quotation2 = new Quotation();
		this.quotation2.setIndicator(new Indicator());
		this.quotation2.getIndicator().setRsPercentSum((float) -5);
		
		this.quotation3 = new Quotation();
		this.quotation3.setIndicator(new Indicator());
		this.quotation3.getIndicator().setRsPercentSum((float) 12.35);
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
		this.dmlStock.setQuotations(sortedQuotations);
		
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
	
	
	@Test
	/**
	 * Tests the calculation of RS numbers.
	 */
	public void testCalculateRsNumbers() {
		List<Quotation> quotations = new ArrayList<>();
		
		//Prepare all quotations on which the RS number is to be calculated.
		quotations.add(this.quotation1);
		quotations.add(this.quotation2);
		quotations.add(this.quotation3);
		
		//Calculate the RS numbers.
		this.indicatorCalculator.calculateRsNumbers(quotations);
		
		//Verify the correct calculation.
		assertEquals(33, this.quotation2.getIndicator().getRsNumber());
		assertEquals(67, this.quotation3.getIndicator().getRsNumber());
		assertEquals(100, this.quotation1.getIndicator().getRsNumber());
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 50-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage50Days() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float actualSma50, expectedSma50 = (float) 1.42;
		
		actualSma50 = this.indicatorCalculator.getSimpleMovingAverage(50, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedSma50, actualSma50);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 150-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage150Days() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float actualSma150, expectedSma150 = (float) 1.67;
		
		actualSma150 = this.indicatorCalculator.getSimpleMovingAverage(150, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedSma150, actualSma150);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 200-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage200Days() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float actualSma200, expectedSma200 = (float) 1.79;
		
		actualSma200 = this.indicatorCalculator.getSimpleMovingAverage(200, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedSma200, actualSma200);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the distance to the 52 week high.
	 */
	public void testGetDistanceTo52WeekHigh() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float actualDistanceTo52WeekHigh, expectedDistanceTo52WeekHigh = (float) -48.09;
		
		actualDistanceTo52WeekHigh = this.indicatorCalculator.getDistanceTo52WeekHigh(sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedDistanceTo52WeekHigh, actualDistanceTo52WeekHigh);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the distance to the 52 week low.
	 */
	public void testGetDistanceTo52WeekLow() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float actualDistanceTo52WeekLow, expectedDistanceTo52WeekLow = (float) 10.57;
		
		actualDistanceTo52WeekLow = this.indicatorCalculator.getDistanceTo52WeekLow(sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedDistanceTo52WeekLow, actualDistanceTo52WeekLow);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the standard deviation.
	 */
	public void testGetStandardDeviation() {
		float[] inputValues = {46, 69, 32, 60, 52, 41};
		float expectedStandardDeviation = (float) 12.1518;
		float actualStandardDeviation;
		
		actualStandardDeviation = this.indicatorCalculator.getStandardDeviation(inputValues);
		
		assertEquals(expectedStandardDeviation, actualStandardDeviation);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the Bollinger BandWidth.
	 */
	public void testGetBollingerBandWidth() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float actualBollingerBandWidth, expectedBollingerBandWidth = (float) 24.7;
		
		actualBollingerBandWidth = this.indicatorCalculator.getBollingerBandWidth(10, 2, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedBollingerBandWidth, actualBollingerBandWidth);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 10-day Simple Moving Average Volume.
	 */
	public void testGetSimpleMovingAverageVolume() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		long expectedVolume = 1440500, actualVolume;
		
		actualVolume = this.indicatorCalculator.getSimpleMovingAverageVolume(10, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedVolume, actualVolume);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the volume differential between two moving averages of the volume.
	 */
	public void testGetVolumeDifferential() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float expectedDifferential = (float) -17.58, actualDifferential;
		
		actualDifferential = this.indicatorCalculator.getVolumeDifferential(30, 10, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedDifferential, actualDifferential);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the volume differential if too few quotations exist for calculation of the moving average volume.
	 */
	public void testGetVolumeDifferentialWithTooFewQuotations() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		List<Quotation> reducedQuotationHistory = new ArrayList<>();
		float expectedDifferential = 0, actualDifferential;
		
		//Reduce the number of quotations below the number needed for moving average volume creation.
		reducedQuotationHistory.add(sortedQuotations.get(0));
		reducedQuotationHistory.add(sortedQuotations.get(1));
		reducedQuotationHistory.add(sortedQuotations.get(2));
		this.dmlStock.setQuotations(reducedQuotationHistory);
		
		actualDifferential = this.indicatorCalculator.getVolumeDifferential(30, 10, reducedQuotationHistory.get(0), reducedQuotationHistory);
		
		assertEquals(expectedDifferential, actualDifferential);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the base length in weeks since the last 52 week high.
	 */
	public void testGetBaseLengthWeeks() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		int expectedBaseLength = 35, actualBaseLength;
		
		actualBaseLength = this.indicatorCalculator.getBaseLengthWeeks(sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedBaseLength, actualBaseLength);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the Up/Down Volume ratio.
	 */
	public void testGetUpDownVolumeRatio() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float expectedUdVolRatio = (float) 0.95, actualUdVolRatio;
		
		actualUdVolRatio = this.indicatorCalculator.getUpDownVolumeRatio(50, sortedQuotations.get(0), sortedQuotations);
		
		assertEquals(expectedUdVolRatio, actualUdVolRatio);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the price performance of the given daily interval.
	 */
	public void testGetPricePerformanceForDays() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float expectedPerformance = (float) 6.25, actualPerformance;
		
		actualPerformance = this.indicatorCalculator.getPricePerformanceForDays(sortedQuotations.get(0), sortedQuotations, 20);
		
		assertEquals(expectedPerformance, actualPerformance);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the trading liquidity for the given daily interval.
	 */
	public void testGetLiquidityForDays() {
		List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
		float expectedLiquidity = (float) 2036934, actualLiquidity;
		
		actualLiquidity = this.indicatorCalculator.getLiquidityForDays(sortedQuotations.get(0), sortedQuotations, 20);
		
		assertEquals(expectedLiquidity, actualLiquidity);
	}
}
