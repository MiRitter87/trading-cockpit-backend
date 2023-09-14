package backend.webservice.common;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.list.List;
import backend.model.scan.Scan;

/**
 * Helper class of the ScanServiceTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class ScanServiceFixture {
    /**
     * Gets the Instrument of the Microsoft stock.
     *
     * @return The Instrument of the Microsoft stock.
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
     * Gets the Instrument of the Amazon stock.
     *
     * @return The Instrument of the Amazon stock.
     */
    public Instrument getAmazonStock() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AMZN");
        instrument.setName("Amazon");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets a list containing a single Instrument.
     *
     * @param instrument The Instrument of the List.
     * @return A List containing a single Instrument.
     */
    public List getSingleInstrumentList(final Instrument instrument) {
        List list = new List();

        list.setName("Single instrument");
        list.setDescription("Contains a single instrument.");
        list.addInstrument(instrument);

        return list;
    }

    /**
     * Gets a list containing multiple instruments.
     *
     * @return A list containing multiple instruments.
     */
    public List getMultipleInstrumentList() {
        List list = new List();

        list.setName("Multiple instruments");
        list.setDescription("Contains multiple instruments.");

        return list;
    }

    /**
     * Gets a scan containing a single list.
     *
     * @param list The List of the Scan.
     * @return A scan containing a single list.
     */
    public Scan getSingleListScan(final List list) {
        Scan scan = new Scan();

        scan.setName("Single list");
        scan.setDescription("Contains a single list");
        scan.addList(list);

        return scan;
    }

    /**
     * Gets a scan containing multiple lists.
     *
     * @return A scan containing multiple lists.
     */
    public Scan getMultipleListsScan() {
        Scan scan = new Scan();

        scan.setName("Multiple lists");
        scan.setDescription("Contains multiple lists");

        return scan;
    }
}
