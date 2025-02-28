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
 * Tests the IndicatorCalculator.
 *
 * @author Michael
 */
public class IndicatorCalculatorTest {
    /**
     * The IndicatorCalculator under test.
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
            quotations.addAll(quotationProviderYahooDAO.getQuotationHistory(this.dmlStock, 1));
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
            quotations.addAll(quotationProviderYahooDAO.getQuotationHistory(this.rioStock, 1));
            this.rioStock.setQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the calculation of the distance to the 52 week high.
     */
    public void testGetDistanceTo52WeekHigh() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualDistanceTo52WeekHigh, expectedDistanceTo52WeekHigh = (float) -48.09;

        actualDistanceTo52WeekHigh = this.indicatorCalculator
                .getDistanceTo52WeekHigh(sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedDistanceTo52WeekHigh, actualDistanceTo52WeekHigh);
    }

    @Test
    /**
     * Tests the calculation of the distance to the 52 week low.
     */
    public void testGetDistanceTo52WeekLow() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualDistanceTo52WeekLow, expectedDistanceTo52WeekLow = (float) 10.57;

        actualDistanceTo52WeekLow = this.indicatorCalculator
                .getDistanceTo52WeekLow(sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedDistanceTo52WeekLow, actualDistanceTo52WeekLow);
    }

    @Test
    /**
     * Tests the calculation of the volume differential between two moving averages of the volume.
     */
    public void testGetVolumeDifferential() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedDifferential = (float) -17.58, actualDifferential;

        actualDifferential = this.indicatorCalculator.getVolumeDifferential(30, 10,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedDifferential, actualDifferential);
    }

    @Test
    /**
     * Tests the calculation of the volume differential if too few quotations exist for calculation of the moving
     * average volume.
     */
    public void testGetVolumeDifferentialWithTooFewQuotations() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        QuotationArray reducedQuotationHistory = new QuotationArray();
        float expectedDifferential = 0, actualDifferential;

        // Reduce the number of quotations below the number needed for moving average volume creation.
        reducedQuotationHistory.getQuotations().add(sortedQuotations.getQuotations().get(0));
        reducedQuotationHistory.getQuotations().add(sortedQuotations.getQuotations().get(1));
        reducedQuotationHistory.getQuotations().add(sortedQuotations.getQuotations().get(2));

        actualDifferential = this.indicatorCalculator.getVolumeDifferential(30, 10,
                reducedQuotationHistory.getQuotations().get(0), reducedQuotationHistory);

        assertEquals(expectedDifferential, actualDifferential);
    }

    @Test
    /**
     * Tests the calculation of the base length in weeks since the last 52 week high.
     */
    public void testGetBaseLengthWeeks() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        int expectedBaseLength = 35, actualBaseLength;

        actualBaseLength = this.indicatorCalculator.getBaseLengthWeeks(sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedBaseLength, actualBaseLength);
    }

    @Test
    /**
     * Tests the calculation of the Up/Down Volume ratio.
     */
    public void testGetUpDownVolumeRatio() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedUdVolRatio = (float) 0.95, actualUdVolRatio;

        actualUdVolRatio = this.indicatorCalculator.getUpDownVolumeRatio(50, sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedUdVolRatio, actualUdVolRatio);
    }

    @Test
    /**
     * Tests the calculation of the Up/Down performance * volume ratio.
     */
    public void testGetUpDownPerformanceVolumeRatio() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedUdPerfVolRatio = (float) 1.04;
        float actualUdPerfVolRatio;

        actualUdPerfVolRatio = this.indicatorCalculator.getUpDownPerformanceVolumeRatio(5,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedUdPerfVolRatio, actualUdPerfVolRatio);
    }

    @Test
    /**
     * Tests the calculation of the trading liquidity for the given daily interval.
     */
    public void testGetLiquidityForDays() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedLiquidity = (float) 2035425.1, actualLiquidity;

        actualLiquidity = this.indicatorCalculator.getLiquidityForDays(20, sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedLiquidity, actualLiquidity);
    }

    @Test
    /**
     * Tests the calculation of the trading liquidity for the given daily interval.
     *
     * If the Instrument is traded at the LSE, the result has to be divided by 100, because all data providers use pence
     * instead of pounds.
     */
    public void testGetLiquidityForDaysLse() {
        QuotationArray sortedQuotations = new QuotationArray(this.rioStock.getQuotationsSortedByDate());
        float expectedLiquidity = 162189066, actualLiquidity;

        actualLiquidity = this.indicatorCalculator.getLiquidityForDays(20, sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedLiquidity, actualLiquidity);
    }
}
