package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;


/**
 * Tests the MarketWatch Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderMarketWatchDAOTest {
	/**
	 * DAO to access quotation data from MarketWatch.
	 */
	private static QuotationProviderMarketWatchDAOStub quotationProviderMarketWatchDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderMarketWatchDAO = new QuotationProviderMarketWatchDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderMarketWatchDAO = null;
	}
	
	
	/**
	 * Gets historical quotations of Denison Mines stock.
	 * The quotations of the three most recent trading days are provided.
	 * 
	 * @return Historical quotations of Denison Mines stock
	 */
	private List<Quotation> getDenisonMinesQuotationHistory() {
		List<Quotation> historicalQuotations = new ArrayList<>();
		Quotation quotation = new Quotation();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		try {
			quotation.setDate(dateFormat.parse("10/21/2022"));
			quotation.setOpen(BigDecimal.valueOf(1.61));
			quotation.setHigh(BigDecimal.valueOf(1.69));
			quotation.setLow(BigDecimal.valueOf(1.60));
			quotation.setClose(BigDecimal.valueOf(1.67));
			quotation.setCurrency(Currency.CAD);
			quotation.setVolume(1129780);
			historicalQuotations.add(quotation);
			
			quotation = new Quotation();
			quotation.setDate(dateFormat.parse("10/20/2022"));
			quotation.setOpen(BigDecimal.valueOf(1.66));
			quotation.setHigh(BigDecimal.valueOf(1.70));
			quotation.setLow(BigDecimal.valueOf(1.61));
			quotation.setClose(BigDecimal.valueOf(1.63));
			quotation.setCurrency(Currency.CAD);
			quotation.setVolume(1126381);
			historicalQuotations.add(quotation);
			
			quotation = new Quotation();
			quotation.setDate(dateFormat.parse("10/19/2022"));
			quotation.setOpen(BigDecimal.valueOf(1.65));
			quotation.setHigh(BigDecimal.valueOf(1.66));
			quotation.setLow(BigDecimal.valueOf(1.62));
			quotation.setClose(BigDecimal.valueOf(1.63));
			quotation.setCurrency(Currency.CAD);
			quotation.setVolume(793508);
			historicalQuotations.add(quotation);
		} catch (ParseException e) {
			fail(e.getMessage());
		}
		
		return historicalQuotations;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
	 */
	public void testGetQueryUrlQuotationHistoryTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/STOCK/DML/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false&countrycode=CA";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.STOCK, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSXV.
	 */
	public void testGetQueryUrlQuotationHistoryTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/STOCK/RCK/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false&countrycode=CA";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.STOCK, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the CSE.
	 */
	public void testGetQueryUrlQuotationHistoryCSE() {
		final String symbol = "AGN";
		final StockExchange stockExchange = StockExchange.CSE;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/STOCK/AGN/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false&countrycode=CA";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.STOCK, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the NYSE.
	 */
	public void testGetQueryUrlQuotationHistoryNYSE() {
		final String symbol = "F";
		final StockExchange stockExchange = StockExchange.NYSE;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/STOCK/F/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.STOCK, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of an ETF listed at the NYSE.
	 */
	public void testGetQueryUrlQuotationHistoryETF() {
		final String symbol = "FFTY";
		final StockExchange stockExchange = StockExchange.NYSE;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/FUND/FFTY/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.ETF, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a sector listed at the NYSE.
	 */
	public void testGetQueryUrlQuotationHistorySector() {
		final String symbol = "XLE";
		final StockExchange stockExchange = StockExchange.NYSE;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/FUND/XLE/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.SECTOR, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of an industry group listed at the NYSE.
	 */
	public void testGetQueryUrlQuotationHistoryIndustryGroup() {
		final String symbol = "COPX";
		final StockExchange stockExchange = StockExchange.NYSE;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/FUND/COPX/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, InstrumentType.IND_GROUP, years);
		assertEquals(expectedUrl, actualUrl);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the quotation history of a stock traded at the TSX.
	 */
	public void testGetQuotationHistoryTSX() {
		List<Quotation> actualQuotationHistory, expectedQuotationHistory;
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotationHistory = quotationProviderMarketWatchDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1);
			expectedQuotationHistory = this.getDenisonMinesQuotationHistory();
			
			//252 Trading days of a full year.
			assertEquals(252, actualQuotationHistory.size());
			
			//Check the three most recent quotations.
			actualQuotation = actualQuotationHistory.get(0);
			expectedQuotation = expectedQuotationHistory.get(0);
			assertEquals(expectedQuotation, actualQuotation);
			
			actualQuotation = actualQuotationHistory.get(1);
			expectedQuotation = expectedQuotationHistory.get(1);
			assertEquals(expectedQuotation, actualQuotation);
			
			actualQuotation = actualQuotationHistory.get(2);
			expectedQuotation = expectedQuotationHistory.get(2);
			assertEquals(expectedQuotation, actualQuotation);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
