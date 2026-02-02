package backend.calculator;

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

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        try {
            quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed once at the end of the test class.
     */
    @AfterAll
    public static void tearDownClass() {
        quotationProviderYahooDAO = null;
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        try {
            this.movingAverageCalculator = new MovingAverageCalculator();
            this.initializeDmlInstrument();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
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
            quotations.addAll(quotationProviderYahooDAO.getQuotationHistory(this.dmlStock, 1));
            this.dmlStock.setQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the calculation of the 10-day Simple Moving Average for the most recent Quotation.
     */
    @Test
    public void testGetSimpleMovingAverage10Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualSma10;
        final float expectedSma10 = 1.377f;
        final int days10 = 10;

        actualSma10 = this.movingAverageCalculator.getSimpleMovingAverage(days10,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSma10, actualSma10);
    }

    /**
     * Tests the calculation of the 20-day Simple Moving Average for the most recent Quotation.
     */
    @Test
    public void testGetSimpleMovingAverage20Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualSma20;
        final float expectedSma20 = 1.349f;
        final int days20 = 20;

        actualSma20 = this.movingAverageCalculator.getSimpleMovingAverage(days20,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSma20, actualSma20);
    }

    /**
     * Tests the calculation of the 50-day Simple Moving Average for the most recent Quotation.
     */
    @Test
    public void testGetSimpleMovingAverage50Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualSma50;
        final float expectedSma50 = 1.416f;
        final int days50 = 50;

        actualSma50 = this.movingAverageCalculator.getSimpleMovingAverage(days50,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSma50, actualSma50);
    }

    /**
     * Tests the calculation of the 50-day Simple Moving Average for a historical Quotation.
     */
    @Test
    public void testGetSimpleMovingAverage50DaysHistorical() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualSma50;
        final float expectedSma50 = 1.414f;
        final int days50 = 50;

        actualSma50 = this.movingAverageCalculator.getSimpleMovingAverage(days50,
                sortedQuotations.getQuotations().get(2), sortedQuotations);

        assertEquals(expectedSma50, actualSma50);
    }

    /**
     * Tests the calculation of the 150-day Simple Moving Average.
     */
    @Test
    public void testGetSimpleMovingAverage150Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualSma150;
        final float expectedSma150 = 1.673f;
        final int days150 = 150;

        actualSma150 = this.movingAverageCalculator.getSimpleMovingAverage(days150,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSma150, actualSma150);
    }

    /**
     * Tests the calculation of the 200-day Simple Moving Average.
     */
    @Test
    public void testGetSimpleMovingAverage200Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualSma200;
        final float expectedSma200 = 1.788f;
        final int days200 = 200;

        actualSma200 = this.movingAverageCalculator.getSimpleMovingAverage(days200,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSma200, actualSma200);
    }

    /**
     * Tests the calculation of the 10-day Exponential Moving Average for the most recent Quotation.
     */
    @Test
    public void testGetExponentialMovingAverage10Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualEma10;
        final float expectedEma10 = 1.395f;
        final int days10 = 10;

        actualEma10 = this.movingAverageCalculator.getExponentialMovingAverage(days10,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedEma10, actualEma10);
    }

    /**
     * Tests the calculation of the 21-day Exponential Moving Average for the most recent Quotation.
     */
    @Test
    public void testGetExponentialMovingAverage21Days() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualEma21;
        final float expectedEma21 = 1.385f;
        final int days21 = 21;

        actualEma21 = this.movingAverageCalculator.getExponentialMovingAverage(days21,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedEma21, actualEma21);
    }

    /**
     * Tests the calculation of the 10-day Simple Moving Average Volume.
     */
    @Test
    public void testGetSimpleMovingAverageVolume() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        final long expectedVolume = 1440500;
        long actualVolume;
        final int days10 = 10;

        actualVolume = this.movingAverageCalculator.getSimpleMovingAverageVolume(days10,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedVolume, actualVolume);
    }

    /**
     * Tests the calculation of the Simple Moving Average if not enough quotations exist.
     */
    @Test
    public void testGetSimpleMovingAverageTooFewQuotations() {
        QuotationArray sortedQuotations;
        final float expectedSma50 = 0;
        float actualSma50;
        final int days50 = 50;

        // Remove all quotations except of 49.
        sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate().subList(0, days50 - 1));

        actualSma50 = this.movingAverageCalculator.getSimpleMovingAverage(days50,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSma50, actualSma50);
    }

    /**
     * Tests the calculation of the Exponential Moving Average if not enough quotations exist.
     */
    @Test
    public void testGetExponentialMovingAverageTooFewQuotations() {
        QuotationArray sortedQuotations;
        final float expectedEma10 = 0;
        float actualEma10;
        final int days10 = 10;
        final int numRemainingQuotations = 19;

        // Remove all quotations except of 19.
        sortedQuotations = new QuotationArray(
                this.dmlStock.getQuotationsSortedByDate().subList(0, numRemainingQuotations));

        actualEma10 = this.movingAverageCalculator.getExponentialMovingAverage(days10,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedEma10, actualEma10);
    }

    /**
     * Tests the calculation of the Simple Moving Average Volume if not enough quotations exist.
     */
    @Test
    public void testGetSMAVolumeTooFewQuotations() {
        QuotationArray sortedQuotations;
        final long expectedVolume = 0;
        long actualVolume;
        final int days10 = 10;

        // Remove all quotations except of 9.
        sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate().subList(0, days10 - 1));

        actualVolume = this.movingAverageCalculator.getSimpleMovingAverageVolume(days10,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedVolume, actualVolume);
    }
}
