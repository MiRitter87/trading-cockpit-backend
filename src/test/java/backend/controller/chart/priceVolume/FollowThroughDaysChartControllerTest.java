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

        try {
            DAOManager.getInstance().close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        try {
            this.followThroughDaysChartController = new FollowThroughDaysChartController();
            this.initializeDmlInstrument();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
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

    @Test
    /**
     * Tests the check if the current Quotation constitutes a Follow-Through Day. In this test the necessary
     * requirements for a Follow-Through Day are met.
     */
    public void testIsFollowThroughDay() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(6);
        Quotation previousQuotation = quotationsSortedByDate.get(7);

        boolean isFollowThroughDay = this.followThroughDaysChartController.isFollowThroughDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertTrue(isFollowThroughDay);
    }

    @Test
    /**
     * Tests the check if the current Quotation constitutes a Follow-Through Day. In this test the volume is not higher
     * than the previous days volume. Therefore no Follow-Through Day is given.
     */
    public void testNoFTDVolumeTooLow() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(6);
        Quotation previousQuotation = quotationsSortedByDate.get(7);

        currentQuotation.setVolume(previousQuotation.getVolume() - 1);

        boolean isFollowThroughDay = this.followThroughDaysChartController.isFollowThroughDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertFalse(isFollowThroughDay);
    }

    @Test
    /**
     * Tests the check if the current Quotation constitutes a Follow-Through Day. In this test the performance is not
     * high enough to constitute a Follow-Through Day.
     */
    public void testNoFTDPerformanceTooLow() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(6);
        Quotation previousQuotation = quotationsSortedByDate.get(7);

        currentQuotation.setClose(previousQuotation.getClose());

        boolean isFollowThroughDay = this.followThroughDaysChartController.isFollowThroughDay(currentQuotation,
                previousQuotation, quotationsSortedByDate);

        assertFalse(isFollowThroughDay);
    }

    @Test
    /**
     * Tests determination of the low price of the past number of days.
     */
    public void testGetLowPricePast() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(9);
        final int daysBefore = -5;
        BigDecimal actualLowPrice;
        BigDecimal expectedLowPrice = new BigDecimal("1.19");

        actualLowPrice = this.followThroughDaysChartController.getLowPrice(currentQuotation, quotationsSortedByDate,
                daysBefore);
        assertEquals(expectedLowPrice, actualLowPrice);
    }

    @Test
    /**
     * Tests determination of the low price of the future number of days.
     */
    public void testGetLowPriceFuture() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation currentQuotation = quotationsSortedByDate.get(9);
        final int daysAfter = 5;
        BigDecimal actualLowPrice;
        BigDecimal expectedLowPrice = new BigDecimal("1.24");

        actualLowPrice = this.followThroughDaysChartController.getLowPrice(currentQuotation, quotationsSortedByDate,
                daysAfter);
        assertEquals(expectedLowPrice, actualLowPrice);
    }

    @Test
    /**
     * Tests the check if the low before the FTD has been undercut after the FTD occurred.
     */
    public void testIsLowBeforeFTDUndercutYes() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(6);
        Quotation undercutQuotation;
        boolean isLowUndercut;

        // Modify price of following Quotation to assure price of FTD is undercut.
        undercutQuotation = quotationsSortedByDate.get(0);
        undercutQuotation.setLow(new BigDecimal("1.18"));

        isLowUndercut = this.followThroughDaysChartController.isLowBeforeFTDUndercut(ftdQuotation,
                quotationsSortedByDate, 10, 10);
        assertTrue(isLowUndercut);
    }

    @Test
    /**
     * Tests the check if the low before the FTD has been undercut after the FTD occurred.
     */
    public void testIsLowBeforeFTDUndercutNo() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(6);
        boolean isLowUndercut;

        isLowUndercut = this.followThroughDaysChartController.isLowBeforeFTDUndercut(ftdQuotation,
                quotationsSortedByDate, 10, 10);
        assertFalse(isLowUndercut);
    }

    @Test
    /**
     * Tests the check if a Distribution Day is following after the FTD.
     */
    public void testIsDistributionDayFollowingYes() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(6);
        boolean isDistributionDayFollowing;

        isDistributionDayFollowing = this.followThroughDaysChartController.isDistributionDayFollowing(ftdQuotation,
                quotationsSortedByDate, 10);
        assertTrue(isDistributionDayFollowing);
    }

    @Test
    /**
     * Tests the check if a Distribution Day is following after the FTD.
     */
    public void testIsDistributionDayFollowingNo() {
        List<Quotation> quotationsSortedByDate = this.dmlStock.getQuotationsSortedByDate();
        Quotation ftdQuotation = quotationsSortedByDate.get(6);
        Quotation distributionQuotation;
        boolean isDistributionDayFollowing;

        // Negate the Distribution Day to assure no Distribution Day is following.
        distributionQuotation = quotationsSortedByDate.get(0);
        distributionQuotation.setClose(distributionQuotation.getOpen());

        isDistributionDayFollowing = this.followThroughDaysChartController.isDistributionDayFollowing(ftdQuotation,
                quotationsSortedByDate, 10);
        assertFalse(isDistributionDayFollowing);
    }
}
