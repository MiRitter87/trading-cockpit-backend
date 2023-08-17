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
     * Gets an Instrument of the Denison Mines stock.
     *
     * @return Instrument of the Denison Mines stock.
     */
    private Instrument getDenisonMinesInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("DML");
        instrument.setStockExchange(StockExchange.TSX);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets an Instrument of the Algernon stock.
     *
     * @return Instrument of the Algernon stock.
     */
    private Instrument getAlgernonInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AGN");
        instrument.setStockExchange(StockExchange.CSE);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets an Instrument of the Ford Motor Co. stock.
     *
     * @return Instrument of the Ford Motor Co. stock.
     */
    private Instrument getFordInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("F");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets an Instrument of the Apple stock.
     *
     * @return Instrument of the Apple stock.
     */
    private Instrument getAppleInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets an Instrument of the Imperial Oil stock.
     *
     * @return Instrument of the Imperial Oil stock.
     */
    private Instrument getImperialOilInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("IMO");
        instrument.setStockExchange(StockExchange.AMEX);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets an Instrument of the Bayer stock.
     *
     * @return Instrument of the Bayer stock.
     */
    private Instrument getBayerInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("BAYRY");
        instrument.setStockExchange(StockExchange.OTC);
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
            actualQuotation = quotationProviderGlobeAndMailDAO
                    .getCurrentQuotation(this.getPatriotBatteryMetalsInstrument());
            expectedQuotation = this.getPatriotBatteryMetalsQuotation();

            assertTrue(expectedQuotation.getClose().compareTo(actualQuotation.getClose()) == 0);
            assertEquals(expectedQuotation.getCurrency(), actualQuotation.getCurrency());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the TSX/V.
     */
    public void testGetQueryUrlCurrentQuotationTSXV() {
        Instrument patriotBatteryMetalsStock = this.getPatriotBatteryMetalsInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/PMET-X/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(patriotBatteryMetalsStock);
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
        Instrument denisonMinesStock = this.getDenisonMinesInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/DML-T/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(denisonMinesStock);
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
        Instrument algernonStock = this.getAlgernonInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/AGN-CN/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(algernonStock);
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
        Instrument fordStock = this.getFordInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/F-N/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(fordStock);
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
        Instrument appleStock = this.getAppleInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/AAPL-Q/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(appleStock);
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
        Instrument imperialOilStock = this.getImperialOilInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/IMO-A/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(imperialOilStock);
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
        Instrument bayerStock = this.getBayerInstrument();
        final String expectedURL = "https://www.theglobeandmail.com/investing/markets/stocks/BAYRY/";
        String actualURL = "";

        try {
            actualURL = quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(bayerStock);
            assertEquals(expectedURL, actualURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the NYSE.
     */
    public void testGetQueryUrlQuotationHistoryNYSE() {
        final String symbol = "F";
        final StockExchange stockExchange = StockExchange.NYSE;
        final Integer years = 1;

        String expectedUrl = "https://globeandmail.pl.barchart.com/proxies/timeseries/queryeod.ashx?"
                + "symbol=F&data=daily&maxrecords=252&volume=contract&order=asc&dividends=false&backadjust=false";
        String actualUrl = "";

        actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
        assertEquals(expectedUrl, actualUrl);
    }
}
