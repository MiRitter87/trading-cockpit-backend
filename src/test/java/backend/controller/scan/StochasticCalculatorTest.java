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
            this.stochasticCalculator = new StochasticCalculator();
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

    @Test
    /**
     * Tests the calculation of the Stochastic.
     */
    public void testGetStochastic() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedStochastic = 48.57f, actualStochastic;

        actualStochastic = this.stochasticCalculator.getStochastic(14, sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedStochastic, actualStochastic);
    }

    @Test
    /**
     * Tests the calculation of the Slow Stochastic.
     */
    public void testGetSlowStochastic() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedSlowStochastic = 74.28f, actualSlowStochastic;
        final int periodDays = 14;
        final int smoothingPeriod = 3;

        actualSlowStochastic = this.stochasticCalculator.getSlowStochastic(periodDays, smoothingPeriod,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedSlowStochastic, actualSlowStochastic);
    }
}
