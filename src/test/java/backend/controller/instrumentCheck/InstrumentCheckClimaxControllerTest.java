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
 * Tests the InstrumentCheckClimaxController.
 *
 * @author Michael
 */
public class InstrumentCheckClimaxControllerTest {
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
     * The controller for climax-related Instrument health checks.
     */
    private InstrumentCheckClimaxController instrumentCheckClimaxController;

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
        this.instrumentCheckClimaxController = new InstrumentCheckClimaxController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.instrumentCheckClimaxController = null;
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
     * Tests the check if Instrument has a climax movement within a week.
     */
    public void testCheckClimaxMoveOneWeek() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 2, 2); // The day on which a climax of at least 25% within a week occurred: 02.03.22.
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
        expectedProtocolEntry
                .setText(MessageFormat.format(this.resources.getString("protocol.climaxOneWeek"), "25,79"));

        // Call controller to perform check.
        calendar.set(2022, 1, 29); // Begin check on 29.02.22
        try {
            protocolEntries = this.instrumentCheckClimaxController.checkClimaxMoveOneWeek(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result
            assertEquals(1, protocolEntries.size());

            // Validate the protocol entry
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument has a climax movement within three weeks.
     */
    public void testCheckClimaxMoveThreeWeeks() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2021, 8, 16); // The day on which a climax of at least 25% within a week occurred: 16.09.21.
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
        expectedProtocolEntry
                .setText(MessageFormat.format(this.resources.getString("protocol.climaxThreeWeeks"), "55,4"));

        // Call controller to perform check.
        calendar.set(2021, 8, 16); // Begin check on 16.09.21
        try {
            protocolEntries = this.instrumentCheckClimaxController.checkClimaxMoveThreeWeeks(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result
            assertEquals(1, protocolEntries.size());

            // Validate the protocol entry
            actualProtocolEntry = protocolEntries.get(0);
            assertEquals(expectedProtocolEntry, actualProtocolEntry);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
