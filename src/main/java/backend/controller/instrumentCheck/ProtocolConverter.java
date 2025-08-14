package backend.controller.instrumentCheck;

import java.util.ArrayList;

import backend.model.protocol.DateBasedProtocolArray;
import backend.model.protocol.DateBasedProtocolEntry;
import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.SimpleProtocolEntry;

/**
 * Performs conversions between different forms of health check protocols.
 *
 * @author Michael
 */
public class ProtocolConverter {
    /**
     * Converts a Protocol to a DateBasedProtocolArray.
     *
     * @param protocol The Protocol.
     * @return The DateBasedProtocolArray.
     */
    public DateBasedProtocolArray convertToDateBasedProtocolArray(final Protocol protocol) {
        DateBasedProtocolArray dateBasedProtocolArray = new DateBasedProtocolArray();
        DateBasedProtocolEntry dateBasedProtocolEntry;
        SimpleProtocolEntry newSimpleEntry;

        dateBasedProtocolArray.setDateBasedProtocolEntries(new ArrayList<>());

        for (ProtocolEntry protocolEntry : protocol.getProtocolEntries()) {
            dateBasedProtocolEntry = dateBasedProtocolArray.getEntryOfDate(protocolEntry.getDate());

            if (dateBasedProtocolEntry == null) {
                dateBasedProtocolEntry = new DateBasedProtocolEntry();
                dateBasedProtocolEntry.setDate(protocolEntry.getDate());
                dateBasedProtocolArray.getDateBasedProtocolEntries().add(dateBasedProtocolEntry);
            }

            newSimpleEntry = new SimpleProtocolEntry();
            newSimpleEntry.setCategory(protocolEntry.getCategory());
            dateBasedProtocolEntry.getSimpleProtocolEntries().add(newSimpleEntry);
        }

        dateBasedProtocolArray.sortDateBasedEntriesByDate();
        dateBasedProtocolArray.calculatePercentages();

        return dateBasedProtocolArray;
    }
}
