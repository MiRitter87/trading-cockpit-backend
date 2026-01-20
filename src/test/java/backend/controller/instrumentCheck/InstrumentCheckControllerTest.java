package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.NoQuotationsExistException;
import backend.controller.scan.IndicatorCalculationController;
import backend.dao.quotation.provider.QuotationProviderYahooDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.tools.DateTools;

/**
 * Tests the InstrumentCheckController.
 *
 * @author Michael
 */
public class InstrumentCheckControllerTest {
    /**
     * DAO to access quotation data from Yahoo.
     */
    private static QuotationProviderYahooDAO quotationProviderYahooDAO;

    /**
     * A list of quotations of the DML stock.
     */
    private QuotationArray dmlQuotations;

    /**
     * The controller for Instrument checks.
     */
    private InstrumentCheckController instrumentCheckController;

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
        this.instrumentCheckController = new InstrumentCheckController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.instrumentCheckController = null;
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
     * Checks if a proper Exception is thrown if no quotations exist at or after the given start date.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testCheckQuotationsExistAfterStartDate() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2022, 6, 23); // 23.07.22 (The last Quotation is for the 22.07.22)

        try {
            this.instrumentCheckController.checkQuotationsExistAfterStartDate(calendar.getTime(), this.dmlQuotations);
            fail("The check should have failed because there is no Quotation at or after the given date.");
        } catch (NoQuotationsExistException expected) {
            // All is well.
        }
    }

    /**
     * Checks if the start date is properly determined based on a lookback period and a list of quotations.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetStartDate() {
        Calendar calendar = Calendar.getInstance();
        final int lookbackPeriod = 15;
        final Date expectedStartDate;
        final Date actualStartDate;

        calendar.set(2022, 6, 4); // 04.07.22
        expectedStartDate = DateTools.getDateWithoutIntradayAttributes(calendar.getTime());

        actualStartDate = this.instrumentCheckController.getStartDate(lookbackPeriod, this.dmlQuotations);
        assertEquals(expectedStartDate, actualStartDate);
    }

    /**
     * Tests the determination of the start date if the lookback period is longer than the actual number of quotations.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetStartDateTooFewQuotations() {
        Calendar calendar = Calendar.getInstance();
        final int lookbackPeriod = 15;
        final int toIndex = 10;
        final Date expectedStartDate;
        final Date actualStartDate;

        // Delete all quotations except the newest 10.
        this.dmlQuotations.setQuotations(this.dmlQuotations.getQuotations().subList(0, toIndex));

        // A lookback period of 15 would provide 04.07.22 if enough quotations were available.
        calendar.set(2022, 6, 11); // 11.07.22, because only 10 quotations are available.
        expectedStartDate = DateTools.getDateWithoutIntradayAttributes(calendar.getTime());

        actualStartDate = this.instrumentCheckController.getStartDate(lookbackPeriod, this.dmlQuotations);
        assertEquals(expectedStartDate, actualStartDate);
    }
}
