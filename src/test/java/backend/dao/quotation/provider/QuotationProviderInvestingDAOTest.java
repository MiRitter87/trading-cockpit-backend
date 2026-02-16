package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
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

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        quotationProviderInvestingDAO = new QuotationProviderInvestingDAOStub();
    }

    /**
     * Tasks to be performed once at end of test class.
     */
    @AfterAll
    public static void tearDownClass() {
        quotationProviderInvestingDAO = null;
    }

    /**
     * Gets a Quotation as expected from the investing.com Website.
     *
     * @return A Quotation.
     */
    private Quotation getAmazonQuotation() {
        Quotation quotation = new Quotation();
        final long timestamp = 1731542400000L;
        final long volume = 44923940;

        quotation.setDate(new Date(timestamp));
        quotation.setOpen(new BigDecimal("225.02"));
        quotation.setHigh(new BigDecimal("228.87"));
        quotation.setLow(new BigDecimal("225"));
        quotation.setClose(new BigDecimal("228.22"));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(volume);

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
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);
        instrument.setInvestingId("6435");

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
        instrument.setInvestingId("504");

        return instrument;
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
        final long date1 = 1732492800000L;
        final long date2 = 1732233600000L;
        final long date3 = 1732147200000L;
        final long volume1 = 2827091;
        final long volume2 = 1835032;
        final long volume3 = 2075737;

        quotation.setDate(new Date(date1));
        quotation.setOpen(new BigDecimal("3.36"));
        quotation.setHigh(new BigDecimal("3.40"));
        quotation.setLow(new BigDecimal("3.18"));
        quotation.setClose(new BigDecimal("3.24"));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(volume1);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        quotation.setDate(new Date(date2));
        quotation.setOpen(new BigDecimal("3.33"));
        quotation.setHigh(new BigDecimal("3.34"));
        quotation.setLow(new BigDecimal("3.25"));
        quotation.setClose(new BigDecimal("3.32"));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(volume2);
        historicalQuotations.add(quotation);

        quotation = new Quotation();
        quotation.setDate(new Date(date3));
        quotation.setOpen(new BigDecimal("3.18"));
        quotation.setHigh(new BigDecimal("3.34"));
        quotation.setLow(new BigDecimal("3.17"));
        quotation.setClose(new BigDecimal("3.34"));
        quotation.setCurrency(Currency.CAD);
        quotation.setVolume(volume3);
        historicalQuotations.add(quotation);

        return historicalQuotations;
    }

    /**
     * Tests getting current Quotation data from a stock listed at the NYSE.
     */
    @Test
    public void testGetCurrentQuotationNYSE() {
        Quotation actualQuotation;
        Quotation expectedQuotation;

        try {
            actualQuotation = quotationProviderInvestingDAO.getCurrentQuotation(this.getAmazonInstrument());
            expectedQuotation = this.getAmazonQuotation();

            assertEquals(expectedQuotation, actualQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationNYSE() {
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.NYSE);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.AMEX);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT5M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.OTC);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.TSX);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX/V.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationTSXV() {
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT5M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.TSXV);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT5M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.CSE);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.LSE);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation of an ETF.
     */
    @Test
    public void testGetQueryUrlCurrentQuotationETF() {
        Instrument diaETF = this.getDowJonesIndustrialETF();
        final String expectedURL = "https://api.investing.com/api/financialdata/"
                + "504/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(diaETF);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the current quotation if attribute 'investingId' of Instrument is not
     * defined.
     */
    @Test
    public void testGetQueryUrlCurrentWithoutInvestingId() {
        Instrument amazonStock = this.getAmazonInstrument();

        amazonStock.setInvestingId("");

        try {
            quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            fail("Determination of URL should have failed because attribute 'investingId' is not defined.");
        } catch (Exception expected) {
            // All is well.
        }
    }

    /**
     * Tests the retrieval of the query URL for the Quotation history.
     */
    @Test
    public void testGetQueryUrlQuotationHistory() {
        Instrument amazonStock = this.getAmazonInstrument();
        final Integer years = 1;
        String expectedUrl = "https://api.investing.com/api/financialdata/historical/6435"
                + "?start-date={start_date}&end-date={end_date}&time-frame=Daily&add-missing-rows=false";
        String actualUrl = "";

        // Replace start and end date with the current date.
        expectedUrl = expectedUrl.replace("{start_date}", quotationProviderInvestingDAO.getDateForHistory(-1));
        expectedUrl = expectedUrl.replace("{end_date}", quotationProviderInvestingDAO.getDateForHistory(0));

        try {
            actualUrl = quotationProviderInvestingDAO.getQueryUrlQuotationHistory(amazonStock, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the query URL for the quotation history if attribute 'investingId' of Instrument is not
     * defined.
     */
    @Test
    public void testGetQueryUrlHistoryWithoutInvestingId() {
        Instrument amazonStock = this.getAmazonInstrument();

        amazonStock.setInvestingId("");

        try {
            quotationProviderInvestingDAO.getQueryUrlQuotationHistory(amazonStock, 1);
            fail("Determination of URL should have failed because attribute 'investingId' is not defined.");
        } catch (Exception expected) {
            // All is well.
        }
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
        final int expectedTradingDays = 251;

        dmlStock.setSymbol("DML");
        dmlStock.setStockExchange(StockExchange.TSX);
        dmlStock.setType(InstrumentType.STOCK);

        try {
            actualQuotationHistory = quotationProviderInvestingDAO.getQuotationHistory(dmlStock, 1);
            expectedQuotationHistory = this.getDenisonMinesQuotationHistory();

            // 251 Trading days of a full year (holiday may be included).
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

    // @Test
    /**
     * An explorative test that tries to retrieve current Quotation data of the Amazon stock using a cURL command.
     */
    public void testGetCurrentQuotationCurl() {
        Process process = null;
        final InputStream resultStream;
        String result;
        final String command = "curl \"https://api.investing.com/api/financialdata/6435/historical/"
                + "chart/?interval=PT1M&pointscount=60\" --compressed "
                + "-H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0\" "
                + "-H \"Accept: */*\" -H \"Accept-Language: de,en-US;q=0.7,en;q=0.3\" "
                + "-H \"Accept-Encoding: gzip, deflate\" "
                + "-H \"Referer: https://www.investing.com/\" -H \"content-type: application/json\" "
                + "-H \"domain-id: www\" -H \\\"Origin: https://www.investing.com\\\""
                + "-H \"DNT: 1\" -H \"Sec-GPC: 1\" -H \"Connection: keep-alive\" "
                + "-H \"Sec-Fetch-Dest: empty\" -H \"Sec-Fetch-Mode: cors\" -H \"Sec-Fetch-Site: same-site\" "
                + "-H \"Priority: u=4\" -H \"TE: trailers";

        try {
            process = Runtime.getRuntime().exec(command);
            resultStream = process.getInputStream();

            result = IOUtils.toString(resultStream, StandardCharsets.UTF_8);
            assertTrue(result.length() > 0);
            // System.out.println(result); // Prints retrieved price and volume data to the console
        } catch (Exception exception) {
            fail(exception.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    // @Test
    /**
     * An explorative test that tries to retrieve historical Quotation data of the Amazon stock using a cURL command.
     *
     * As of February 2026 the site seems to use Cloudflare; previous requests don't work any longer.
     */
    public void testGetQuotationHistoryCurl() {
        Process process = null;
        final InputStream resultStream;
        String result;
        final String command = "curl 'https://api.investing.com/api/financialdata/historical/13928"
                + "?start-date=2025-02-16&end-date=2026-02-16&time-frame=Daily&add-missing-rows=false'"
                + " --compressed"
                + " -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:147.0) Gecko/20100101 Firefox/147.0'"
                + " -H 'Accept: */*'" + " -H 'Accept-Language: de,en-US;q=0.9,en;q=0.8'"
                + " -H 'Accept-Encoding: gzip, deflate, br, zstd'" + " -H 'Referer: https://www.investing.com/'"
                + " -H 'domain-id: www'" + " -H 'Origin: https://www.investing.com'" + " -H 'Sec-GPC: 1'"
                + " -H 'Connection: keep-alive'" + " -H 'Sec-Fetch-Dest: empty'" + " -H 'Sec-Fetch-Mode: cors'"
                + " -H 'Sec-Fetch-Site: same-site'" + " -H 'Priority: u=0'n" + " -H 'TE: trailers'";

        try {
            process = Runtime.getRuntime().exec(command);
            resultStream = process.getInputStream();

            result = IOUtils.toString(resultStream, StandardCharsets.UTF_8);
            assertTrue(result.length() > 0);
            // System.out.println(result); // Prints retrieved price and volume data to the console
        } catch (Exception exception) {
            fail(exception.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
