package backend.dao.quotation;

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

import backend.dao.DAOManager;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the QuotationHibernateDAO.
 * 
 * @author Michael
 */
public class QuotationHibernateDAOTest {
	/**
	 * DAO to access Quotation data.
	 */
	private static QuotationDAO quotationDAO;
	
	/**
	 * DAO to access Instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * The stock of Apple.
	 */
	private Instrument appleStock;
	
	/**
	 * The first Quotation of the Apple stock.
	 */
	private Quotation appleQuotation1;
	
	/**
	 * The second Quotation of the Apple stock.
	 */
	private Quotation appleQuotation2;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
		quotationDAO = DAOManager.getInstance().getQuotationDAO();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
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
		Calendar calendar = Calendar.getInstance();
		List<Quotation> quotations = new ArrayList<>();
		this.appleStock = new Instrument();
		
		try {
			this.appleStock.setSymbol("AAPL");
			this.appleStock.setName("Apple");
			this.appleStock.setStockExchange(StockExchange.NYSE);
			this.appleStock.setType(InstrumentType.STOCK);
			instrumentDAO.insertInstrument(this.appleStock);
			
			calendar.setTime(new Date());
			this.appleQuotation1 = new Quotation();
			this.appleQuotation1.setDate(calendar.getTime());
			this.appleQuotation1.setPrice(BigDecimal.valueOf(78.54));
			this.appleQuotation1.setCurrency(Currency.USD);
			this.appleQuotation1.setVolume(6784544);
			this.appleQuotation1.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation1);
			
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			this.appleQuotation2 = new Quotation();
			this.appleQuotation2.setDate(calendar.getTime());
			this.appleQuotation2.setPrice(BigDecimal.valueOf(79.14));
			this.appleQuotation2.setCurrency(Currency.USD);
			this.appleQuotation2.setVolume(4584544);
			this.appleQuotation2.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation2);
			
			quotationDAO.insertQuotations(quotations);			
		} catch (DuplicateInstrumentException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	
	/**
	 * Deletes the apple stock and its quotations from the database.
	 */
	private void deleteTestData() {
		try {
			List<Quotation> quotations = new ArrayList<>();
			
			quotations.add(this.appleQuotation1);
			quotations.add(this.appleQuotation2);
			
			quotationDAO.deleteQuotations(quotations);
			instrumentDAO.deleteInstrument(this.appleStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
