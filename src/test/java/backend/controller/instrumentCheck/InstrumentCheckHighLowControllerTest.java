package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
 * Tests the InstrumentCheckHighLowController.
 *
 * @author MiRitter87
 */
public class InstrumentCheckHighLowControllerTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * DAO to access quotation data from Yahoo.
     */
    private static QuotationProviderYahooDAO quotationProviderYahooDAO;

    /**
     * The controller for Instrument checks.
     */
    private InstrumentCheckHighLowController instrumentCheckHighLowController;

    /**
     * A list of quotations of the DML stock.
     */
    private QuotationArray dmlQuotations;

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
        this.instrumentCheckHighLowController = new InstrumentCheckHighLowController();

        this.initializeDMLQuotations();
        this.initializeDummyRsLinePrices();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.instrumentCheckHighLowController = null;
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
     * Initializes dummy values of the RS-line prices.
     */
    private void initializeDummyRsLinePrices() {
        BigDecimal rsLinePrice = new BigDecimal("30");

        for (Quotation quotation : this.dmlQuotations.getQuotations()) {
            if (quotation.getRelativeStrengthData() == null) {
                quotation.setRelativeStrengthData(new RelativeStrengthData());
            }

            if (this.dmlQuotations.getQuotations().indexOf(quotation) > 1) {
                quotation.getRelativeStrengthData().setRsLinePrice(rsLinePrice);
            } else {
                quotation.getRelativeStrengthData().setRsLinePrice(new BigDecimal("0.1"));
            }

            rsLinePrice = rsLinePrice.subtract(new BigDecimal("0.1"));
        }
    }

    /**
     * Tests the check if Instrument closed near its daily high price.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
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
            protocolEntries = this.instrumentCheckHighLowController.checkCloseNearHigh(calendar.getTime(),
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

    /**
     * Tests the check if Instrument closed near its daily low price.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
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
            protocolEntries = this.instrumentCheckHighLowController.checkCloseNearLow(calendar.getTime(),
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

    /**
     * Tests the check if Instrument made a new 52-week high.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
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
            protocolEntries = this.instrumentCheckHighLowController.checkNew52WeekHigh(calendar.getTime(),
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

    /**
     * Tests the check if the RS-line of an Instrument made a new 52-week high.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
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
            protocolEntries = this.instrumentCheckHighLowController.checkRsLineNew52WeekHigh(calendar.getTime(),
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
