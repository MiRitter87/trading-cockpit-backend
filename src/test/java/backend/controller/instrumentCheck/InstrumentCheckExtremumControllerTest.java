package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

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
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Tests the InstrumentCheckExtremumController.
 *
 * @author Michael
 */
public class InstrumentCheckExtremumControllerTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

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
    private InstrumentCheckExtremumController instrumentCheckExtremumController;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
    }

    @AfterAll
    /**
     * Tasks to be performed once at end of test class.
     */
    public static void tearDownClass() {
        quotationProviderYahooDAO = null;
    }

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        this.instrumentCheckExtremumController = new InstrumentCheckExtremumController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.instrumentCheckExtremumController = null;
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
            this.addDummyQuotation();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Adds a dummy quotation to the list of quotations. The goal is to have a quotation history that spans more than a
     * year.
     */
    private void addDummyQuotation() {
        Quotation newQuotation;
        Calendar calendar = Calendar.getInstance();

        newQuotation = this.dmlQuotations.getQuotations().get(this.dmlQuotations.getQuotations().size() - 1);
        calendar.setTime(newQuotation.getDate());
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        newQuotation.setDate(calendar.getTime());

        this.dmlQuotations.getQuotations().add(newQuotation);
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
            if (i == 0)
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
            else
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, false);
        }
    }

    @Test
    /**
     * Tests the check if Instrument had largest down-day of the year.
     */
    public void testCheckLargestDownDay() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 4, 9); // Largest down day is 09.05.22 (-12,34%)
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry
                .setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"), "-12,34"));

        // Call controller to perform check.
        calendar.set(2022, 4, 4); // Begin check on 04.05.22
        try {
            protocolEntries = this.instrumentCheckExtremumController.checkLargestDownDay(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(1, protocolEntries.size());

            // Validate the protocol entry.
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument had largest up-day of the year.
     */
    public void testCheckLargestUpDay() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2021, 9, 12); // Largest up day is 12.10.21 (19,41%)
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestUpDay"), "19,41"));

        // Call controller to perform check.
        calendar.set(2021, 9, 1); // Begin check on 01.10.21
        try {
            protocolEntries = this.instrumentCheckExtremumController.checkLargestUpDay(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(1, protocolEntries.size());

            // Validate the protocol entry.
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument had largest daily high/low-spread of the year.
     */
    public void testCheckLargestDailySpread() {
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();
        ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        float expectedPercentage1 = 15.08f;
        float expectedPercentage2 = 17.34f;

        // Define the expected protocol entries.
        calendar.set(2021, 8, 10); // Largest daily high/low-spread occurred on 10.09.21
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry1.setText(
                MessageFormat.format(this.resources.getString("protocol.largestDailySpread"), expectedPercentage1));

        calendar.set(2021, 9, 12); // A new largest daily high/low-spread occurred on 12.10.21.
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry2.setText(
                MessageFormat.format(this.resources.getString("protocol.largestDailySpread"), expectedPercentage2));

        // Call controller to perform check.
        calendar.set(2021, 8, 6); // Begin check on 06.09.21.
        try {
            protocolEntries = this.instrumentCheckExtremumController.checkLargestDailySpread(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(2, protocolEntries.size());

            // Validate the protocol entries.
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry1, actualProtocolEntry);

            actualProtocolEntry = protocolEntries.get(1);
            assertEquals(expectedProtocolEntry2, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument had largest daily volume of the year.
     */
    public void testCheckLargestDailyVolume() {
        ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entries.
        calendar.set(2022, 0, 5); // Largest daily volume occurred on 05.01.22.
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry1.setText(this.resources.getString("protocol.largestDailyVolume"));

        calendar.set(2022, 1, 4); // A new largest daily volume occurred on 04.02.22.
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry2.setText(this.resources.getString("protocol.largestDailyVolume"));

        // Call controller to perform check.
        calendar.set(2022, 0, 1); // Begin check on 01.01.22.
        try {
            protocolEntries = this.instrumentCheckExtremumController.checkLargestDailyVolume(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(2, protocolEntries.size());

            // Validate the protocol entries.
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry1, actualProtocolEntry);

            actualProtocolEntry = protocolEntries.get(1);
            assertEquals(expectedProtocolEntry2, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument had largest down-day of the year. Set starting date before begin of trading
     * history.
     */
    public void testCheckLargestDownDayStartBeforeHistory() {
        ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry3 = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entries.
        calendar.set(2022, 6, 12); // Largest down day is 12.07.22 (-3,08%)
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry1
                .setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"), "-3,08"));

        calendar.set(2022, 6, 21); // Largest down day is 21.07.22 (-4,58%)
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry2
                .setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"), "-4,58"));

        calendar.set(2022, 6, 22); // Largest down day is 22.07.22 (-6,85%)
        expectedProtocolEntry3.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry3.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry3
                .setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"), "-6,85"));

        // Shrink size of quotation history simplifying start before history began.
        this.dmlQuotations.setQuotations(this.dmlQuotations.getQuotations().subList(0, 10));

        // Call controller to perform check.
        calendar.set(2022, 6, 10); // Begin check on 10.07.22
        try {
            protocolEntries = this.instrumentCheckExtremumController.checkLargestDownDay(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(3, protocolEntries.size());

            // Validate the protocol entries.
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry1, actualProtocolEntry);
            actualProtocolEntry = protocolEntries.get(1);
            assertEquals(expectedProtocolEntry2, actualProtocolEntry);
            actualProtocolEntry = protocolEntries.get(2);
            assertEquals(expectedProtocolEntry3, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument had largest up-day of the year. Set starting date before begin of trading history.
     */
    public void testCheckLargestUpDayStartBeforeHistory() {
        ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 13); // Largest up day is 13.07.22 (0,79%)
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry1.setText(MessageFormat.format(this.resources.getString("protocol.largestUpDay"), "0,79"));

        calendar.set(2022, 6, 14); // Largest up day is 14.07.22 (7,87%)
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry2.setText(MessageFormat.format(this.resources.getString("protocol.largestUpDay"), "7,87"));

        // Shrink size of quotation history simplifying start before history began.
        this.dmlQuotations.setQuotations(this.dmlQuotations.getQuotations().subList(0, 10));

        // Call controller to perform check.
        calendar.set(2022, 6, 10); // Begin check on 10.07.22
        try {
            protocolEntries = this.instrumentCheckExtremumController.checkLargestUpDay(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(2, protocolEntries.size());

            // Validate the protocol entries.
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry1, actualProtocolEntry);
            actualProtocolEntry = protocolEntries.get(1);
            assertEquals(expectedProtocolEntry2, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
