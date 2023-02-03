package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
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
	
	
	/**
	 * Gets an Instrument of the Amazon stock.
	 * 
	 * @return Instrument of the Amazon stock.
	 */
	private Instrument getAmazonInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AMZN");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		instrument.setCompanyPathInvestingCom("amazon-com-inc");
		
		return instrument;
	}
	
	
	/**
	 * Gets an Instrument of the Dow Jones Industrial ETF.
	 * 
	 * @return Instrument of the Dow Jones Industrial ETF.
	 */
	private Instrument getDowJonesIndustrialETF() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("DIA");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.ETF);
		instrument.setCompanyPathInvestingCom("diamonds-trust");
		
		return instrument;
	}
	
	
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the NYSE.
	 */
	public void testGetCurrentQuotationNYSE() {
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotation = quotationProviderInvestingDAO.getCurrentQuotation(this.getAmazonInstrument());
			expectedQuotation = this.getAmazonQuotation();
			
			assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
			assertEquals(Currency.USD, actualQuotation.getCurrency());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock.
	 */
	public void testGetQueryUrlCurrentQuotationStock() {
		Instrument amazonStock = this.getAmazonInstrument();
		final String expectedURL = "https://www.investing.com/equities/amazon-com-inc";
		String actualURL = "";
		
		actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of an ETF.
	 */
	public void testGetQueryUrlCurrentQuotationETF() {
		Instrument diaETF = this.getDowJonesIndustrialETF();
		final String expectedURL = "https://www.investing.com/etfs/diamonds-trust";
		String actualURL = "";
		
		actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(diaETF);
		assertEquals(expectedURL, actualURL);
	}
}
