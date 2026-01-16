package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import backend.dao.DAOManager;
import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the FollowThroughDaysChartController.
 *
 * @author Michael
 */
public class FollowThroughDaysChartControllerTest {
    /**
     * The FollowThroughDaysChartController under test.
     */
    private FollowThroughDaysChartController followThroughDaysChartController;

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
            this.followThroughDaysChartController = new FollowThroughDaysChartController();
            this.initializeDmlInstrument();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.followThroughDaysChartController = null;
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
     * Tests the check if the current Quotation constitutes a Follow-Through Day. In this test the necessary
     * requirements for a Follow-Through Day are met.
     */
    @Test
    public void testIsFollowThroughDay() {
        final int index6 = 6;
        final int index7 = 7;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(index6);
        Quotation previousQuotation = quotationsSortedByDate.get(index7);

        boolean isFollowThroughDay = this.followThroughDaysChartController.isFollowThroughDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertTrue(isFollowThroughDay);
    }

    /**
     * Tests the check if the current Quotation constitutes a Follow-Through Day. In this test the volume is not higher
     * than the previous days volume. Therefore no Follow-Through Day is given.
     */
    @Test
    public void testNoFTDVolumeTooLow() {
        final int index6 = 6;
        final int index7 = 7;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(index6);
        Quotation previousQuotation = quotationsSortedByDate.get(index7);

        currentQuotation.setVolume(previousQuotation.getVolume() - 1);

        boolean isFollowThroughDay = this.followThroughDaysChartController.isFollowThroughDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertFalse(isFollowThroughDay);
    }

    /**
     * Tests the check if the current Quotation constitutes a Follow-Through Day. In this test the performance is not
     * high enough to constitute a Follow-Through Day.
     */
    @Test
    public void testNoFTDPerformanceTooLow() {
        final int index6 = 6;
        final int index7 = 7;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(index6);
        Quotation previousQuotation = quotationsSortedByDate.get(index7);

        currentQuotation.setClose(previousQuotation.getClose());

        boolean isFollowThroughDay = this.followThroughDaysChartController.isFollowThroughDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertFalse(isFollowThroughDay);
    }

    /**
     * Tests determination of the low price of the past number of days.
     */
    @Test
    public void testGetLowPricePast() {
        final int index9 = 9;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(index9);
        final int daysBefore = -5;
        BigDecimal actualLowPrice;
        BigDecimal expectedLowPrice = new BigDecimal("1.19");

        actualLowPrice = this.followThroughDaysChartController.getLowPrice(currentQuotation, quotationsSortedByDate,
                daysBefore);
        assertEquals(expectedLowPrice, actualLowPrice);
    }

    /**
     * Tests determination of the low price of the future number of days.
     */
    @Test
    public void testGetLowPriceFuture() {
        final int index9 = 9;
        final int daysAfter = 5;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(index9);
        BigDecimal actualLowPrice;
        BigDecimal expectedLowPrice = new BigDecimal("1.24");

        actualLowPrice = this.followThroughDaysChartController.getLowPrice(currentQuotation, quotationsSortedByDate,
                daysAfter);
        assertEquals(expectedLowPrice, actualLowPrice);
    }

    /**
     * Tests the check if the low before the FTD has been undercut after the FTD occurred.
     */
    @Test
    public void testIsLowBeforeFTDUndercutYes() {
        final int index6 = 6;
        final int days10 = 10;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(index6);
        Quotation undercutQuotation;
        boolean isLowUndercut;

        // Modify price of following Quotation to assure price of FTD is undercut.
        undercutQuotation = quotationsSortedByDate.get(0);
        undercutQuotation.setLow(new BigDecimal("1.18"));

        isLowUndercut = this.followThroughDaysChartController.isLowBeforeFTDUndercut(ftdQuotation,
                quotationsSortedByDate, days10, days10);
        assertTrue(isLowUndercut);
    }

    /**
     * Tests the check if the low before the FTD has been undercut after the FTD occurred.
     */
    @Test
    public void testIsLowBeforeFTDUndercutNo() {
        final int index6 = 6;
        final int days10 = 10;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(index6);
        boolean isLowUndercut;

        isLowUndercut = this.followThroughDaysChartController.isLowBeforeFTDUndercut(ftdQuotation,
                quotationsSortedByDate, days10, days10);
        assertFalse(isLowUndercut);
    }

    /**
     * Tests the check if a Distribution Day is following after the FTD.
     */
    @Test
    public void testIsDistributionDayFollowingYes() {
        final int index6 = 6;
        final int days10 = 10;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(index6);
        boolean isDistributionDayFollowing;

        isDistributionDayFollowing = this.followThroughDaysChartController.isDistributionDayFollowing(ftdQuotation,
                quotationsSortedByDate, days10);
        assertTrue(isDistributionDayFollowing);
    }

    /**
     * Tests the check if a Distribution Day is following after the FTD.
     */
    @Test
    public void testIsDistributionDayFollowingNo() {
        final int index6 = 6;
        final int days10 = 10;
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(index6);
        Quotation distributionQuotation;
        boolean isDistributionDayFollowing;

        // Negate the Distribution Day to assure no Distribution Day is following.
        distributionQuotation = quotationsSortedByDate.get(0);
        distributionQuotation.setClose(distributionQuotation.getOpen());

        isDistributionDayFollowing = this.followThroughDaysChartController.isDistributionDayFollowing(ftdQuotation,
                quotationsSortedByDate, days10);
        assertFalse(isDistributionDayFollowing);
    }
}
