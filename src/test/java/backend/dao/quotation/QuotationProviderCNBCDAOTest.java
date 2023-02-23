package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;

/**
 * Tests the CNBC Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderCNBCDAOTest {
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationProviderCNBCDAO quotationProviderCNBCDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderCNBCDAO = new QuotationProviderCNBCDAO();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderCNBCDAO = null;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
	 */
	public void testGetQueryUrlCurrentQuotationLSE() {
		final String symbol = "RIO";
		final StockExchange stockExchange = StockExchange.LSE;
		final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=RIO-GB"
				+ "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
		String actualURL = "";
		
		try {
			actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
			assertEquals(expectedURL, actualURL);			
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
	 */
	public void testGetQueryUrlCurrentQuotationNYSE() {
		final String symbol = "AAPL";
		final StockExchange stockExchange = StockExchange.NYSE;
		final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=AAPL"
				+ "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
		String actualURL = "";
		
		try {
			actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
			assertEquals(expectedURL, actualURL);			
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX.
	 */
	public void testGetQueryUrlCurrentQuotationTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=DML-CA"
				+ "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
		String actualURL = "";
		
		try {
			actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
			assertEquals(expectedURL, actualURL);			
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX/V.
	 */
	public void testGetQueryUrlCurrentQuotationTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=RCK-V"
				+ "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
		String actualURL = "";
		
		try {
			actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
			assertEquals(expectedURL, actualURL);			
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the CSE.
	 */
	public void testGetQueryUrlCurrentQuotationCSE() {
		final String symbol = "AGN";
		final StockExchange stockExchange = StockExchange.CSE;
		
		try {
			quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
			fail("URL determination should have failed because exchange 'CSE' is not supported.");	
		}
		catch(Exception expected) {
			//All is well.
		}
	}
}
