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
    public void setUp() {
        try {
            this.bollingerCalculator = new BollingerCalculator();
            this.initializeDmlInstrument();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
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

    @Test
    /**
     * Tests the calculation of the standard deviation.
     */
    public void testGetStandardDeviation() {
        float[] inputValues = { 46, 69, 32, 60, 52, 41 };
        float expectedStandardDeviation = (float) 12.1518;
        float actualStandardDeviation;

        actualStandardDeviation = this.bollingerCalculator.getStandardDeviation(inputValues);

        assertEquals(expectedStandardDeviation, actualStandardDeviation);
    }

    @Test
    /**
     * Tests the calculation of the Bollinger BandWidth.
     */
    public void testGetBollingerBandWidth() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float actualBollingerBandWidth, expectedBollingerBandWidth = (float) 24.75;

        actualBollingerBandWidth = this.bollingerCalculator.getBollingerBandWidth(10, 2,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedBollingerBandWidth, actualBollingerBandWidth);
    }

    @Test
    /**
     * Tests the calculation of the Bollinger BandWidth threshold.
     */
    public void testGetBollingerBandWidthThreshold() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedThreshold = (float) 13.86;
        float actualThreshold;

        actualThreshold = this.bollingerCalculator.getBollingerBandWidthThreshold(10, 2, 20,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    /**
     * Tests the calculation of the Bollinger BandWidth threshold of an instrument that only has a short trading
     * history.
     */
    public void testBBWThresholdShortHistory() {
        // Reduce trading history to ten days.
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate().subList(0, 10));

        float expectedThreshold = 24.75f;
        float actualThreshold;

        actualThreshold = this.bollingerCalculator.getBollingerBandWidthThreshold(10, 2, 20,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedThreshold, actualThreshold);
    }
}
