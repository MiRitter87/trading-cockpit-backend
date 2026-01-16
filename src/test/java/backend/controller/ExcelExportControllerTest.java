package backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.RelativeStrengthData;

/**
 * Tests the ExcelExportController.
 *
 * @author Michael
 */
public class ExcelExportControllerTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * A List of quotations.
     */
    private List<Quotation> quotations;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

    /**
     * A Quotation of the Apple stock.
     */
    private Quotation appleQuotation;

    /**
     * A Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation;

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.createTestData();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteTestData();
    }

    /**
     * Initializes the database with the apple stock and its quotations.
     */
    private void createTestData() {
        this.initializeInstruments();
        this.initializeQuotations();
        this.initializeIndicators();
    }

    /**
     * Deletes the apple stock and its quotations from the database.
     */
    private void deleteTestData() {
        this.quotations = null;
        this.appleQuotation = null;
        this.microsoftQuotation = null;
        this.microsoftStock = null;
        this.appleStock = null;
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

        this.microsoftStock = new Instrument();
        this.microsoftStock.setSymbol("MSFT");
        this.microsoftStock.setName("Microsoft");
        this.microsoftStock.setStockExchange(StockExchange.NDQ);
        this.microsoftStock.setType(InstrumentType.STOCK);
    }

    /**
     * Initializes the quotations.
     */
    private void initializeQuotations() {
        Calendar calendar = Calendar.getInstance();
        final long volume1 = 20200000;
        final long volume2 = 6784544;

        calendar.setTime(new Date());
        this.microsoftQuotation = new Quotation();
        this.microsoftQuotation.setDate(calendar.getTime());
        this.microsoftQuotation.setClose(new BigDecimal("246.79"));
        this.microsoftQuotation.setCurrency(Currency.USD);
        this.microsoftQuotation.setVolume(volume1);
        this.microsoftQuotation.setInstrument(this.microsoftStock);

        calendar.setTime(new Date());
        this.appleQuotation = new Quotation();
        this.appleQuotation.setDate(calendar.getTime());
        this.appleQuotation.setClose(new BigDecimal("78.54"));
        this.appleQuotation.setCurrency(Currency.USD);
        this.appleQuotation.setVolume(volume2);
        this.appleQuotation.setInstrument(this.appleStock);

        this.quotations = new ArrayList<>();
        this.quotations.add(this.appleQuotation);
        this.quotations.add(this.microsoftQuotation);
    }

    /**
     * Initializes indicators.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void initializeIndicators() {
        this.microsoftQuotation.setRelativeStrengthData(new RelativeStrengthData());
        this.microsoftQuotation.getRelativeStrengthData().setRsNumber(78);
        this.microsoftQuotation.setIndicator(new Indicator());
        this.microsoftQuotation.getIndicator().setAverageTrueRangePercent20(1.72f);

        this.appleQuotation.setRelativeStrengthData(new RelativeStrengthData());
        this.appleQuotation.getRelativeStrengthData().setRsNumber(54);
        this.appleQuotation.setIndicator(new Indicator());
        this.appleQuotation.getIndicator().setAverageTrueRangePercent20(2.01f);
    }

    /**
     * Tests the retrieval of an Excel Workbook that contains the following quotation data for given List: Symbol, Date,
     * Price, RS Number, ATR%.
     */
    @Test
    public void testGetPriceDataOfQuotations() {
        ExcelExportController excelExportController = new ExcelExportController();
        Map<Integer, List<String>> workbookContent;
        List<String> tableRowAttributes;
        final int row3 = 3;
        final int row4 = 4;

        // Construct an Excel workbook for the given quotations.
        Workbook workbook = excelExportController.getQuotationDataWorkbook(this.quotations);

        // Check if the content of the workbook matches the expectations.
        workbookContent = excelExportController.readWorkbook(workbook);

        // The table header
        tableRowAttributes = workbookContent.get(0);
        assertEquals(this.resources.getString("instrument.attribute.symbol"), tableRowAttributes.get(0));
        assertEquals(this.resources.getString("quotation.attribute.date"), tableRowAttributes.get(1));
        assertEquals(this.resources.getString("quotation.attribute.price"), tableRowAttributes.get(2));
        assertEquals(this.resources.getString("quotation.attribute.rsNumber"), tableRowAttributes.get(row3));
        assertEquals(this.resources.getString("quotation.attribute.atrp"), tableRowAttributes.get(row4));

        // Data of the first Quotation.
        tableRowAttributes = workbookContent.get(1);
        assertEquals(this.appleQuotation.getInstrument().getSymbol(), tableRowAttributes.get(0));
        assertEquals(excelExportController.getDateAsExcelString(this.appleQuotation.getDate()),
                tableRowAttributes.get(1));
        assertEquals(this.appleQuotation.getClose().toString(), tableRowAttributes.get(2));
        assertEquals(Double.valueOf(this.appleQuotation.getRelativeStrengthData().getRsNumber()),
                Double.valueOf(tableRowAttributes.get(row3)));
        assertEquals(this.appleQuotation.getIndicator().getAverageTrueRangePercent20(),
                Double.valueOf(tableRowAttributes.get(row4)));

        // Data of the second Quotation.
        tableRowAttributes = workbookContent.get(2);
        assertEquals(this.microsoftQuotation.getInstrument().getSymbol(), tableRowAttributes.get(0));
        assertEquals(excelExportController.getDateAsExcelString(this.microsoftQuotation.getDate()),
                tableRowAttributes.get(1));
        assertEquals(this.microsoftQuotation.getClose().toString(), tableRowAttributes.get(2));
        assertEquals(Double.valueOf(this.microsoftQuotation.getRelativeStrengthData().getRsNumber()),
                Double.valueOf(tableRowAttributes.get(row3)));
        assertEquals(this.microsoftQuotation.getIndicator().getAverageTrueRangePercent20(),
                Double.valueOf(tableRowAttributes.get(row4)));
    }
}
