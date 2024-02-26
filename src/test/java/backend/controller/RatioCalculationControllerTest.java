package backend.controller;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Tests the RatioCalculationController.
 *
 * @author Michael
 */
public class RatioCalculationControllerTest {
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
        this.dividendInstrument.addQuotation(this.dividendQuotation1);

        this.dividendQuotation2 = new Quotation();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        this.dividendQuotation2.setDate(calendar.getTime());
        this.dividendQuotation2.setOpen(new BigDecimal(33.00));
        this.dividendQuotation2.setHigh(new BigDecimal(33.19));
        this.dividendQuotation2.setLow(new BigDecimal(30.71));
        this.dividendQuotation2.setClose(new BigDecimal(30.89));
        this.dividendQuotation2.setVolume(30588446);
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
        this.divisorInstrument.addQuotation(this.divisorQuotation1);

        this.divisorQuotation2 = new Quotation();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        this.divisorQuotation2.setDate(calendar.getTime());
        this.divisorQuotation2.setOpen(new BigDecimal(26.70));
        this.divisorQuotation2.setHigh(new BigDecimal(26.74));
        this.divisorQuotation2.setLow(new BigDecimal(26.06));
        this.divisorQuotation2.setClose(new BigDecimal(26.16));
        this.divisorQuotation2.setVolume(27627548);
        this.divisorInstrument.addQuotation(this.divisorQuotation2);
    }
}
