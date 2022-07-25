package backend.dao.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;

/**
 * Tests the Yahoo quotation DAO.
 * 
 * @author Michael
 *
 */
public class QuotationYahooDAOTest {
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationYahooDAO quotationYahooDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationYahooDAO = new QuotationYahooDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationYahooDAO = null;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
	 */
	public void testGetQueryUrlQuotationHistoryTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/DML.TO?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for a stock listed at the TSXV.
	 */
	public void testGetQueryUrlTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/RCK.V?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for a stock listed at the NYSE.
	 */
	public void testGetQueryUrlNYSE() {
		final String symbol = "F";
		final StockExchange stockExchange = StockExchange.NYSE;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/F?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
}
