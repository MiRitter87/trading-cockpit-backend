package backend.dao.stockQuote;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

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
}
