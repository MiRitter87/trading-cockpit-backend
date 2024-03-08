package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.RelativeStrengthData;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.webservice.ScanTemplate;

/**
 * Tests the QuotationService.
 *
 * @author Michael
 */
public class QuotationServiceTest {
    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private QuotationServiceFixture fixtureHelper;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

    /**
     * The stock of Denison Mines.
     */
    private Instrument denisonMinesStock;

    /**
     * The stock of Ford.
     */
    private Instrument fordStock;

    /**
     * ETF: XLE.
     */
    private Instrument xleETF;

    /**
     * ETF: XLB.
     */
    private Instrument xlbETF;

    /**
     * ETF: XLF.
     */
    private Instrument xlfETF;

    /**
     * Sector: Industrial.
     */
    private Instrument xliSector;

    /**
     * Industry Group: Copper Miners.
     */
    private Instrument copperIndustryGroup;

    /**
     * A Quotation of the Apple stock.
     */
    private Quotation appleQuotation1;

    /**
     * A Quotation of the Apple stock.
     */
    private Quotation appleQuotation2;

    /**
     * A Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation1;

    /**
     * A Quotation of the Ford stock.
     */
    private Quotation fordQuotation1;

    /**
     * A Quotation of the XLE ETF.
     */
    private Quotation xleQuotation1;

    /**
     * A Quotation of the XLE ETF.
     */
    private Quotation xleQuotation2;

    /**
     * A Quotation of the XLB ETF.
     */
    private Quotation xlbQuotation1;

    /**
     * A Quotation of the XLF ETF.
     */
    private Quotation xlfQuotation1;

    /**
     * Quotations of the Denison Mines stock.
     */
    private List<Quotation> denisonMinesQuotations;

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
        this.fixtureHelper = new QuotationServiceFixture();
        this.createDummyInstruments();
        this.createDummyQuotations();
        this.createRelativeStrengthData();
        this.createMovingAverageData();
        this.createDummyIndicators();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteDummyQuotations();
        this.deleteDummyInstruments();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        this.xliSector = this.fixtureHelper.getXliSector();
        this.copperIndustryGroup = this.fixtureHelper.getCopperIndustryGroup();
        this.appleStock = this.fixtureHelper.getAppleStock(this.xliSector, this.copperIndustryGroup);
        this.microsoftStock = this.fixtureHelper.getMicrosoftStock(this.xliSector, this.copperIndustryGroup);
        this.fordStock = this.fixtureHelper.getFordStock(this.xliSector, this.copperIndustryGroup);
        this.denisonMinesStock = this.fixtureHelper.getDenisonMinesStock(this.xliSector, this.copperIndustryGroup);
        this.xleETF = this.fixtureHelper.getXleEtf(this.xliSector, this.copperIndustryGroup);
        this.xlbETF = this.fixtureHelper.getXlbEtf(this.xliSector, this.copperIndustryGroup);
        this.xlfETF = this.fixtureHelper.getXlfEtf(this.xliSector, this.copperIndustryGroup);

        try {
            instrumentDAO.insertInstrument(this.xliSector);
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
            instrumentDAO.insertInstrument(this.appleStock);
            instrumentDAO.insertInstrument(this.microsoftStock);
            instrumentDAO.insertInstrument(this.fordStock);
            instrumentDAO.insertInstrument(this.denisonMinesStock);
            instrumentDAO.insertInstrument(this.xleETF);
            instrumentDAO.insertInstrument(this.xlbETF);
            instrumentDAO.insertInstrument(this.xlfETF);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.xlfETF);
            instrumentDAO.deleteInstrument(this.xlbETF);
            instrumentDAO.deleteInstrument(this.xleETF);
            instrumentDAO.deleteInstrument(this.denisonMinesStock);
            instrumentDAO.deleteInstrument(this.fordStock);
            instrumentDAO.deleteInstrument(this.microsoftStock);
            instrumentDAO.deleteInstrument(this.appleStock);
            instrumentDAO.deleteInstrument(this.copperIndustryGroup);
            instrumentDAO.deleteInstrument(this.xliSector);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy quotations.
     */
    private void createDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        this.appleQuotation1 = this.fixtureHelper.getAppleQuotation1(this.appleStock);
        this.appleQuotation2 = this.fixtureHelper.getAppleQuotation2(this.appleStock);
        this.microsoftQuotation1 = this.fixtureHelper.getMicrosoftQuotation1(this.microsoftStock);
        this.fordQuotation1 = this.fixtureHelper.getFordQuotation1(this.fordStock);
        this.xleQuotation1 = this.fixtureHelper.getXleQuotation1(this.xleETF);
        this.xleQuotation2 = this.fixtureHelper.getXleQuotation2(this.xleETF);
        this.xlbQuotation1 = this.fixtureHelper.getXlbQuotation1(this.xlbETF);
        this.xlfQuotation1 = this.fixtureHelper.getXlfQuotation1(this.xlfETF);
        this.denisonMinesQuotations = this.fixtureHelper
                .getDenisonMinesQuotationsWithoutIndicators(this.denisonMinesStock);

        quotations.add(this.appleQuotation1);
        quotations.add(this.appleQuotation2);
        quotations.add(this.microsoftQuotation1);
        quotations.add(this.fordQuotation1);
        quotations.add(this.xleQuotation1);
        quotations.add(this.xleQuotation2);
        quotations.add(this.xlbQuotation1);
        quotations.add(this.xlfQuotation1);
        quotations.addAll(this.denisonMinesQuotations);

        try {
            quotationDAO.insertQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy quotations from the database.
     */
    private void deleteDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        quotations.add(this.xlfQuotation1);
        quotations.add(this.xlbQuotation1);
        quotations.add(this.xleQuotation2);
        quotations.add(this.xleQuotation1);
        quotations.add(this.fordQuotation1);
        quotations.add(this.microsoftQuotation1);
        quotations.add(this.appleQuotation2);
        quotations.add(this.appleQuotation1);
        quotations.addAll(this.denisonMinesQuotations);

        try {
            quotationDAO.deleteQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy indicators.
     */
    private void createDummyIndicators() {
        List<Quotation> quotations = new ArrayList<>();

        this.appleQuotation2.setIndicator(this.fixtureHelper.getAppleQuotation2Indicator());
        this.fordQuotation1.setIndicator(this.fixtureHelper.getFordQuotation1Indicator());
        this.xleQuotation2.setIndicator(this.fixtureHelper.getXleQuotation2Indicator());
        this.xlbQuotation1.setIndicator(this.fixtureHelper.getXlbQuotation1Indicator());

        quotations.add(this.appleQuotation2);
        quotations.add(this.fordQuotation1);
        quotations.add(this.xleQuotation2);
        quotations.add(this.xlbQuotation1);
        quotations.addAll(this.fixtureHelper.getDenisonMinesQuotationsWithIndicators(this.denisonMinesStock,
                this.denisonMinesQuotations));

        try {
            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates RelativeStrengthData.
     */
    private void createRelativeStrengthData() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation2.setRelativeStrengthData(new RelativeStrengthData());
            this.appleQuotation2.getRelativeStrengthData().setRsNumber(71);
            quotations.add(this.appleQuotation2);

            this.fordQuotation1.setRelativeStrengthData(new RelativeStrengthData());
            this.fordQuotation1.getRelativeStrengthData().setRsNumber(45);
            quotations.add(this.fordQuotation1);

            this.xleQuotation2.setRelativeStrengthData(new RelativeStrengthData());
            this.xleQuotation2.getRelativeStrengthData().setRsNumber(71);
            quotations.add(this.xleQuotation2);

            this.xlbQuotation1.setRelativeStrengthData(new RelativeStrengthData());
            this.xlbQuotation1.getRelativeStrengthData().setRsNumber(71);
            quotations.add(this.xlbQuotation1);

            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates MovingAverageData.
     */
    private void createMovingAverageData() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation2.setMovingAverageData(new MovingAverageData());
            this.appleQuotation2.getMovingAverageData().setSma200(60);
            this.appleQuotation2.getMovingAverageData().setSma150((float) 63.45);
            this.appleQuotation2.getMovingAverageData().setSma50((float) 69.24);
            quotations.add(this.appleQuotation2);

            this.fordQuotation1.setMovingAverageData(new MovingAverageData());
            this.fordQuotation1.getMovingAverageData().setSma200((float) 16.36);
            this.fordQuotation1.getMovingAverageData().setSma150((float) 15.08);
            this.fordQuotation1.getMovingAverageData().setSma50((float) 13.07);
            quotations.add(this.fordQuotation1);

            this.xleQuotation2.setMovingAverageData(new MovingAverageData());
            this.xleQuotation2.getMovingAverageData().setSma200((float) 74.02);
            this.xleQuotation2.getMovingAverageData().setSma150((float) 76.84);
            this.xleQuotation2.getMovingAverageData().setSma50((float) 78.15);
            quotations.add(this.xleQuotation2);

            this.xlbQuotation1.setMovingAverageData(new MovingAverageData());
            this.xlbQuotation1.getMovingAverageData().setSma200((float) 79.83);
            this.xlbQuotation1.getMovingAverageData().setSma150((float) 78.64);
            this.xlbQuotation1.getMovingAverageData().setSma50((float) 74.01);
            quotations.add(this.xlbQuotation1);

            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotation with its corresponding Indicator and Instrument. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType
     * stock are requested.
     */
    public void testGetRecentQuotationsStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.ALL, InstrumentType.STOCK, null, null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if three quotations are returned.
        assertEquals(3, quotations.getQuotations().size());

        // Check if the correct quotations are returned.
        for (Quotation tempQuotation : quotations.getQuotations()) {
            if (tempQuotation.getId().equals(appleQuotation2.getId())) {
                assertEquals(this.appleQuotation2, tempQuotation);
            } else if (tempQuotation.getId().equals(this.fordQuotation1.getId())) {
                assertEquals(this.fordQuotation1, tempQuotation);
            } else if (tempQuotation.getId().equals(this.denisonMinesQuotations.get(0).getId())) {
                assertEquals(this.denisonMinesQuotations.get(0), tempQuotation);
            } else {
                fail("An unrelated Quotation has been returned.");
            }
        }
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotation with its corresponding Indicator and Instrument. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType ETF
     * are requested.
     */
    public void testGetRecentQuotationsETF() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.ALL, InstrumentType.ETF, null, null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if two quotations are returned.
        assertEquals(2, quotations.getQuotations().size());

        // Check if the correct quotations are returned.
        for (Quotation tempQuotation : quotations.getQuotations()) {
            if (tempQuotation.getId().equals(xleQuotation2.getId())) {
                assertEquals(this.xleQuotation2, tempQuotation);
            } else if (tempQuotation.getId().equals(this.xlbQuotation1.getId())) {
                assertEquals(this.xlbQuotation1, tempQuotation);
            } else {
                fail("An unrelated Quotation has been returned.");
            }
        }
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the Minervini Trend Template. Only those quotations
     * should be returned that have an Indicator associated with them. Only instruments of InstrumentType stock are
     * requested.
     */
    public void testGetQuotationsMinerviniTrendTemplateStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.MINERVINI_TREND_TEMPLATE, InstrumentType.STOCK, null,
                null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.appleQuotation2, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Volatility Contraction 10 Days" template. Only
     * those quotations should be returned that have an Indicator associated with them. Only instruments of
     * InstrumentType stock are requested.
     */
    public void testGetQuotationsVolatiltyContraction10DaysStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.VOLATILITY_CONTRACTION_10_DAYS, InstrumentType.STOCK,
                null, null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.fordQuotation1, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Breakout Candidates" template. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType
     * stock are requested.
     */
    public void testGetQuotationsBreakoutCandidatesStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.BREAKOUT_CANDIDATES, InstrumentType.STOCK, null, null,
                null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.fordQuotation1, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Up on Volume" template. Only those quotations
     * should be returned that have an Indicator associated with them. Only instruments of InstrumentType stock are
     * requested.
     */
    public void testGetQuotationsUpOnVolumeStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.UP_ON_VOLUME, InstrumentType.STOCK, null, null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.appleQuotation2, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Down on Volume" template. Only those quotations
     * should be returned that have an Indicator associated with them. Only instruments of InstrumentType stock are
     * requested.
     */
    public void testGetQuotationsDownOnVolumeStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.DOWN_ON_VOLUME, InstrumentType.STOCK, null, null,
                null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.fordQuotation1, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Near 52-week High" template. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType
     * stock are requested.
     */
    public void testGetQuotationsNear52WeekHighStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.NEAR_52_WEEK_HIGH, InstrumentType.STOCK, null, null,
                null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.appleQuotation2, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Near 52-week Low" template. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType
     * 'ETF' are requested.
     */
    public void testGetQuotationsNear52WeekLowETF() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation quotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.NEAR_52_WEEK_LOW, InstrumentType.ETF, null, null,
                null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        quotation = quotations.getQuotations().get(0);
        assertEquals(this.xlbQuotation1, quotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "3 Weeks Tight" template. Only those quotations
     * should be returned that have an Indicator associated with them. Only instruments of InstrumentType 'STOCK' are
     * requested.
     */
    public void testGetQuotations3WeeksTightStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation expectedQuotation, actualQuotation;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.THREE_WEEKS_TIGHT, InstrumentType.STOCK, null, null,
                null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        actualQuotation = quotations.getQuotations().get(0);
        expectedQuotation = this.denisonMinesQuotations.get(0);
        assertEquals(expectedQuotation, actualQuotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "3 Weeks Tight" template. Only those quotations
     * should be returned that have an Indicator associated with them. Only instruments of InstrumentType 'STOCK' are
     * requested. Test if the liquidity filter is correctly applied.
     */
    public void testGetQuotations3WeeksTightStockWithLiquidityFilter() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.THREE_WEEKS_TIGHT, InstrumentType.STOCK, null,
                Float.valueOf(2500000), null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if no Quotation is returned.
        assertEquals(0, quotations.getQuotations().size());
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "High Tight Flag" template. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType
     * 'STOCK' are requested.
     */
    public void testGetQuotationsHighTightFlagStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation expectedQuotation;
        Quotation actualQuotation;
        Quotation quotation;
        List<Quotation> modifiedQuotations = new ArrayList<Quotation>();

        // Modify the test data to achieve a high tight flag.
        quotations = new QuotationArray(this.denisonMinesQuotations);
        quotations.sortQuotationsByDate();

        // Assure a double in price vs. 14 weeks ago.
        quotation = quotations.getQuotations().get(1);
        quotation.setClose(new BigDecimal(5));
        modifiedQuotations.add(quotation);

        // Make a -20% setback on the most recent day.
        quotation = quotations.getQuotations().get(0);
        quotation.setClose(new BigDecimal(4));
        quotation.getIndicator().setDistanceTo52WeekHigh(-20);
        modifiedQuotations.add(quotation);

        // Persist the changes.
        try {
            quotationDAO.updateQuotations(modifiedQuotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.HIGH_TIGHT_FLAG, InstrumentType.STOCK, null, null,
                null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        actualQuotation = quotations.getQuotations().get(0);
        expectedQuotation = this.denisonMinesQuotations.get(0);
        assertEquals(expectedQuotation, actualQuotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotations that match the "Swing Trading Environment" template. Only those
     * quotations should be returned that have an Indicator associated with them. Only instruments of InstrumentType
     * 'STOCK' are requested.
     */
    public void testGetQuotationsSwingTradingEnvironmentStock() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation expectedQuotation;
        Quotation actualQuotation;
        Quotation quotation;
        List<Quotation> modifiedQuotations = new ArrayList<Quotation>();

        // Modify the test data to match the SWING_TRADING_ENVIRONMENT template.
        quotations = new QuotationArray(this.denisonMinesQuotations);
        quotations.sortQuotationsByDate();

        // Assure SMA(10) > SMA(20) and price above SMA(20)
        quotation = quotations.getQuotations().get(0);
        quotation.getMovingAverageData().setSma10(1.33f);
        quotation.getMovingAverageData().setSma20(1.32f);
        modifiedQuotations.add(quotation);

        // Assure SMA(10) and SMA(20) are rising
        quotation = quotations.getQuotations().get(1);
        quotation.getMovingAverageData().setSma10(1.32f);
        quotation.getMovingAverageData().setSma20(1.31f);
        modifiedQuotations.add(quotation);

        // Persist the changes.
        try {
            quotationDAO.updateQuotations(modifiedQuotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.SWING_TRADING_ENVIRONMENT, InstrumentType.STOCK, null,
                null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one Quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct Quotation is returned.
        actualQuotation = quotations.getQuotations().get(0);
        expectedQuotation = this.denisonMinesQuotations.get(0);
        assertEquals(expectedQuotation, actualQuotation);
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotation with its corresponding Indicator and Instrument. Only those
     * quotations should be returned that have an Indicator associated with them. Instruments of all types are
     * requested.
     */
    public void testGetRecentQuotationsAllTypes() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.ALL, null, null, null, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if five quotations are returned.
        assertEquals(5, quotations.getQuotations().size());

        // Check if the correct quotations are returned
        for (Quotation quotation : quotations.getQuotations()) {
            if (quotation.getId().equals(this.appleQuotation2.getId())) {
                assertEquals(this.appleQuotation2, quotation);
            } else if (quotation.getId().equals(this.fordQuotation1.getId())) {
                assertEquals(this.fordQuotation1, quotation);
            } else if (quotation.getId().equals(this.xleQuotation2.getId())) {
                assertEquals(this.xleQuotation2, quotation);
            } else if (quotation.getId().equals(this.xlbQuotation1.getId())) {
                assertEquals(this.xlbQuotation1, quotation);
            } else if (quotation.getId().equals(this.denisonMinesQuotations.get(0).getId())) {
                assertEquals(this.denisonMinesQuotations.get(0), quotation);
            }
        }
    }

    @Test
    /**
     * Tests the retrieval of the most recent quotation with its corresponding Indicator and Instrument. Only those
     * quotations should be returned that have an Indicator associated with them. Instruments are filtered by a minimum
     * amount of liquidity.
     */
    public void testGetRecentQuotationsLiquidity() {
        QuotationArray quotations;
        WebServiceResult getQuotationsResult;
        Quotation actualQuotation;
        final float minLiquidity = 900000000;

        // Get the quotations.
        QuotationService service = new QuotationService();
        getQuotationsResult = service.getQuotations(ScanTemplate.ALL, InstrumentType.STOCK, null, minLiquidity, null);
        quotations = (QuotationArray) getQuotationsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);

        // Check if one quotation is returned.
        assertEquals(1, quotations.getQuotations().size());

        // Check if the correct quotation is returned.
        actualQuotation = quotations.getQuotations().get(0);
        assertEquals(this.appleQuotation2, actualQuotation);
    }
}
