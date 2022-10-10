package backend.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;


/**
 * Tests the ExcelExportController
 * 
 * @author Michael
 */
public class ExcelExportControllerTest {
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
		Calendar calendar = Calendar.getInstance();
		
		this.appleStock = new Instrument();
		this.appleStock.setSymbol("AAPL");
		this.appleStock.setName("Apple");
		this.appleStock.setStockExchange(StockExchange.NYSE);
		this.appleStock.setType(InstrumentType.STOCK);
		
		this.microsoftStock = new Instrument();
		this.microsoftStock.setSymbol("MSFT");
		this.microsoftStock.setName("Microsoft");
		this.microsoftStock.setStockExchange(StockExchange.NYSE);
		this.microsoftStock.setType(InstrumentType.STOCK);
		
		calendar.setTime(new Date());
		this.microsoftQuotation = new Quotation();
		this.microsoftQuotation.setDate(calendar.getTime());
		this.microsoftQuotation.setPrice(BigDecimal.valueOf(246.79));
		this.microsoftQuotation.setCurrency(Currency.USD);
		this.microsoftQuotation.setVolume(20200000);
		this.microsoftQuotation.setInstrument(this.microsoftStock);
		
		calendar.setTime(new Date());
		this.appleQuotation = new Quotation();
		this.appleQuotation.setDate(calendar.getTime());
		this.appleQuotation.setPrice(BigDecimal.valueOf(78.54));
		this.appleQuotation.setCurrency(Currency.USD);
		this.appleQuotation.setVolume(6784544);
		this.appleQuotation.setInstrument(this.appleStock);
		
		this.quotations = new ArrayList<>();
		this.quotations.add(this.appleQuotation);
		this.quotations.add(this.microsoftQuotation);
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
}
