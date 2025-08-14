package backend.model.protocol;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of date-based protocol entries.
 *
 * @author Michael
 */
public class DateBasedProtocolArray {
    /**
     * A list of date-based protocol entries.
     */
    private List<DateBasedProtocolEntry> dateBasedProtocolEntries = null;

    /**
     * @return the dateBasedProtocolEntries
     */
    @XmlElementWrapper(name = "dateBasedProtocolEntries")
    @XmlElement(name = "dateBasedProtocolEntry")
    public List<DateBasedProtocolEntry> getDateBasedProtocolEntries() {
        return dateBasedProtocolEntries;
    }

    /**
     * @param dateBasedProtocolEntries the dateBasedProtocolEntries to set
     */
    public void setDateBasedProtocolEntries(final List<DateBasedProtocolEntry> dateBasedProtocolEntries) {
        this.dateBasedProtocolEntries = dateBasedProtocolEntries;
    }

    /**
     * Gets the DateBasedProtocolEntry of the given date.
     *
     * @param date The requested Date.
     * @return The DateBasedProtocolEntry of the given Date, if exists.
     */
    public DateBasedProtocolEntry getEntryOfDate(final Date date) {
        for (DateBasedProtocolEntry entry : this.dateBasedProtocolEntries) {
            if (entry.getDate().getTime() == date.getTime()) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Sorts the date-based protocol entries by their date. Newest first.
     */
    public void sortDateBasedEntriesByDate() {
        Collections.sort(this.dateBasedProtocolEntries, new DateBasedProtocolEntryDateComparator());
    }

    /**
     * Calculates percentage values for confirmations, violations and warnings based on all protocol entries.
     */
    public void calculatePercentages() {
        for (DateBasedProtocolEntry entry : this.dateBasedProtocolEntries) {
            entry.calculatePercentages();
        }
    }
}
