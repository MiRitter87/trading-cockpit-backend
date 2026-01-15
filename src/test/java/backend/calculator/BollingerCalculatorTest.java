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
 * Tests the BollingerCalculator.
 *
 * @author Michael
 */
public class BollingerCalculatorTest {
    /**
     * The BollingerCalculator under test.
     */
    private BollingerCalculator bollingerCalculator;

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
            this.bollingerCalculator = new BollingerCalculator();
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
        this.bollingerCalculator = null;

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
     * Tests the calculation of the standard deviation.
     */
    @Test
    public void testGetStandardDeviation() {
        @SuppressWarnings("checkstyle:magicnumber")
        float[] inputValues = {46, 69, 32, 60, 52, 41};
        final float expectedStandardDeviation = 12.1518f;
        float actualStandardDeviation;

        actualStandardDeviation = this.bollingerCalculator.getStandardDeviation(inputValues);

        assertEquals(expectedStandardDeviation, actualStandardDeviation);
    }

    /**
     * Tests the calculation of the Bollinger BandWidth.
     */
    @Test
    public void testGetBollingerBandWidth() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualBollingerBandWidth;
        final float expectedBollingerBandWidth = 24.75f;
        final int days10 = 10;

        actualBollingerBandWidth = this.bollingerCalculator.getBollingerBandWidth(days10, 2,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedBollingerBandWidth, actualBollingerBandWidth);
    }

    /**
     * Tests the calculation of the Bollinger BandWidth threshold.
     */
    @Test
    public void testGetBollingerBandWidthThreshold() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        final float expectedThreshold = 13.86f;
        float actualThreshold;
        final int days10 = 10;
        final int threshold = 20;

        actualThreshold = this.bollingerCalculator.getBollingerBandWidthThreshold(days10, 2, threshold,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedThreshold, actualThreshold);
    }

    /**
     * Tests the calculation of the Bollinger BandWidth threshold of an instrument that only has a short trading
     * history.
     */
    @Test
    public void testBBWThresholdShortHistory() {
        final int days10 = 10;
        final int threshold = 20;

        // Reduce trading history to ten days.
        QuotationArray sortedQuotations = new QuotationArray(
                this.dmlStock.getQuotationsSortedByDate().subList(0, days10));

        final float expectedThreshold = 24.75f;
        float actualThreshold;

        actualThreshold = this.bollingerCalculator.getBollingerBandWidthThreshold(days10, 2, threshold,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedThreshold, actualThreshold);
    }
}
