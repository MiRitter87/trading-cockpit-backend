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
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.MovingAverageData;
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
     * The StatisticCalculationController under test.
     */
    private StatisticCalculationController statisticCalculationController;

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
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        try {
            instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
            quotationDAO = DAOManager.getInstance().getQuotationDAO();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed once at the end of the test class.
     */
    @AfterAll
    public static void tearDownClass() {
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
        this.statisticCalculationController = new StatisticCalculationController();
        this.createTestData();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteTestData();
        this.statisticCalculationController = null;
    }

    /**
     * Initializes the database with the apple stock and its quotations.
     */
    private void createTestData() {
        this.createDummyInstruments();
        this.createDummyQuotations();
        this.createDummyMovingAverageData();
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
        final long volume1 = 6784544;
        final long volume2 = 4584544;
        final long volume3 = 3184544;
        final int days2 = 2;

        try {
            calendar.setTime(new Date());
            this.appleQuotation1 = new Quotation();
            this.appleQuotation1.setDate(calendar.getTime());
            this.appleQuotation1.setOpen(new BigDecimal("76.37"));
            this.appleQuotation1.setHigh(new BigDecimal("78.37"));
            this.appleQuotation1.setLow(new BigDecimal("75.37"));
            this.appleQuotation1.setClose(new BigDecimal("76.37"));
            this.appleQuotation1.setCurrency(Currency.USD);
            this.appleQuotation1.setVolume(volume1);
            this.appleQuotation1.setInstrument(this.appleStock);
            quotations.add(this.appleQuotation1);

            calendar.add(Calendar.DAY_OF_YEAR, -1);
            this.appleQuotation2 = new Quotation();
            this.appleQuotation2.setDate(calendar.getTime());
            this.appleQuotation2.setClose(new BigDecimal("79.14"));
            this.appleQuotation2.setCurrency(Currency.USD);
            this.appleQuotation2.setVolume(volume2);
            this.appleQuotation2.setInstrument(this.appleStock);
            quotations.add(this.appleQuotation2);

            calendar.add(Calendar.DAY_OF_YEAR, -days2);
            this.appleQuotation3 = new Quotation();
            this.appleQuotation3.setDate(calendar.getTime());
            this.appleQuotation3.setClose(new BigDecimal("81.23"));
            this.appleQuotation3.setCurrency(Currency.USD);
            this.appleQuotation3.setVolume(volume3);
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
        final long volume1 = 1234544;
        final long volume2 = 6664544;
        final long volume3 = 8764544;
        final int days2 = 2;

        try {
            calendar.setTime(new Date());
            this.microsoftQuotation1 = new Quotation();
            this.microsoftQuotation1.setDate(calendar.getTime());
            this.microsoftQuotation1.setOpen(new BigDecimal("254.71"));
            this.microsoftQuotation1.setHigh(new BigDecimal("254.88"));
            this.microsoftQuotation1.setLow(new BigDecimal("247.12"));
            this.microsoftQuotation1.setClose(new BigDecimal("254.72"));
            this.microsoftQuotation1.setCurrency(Currency.USD);
            this.microsoftQuotation1.setVolume(volume1);
            this.microsoftQuotation1.setInstrument(this.microsoftStock);
            quotations.add(this.microsoftQuotation1);

            calendar.add(Calendar.DAY_OF_YEAR, -1);
            this.microsoftQuotation2 = new Quotation();
            this.microsoftQuotation2.setDate(calendar.getTime());
            this.microsoftQuotation2.setClose(new BigDecimal("246.11"));
            this.microsoftQuotation2.setCurrency(Currency.USD);
            this.microsoftQuotation2.setVolume(volume2);
            this.microsoftQuotation2.setInstrument(this.microsoftStock);
            quotations.add(this.microsoftQuotation2);

            calendar.add(Calendar.DAY_OF_YEAR, -days2);
            this.microsoftQuotation3 = new Quotation();
            this.microsoftQuotation3.setDate(calendar.getTime());
            this.microsoftQuotation3.setClose(new BigDecimal("246.88"));
            this.microsoftQuotation3.setCurrency(Currency.USD);
            this.microsoftQuotation3.setVolume(volume3);
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
     * Initializes the database with dummy MovingAverageData.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void createDummyMovingAverageData() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation1.setMovingAverageData(new MovingAverageData());
            this.appleQuotation1.getMovingAverageData().setSma50(78.54f);
            this.appleQuotation1.getMovingAverageData().setSma200(75.34f);
            this.appleQuotation1.getMovingAverageData().setSma30Volume(5000000);
            quotations.add(this.appleQuotation1);

            this.appleQuotation2.setMovingAverageData(new MovingAverageData());
            this.appleQuotation2.getMovingAverageData().setSma50(80.54f);
            this.appleQuotation2.getMovingAverageData().setSma200(76.01f);
            this.appleQuotation2.getMovingAverageData().setSma30Volume(5000000);
            quotations.add(this.appleQuotation2);

            this.appleQuotation3.setMovingAverageData(new MovingAverageData());
            this.appleQuotation3.getMovingAverageData().setSma50(82.54f);
            quotations.add(this.appleQuotation3);

            this.microsoftQuotation1.setMovingAverageData(new MovingAverageData());
            this.microsoftQuotation1.getMovingAverageData().setSma50(247.54f);
            this.microsoftQuotation1.getMovingAverageData().setSma200(260.70f);
            this.microsoftQuotation1.getMovingAverageData().setSma30Volume(1200000);
            quotations.add(this.microsoftQuotation1);

            this.microsoftQuotation2.setMovingAverageData(new MovingAverageData());
            this.microsoftQuotation2.getMovingAverageData().setSma50(246.54f);
            this.microsoftQuotation2.getMovingAverageData().setSma200(250.60f);
            this.microsoftQuotation2.getMovingAverageData().setSma30Volume(1200000);
            quotations.add(this.microsoftQuotation2);

            this.microsoftQuotation3.setMovingAverageData(new MovingAverageData());
            this.microsoftQuotation3.getMovingAverageData().setSma50(245.54f);
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

    /**
     * Tests the calculation of all statistics. Results of day 1 are verified.
     */
    @Test
    public void testCalculateStatisticsDay1() {
        StatisticArray calculatedStatistics = new StatisticArray();
        List<Instrument> instruments;
        Statistic statistic;
        final int percent50 = 50;
        final int expectedStatistics = 2;

        try {
            instruments = instrumentDAO.getInstruments(InstrumentType.STOCK);
            calculatedStatistics
                    .setStatistics(this.statisticCalculationController.calculateStatistics(instruments, null));

            // Two statistics should exist.
            assertEquals(expectedStatistics, calculatedStatistics.getStatistics().size());

            // Check correct statistic of first day.
            statistic = calculatedStatistics.getStatistics().get(0);
            assertEquals(DateTools.getDateWithoutIntradayAttributes(this.appleQuotation1.getDate()).getTime(),
                    statistic.getDate().getTime());
            assertEquals(1, statistic.getNumberAdvance());
            assertEquals(1, statistic.getNumberDecline());
            assertEquals(0, statistic.getAdvanceDeclineNumber());
            assertEquals(1, statistic.getNumberAboveSma50());
            assertEquals(1, statistic.getNumberAtOrBelowSma50());
            assertEquals(percent50, statistic.getPercentAboveSma50());
            assertEquals(1, statistic.getNumberAboveSma200());
            assertEquals(1, statistic.getNumberAtOrBelowSma200());
            assertEquals(percent50, statistic.getPercentAboveSma200());
            assertEquals(0, statistic.getNumberRitterMarketTrend());
            assertEquals(1, statistic.getNumberUpOnVolume());
            assertEquals(1, statistic.getNumberDownOnVolume());
            assertEquals(1, statistic.getNumberBearishReversal());
            assertEquals(1, statistic.getNumberBullishReversal());
            assertEquals(0, statistic.getNumberChurning());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the calculation of all statistics. Results of day 2 are verified.
     */
    @Test
    public void testCalculateStatisticsDay2() {
        StatisticArray calculatedStatistics = new StatisticArray();
        List<Instrument> instruments;
        Statistic statistic;
        final int num2 = 2;
        final int percent50 = 50;

        try {
            instruments = instrumentDAO.getInstruments(InstrumentType.STOCK);
            calculatedStatistics
                    .setStatistics(this.statisticCalculationController.calculateStatistics(instruments, null));

            // Two statistics should exist.
            assertEquals(num2, calculatedStatistics.getStatistics().size());

            // Check correct statistic of second day.
            statistic = calculatedStatistics.getStatistics().get(1);
            assertEquals(DateTools.getDateWithoutIntradayAttributes(this.appleQuotation2.getDate()).getTime(),
                    statistic.getDate().getTime());
            assertEquals(0, statistic.getNumberAdvance());
            assertEquals(num2, statistic.getNumberDecline());
            assertEquals(-num2, statistic.getAdvanceDeclineNumber());
            assertEquals(0, statistic.getNumberAboveSma50());
            assertEquals(num2, statistic.getNumberAtOrBelowSma50());
            assertEquals(0, statistic.getPercentAboveSma50());
            assertEquals(1, statistic.getNumberAboveSma200());
            assertEquals(1, statistic.getNumberAtOrBelowSma200());
            assertEquals(percent50, statistic.getPercentAboveSma200());
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

    /**
     * Tests the calculation of a single statistic.
     */
    @Test
    public void testCalculateOneStatistic() {
        StatisticArray calculatedStatistics = new StatisticArray();
        List<Instrument> instruments;
        Statistic statistic;
        final int requestedStatistics = 1;
        final int percent50 = 50;

        try {
            instruments = instrumentDAO.getInstruments(InstrumentType.STOCK);
            calculatedStatistics.setStatistics(
                    this.statisticCalculationController.calculateStatistics(instruments, requestedStatistics));

            // One statistics should exist.
            assertEquals(1, calculatedStatistics.getStatistics().size());

            // Check correct statistic of first day.
            statistic = calculatedStatistics.getStatistics().get(0);
            assertEquals(DateTools.getDateWithoutIntradayAttributes(this.appleQuotation1.getDate()).getTime(),
                    statistic.getDate().getTime());
            assertEquals(1, statistic.getNumberAdvance());
            assertEquals(1, statistic.getNumberDecline());
            assertEquals(0, statistic.getAdvanceDeclineNumber());
            assertEquals(1, statistic.getNumberAboveSma50());
            assertEquals(1, statistic.getNumberAtOrBelowSma50());
            assertEquals(percent50, statistic.getPercentAboveSma50());
            assertEquals(1, statistic.getNumberAboveSma200());
            assertEquals(1, statistic.getNumberAtOrBelowSma200());
            assertEquals(percent50, statistic.getPercentAboveSma200());
            assertEquals(0, statistic.getNumberRitterMarketTrend());
            assertEquals(1, statistic.getNumberUpOnVolume());
            assertEquals(1, statistic.getNumberDownOnVolume());
            assertEquals(1, statistic.getNumberBearishReversal());
            assertEquals(1, statistic.getNumberBullishReversal());
            assertEquals(0, statistic.getNumberChurning());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
