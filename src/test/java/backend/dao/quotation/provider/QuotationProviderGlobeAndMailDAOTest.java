package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

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

    /**
     * Class providing helper methods for fixture.
     */
    private QuotationProviderGlobeAndMailDAOFixture fixtureHelper;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        quotationProviderGlobeAndMailDAO = new QuotationProviderGlobeAndMailDAOStub();
    }

    /**
     * Tasks to be performed once at end of test class.
     */
    @AfterAll
    public static void tearDownClass() {
        quotationProviderGlobeAndMailDAO = null;
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.fixtureHelper = new QuotationProviderGlobeAndMailDAOFixture();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.fixtureHelper = null;
    }

    /**
     * Gets a Quotation as expected from the theglobeandmail.com website.
     *
     * @return A Quotation.
     */
    private Quotation getPatriotBatteryMetalsQuotation() {
        Quotation quotation = new Quotation();

        quotation.setClose(new BigDecimal("17.17"));
        quotation.setCurrency(Currency.CAD);

        return quotation;
    }

    /**
     * Tests getting current Quotation data from a stock listed at the TSX/V.
     */
    @Test
    public void testGetCurrentQuotationTSXV() {
        Quotation actualQuotation;
        Quotation expectedQuotation;

        try {
            actualQuotation = quotationProviderGlobeAndMailDAO
                    .getCurrentQuotation(this.fixtureHelper.getPatriotBatteryMetalsInstrument());
            expectedQuotation = this.getPatriotBatteryMetalsQuotation();

            assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX/V.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationTSXV() {
        Instrument patriotBatteryMetalsStock = this.fixtureHelper.getPatriotBatteryMetalsInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/PMET-X/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(patriotBatteryMetalsStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationTSX() {
        Instrument denisonMinesStock = this.fixtureHelper.getDenisonMinesInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/DML-T/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(denisonMinesStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the CSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationCSE() {
        Instrument algernonStock = this.fixtureHelper.getAlgernonInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/AGN-CN/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(algernonStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationNYSE() {
        Instrument fordStock = this.fixtureHelper.getFordInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/F-N/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(fordStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the Nasdaq.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationNasdaq() {
        Instrument appleStock = this.fixtureHelper.getAppleInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/AAPL-Q/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(appleStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the AMEX.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationAMEX() {
        Instrument imperialOilStock = this.fixtureHelper.getImperialOilInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/IMO-A/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(imperialOilStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the US OTC.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationOTC() {
        Instrument bayerStock = this.fixtureHelper.getBayerInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/BAYRY/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(bayerStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationLSE() {
        Instrument glencoreStock = this.fixtureHelper.getGlencoreInstrument();

        try {
            quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(glencoreStock);
            fail("URL Determination should have failed because exchange LSE is not supported.");
        } catch (Exception expected) {
            // All is well.
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryNYSE() {
        final String symbol = "F";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=F&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the Nasdaq.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryNasdaq() {
        final String symbol = "AMZN";
        final StockExchange stockExchange = StockExchange.NDQ;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=AMZN&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the AMEX.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryAMEX() {
        final String symbol = "PRK";
        final StockExchange stockExchange = StockExchange.AMEX;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=PRK&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the US OTC.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryOTC() {
        final String symbol = "BAYRY";
        final StockExchange stockExchange = StockExchange.OTC;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=BAYRY&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryTSX() {
        final String symbol = "DML";
        final StockExchange stockExchange = StockExchange.TSX;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=DML.TO&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX/V.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryTSXV() {
        final String symbol = "RCK";
        final StockExchange stockExchange = StockExchange.TSXV;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=RCK.VN&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the CSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryCSE() {
        final String symbol = "AGN";
        final StockExchange stockExchange = StockExchange.CSE;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=AGN.CN&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the LSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryLSE() {
        final String symbol = "GLEN";
        final StockExchange stockExchange = StockExchange.LSE;
        final Integer years = 1;

        try {
            quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            fail("URL Determination should have failed because exchange LSE is not supported.");
        } catch (Exception expected) {
            // All is well.
        }
    }

    /**
     * Tests the retrieval of the quotation history of a stock traded at the TSX.
     */
    @Test
    public void testGetQuotationHistoryTSX() {
        QuotationArray actualQuotationHistory = new QuotationArray();
        List<Quotation> expectedQuotationHistory;
        Quotation actualQuotation;
        Quotation expectedQuotation;
        final int daysOfYear = 252;

        try {
            actualQuotationHistory.setQuotations(quotationProviderGlobeAndMailDAO
                    .getQuotationHistory(this.fixtureHelper.getDenisonMinesInstrument(), 1));
            expectedQuotationHistory = this.fixtureHelper.getDenisonMinesQuotationHistory();

            actualQuotationHistory.sortQuotationsByDate();

            // 252 Trading days of a full year.
            assertEquals(daysOfYear, actualQuotationHistory.getQuotations().size());

            // Check the three most recent quotations.
            actualQuotation = actualQuotationHistory.getQuotations().get(0);
            expectedQuotation = expectedQuotationHistory.get(0);
            assertEquals(expectedQuotation, actualQuotation);

            actualQuotation = actualQuotationHistory.getQuotations().get(1);
            expectedQuotation = expectedQuotationHistory.get(1);
            assertEquals(expectedQuotation, actualQuotation);

            actualQuotation = actualQuotationHistory.getQuotations().get(2);
            expectedQuotation = expectedQuotationHistory.get(2);
            assertEquals(expectedQuotation, actualQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
