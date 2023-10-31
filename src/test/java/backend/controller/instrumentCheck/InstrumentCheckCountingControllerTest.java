package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.IndicatorCalculator;
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
 * Tets the InstrumentCheckCountingController.
 *
 * @author Michael
 */
public class InstrumentCheckCountingControllerTest {
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
     * The controller for counting related Instrument checks.
     */
    private InstrumentCheckCountingController instrumentCheckCountingController;

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
    private void setUp() {
        this.instrumentCheckCountingController = new InstrumentCheckCountingController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.instrumentCheckCountingController = null;
        this.dmlQuotations = null;
    }

    /**
     * Initializes quotations of the DML stock.
     */
    private void initializeDMLQuotations() {
        try {
            this.dmlQuotations = new QuotationArray();
            this.dmlQuotations.setQuotations(
                    quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the indicators of the DML stock.
     */
    private void initializeDMLIndicators() {
        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
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
     * Tests getting the number of good and bad closes in a range of the trading history.
     */
    public void testGetNumberOfGoodAndBadCloses() {
        int expectedNumberOfGoodCloses, actualNumberOfGoodCloses, expectedNumberOfBadCloses, actualNumberOfBadCloses;
        int expectedDaysTotal, actualDaysTotal;
        Map<String, Integer> resultMap;

        this.dmlQuotations.sortQuotationsByDate();

        expectedNumberOfGoodCloses = 3;
        expectedNumberOfBadCloses = 3;
        expectedDaysTotal = 6;

        resultMap = this.instrumentCheckCountingController.getNumberOfGoodAndBadCloses(
                this.dmlQuotations.getQuotations().get(5), this.dmlQuotations.getQuotations().get(0),
                this.dmlQuotations);
        assertNotNull(resultMap);

        actualNumberOfGoodCloses = resultMap.get(InstrumentCheckCountingController.MAP_ENTRY_GOOD_CLOSES);
        actualNumberOfBadCloses = resultMap.get(InstrumentCheckCountingController.MAP_ENTRY_BAD_CLOSES);
        actualDaysTotal = resultMap.get(InstrumentCheckCountingController.MAP_ENTRY_DAYS_TOTAL);

        assertEquals(expectedNumberOfGoodCloses, actualNumberOfGoodCloses);
        assertEquals(expectedNumberOfBadCloses, actualNumberOfBadCloses);
        assertEquals(expectedDaysTotal, actualDaysTotal);
    }

    @Test
    /**
     * Tests the check if there are more bad closes than good closes.
     */
    public void testCheckMoreBadThanGoodCloses() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 22); // The first day on which the number of bad closes exceeds the number of good closes.
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry
                .setText(MessageFormat.format(this.resources.getString("protocol.moreBadCloses"), "3", "5"));

        // Call controller to perform check.
        calendar.set(2022, 6, 18); // Begin check on 18.07.22 (3 of 5 days have bad closes from there on)
        try {
            protocolEntries = this.instrumentCheckCountingController.checkMoreBadThanGoodCloses(calendar.getTime(),
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
     * Tests getting the number of up- and down-days in a range of the trading history.
     */
    public void testGetNumberOfUpAndDownDays() {
        int expectedNumberOfUpDays, actualNumberOfUpDays, expectedNumberOfDownDays, actualNumberOfDownDays,
                expectedDaysTotal, actualDaysTotal;
        Map<String, Integer> resultMap;

        this.dmlQuotations.sortQuotationsByDate();

        expectedNumberOfUpDays = 1;
        expectedNumberOfDownDays = 2;
        expectedDaysTotal = 3;

        resultMap = this.instrumentCheckCountingController.getNumberOfUpAndDownDays(
                this.dmlQuotations.getQuotations().get(2), this.dmlQuotations.getQuotations().get(0),
                this.dmlQuotations);
        assertNotNull(resultMap);

        actualNumberOfUpDays = resultMap.get(InstrumentCheckCountingController.MAP_ENTRY_UP_DAYS);
        actualNumberOfDownDays = resultMap.get(InstrumentCheckCountingController.MAP_ENTRY_DOWN_DAYS);
        actualDaysTotal = resultMap.get(InstrumentCheckCountingController.MAP_ENTRY_DAYS_TOTAL);

        assertEquals(expectedNumberOfUpDays, actualNumberOfUpDays);
        assertEquals(expectedNumberOfDownDays, actualNumberOfDownDays);
        assertEquals(expectedDaysTotal, actualDaysTotal);
    }

    @Test
    /**
     * Tests the check if there are more down-days than up-days.
     */
    public void testCheckMoreDownThanUpDays() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 22); // The first day on which the number of down-days exceeds the number of up-days.
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry
                .setText(MessageFormat.format(this.resources.getString("protocol.moreDownDays"), "2", "3"));

        // Call controller to perform check.
        calendar.set(2022, 6, 20); // Begin check on 20.07.22 (2 of 3 days are down days from there on)
        try {
            protocolEntries = this.instrumentCheckCountingController.checkMoreDownThanUpDays(calendar.getTime(),
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
     * Tests the check if there are more up-days than down-days.
     */
    public void testCheckMoreUpThanDownDays() {
        ProtocolEntry expectedProtocolEntry1, expectedProtocolEntry2;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entries.
        calendar.set(2022, 6, 20); // The first day on which the number of down-days exceeds the number of up-days.
        expectedProtocolEntry1 = new ProtocolEntry();
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry1.setText(MessageFormat.format(this.resources.getString("protocol.moreUpDays"), "2", "2"));

        calendar.set(2022, 6, 21); // The second day on which the number of down-days exceeds the number of up-days.
        expectedProtocolEntry2 = new ProtocolEntry();
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry2.setText(MessageFormat.format(this.resources.getString("protocol.moreUpDays"), "2", "3"));

        // Call controller to perform check.
        calendar.set(2022, 6, 19); // Begin check on 19.07.22.
        try {
            protocolEntries = this.instrumentCheckCountingController.checkMoreUpThanDownDays(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(2, protocolEntries.size());

            // Validate the protocol entries.
            for (ProtocolEntry actualProtocolEntry : protocolEntries) {
                if (actualProtocolEntry.getDate().getTime() == expectedProtocolEntry1.getDate().getTime())
                    assertEquals(expectedProtocolEntry1, actualProtocolEntry);
                else if (actualProtocolEntry.getDate().getTime() == expectedProtocolEntry2.getDate().getTime())
                    assertEquals(expectedProtocolEntry2, actualProtocolEntry);
                else
                    fail("The result of the check contains an unexpected protocol entry.");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if there are good closes than bad closes.
     */
    public void testCheckMoreGoodThanBadCloses() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 20); // The first day on which the number of good closes exceeds the number of bad closes.
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry
                .setText(MessageFormat.format(this.resources.getString("protocol.moreGoodCloses"), "2", "3"));

        // Call controller to perform check.
        calendar.set(2022, 6, 18); // Begin check on 18.07.22.
        try {
            protocolEntries = this.instrumentCheckCountingController.checkMoreGoodThanBadCloses(calendar.getTime(),
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
     * Tests the check if three lower closes have occurred.
     */
    public void testCheckThreeLowerCloses() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 5, 14); // The day on which threee lower lows occurred (14.06.22).
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.threeLowerClosesLowVolume"));

        // Call controller to perform check.
        calendar.set(2022, 5, 14); // Begin check on 14.06.22.
        try {
            protocolEntries = this.instrumentCheckCountingController.checkThreeLowerCloses(calendar.getTime(),
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
}
