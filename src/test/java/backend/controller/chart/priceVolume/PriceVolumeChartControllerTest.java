package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

/**
 * Tests the PriceVolumeChartController.
 *
 * @author Michael
 */
public class PriceVolumeChartControllerTest {
    /**
     * The PriceVolumeChartController under test.
     */
    private PriceVolumeChartController priceVolumeChartController;

    /**
     * A trading instrument with quotations.
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
            this.priceVolumeChartController = new PriceVolumeChartController();
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
        this.priceVolumeChartController = null;
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
            quotations.addAll(quotationProviderYahooDAO.getQuotationHistory(this.dmlStock, 1));
            this.dmlStock.setQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the check if the current Quotation constitutes a Distribution Day. In this test the necessary requirements
     * for a Distribution Day are met.
     */
    public void testIsDistributionDay() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(0);
        Quotation previousQuotation = quotationsSortedByDate.get(1);

        boolean isDistributionDay = this.priceVolumeChartController.isDistributionDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertTrue(isDistributionDay);
    }

    @Test
    /**
     * Tests the check if the current Quotation constitutes a Distribution Day. In this test the volume is not higher
     * than the previous days volume. Therefore no Distribution Day is given.
     */
    public void testNoDDVolumeTooLow() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(0);
        Quotation previousQuotation = quotationsSortedByDate.get(1);

        currentQuotation.setVolume(previousQuotation.getVolume() - 1);

        boolean isDistributionDay = this.priceVolumeChartController.isDistributionDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertFalse(isDistributionDay);
    }

    @Test
    /**
     * Tests the check if the current Quotation constitutes a Distribution Day. In this test the performance is not low
     * enough to constitute a Distribution Day.
     */
    public void testNoDDPerformanceTooHigh() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(0);
        Quotation previousQuotation = quotationsSortedByDate.get(1);

        currentQuotation.setClose(previousQuotation.getClose());

        boolean isDistributionDay = this.priceVolumeChartController.isDistributionDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertFalse(isDistributionDay);
    }
}
