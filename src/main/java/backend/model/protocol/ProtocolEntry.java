package backend.model.protocol;

import java.util.Date;
import java.util.Objects;

import backend.controller.instrumentCheck.HealthCheckProfile;

/**
 * The entry of a protocol that informs about the health of an Instrument at a certain date. The behavior of an
 * instruments price and volume can confirm or violate the current trend.
 *
 * @author Michael
 */
public class ProtocolEntry {
    /**
     * The date.
     */
    private Date date;

    /**
     * The category.
     */
    private ProtocolEntryCategory category;

    /**
     * The HealthCheckProfile whose health check created this ProtocolEntry.
     */
    private HealthCheckProfile profile;

    /**
     * The protocol text.
     */
    private String text;

    /**
     * Default constructor.
     */
    public ProtocolEntry() {

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
     * @return the category
     */
    public ProtocolEntryCategory getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(final ProtocolEntryCategory category) {
        this.category = category;
    }

    /**
     * @return the profile
     */
    public HealthCheckProfile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(final HealthCheckProfile profile) {
        this.profile = profile;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * Calculates the hashCode of a ProtocolEntry.
     */
    @Override
    public int hashCode() {
        return Objects.hash(category, date, profile, text);
    }

    /**
     * Indicates whether some other ProtocolEntry is "equal to" this one.
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
        ProtocolEntry other = (ProtocolEntry) obj;
        return category == other.category && Objects.equals(date, other.date) && profile == other.profile
                && Objects.equals(text, other.text);
    }
}
