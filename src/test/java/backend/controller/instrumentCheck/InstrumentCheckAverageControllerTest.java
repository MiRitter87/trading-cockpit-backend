package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
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
 * Tests the InstrumentCheckAverageController.
 *
 * @author Michael
 */
public class InstrumentCheckAverageControllerTest {
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
     * The controller for moving average related Instrument checks.
     */
    private InstrumentCheckAverageController instrumentCheckAverageController;

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
        this.instrumentCheckAverageController = new InstrumentCheckAverageController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.instrumentCheckAverageController = null;
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
     * Tests the check if the price of an Instrument closed below SMA(50).
     */
    public void testCheckCloseBelowSma50() {
        ProtocolEntry expectedProtocolEntry1 = new ProtocolEntry();
        ProtocolEntry expectedProtocolEntry2 = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entries.
        calendar.set(2022, 3, 21); // The day on which the price closed below the SMA(50).
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry1.setText(this.resources.getString("protocol.closeBelowSma50HighVolume"));

        calendar.set(2022, 6, 22); // The day on which the price closed below the SMA(50).
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry2.setText(this.resources.getString("protocol.closeBelowSma50HighVolume"));

        // Call controller to perform check.
        calendar.set(2022, 3, 7); // Begin check on 07.04.22
        try {
            protocolEntries = this.instrumentCheckAverageController.checkCloseBelowSma50(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result
            assertEquals(2, protocolEntries.size());

            // Validate the protocol entries
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
     * Tests the check if the price of an Instrument closed below EMA(21).
     */
    public void testCheckCloseBelowEma21() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 22); // The day on which the price closed below the EMA(21).
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.closeBelowEma21"));

        // Call controller to perform check.
        calendar.set(2022, 6, 15); // Begin check on 15.07.22
        try {
            protocolEntries = this.instrumentCheckAverageController.checkCloseBelowEma21(calendar.getTime(),
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
     * Tests the check if the price of an Instrument is extended above the SMA(200).
     */
    public void testCheckExtendedAboveSma200() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();
        float percentAboveSma200 = (float) 123.71;

        // Modify quotation of test data. Closing price has to be at least 100% above the SMA(200).
        this.dmlQuotations.sortQuotationsByDate();
        this.dmlQuotations.getQuotations().get(0).setClose(BigDecimal.valueOf(4));

        // Define the expected protocol entry.
        calendar.set(2022, 6, 22); // The day on which the price is extended above the SMA(200).
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
        expectedProtocolEntry.setText(
                MessageFormat.format(this.resources.getString("protocol.extendedAboveSma200"), percentAboveSma200));

        // Call controller to perform check.
        calendar.set(2022, 6, 15); // Begin check on 15.07.22
        try {
            protocolEntries = this.instrumentCheckAverageController.checkExtendedAboveSma200(calendar.getTime(),
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
