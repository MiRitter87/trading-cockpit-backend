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

import backend.controller.scan.IndicatorCalculationController;
import backend.dao.quotation.provider.QuotationProviderYahooDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.RelativeStrengthData;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Tests the InstrumentCheckPatternController.
 *
 * @author Michael
 */
public class InstrumentCheckPatternControllerTest {
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
    private InstrumentCheckPatternController instrumentCheckPatternController;

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
        this.instrumentCheckPatternController = new InstrumentCheckPatternController();

        this.initializeDMLQuotations();
        this.initializeDMLIndicators();
        this.initializeDummyRsLinePrices();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.instrumentCheckPatternController = null;
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
            if (i == 0)
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
            else
                quotation = indicatorCalculator.calculateIndicators(instrument, quotation, false);
        }
    }

    /**
     * Initializes dummy values of the RS-line prices.
     */
    private void initializeDummyRsLinePrices() {
        BigDecimal rsLinePrice = new BigDecimal(30);

        for (Quotation quotation : this.dmlQuotations.getQuotations()) {
            if (quotation.getRelativeStrengthData() == null) {
                quotation.setRelativeStrengthData(new RelativeStrengthData());
            }

            if (this.dmlQuotations.getQuotations().indexOf(quotation) > 1) {
                quotation.getRelativeStrengthData().setRsLinePrice(rsLinePrice);
            } else {
                quotation.getRelativeStrengthData().setRsLinePrice(new BigDecimal(0.1));
            }

            rsLinePrice = rsLinePrice.subtract(new BigDecimal(0.1));
        }
    }

    @Test
    /**
     * Tests the check if Instrument has advanced a certain amount on above-average volume.
     */
    public void testCheckUpOnVolume() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 14); // Up on Volume day is 14.07.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.upOnVolume"));

        // Call controller to perform check.
        calendar.set(2022, 6, 8); // Begin check on 08.07.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkUpOnVolume(calendar.getTime(),
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
     * Tests the check if Instrument has declined a certain amount on above-average volume.
     */
    public void testCheckDownOnVolume() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 22); // Down on Volume day is 22.07.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.downOnVolume"));

        // Call controller to perform check.
        calendar.set(2022, 5, 9); // Begin check on 09.06.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkDownOnVolume(calendar.getTime(),
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
     * Tests the check if Instrument is churning (price stalling on increased volume).
     */
    public void testCheckChurning() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 2, 10); // Churning on 10.03.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry.setText(this.resources.getString("protocol.churning"));

        // Call controller to perform check.
        calendar.set(2022, 2, 10); // Begin check on 10.03.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkChurning(calendar.getTime(),
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
     * Tests the check if Instrument is reversing (open and close in lower third of candle on above-average volume).
     */
    public void testCheckHighVolumeReversal() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 4, 12); // Reversal on 12.05.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.reversal"));

        // Call controller to perform check.
        calendar.set(2022, 3, 14); // Begin check on 14.04.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkHighVolumeReversal(calendar.getTime(),
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
     * Tests the check if Instrument made an exhaustion gap up.
     */
    public void testCheckExhaustionGapUp() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();
        float gapSizePercent = (float) 1.14;

        // Define the expected protocol entry.
        calendar.set(2022, 1, 28); // Gap up on 28.02.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        expectedProtocolEntry
                .setText(MessageFormat.format(resources.getString("protocol.exhaustionGapUp"), gapSizePercent));

        // Call controller to perform check.
        calendar.set(2022, 1, 22); // Begin check on 22.02.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkExhaustionGapUp(calendar.getTime(),
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
     * Tests the check if Instrument made a bullish gap up.
     */
    public void testCheckBullishGapUp() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();
        float gapSizePercent = (float) 1.14;

        // Define the expected protocol entry.
        calendar.set(2022, 1, 28); // Gap up on 28.02.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry
                .setText(MessageFormat.format(resources.getString("protocol.bullishGapUp"), gapSizePercent));

        // Call controller to perform check.
        calendar.set(2022, 1, 22); // Begin check on 22.02.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkBullishGapUp(calendar.getTime(),
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
     * Tests the check if Instrument made a new 52-week high.
     */
    public void testCheckNew52WeekHigh() {
        ProtocolEntry expectedProtocolEntry1;
        ProtocolEntry expectedProtocolEntry2;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entries.
        calendar.set(2021, 10, 8); // The first day with a new 52-week high.
        expectedProtocolEntry1 = new ProtocolEntry();
        expectedProtocolEntry1.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry1.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry1.setText(this.resources.getString("protocol.new52WeekHigh"));

        calendar.set(2021, 10, 9); // The second day with a new 52-week high.
        expectedProtocolEntry2 = new ProtocolEntry();
        expectedProtocolEntry2.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry2.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry2.setText(this.resources.getString("protocol.new52WeekHigh"));

        // Call controller to perform check.
        calendar.set(2021, 10, 8); // Begin check on 08.11.21.
        try {
            protocolEntries = this.instrumentCheckPatternController.checkNew52WeekHigh(calendar.getTime(),
                    this.dmlQuotations);

            // Verify the check result.
            assertEquals(2, protocolEntries.size());

            // Validate the protocol entries.
            for (ProtocolEntry actualProtocolEntry : protocolEntries) {
                if (actualProtocolEntry.getDate().getTime() == expectedProtocolEntry1.getDate().getTime()) {
                    assertEquals(expectedProtocolEntry1, actualProtocolEntry);
                } else if (actualProtocolEntry.getDate().getTime() == expectedProtocolEntry2.getDate().getTime()) {
                    assertEquals(expectedProtocolEntry2, actualProtocolEntry);
                } else {
                    fail("The result of the check contains an unexpected protocol entry.");
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if Instrument closed near its daily high price.
     */
    public void testCheckCloseNearHigh() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 14); // Close near high on 14.07.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.closeNearHigh"));

        // Call controller to perform check.
        calendar.set(2022, 6, 8); // Begin check on 08.07.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkCloseNearHigh(calendar.getTime(),
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
     * Tests the check if Instrument closed near its daily low price.
     */
    public void testCheckCloseNearLow() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 22); // Close near low on 22.07.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.closeNearLow"));

        // Call controller to perform check.
        calendar.set(2022, 6, 19); // Begin check on 19.07.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkCloseNearLow(calendar.getTime(),
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
     * Tests the check if the RS-line of an Instrument made a new 52-week high.
     */
    public void testCheckRsLineNew52WeekHigh() {
        ProtocolEntry expectedProtocolEntry = new ProtocolEntry();
        ProtocolEntry actualProtocolEntry;
        List<ProtocolEntry> protocolEntries;
        Calendar calendar = Calendar.getInstance();

        // Define the expected protocol entry.
        calendar.set(2022, 6, 20); // New RS line 52w-high on 20.07.22
        expectedProtocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        expectedProtocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        expectedProtocolEntry.setText(this.resources.getString("protocol.rsLineNew52WeekHigh"));

        // Call controller to perform check.
        calendar.set(2022, 6, 20); // Begin check on 20.07.22
        try {
            protocolEntries = this.instrumentCheckPatternController.checkRsLineNew52WeekHigh(calendar.getTime(),
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
