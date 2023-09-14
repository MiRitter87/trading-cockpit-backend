package backend.webservice.common;

import java.util.Date;
import java.util.Iterator;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.list.List;
import backend.model.list.ListWS;
import backend.model.scan.Scan;
import backend.model.scan.ScanExecutionStatus;

/**
 * Helper class of the ListServiceTest, that provides methods for fixture initialization.
 *
 * @author Michael
 */
public class ListServiceFixture {
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
     * Gets the instrument of the Amazon stock.
     *
     * @return The instrument of the Amazon stock.
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
     * Gets a list containing a single instrument.
     *
     * @param instrument The Instrument of the List.
     * @return A list containing a single instrument.
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
     * Gets a Scan.
     *
     * @param list The List of the Scan.
     * @return The Scan.
     */
    public Scan getScan(final List list) {
        Scan scan = new Scan();

        scan.setName("Dummy Scan");
        scan.setDescription("Some Description");
        scan.setExecutionStatus(ScanExecutionStatus.FINISHED);
        scan.setLastScan(new Date());
        scan.addList(list);

        return scan;
    }

    /**
     * Converts a list to the lean WebService representation.
     *
     * @param list The list to be converted.
     * @return The lean WebService representation of the list.
     */
    public ListWS convertToWsList(final List list) {
        ListWS listWS = new ListWS();
        Iterator<Instrument> instrumentIterator;
        Instrument instrument;

        // Head level
        listWS.setId(list.getId());
        listWS.setName(list.getName());
        listWS.setDescription(list.getDescription());

        // Instruments
        instrumentIterator = list.getInstruments().iterator();
        while (instrumentIterator.hasNext()) {
            instrument = instrumentIterator.next();
            listWS.getInstrumentIds().add(instrument.getId());
        }

        return listWS;
    }
}
