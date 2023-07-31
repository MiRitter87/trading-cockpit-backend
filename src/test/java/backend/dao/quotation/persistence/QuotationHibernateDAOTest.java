package backend.dao.quotation.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.webservice.ScanTemplate;

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
	 * The stock of Microsoft.
	 */
	private Instrument microsoftStock;
	
	/**
	 * The ETF XLE.
	 */
	private Instrument xleETF;
	
	/**
	 * The Industrials sector.
	 */
	private Instrument xliSector;
	
	/**
	 * The copper industry group.
	 */
	private Instrument copperIndustryGroup;
	
	/**
	 * The first Quotation of the Apple stock.
	 */
	private Quotation appleQuotation1;
	
	/**
	 * The second Quotation of the Apple stock.
	 */
	private Quotation appleQuotation2;
	
	/**
	 * The first Quotation of the Microsoft stock.
	 */
	private Quotation microsoftQuotation1;
	
	/**
	 * The first Quotation of the XLE ETF.
	 */
	private Quotation xleQuotation1;
	
	/**
	 * The first Quotation of the XLI sector.
	 */
	private Quotation xliSectorQuotation1;
	
	/**
	 * The first Quotation of the copper industry group.
	 */
	private Quotation copperIndustryGroupQuotation1;
	
	/**
	 * Indicator of the Second Quotation of the Apple stock.
	 */
	private Indicator appleQuotation2Indicator;
	
	/**
	 * Indicator of the first Quotation of the XLE ETF.
	 */
	private Indicator xleQuotation1Indicator;
	
	/**
	 * Indicator of the first Quotation of the XLI sector.
	 */
	private Indicator xliSectorQuotation1Indicator;
	
	/**
	 * Indicator of the first Quotation of the copper industry group.
	 */
	private Indicator copperIndustryGroupQuotation1Indicator;
	
	
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
	 * Initializes the database with instruments, quotations and indicators used as test data.
	 */
	private void createTestData() {
		this.createInstruments();
		this.createQuotations();
		this.createIndicators();
	}
	
	
	/**
	 * Creates Instrument data.
	 */
	private void createInstruments() {
		this.appleStock = new Instrument();
		this.microsoftStock = new Instrument();
		this.xleETF = new Instrument();
		this.xliSector = new Instrument();
		this.copperIndustryGroup = new Instrument();
		
		try {
			this.xliSector.setSymbol("XLI");
			this.xliSector.setName("Industrial Select Sector SPDR Fund");
			this.xliSector.setStockExchange(StockExchange.NYSE);
			this.xliSector.setType(InstrumentType.SECTOR);
			instrumentDAO.insertInstrument(this.xliSector);
			
			this.copperIndustryGroup.setSymbol("COPX");
			this.copperIndustryGroup.setName("Global X Copper Miners ETF");
			this.copperIndustryGroup.setStockExchange(StockExchange.NYSE);
			this.copperIndustryGroup.setType(InstrumentType.IND_GROUP);
			instrumentDAO.insertInstrument(this.copperIndustryGroup);
			
			this.appleStock.setSymbol("AAPL");
			this.appleStock.setName("Apple");
			this.appleStock.setStockExchange(StockExchange.NDQ);
			this.appleStock.setType(InstrumentType.STOCK);
			this.appleStock.setSector(this.xliSector);
			this.appleStock.setIndustryGroup(this.copperIndustryGroup);
			instrumentDAO.insertInstrument(this.appleStock);
			
			this.microsoftStock.setSymbol("MSFT");
			this.microsoftStock.setName("Microsoft");
			this.microsoftStock.setStockExchange(StockExchange.NDQ);
			this.microsoftStock.setType(InstrumentType.STOCK);
			instrumentDAO.insertInstrument(this.microsoftStock);
			
			this.xleETF.setSymbol("XLE");
			this.xleETF.setName("Energy Select Sector SPDR Fund");
			this.xleETF.setStockExchange(StockExchange.NYSE);
			this.xleETF.setType(InstrumentType.ETF);
			instrumentDAO.insertInstrument(this.xleETF);
		} catch (DuplicateInstrumentException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	
	/**
	 * Creates Quotation data.
	 */
	private void createQuotations() {
		Calendar calendar = Calendar.getInstance();
		List<Quotation> quotations = new ArrayList<>();
		
		try {
			calendar.setTime(new Date());
			this.microsoftQuotation1 = new Quotation();
			this.microsoftQuotation1.setDate(calendar.getTime());
			this.microsoftQuotation1.setClose(BigDecimal.valueOf(246.79));
			this.microsoftQuotation1.setCurrency(Currency.USD);
			this.microsoftQuotation1.setVolume(20200000);
			this.microsoftQuotation1.setInstrument(this.microsoftStock);
			quotations.add(this.microsoftQuotation1);
			
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			this.appleQuotation1 = new Quotation();
			this.appleQuotation1.setDate(calendar.getTime());
			this.appleQuotation1.setClose(BigDecimal.valueOf(78.54));
			this.appleQuotation1.setCurrency(Currency.USD);
			this.appleQuotation1.setVolume(6784544);
			this.appleQuotation1.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation1);
			
			calendar.setTime(new Date());
			this.appleQuotation2 = new Quotation();
			this.appleQuotation2.setDate(calendar.getTime());
			this.appleQuotation2.setClose(BigDecimal.valueOf(79.14));
			this.appleQuotation2.setCurrency(Currency.USD);
			this.appleQuotation2.setVolume(4584544);
			this.appleQuotation2.setInstrument(this.appleStock);
			quotations.add(this.appleQuotation2);
			
			calendar.setTime(new Date());
			this.xleQuotation1 = new Quotation();
			this.xleQuotation1.setDate(calendar.getTime());
			this.xleQuotation1.setClose(BigDecimal.valueOf(81.28));
			this.xleQuotation1.setCurrency(Currency.USD);
			this.xleQuotation1.setVolume(18994000);
			this.xleQuotation1.setInstrument(this.xleETF);
			quotations.add(this.xleQuotation1);
			
			calendar.setTime(new Date());
			this.xliSectorQuotation1 = new Quotation();
			this.xliSectorQuotation1.setDate(calendar.getTime());
			this.xliSectorQuotation1.setClose(BigDecimal.valueOf(92.60));
			this.xliSectorQuotation1.setCurrency(Currency.USD);
			this.xliSectorQuotation1.setVolume(13884800);
			this.xliSectorQuotation1.setInstrument(this.xliSector);
			quotations.add(this.xliSectorQuotation1);
			
			calendar.setTime(new Date());
			this.copperIndustryGroupQuotation1 = new Quotation();
			this.copperIndustryGroupQuotation1.setDate(calendar.getTime());
			this.copperIndustryGroupQuotation1.setClose(BigDecimal.valueOf(29.04));
			this.copperIndustryGroupQuotation1.setCurrency(Currency.USD);
			this.copperIndustryGroupQuotation1.setVolume(401200);
			this.copperIndustryGroupQuotation1.setInstrument(this.copperIndustryGroup);
			quotations.add(this.copperIndustryGroupQuotation1);
			
			quotationDAO.insertQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	
	/**
	 * Creates Indicator data.
	 */
	private void createIndicators() {
		List<Quotation> quotations = new ArrayList<>();
		
		try {
			this.appleQuotation2Indicator = new Indicator();
			this.appleQuotation2Indicator.setStage(3);
			this.appleQuotation2.setIndicator(this.appleQuotation2Indicator);
			quotations.add(this.appleQuotation2);
			
			this.xleQuotation1Indicator = new Indicator();
			this.xleQuotation1Indicator.setStage(2);
			this.xleQuotation1.setIndicator(this.xleQuotation1Indicator);
			quotations.add(this.xleQuotation1);
			
			this.xliSectorQuotation1Indicator = new Indicator();
			this.xliSectorQuotation1Indicator.setStage(1);
			this.xliSectorQuotation1Indicator.setRsNumber(46);
			this.xliSectorQuotation1.setIndicator(this.xliSectorQuotation1Indicator);
			quotations.add(this.xliSectorQuotation1);
			
			this.copperIndustryGroupQuotation1Indicator = new Indicator();
			this.copperIndustryGroupQuotation1Indicator.setStage(4);
			this.copperIndustryGroupQuotation1Indicator.setRsNumber(12);
			this.copperIndustryGroupQuotation1.setIndicator(this.copperIndustryGroupQuotation1Indicator);
			quotations.add(this.copperIndustryGroupQuotation1);
			
			quotationDAO.updateQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the instruments, quotations and indicators used as test data from the database.
	 */
	private void deleteTestData() {
		try {
			List<Quotation> quotations = new ArrayList<>();

			quotations.add(this.appleQuotation1);
			quotations.add(this.appleQuotation2);
			quotations.add(this.microsoftQuotation1);
			quotations.add(this.xleQuotation1);
			quotations.add(this.xliSectorQuotation1);
			quotations.add(this.copperIndustryGroupQuotation1);

			quotationDAO.deleteQuotations(quotations);
			instrumentDAO.deleteInstrument(this.xleETF);
			instrumentDAO.deleteInstrument(this.microsoftStock);
			instrumentDAO.deleteInstrument(this.appleStock);
			instrumentDAO.deleteInstrument(this.copperIndustryGroup);
			instrumentDAO.deleteInstrument(this.xliSector);
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
		newQuotation.setClose(BigDecimal.valueOf(78.19));
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
			assertTrue(newQuotation.getClose().compareTo(databaseQuotation.getClose()) == 0);
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
			assertTrue(databaseQuotation.getClose().compareTo(this.appleQuotation2.getClose()) == 0);
			assertEquals(databaseQuotation.getCurrency(), this.appleQuotation2.getCurrency());
			assertEquals(databaseQuotation.getVolume(), this.appleQuotation2.getVolume());
			assertEquals(databaseQuotation.getInstrument().getId(), this.appleQuotation2.getInstrument().getId());
			
			assertNotNull(databaseQuotation.getIndicator());
			assertEquals(this.appleQuotation2Indicator.getStage(), databaseQuotation.getIndicator().getStage());
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
			
			for(Quotation databaseQuotation:quotations) {
				if(databaseQuotation.getId().equals(appleQuotation1.getId())) {
					assertEquals(this.appleQuotation1, databaseQuotation);
				}
				else if(databaseQuotation.getId().equals(this.appleQuotation2.getId())) {
					assertEquals(this.appleQuotation2, databaseQuotation);
				}
				else {
					fail("The method 'getQuotationsOfInstrument' has returned an unrelated quotation.");
				}
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests updating a Quotation adding a new Indicator relation.
	 */
	public void testUpdateQuotationWithNewIndicator() {
		List<Quotation> updateQuotations = new ArrayList<>();
		Quotation databaseQuotation;
		Indicator newIndicator = new Indicator();
		final int indicatorStage = 3;
		
		//Define the new Indicator and add relation to existing Quotation.
		newIndicator.setStage(indicatorStage);
		this.appleQuotation2.setIndicator(newIndicator);
		
		try {
			//Persist the new Quotation-Indicator relation.
			updateQuotations.add(this.appleQuotation2);
			quotationDAO.updateQuotations(updateQuotations);
			
			//Retrieve the updated Quotation from the database and check if the Indicator has been persisted.
			databaseQuotation = quotationDAO.getQuotation(this.appleQuotation2.getId());
			assertNotNull(databaseQuotation);
			assertNotNull(databaseQuotation.getIndicator());
			assertEquals(indicatorStage, databaseQuotation.getIndicator().getStage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally	{
			try {
				//Remove the newly created Indicator.
				this.appleQuotation2.setIndicator(null);
				updateQuotations.clear();
				updateQuotations.add(this.appleQuotation2);
				quotationDAO.updateQuotations(updateQuotations);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent Quotation for each Instrument of a List.
	 */
	public void testGetRecentQuotationsForList() {
		backend.model.list.List list = new backend.model.list.List();
		List<Quotation> quotations;
		
		list.addInstrument(this.appleStock);
		list.addInstrument(this.microsoftStock);
		
		try {
			quotations = quotationDAO.getRecentQuotationsForList(list);
			
			//Assure one quotation for each Instrument is provided.
			assertEquals(2, quotations.size());
			
			//Assure the correct quotations are provided.
			for(Quotation databaseQuotation:quotations) {
				if(databaseQuotation.getId().equals(microsoftQuotation1.getId())) {
					assertEquals(this.microsoftQuotation1, databaseQuotation);
				}
				else if(databaseQuotation.getId().equals(this.appleQuotation2.getId())) {
					assertEquals(this.appleQuotation2, databaseQuotation);
				}
				else {
					fail("The method 'getRecentQuotationsForList' has returned an unrelated quotation.");
				}
			}
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent Quotation of each Instrument of type STOCK.
	 */
	public void testGetRecentQuotationsTypeStock() {
		List<Quotation> quotations;
		Quotation databaseQuotation;
		
		try {
			quotations = quotationDAO.getRecentQuotations(InstrumentType.STOCK);
			
			//Assure one Quotation is returned.
			assertEquals(1, quotations.size());
			
			//Assure the correct Quotation is provided.
			databaseQuotation = quotations.get(0);
			assertEquals(this.appleQuotation2, databaseQuotation);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent Quotation of each Instrument of type ETF.
	 */
	public void testGetRecentQuotationsTypeEtf() {
		List<Quotation> quotations;
		Quotation databaseQuotation;
		
		try {
			quotations = quotationDAO.getRecentQuotations(InstrumentType.ETF);
			
			//Assure one Quotation is returned.
			assertEquals(1, quotations.size());
			
			//Assure the correct Quotation is provided.
			databaseQuotation = quotations.get(0);
			assertEquals(this.xleQuotation1, databaseQuotation);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent Quotation of each Instrument of type STOCK.
	 * Especially the filling of transient attributes is tested here.
	 */
	public void testGetQuotationsByTemplate() {
		List<Quotation> quotations;
		Quotation databaseQuotation;
		
		try {
			quotations = quotationDAO.getQuotationsByTemplate(ScanTemplate.ALL, InstrumentType.STOCK, null, null);
			
			//Assure one Quotation is returned.
			assertEquals(1, quotations.size());
			
			//Assure the correct Quotation is provided.
			databaseQuotation = quotations.get(0);
			assertEquals(this.appleQuotation2, databaseQuotation);
			
			//Assure the RS number of the sector and industry group is provided. These are transient attributes.
			assertEquals(this.xliSectorQuotation1Indicator.getRsNumber(), databaseQuotation.getIndicator().getRsNumberSector());
			assertEquals(this.copperIndustryGroupQuotation1Indicator.getRsNumber(), databaseQuotation.getIndicator().getRsNumberIndustryGroup());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
