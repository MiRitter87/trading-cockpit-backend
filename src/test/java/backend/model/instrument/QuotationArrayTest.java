package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;

/**
 * Tests the QuotationArray model.
 * 
 * @author Michael
 */
public class QuotationArrayTest {
	/**
	 * The QuotationArray under test.
	 */
	private QuotationArray quotationArray;
	
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationProviderDAO quotationProviderYahooDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		try {
			quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at the end of the test class.
	 */
	public static void tearDownClass() {
		quotationProviderYahooDAO = null;
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		List<Quotation> quotations = new ArrayList<>();
		
		try {
			quotations.addAll(quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1));
			this.quotationArray = new QuotationArray(quotations);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.quotationArray = null;
	}
	
	
	@Test
	/**
	 * Tests the determination of the age of the newest Quotation within the QuotationArray.
	 */
	public void testGetAgeOfNewestQuotationInDays() {
		Date currentDate = new Date();
		LocalDate currentDateLocal = LocalDate.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		LocalDate newestQuotationDateLocal;
		long actualNumberDays, expectedNumberDays;
		Quotation newestQuotation;
		
		this.quotationArray.sortQuotationsByDate();
		newestQuotation = this.quotationArray.getQuotations().get(0);
		
		newestQuotationDateLocal = LocalDate.ofInstant(newestQuotation.getDate().toInstant(), ZoneId.systemDefault());
		expectedNumberDays = ChronoUnit.DAYS.between(newestQuotationDateLocal, currentDateLocal);
		
		actualNumberDays = this.quotationArray.getAgeOfNewestQuotationInDays();
		
		assertEquals(expectedNumberDays, actualNumberDays);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of quotations that are older than the given Quotation but still on the same day.
	 */
	public void testGetOlderQuotationsOfSameDay() {
		List<Quotation> olderQuotationsSameDay;
		Quotation olderQuotationOfSameDay, addedQuotation, quotation;
		Calendar calendar = Calendar.getInstance();
		
		//The newest Quotation of a day.
		quotation = this.quotationArray.getQuotations().get(0);
		calendar.setTime(quotation.getDate());
		calendar.add(Calendar.MINUTE, -1);
		
		//Add an additional Quotation to the array that is older but at the same day.
		addedQuotation = new Quotation();
		addedQuotation.setDate(calendar.getTime());
		addedQuotation.setCurrency(quotation.getCurrency());
		addedQuotation.setVolume(150000);
		addedQuotation.setOpen(new BigDecimal(1.95));
		addedQuotation.setHigh(new BigDecimal(2.05));
		addedQuotation.setLow(new BigDecimal(1.95));
		addedQuotation.setClose(new BigDecimal(2.00));
		this.quotationArray.getQuotations().add(addedQuotation);
		
		olderQuotationsSameDay = this.quotationArray.getOlderQuotationsOfSameDay(quotation.getDate());
		
		assertEquals(1, olderQuotationsSameDay.size());
		
		olderQuotationOfSameDay = olderQuotationsSameDay.get(0);
		assertEquals(addedQuotation, olderQuotationOfSameDay);
	}
}
