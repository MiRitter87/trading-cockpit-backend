package backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.scan.QuotationCalculator;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.tools.DateTools;

/**
 * Tests the QuotationCalculator.
 *
 * @author Michael
 */
public class QuotationCalculatorTest {
    /**
     * The Apple stock.
     */
    private Instrument appleStock;

    /**
     * The Netflix stock.
     */
    private Instrument netflixStock;

    /**
     * The QuotationCalculator being tested.
     */
    private QuotationCalculator quotationCalculator;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        this.quotationCalculator = new QuotationCalculator();
        this.initializeInstruments();
        this.initializeAppleQuotations();
        this.initializeNetflixQuotations();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.quotationCalculator = null;
    }

    /**
     * Initializes the instruments.
     */
    private void initializeInstruments() {
        this.appleStock = new Instrument();
        this.appleStock.setSymbol("AAPL");
        this.appleStock.setName("Apple");
        this.appleStock.setStockExchange(StockExchange.NDQ);
        this.appleStock.setType(InstrumentType.STOCK);

        this.netflixStock = new Instrument();
        this.netflixStock.setSymbol("NFLX");
        this.netflixStock.setName("Netflix");
        this.netflixStock.setStockExchange(StockExchange.NDQ);
        this.netflixStock.setType(InstrumentType.STOCK);
    }

    /**
     * Initializes quotations of the Apple Instrument.
     */
    private void initializeAppleQuotations() {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("210.39"));
        quotation.setHigh(new BigDecimal("211.89"));
        quotation.setLow(new BigDecimal("207.11"));
        quotation.setClose(new BigDecimal("207.49"));
        quotation.setVolume(333336960);
        this.appleStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -1);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("213.93"));
        quotation.setHigh(new BigDecimal("214.24"));
        quotation.setLow(new BigDecimal("208.85"));
        quotation.setClose(new BigDecimal("209.68"));
        quotation.setVolume(84387904);
        this.appleStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -3);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("217.59"));
        quotation.setHigh(new BigDecimal("218.63"));
        quotation.setLow(new BigDecimal("213.00"));
        quotation.setClose(new BigDecimal("214.29"));
        quotation.setVolume(79666048);
        this.appleStock.addQuotation(quotation);
    }

    /**
     * Initializes quotations of the Netflix Instrument.
     */
    private void initializeNetflixQuotations() {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("683.26"));
        quotation.setHigh(new BigDecimal("686.90"));
        quotation.setLow(new BigDecimal("678.10"));
        quotation.setClose(new BigDecimal("686.12"));
        quotation.setVolume(5385950);
        this.netflixStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -1);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("681.80"));
        quotation.setHigh(new BigDecimal("689.88"));
        quotation.setLow(new BigDecimal("673.72"));
        quotation.setClose(new BigDecimal("679.03"));
        quotation.setVolume(2572490);
        this.netflixStock.addQuotation(quotation);
    }

    /**
     * Gets the expected quotations sorted by date.
     */
    private List<Quotation> getExpectedQuotations() {
        List<Quotation> expectedQuotations = new ArrayList<>();
        List<Quotation> appleQuotations = this.appleStock.getQuotationsSortedByDate();
        Quotation quotation;

        quotation = new Quotation();
        quotation.setDate(appleQuotations.get(0).getDate());
        quotation.setOpen(new BigDecimal("446.825"));
        quotation.setHigh(new BigDecimal("449.395"));
        quotation.setLow(new BigDecimal("442.605"));
        quotation.setClose(new BigDecimal("442.605"));
        quotation.setVolume(169361455);

        quotation = new Quotation();
        quotation.setDate(appleQuotations.get(1).getDate());
        quotation.setOpen(new BigDecimal("447.865"));
        quotation.setHigh(new BigDecimal("452.06"));
        quotation.setLow(new BigDecimal("441.285"));
        quotation.setClose(new BigDecimal("444.355"));
        quotation.setVolume(86960394);

        quotation = new Quotation();
        quotation.setDate(appleQuotations.get(2).getDate());
        quotation.setOpen(new BigDecimal("217.59"));
        quotation.setHigh(new BigDecimal("218.63"));
        quotation.setLow(new BigDecimal("213.00"));
        quotation.setClose(new BigDecimal("214.29"));
        quotation.setVolume(79666048);

        return expectedQuotations;
    }

    /**
     * Gets the expected dates without intraday-attributes.
     *
     * @return A List of dates.
     */
    private List<Date> getExpectedDates() {
        List<Date> dates = new ArrayList<>();
        List<Quotation> quotations = this.getExpectedQuotations();
        Date date;

        for (Quotation quotation : quotations) {
            date = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());
            dates.add(date);
        }

        return dates;
    }

    // @Test
    /**
     * Tests the determination of calculated quotations.
     */
    public void testGetCalculatedQuotations() {
        List<Instrument> instruments = new ArrayList<>();
        List<Quotation> calculatedQuotations;
        final int expectedQutoations = 3;

        instruments.add(this.appleStock);
        instruments.add(this.netflixStock);

        calculatedQuotations = this.quotationCalculator.getCalculatedQuotations(instruments);

        assertEquals(expectedQutoations, calculatedQuotations.size());
    }

    @Test
    /**
     * Tests the determination of all Quotation dates of the given List of instruments with their quotations.
     */
    public void testGetQuotationDates() {
        List<Instrument> instruments = new ArrayList<>();
        List<Date> expectedDates = this.getExpectedDates();
        HashSet<Date> actualDates;
        final int expectedNumberOfDates = 3;

        instruments.add(this.appleStock);
        instruments.add(this.netflixStock);

        actualDates = this.quotationCalculator.getQuotationDates(instruments);

        assertEquals(expectedNumberOfDates, actualDates.size());

        for (Date expectedDate : expectedDates) {
            assertTrue(actualDates.contains(expectedDate));
        }
    }
}
