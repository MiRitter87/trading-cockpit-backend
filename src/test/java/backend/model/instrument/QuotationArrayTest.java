package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderYahooDAOStub;
import backend.model.Currency;
import backend.model.StockExchange;

/**
 * Tests the QuotationArray model.
 *
 * @author Michael
 */
public class QuotationArrayTest {
    /**
     * The QuotationArray under test.
     */
    private QuotationArray quotationArray;

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
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        List<Quotation> quotations = new ArrayList<>();
        Instrument dmlStock = new Instrument();

        dmlStock.setSymbol("DML");
        dmlStock.setStockExchange(StockExchange.TSX);
        dmlStock.setType(InstrumentType.STOCK);

        try {
            quotations.addAll(quotationProviderYahooDAO.getQuotationHistory(dmlStock, 1));
            this.quotationArray = new QuotationArray(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.quotationArray = null;
    }

    /**
     * Gets the expected weekly Quotation 1.
     *
     * @return Quotation 1.
     */
    private Quotation getQuotation1() {
        Quotation quotation1 = new Quotation();
        final long volume1 = 8146700;
        final int index4 = 4;

        quotation1.setDate(this.quotationArray.getQuotations().get(index4).getDate());
        quotation1.setCurrency(Currency.CAD);
        quotation1.setVolume(volume1);
        quotation1.setOpen(new BigDecimal("1.38"));
        quotation1.setHigh(new BigDecimal("1.54"));
        quotation1.setLow(new BigDecimal("1.35"));
        quotation1.setClose(new BigDecimal("1.36"));

        return quotation1;
    }

    /**
     * Gets the expected weekly Quotation 2.
     *
     * @return Quotation 2.
     */
    private Quotation getQuotation2() {
        Quotation quotation2 = new Quotation();
        final long volume2 = 6258300;
        final int index9 = 9;

        quotation2.setDate(this.quotationArray.getQuotations().get(index9).getDate());
        quotation2.setCurrency(Currency.CAD);
        quotation2.setVolume(volume2);
        quotation2.setOpen(new BigDecimal("1.34"));
        quotation2.setHigh(new BigDecimal("1.38"));
        quotation2.setLow(new BigDecimal("1.24"));
        quotation2.setClose(new BigDecimal("1.36"));

        return quotation2;
    }

    /**
     * Gets the expected weekly Quotation 53.
     *
     * @return Quotation 53.
     */
    private Quotation getQuotation53() {
        Quotation quotation53 = new Quotation();
        final long volume53 = 1886800;
        final int index251 = 251;

        quotation53.setDate(this.quotationArray.getQuotations().get(index251).getDate());
        quotation53.setCurrency(Currency.CAD);
        quotation53.setVolume(volume53);
        quotation53.setOpen(new BigDecimal("1.35"));
        quotation53.setHigh(new BigDecimal("1.35"));
        quotation53.setLow(new BigDecimal("1.27"));
        quotation53.setClose(new BigDecimal("1.29"));

        return quotation53;
    }

    /**
     * Tests the determination of the age of the newest Quotation within the QuotationArray.
     */
    @Test
    public void testGetAgeOfNewestQuotationInDays() {
        Date currentDate = new Date();
        LocalDate currentDateLocal = LocalDate.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
        LocalDate newestQuotationDateLocal;
        long actualNumberDays;
        long expectedNumberDays;
        Quotation newestQuotation;

        this.quotationArray.sortQuotationsByDate();
        newestQuotation = this.quotationArray.getQuotations().get(0);

        newestQuotationDateLocal = LocalDate.ofInstant(newestQuotation.getDate().toInstant(), ZoneId.systemDefault());
        expectedNumberDays = ChronoUnit.DAYS.between(newestQuotationDateLocal, currentDateLocal);

        actualNumberDays = this.quotationArray.getAgeOfNewestQuotationInDays();

        assertEquals(expectedNumberDays, actualNumberDays);
    }

    /**
     * Tests the retrieval of quotations that are older than the given Quotation but still on the same day.
     */
    @Test
    public void testGetOlderQuotationsOfSameDay() {
        List<Quotation> olderQuotationsSameDay;
        Quotation olderQuotationOfSameDay;
        Quotation addedQuotation;
        Quotation quotation;
        Calendar calendar = Calendar.getInstance();
        final long volume = 150000;

        // The newest Quotation of a day.
        quotation = this.quotationArray.getQuotations().get(0);
        calendar.setTime(quotation.getDate());
        calendar.add(Calendar.MINUTE, -1);

        // Add an additional Quotation to the array that is older but at the same day.
        addedQuotation = new Quotation();
        addedQuotation.setDate(calendar.getTime());
        addedQuotation.setCurrency(quotation.getCurrency());
        addedQuotation.setVolume(volume);
        addedQuotation.setOpen(new BigDecimal("1.95"));
        addedQuotation.setHigh(new BigDecimal("2.05"));
        addedQuotation.setLow(new BigDecimal("1.95"));
        addedQuotation.setClose(new BigDecimal("2.00"));
        this.quotationArray.getQuotations().add(addedQuotation);

        olderQuotationsSameDay = this.quotationArray.getOlderQuotationsOfSameDay(quotation.getDate());

        assertEquals(1, olderQuotationsSameDay.size());

        olderQuotationOfSameDay = olderQuotationsSameDay.get(0);
        assertEquals(addedQuotation, olderQuotationOfSameDay);
    }

    /**
     * Tests the retrieval of the newest Quotation of the given date.
     */
    @Test
    public void testGetNewestQuotationOfDate() {
        Quotation expectedQuotation;
        Quotation actualQuotation;
        Quotation addedQuotation;
        Calendar calendar = Calendar.getInstance();
        final long volume = 150000;

        // The newest Quotation of a day.
        expectedQuotation = this.quotationArray.getQuotations().get(0);
        calendar.setTime(expectedQuotation.getDate());
        calendar.add(Calendar.MINUTE, -1);

        // Add an additional Quotation to the array that is older but at the same day.
        addedQuotation = new Quotation();
        addedQuotation.setDate(calendar.getTime());
        addedQuotation.setCurrency(expectedQuotation.getCurrency());
        addedQuotation.setVolume(volume);
        addedQuotation.setOpen(new BigDecimal("1.95"));
        addedQuotation.setHigh(new BigDecimal("2.05"));
        addedQuotation.setLow(new BigDecimal("1.95"));
        addedQuotation.setClose(new BigDecimal("2.00"));
        this.quotationArray.getQuotations().add(addedQuotation);

        actualQuotation = this.quotationArray.getNewestQuotationOfDate(addedQuotation.getDate());

        assertEquals(expectedQuotation, actualQuotation);
    }

    /**
     * Tests the determination of weekly quotations.
     */
    @Test
    public void testGetWeeklyQuotations() {
        Quotation expectedQuotation1 = this.getQuotation1();
        Quotation expectedQuotation2 = this.getQuotation2();
        Quotation expectedQuotation53 = this.getQuotation53();
        Quotation currentQuotation;
        List<Quotation> weeklyQuotations;
        final int week53 = 53;
        final int index52 = 52;

        weeklyQuotations = this.quotationArray.getWeeklyQuotations(null);

        // Assure that there is one Quotation for each week of the year.
        // The last two days are part of week 53.
        assertEquals(week53, weeklyQuotations.size());

        // Check the newest two weekly quotations.
        currentQuotation = weeklyQuotations.get(0);
        assertEquals(expectedQuotation1, currentQuotation);

        currentQuotation = weeklyQuotations.get(1);
        assertEquals(expectedQuotation2, currentQuotation);

        // Check the oldest weekly Quotation.
        currentQuotation = weeklyQuotations.get(index52);
        assertEquals(expectedQuotation53, currentQuotation);
    }

    /**
     * Tests the determination of weekly quotations if only quotations of two days exist.
     */
    @Test
    public void testGetWeeklyQuotations2Days() {
        QuotationArray twoDaysQuotationArray = new QuotationArray();
        List<Quotation> weeklyQuotations;
        Quotation currentQuotation;
        Quotation expectedQuotation;
        final long volume = 3244200;

        // Initialize QuotationArray with quotations of two days.
        twoDaysQuotationArray.getQuotations().add(this.quotationArray.getQuotations().get(0));
        twoDaysQuotationArray.getQuotations().add(this.quotationArray.getQuotations().get(1));

        // Define the expected weekly Quotation.
        expectedQuotation = new Quotation();
        expectedQuotation.setDate(this.quotationArray.getQuotations().get(1).getDate());
        expectedQuotation.setCurrency(Currency.CAD);
        expectedQuotation.setVolume(volume);
        expectedQuotation.setOpen(new BigDecimal("1.50"));
        expectedQuotation.setHigh(new BigDecimal("1.52"));
        expectedQuotation.setLow(new BigDecimal("1.35"));
        expectedQuotation.setClose(new BigDecimal("1.36"));

        weeklyQuotations = twoDaysQuotationArray.getWeeklyQuotations(null);

        // Assure that there is one Quotation.
        assertEquals(1, weeklyQuotations.size());

        // Verify the weekly Quotation.
        currentQuotation = weeklyQuotations.get(0);
        assertEquals(expectedQuotation, currentQuotation);
    }

    /**
     * Tests the determination of weekly quotations. The newest daily Quotation is not taken into account.
     */
    @Test
    public void testGetWeeklyQuotationsSkipNewestDay() {
        Quotation actualQuotation;
        Quotation expectedQuotation;
        List<Quotation> weeklyQuotations;
        final long volume = 6353400;
        final int index4 = 4;
        final int expectedWeeks = 53;

        this.quotationArray.sortQuotationsByDate();

        // Define the expected weekly Quotation.
        expectedQuotation = new Quotation();
        expectedQuotation.setDate(this.quotationArray.getQuotations().get(index4).getDate());
        expectedQuotation.setCurrency(Currency.CAD);
        expectedQuotation.setVolume(volume);
        expectedQuotation.setOpen(new BigDecimal("1.38"));
        expectedQuotation.setHigh(new BigDecimal("1.54"));
        expectedQuotation.setLow(new BigDecimal("1.38"));
        expectedQuotation.setClose(new BigDecimal("1.46"));

        weeklyQuotations = this.quotationArray.getWeeklyQuotations(this.quotationArray.getQuotations().get(1));

        // Assure that there is one Quotation for each week of the year.
        // The last two days are part of week 53.
        assertEquals(expectedWeeks, weeklyQuotations.size());

        // Check the newest weekly quotation.
        actualQuotation = weeklyQuotations.get(0);
        assertEquals(expectedQuotation, actualQuotation);
    }
}
