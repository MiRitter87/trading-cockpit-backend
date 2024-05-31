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
        List<Quotation> quotations = new ArrayList<>();

        try {
            quotations.addAll(
                    quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));
            this.quotationArray = new QuotationArray(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.quotationArray = null;
    }

    @Test
    /**
     * Tests the determination of the age of the newest Quotation within the QuotationArray.
     */
    public void testGetAgeOfNewestQuotationInDays() {
        Date currentDate = new Date();
        LocalDate currentDateLocal = LocalDate.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
        LocalDate newestQuotationDateLocal;
        long actualNumberDays, expectedNumberDays;
        Quotation newestQuotation;

        this.quotationArray.sortQuotationsByDate();
        newestQuotation = this.quotationArray.getQuotations().get(0);

        newestQuotationDateLocal = LocalDate.ofInstant(newestQuotation.getDate().toInstant(), ZoneId.systemDefault());
        expectedNumberDays = ChronoUnit.DAYS.between(newestQuotationDateLocal, currentDateLocal);

        actualNumberDays = this.quotationArray.getAgeOfNewestQuotationInDays();

        assertEquals(expectedNumberDays, actualNumberDays);
    }

    @Test
    /**
     * Tests the retrieval of quotations that are older than the given Quotation but still on the same day.
     */
    public void testGetOlderQuotationsOfSameDay() {
        List<Quotation> olderQuotationsSameDay;
        Quotation olderQuotationOfSameDay, addedQuotation, quotation;
        Calendar calendar = Calendar.getInstance();

        // The newest Quotation of a day.
        quotation = this.quotationArray.getQuotations().get(0);
        calendar.setTime(quotation.getDate());
        calendar.add(Calendar.MINUTE, -1);

        // Add an additional Quotation to the array that is older but at the same day.
        addedQuotation = new Quotation();
        addedQuotation.setDate(calendar.getTime());
        addedQuotation.setCurrency(quotation.getCurrency());
        addedQuotation.setVolume(150000);
        addedQuotation.setOpen(new BigDecimal(1.95));
        addedQuotation.setHigh(new BigDecimal(2.05));
        addedQuotation.setLow(new BigDecimal(1.95));
        addedQuotation.setClose(new BigDecimal(2.00));
        this.quotationArray.getQuotations().add(addedQuotation);

        olderQuotationsSameDay = this.quotationArray.getOlderQuotationsOfSameDay(quotation.getDate());

        assertEquals(1, olderQuotationsSameDay.size());

        olderQuotationOfSameDay = olderQuotationsSameDay.get(0);
        assertEquals(addedQuotation, olderQuotationOfSameDay);
    }

    @Test
    /**
     * Tests the retrieval of the newest Quotation of the given date.
     */
    public void testGetNewestQuotationOfDate() {
        Quotation expectedQuotation;
        Quotation actualQuotation;
        Quotation addedQuotation;
        Calendar calendar = Calendar.getInstance();

        // The newest Quotation of a day.
        expectedQuotation = this.quotationArray.getQuotations().get(0);
        calendar.setTime(expectedQuotation.getDate());
        calendar.add(Calendar.MINUTE, -1);

        // Add an additional Quotation to the array that is older but at the same day.
        addedQuotation = new Quotation();
        addedQuotation.setDate(calendar.getTime());
        addedQuotation.setCurrency(expectedQuotation.getCurrency());
        addedQuotation.setVolume(150000);
        addedQuotation.setOpen(new BigDecimal(1.95));
        addedQuotation.setHigh(new BigDecimal(2.05));
        addedQuotation.setLow(new BigDecimal(1.95));
        addedQuotation.setClose(new BigDecimal(2.00));
        this.quotationArray.getQuotations().add(addedQuotation);

        actualQuotation = this.quotationArray.getNewestQuotationOfDate(addedQuotation.getDate());

        assertEquals(expectedQuotation, actualQuotation);
    }

    @Test
    /**
     * Tests the determination of weekly quotations.
     */
    public void testGetWeeklyQuotations() {
        Quotation quotation1;
        Quotation quotation2;
        Quotation quotation53;
        Quotation currentQuotation;
        List<Quotation> weeklyQuotations;

        // Define the most recent two of the expected weekly quotations.
        quotation1 = new Quotation();
        quotation1.setDate(this.quotationArray.getQuotations().get(4).getDate());
        quotation1.setCurrency(Currency.CAD);
        quotation1.setVolume(8146700);
        quotation1.setOpen(new BigDecimal("1.38"));
        quotation1.setHigh(new BigDecimal("1.54"));
        quotation1.setLow(new BigDecimal("1.35"));
        quotation1.setClose(new BigDecimal("1.36"));

        quotation2 = new Quotation();
        quotation2.setDate(this.quotationArray.getQuotations().get(9).getDate());
        quotation2.setCurrency(Currency.CAD);
        quotation2.setVolume(6258300);
        quotation2.setOpen(new BigDecimal("1.34"));
        quotation2.setHigh(new BigDecimal("1.38"));
        quotation2.setLow(new BigDecimal("1.24"));
        quotation2.setClose(new BigDecimal("1.36"));

        // Define the last expected weekly Quotation.
        quotation53 = new Quotation();
        quotation53.setDate(this.quotationArray.getQuotations().get(251).getDate());
        quotation53.setCurrency(Currency.CAD);
        quotation53.setVolume(1886800);
        quotation53.setOpen(new BigDecimal("1.35"));
        quotation53.setHigh(new BigDecimal("1.35"));
        quotation53.setLow(new BigDecimal("1.27"));
        quotation53.setClose(new BigDecimal("1.29"));

        weeklyQuotations = this.quotationArray.getWeeklyQuotations(null);

        // Assure that there is one Quotation for each week of the year.
        // The last two days are part of week 53.
        assertEquals(53, weeklyQuotations.size());

        // Check the newest two weekly quotations.
        currentQuotation = weeklyQuotations.get(0);
        assertEquals(quotation1, currentQuotation);

        currentQuotation = weeklyQuotations.get(1);
        assertEquals(quotation2, currentQuotation);

        // Check the oldest weekly Quotation.
        currentQuotation = weeklyQuotations.get(52);
        assertEquals(quotation53, currentQuotation);
    }

    @Test
    /**
     * Tests the determination of weekly quotations if only quotations of two days exist.
     */
    public void testGetWeeklyQuotations2Days() {
        QuotationArray twoDaysQuotationArray = new QuotationArray();
        List<Quotation> weeklyQuotations;
        Quotation currentQuotation;
        Quotation expectedQuotation;

        // Initialize QuotationArray with quotations of two days.
        twoDaysQuotationArray.getQuotations().add(this.quotationArray.getQuotations().get(0));
        twoDaysQuotationArray.getQuotations().add(this.quotationArray.getQuotations().get(1));

        // Define the expected weekly Quotation.
        expectedQuotation = new Quotation();
        expectedQuotation.setDate(this.quotationArray.getQuotations().get(1).getDate());
        expectedQuotation.setCurrency(Currency.CAD);
        expectedQuotation.setVolume(3244200);
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

    @Test
    /**
     * Tests the determination of weekly quotations. The newest daily Quotation is not taken into account.
     */
    public void testGetWeeklyQuotationsSkipNewestDay() {
        Quotation actualQuotation;
        Quotation expectedQuotation;
        List<Quotation> weeklyQuotations;

        this.quotationArray.sortQuotationsByDate();

        // Define the expected weekly Quotation.
        expectedQuotation = new Quotation();
        expectedQuotation.setDate(this.quotationArray.getQuotations().get(4).getDate());
        expectedQuotation.setCurrency(Currency.CAD);
        expectedQuotation.setVolume(6353400);
        expectedQuotation.setOpen(new BigDecimal("1.38"));
        expectedQuotation.setHigh(new BigDecimal("1.54"));
        expectedQuotation.setLow(new BigDecimal("1.38"));
        expectedQuotation.setClose(new BigDecimal("1.46"));

        weeklyQuotations = this.quotationArray.getWeeklyQuotations(this.quotationArray.getQuotations().get(1));

        // Assure that there is one Quotation for each week of the year.
        // The last two days are part of week 53.
        assertEquals(53, weeklyQuotations.size());

        // Check the newest weekly quotation.
        actualQuotation = weeklyQuotations.get(0);
        assertEquals(expectedQuotation, actualQuotation);
    }
}
