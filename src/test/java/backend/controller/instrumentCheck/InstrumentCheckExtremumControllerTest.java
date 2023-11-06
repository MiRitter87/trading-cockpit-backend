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
    private void setUp() {
        this.instrumentCheckExtremumController = new InstrumentCheckExtremumController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.instrumentCheckExtremumController = null;
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
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
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
        ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entries.
        calendar.set(2021, 9, 12); // Largest daily high/low-spread occurred on 12.10.21.
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.UNCERTAIN);
        expectedProtocolEntry1.setText(this.resources.getString("protocol.largestDailySpread"));

        calendar.set(2022, 2, 9); // A new largest daily high/low-spread occurred on 09.03.22.
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.UNCERTAIN);
        expectedProtocolEntry2.setText(this.resources.getString("protocol.largestDailySpread"));

        // Call controller to perform check.
        calendar.set(2021, 9, 1); // Begin check on 01.10.21.
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
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.UNCERTAIN);
        expectedProtocolEntry1.setText(this.resources.getString("protocol.largestDailyVolume"));

        calendar.set(2022, 1, 4); // A new largest daily volume occurred on 04.02.22.
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.UNCERTAIN);
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
}
