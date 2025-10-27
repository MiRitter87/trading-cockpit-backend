package backend.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Tests the RatioCalculator.
 *
 * @author Michael
 */
public class RatioCalculatorTest {
    /**
     * The dividend of the ratio.
     */
    private Instrument dividendInstrument;

    /**
     * The divisor of the ratio.
     */
    private Instrument divisorInstrument;

    /**
     * The first Quotation of the dividend.
     */
    private Quotation dividendQuotation1;

    /**
     * The second Quotation of the dividend.
     */
    private Quotation dividendQuotation2;

    /**
     * The first Quotation of the divisor.
     */
    private Quotation divisorQuotation1;

    /**
     * The second Quotation of the divisor.
     */
    private Quotation divisorQuotation2;

    /**
     * The RatioCalculator under test.
     */
    private RatioCalculator ratioCalculator;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        this.ratioCalculator = new RatioCalculator();
        this.createTestData();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.deleteTestData();
        this.ratioCalculator = null;
    }

    /**
     * Initializes the test data.
     */
    private void createTestData() {
        this.initializeDividend();
        this.initializeDivisor();
    }

    /**
     * Deletes the test data.
     */
    private void deleteTestData() {
        this.dividendInstrument = null;
        this.dividendQuotation1 = null;
        this.dividendQuotation2 = null;

        this.divisorInstrument = null;
        this.divisorQuotation1 = null;
        this.divisorQuotation2 = null;
    }

    /**
     * Initializes the dividend Instrument with its quotations.
     */
    private void initializeDividend() {
        Calendar calendar = Calendar.getInstance();

        this.dividendInstrument = new Instrument();

        this.dividendQuotation1 = new Quotation();
        this.dividendQuotation1.setDate(calendar.getTime());
        this.dividendQuotation1.setOpen(new BigDecimal(31.07));
        this.dividendQuotation1.setHigh(new BigDecimal(31.48));
        this.dividendQuotation1.setLow(new BigDecimal(30.18));
        this.dividendQuotation1.setClose(new BigDecimal(31.28));
        this.dividendQuotation1.setVolume(21714395);
        this.dividendQuotation1.setCurrency(Currency.USD);
        this.dividendInstrument.addQuotation(this.dividendQuotation1);

        this.dividendQuotation2 = new Quotation();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        this.dividendQuotation2.setDate(calendar.getTime());
        this.dividendQuotation2.setOpen(new BigDecimal(33.00));
        this.dividendQuotation2.setHigh(new BigDecimal(33.19));
        this.dividendQuotation2.setLow(new BigDecimal(30.71));
        this.dividendQuotation2.setClose(new BigDecimal(30.89));
        this.dividendQuotation2.setVolume(30588446);
        this.dividendQuotation2.setCurrency(Currency.USD);
        this.dividendInstrument.addQuotation(this.dividendQuotation2);
    }

    /**
     * Initializes the divisor Instrument with its quotations.
     */
    private void initializeDivisor() {
        Calendar calendar = Calendar.getInstance();

        this.divisorInstrument = new Instrument();

        this.divisorQuotation1 = new Quotation();
        this.divisorQuotation1.setDate(calendar.getTime());
        this.divisorQuotation1.setOpen(new BigDecimal(26.25));
        this.divisorQuotation1.setHigh(new BigDecimal(26.76));
        this.divisorQuotation1.setLow(new BigDecimal(26.01));
        this.divisorQuotation1.setClose(new BigDecimal(26.66));
        this.divisorQuotation1.setVolume(27368364);
        this.divisorQuotation1.setCurrency(Currency.USD);
        this.divisorInstrument.addQuotation(this.divisorQuotation1);

        this.divisorQuotation2 = new Quotation();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        this.divisorQuotation2.setDate(calendar.getTime());
        this.divisorQuotation2.setOpen(new BigDecimal(26.70));
        this.divisorQuotation2.setHigh(new BigDecimal(26.74));
        this.divisorQuotation2.setLow(new BigDecimal(26.06));
        this.divisorQuotation2.setClose(new BigDecimal(26.16));
        this.divisorQuotation2.setVolume(27627548);
        this.divisorQuotation2.setCurrency(Currency.USD);
        this.divisorInstrument.addQuotation(this.divisorQuotation2);
    }

    /**
     * Provides the expected ratio Quotation based on dividendQuotation1 and divisorQuotation1.
     *
     * @return The expected ratio Quotation 1.
     */
    private Quotation getExpectedRatioQuotation1() {
        Quotation quotation = new Quotation();

        quotation.setDate(this.dividendQuotation1.getDate());
        quotation.setCurrency(Currency.USD);
        quotation.setOpen(new BigDecimal("1.184"));
        quotation.setHigh(new BigDecimal("1.176"));
        quotation.setLow(new BigDecimal("1.160"));
        quotation.setClose(new BigDecimal("1.173"));

        return quotation;
    }

    /**
     * Provides the expected ratio Quotation based on dividendQuotation2 and divisorQuotation2.
     *
     * @return The expected ratio Quotation 2.
     */
    private Quotation getExpectedRatioQuotation2() {
        Quotation quotation = new Quotation();

        quotation.setDate(this.dividendQuotation2.getDate());
        quotation.setCurrency(Currency.USD);
        quotation.setOpen(new BigDecimal("1.236"));
        quotation.setHigh(new BigDecimal("1.241"));
        quotation.setLow(new BigDecimal("1.178"));
        quotation.setClose(new BigDecimal("1.181"));

        return quotation;
    }

    @Test
    /**
     * Tests the calculation of ratio quotations between two instruments.
     */
    public void testGetRatios() {
        List<Quotation> ratioQuotations;
        Quotation expectedQuotation1 = this.getExpectedRatioQuotation1();
        Quotation expectedQuotation2 = this.getExpectedRatioQuotation2();
        Quotation ratioQuotation;

        try {
            ratioQuotations = this.ratioCalculator.getRatios(this.dividendInstrument, this.divisorInstrument);

            assertEquals(2, ratioQuotations.size());

            ratioQuotation = ratioQuotations.get(0);
            assertEquals(expectedQuotation1, ratioQuotation);

            ratioQuotation = ratioQuotations.get(1);
            assertEquals(expectedQuotation2, ratioQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
