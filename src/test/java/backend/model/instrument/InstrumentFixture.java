package backend.model.instrument;

import java.math.BigDecimal;
import java.util.Calendar;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.list.List;

/**
 * Helper class of the Instrumenttest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class InstrumentFixture {
    /**
     * Initializes and provides the Apple stock.
     *
     * @return The Apple stock.
     */
    public Instrument getAppleStock() {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(1));
        instrument.setSymbol("AAPL");
        instrument.setType(InstrumentType.STOCK);
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setName("Apple");

        return instrument;
    }

    /**
     * Initializes and provides the Microsoft stock.
     *
     * @return The Microsoft stock.
     */
    public Instrument getMicrosoftStock() {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(2));
        instrument.setSymbol("MSFT");
        instrument.setType(InstrumentType.STOCK);
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setName("Microsoft");

        return instrument;
    }

    /**
     * Initializes and provides the sector.
     *
     * @return The sector.
     */
    public Instrument getSector() {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(3));
        instrument.setSymbol("XLE");
        instrument.setType(InstrumentType.SECTOR);
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setName("Energy Select Sector SPDR Fund");

        return instrument;
    }

    /**
     * Initializes and provides the industry group.
     *
     * @return The industry group.
     */
    public Instrument getIndustryGroup() {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(4));
        instrument.setSymbol("COPX");
        instrument.setType(InstrumentType.IND_GROUP);
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setName("Global X Copper Miners ETF");

        return instrument;
    }

    /**
     * Initializes and provides the sector/ig ratio.
     *
     * @param dividend The dividend.
     * @param divisor  The divisor.
     * @return The sector/ig ratio.
     */
    public Instrument getSectorIgRatio(final Instrument dividend, final Instrument divisor) {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(5));
        instrument.setType(InstrumentType.RATIO);
        instrument.setName("Sector/Industry Group");
        instrument.setDividend(dividend);
        instrument.setDivisor(divisor);

        return instrument;
    }

    /**
     * Initializes and provides a Instrument that has a List defined as data source.
     *
     * @return The Instrument with data source.
     */
    public Instrument getInstrumentWithDataSource() {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(6));
        instrument.setType(InstrumentType.ETF);
        instrument.setName("Instrument with data source");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setDataSourceList(new List());

        return instrument;
    }

    /**
     * Initializes and provides the quotation1.
     *
     * @return The quotation1.
     */
    public Quotation getQuotation1() {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.clear();
        calendar.set(2022, 07, 26, 15, 30, 0);
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(1.11));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(10000);

        return quotation;
    }

    /**
     * Initializes and provides the quotation2.
     *
     * @return The quotation2.
     */
    public Quotation getQuotation2() {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.clear();
        calendar.set(2022, 07, 27, 15, 30, 0);
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(1.12));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(13400);

        return quotation;
    }

    /**
     * Initializes and provides the quotation3.
     *
     * @return The quotation3.
     */
    public Quotation getQuotation3() {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.clear();
        calendar.set(2022, 07, 27, 14, 30, 0);
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(1.11));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(10110);

        return quotation;
    }
}
