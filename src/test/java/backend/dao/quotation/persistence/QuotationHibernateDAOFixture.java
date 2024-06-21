package backend.dao.quotation.persistence;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Helper class of the QuotationHibernateDAOTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class QuotationHibernateDAOFixture {
    /**
     * Initializes and provides the XLI sector.
     *
     * @return The XLI sector.
     */
    public Instrument getXliSector() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLI");
        instrument.setName("Industrial Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.SECTOR);

        return instrument;
    }

    /**
     * Initializes and provides the copper industry group.
     *
     * @return The copper industry group.
     */
    public Instrument getCopperIndustryGroup() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("COPX");
        instrument.setName("Global X Copper Miners ETF");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.IND_GROUP);

        return instrument;
    }

    /**
     * Initializes and provides the XLE ETF.
     *
     * @return The XLE ETF.
     */
    public Instrument getXleEtf() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLE");
        instrument.setName("Energy Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.ETF);

        return instrument;
    }

    /**
     * Initializes and provides the Apple stock.
     *
     * @return The Apple stock.
     */
    public Instrument getAppleStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Initializes and provides the Microsoft stock.
     *
     * @return The Microsoft stock.
     */
    public Instrument getMicrosoftStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("MSFT");
        instrument.setName("Microsoft");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Initializes and provides the microsoftQuotation1.
     *
     * @param instrument The Instrument the Quotation relates to.
     * @return The microsoftQuotation1.
     */
    public Quotation getMicrosoftQuotation1(final Instrument instrument) {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.setTime(new Date());
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(246.79));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(20200000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Initializes and provides the appleQuotation1.
     *
     * @param instrument The Instrument the Quotation relates to.
     * @return The appleQuotation1.
     */
    public Quotation getAppleQuotation1(final Instrument instrument) {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(78.54));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(6784544);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Initializes and provides the appleQuotation2.
     *
     * @param instrument The Instrument the Quotation relates to.
     * @return The appleQuotation2.
     */
    public Quotation getAppleQuotation2(final Instrument instrument) {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.setTime(new Date());
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(79.14));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(4584544);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Initializes and provides the xliSectorQuotation1.
     *
     * @param instrument The Instrument the Quotation relates to.
     * @return The xliSectorQuotation1.
     */
    public Quotation getXliSectorQuotation1(final Instrument instrument) {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.setTime(new Date());
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(92.60));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(13884800);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Initializes and provides the xleQuotation1.
     *
     * @param instrument The Instrument the Quotation relates to.
     * @return The xleQuotation1.
     */
    public Quotation getXleQuotation1(final Instrument instrument) {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.setTime(new Date());
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(81.28));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(18994000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Initializes and provides the copperIndustryGroupQuotation1.
     *
     * @param instrument The Instrument the Quotation relates to.
     * @return The copperIndustryGroupQuotation1.
     */
    public Quotation getCopperIndustryGroupQuotation1(final Instrument instrument) {
        Calendar calendar = Calendar.getInstance();
        Quotation quotation = new Quotation();

        calendar.setTime(new Date());
        quotation = new Quotation();
        quotation.setDate(calendar.getTime());
        quotation.setClose(BigDecimal.valueOf(29.04));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(401200);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Initializes and provides the appleQuotation2Indicator.
     *
     * @return The AppleQuotation2Indicator.
     */
    public Indicator getAppleQuotation2Indicator() {
        Indicator indicator = new Indicator();

        indicator.setBaseLengthWeeks(3);

        return indicator;
    }

    /**
     * Initializes and provides the xleQuotation1Indicator.
     *
     * @return The xleQuotation1Indicator.
     */
    public Indicator getXleQuotation1Indicator() {
        Indicator indicator = new Indicator();

        indicator.setBaseLengthWeeks(2);

        return indicator;
    }

    /**
     * Initializes and provides the xliSectorQuotation1Indicator.
     *
     * @return The xliSectorQuotation1Indicator.
     */
    public Indicator getXliSectorQuotation1Indicator() {
        Indicator indicator = new Indicator();

        indicator.setBaseLengthWeeks(1);

        return indicator;
    }

    /**
     * Initializes and provides the copperIndustryGroupQuotation1Indicator.
     *
     * @return The copperIndustryGroupQuotation1Indicator.
     */
    public Indicator getCopperIndustryGroupQuotation1Indicator() {
        Indicator indicator = new Indicator();

        indicator.setBaseLengthWeeks(4);

        return indicator;
    }
}
