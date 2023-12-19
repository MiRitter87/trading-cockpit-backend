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

        quotation.setClose(BigDecimal.valueOf(120.65));
        quotation.setCurrency(Currency.USD);

        return quotation;
    }

    /**
     * Gets a Quotation as expected from the investing.com Website in the fallback scenario file.
     *
     * @return A Quotation.
     */
    private Quotation getAmazonQuotationFallback() {
        Quotation quotation = new Quotation();

        quotation.setClose(BigDecimal.valueOf(154.07));
        quotation.setCurrency(Currency.USD);

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
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests getting current Quotation data from a stock listed at the NYSE. The fallback scenario is tested where the
     * Investing server returns a different HTML document.
     */
    public void testGetCurrentQuotationNYSEFallback() {
        Quotation actualQuotation, expectedQuotation;
        Instrument amazonInstrument = this.getAmazonInstrument();

        try {
            amazonInstrument.setCompanyPathInvestingCom("fallback"); // Let the DAO stub use the fallback HTML document.

            actualQuotation = quotationProviderInvestingDAO.getCurrentQuotation(amazonInstrument);
            expectedQuotation = this.getAmazonQuotationFallback();

            assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
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
        final String expectedURL = "https://www.investing.com/etfs/diamonds-trust";
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
}
