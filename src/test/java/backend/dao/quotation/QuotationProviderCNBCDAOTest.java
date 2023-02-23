package backend.dao.quotation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

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
}
