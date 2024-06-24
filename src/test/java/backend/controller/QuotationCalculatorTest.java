package backend.controller;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import backend.controller.scan.QuotationCalculator;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

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
}
