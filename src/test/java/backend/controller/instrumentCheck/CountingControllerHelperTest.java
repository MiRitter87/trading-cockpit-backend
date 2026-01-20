package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.IndicatorCalculationController;
import backend.dao.quotation.provider.QuotationProviderYahooDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Tests the CountingControllerHelper.
 *
 * @author Michael
 */
public class CountingControllerHelperTest {
    /**
     * DAO to access quotation data from Yahoo.
     */
    private static QuotationProviderYahooDAO quotationProviderYahooDAO;

    /**
     * A list of quotations of the DML stock.
     */
    private QuotationArray dmlQuotations;

    /**
     * The controller with helper methods for counting related Instrument checks.
     */
    private CountingControllerHelper countingControllerHelper;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
    }

    /**
     * Tasks to be performed once at end of test class.
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
        this.countingControllerHelper = new CountingControllerHelper();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.countingControllerHelper = null;
        this.dmlQuotations = null;
    }

    /**
     * Initializes quotations of the DML stock.
     */
    private void initializeDMLQuotations() {
        Instrument dmlStock = new Instrument();

        dmlStock.setSymbol("DML");
        dmlStock.setStockExchange(StockExchange.TSX);
        dmlStock.setType(InstrumentType.STOCK);

        try {
            this.dmlQuotations = new QuotationArray();
            this.dmlQuotations.setQuotations(quotationProviderYahooDAO.getQuotationHistory(dmlStock, 1));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the indicators of the DML stock.
     */
    private void initializeDMLIndicators() {
        IndicatorCalculationController indicatorCalculator = new IndicatorCalculationController();
        List<Quotation> sortedQuotations;
        Instrument instrument = new Instrument();
        Quotation quotation;

        instrument.setQuotations(this.dmlQuotations.getQuotations());
        sortedQuotations = instrument.getQuotationsSortedByDate();

        for (int i = 0; i < sortedQuotations.size(); i++) {
            quotation = sortedQuotations.get(i);

            // Calculate all Indicators only for most recent Quotation like in the ScanThread.
            if (i == 0) {
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
            } else {
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, false);
            }
        }
    }

    /**
     * Tests getting the number of good and bad closes in a range of the trading history.
     */
    @Test
    public void testGetNumberOfGoodAndBadCloses() {
        final int expectedNumberOfGoodCloses = 3;
        final int expectedNumberOfBadCloses = 3;
        final int expectedDaysTotal = 6;
        final int startIndex = 5;
        int actualNumberOfGoodCloses;
        int actualNumberOfBadCloses;
        int actualDaysTotal;
        Map<String, Integer> resultMap;

        this.dmlQuotations.sortQuotationsByDate();

        resultMap = this.countingControllerHelper.getNumberOfGoodAndBadCloses(
                this.dmlQuotations.getQuotations().get(startIndex), this.dmlQuotations.getQuotations().get(0),
                this.dmlQuotations);
        assertNotNull(resultMap);

        actualNumberOfGoodCloses = resultMap.get(CountingControllerHelper.MAP_ENTRY_GOOD_CLOSES);
        actualNumberOfBadCloses = resultMap.get(CountingControllerHelper.MAP_ENTRY_BAD_CLOSES);
        actualDaysTotal = resultMap.get(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL);

        assertEquals(expectedNumberOfGoodCloses, actualNumberOfGoodCloses);
        assertEquals(expectedNumberOfBadCloses, actualNumberOfBadCloses);
        assertEquals(expectedDaysTotal, actualDaysTotal);
    }

    /**
     * Tests getting the number of up- and down-days in a range of the trading history.
     */
    @Test
    public void testGetNumberOfUpAndDownDays() {
        final int expectedNumberOfUpDays = 1;
        final int expectedNumberOfDownDays = 2;
        final int expectedDaysTotal = 3;
        int actualNumberOfUpDays;
        int actualNumberOfDownDays;
        int actualDaysTotal;
        Map<String, Integer> resultMap;

        this.dmlQuotations.sortQuotationsByDate();

        resultMap = this.countingControllerHelper.getNumberOfUpAndDownDays(this.dmlQuotations.getQuotations().get(2),
                this.dmlQuotations.getQuotations().get(0), this.dmlQuotations);
        assertNotNull(resultMap);

        actualNumberOfUpDays = resultMap.get(CountingControllerHelper.MAP_ENTRY_UP_DAYS);
        actualNumberOfDownDays = resultMap.get(CountingControllerHelper.MAP_ENTRY_DOWN_DAYS);
        actualDaysTotal = resultMap.get(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL);

        assertEquals(expectedNumberOfUpDays, actualNumberOfUpDays);
        assertEquals(expectedNumberOfDownDays, actualNumberOfDownDays);
        assertEquals(expectedDaysTotal, actualDaysTotal);
    }
}
