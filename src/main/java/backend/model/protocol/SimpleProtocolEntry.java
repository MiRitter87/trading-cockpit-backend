package backend.model.protocol;

import java.util.Objects;

/**
 * A simple protocol entry that contains no information about the date or the corresponding profile.
 *
 * @author Michael
 */
public class SimpleProtocolEntry {
    /**
     * The category.
     */
    private ProtocolEntryCategory category;

    /**
     * The protocol text.
     */
    private String text;

    /**
     * Default constructor.
     */
    public SimpleProtocolEntry() {

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
     * Calculates the hashCode of a SimpleProtocolEntry.
     */
    @Override
    public int hashCode() {
        return Objects.hash(category, text);
    }

    /**
     * Indicates whether some other SimpleProtocolEntry is "equal to" this one.
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
        SimpleProtocolEntry other = (SimpleProtocolEntry) obj;
        return category == other.category && Objects.equals(text, other.text);
    }
}
