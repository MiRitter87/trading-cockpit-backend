package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.IndicatorCalculator;
import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the DistributionDaysChartController.
 *
 * @author Michael
 */
public class DistributionDaysChartControllerTest {
    /**
     * The DistributionDaysChartController under test.
     */
    private DistributionDaysChartController distributionDaysChartController;

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
            this.distributionDaysChartController = new DistributionDaysChartController();
            this.initializeDmlInstrument();
            this.initializeDMLIndicators();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.distributionDaysChartController = null;
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
            quotations.addAll(
                    quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));
            this.dmlStock.setQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the indicators of the DML stock.
     */
    private void initializeDMLIndicators() {
        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
        List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
        Quotation quotation;

        for (int i = 0; i < sortedQuotations.size(); i++) {
            quotation = sortedQuotations.get(i);

            // Calculate all Indicators only for most recent Quotation like in the ScanThread.
            if (i == 0)
                quotation = indicatorCalculator.calculateIndicators(this.dmlStock, quotation, true);
            else
                quotation = indicatorCalculator.calculateIndicators(this.dmlStock, quotation, false);
        }
    }

    @Test
    /**
     * Tests the determination of the 25-day rolling sum of Distribution Days.
     */
    public void testGetDistributionDaysSum() {
        int expectedDDSum = 1;
        int actualDDSum;
        Quotation latestQuotation = this.dmlStock.getQuotationsSortedByDate().get(0);

        actualDDSum = this.distributionDaysChartController.getDistributionDaysSum(latestQuotation,
                this.dmlStock.getQuotationsSortedByDate());

        assertEquals(expectedDDSum, actualDDSum);
    }
}
