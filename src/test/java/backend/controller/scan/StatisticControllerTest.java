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
import backend.dao.quotation.QuotationDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;

/**
 * Tests the StatisticController.
 * 
 * @author Michael
 */
public class StatisticControllerTest {
	/**
	 * DAO to access Instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * DAO to access Quotation data.
	 */
	private static QuotationDAO quotationDAO;
	
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
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		try {
			instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
			quotationDAO = DAOManager.getInstance().getQuotationDAO();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at the end of the test class.
	 */
	public static void tearDownClass() {
		try {
			DAOManager.getInstance().close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
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
		this.createDummyInstruments();
		this.createDummyQuotations();
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
			this.appleStock.setStockExchange(StockExchange.NYSE);
			this.appleStock.setType(InstrumentType.STOCK);
			instrumentDAO.insertInstrument(this.appleStock);
			
			this.microsoftStock.setSymbol("MSFT");
			this.microsoftStock.setName("Microsoft");
			this.microsoftStock.setStockExchange(StockExchange.NYSE);
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
		Calendar calendar = Calendar.getInstance();
		List<Quotation> quotations = new ArrayList<>();
		
		try {		
			calendar.setTime(new Date());
			this.appleQuotation1 = new Quotation();
			this.appleQuotation1.setDate(calendar.getTime());
			this.appleQuotation1.setPrice(BigDecimal.valueOf(78.54));
			this.appleQuotation1.setCurrency(Currency.USD);
			this.appleQuotation1.setVolume(6784544);
			this.appleQuotation1.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation1);
			
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			this.appleQuotation2 = new Quotation();
			this.appleQuotation2.setDate(calendar.getTime());
			this.appleQuotation2.setPrice(BigDecimal.valueOf(79.14));
			this.appleQuotation2.setCurrency(Currency.USD);
			this.appleQuotation2.setVolume(4584544);
			this.appleQuotation2.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation2);
			
			calendar.add(Calendar.DAY_OF_YEAR, -2);
			this.appleQuotation3 = new Quotation();
			this.appleQuotation3.setDate(calendar.getTime());
			this.appleQuotation3.setPrice(BigDecimal.valueOf(81.23));
			this.appleQuotation3.setCurrency(Currency.USD);
			this.appleQuotation3.setVolume(3184544);
			this.appleQuotation3.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation3);
			
			calendar.setTime(new Date());
			this.microsoftQuotation1 = new Quotation();
			this.microsoftQuotation1.setDate(calendar.getTime());
			this.microsoftQuotation1.setPrice(BigDecimal.valueOf(247.58));
			this.microsoftQuotation1.setCurrency(Currency.USD);
			this.microsoftQuotation1.setVolume(1234544);
			this.microsoftQuotation1.setInstrument(this.microsoftStock);
			quotations.add(this.microsoftQuotation1);
			
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			this.microsoftQuotation2 = new Quotation();
			this.microsoftQuotation2.setDate(calendar.getTime());
			this.microsoftQuotation2.setPrice(BigDecimal.valueOf(246.11));
			this.microsoftQuotation2.setCurrency(Currency.USD);
			this.microsoftQuotation2.setVolume(6664544);
			this.microsoftQuotation2.setInstrument(this.microsoftStock);
			quotations.add(this.microsoftQuotation2);
			
			calendar.add(Calendar.DAY_OF_YEAR, -2);
			this.microsoftQuotation3 = new Quotation();
			this.microsoftQuotation3.setDate(calendar.getTime());
			this.microsoftQuotation3.setPrice(BigDecimal.valueOf(246.88));
			this.microsoftQuotation3.setCurrency(Currency.USD);
			this.microsoftQuotation3.setVolume(8764544);
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
	
	
	@Test
	/**
	 * Tests the calculation of statistics.
	 */
	public void testCalculateStatistics() {
		List<Statistic> calculatedStatistics;
		StatisticController statisticController = new StatisticController();
		List<Instrument> instruments;
		
		try {
			instruments = instrumentDAO.getInstruments(InstrumentType.STOCK);
			calculatedStatistics = statisticController.calculateStatistics(instruments);
			
			assertEquals(2, calculatedStatistics.size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
}
