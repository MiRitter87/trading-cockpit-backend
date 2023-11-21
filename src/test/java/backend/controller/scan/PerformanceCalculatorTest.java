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
 * Tests the PerformanceCalculator.
 *
 * @author Michael
 */
public class PerformanceCalculatorTest {
    /**
     * The PerformanceCalculator under test.
     */
    private PerformanceCalculator performanceCalculator;

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
            this.performanceCalculator = new PerformanceCalculator();
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
        this.performanceCalculator = null;

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
            quotations.addAll(
                    quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));
            this.dmlStock.setQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the calculation of the price performance of the given daily interval.
     */
    public void testGetPricePerformanceForDays() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedPerformance = (float) 6.25, actualPerformance;

        actualPerformance = this.performanceCalculator.getPricePerformanceForDays(20,
                sortedQuotations.getQuotations().get(0), sortedQuotations);

        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    /**
     * Tests the calculation of the price performance.
     */
    public void testGetPerformance() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedPerformance = (float) -6.85, actualPerformance;

        actualPerformance = this.performanceCalculator.getPerformance(sortedQuotations.getQuotations().get(0),
                sortedQuotations.getQuotations().get(1));

        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    /**
     * Tests the calculation of the RS percentage sum.
     */
    public void testGetRSPercentSum() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedRSPercentSum, actualRSPercentSum;

        // Actual price: 1,36
        // Three months ago price: 1,75
        // Six months ago price: 1,49
        // Nine months ago price: 2,30
        // Twelve months ago price: 1,30
        // Expected rsPercentSum = 3 month performance + 3 month perf. + 6 month perf. + 9 month perf + 12 month perf.
        // = -22,29% -22,29% -8,72% -40,87% +4,62% = -89,55%
        expectedRSPercentSum = (float) -89.55;

        actualRSPercentSum = this.performanceCalculator.getRSPercentSum(sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedRSPercentSum, actualRSPercentSum);
    }

    @Test
    /**
     * Tests the calculation of the RS percentage sum, if the quotation history is smaller than a year.
     */
    public void testGetRSPercentSumOfIncompleteHistory() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedRSPercentSum, actualRSPercentSum;

        // Remove the last quote from the instrument. The 12 month performance can't be calculated then.
        sortedQuotations.getQuotations().remove(sortedQuotations.getQuotations().size() - 1);

        // Actual price: 1,36
        // Three months ago price: 1,75
        // Six months ago price: 1,49
        // Nine months ago price: 2,30
        // Twelve months ago price: unknown
        // Expected rsPercentSum = 3 month performance + 3 month perf. + 6 month perf. + 9 month perf.
        // = -22,29% -22,29% -8,72% -40,87% = -94,17%
        expectedRSPercentSum = (float) -94.17;

        actualRSPercentSum = this.performanceCalculator.getRSPercentSum(sortedQuotations.getQuotations().get(0),
                sortedQuotations);

        assertEquals(expectedRSPercentSum, actualRSPercentSum);
    }

    @Test
    /**
     * Tests the calculation of the average performance on up-days.
     */
    public void testGetAveragePerformanceOfUpDays() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedPerformance = (float) 4.15;
        float actualPerformance;

        actualPerformance = this.performanceCalculator
                .getAveragePerformanceOfUpDays(sortedQuotations.getQuotations().get(0), sortedQuotations, 5, 10);

        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    /**
     * Tests the calculation of the average performance on down-days.
     */
    public void testGetAveragePerformanceOfDownDays() {
        QuotationArray sortedQuotations = new QuotationArray(this.dmlStock.getQuotationsSortedByDate());
        float expectedPerformance = (float) -3.79;
        float actualPerformance;

        actualPerformance = this.performanceCalculator
                .getAveragePerformanceOfDownDays(sortedQuotations.getQuotations().get(0), sortedQuotations, 5, 10);

        assertEquals(expectedPerformance, actualPerformance);
    }
}
