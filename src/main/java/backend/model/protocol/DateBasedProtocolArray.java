package backend.model.protocol;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of date-based protocol entries.
 *
 * @author Michael
 */
public class DateBasedProtocolArray {
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
}
