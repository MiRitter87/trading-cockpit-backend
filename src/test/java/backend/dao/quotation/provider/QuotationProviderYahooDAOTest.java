package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.dao.quotation.provider.QuotationProviderYahooDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the Yahoo quotation DAO.
 * 
 * @author Michael
 *
 */
public class QuotationProviderYahooDAOTest {
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationProviderYahooDAO quotationProviderYahooDAO;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderYahooDAO = null;
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
		long secondsSince1970;
		
		secondsSince1970 = 1658496600;
		quotation.setDate(new Date(secondsSince1970 * 1000));
		quotation.setOpen(BigDecimal.valueOf(1.45));
		quotation.setHigh(BigDecimal.valueOf(1.48));
		quotation.setLow(BigDecimal.valueOf(1.35));
		quotation.setClose(BigDecimal.valueOf(1.36));
		quotation.setCurrency(Currency.CAD);
		quotation.setVolume(1793300);
		historicalQuotations.add(quotation);
		
		quotation = new Quotation();
		secondsSince1970 = 1658410200;
		quotation.setDate(new Date(secondsSince1970  * 1000));
		quotation.setOpen(BigDecimal.valueOf(1.50));
		quotation.setHigh(BigDecimal.valueOf(1.52));
		quotation.setLow(BigDecimal.valueOf(1.44));
		quotation.setClose(BigDecimal.valueOf(1.46));
		quotation.setCurrency(Currency.CAD);
		quotation.setVolume(1450900);
		historicalQuotations.add(quotation);
		
		quotation = new Quotation();
		secondsSince1970 = 1658323800;
		quotation.setDate(new Date(secondsSince1970  * 1000));
		quotation.setOpen(BigDecimal.valueOf(1.49));
		quotation.setHigh(BigDecimal.valueOf(1.54));
		quotation.setLow(BigDecimal.valueOf(1.46));
		quotation.setClose(BigDecimal.valueOf(1.53));
		quotation.setCurrency(Currency.CAD);
		quotation.setVolume(1534800);
		historicalQuotations.add(quotation);
		
		return historicalQuotations;
	}
	
	
	/**
	 * Gets historical quotations of Rio Tinto stock.
	 * The quotations of the three most recent trading days are provided.
	 * 
	 * @return Historical quotations of Rio Tinto stock
	 */
	private List<Quotation> getRioTintoQuotationHistory() {
		List<Quotation> historicalQuotations = new ArrayList<>();
		Quotation quotation = new Quotation();
		long secondsSince1970;
		
		secondsSince1970 = 1672823859;
		quotation.setDate(new Date(secondsSince1970 * 1000));
		quotation.setOpen(BigDecimal.valueOf(5910));
		quotation.setHigh(BigDecimal.valueOf(5941));
		quotation.setLow(BigDecimal.valueOf(5834));
		quotation.setClose(BigDecimal.valueOf(5835));
		quotation.setCurrency(Currency.GBP);
		quotation.setVolume(243671);
		historicalQuotations.add(quotation);
		
		quotation = new Quotation();
		secondsSince1970 = 1672732800;
		quotation.setDate(new Date(secondsSince1970  * 1000));
		quotation.setOpen(BigDecimal.valueOf(5818));
		quotation.setHigh(BigDecimal.valueOf(5905));
		quotation.setLow(BigDecimal.valueOf(5810));
		quotation.setClose(BigDecimal.valueOf(5839));
		quotation.setCurrency(Currency.GBP);
		quotation.setVolume(2112533);
		historicalQuotations.add(quotation);
		
		quotation = new Quotation();
		secondsSince1970 = 1672387200;
		quotation.setDate(new Date(secondsSince1970  * 1000));
		quotation.setOpen(BigDecimal.valueOf(5803));
		quotation.setHigh(BigDecimal.valueOf(5846));
		quotation.setLow(BigDecimal.valueOf(5787));
		quotation.setClose(BigDecimal.valueOf(5798));
		quotation.setCurrency(Currency.GBP);
		quotation.setVolume(588428);
		historicalQuotations.add(quotation);
		
		return historicalQuotations;
	}
	
	
	/**
	 * Gets a Quotation as expected from the Yahoo service.
	 * 
	 * @return A Quotation.
	 */
	private Quotation getDenisonMinesQuotation() {
		Quotation quotation = new Quotation();
		
		quotation.setClose(BigDecimal.valueOf(1.39));
		quotation.setCurrency(Currency.CAD);
		
		return quotation;
	}
	
	
	/**
	 * Gets a Quotation as expected from the Yahoo service.
	 * 
	 * @return A Quotation.
	 */
	private Quotation getRioTintoQuotation() {
		Quotation quotation = new Quotation();
		
		quotation.setClose(BigDecimal.valueOf(4821.5));
		quotation.setCurrency(Currency.GBP);
		
		return quotation;
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
	 */
	public void testGetQueryUrlQuotationHistoryTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final Integer years = 1;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/DML.TO?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSXV.
	 */
	public void testGetQueryUrlQuotationHistoryTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final Integer years = 1;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/RCK.V?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the CSE.
	 */
	public void testGetQueryUrlQuotationHistoryCSE() {
		final String symbol = "AGN";
		final StockExchange stockExchange = StockExchange.CSE;
		final Integer years = 1;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/AGN.CN?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the NYSE.
	 */
	public void testGetQueryUrlQuotationHistoryNYSE() {
		final String symbol = "F";
		final StockExchange stockExchange = StockExchange.NYSE;
		final Integer years = 1;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/F?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the Nasdaq.
	 */
	public void testGetQueryUrlQuotationHistoryNasdaq() {
		final String symbol = "AMZN";
		final StockExchange stockExchange = StockExchange.NDQ;
		final Integer years = 1;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/AMZN?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for historical quotations of a stock listed at the LSE.
	 */
	public void testGetQueryUrlQuotationHistoryLSE() {
		final String symbol = "RIO";
		final StockExchange stockExchange = StockExchange.LSE;
		final Integer years = 1;
		final String expectedURL = 
				"https://query1.finance.yahoo.com/v7/finance/chart/RIO.L?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the quotation history of a stock traded at the TSX.
	 */
	public void testGetQuotationHistoryTSX() {
		List<Quotation> actualQuotationHistory, expectedQuotationHistory;
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotationHistory = quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1);
			expectedQuotationHistory = this.getDenisonMinesQuotationHistory();
			
			//252 Trading days of a full year.
			assertEquals(252, actualQuotationHistory.size());
			
			//Check the three most recent quotations.
			actualQuotation = actualQuotationHistory.get(0);
			expectedQuotation = expectedQuotationHistory.get(0);
			assertEquals(expectedQuotation, actualQuotation);
			
			actualQuotation = actualQuotationHistory.get(1);
			expectedQuotation = expectedQuotationHistory.get(1);
			assertEquals(expectedQuotation, actualQuotation);
			
			actualQuotation = actualQuotationHistory.get(2);
			expectedQuotation = expectedQuotationHistory.get(2);
			assertEquals(expectedQuotation, actualQuotation);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the quotation history of a stock traded at the LSE.
	 */
	public void testGetQuotationHistoryLSE() {
		List<Quotation> actualQuotationHistory, expectedQuotationHistory;
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotationHistory = quotationProviderYahooDAO.getQuotationHistory("RIO", StockExchange.LSE, InstrumentType.STOCK, 1);
			expectedQuotationHistory = this.getRioTintoQuotationHistory();
			
			//251 Trading days of a full year. Volume data are missing for a single day which is excluded. Therefore 251 days instead of 252.
			assertEquals(251, actualQuotationHistory.size());
			
			//Check the three most recent quotations.
			actualQuotation = actualQuotationHistory.get(0);
			expectedQuotation = expectedQuotationHistory.get(0);
			assertEquals(expectedQuotation, actualQuotation);
			
			actualQuotation = actualQuotationHistory.get(1);
			expectedQuotation = expectedQuotationHistory.get(1);
			assertEquals(expectedQuotation, actualQuotation);
			
			actualQuotation = actualQuotationHistory.get(2);
			expectedQuotation = expectedQuotationHistory.get(2);
			assertEquals(expectedQuotation, actualQuotation);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests the retrieval of quotations from a quotation history that has incomplete data.
	 * Volume and/or price data are missing partially.
	 * Only quotations should be created for JSON datasets that have both price and volume data on the given day.
	 */
	public void testGetQuotationHistoryIncomplete() {
		final int expectedNumberOfQuotations = 14, actualNumberOfQuotations;
		List<Quotation> actualQuotations;
		
		try {
			actualQuotations = quotationProviderYahooDAO.getQuotationHistory("BNCH", StockExchange.TSXV, InstrumentType.STOCK, 1);
			actualNumberOfQuotations = actualQuotations.size();
			
			assertEquals(expectedNumberOfQuotations, actualNumberOfQuotations);
		} 
		catch(Exception exception) {
			fail(exception.getMessage());
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX.
	 */
	public void testGetQueryUrlCurrentQuotationQuoteTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=DML.TO";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationQuote(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSXV.
	 */
	public void testGetQueryUrlCurrentQuotationQuoteTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=RCK.V";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationQuote(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the CSE.
	 */
	public void testGetQueryUrlCurrentQuotationQuoteCSE() {
		final String symbol = "AGN";
		final StockExchange stockExchange = StockExchange.CSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=AGN.CN";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationQuote(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
	 */
	public void testGetQueryUrlCurrentQuotationQuoteNYSE() {
		final String symbol = "F";
		final StockExchange stockExchange = StockExchange.NYSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=F";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationQuote(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
	 */
	public void testGetQueryUrlCurrentQuotationQuoteLSE() {
		final String symbol = "RIO";
		final StockExchange stockExchange = StockExchange.LSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=RIO.L";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationQuote(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX.
	 */
	public void testGetQueryUrlCurrentQuotationChartTSX() {
		final String symbol = "DML";
		final StockExchange stockExchange = StockExchange.TSX;
		final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/DML.TO";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSXV.
	 */
	public void testGetQueryUrlCurrentQuotationChartTSXV() {
		final String symbol = "RCK";
		final StockExchange stockExchange = StockExchange.TSXV;
		final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/RCK.V";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the CSE.
	 */
	public void testGetQueryUrlCurrentQuotationChartCSE() {
		final String symbol = "AGN";
		final StockExchange stockExchange = StockExchange.CSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/AGN.CN";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
	 */
	public void testGetQueryUrlCurrentQuotationChartNYSE() {
		final String symbol = "F";
		final StockExchange stockExchange = StockExchange.NYSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/F";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the Nasdaq.
	 */
	public void testGetQueryUrlCurrentQuotationChartNasdaq() {
		final String symbol = "AMZN";
		final StockExchange stockExchange = StockExchange.NDQ;
		final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/AMZN";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
	 */
	public void testGetQueryUrlCurrentQuotationChartLSE() {
		final String symbol = "RIO";
		final StockExchange stockExchange = StockExchange.LSE;
		final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/RIO.L";
		String actualURL = "";
		
		actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
		assertEquals(expectedURL, actualURL);
	}
	
	
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the TSE.
	 */
	public void testGetCurrentQuotationTSE() {
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotation = quotationProviderYahooDAO.getCurrentQuotation(new Instrument("DML", StockExchange.TSX));
			expectedQuotation = this.getDenisonMinesQuotation();
			
			assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
			assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the LSE.
	 */
	public void testGetCurrentQuotationLSE() {
		Quotation actualQuotation, expectedQuotation;
		
		try {
			actualQuotation = quotationProviderYahooDAO.getCurrentQuotation(new Instrument("RIO", StockExchange.LSE));
			expectedQuotation = this.getRioTintoQuotation();
			
			assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
			assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}