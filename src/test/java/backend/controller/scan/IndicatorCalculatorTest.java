package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Calendar;
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
import backend.model.instrument.QuotationArray;
import backend.tools.DateTools;

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
	 * Stock of Rio Tinto.
	 */
	private Instrument rioStock;
	
	/**
	 * A Quotation for testing.
	 */
	private Quotation dmlQuotation1;
	
	/**
	 * A Quotation for testing.
	 */
	private Quotation dmlQuotation2;
	
	/**
	 * A Quotation for testing.
	 */
	private Quotation dmlQuotation3;
	
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
			this.initializeDmlInstrument();
			this.initializeDmlQuotations();
			this.initializeRioInstrument();
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
		this.rioStock = null;
		
		this.dmlQuotation3 = null;
		this.dmlQuotation2 = null;
		this.dmlQuotation1 = null;
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
	
	
	/**
	 * Initializes the RIO Instrument.
	 */
	private void initializeRioInstrument() {
		List<Quotation> quotations = new ArrayList<>();
		
		this.rioStock = new Instrument();
		this.rioStock.setSymbol("RIO");
		this.rioStock.setStockExchange(StockExchange.LSE);
		this.rioStock.setType(InstrumentType.STOCK);
		this.rioStock.setName("Rio Tinto");
		
		try {
			quotations.addAll(quotationProviderYahooDAO.getQuotationHistory("RIO", StockExchange.LSE, InstrumentType.STOCK, 1));
			this.rioStock.setQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	
	/**
	 * Initializes dummy quotations of the DML Instrument.
	 */
	private void initializeDmlQuotations() {
		this.dmlQuotation1 = new Quotation();
		this.dmlQuotation1.setIndicator(new Indicator());
		this.dmlQuotation1.getIndicator().setRsPercentSum((float) 34.5);
		
		this.dmlQuotation2 = new Quotation();
		this.dmlQuotation2.setIndicator(new Indicator());
		this.dmlQuotation2.getIndicator().setRsPercentSum((float) -5);
		
		this.dmlQuotation3 = new Quotation();
		this.dmlQuotation3.setIndicator(new Indicator());
		this.dmlQuotation3.getIndicator().setRsPercentSum((float) 12.35);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the RS percentage sum.
	 */
	public void testGetRSPercentSum() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedRSPercentSum, actualRSPercentSum;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		
		//Actual price: 1,36
		//Three months ago price: 1,75
		//Six months ago price: 1,49
		//Nine months ago price: 2,30
		//Twelve months ago price: 1,30
		//Expected rsPercentSum = 3 month performance + 3 month perf. + 6 month perf. + 9 month perf + 12 month perf.
		//= -22,29% -22,29% -8,72% -40,87% +4,62% = -89,55%
		expectedRSPercentSum = (float) -89.55;
		
		actualRSPercentSum = indicatorCalculator.getRSPercentSum(sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedRSPercentSum, actualRSPercentSum);
	}
	
	
	@Test
	/**
	 * Tests the calculation of price performance since the given date.
	 */
	public void testGetRSPercentSinceDate() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedRSPercent, actualRSPercent;
		Calendar calendar = Calendar.getInstance();
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		
		//Actual price: 1,36
		//Price on 11.07.22: 1,30
		//Expected RSPercentSinceDate = 4,62%
		expectedRSPercent = (float) 4.62;
		calendar.set(2022, 6, 11);
		
		actualRSPercent = indicatorCalculator.getRSPercentSinceDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()), sortedQuotations);
		
		assertEquals(expectedRSPercent, actualRSPercent);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the RS percentage sum, if the quotation history is smaller than a year.
	 */
	public void testGetRSPercentSumOfIncompleteHistory() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedRSPercentSum, actualRSPercentSum;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		
		//Remove the last quote from the instrument. The 12 month performance can't be calculated then.
		sortedQuotations.getQuotations().remove(sortedQuotations.getQuotations().size() - 1);
		
		//Actual price: 1,36
		//Three months ago price: 1,75
		//Six months ago price: 1,49
		//Nine months ago price: 2,30
		//Twelve months ago price: unknown
		//Expected rsPercentSum = 3 month performance + 3 month perf. + 6 month perf. + 9 month perf.
		//= -22,29% -22,29% -8,72% -40,87% = -94,17%
		expectedRSPercentSum = (float) -94.17;
		
		actualRSPercentSum = indicatorCalculator.getRSPercentSum(sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedRSPercentSum, actualRSPercentSum);
	}
	
	
	@Test
	/**
	 * Tests the calculation of RS numbers.
	 */
	public void testCalculateRsNumbers() {
		List<Quotation> quotations = new ArrayList<>();
		
		//Prepare all quotations on which the RS number is to be calculated.
		quotations.add(this.dmlQuotation1);
		quotations.add(this.dmlQuotation2);
		quotations.add(this.dmlQuotation3);
		
		//Calculate the RS numbers.
		this.indicatorCalculator.calculateRsNumbers(quotations);
		
		//Verify the correct calculation.
		assertEquals(33, this.dmlQuotation2.getIndicator().getRsNumber());
		assertEquals(67, this.dmlQuotation3.getIndicator().getRsNumber());
		assertEquals(100, this.dmlQuotation1.getIndicator().getRsNumber());
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 50-day Simple Moving Average for the most recent Quotation.
	 */
	public void testGetSimpleMovingAverage50Days() {
		QuotationArray sortedQuotations = new QuotationArray();
		float actualSma50, expectedSma50 = (float) 1.42;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualSma50 = this.indicatorCalculator.getSimpleMovingAverage(50, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedSma50, actualSma50);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 50-day Simple Moving Average for a historical Quotation.
	 */
	public void testGetSimpleMovingAverage50DaysHistorical() {
		QuotationArray sortedQuotations = new QuotationArray();
		float actualSma50, expectedSma50 = (float) 1.41;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualSma50 = this.indicatorCalculator.getSimpleMovingAverage(50, sortedQuotations.getQuotations().get(2), sortedQuotations);
		
		assertEquals(expectedSma50, actualSma50);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 150-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage150Days() {
		QuotationArray sortedQuotations = new QuotationArray();
		float actualSma150, expectedSma150 = (float) 1.67;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualSma150 = this.indicatorCalculator.getSimpleMovingAverage(150, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedSma150, actualSma150);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 200-day Simple Moving Average.
	 */
	public void testGetSimpleMovingAverage200Days() {
		QuotationArray sortedQuotations = new QuotationArray();
		float actualSma200, expectedSma200 = (float) 1.79;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualSma200 = this.indicatorCalculator.getSimpleMovingAverage(200, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedSma200, actualSma200);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the distance to the 52 week high.
	 */
	public void testGetDistanceTo52WeekHigh() {
		QuotationArray sortedQuotations = new QuotationArray();
		float actualDistanceTo52WeekHigh, expectedDistanceTo52WeekHigh = (float) -48.09;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualDistanceTo52WeekHigh = this.indicatorCalculator.getDistanceTo52WeekHigh(sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedDistanceTo52WeekHigh, actualDistanceTo52WeekHigh);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the distance to the 52 week low.
	 */
	public void testGetDistanceTo52WeekLow() {
		QuotationArray sortedQuotations = new QuotationArray();
		float actualDistanceTo52WeekLow, expectedDistanceTo52WeekLow = (float) 10.57;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualDistanceTo52WeekLow = this.indicatorCalculator.getDistanceTo52WeekLow(sortedQuotations.getQuotations().get(0), sortedQuotations);
		
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
		QuotationArray sortedQuotations = new QuotationArray();
		float actualBollingerBandWidth, expectedBollingerBandWidth = (float) 24.7;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualBollingerBandWidth = this.indicatorCalculator.getBollingerBandWidth(10, 2, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedBollingerBandWidth, actualBollingerBandWidth);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the 10-day Simple Moving Average Volume.
	 */
	public void testGetSimpleMovingAverageVolume() {
		QuotationArray sortedQuotations = new QuotationArray();
		long expectedVolume = 1440500, actualVolume;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualVolume = this.indicatorCalculator.getSimpleMovingAverageVolume(10, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedVolume, actualVolume);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the volume differential between two moving averages of the volume.
	 */
	public void testGetVolumeDifferential() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedDifferential = (float) -17.58, actualDifferential;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualDifferential = this.indicatorCalculator.getVolumeDifferential(30, 10, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedDifferential, actualDifferential);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the volume differential if too few quotations exist for calculation of the moving average volume.
	 */
	public void testGetVolumeDifferentialWithTooFewQuotations() {
		QuotationArray sortedQuotations = new QuotationArray();
		QuotationArray reducedQuotationHistory = new QuotationArray();
		float expectedDifferential = 0, actualDifferential;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		
		//Reduce the number of quotations below the number needed for moving average volume creation.
		reducedQuotationHistory.getQuotations().add(sortedQuotations.getQuotations().get(0));
		reducedQuotationHistory.getQuotations().add(sortedQuotations.getQuotations().get(1));
		reducedQuotationHistory.getQuotations().add(sortedQuotations.getQuotations().get(2));
		
		actualDifferential = this.indicatorCalculator.getVolumeDifferential(30, 10, reducedQuotationHistory.getQuotations().get(0), 
				reducedQuotationHistory);
		
		assertEquals(expectedDifferential, actualDifferential);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the base length in weeks since the last 52 week high.
	 */
	public void testGetBaseLengthWeeks() {
		QuotationArray sortedQuotations = new QuotationArray();
		int expectedBaseLength = 35, actualBaseLength;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualBaseLength = this.indicatorCalculator.getBaseLengthWeeks(sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedBaseLength, actualBaseLength);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the Up/Down Volume ratio.
	 */
	public void testGetUpDownVolumeRatio() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedUdVolRatio = (float) 0.95, actualUdVolRatio;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualUdVolRatio = this.indicatorCalculator.getUpDownVolumeRatio(50, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedUdVolRatio, actualUdVolRatio);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the price performance of the given daily interval.
	 */
	public void testGetPricePerformanceForDays() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedPerformance = (float) 6.25, actualPerformance;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualPerformance = this.indicatorCalculator.getPricePerformanceForDays(20, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedPerformance, actualPerformance);
	}
	
	
	@Test
	/**
	 * Tests the calculation of the trading liquidity for the given daily interval.
	 */
	public void testGetLiquidityForDays() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedLiquidity = (float) 2036934, actualLiquidity;
		
		sortedQuotations.setQuotations(this.dmlStock.getQuotationsSortedByDate());
		actualLiquidity = this.indicatorCalculator.getLiquidityForDays(20, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedLiquidity, actualLiquidity);
	}
	
	@Test
	/**
	 * Tests the calculation of the trading liquidity for the given daily interval.
	 * 
	 * If the Instrument is traded at the LSE, the result has to be divided by 100,
	 * because all data providers use pence instead of pounds.
	 */
	public void testGetLiquidityForDaysLse() {
		QuotationArray sortedQuotations = new QuotationArray();
		float expectedLiquidity = (float) 162189066, actualLiquidity;
		
		sortedQuotations.setQuotations(this.rioStock.getQuotationsSortedByDate());
		actualLiquidity = this.indicatorCalculator.getLiquidityForDays(20, sortedQuotations.getQuotations().get(0), sortedQuotations);
		
		assertEquals(expectedLiquidity, actualLiquidity);
	}
}
