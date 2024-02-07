package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import backend.dao.DAOManager;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;
import backend.model.statistic.StatisticArray;
import backend.tools.DateTools;

/**
 * Tests the StatisticController.
 *
 * @author Michael
 */
public class StatisticCalculationControllerTest {
    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

    /**
     * The first Quotation of the Apple stock.
     */
    private Quotation appleQuotation1;

    /**
     * The second Quotation of the Apple stock.
     */
    private Quotation appleQuotation2;

    /**
     * The third Quotation of the Apple stock.
     */
    private Quotation appleQuotation3;

    /**
     * The first Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation1;

    /**
     * The second Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation2;

    /**
     * The third Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation3;

    /**
     * The Indicator of the first Quotation of the Apple stock.
     */
    private Indicator appleQuotation1Indicator;

    /**
     * The Indicator of the second Quotation of the Apple stock.
     */
    private Indicator appleQuotation2Indicator;

    /**
     * The Indicator of the third Quotation of the Apple stock.
     */
    private Indicator appleQuotation3Indicator;

    /**
     * The Indicator of the first Quotation of the Microsoft stock.
     */
    private Indicator microsoftQuotation1Indicator;

    /**
     * The Indicator of the second Quotation of the Microsoft stock.
     */
    private Indicator microsoftQuotation2Indicator;

    /**
     * The Indicator of the third Quotation of the Microsoft stock.
     */
    private Indicator microsoftQuotation3Indicator;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        try {
            instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
            quotationDAO = DAOManager.getInstance().getQuotationDAO();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    /**
     * Tasks to be performed once at the end of the test class.
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
        this.createTestData();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteTestData();
    }

    /**
     * Initializes the database with the apple stock and its quotations.
     */
    private void createTestData() {
        this.createDummyInstruments();
        this.createDummyQuotations();
        this.createDummyIndicators();
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        this.appleStock = new Instrument();
        this.microsoftStock = new Instrument();

        try {
            this.appleStock.setSymbol("AAPL");
            this.appleStock.setName("Apple");
            this.appleStock.setStockExchange(StockExchange.NDQ);
            this.appleStock.setType(InstrumentType.STOCK);
            instrumentDAO.insertInstrument(this.appleStock);

            this.microsoftStock.setSymbol("MSFT");
            this.microsoftStock.setName("Microsoft");
            this.microsoftStock.setStockExchange(StockExchange.NDQ);
            this.microsoftStock.setType(InstrumentType.STOCK);
            instrumentDAO.insertInstrument(this.microsoftStock);
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy quotations.
     */
    private void createDummyQuotations() {
        this.createDummyQuotationsApple();
        this.createDummyQuotationsMicrosoft();
    }

    /**
     * Initializes the database with dummy quotations for the Apple stock.
     */
    private void createDummyQuotationsApple() {
        Calendar calendar = Calendar.getInstance();
        List<Quotation> quotations = new ArrayList<>();

        try {
            calendar.setTime(new Date());
            this.appleQuotation1 = new Quotation();
            this.appleQuotation1.setDate(calendar.getTime());
            this.appleQuotation1.setOpen(BigDecimal.valueOf(76.37));
            this.appleQuotation1.setHigh(BigDecimal.valueOf(78.37));
            this.appleQuotation1.setLow(BigDecimal.valueOf(75.37));
            this.appleQuotation1.setClose(BigDecimal.valueOf(76.37));
            this.appleQuotation1.setCurrency(Currency.USD);
            this.appleQuotation1.setVolume(6784544);
            this.appleQuotation1.setInstrument(this.appleStock);
            quotations.add(this.appleQuotation1);

            calendar.add(Calendar.DAY_OF_YEAR, -1);
            this.appleQuotation2 = new Quotation();
            this.appleQuotation2.setDate(calendar.getTime());
            this.appleQuotation2.setClose(BigDecimal.valueOf(79.14));
            this.appleQuotation2.setCurrency(Currency.USD);
            this.appleQuotation2.setVolume(4584544);
            this.appleQuotation2.setInstrument(this.appleStock);
            quotations.add(this.appleQuotation2);

            calendar.add(Calendar.DAY_OF_YEAR, -2);
            this.appleQuotation3 = new Quotation();
            this.appleQuotation3.setDate(calendar.getTime());
            this.appleQuotation3.setClose(BigDecimal.valueOf(81.23));
            this.appleQuotation3.setCurrency(Currency.USD);
            this.appleQuotation3.setVolume(3184544);
            this.appleQuotation3.setInstrument(this.appleStock);
            quotations.add(this.appleQuotation3);

            quotationDAO.insertQuotations(quotations);
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy quotations for the Microsoft stock.
     */
    private void createDummyQuotationsMicrosoft() {
        Calendar calendar = Calendar.getInstance();
        List<Quotation> quotations = new ArrayList<>();

        try {
            calendar.setTime(new Date());
            this.microsoftQuotation1 = new Quotation();
            this.microsoftQuotation1.setDate(calendar.getTime());
            this.microsoftQuotation1.setOpen(BigDecimal.valueOf(254.71));
            this.microsoftQuotation1.setHigh(BigDecimal.valueOf(254.88));
            this.microsoftQuotation1.setLow(BigDecimal.valueOf(247.12));
            this.microsoftQuotation1.setClose(BigDecimal.valueOf(254.72));
            this.microsoftQuotation1.setCurrency(Currency.USD);
            this.microsoftQuotation1.setVolume(1234544);
            this.microsoftQuotation1.setInstrument(this.microsoftStock);
            quotations.add(this.microsoftQuotation1);

            calendar.add(Calendar.DAY_OF_YEAR, -1);
            this.microsoftQuotation2 = new Quotation();
            this.microsoftQuotation2.setDate(calendar.getTime());
            this.microsoftQuotation2.setClose(BigDecimal.valueOf(246.11));
            this.microsoftQuotation2.setCurrency(Currency.USD);
            this.microsoftQuotation2.setVolume(6664544);
            this.microsoftQuotation2.setInstrument(this.microsoftStock);
            quotations.add(this.microsoftQuotation2);

            calendar.add(Calendar.DAY_OF_YEAR, -2);
            this.microsoftQuotation3 = new Quotation();
            this.microsoftQuotation3.setDate(calendar.getTime());
            this.microsoftQuotation3.setClose(BigDecimal.valueOf(246.88));
            this.microsoftQuotation3.setCurrency(Currency.USD);
            this.microsoftQuotation3.setVolume(8764544);
            this.microsoftQuotation3.setInstrument(this.microsoftStock);
            quotations.add(this.microsoftQuotation3);

            quotationDAO.insertQuotations(quotations);
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy indicators.
     */
    private void createDummyIndicators() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation1Indicator = new Indicator(false, true);
            this.appleQuotation1Indicator.getMovingAverageData().setSma50((float) 78.54);
            this.appleQuotation1Indicator.getMovingAverageData().setSma200((float) 75.34);
            this.appleQuotation1Indicator.getMovingAverageData().setSma30Volume(5000000);
            this.appleQuotation1.setIndicator(this.appleQuotation1Indicator);
            quotations.add(this.appleQuotation1);

            this.appleQuotation2Indicator = new Indicator(false, true);
            this.appleQuotation2Indicator.getMovingAverageData().setSma50((float) 80.54);
            this.appleQuotation2Indicator.getMovingAverageData().setSma200((float) 76.01);
            this.appleQuotation2Indicator.getMovingAverageData().setSma30Volume(5000000);
            this.appleQuotation2.setIndicator(this.appleQuotation2Indicator);
            quotations.add(this.appleQuotation2);

            this.appleQuotation3Indicator = new Indicator(false, true);
            this.appleQuotation3Indicator.getMovingAverageData().setSma50((float) 82.54);
            this.appleQuotation3.setIndicator(this.appleQuotation3Indicator);
            quotations.add(this.appleQuotation3);

            this.microsoftQuotation1Indicator = new Indicator(false, true);
            this.microsoftQuotation1Indicator.getMovingAverageData().setSma50((float) 247.54);
            this.microsoftQuotation1Indicator.getMovingAverageData().setSma200((float) 260.70);
            this.microsoftQuotation1Indicator.getMovingAverageData().setSma30Volume(1200000);
            this.microsoftQuotation1.setIndicator(this.microsoftQuotation1Indicator);
            quotations.add(this.microsoftQuotation1);

            this.microsoftQuotation2Indicator = new Indicator(false, true);
            this.microsoftQuotation2Indicator.getMovingAverageData().setSma50((float) 246.54);
            this.microsoftQuotation2Indicator.getMovingAverageData().setSma200((float) 250.60);
            this.microsoftQuotation2Indicator.getMovingAverageData().setSma30Volume(1200000);
            this.microsoftQuotation2.setIndicator(this.microsoftQuotation2Indicator);
            quotations.add(this.microsoftQuotation2);

            this.microsoftQuotation3Indicator = new Indicator(false, true);
            this.microsoftQuotation3Indicator.getMovingAverageData().setSma50((float) 245.54);
            this.microsoftQuotation3.setIndicator(this.microsoftQuotation3Indicator);
            quotations.add(this.microsoftQuotation3);

            quotationDAO.updateQuotations(quotations);
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the stocks and their quotations from the database.
     */
    private void deleteTestData() {
        try {
            List<Quotation> quotations = new ArrayList<>();

            quotations.add(this.microsoftQuotation3);
            quotations.add(this.microsoftQuotation2);
            quotations.add(this.microsoftQuotation1);
            quotations.add(this.appleQuotation3);
            quotations.add(this.appleQuotation2);
            quotations.add(this.appleQuotation1);

            quotationDAO.deleteQuotations(quotations);
            instrumentDAO.deleteInstrument(this.microsoftStock);
            instrumentDAO.deleteInstrument(this.appleStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the calculation of statistics.
     */
    public void testCalculateStatistics() {
        StatisticArray calculatedStatistics = new StatisticArray();
        StatisticCalculationController statisticController = new StatisticCalculationController();
        List<Instrument> instruments;
        Statistic statistic;

        try {
            instruments = instrumentDAO.getInstruments(InstrumentType.STOCK);
            calculatedStatistics.setStatistics(statisticController.calculateStatistics(instruments));

            // Two statistics should exist.
            assertEquals(2, calculatedStatistics.getStatistics().size());

            // Check correct statistic of first day.
            statistic = calculatedStatistics.getStatistics().get(0);
            assertEquals(DateTools.getDateWithoutIntradayAttributes(this.appleQuotation1.getDate()).getTime(),
                    statistic.getDate().getTime());
            assertEquals(1, statistic.getNumberAdvance());
            assertEquals(1, statistic.getNumberDecline());
            assertEquals(0, statistic.getAdvanceDeclineNumber());
            assertEquals(1, statistic.getNumberAboveSma50());
            assertEquals(1, statistic.getNumberAtOrBelowSma50());
            assertEquals(50, statistic.getPercentAboveSma50());
            assertEquals(1, statistic.getNumberAboveSma200());
            assertEquals(1, statistic.getNumberAtOrBelowSma200());
            assertEquals(50, statistic.getPercentAboveSma200());
            assertEquals(0, statistic.getNumberRitterMarketTrend());
            assertEquals(1, statistic.getNumberUpOnVolume());
            assertEquals(1, statistic.getNumberDownOnVolume());
            assertEquals(1, statistic.getNumberBearishReversal());
            assertEquals(1, statistic.getNumberBullishReversal());
            assertEquals(0, statistic.getNumberChurning());

            // Check correct statistic of second day.
            statistic = calculatedStatistics.getStatistics().get(1);
            assertEquals(DateTools.getDateWithoutIntradayAttributes(this.appleQuotation2.getDate()).getTime(),
                    statistic.getDate().getTime());
            assertEquals(0, statistic.getNumberAdvance());
            assertEquals(2, statistic.getNumberDecline());
            assertEquals(-2, statistic.getAdvanceDeclineNumber());
            assertEquals(0, statistic.getNumberAboveSma50());
            assertEquals(2, statistic.getNumberAtOrBelowSma50());
            assertEquals(0, statistic.getPercentAboveSma50());
            assertEquals(1, statistic.getNumberAboveSma200());
            assertEquals(1, statistic.getNumberAtOrBelowSma200());
            assertEquals(50, statistic.getPercentAboveSma200());
            assertEquals(0, statistic.getNumberRitterMarketTrend());
            assertEquals(0, statistic.getNumberUpOnVolume());
            assertEquals(0, statistic.getNumberDownOnVolume());
            assertEquals(0, statistic.getNumberBearishReversal());
            assertEquals(0, statistic.getNumberBullishReversal());
            assertEquals(1, statistic.getNumberChurning());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
