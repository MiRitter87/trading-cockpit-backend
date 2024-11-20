package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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
     * Gets a Quotation as expected from the investing.com Website.
     *
     * @return A Quotation.
     */
    private Quotation getAmazonQuotation() {
        Quotation quotation = new Quotation();

        quotation.setDate(new Date(1731542400000L));
        quotation.setOpen(new BigDecimal("225.02"));
        quotation.setHigh(new BigDecimal("228.87"));
        quotation.setLow(new BigDecimal("225"));
        quotation.setClose(new BigDecimal("228.22"));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(44923940);

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
        instrument.setCompanyPathInvestingCom("6435");

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
        instrument.setCompanyPathInvestingCom("504");

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

            assertEquals(expectedQuotation, actualQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the NYSE.
     */
    public void testGetQueryUrlCurrentQuotationNYSE() {
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.NYSE);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the AMEX.
     */
    public void testGetQueryUrlCurrentQuotationAMEX() {
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.AMEX);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT5M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.OTC);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.TSX);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT5M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.TSXV);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
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
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT5M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.CSE);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
     */
    public void testGetQueryUrlCurrentQuotationLSE() {
        Instrument amazonStock = this.getAmazonInstrument();
        final String expectedURL = "https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        amazonStock.setStockExchange(StockExchange.LSE);

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of an ETF.
     */
    public void testGetQueryUrlCurrentQuotationETF() {
        Instrument diaETF = this.getDowJonesIndustrialETF();
        final String expectedURL = "https://api.investing.com/api/financialdata/504/historical/chart/?interval=PT1M&pointscount=60";
        String actualURL = "";

        try {
            actualURL = quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(diaETF);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL if attribute 'companyPathInvestingCom' of Instrument is not defined.
     */
    public void testGetQueryUrlWithoutCompanyPath() {
        Instrument amazonStock = this.getAmazonInstrument();

        amazonStock.setCompanyPathInvestingCom("");

        try {
            quotationProviderInvestingDAO.getQueryUrlCurrentQuotation(amazonStock);
            fail("Determination of URL should have failed because attribute 'companyPathInvestingCom' is not defined.");
        } catch (Exception expected) {
            // All is well.
        }
    }

    // @Test
    /**
     * An explorative test that tries to retrieve Quotation data of the Amazon stock using a cURL command.
     */
    public void testGetCurrentQuotationCurl() {
        Process process = null;
        final InputStream resultStream;
        String result;
        final String command = "curl \"https://api.investing.com/api/financialdata/6435/historical/chart/?interval=PT1M&pointscount=60\" "
                + "--compressed -H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0\" "
                + "-H \"Accept: */*\" -H \"Accept-Language: de,en-US;q=0.7,en;q=0.3\" -H \"Accept-Encoding: gzip, deflate\" "
                + "-H \"Referer: https://www.investing.com/\" -H \"content-type: application/json\" -H \"domain-id: www\" "
                + "-H \"Origin: https://www.investing.com\" -H \"DNT: 1\" -H \"Sec-GPC: 1\" -H \"Connection: keep-alive\" "
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
}
