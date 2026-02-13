package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the MarketWatch Quotation DAO.
 *
 * @author Michael
 */
public class QuotationProviderMarketWatchDAOTest {
    /**
     * DAO to access quotation data from MarketWatch.
     */
    private static QuotationProviderMarketWatchDAOStub quotationProviderMarketWatchDAO;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        quotationProviderMarketWatchDAO = new QuotationProviderMarketWatchDAOStub();
    }

    /**
     * Tasks to be performed once at end of test class.
     */
    @AfterAll
    public static void tearDownClass() {
        quotationProviderMarketWatchDAO = null;
    }

    /**
     * Gets historical quotations of Denison Mines stock. The quotations of the three most recent trading days are
     * provided.
     *
     * @return Historical quotations of Denison Mines stock
     */
    private List<Quotation> getDenisonMinesQuotationHistory() {
        List<Quotation> historicalQuotations = new ArrayList<>();
        Quotation quotation = new Quotation();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final long volume1 = 1129780;
        final long volume2 = 1126381;
        final long volume3 = 793508;

        try {
            quotation.setDate(dateFormat.parse("10/21/2022"));
            quotation.setOpen(new BigDecimal("1.61"));
            quotation.setHigh(new BigDecimal("1.69"));
            quotation.setLow(new BigDecimal("1.60"));
            quotation.setClose(new BigDecimal("1.67"));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(volume1);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("10/20/2022"));
            quotation.setOpen(new BigDecimal("1.66"));
            quotation.setHigh(new BigDecimal("1.70"));
            quotation.setLow(new BigDecimal("1.61"));
            quotation.setClose(new BigDecimal("1.63"));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(volume2);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("10/19/2022"));
            quotation.setOpen(new BigDecimal("1.65"));
            quotation.setHigh(new BigDecimal("1.66"));
            quotation.setLow(new BigDecimal("1.62"));
            quotation.setClose(new BigDecimal("1.63"));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(volume3);
            historicalQuotations.add(quotation);
        } catch (ParseException e) {
            fail(e.getMessage());
        }

        return historicalQuotations;
    }

    /**
     * Gets historical quotations of Rio Tinto stock. The quotations of the three most recent trading days are provided.
     *
     * @return Historical quotations of Rio Tinto stock
     */
    private List<Quotation> getRioTintoQuotationHistory() {
        List<Quotation> historicalQuotations = new ArrayList<>();
        Quotation quotation = new Quotation();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final long volume1 = 3462068;
        final long volume2 = 3269297;
        final long volume3 = 3441729;

        try {
            quotation.setDate(dateFormat.parse("12/12/2022"));
            quotation.setOpen(new BigDecimal("5792"));
            quotation.setHigh(new BigDecimal("5805"));
            quotation.setLow(new BigDecimal("5666"));
            quotation.setClose(new BigDecimal("5679"));
            quotation.setCurrency(Currency.GBP);
            quotation.setVolume(volume1);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("12/09/2022"));
            quotation.setOpen(new BigDecimal("5800"));
            quotation.setHigh(new BigDecimal("5895"));
            quotation.setLow(new BigDecimal("5768"));
            quotation.setClose(new BigDecimal("5835"));
            quotation.setCurrency(Currency.GBP);
            quotation.setVolume(volume2);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("12/08/2022"));
            quotation.setOpen(new BigDecimal("5627"));
            quotation.setHigh(new BigDecimal("5804"));
            quotation.setLow(new BigDecimal("5608"));
            quotation.setClose(new BigDecimal("5780"));
            quotation.setCurrency(Currency.GBP);
            quotation.setVolume(volume3);
            historicalQuotations.add(quotation);
        } catch (ParseException e) {
            fail(e.getMessage());
        }

        return historicalQuotations;
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryTSX() {
        final String symbol = "DML";
        final StockExchange stockExchange = StockExchange.TSX;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/DML/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false&countrycode=CA";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSXV.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryTSXV() {
        final String symbol = "RCK";
        final StockExchange stockExchange = StockExchange.TSXV;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/RCK/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false&countrycode=CA";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the CSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryCSE() {
        final String symbol = "AGN";
        final StockExchange stockExchange = StockExchange.CSE;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/AGN/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false&countrycode=CA";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryNYSE() {
        final String symbol = "F";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/F/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the Nasdaq.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryNasdaq() {
        final String symbol = "AMZN";
        final StockExchange stockExchange = StockExchange.NDQ;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/AMZN/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the AMEX.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryAmex() {
        final String symbol = "PRK";
        final StockExchange stockExchange = StockExchange.AMEX;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/PRK/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the US OTC.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryOTC() {
        final String symbol = "BAYRY";
        final StockExchange stockExchange = StockExchange.OTC;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/BAYRY/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the LSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryLSE() {
        final String symbol = "RIO";
        final StockExchange stockExchange = StockExchange.LSE;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/STOCK/RIO/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false&countrycode=UK";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.STOCK, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of an ETF listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryETF() {
        final String symbol = "FFTY";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/FUND/FFTY/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.ETF, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of a sector listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistorySector() {
        final String symbol = "XLE";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/FUND/XLE/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1d"
                + "&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.SECTOR, years);
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Tests the retrieval of the query URL for historical quotations of an industry group listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlQuotationHistoryIndustryGroup() {
        final String symbol = "COPX";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;
        String expectedUrl = "https://www.marketwatch.com/investing/FUND/COPX/downloaddatapartial?"
                + "startdate={start_date}%2000:00:00&enddate={end_date}%2023:59:59&daterange=d30&frequency=p1"
                + "d&csvdownload=true&downloadpartial=false&newdates=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderMarketWatchDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderMarketWatchDAO.getDateForHistory(0));

        actualUrl = quotationProviderMarketWatchDAO.getQueryUrlQuotationHistory(symbol, stockExchange,
                InstrumentType.IND_GROUP, years);
        assertEquals(expectedUrl, actualUrl);
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
            actualQuotationHistory = quotationProviderMarketWatchDAO.getQuotationHistory(dmlStock, 1);
            expectedQuotationHistory = this.getDenisonMinesQuotationHistory();

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
            actualQuotationHistory = quotationProviderMarketWatchDAO.getQuotationHistory(rioStock, 1);
            expectedQuotationHistory = this.getRioTintoQuotationHistory();

            // 251 Trading days of a full year.
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
}
