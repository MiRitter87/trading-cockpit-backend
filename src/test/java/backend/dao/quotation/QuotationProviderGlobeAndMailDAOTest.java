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
 * Tests the GlobeAndMail Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAOTest {
	/**
	 * DAO to access quotation data from theglobeandmail.com.
	 */
	private static QuotationProviderGlobeAndMailDAO quotationProviderGlobeAndMailDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderGlobeAndMailDAO = new QuotationProviderGlobeAndMailDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderGlobeAndMailDAO = null;
	}
	
	
	/**
	 * Gets an Instrument of the Patriot Battery Metals stock.
	 * 
	 * @return Instrument of the Patriot Battery Metals stock.
	 */
	private Instrument getPatriotBatteryMetalsInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("PMET");
		instrument.setStockExchange(StockExchange.TSXV);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets a Quotation as expected from the theglobeandmail.com website.
	 * 
	 * @return A Quotation.
	 */
	private Quotation getPatriotBatteryMetalsQuotation() {
		Quotation quotation = new Quotation();
		
		quotation.setClose(BigDecimal.valueOf(17.17));
		quotation.setCurrency(Currency.CAD);
		
		return quotation;
	}
	
	
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the TSX/V.
	 */
	public void testGetCurrentQuotationTSXV() {
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotation = quotationProviderGlobeAndMailDAO.getCurrentQuotation(this.getPatriotBatteryMetalsInstrument());
			expectedQuotation = this.getPatriotBatteryMetalsQuotation();
			
			assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
			assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
