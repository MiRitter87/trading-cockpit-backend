package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the Yahoo Quotation DAO.
 *
 * @author Michael
 *
 */
public class QuotationProviderYahooDAOTest {
    /**
     * DAO to access quotation data from Yahoo.
     */
    private static QuotationProviderYahooDAO quotationProviderYahooDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private QuotationProviderYahooDAOFixture fixtureHelper;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
    }

    /**
     * Tasks to be performed once at end of test class.
     */
    @AfterAll
    public static void tearDownClass() {
        quotationProviderYahooDAO = null;
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.fixtureHelper = new QuotationProviderYahooDAOFixture();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.fixtureHelper = null;
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryTSX() {
        final String symbol = "DML";
        final StockExchange stockExchange = StockExchange.TSX;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "DML.TO?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSXV.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryTSXV() {
        final String symbol = "RCK";
        final StockExchange stockExchange = StockExchange.TSXV;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "RCK.V?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the CSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryCSE() {
        final String symbol = "AGN";
        final StockExchange stockExchange = StockExchange.CSE;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "AGN.CN?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryNYSE() {
        final String symbol = "F";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "F?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the Nasdaq.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryNasdaq() {
        final String symbol = "AMZN";
        final StockExchange stockExchange = StockExchange.NDQ;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "AMZN?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the AMEX.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryAmex() {
        final String symbol = "PRK";
        final StockExchange stockExchange = StockExchange.AMEX;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "PRK?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the US OTC.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryOTC() {
        final String symbol = "BAYRY";
        final StockExchange stockExchange = StockExchange.OTC;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "BAYRY?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the LSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryLSE() {
        final String symbol = "RIO";
        final StockExchange stockExchange = StockExchange.LSE;
        final Integer years = 1;
        final String expectedURL = "https://query1.finance.yahoo.com/v7/finance/chart/"
                + "RIO.L?range=1y&interval=1d&indicators=quote&includeTimestamps=true";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the quotation history of a stock traded at the TSX.
     */
    @Test
    public void testGetQuotationHistoryTSX() {
        List<Quotation> actualQuotationHistory;
        List<Quotation> expectedQuotationHistory;
        Quotation actualQuotation;
        Quotation expectedQuotation;
        Instrument dmlStock = new Instrument();
        final int expectedTradingDays = 252;

        dmlStock.setSymbol("DML");
        dmlStock.setStockExchange(StockExchange.TSX);
        dmlStock.setType(InstrumentType.STOCK);

        try {
            actualQuotationHistory = quotationProviderYahooDAO.getQuotationHistory(dmlStock, 1);
            expectedQuotationHistory = this.fixtureHelper.getDenisonMinesQuotationHistory();

            // 252 Trading days of a full year.
            assertEquals(expectedTradingDays, actualQuotationHistory.size());

            // Check the three most recent quotations.
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

    /**
     * Tests the retrieval of the quotation history of a stock traded at the LSE.
     */
    @Test
    public void testGetQuotationHistoryLSE() {
        List<Quotation> actualQuotationHistory;
        List<Quotation> expectedQuotationHistory;
        Quotation actualQuotation;
        Quotation expectedQuotation;
        Instrument rioStock = new Instrument();
        final int expectedTradingDays = 251;

        rioStock.setSymbol("RIO");
        rioStock.setStockExchange(StockExchange.LSE);
        rioStock.setType(InstrumentType.STOCK);

        try {
            actualQuotationHistory = quotationProviderYahooDAO.getQuotationHistory(rioStock, 1);
            expectedQuotationHistory = this.fixtureHelper.getRioTintoQuotationHistory();

            // 251 Trading days of a full year. Volume data are missing for a single day which is excluded. Therefore
            // 251 days instead of 252.
            assertEquals(expectedTradingDays, actualQuotationHistory.size());

            // Check the three most recent quotations.
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

    /**
     * Tests the retrieval of quotations from a quotation history that has incomplete data. Volume and/or price data are
     * missing partially. Only quotations should be created for JSON datasets that have both price and volume data on
     * the given day.
     */
    @Test
    public void testGetQuotationHistoryIncomplete() {
        final int expectedNumberOfQuotations = 14;
        int actualNumberOfQuotations;
        List<Quotation> actualQuotations;
        Instrument bnchStock = new Instrument();

        bnchStock.setSymbol("BNCH");
        bnchStock.setStockExchange(StockExchange.TSXV);
        bnchStock.setType(InstrumentType.STOCK);

        try {
            actualQuotations = quotationProviderYahooDAO.getQuotationHistory(bnchStock, 1);
            actualNumberOfQuotations = actualQuotations.size();

            assertEquals(expectedNumberOfQuotations, actualNumberOfQuotations);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartTSX() {
        final String symbol = "DML";
        final StockExchange stockExchange = StockExchange.TSX;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/DML.TO";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSXV.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartTSXV() {
        final String symbol = "RCK";
        final StockExchange stockExchange = StockExchange.TSXV;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/RCK.V";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the CSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartCSE() {
        final String symbol = "AGN";
        final StockExchange stockExchange = StockExchange.CSE;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/AGN.CN";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartNYSE() {
        final String symbol = "F";
        final StockExchange stockExchange = StockExchange.NYSE;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/F";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the Nasdaq.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartNasdaq() {
        final String symbol = "AMZN";
        final StockExchange stockExchange = StockExchange.NDQ;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/AMZN";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the AMEX.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartAmex() {
        final String symbol = "PRK";
        final StockExchange stockExchange = StockExchange.AMEX;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/PRK";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the US OTC.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartOTC() {
        final String symbol = "BAYRY";
        final StockExchange stockExchange = StockExchange.OTC;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/BAYRY";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationChartLSE() {
        final String symbol = "RIO";
        final StockExchange stockExchange = StockExchange.LSE;
        final String expectedURL = "https://query1.finance.yahoo.com/v8/finance/chart/RIO.L";
        String actualURL = "";

        actualURL = quotationProviderYahooDAO.getQueryUrlCurrentQuotationChart(symbol, stockExchange);
        assertEquals(expectedURL, actualURL);
    }

    /**
     * Tests getting current Quotation data from a stock listed at the TSE.
     */
    @Test
    public void testGetCurrentQuotationTSE() {
        Quotation actualQuotation;
        Quotation expectedQuotation;

        try {
            actualQuotation = quotationProviderYahooDAO.getCurrentQuotation(new Instrument("DML", StockExchange.TSX));
            expectedQuotation = this.fixtureHelper.getDenisonMinesQuotation();

            assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests getting current Quotation data from a stock listed at the LSE.
     */
    @Test
    public void testGetCurrentQuotationLSE() {
        Quotation actualQuotation;
        Quotation expectedQuotation;

        try {
            actualQuotation = quotationProviderYahooDAO.getCurrentQuotation(new Instrument("RIO", StockExchange.LSE));
            expectedQuotation = this.fixtureHelper.getRioTintoQuotation();

            assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
