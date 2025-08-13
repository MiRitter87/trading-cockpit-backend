package backend.model.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Contains all health check events that occurred on a given day as well as statistical data.
 *
 * @author Michael
 */
public class DateBasedProtocolEntry {
    /**
     * The date.
     */
    private Date date;

    /**
     * The percentage of protocol entries that constitute confirmations.
     */
    private int confirmationPercentage;

    /**
     * The percentage of protocol entries that constitute a warning.
     */
    private int warningPercentage;

    /**
     * The percentage of protocol entries that constitute violations.
     */
    private int violationPercentage;

    /**
     * A list of simple protocol entries.
     */
    private List<SimpleProtocolEntry> simpleProtocolEntries;

    /**
     * Default constructor.
     */
    public DateBasedProtocolEntry() {
        this.simpleProtocolEntries = new ArrayList<>();
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * @return the confirmationPercentage
     */
    public int getConfirmationPercentage() {
        return confirmationPercentage;
    }

    /**
     * @param confirmationPercentage the confirmationPercentage to set
     */
    public void setConfirmationPercentage(final int confirmationPercentage) {
        this.confirmationPercentage = confirmationPercentage;
    }

    /**
     * @return the warningPercentage
     */
    public int getWarningPercentage() {
        return warningPercentage;
    }

    /**
     * @param warningPercentage the warningPercentage to set
     */
    public void setWarningPercentage(final int warningPercentage) {
        this.warningPercentage = warningPercentage;
    }

    /**
     * @return the violationPercentage
     */
    public int getViolationPercentage() {
        return violationPercentage;
    }

    /**
     * @param violationPercentage the violationPercentage to set
     */
    public void setViolationPercentage(final int violationPercentage) {
        this.violationPercentage = violationPercentage;
    }

    /**
     * @return the simpleProtocolEntries
     */
    public List<SimpleProtocolEntry> getSimpleProtocolEntries() {
        return simpleProtocolEntries;
    }

    /**
     * @param simpleProtocolEntries the simpleProtocolEntries to set
     */
    public void setSimpleProtocolEntries(final List<SimpleProtocolEntry> simpleProtocolEntries) {
        this.simpleProtocolEntries = simpleProtocolEntries;
    }

    /**
     * Calculates the hashCode of a DateBasedProtocolEntry.
     */
    @Override
    public int hashCode() {
        return Objects.hash(confirmationPercentage, date, simpleProtocolEntries, violationPercentage,
                warningPercentage);
    }

    /**
     * Indicates whether some other DateBasedProtocolEntry is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DateBasedProtocolEntry other = (DateBasedProtocolEntry) obj;
        return confirmationPercentage == other.confirmationPercentage && Objects.equals(date, other.date)
                && Objects.equals(simpleProtocolEntries, other.simpleProtocolEntries)
                && violationPercentage == other.violationPercentage && warningPercentage == other.warningPercentage;
    }
}
