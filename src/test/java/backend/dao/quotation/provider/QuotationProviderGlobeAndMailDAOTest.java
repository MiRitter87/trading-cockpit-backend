package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
     * Gets an Instrument of the Glencore stock.
     *
     * @return Instrument of the Glencore stock.
     */
    private Instrument getGlencoreInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("GLEN");
        instrument.setStockExchange(StockExchange.LSE);
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

        try {
            quotation.setDate(dateFormat.parse("08/17/2023"));
            quotation.setOpen(BigDecimal.valueOf(1.80));
            quotation.setHigh(BigDecimal.valueOf(1.81));
            quotation.setLow(BigDecimal.valueOf(1.74));
            quotation.setClose(BigDecimal.valueOf(1.76));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(949015);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("08/16/2023"));
            quotation.setOpen(BigDecimal.valueOf(1.79));
            quotation.setHigh(BigDecimal.valueOf(1.83));
            quotation.setLow(BigDecimal.valueOf(1.78));
            quotation.setClose(BigDecimal.valueOf(1.80));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(831450);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("08/15/2023"));
            quotation.setOpen(BigDecimal.valueOf(1.82));
            quotation.setHigh(BigDecimal.valueOf(1.84));
            quotation.setLow(BigDecimal.valueOf(1.78));
            quotation.setClose(BigDecimal.valueOf(1.79));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(1264235);
            historicalQuotations.add(quotation);
        } catch (ParseException e) {
            fail(e.getMessage());
        }

        return historicalQuotations;
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
     * Tests the retrieval of the query URL for the current quotation of a stock listed at the LSE.
     */
    public void testGetQueryUrlCurrentQuotationLSE() {
        Instrument glencoreStock = this.getGlencoreInstrument();

        try {
            quotationProviderGlobeAndMailDAO.getQueryUrlCurrentQuotation(glencoreStock);
            fail("URL Determination should have failed because exchange LSE is not supported.");
        } catch (Exception expected) {
            // All is well.
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

        try {
            actualUrl = quotationProviderGlobeAndMailDAO.getQueryUrlQuotationHistory(symbol, stockExchange, years);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the Nasdaq.
     */
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

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the AMEX.
     */
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

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the US OTC.
     */
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

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX.
     */
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

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the TSX/V.
     */
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

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the CSE.
     */
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

    @Test
    /**
     * Tests the retrieval of the query URL for historical quotations of a stock listed at the LSE.
     */
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

    @Test
    /**
     * Tests the retrieval of the quotation history of a stock traded at the TSX.
     */
    public void testGetQuotationHistoryTSX() {
        QuotationArray actualQuotationHistory = new QuotationArray();
        List<Quotation> expectedQuotationHistory;
        Quotation actualQuotation;
        Quotation expectedQuotation;

        try {
            actualQuotationHistory.setQuotations(
                    quotationProviderGlobeAndMailDAO.getQuotationHistory(this.getDenisonMinesInstrument(), 1));
            expectedQuotationHistory = this.getDenisonMinesQuotationHistory();

            actualQuotationHistory.sortQuotationsByDate();

            // 252 Trading days of a full year.
            assertEquals(252, actualQuotationHistory.getQuotations().size());

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
