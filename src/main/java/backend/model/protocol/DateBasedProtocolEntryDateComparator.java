package backend.model.protocol;

import java.util.Comparator;

/**
 * Compares two date-based protocol entries by their date.
 *
 * @author Michael
 */
public class DateBasedProtocolEntryDateComparator implements Comparator<DateBasedProtocolEntry> {
    /**
     * Compares its two date-based protocol entries for order by their date.
     */
    @Override
    public int compare(final DateBasedProtocolEntry dateBasedProtocolEntry1,
            final DateBasedProtocolEntry dateBasedProtocolEntry2) {

        if (dateBasedProtocolEntry1.getDate().getTime() < dateBasedProtocolEntry2.getDate().getTime()) {
            return 1;
        } else if (dateBasedProtocolEntry1.getDate().getTime() > dateBasedProtocolEntry2.getDate().getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
