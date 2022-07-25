package backend.dao.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Quotation;

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
	
	
	/**
	 * Gets historical quotations of Denison Mines stock.
	 * The quotations of the three most recent trading days are provided.
	 * 
	 * @return Historical quotations of Denison Mines stock
	 */
	private List<Quotation> getDenisonMinesQuotationHistory() {
		List<Quotation> historicalQuotations = new ArrayList<>();
		Quotation quotation = new Quotation();
		
		quotation.setDate(new Date(1658496600 * 1000));
		quotation.setPrice(BigDecimal.valueOf(1.36));
		quotation.setCurrency(Currency.CAD);
		quotation.setVolume(1793300);
		historicalQuotations.add(quotation);
		
		quotation = new Quotation();
		quotation.setDate(new Date(1658410200  * 1000));
		quotation.setPrice(BigDecimal.valueOf(1.46));
		quotation.setCurrency(Currency.CAD);
		quotation.setVolume(1450900);
		historicalQuotations.add(quotation);
		
		quotation = new Quotation();
		quotation.setDate(new Date(1658323800  * 1000));
		quotation.setPrice(BigDecimal.valueOf(1.53));
		quotation.setCurrency(Currency.CAD);
		quotation.setVolume(1534800);
		historicalQuotations.add(quotation);
		
		return historicalQuotations;
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
	
	
	@Test
	/**
	 * Tests the retrieval of the quotation history of a stock traded at the TSX.
	 */
	public void testGetQuotationHistoryTSE() {
		List<Quotation> actualQuotationHistory, expectedQuotationHistory;
		
		try {
			actualQuotationHistory = quotationYahooDAO.getQuotationHistory("DML", StockExchange.TSX, 1);
			expectedQuotationHistory = this.getDenisonMinesQuotationHistory();
			
			//252 Trading days of a full year.
			assertEquals(252, actualQuotationHistory.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
