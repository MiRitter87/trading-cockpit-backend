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
 * Tests the StochasticCalculator.
 *
 * @author Michael
 */
public class StochasticCalculatorTest {
    /**
     * The StochasticCalculator under test.
     */
    private StochasticCalculator stochasticCalculator;

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
            this.stochasticCalculator = new StochasticCalculator();
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
        this.stochasticCalculator = null;

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
     * Tests the calculation of the Stochastic.
     */
    @Test
    public void testGetStochastic() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        final float expectedStochastic = 48.57f;
        float actualStochastic;
        final int days14 = 14;

        actualStochastic = this.stochasticCalculator.getStochastic(days14, sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedStochastic, actualStochastic);
    }

    /**
     * Tests the calculation of the Slow Stochastic.
     */
    @Test
    public void testGetSlowStochastic() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        final float expectedSlowStochastic = 74.28f;
        float actualSlowStochastic;
        final int periodDays = 14;
        final int smoothingPeriod = 3;

        actualSlowStochastic = this.stochasticCalculator.getSlowStochastic(periodDays, smoothingPeriod,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSlowStochastic, actualSlowStochastic);
    }
}
