package backend.dao.stockQuote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.stockQuote.StockQuote;

/**
 * Tests the Yahoo stock quote DAO.
 * 
 * @author Michael
 */
public class StockQuoteYahooDAOTest {
	/**
	 * DAO to access stock data from Yahoo.
	 */
	private static StockQuoteYahooDAO stockQuoteYahooDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		stockQuoteYahooDAO = new StockQuoteYahooDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		stockQuoteYahooDAO = null;
	}
	
	
	/**
	 * Gets a stock quote as expected from the Yahoo service.
	 * 
	 * @return A stock quote
	 */
	private StockQuote getDenisonMinesQuote() {
		StockQuote stockQuote = new StockQuote();
		
		stockQuote.setSymbol("DML");
		stockQuote.setStockExchange(StockExchange.TSX);
		stockQuote.setPrice(BigDecimal.valueOf(1.88));
		stockQuote.setCurrency(Currency.CAD);
		
		return stockQuote;
	}
	
	
	@Test
	/**
	 * Tests getting  data from a stock listed at the TSE.
	 */
	public void testGetStockQuoteTSE() {
		StockQuote actualStockQuote, expectedStockQuote;
		
		try {
			actualStockQuote = stockQuoteYahooDAO.getStockQuote("DML", StockExchange.TSX);
			expectedStockQuote = this.getDenisonMinesQuote();
			
			assertEquals(expectedStockQuote.getSymbol(), actualStockQuote.getSymbol());
			assertEquals(expectedStockQuote.getStockExchange(), actualStockQuote.getStockExchange());
			assertTrue(expectedStockQuote.getPrice().compareTo(actualStockQuote.getPrice()) == 0);
			assertEquals(expectedStockQuote.getCurrency(), actualStockQuote.getCurrency());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for a stock listed at the TSX.
	 */
	public void testGetQueryUrlTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=DML.TO";
		String actualURL = "";
		
		actualURL = stockQuoteYahooDAO.getQueryUrl(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for a stock listed at the TSXV.
	 */
	public void testGetQueryUrlTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=RCK.V";
		String actualURL = "";
		
		actualURL = stockQuoteYahooDAO.getQueryUrl(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for a stock listed at the NYSE.
	 */
	public void testGetQueryUrlNYSE() {
		final String symbol = "F";
		final StockExchange stockExchange = StockExchange.NYSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=F";
		String actualURL = "";
		
		actualURL = stockQuoteYahooDAO.getQueryUrl(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
}
