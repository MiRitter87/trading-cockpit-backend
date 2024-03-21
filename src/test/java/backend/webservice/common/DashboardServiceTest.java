package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.StockExchange;
import backend.model.dashboard.MarketHealthStatus;
import backend.model.dashboard.SwingTradingEnvironmentStatus;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Tests the DashboardService.
 *
 * @author Michael
 */
public class DashboardServiceTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * Industry Group: Copper Miners.
     */
    private Instrument copperIndustryGroup;

    /**
     * The first Quotation of the Copper IG.
     */
    private Quotation copperIgQuotation1;

    /**
     * The second Quotation of the Copper IG.
     */
    private Quotation copperIgQuotation2;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    @AfterAll
    /**
     * Tasks to be performed once at end of test class.
     */
    public static void tearDownClass() {
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
    private void setUp() {
        this.createDummyInstruments();
        this.createDummyQuotations();
        this.createMovingAverageData();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteDummyQuotations();
        this.deleteDummyInstruments();
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        this.copperIndustryGroup = this.getCopperIndustryGroup();

        try {
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.copperIndustryGroup);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy quotations.
     */
    private void createDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        this.copperIgQuotation1 = new Quotation();
        this.copperIgQuotation1.setInstrument(this.copperIndustryGroup);
        this.copperIgQuotation1.setDate(calendar.getTime());
        this.copperIgQuotation1.setClose(new BigDecimal(100));
        quotations.add(this.copperIgQuotation1);

        this.copperIgQuotation2 = new Quotation();
        this.copperIgQuotation2.setInstrument(this.copperIndustryGroup);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        this.copperIgQuotation2.setDate(calendar.getTime());
        this.copperIgQuotation2.setClose(new BigDecimal(99));
        quotations.add(this.copperIgQuotation2);

        try {
            quotationDAO.insertQuotations(quotations);
            this.copperIndustryGroup.getQuotations().addAll(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy quotations from the database.
     */
    private void deleteDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        quotations.add(this.copperIgQuotation1);
        quotations.add(this.copperIgQuotation2);

        try {
            quotationDAO.deleteQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates MovingAverageData.
     */
    private void createMovingAverageData() {
        List<Quotation> quotations = new ArrayList<>();
        MovingAverageData maData1 = new MovingAverageData();
        MovingAverageData maData2 = new MovingAverageData();

        try {
            maData1.setSma10(95);
            maData1.setSma20(90);
            this.copperIgQuotation1.setMovingAverageData(maData1);
            quotations.add(this.copperIgQuotation1);

            maData2.setSma10(94);
            maData2.setSma20(89);
            this.copperIgQuotation2.setMovingAverageData(maData2);
            quotations.add(this.copperIgQuotation2);

            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Gets the Instrument of the Copper Miners Industry Group.
     *
     * @return The Instrument of the Copper Miners Industry Group.
     */
    private Instrument getCopperIndustryGroup() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("COPX");
        instrument.setName("Global X Copper Miners ETF");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.IND_GROUP);

        return instrument;
    }

    @Test
    /**
     * Tests the determination of the MarketHealthStatus.
     */
    public void testGetMarketHealthStatus() {
        MarketHealthStatus marketHealthStatus;
        WebServiceResult getMarketHealthStatusResult;
        DashboardService dashboardService = new DashboardService();

        getMarketHealthStatusResult = dashboardService.getMarketHealthStatus(this.copperIndustryGroup.getId());

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getMarketHealthStatusResult) == false);

        // Check attributes of the provided MarketHealthStatus object.
        marketHealthStatus = (MarketHealthStatus) getMarketHealthStatusResult.getData();
        assertTrue(marketHealthStatus instanceof MarketHealthStatus);
        assertEquals(this.copperIndustryGroup.getSymbol(), marketHealthStatus.getSymbol());
        assertEquals(this.copperIndustryGroup.getName(), marketHealthStatus.getName());
        assertEquals(this.copperIgQuotation1.getDate().getTime(), marketHealthStatus.getDate().getTime());
    }

    @Test
    /**
     * Tests the determination of the MarketHealthStatus. Checks if a correct error is provided if an Instrument of the
     * wrong type is being given.
     */
    public void testGetMarketHealthStatusWrongType() {
        WebServiceResult getMarketHealthStatusResult;
        DashboardService dashboardService = new DashboardService();
        String expectedErrorMessage = this.resources.getString("dashboard.wrongInstrumentType");
        String actualErrorMessage;

        // Change type of Copper industry group to ETF. This type is not allowed.
        this.copperIndustryGroup.setType(InstrumentType.ETF);
        try {
            instrumentDAO.updateInstrument(this.copperIndustryGroup);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        getMarketHealthStatusResult = dashboardService.getMarketHealthStatus(this.copperIndustryGroup.getId());

        // Assure an error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getMarketHealthStatusResult) == true);

        // Verify the given error message.
        actualErrorMessage = getMarketHealthStatusResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests determination of the SwingTradingEnvironmentStatus 'GREEN'.
     */
    public void testGetSwingTradingEnvironmentStatusGreen() {
        DashboardService dashboardService = new DashboardService();
        SwingTradingEnvironmentStatus actualStatus;
        SwingTradingEnvironmentStatus expectedStatus = SwingTradingEnvironmentStatus.GREEN;

        actualStatus = dashboardService.getSwingTradingEnvironmentStatus(this.copperIndustryGroup);
        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    /**
     * Tests determination of the SwingTradingEnvironmentStatus 'YELLOW'.
     */
    public void testGetSwingTradingEnvironmentStatusYellow() {
        DashboardService dashboardService = new DashboardService();
        SwingTradingEnvironmentStatus actualStatus;
        SwingTradingEnvironmentStatus expectedStatus = SwingTradingEnvironmentStatus.YELLOW;

        // Modify Quotation data to assure status 'YELLOW' is given. SMA(10) is falling.
        this.copperIgQuotation1.getMovingAverageData().setSma10(93);

        actualStatus = dashboardService.getSwingTradingEnvironmentStatus(this.copperIndustryGroup);
        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    /**
     * Tests determination of the SwingTradingEnvironmentStatus 'RED'.
     */
    public void testGetSwingTradingEnvironmentStatusRed() {
        DashboardService dashboardService = new DashboardService();
        SwingTradingEnvironmentStatus actualStatus;
        SwingTradingEnvironmentStatus expectedStatus = SwingTradingEnvironmentStatus.RED;

        // Modify Quotation data to assure status 'RED' is given. Price below SMA(20).
        this.copperIgQuotation1.setClose(new BigDecimal(this.copperIgQuotation1.getMovingAverageData().getSma20() - 1));

        actualStatus = dashboardService.getSwingTradingEnvironmentStatus(this.copperIndustryGroup);
        assertEquals(expectedStatus, actualStatus);
    }
}
