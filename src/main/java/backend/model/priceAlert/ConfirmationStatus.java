package backend.model.priceAlert;

/**
 * The confirmation status of a PriceAlert.
 *
 * @author Michael
 */
public enum ConfirmationStatus {
    /**
     * Either confirmed or not-confirmed.
     */
    ALL,

    /**
     * Confirmed only.
     */
    CONFIRMED,

    /**
     * Not-confirmed only.
     */
    NOT_CONFIRMED
}
