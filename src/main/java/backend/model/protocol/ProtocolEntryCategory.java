package backend.model.protocol;

/**
 * The category of a ProtocolEntry.
 *
 * @author Michael
 */
public enum ProtocolEntryCategory {
    /**
     * Confirmation of a trend.
     */
    CONFIRMATION,

    /**
     * Violation of a trend.
     */
    VIOLATION,

    /**
     * The behavior constitutes a warning sign.
     */
    WARNING
}
