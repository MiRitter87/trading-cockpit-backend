package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Tests the Investing Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderInvestingDAOTest {
	/**
	 * DAO to access quotation data from investing.com.
	 */
	private static QuotationProviderInvestingDAO quotationProviderInvestingDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderInvestingDAO = new QuotationProviderInvestingDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderInvestingDAO = null;
	}
	
	
	/**
	 * Gets a Quotation as expected from the investing.com website.
	 * 
	 * @return A Quotation.
	 */
	private Quotation getAmazonQuotation() {
		Quotation quotation = new Quotation();
		
		quotation.setClose(BigDecimal.valueOf(103.13));
		
		return quotation;
	}
	
	
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the NYSE.
	 */
	public void testGetCurrentQuotationNYSE() {
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotation = quotationProviderInvestingDAO.getCurrentQuotation("AMZN", StockExchange.NYSE);
			expectedQuotation = this.getAmazonQuotation();
			
			assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
