package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;


/**
 * Tests the MarketWatch Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderMarketWatchDAOTest {
	/**
	 * DAO to access quotation data from MarketWatch.
	 */
	private static QuotationProviderMarketWatchDAO quotationProviderMarketWatchDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderMarketWatchDAO = new QuotationProviderMarketWatchDAO();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderMarketWatchDAO = null;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
	 */
	public void testGetQueryUrlQuotationHistoryTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final Integer years = 1;
		String expectedUrl = 	"https://www.marketwatch.com/investing/stock/DML/downloaddatapartial?"
				+ "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&"
				+ "downloadpartial=false&newdates=false&countrycode=CA";
		String actualUrl = "";
		
		//Replace start and end date with the current date.
		expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
		expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));
		
		actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedUrl, actualUrl);
	}
}
