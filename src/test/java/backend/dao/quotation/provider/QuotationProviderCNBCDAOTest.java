package backend.dao.quotation.provider;

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
import backend.model.instrument.Quotation;

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
        quotationProviderCNBCDAO = new QuotationProviderCNBCDAOStub();
    }

    @AfterAll
    /**
     * Tasks to be performed once at end of test class.
     */
    public static void tearDownClass() {
        quotationProviderCNBCDAO = null;
    }

    /**
     * Gets a Quotation as expected from the CNBC API.
     *
     * @return A Quotation.
     */
    private Quotation getRioTintoQuotation() {
        Quotation quotation = new Quotation();

        quotation.setClose(BigDecimal.valueOf(5965));
        quotation.setCurrency(Currency.GBP);

        return quotation;
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
     */
    public void testGetQueryUrlCurrentQuotationLSE() {
        final String symbol = "RIO";
        final StockExchange stockExchange = StockExchange.LSE;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=RIO-GB"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
     */
    public void testGetQueryUrlCurrentQuotationNYSE() {
        final String symbol = "F";
        final StockExchange stockExchange = StockExchange.NYSE;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=F"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the Nasdaq.
     */
    public void testGetQueryUrlCurrentQuotationNasdaq() {
        final String symbol = "AAPL";
        final StockExchange stockExchange = StockExchange.NDQ;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=AAPL"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the AMEX.
     */
    public void testGetQueryUrlCurrentQuotationAmex() {
        final String symbol = "ARKK";
        final StockExchange stockExchange = StockExchange.AMEX;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=ARKK"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the US OTC.
     */
    public void testGetQueryUrlCurrentQuotationOTC() {
        final String symbol = "BAYRY";
        final StockExchange stockExchange = StockExchange.OTC;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=BAYRY"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX.
     */
    public void testGetQueryUrlCurrentQuotationTSX() {
        final String symbol = "DML";
        final StockExchange stockExchange = StockExchange.TSX;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=DML-CA"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX/V.
     */
    public void testGetQueryUrlCurrentQuotationTSXV() {
        final String symbol = "RCK";
        final StockExchange stockExchange = StockExchange.TSXV;
        final String expectedURL = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=RCK-V"
                + "&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
        String actualURL = "";

        try {
            actualURL = quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the CSE.
     */
    public void testGetQueryUrlCurrentQuotationCSE() {
        final String symbol = "AGN";
        final StockExchange stockExchange = StockExchange.CSE;

        try {
            quotationProviderCNBCDAO.getQueryUrlCurrentQuotation(symbol, stockExchange);
            fail("URL determination should have failed because exchange 'CSE' is not supported.");
        } catch (Exception expected) {
            // All is well.
        }
    }

    @Test
    /**
     * Tests getting current Quotation data from a stock listed at the LSE.
     */
    public void testGetCurrentQuotationLSE() {
        Quotation actualQuotation, expectedQuotation;

        try {
            actualQuotation = quotationProviderCNBCDAO.getCurrentQuotation(new Instrument("RIO", StockExchange.LSE));
            expectedQuotation = this.getRioTintoQuotation();

            assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the determination of the currency based on the currency string provided by the CNBC API.
     */
    public void testGetCurrency() {
        String apiCurrency;

        apiCurrency = "USD";
        assertEquals(Currency.USD, quotationProviderCNBCDAO.getCurrency(apiCurrency));

        apiCurrency = "CAD";
        assertEquals(Currency.CAD, quotationProviderCNBCDAO.getCurrency(apiCurrency));

        apiCurrency = "GBp";
        assertEquals(Currency.GBP, quotationProviderCNBCDAO.getCurrency(apiCurrency));

        apiCurrency = "";
        assertEquals(null, quotationProviderCNBCDAO.getCurrency(apiCurrency));
    }
}
