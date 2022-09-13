package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.QuotationDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.webservice.ScanTemplate;

/**
 * Tests the QuotationService.
 * 
 * @author Michael
 */
public class QuotationServiceTest {
	/**
	 * DAO to access instrument data.
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
	 * The stock of Ford.
	 */
	private Instrument fordStock;
	
	/**
	 * A Quotation of the Apple stock.
	 */
	private Quotation appleQuotation1;
	
	/**
	 * A Quotation of the Apple stock.
	 */
	private Quotation appleQuotation2;
	
	/**
	 * A Quotation of the Microsoft stock.
	 */
	private Quotation microsoftQuotation1;
	
	/**
	 * A Quotation of the Ford stock.
	 */
	private Quotation fordQuotation1;
	
	/**
	 * The Indicator the Apple stock Quotation 2.
	 */
	private Indicator appleQuotation2Indicator;
	
	/**
	 * The Indicator of the Ford stock Quotation 1.
	 */
	private Indicator fordQuotation1Indicator;
	
	
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
		this.createDummyInstruments();
		this.createDummyQuotations();
		this.createDummyIndicators();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyQuotations();
		this.deleteDummyInstruments();
	}
	
	
	/**
	 * Initializes the database with dummy instruments.
	 */
	private void createDummyInstruments() {
		this.appleStock = this.getAppleStock();
		this.microsoftStock = this.getMicrosoftStock();
		this.fordStock = this.getFordStock();
		
		try {
			instrumentDAO.insertInstrument(this.appleStock);
			instrumentDAO.insertInstrument(this.microsoftStock);
			instrumentDAO.insertInstrument(this.fordStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy instruments from the database.
	 */
	private void deleteDummyInstruments() {
		try {
			instrumentDAO.deleteInstrument(this.fordStock);
			instrumentDAO.deleteInstrument(this.microsoftStock);
			instrumentDAO.deleteInstrument(this.appleStock);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets the Instrument of the Apple stock.
	 * 
	 * @return The Instrument of the Apple stock.
	 */
	private Instrument getAppleStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AAPL");
		instrument.setName("Apple");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the Instrument of the Microsoft stock.
	 * 
	 * @return The Instrument of the Microsoft stock.
	 */
	private Instrument getMicrosoftStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("MSFT");
		instrument.setName("Microsoft");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	/**
	 * Gets the Instrument of the Ford stock.
	 * 
	 * @return The Instrument of the Ford stock.
	 */
	private Instrument getFordStock() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("F");
		instrument.setName("Ford Motor Company");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Initializes the database with dummy quotations.
	 */
	private void createDummyQuotations() {
		List<Quotation> quotations = new ArrayList<>();
		
		this.appleQuotation1 = this.getAppleQuotation1();
		this.appleQuotation2 = this.getAppleQuotation2();
		this.microsoftQuotation1 = this.getMicrosoftQuotation1();
		this.fordQuotation1 = this.getFordQuotation1();
		
		quotations.add(this.appleQuotation1);
		quotations.add(this.appleQuotation2);
		quotations.add(this.microsoftQuotation1);
		quotations.add(this.fordQuotation1);
		
		try {
			quotationDAO.insertQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy quotations from the database.
	 */
	private void deleteDummyQuotations() {
		List<Quotation> quotations = new ArrayList<>();
		
		quotations.add(this.fordQuotation1);
		quotations.add(this.microsoftQuotation1);
		quotations.add(this.appleQuotation2);
		quotations.add(this.appleQuotation1);
		
		try {
			quotationDAO.deleteQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets the Quotation 1 of the Apple stock.
	 * 
	 * @return The Quotation 1 of the Apple stock.
	 */
	private Quotation getAppleQuotation1() {
		Quotation quotation = new Quotation();
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		
		quotation.setDate(calendar.getTime());
		quotation.setPrice(BigDecimal.valueOf(78.54));
		quotation.setCurrency(Currency.USD);
		quotation.setVolume(28973654);
		quotation.setInstrument(this.appleStock);
		
		return quotation;
	}
	
	
	/**
	 * Gets the Quotation 2 of the Apple stock.
	 * 
	 * @return The Quotation 2 of the Apple stock.
	 */
	private Quotation getAppleQuotation2() {
		Quotation quotation = new Quotation();
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 15);
		calendar.set(Calendar.MINUTE, 30);
		
		quotation.setDate(calendar.getTime());
		quotation.setPrice(BigDecimal.valueOf(77.52));
		quotation.setCurrency(Currency.USD);
		quotation.setVolume(12373654);
		quotation.setInstrument(this.appleStock);
		
		return quotation;
	}
	
	
	/**
	 * Gets the Quotation 1 of the Microsoft stock.
	 * 
	 * @return The Quotation 1 of the Microsoft stock.
	 */
	private Quotation getMicrosoftQuotation1() {
		Quotation quotation = new Quotation();
		
		quotation.setDate(new Date());
		quotation.setPrice(BigDecimal.valueOf(124.07));
		quotation.setCurrency(Currency.USD);
		quotation.setVolume(13973124);
		quotation.setInstrument(this.microsoftStock);
		
		return quotation;
	}
	
	
	/**
	 * Gets the Quotation 1 of the Ford stock.
	 * 
	 * @return The Quotation 1 of the Ford stock.
	 */
	private Quotation getFordQuotation1() {
		Quotation quotation = new Quotation();
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 22);
		calendar.set(Calendar.MINUTE, 0);
		
		quotation.setDate(calendar.getTime());
		quotation.setPrice(BigDecimal.valueOf(15.88));
		quotation.setCurrency(Currency.USD);
		quotation.setVolume(48600000);
		quotation.setInstrument(this.fordStock);
		
		return quotation;
	}
	
	
	/**
	 * Initializes the database with dummy indicators.
	 */
	private void createDummyIndicators() {
		List<Quotation> quotations = new ArrayList<>();
		
		this.appleQuotation2Indicator = new Indicator();
		this.appleQuotation2Indicator.setStage(2);
		this.appleQuotation2Indicator.setSma200(60);
		this.appleQuotation2Indicator.setSma150((float) 63.45);
		this.appleQuotation2Indicator.setSma50((float) 69.24);
		this.appleQuotation2Indicator.setRsNumber(71);
		this.appleQuotation2Indicator.setDistanceTo52WeekHigh((float) 21.4);
		this.appleQuotation2Indicator.setDistanceTo52WeekLow((float) 78.81);
		this.appleQuotation2Indicator.setBollingerBandWidth((float) 8.71);
		this.appleQuotation2Indicator.setVolumeDifferential10Days((float) 19.34);
		this.appleQuotation2.setIndicator(this.appleQuotation2Indicator);
		
		this.fordQuotation1Indicator = new Indicator();
		this.fordQuotation1Indicator.setStage(3);
		this.fordQuotation1Indicator.setSma200((float) 16.36);
		this.fordQuotation1Indicator.setSma150((float) 15.08);
		this.fordQuotation1Indicator.setSma50((float) 13.07);
		this.fordQuotation1Indicator.setRsNumber(45);
		this.fordQuotation1Indicator.setDistanceTo52WeekHigh((float) 41.41);
		this.fordQuotation1Indicator.setDistanceTo52WeekLow((float) 48.81);
		this.fordQuotation1Indicator.setBollingerBandWidth((float) 4.11);
		this.fordQuotation1Indicator.setVolumeDifferential10Days((float) -9.67);
		this.fordQuotation1.setIndicator(this.fordQuotation1Indicator);
		
		quotations.add(this.appleQuotation2);
		quotations.add(this.fordQuotation1);
		
		try {
			quotationDAO.updateQuotations(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent quotation with its corresponding Indicator and Instrument.
	 * Only those quotations should be returned that have an Indicator associated with them.
	 */
	public void testGetRecentQuotations() {
		QuotationArray quotations;
		WebServiceResult getQuotationsResult;
		
		//Get the quotations.
		QuotationService service = new QuotationService();
		getQuotationsResult = service.getQuotations(null);
		quotations = (QuotationArray) getQuotationsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);
		
		//Check if two quotations are returned.
		assertEquals(2, quotations.getQuotations().size());
		
		//Check if the correct quotations are returned.
		for(Quotation tempQuotation: quotations.getQuotations()) {
			if(tempQuotation.getId().equals(appleQuotation2.getId())) {
				assertEquals(this.appleQuotation2.getDate().getTime(), tempQuotation.getDate().getTime());
				assertTrue(this.appleQuotation2.getPrice().compareTo(tempQuotation.getPrice()) == 0);
				assertEquals(this.appleQuotation2.getCurrency(), tempQuotation.getCurrency());
				assertEquals(this.appleQuotation2.getVolume(), tempQuotation.getVolume());
				
				//Check if Indicator and Instrument have been initialized and contain data.
				assertEquals(this.appleQuotation2.getInstrument().getId(), tempQuotation.getInstrument().getId());
				assertEquals(this.appleQuotation2.getIndicator().getId(), tempQuotation.getIndicator().getId());
				assertEquals(this.appleQuotation2.getIndicator().getStage(), tempQuotation.getIndicator().getStage());		
			}
			else if(tempQuotation.getId().equals(this.fordQuotation1.getId())) {
				assertEquals(this.fordQuotation1.getDate().getTime(), tempQuotation.getDate().getTime());
				assertTrue(this.fordQuotation1.getPrice().compareTo(tempQuotation.getPrice()) == 0);
				assertEquals(this.fordQuotation1.getCurrency(), tempQuotation.getCurrency());
				assertEquals(this.fordQuotation1.getVolume(), tempQuotation.getVolume());
				
				//Check if Indicator and Instrument have been initialized and contain data.
				assertEquals(this.fordQuotation1.getInstrument().getId(), tempQuotation.getInstrument().getId());
				assertEquals(this.fordQuotation1.getIndicator().getId(), tempQuotation.getIndicator().getId());
				assertEquals(this.fordQuotation1.getIndicator().getStage(), tempQuotation.getIndicator().getStage());	
			}
			else {
				fail("An unrelated Quotation has been returned.");
			}
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent quotations that match the Minervini Trend Template.
	 * Only those quotations should be returned that have an Indicator associated with them.
	 */
	public void testGetQuotationsMinerviniTrendTemplate() {
		QuotationArray quotations;
		WebServiceResult getQuotationsResult;
		Quotation quotation;
		
		//Get the quotations.
		QuotationService service = new QuotationService();
		getQuotationsResult = service.getQuotations(ScanTemplate.MINERVINI_TREND_TEMPLATE);
		quotations = (QuotationArray) getQuotationsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);
		
		//Check if one Quotation is returned.
		assertEquals(1, quotations.getQuotations().size());
		
		//Check if the correct Quotation is returned.
		quotation = quotations.getQuotations().get(0);
		assertEquals(this.appleQuotation2.getId(), quotation.getId());
		assertEquals(this.appleQuotation2.getDate().getTime(), quotation.getDate().getTime());
		assertTrue(this.appleQuotation2.getPrice().compareTo(quotation.getPrice()) == 0);
		assertEquals(this.appleQuotation2.getCurrency(), quotation.getCurrency());
		assertEquals(this.appleQuotation2.getVolume(), quotation.getVolume());
		
		//Check if Indicator and Instrument have been initialized and contain data.
		assertEquals(this.appleQuotation2.getInstrument().getId(), quotation.getInstrument().getId());
		assertEquals(this.appleQuotation2.getIndicator().getId(), quotation.getIndicator().getId());
		assertEquals(this.appleQuotation2.getIndicator().getStage(), quotation.getIndicator().getStage());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the most recent quotations that match the "Volatility Contraction 10 Days" template.
	 * Only those quotations should be returned that have an Indicator associated with them.
	 */
	public void testGetQuotationsVolatiltyContraction10DaysTemplate() {
		QuotationArray quotations;
		WebServiceResult getQuotationsResult;
		Quotation quotation;
		
		//Get the quotations.
		QuotationService service = new QuotationService();
		getQuotationsResult = service.getQuotations(ScanTemplate.VOLATILITY_CONTRACTION_10_DAYS);
		quotations = (QuotationArray) getQuotationsResult.getData();
		
		//Assure no error message exists
		assertTrue(WebServiceTools.resultContainsErrorMessage(getQuotationsResult) == false);
		
		//Check if one Quotation is returned.
		assertEquals(1, quotations.getQuotations().size());
		
		//Check if the correct Quotation is returned.
		quotation = quotations.getQuotations().get(0);
		assertEquals(this.fordQuotation1.getId(), quotation.getId());
		assertEquals(this.fordQuotation1.getDate().getTime(), quotation.getDate().getTime());
		assertTrue(this.fordQuotation1.getPrice().compareTo(quotation.getPrice()) == 0);
		assertEquals(this.fordQuotation1.getCurrency(), quotation.getCurrency());
		assertEquals(this.fordQuotation1.getVolume(), quotation.getVolume());
		
		//Check if Indicator and Instrument have been initialized and contain data.
		assertEquals(this.fordQuotation1.getInstrument().getId(), quotation.getInstrument().getId());
		assertEquals(this.fordQuotation1.getIndicator().getId(), quotation.getIndicator().getId());
		assertEquals(this.fordQuotation1.getIndicator().getStage(), quotation.getIndicator().getStage());
	}
}
