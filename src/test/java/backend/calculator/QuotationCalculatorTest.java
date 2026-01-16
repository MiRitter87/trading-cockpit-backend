package backend.calculator;

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

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.quotationCalculator = new QuotationCalculator();
        this.initializeInstruments();
        this.initializeAppleQuotations();
        this.initializeNetflixQuotations();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
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
        final long volumeDay0 = 333336960;
        final long volumeDay1 = 84387904;
        final long volumeDay3 = 79666048;
        final int days2 = 2;

        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("210.39"));
        quotation.setHigh(new BigDecimal("211.89"));
        quotation.setLow(new BigDecimal("207.11"));
        quotation.setClose(new BigDecimal("207.49"));
        quotation.setVolume(volumeDay0);
        this.appleStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -1);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("213.93"));
        quotation.setHigh(new BigDecimal("214.24"));
        quotation.setLow(new BigDecimal("208.85"));
        quotation.setClose(new BigDecimal("209.68"));
        quotation.setVolume(volumeDay1);
        this.appleStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -days2);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("217.59"));
        quotation.setHigh(new BigDecimal("218.63"));
        quotation.setLow(new BigDecimal("213.00"));
        quotation.setClose(new BigDecimal("214.29"));
        quotation.setVolume(volumeDay3);
        this.appleStock.addQuotation(quotation);
    }

    /**
     * Initializes quotations of the Netflix Instrument.
     */
    private void initializeNetflixQuotations() {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();
        final long volumeDay0 = 5385950;
        final long volumeDay1 = 2572490;
        final long volumeDay2 = 2857420;

        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("683.26"));
        quotation.setHigh(new BigDecimal("686.90"));
        quotation.setLow(new BigDecimal("678.10"));
        quotation.setClose(new BigDecimal("686.12"));
        quotation.setVolume(volumeDay0);
        this.netflixStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -1);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("681.80"));
        quotation.setHigh(new BigDecimal("689.88"));
        quotation.setLow(new BigDecimal("673.72"));
        quotation.setClose(new BigDecimal("679.03"));
        quotation.setVolume(volumeDay1);
        this.netflixStock.addQuotation(quotation);

        calendar.add(Calendar.DATE, -1);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setOpen(new BigDecimal("675.63"));
        quotation.setHigh(new BigDecimal("687.06"));
        quotation.setLow(new BigDecimal("674.64"));
        quotation.setClose(new BigDecimal("685.67"));
        quotation.setVolume(volumeDay2);
        this.netflixStock.addQuotation(quotation);
    }

    /**
     * Gets the expected quotations sorted by date.
     *
     * @return Quotations sorted by date.
     */
    private List<Quotation> getExpectedQuotations() {
        List<Quotation> expectedQuotations = new ArrayList<>();
        List<Quotation> appleQuotations = this.appleStock.getQuotationsSortedByDate();
        List<Quotation> netflixQuotations = this.netflixStock.getQuotationsSortedByDate();
        Quotation quotation;
        final long volume0 = 169361455;
        final long volume1 = 43480197;
        final long volume2 = 41261734;
        final long volume3 = 79666048;

        // (appleQuotation0 + netflixQuotation0) / 2
        quotation = new Quotation();
        quotation.setDate(DateTools.getDateWithoutIntradayAttributes(appleQuotations.get(0).getDate()));
        quotation.setOpen(new BigDecimal("446.825"));
        quotation.setHigh(new BigDecimal("449.395"));
        quotation.setLow(new BigDecimal("442.605"));
        quotation.setClose(new BigDecimal("446.805"));
        quotation.setVolume(volume0);
        expectedQuotations.add(quotation);
        // (appleQuotation1 + netflixQuotation1) / 2
        quotation = new Quotation();
        quotation.setDate(DateTools.getDateWithoutIntradayAttributes(appleQuotations.get(1).getDate()));
        quotation.setOpen(new BigDecimal("447.865"));
        quotation.setHigh(new BigDecimal("452.060"));
        quotation.setLow(new BigDecimal("441.285"));
        quotation.setClose(new BigDecimal("444.355"));
        quotation.setVolume(volume1);
        expectedQuotations.add(quotation);
        // (appleQuotation2 + netflixQuotation2) / 2
        quotation = new Quotation();
        quotation.setDate(DateTools.getDateWithoutIntradayAttributes(netflixQuotations.get(2).getDate()));
        quotation.setOpen(new BigDecimal("446.610"));
        quotation.setHigh(new BigDecimal("452.845"));
        quotation.setLow(new BigDecimal("443.820"));
        quotation.setClose(new BigDecimal("449.980"));
        quotation.setVolume(volume2);
        expectedQuotations.add(quotation);
        // appleQuotation2
        quotation = new Quotation();
        quotation.setDate(DateTools.getDateWithoutIntradayAttributes(appleQuotations.get(2).getDate()));
        quotation.setOpen(new BigDecimal("217.590"));
        quotation.setHigh(new BigDecimal("218.630"));
        quotation.setLow(new BigDecimal("213.000"));
        quotation.setClose(new BigDecimal("214.290"));
        quotation.setVolume(volume3);
        expectedQuotations.add(quotation);

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

    /**
     * Tests the determination of calculated quotations.
     */
    @Test
    public void testGetCalculatedQuotations() {
        List<Instrument> instruments = new ArrayList<>();
        List<Quotation> calculatedQuotations;
        List<Quotation> expectedQuotations = this.getExpectedQuotations();
        final int expectedQutoations = 4;
        final int index3 = 3;

        instruments.add(this.appleStock);
        instruments.add(this.netflixStock);

        calculatedQuotations = this.quotationCalculator.getCalculatedQuotations(instruments);

        assertEquals(expectedQutoations, calculatedQuotations.size());

        assertEquals(expectedQuotations.get(0), calculatedQuotations.get(0));
        assertEquals(expectedQuotations.get(1), calculatedQuotations.get(1));
        assertEquals(expectedQuotations.get(2), calculatedQuotations.get(2));
        assertEquals(expectedQuotations.get(index3), calculatedQuotations.get(index3));
    }

    /**
     * Tests the determination of all Quotation dates of the given List of instruments with their quotations.
     */
    @Test
    public void testGetQuotationDates() {
        List<Instrument> instruments = new ArrayList<>();
        List<Date> expectedDates = this.getExpectedDates();
        HashSet<Date> actualDates;
        final int expectedNumberOfDates = 4;

        instruments.add(this.appleStock);
        instruments.add(this.netflixStock);

        actualDates = this.quotationCalculator.getQuotationDates(instruments);

        assertEquals(expectedNumberOfDates, actualDates.size());

        for (Date expectedDate : expectedDates) {
            assertTrue(actualDates.contains(expectedDate));
        }
    }
}
