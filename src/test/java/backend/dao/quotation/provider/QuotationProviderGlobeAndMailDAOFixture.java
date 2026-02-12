package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Helper class of the QuotationProviderGlobeAndMailDAOTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAOFixture {
    /**
     * Gets an Instrument of the Patriot Battery Metals stock.
     *
     * @return Instrument of the Patriot Battery Metals stock.
     */
    public Instrument getPatriotBatteryMetalsInstrument() {
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
    public Instrument getDenisonMinesInstrument() {
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
    public Instrument getAlgernonInstrument() {
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
    public Instrument getFordInstrument() {
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
    public Instrument getAppleInstrument() {
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
    public Instrument getImperialOilInstrument() {
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
    public Instrument getBayerInstrument() {
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
    public Instrument getGlencoreInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("GLEN");
        instrument.setStockExchange(StockExchange.LSE);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets historical quotations of Denison Mines stock. The quotations of the three most recent trading days are
     * provided.
     *
     * @return Historical quotations of Denison Mines stock
     */
    public List<Quotation> getDenisonMinesQuotationHistory() {
        List<Quotation> historicalQuotations = new ArrayList<>();
        Quotation quotation = new Quotation();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final long volume1 = 949015;
        final long volume2 = 831450;
        final long volume3 = 1264235;

        try {
            quotation.setDate(dateFormat.parse("08/17/2023"));
            quotation.setOpen(new BigDecimal("1.80"));
            quotation.setHigh(new BigDecimal("1.81"));
            quotation.setLow(new BigDecimal("1.74"));
            quotation.setClose(new BigDecimal("1.76"));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(volume1);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("08/16/2023"));
            quotation.setOpen(new BigDecimal("1.79"));
            quotation.setHigh(new BigDecimal("1.83"));
            quotation.setLow(new BigDecimal("1.78"));
            quotation.setClose(new BigDecimal("1.80"));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(volume2);
            historicalQuotations.add(quotation);

            quotation = new Quotation();
            quotation.setDate(dateFormat.parse("08/15/2023"));
            quotation.setOpen(new BigDecimal("1.82"));
            quotation.setHigh(new BigDecimal("1.84"));
            quotation.setLow(new BigDecimal("1.78"));
            quotation.setClose(new BigDecimal("1.79"));
            quotation.setCurrency(Currency.CAD);
            quotation.setVolume(volume3);
            historicalQuotations.add(quotation);
        } catch (ParseException e) {
            fail(e.getMessage());
        }

        return historicalQuotations;
    }
}
