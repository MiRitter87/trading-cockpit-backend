package backend.model.protocol;

import java.util.Comparator;

/**
 * Compares two protocol entries by their date.
 *
 * @author Michael
 */
public class ProtocolEntryDateComparator implements Comparator<ProtocolEntry> {
    /**
     * Compares its two protocol entries for order by their date.
     */
    @Override
    public int compare(final ProtocolEntry protocolEntry1, final ProtocolEntry protocolEntry2) {
        if (protocolEntry1.getDate().getTime() < protocolEntry2.getDate().getTime()) {
            return 1;
        } else if (protocolEntry1.getDate().getTime() > protocolEntry2.getDate().getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
