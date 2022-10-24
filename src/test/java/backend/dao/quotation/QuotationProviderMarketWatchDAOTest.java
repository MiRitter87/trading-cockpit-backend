package backend.dao.quotation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;


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
}
