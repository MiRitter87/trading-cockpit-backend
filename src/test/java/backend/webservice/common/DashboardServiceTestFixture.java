package backend.webservice.common;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Helper class of the DashboardServiceTest, that provides methods for fixture initialization.
 *
 * @author MiRitter87
 */
public class DashboardServiceTestFixture {
    /**
     * Gets the Instrument of the Copper Miners Industry Group.
     *
     * @return The Instrument of the Copper Miners Industry Group.
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
     * Gets the Southern Copper Stock.
     *
     * @param industryGroup The industry group to set.
     * @return The Southern Copper Stock.
     */
    public Instrument getSouthernCopperStock(final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("SCCO");
        instrument.setName("Southern Copper");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.STOCK);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }

    /**
     * Gets the Freeport-McMoRan stock.
     *
     * @param industryGroup The industry group to set.
     * @return The Freeport-McMoRan stock.
     */
    public Instrument getFreeportStock(final Instrument industryGroup) {
        Instrument instrument = new Instrument();

        instrument.setSymbol("FCX");
        instrument.setName("Freeport-McMoRan");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.STOCK);
        instrument.setIndustryGroup(industryGroup);

        return instrument;
    }
}
