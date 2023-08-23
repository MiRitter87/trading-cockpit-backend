package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.IndicatorCalculator;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
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
     * DAO to access quotation data from Yahoo.
     */
    private static QuotationProviderDAO quotationProviderYahooDAO;

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

    /**
     * The Indicator the Apple stock Quotation 2.
     */
    private Indicator appleQuotation2Indicator;

    /**
     * The Indicator of the Ford stock Quotation 1.
     */
    private Indicator fordQuotation1Indicator;

    /**
     * The Indicator of the XLE ETF Quotation 2.
     */
    private Indicator xleQuotation2Indicator;

    /**
     * The Indicator of the XLB ETF Quotation 1.
     */
    private Indicator xlbQuotation1Indicator;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
        quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
    }

    @AfterAll
    /**
     * Tasks to be performed once at end of test class.
     */
    public static void tearDownClass() {
        try {
            DAOManager.getInstance().close();
            quotationProviderYahooDAO = null;
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
        this.createDummyIndicators();
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
        this.appleStock = this.getAppleStock();
        this.microsoftStock = this.getMicrosoftStock();
        this.fordStock = this.getFordStock();
        this.denisonMinesStock = this.getDenisonMinesStock();
        this.xleETF = this.getXleEtf();
        this.xlbETF = this.getXlbEtf();
        this.xlfETF = this.getXlfEtf();

        try {
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Gets the Instrument of the Apple stock.
     *
     * @return The Instrument of the Apple stock.
     */
    private Instrument getAppleStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Microsoft stock.
     *
     * @return The Instrument of the Microsoft stock.
     */
    private Instrument getMicrosoftStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("MSFT");
        instrument.setName("Microsoft");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Ford stock.
     *
     * @return The Instrument of the Ford stock.
     */
    private Instrument getFordStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("F");
        instrument.setName("Ford Motor Company");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Denison Mines stock.
     *
     * @return The Instrument of the Denison Mines stock.
     */
    private Instrument getDenisonMinesStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("DML");
        instrument.setStockExchange(StockExchange.TSX);
        instrument.setType(InstrumentType.STOCK);
        instrument.setName("Denison Mines");

        return instrument;
    }

    /**
     * Gets the Instrument of the XLE ETF.
     *
     * @return The Instrument of the XLE ETF.
     */
    private Instrument getXleEtf() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLE");
        instrument.setName("Energy Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);

        return instrument;
    }

    /**
     * Gets the Instrument of the XLB ETF.
     *
     * @return The Instrument of the XLB ETF.
     */
    private Instrument getXlbEtf() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLB");
        instrument.setName("Materials Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);

        return instrument;
    }

    /**
     * Gets the Instrument of the XLF ETF.
     *
     * @return The Instrument of the XLF ETF.
     */
    private Instrument getXlfEtf() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLF");
        instrument.setName("Financial Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);

        return instrument;
    }

    /**
     * Initializes the database with dummy quotations.
     */
    private void createDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        this.appleQuotation1 = this.getAppleQuotation1();
        this.appleQuotation2 = this.getAppleQuotation2();
        this.microsoftQuotation1 = this.getMicrosoftQuotation1();
        this.fordQuotation1 = this.getFordQuotation1();
        this.xleQuotation1 = this.getXleQuotation1();
        this.xleQuotation2 = this.getXleQuotation2();
        this.xlbQuotation1 = this.getXlbQuotation1();
        this.xlfQuotation1 = this.getXlfQuotation1();
        this.denisonMinesQuotations = this.getDenisonMinesQuotationsWithoutIndicators();

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
     * Gets the Quotation 1 of the Apple stock.
     *
     * @return The Quotation 1 of the Apple stock.
     */
    private Quotation getAppleQuotation1() {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(78.54));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(28973654);
        quotation.setInstrument(this.appleStock);

        return quotation;
    }

    /**
     * Gets the Quotation 2 of the Apple stock.
     *
     * @return The Quotation 2 of the Apple stock.
     */
    private Quotation getAppleQuotation2() {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(77.52));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(12373654);
        quotation.setInstrument(this.appleStock);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the Microsoft stock.
     *
     * @return The Quotation 1 of the Microsoft stock.
     */
    private Quotation getMicrosoftQuotation1() {
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        quotation.setClose(BigDecimal.valueOf(124.07));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(13973124);
        quotation.setInstrument(this.microsoftStock);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the Ford stock.
     *
     * @return The Quotation 1 of the Ford stock.
     */
    private Quotation getFordQuotation1() {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(15.88));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(48600000);
        quotation.setInstrument(this.fordStock);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the XLE ETF.
     *
     * @return The Quotation 1 of the XLE ETF.
     */
    private Quotation getXleQuotation1() {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(81.28));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(18994000);
        quotation.setInstrument(this.xleETF);

        return quotation;
    }

    /**
     * Gets the Quotation 2 of the XLE ETF.
     *
     * @return The Quotation 2 of the XLE ETF.
     */
    private Quotation getXleQuotation2() {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(81.99));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(25187000);
        quotation.setInstrument(this.xleETF);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the XLB ETF.
     *
     * @return The Quotation 1 of the XLB ETF.
     */
    private Quotation getXlbQuotation1() {
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        quotation.setClose(BigDecimal.valueOf(71.25));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(79794000);
        quotation.setInstrument(this.xlbETF);

        return quotation;
    }

    /**
     * Gets the Quotation 1 of the XLF ETF.
     *
     * @return The Quotation 1 of the XLF ETF.
     */
    private Quotation getXlfQuotation1() {
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);

        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(32.30));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(48148000);
        quotation.setInstrument(this.xlfETF);

        return quotation;
    }

    /**
     * Gets quotations of the Denison Mines stock.
     *
     * @return A List of quotations.
     */
    private List<Quotation> getDenisonMinesQuotationsWithoutIndicators() {
        List<Quotation> quotationsWithoutIndicators = new ArrayList<>();

        try {
            quotationsWithoutIndicators.addAll(
                    quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));

            for (Quotation tempQuotation : quotationsWithoutIndicators) {
                tempQuotation.setInstrument(this.denisonMinesStock);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }

        return quotationsWithoutIndicators;
    }

    /**
     * Initializes the database with dummy indicators.
     */
    private void createDummyIndicators() {
        List<Quotation> quotations = new ArrayList<>();

        this.initAppleQuotation2Indicator();
        this.appleQuotation2.setIndicator(this.appleQuotation2Indicator);

        this.initFordQuotation1Indicator();
        this.fordQuotation1.setIndicator(this.fordQuotation1Indicator);

        this.initXleQuotation2Indicator();
        this.xleQuotation2.setIndicator(this.xleQuotation2Indicator);

        this.initXlbQuotation1Indicator();
        this.xlbQuotation1.setIndicator(this.xlbQuotation1Indicator);

        quotations.add(this.appleQuotation2);
        quotations.add(this.fordQuotation1);
        quotations.add(this.xleQuotation2);
        quotations.add(this.xlbQuotation1);
        quotations.addAll(this.getDenisonMinesQuotationsWithIndicators());

        try {
            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the appleQuotation2Indicator.
     */
    private void initAppleQuotation2Indicator() {
        this.appleQuotation2Indicator = new Indicator();
        this.appleQuotation2Indicator.setStage(2);
        this.appleQuotation2Indicator.setSma200(60);
        this.appleQuotation2Indicator.setSma150((float) 63.45);
        this.appleQuotation2Indicator.setSma50((float) 69.24);
        this.appleQuotation2Indicator.setRsNumber(71);
        this.appleQuotation2Indicator.setPerformance5Days((float) 12.44);
        this.appleQuotation2Indicator.setDistanceTo52WeekHigh((float) -4.4);
        this.appleQuotation2Indicator.setDistanceTo52WeekLow((float) 78.81);
        this.appleQuotation2Indicator.setBollingerBandWidth((float) 8.71);
        this.appleQuotation2Indicator.setVolumeDifferential5Days((float) 47.18);
        this.appleQuotation2Indicator.setVolumeDifferential10Days((float) 19.34);
        this.appleQuotation2Indicator.setBaseLengthWeeks(32);
    }

    /**
     * Initializes the fordQuotation1Indicator.
     */
    private void initFordQuotation1Indicator() {
        this.fordQuotation1Indicator = new Indicator();
        this.fordQuotation1Indicator.setStage(3);
        this.fordQuotation1Indicator.setSma200((float) 16.36);
        this.fordQuotation1Indicator.setSma150((float) 15.08);
        this.fordQuotation1Indicator.setSma50((float) 13.07);
        this.fordQuotation1Indicator.setRsNumber(45);
        this.fordQuotation1Indicator.setPerformance5Days((float) -10.21);
        this.fordQuotation1Indicator.setDistanceTo52WeekHigh((float) -9.41);
        this.fordQuotation1Indicator.setDistanceTo52WeekLow((float) 48.81);
        this.fordQuotation1Indicator.setBollingerBandWidth((float) 4.11);
        this.fordQuotation1Indicator.setVolumeDifferential5Days((float) 25.55);
        this.fordQuotation1Indicator.setVolumeDifferential10Days((float) -9.67);
        this.fordQuotation1Indicator.setBaseLengthWeeks(3);
    }

    /**
     * Initializes the xleQuotation2Indicator.
     */
    private void initXleQuotation2Indicator() {
        this.xleQuotation2Indicator = new Indicator();
        this.xleQuotation2Indicator.setStage(2);
        this.xleQuotation2Indicator.setSma200((float) 74.02);
        this.xleQuotation2Indicator.setSma150((float) 76.84);
        this.xleQuotation2Indicator.setSma50((float) 78.15);
        this.xleQuotation2Indicator.setRsNumber(71);
        this.xleQuotation2Indicator.setPerformance5Days((float) 3.17);
        this.xleQuotation2Indicator.setDistanceTo52WeekHigh((float) -21.4);
        this.xleQuotation2Indicator.setDistanceTo52WeekLow((float) 78.81);
        this.xleQuotation2Indicator.setBollingerBandWidth((float) 8.71);
        this.xleQuotation2Indicator.setVolumeDifferential5Days((float) 12.12);
        this.xleQuotation2Indicator.setVolumeDifferential10Days((float) 19.34);
        this.xleQuotation2Indicator.setBaseLengthWeeks(32);
    }

    /**
     * Initializes the xlbQuotation1Indicator.
     */
    private void initXlbQuotation1Indicator() {
        this.xlbQuotation1Indicator = new Indicator();
        this.xlbQuotation1Indicator.setStage(4);
        this.xlbQuotation1Indicator.setSma200((float) 79.83);
        this.xlbQuotation1Indicator.setSma150((float) 78.64);
        this.xlbQuotation1Indicator.setSma50((float) 74.01);
        this.xlbQuotation1Indicator.setRsNumber(71);
        this.xlbQuotation1Indicator.setPerformance5Days((float) -6.70);
        this.xlbQuotation1Indicator.setDistanceTo52WeekHigh((float) -9.41);
        this.xlbQuotation1Indicator.setDistanceTo52WeekLow((float) 0.81);
        this.xlbQuotation1Indicator.setBollingerBandWidth((float) 4.11);
        this.xlbQuotation1Indicator.setVolumeDifferential5Days((float) 21.89);
        this.xlbQuotation1Indicator.setVolumeDifferential10Days((float) -9.67);
        this.xlbQuotation1Indicator.setBaseLengthWeeks(3);
    }

    /**
     * Calculates indicators and adds them to the DML quotations.
     */
    private List<Quotation> getDenisonMinesQuotationsWithIndicators() {
        Quotation quotation;
        List<Quotation> quotationsWithIndicators = new ArrayList<>();
        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();

        // Quotations of Instrument are needed for indicator calculation.
        this.denisonMinesStock.setQuotations(this.denisonMinesQuotations);

        for (int i = 0; i < this.denisonMinesQuotations.size(); i++) {
            quotation = this.denisonMinesQuotations.get(i);

            if (i == 0)
                quotation = indicatorCalculator.calculateIndicators(this.denisonMinesStock, quotation, true);
            else
                quotation = indicatorCalculator.calculateIndicators(this.denisonMinesStock, quotation, false);

            quotationsWithIndicators.add(quotation);
        }

        return quotationsWithIndicators;
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
        getQuotationsResult = service.getQuotations(ScanTemplate.ALL, InstrumentType.STOCK, null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.ALL, InstrumentType.ETF, null, null);
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
                null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.BREAKOUT_CANDIDATES, InstrumentType.STOCK, null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.UP_ON_VOLUME, InstrumentType.STOCK, null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.DOWN_ON_VOLUME, InstrumentType.STOCK, null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.NEAR_52_WEEK_HIGH, InstrumentType.STOCK, null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.NEAR_52_WEEK_LOW, InstrumentType.ETF, null, null);
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
        getQuotationsResult = service.getQuotations(ScanTemplate.THREE_WEEKS_TIGHT, InstrumentType.STOCK, null, null);
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
                Float.valueOf(2500000));
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
        getQuotationsResult = service.getQuotations(ScanTemplate.HIGH_TIGHT_FLAG, InstrumentType.STOCK, null, null);
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
        quotation.getIndicator().setSma10(1.33f);
        quotation.getIndicator().setSma20(1.32f);
        modifiedQuotations.add(quotation);

        // Assure SMA(10) and SMA(20) are rising
        quotation = quotations.getQuotations().get(1);
        quotation.getIndicator().setSma10(1.32f);
        quotation.getIndicator().setSma20(1.31f);
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
}
