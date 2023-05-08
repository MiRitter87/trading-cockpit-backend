package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.LocalizedException;
import backend.model.StockExchange;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the instrument model.
 * 
 * @author Michael
 */
public class InstrumentTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * The instrument under test.
	 */
	private Instrument instrument;
	
	/**
	 * The first Quotation under test.
	 */
	private Quotation quotation1;
	
	/**
	 * The second Quotation under test.
	 */
	private Quotation quotation2;
	
	/**
	 * The third Quotation under test.
	 */
	private Quotation quotation3;
	
	/**
	 * The Microsoft stock.
	 */
	private Instrument microsoftStock;
	
	/**
	 * A sector Instrument.
	 */
	private Instrument sector;
	
	/**
	 * An industry group Instrument.
	 */
	private Instrument industryGroup;
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		this.quotation1 = new Quotation();
		calendar.set(2022, 07, 26, 15, 30, 0);
		this.quotation1.setDate(calendar.getTime());
		this.quotation1.setClose(BigDecimal.valueOf(1.11));
		this.quotation1.setCurrency(Currency.USD);
		this.quotation1.setVolume(10000);
		
		this.quotation2 = new Quotation();
		calendar.set(2022, 07, 27, 15, 30, 0);
		this.quotation2.setDate(calendar.getTime());
		this.quotation2.setClose(BigDecimal.valueOf(1.12));
		this.quotation2.setCurrency(Currency.USD);
		this.quotation2.setVolume(13400);
		
		this.quotation3 = new Quotation();
		calendar.set(2022, 07, 27, 14, 30, 0);
		this.quotation3.setDate(calendar.getTime());
		this.quotation3.setClose(BigDecimal.valueOf(1.11));
		this.quotation3.setCurrency(Currency.USD);
		this.quotation3.setVolume(10110);
		
		this.instrument = new Instrument();
		this.instrument.setId(Integer.valueOf(1));
		this.instrument.setSymbol("AAPL");
		this.instrument.setType(InstrumentType.STOCK);
		this.instrument.setStockExchange(StockExchange.NYSE);
		this.instrument.setName("Apple");
		this.instrument.addQuotation(this.quotation1);
		this.instrument.addQuotation(this.quotation2);
		this.instrument.addQuotation(this.quotation3);
		
		this.microsoftStock = new Instrument();
		this.microsoftStock.setId(Integer.valueOf(2));
		this.microsoftStock.setSymbol("MSFT");
		this.microsoftStock.setType(InstrumentType.STOCK);
		this.microsoftStock.setStockExchange(StockExchange.NYSE);
		this.microsoftStock.setName("Microsoft");
		
		this.sector = new Instrument();
		this.sector.setId(Integer.valueOf(3));
		this.sector.setSymbol("XLE");
		this.sector.setType(InstrumentType.SECTOR);
		this.sector.setStockExchange(StockExchange.NYSE);
		this.sector.setName("Energy Select Sector SPDR Fund");
		
		this.industryGroup = new Instrument();
		this.industryGroup.setId(Integer.valueOf(4));
		this.industryGroup.setSymbol("COPX");
		this.industryGroup.setType(InstrumentType.IND_GROUP);
		this.industryGroup.setStockExchange(StockExchange.NYSE);
		this.industryGroup.setName("Global X Copper Miners ETF");
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.instrument = null;
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose ID is too low.
	 */
	public void testIdTooLow() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setId(0);
		
		String expectedErrorMessage = messageProvider.getMinValidationMessage("instrument", "id", "1");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because Id is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose symbol is null.
	 */
	public void testSymbolIsNull() {
		this.instrument.setSymbol(null);
		
		String expectedErrorMessage = this.resources.getString("instrument.symbol.notNull.message");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because symbol is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getLocalizedMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose symbol is not given.
	 */
	public void testSymbolNotGiven() {	
		this.instrument.setSymbol("");
		
		String expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.symbol.size.message"),
				this.instrument.getSymbol().length(), "1", "6");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because symbol is not given.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getLocalizedMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose symbol is too long.
	 */
	public void testSymbolTooLong() {		
		this.instrument.setSymbol("ABCDEFG");
		
		String expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.symbol.size.message"),
				this.instrument.getSymbol().length(), "1", "6");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because symbol is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getLocalizedMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose type is null.
	 */
	public void testTypeIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setType(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("instrument", "type");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because type is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose stock exchange is null.
	 */
	public void testStockExchangeIsNull() {	
		this.instrument.setStockExchange(null);
		
		String expectedErrorMessage = this.resources.getString("instrument.stockExchange.notNull.message");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because stock exchange is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getLocalizedMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose name is too long.
	 */
	public void testNameTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setName("This is a company name that is way too long to be of use");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "name", 
				String.valueOf(this.instrument.getName().length()), "0", "50");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because name is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose name is null.
	 */
	public void testNameIsNull() {
		this.instrument.setName(null);
		
		try {
			this.instrument.validate();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests validation of an Instrument whose companyPathInvestingCom is too long.
	 */
	public void testCompanyPathInvestingComTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setCompanyPathInvestingCom("denison-mines-corp?cid=24520kjndfkvnfkjgndffjkkfn11");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "companyPathInvestingCom", 
				String.valueOf(this.instrument.getCompanyPathInvestingCom().length()), "0", "50");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because companyPathInvestingCom is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an Instrument whose companyNameInvestingCom is null.
	 */
	public void testCompanyPathInvestingComIsNull() {
		this.instrument.setCompanyPathInvestingCom(null);
		
		try {
			this.instrument.validate();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests getting a quotation by a given date.
	 * A quotation for the date exists.
	 */
	public void testGetQuotationByDateExisting() {
		Quotation quotation;
		Calendar calendar = Calendar.getInstance();
		
		calendar.clear();
		calendar.set(2022, 07, 26, 15, 30, 0);		//Date of quotation1.
		
		quotation = this.instrument.getQuotationByDate(calendar.getTime());
		
		assertEquals(this.quotation1, quotation);
	}
	
	
	@Test
	/**
	 * Tests getting a quotation by a given date.
	 * A quotation for the date does not exist.
	 */
	public void testGetQuotationByDateNotExisting() {
		Quotation quotation;
		Calendar calendar = Calendar.getInstance();
		calendar.set(2022, 07, 25);		//No quotation at that date exists.
		
		quotation = this.instrument.getQuotationByDate(calendar.getTime());
		
		assertNull(quotation);
	}
	
	
	@Test
	/**
	 * Tests getting the quotations of an instrument as a list sorted by date.
	 */
	public void testGetQuotationsSortedByDate() {
		List<Quotation> sortedQuotations = this.instrument.getQuotationsSortedByDate();
		Quotation quotation;
		
		assertNotNull(sortedQuotations);
		assertEquals(this.instrument.getQuotations().size(), sortedQuotations.size());
		
		//Assure correct sorting. Index 0 has to contain the quotation with the most recent date.
		quotation = sortedQuotations.get(0);
		assertEquals(this.quotation2.getDate().getTime(), quotation.getDate().getTime());
		
		quotation = sortedQuotations.get(1);
		assertEquals(this.quotation3.getDate().getTime(), quotation.getDate().getTime());
		
		quotation = sortedQuotations.get(2);
		assertEquals(this.quotation1.getDate().getTime(), quotation.getDate().getTime());
	}
	
	
	@Test
	/**
	 * Tests the retrieval of quotations that are older than the given Quotation but still on the same day.
	 */
	public void testGetOlderQuotationsOfSameDay() {
		List<Quotation> olderQuotationsSameDay;
		Quotation olderQuotationOfSameDay;
		
		olderQuotationsSameDay = this.instrument.getOlderQuotationsOfSameDay(this.quotation2.getDate());
		
		assertEquals(1, olderQuotationsSameDay.size());
		
		olderQuotationOfSameDay = olderQuotationsSameDay.get(0);
		assertEquals(this.quotation3, olderQuotationOfSameDay);
	}
	
	
	@Test
	/**
	 * Tests referencing the sector of an Instrument with another Instrument of type stock.
	 */
	public void testReferenceSectorWithStock() {
		this.instrument.setSector(this.microsoftStock);
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because the sector is referenced with an Instrument that is not of type 'SECTOR'.");
		} catch (InstrumentReferenceException expected) {
			assertEquals(expected.getExpectedType(), InstrumentType.SECTOR);
			assertEquals(expected.getActualType(), InstrumentType.STOCK);
		} catch (Exception e) {
			fail("No general exception should have occurred. Just the InstrumentReferenceException.");
		}
	}
	
	
	@Test
	/**
	 * Tests referencing the industry group of an Instrument with another Instrument of type stock.
	 */
	public void testReferenceIndustryGroupWithStock() {
		this.instrument.setIndustryGroup(this.microsoftStock);
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because the industry group is referenced with an Instrument that is not of type 'INDUSTRY_GROUP'.");
		} catch (InstrumentReferenceException expected) {
			assertEquals(expected.getExpectedType(), InstrumentType.IND_GROUP);
			assertEquals(expected.getActualType(), InstrumentType.STOCK);
		} catch (Exception e) {
			fail("No general exception should have occurred. Just the InstrumentReferenceException.");
		}
	}
	
	
	@Test
	/**
	 * Tests referencing an Instrument of type sector with another sector.
	 */
	public void testReferenceSectorWithSector() {
		this.instrument.setType(InstrumentType.SECTOR);
		this.instrument.setSector(this.sector);
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because the sector is referenced with an Instrument that is of type 'SECTOR'.");
		} catch (InstrumentReferenceException expected) {
			assertEquals(expected.getExpectedType(), null);
			assertEquals(expected.getActualType(), InstrumentType.SECTOR);
		} catch (Exception e) {
			fail("No general exception should have occurred. Just the InstrumentReferenceException.");
		}
	}
	
	
	@Test
	/**
	 * Tests referencing an Instrument of type industry group with another industry group.
	 */
	public void testReferenceIgWithIg() {
		this.instrument.setType(InstrumentType.IND_GROUP);
		this.instrument.setIndustryGroup(this.industryGroup);
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because the industry group is referenced with an Instrument that is of type 'IND_GROUP'.");
		} catch (InstrumentReferenceException expected) {
			assertEquals(expected.getExpectedType(), null);
			assertEquals(expected.getActualType(), InstrumentType.IND_GROUP);
		} catch (Exception e) {
			fail("No general exception should have occurred. Just the InstrumentReferenceException.");
		}
	}
	
	
	@Test
	/**
	 * Tests the determination of the age of the instruments newest Quotation.
	 */
	public void testGetAgeOfNewestQuotationInDays() {
		Date currentDate = new Date();
		LocalDate currentDateLocal = LocalDate.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		LocalDate newestQuotationDateLocal = LocalDate.ofInstant(this.quotation2.getDate().toInstant(), ZoneId.systemDefault());
		long actualNumberDays, expectedNumberDays = ChronoUnit.DAYS.between(newestQuotationDateLocal, currentDateLocal);
		
		actualNumberDays = this.instrument.getAgeOfNewestQuotationInDays();
		
		assertEquals(expectedNumberDays, actualNumberDays);
	}
	
	
	@Test
	/**
	 * Tests if the stock exchange is null, if the instrument type is set to 'RATIO'.
	 */
	public void testExchangeNullOnTypeRatio() {
		String expectedErrorMessage = this.resources.getString("instrument.exchangeDefinedOnTypeRatio");;
		
		this.instrument.setType(InstrumentType.RATIO);
		this.instrument.setSymbol(null);
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because Instrument is of type 'RATIO' and stock exchange is not null.");
		} catch (LocalizedException expected) {
			assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
		} catch (InstrumentReferenceException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests if the symbol is null, if the instrument type is set to 'RATIO'.
	 */
	public void testSymbolNullOnTypeRatio() {
		String expectedErrorMessage = this.resources.getString("instrument.symbolDefinedOnTypeRatio");
		
		this.instrument.setType(InstrumentType.RATIO);
		this.instrument.setStockExchange(null);
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because Instrument is of type 'RATIO' and symbol is not null.");
		} catch (LocalizedException expected) {
			assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
		} catch (InstrumentReferenceException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}