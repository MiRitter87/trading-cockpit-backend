package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.IndicatorCalculationController;
import backend.dao.DAOManager;
import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the PocketPivotChartController.
 *
 * @author Michael
 */
public class PocketPivotChartControllerTest {
    /**
     * The PocketPivotChartController under test.
     */
    private PocketPivotChartController pocketPivotChartController;

    /**
     * A trading instrument with quotations.
     */
    private Instrument dmlStock;

    /**
     * DAO to access quotation data from Yahoo.
     */
    private static QuotationProviderDAO quotationProviderYahooDAO;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        try {
            quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed once at the end of the test class.
     */
    @AfterAll
    public static void tearDownClass() {
        quotationProviderYahooDAO = null;

        try {
            DAOManager.getInstance().close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        try {
            this.pocketPivotChartController = new PocketPivotChartController();
            this.initializeDmlInstrument();
            this.initializeDMLIndicators();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.pocketPivotChartController = null;
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

    /**
     * Initializes the indicators of the DML stock.
     */
    private void initializeDMLIndicators() {
        IndicatorCalculationController indicatorCalculator = new IndicatorCalculationController();
        List<Quotation> sortedQuotations = this.dmlStock.getQuotationsSortedByDate();
        Quotation quotation;

        for (int i = 0; i < sortedQuotations.size(); i++) {
            quotation = sortedQuotations.get(i);

            // Calculate all Indicators only for most recent Quotation like in the ScanThread.
            if (i == 0) {
                quotation = indicatorCalculator.calculateIndicators(this.dmlStock, quotation, true);
            } else {
                quotation = indicatorCalculator.calculateIndicators(this.dmlStock, quotation, false);
            }
        }
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the necessary requirements for
     * a Pocket Pivot are met.
     */
    @Test
    public void testIsPocketPivot() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int indexOfPocketPivot = 73;

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate,
                indexOfPocketPivot);

        assertTrue(isPocketPivot);
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the Quotation is not an up-day.
     * Therefore no Pocket Pivot is given.
     */
    @Test
    public void testIsPocketPivotNoUpDay() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int indexOfPocketPivot = 73;
        Quotation currentQuotation = quotationsSortedByDate.get(indexOfPocketPivot);
        Quotation previousQuotation = quotationsSortedByDate.get(indexOfPocketPivot + 1);

        currentQuotation.setClose(previousQuotation.getClose());

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate,
                indexOfPocketPivot);

        assertFalse(isPocketPivot);
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the closing price of the
     * Quotation is below the SMA(50). Therefore no Pocket Pivot is given.
     */
    @Test
    public void testIsPocketPivotCloseBelowSma50() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int indexOfPocketPivot = 73;
        Quotation currentQuotation = quotationsSortedByDate.get(indexOfPocketPivot);

        currentQuotation.getMovingAverageData().setSma50(currentQuotation.getClose().floatValue() + 1);

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate,
                indexOfPocketPivot);

        assertFalse(isPocketPivot);
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the volume of the Quotation is
     * not high enough. Therefore no Pocket Pivot is given.
     */
    @Test
    public void testIsPocketPivotVolumeTooLow() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int indexOfPocketPivot = 73;
        final int indexOfPreviousDownDay = 75;
        Quotation currentQuotation = quotationsSortedByDate.get(indexOfPocketPivot);
        Quotation previousDownQuotation = quotationsSortedByDate.get(indexOfPreviousDownDay);

        currentQuotation.setVolume(previousDownQuotation.getVolume());

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate,
                indexOfPocketPivot);

        assertFalse(isPocketPivot);
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the closing price of the
     * Quotation is below the SMA(10). Therefore no Pocket Pivot is given.
     */
    @Test
    public void testIsPocketPivotCloseBelowSma10() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int indexOfPocketPivot = 73;
        Quotation currentQuotation = quotationsSortedByDate.get(indexOfPocketPivot);

        currentQuotation.getMovingAverageData().setSma10(currentQuotation.getClose().floatValue() + 1);

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate,
                indexOfPocketPivot);

        assertFalse(isPocketPivot);
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the low price of the Quotation
     * is extended above the SMA(10). Therefore no Pocket Pivot is given.
     */
    @Test
    public void testIsPocketPivotExtendedAboveSma10() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int indexOfPocketPivot = 73;
        final float threePercent = 1.03f;
        Quotation currentQuotation = quotationsSortedByDate.get(indexOfPocketPivot);
        BigDecimal extendedLowPrice;

        extendedLowPrice = new BigDecimal(currentQuotation.getMovingAverageData().getSma10() * threePercent);
        currentQuotation.setLow(extendedLowPrice);

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate,
                indexOfPocketPivot);

        assertFalse(isPocketPivot);
    }

    /**
     * Tests the check if the current Quotation constitutes a Pocket Pivot. In this test the history contains not enough
     * quotations to check if a Pocket Pivot is given. Therefore no Pocket Pivot is given.
     */
    @Test
    public void testIsPocketPivotHistoryTooSmall() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        final int index11 = 11;

        // Shrink history size, preventing pocket pivot determination.
        quotationsSortedByDate = quotationsSortedByDate.subList(0, index11);

        boolean isPocketPivot = this.pocketPivotChartController.isPocketPivot(quotationsSortedByDate, 0);

        assertFalse(isPocketPivot);
    }
}
