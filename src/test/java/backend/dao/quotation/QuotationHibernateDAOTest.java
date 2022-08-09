package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	
	
	@Test
	/**
	 * Tests insertion of a Quotation using the 'insertQuotations' method.
	 */
	public void testInsertQuotations() {
		Calendar calendar = Calendar.getInstance();
		List<Quotation> quotations = new ArrayList<>();
		Quotation newQuotation, databaseQuotation;
		
		//Define a new Quotation to be added.
		calendar.add(Calendar.DAY_OF_YEAR, 2);
		newQuotation = new Quotation();
		newQuotation.setDate(calendar.getTime());
		newQuotation.setPrice(BigDecimal.valueOf(78.19));
		newQuotation.setCurrency(Currency.USD);
		newQuotation.setVolume(1184234);
		newQuotation.setInstrument(this.appleStock);
		quotations.add(newQuotation);
		
		try {
			//Add Quotation to database.
			quotationDAO.insertQuotations(quotations);
			
			//Check if Quotation has been correctly persisted.
			databaseQuotation = quotationDAO.getQuotation(newQuotation.getId());
			assertEquals(newQuotation.getId(), databaseQuotation.getId());
			assertEquals(newQuotation.getDate().getTime(), databaseQuotation.getDate().getTime());
			assertTrue(newQuotation.getPrice().compareTo(databaseQuotation.getPrice()) == 0);
			assertEquals(newQuotation.getCurrency(), databaseQuotation.getCurrency());
			assertEquals(newQuotation.getVolume(), databaseQuotation.getVolume());
			assertEquals(newQuotation.getInstrument().getId(), databaseQuotation.getInstrument().getId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Remove the newly added quotation from the database.
			try {
				quotationDAO.deleteQuotations(quotations);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests deletion of a Quotation using the 'deleteQuotations' method.
	 */
	public void testDeleteQuotations() {
		List<Quotation> quotations = new ArrayList<>();
		Quotation deletedQuotation;
		
		try {
			//Delete Quotation.
			quotations.add(this.appleQuotation1);
			quotationDAO.deleteQuotations(quotations);
			
			//Try to get the previously deleted Quotation.
			deletedQuotation = quotationDAO.getQuotation(this.appleQuotation1.getId());
			
			//Assure the Quotation does not exist anymore.
			assertNull(deletedQuotation);
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Add the previously deleted quotation back to the database.
			this.appleQuotation1.setId(null);
			quotations.clear();
			quotations.add(this.appleQuotation1);
			
			try {
				quotationDAO.insertQuotations(quotations);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of a Quotation with a given ID.
	 */
	public void testGetQuotation() {
		Quotation databaseQuotation;
		
		try {
			databaseQuotation = quotationDAO.getQuotation(this.appleQuotation2.getId());
			
			//Check the attributes of the database Quotation.
			assertEquals(databaseQuotation.getId(), this.appleQuotation2.getId());
			assertEquals(databaseQuotation.getDate().getTime(), this.appleQuotation2.getDate().getTime());
			assertTrue(databaseQuotation.getPrice().compareTo(this.appleQuotation2.getPrice()) == 0);
			assertEquals(databaseQuotation.getCurrency(), this.appleQuotation2.getCurrency());
			assertEquals(databaseQuotation.getVolume(), this.appleQuotation2.getVolume());
			assertEquals(databaseQuotation.getInstrument().getId(), this.appleQuotation2.getInstrument().getId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of all quotations of an Instrument.
	 */
	public void testGetQuotationsOfInstrument() {
		List<Quotation> quotations;
		
		try {
			quotations = quotationDAO.getQuotationsOfInstrument(this.appleStock.getId());
			
			//TODO Test if indicators are provided.
			
			for(Quotation databaseQuotation:quotations) {
				if(databaseQuotation.getId() == this.appleQuotation1.getId()) {
					assertEquals(this.appleQuotation1.getId(), databaseQuotation.getId());
					assertEquals(this.appleQuotation1.getDate().getTime(), databaseQuotation.getDate().getTime());
					assertTrue(this.appleQuotation1.getPrice().compareTo(databaseQuotation.getPrice()) == 0);
					assertEquals(this.appleQuotation1.getCurrency(), databaseQuotation.getCurrency());
					assertEquals(this.appleQuotation1.getVolume(), databaseQuotation.getVolume());
					assertEquals(this.appleQuotation1.getInstrument().getId(), databaseQuotation.getInstrument().getId());
				}
				else if(databaseQuotation.getId() == this.appleQuotation2.getId()) {
					assertEquals(this.appleQuotation2.getId(), databaseQuotation.getId());
					assertEquals(this.appleQuotation2.getDate().getTime(), databaseQuotation.getDate().getTime());
					assertTrue(this.appleQuotation2.getPrice().compareTo(databaseQuotation.getPrice()) == 0);
					assertEquals(this.appleQuotation2.getCurrency(), databaseQuotation.getCurrency());
					assertEquals(this.appleQuotation2.getVolume(), databaseQuotation.getVolume());
					assertEquals(this.appleQuotation2.getInstrument().getId(), databaseQuotation.getInstrument().getId());
				}
				else {
					fail("The method 'getQuotationsOfInstrument' has returned an unrelated quotation.");
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
