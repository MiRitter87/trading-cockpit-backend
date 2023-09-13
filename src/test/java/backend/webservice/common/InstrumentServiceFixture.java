package backend.webservice.common;

import java.math.BigDecimal;
import java.util.Date;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.InstrumentWS;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;

/**
 * Helper class of the InstrumentServiceTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class InstrumentServiceFixture {
    /**
     * Gets the Instrument of the technology sector.
     *
     * @return The Instrument of the technology sector.
     */
    public Instrument getTechnologySector() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("XLK");
        instrument.setName("Technology Select Sector SPDR Fund");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.SECTOR);

        return instrument;
    }

    /**
     * Gets the Instrument of the Copper Industry Group.
     *
     * @return The Instrument of the Copper Industry Group.
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
     * Gets the instrument of the Apple stock.
     *
     * @param sector The sector Instrument.
     * @param industryGroup The industry group Instrument.
     * @return The instrument of the Apple stock.
     */
    public Instrument getAppleStock(final Instrument sector, final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);
        instrument.setSector(sector);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the instrument of the Microsoft stock.
     *
     * @return The instrument of the Microsoft stock.
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
     * Gets the instrument of the NVidia stock.
     *
     * @return The instrument of the NVidia stock.
     */
    public Instrument getNvidiaStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("NVDA");
        instrument.setName("NVIDIA");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Tesla stock.
     *
     * @return The instrument of the Tesla stock.
     */
    public Instrument getTeslaStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("TSLA");
        instrument.setName("Tesla");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Apple/Tesla ratio.
     *
     * @param dividend The dividend Instrument.
     * @param divisor The divisor Instrument.
     * @return The Instrument of the Apple/Tesla ratio.
     */
    public Instrument getAppleTeslaRatio(final Instrument dividend, final Instrument divisor) {
        Instrument instrument = new Instrument();

        instrument.setName("Apple/Tesla");
        instrument.setType(InstrumentType.RATIO);
        instrument.setDividend(dividend);
        instrument.setDivisor(divisor);

        return instrument;
    }

    /**
     * Gets the Quotation of the Microsoft stock.
     *
     * @param instrument The referenced Instrument.
     * @return The Quotation of the Microsoft stock.
     */
    public Quotation getMicrosoftQuotation(final Instrument instrument) {
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        quotation.setClose(BigDecimal.valueOf(332.88));
        quotation.setCurrency(Currency.USD);
        quotation.setVolume(17540000);
        quotation.setInstrument(instrument);

        return quotation;
    }

    /**
     * Gets the List.
     *
     * @return The List.
     */
    public backend.model.list.List getList() {
        backend.model.list.List list = new backend.model.list.List();

        list.setName("Dummy List");
        list.setDescription("Some Description");

        return list;
    }

    /**
     * Gets a PriceAlert for the NVIDIA stock.
     *
     * @param instrument The referenced Instrument.
     * @return A PriceAlert for the NVIDIA stock.
     */
    public PriceAlert getNvidiaAlert(final Instrument instrument) {
        PriceAlert alert = new PriceAlert();

        alert.setInstrument(instrument);
        alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(120.00));
        alert.setCurrency(Currency.USD);

        return alert;
    }

    /**
     * Converts an Instrument to the lean WebService representation.
     *
     * @param instrument The Instrument to be converted.
     * @return The lean WebService representation of the Instrument.
     */
    public InstrumentWS convertToWsInstrument(final Instrument instrument) {
        InstrumentWS instrumentWS = new InstrumentWS();

        // Simple object attributes.
        instrumentWS.setId(instrument.getId());
        instrumentWS.setSymbol(instrument.getSymbol());
        instrumentWS.setType(instrument.getType());
        instrumentWS.setStockExchange(instrument.getStockExchange());
        instrumentWS.setName(instrument.getName());
        instrumentWS.setCompanyPathInvestingCom(instrument.getCompanyPathInvestingCom());

        // Object references.
        if (instrument.getSector() != null)
            instrumentWS.setSectorId(instrument.getSector().getId());

        if (instrument.getIndustryGroup() != null)
            instrumentWS.setIndustryGroupId(instrument.getIndustryGroup().getId());

        if(instrument.getDividend() != null)
            instrumentWS.setDividendId(instrument.getDividend().getId());

        if(instrument.getDivisor() != null)
            instrumentWS.setDivisorId(instrument.getDivisor().getId());

        return instrumentWS;
    }
}
