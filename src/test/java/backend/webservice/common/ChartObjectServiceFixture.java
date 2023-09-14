package backend.webservice.common;

import java.math.BigDecimal;

import backend.model.StockExchange;
import backend.model.chart.HorizontalLine;
import backend.model.chart.HorizontalLineWS;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Helper class of the ChartObjectServiceTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class ChartObjectServiceFixture {
    /**
     * Gets the Instrument of the Apple stock.
     *
     * @return The Instrument of the Apple stock.
     */
    public Instrument getAppleInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Microsoft stock.
     *
     * @return The Instrument of the Microsoft stock.
     */
    public Instrument getMicrosoftInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("MSFT");
        instrument.setName("Microsoft");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the first horizontal line of the Apple stock.
     *
     * @param instrument The referenced Instrument.
     * @return The HorizontalLine.
     */
    public HorizontalLine getHorizontalLine1(final Instrument instrument) {
        HorizontalLine horizontalLine = new HorizontalLine();

        horizontalLine.setInstrument(instrument);
        horizontalLine.setPrice(new BigDecimal("175.00"));

        return horizontalLine;
    }

    /**
     * Gets the second horizontal line of the Apple stock.
     *
     * @param instrument The referenced Instrument.
     * @return The HorizontalLine.
     */
    public HorizontalLine getHorizontalLine2(final Instrument instrument) {
        HorizontalLine horizontalLine = new HorizontalLine();

        horizontalLine.setInstrument(instrument);
        horizontalLine.setPrice(new BigDecimal("155.00"));

        return horizontalLine;
    }

    /**
     * Gets the first horizontal line of the Microsoft stock.
     *
     * @param instrument The referenced Instrument.
     * @return The HorizontalLine.
     */
    public HorizontalLine getHorizontalLine3(final Instrument instrument) {
        HorizontalLine horizontalLine = new HorizontalLine();

        horizontalLine.setInstrument(instrument);
        horizontalLine.setPrice(new BigDecimal("290.00"));

        return horizontalLine;
    }

    /**
     * Converts a HorizontalLine to the lean WebService representation.
     *
     * @param horizontalLine The HorizontalLine to be converted.
     * @return The lean WebService representation of the HorizontalLine.
     */
    public HorizontalLineWS convertToWsHorizontalLine(final HorizontalLine horizontalLine) {
        HorizontalLineWS horizontalLineWS = new HorizontalLineWS();

        // Simple attributes.
        horizontalLineWS.setId(horizontalLine.getId());
        horizontalLineWS.setPrice(horizontalLine.getPrice());

        // Object references.
        if (horizontalLine.getInstrument() != null)
            horizontalLineWS.setInstrumentId(horizontalLine.getInstrument().getId());

        return horizontalLineWS;
    }
}
