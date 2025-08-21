package backend.controller.instrumentCheck;

/**
 * A HealthCheckProfile is a bundle of multiple Instrument health checks.
 *
 * @author Michael
 */
public enum HealthCheckProfile {
    /**
     * All health checks.
     */
    ALL,

    /**
     * Price and volume action that confirms an up-trend. This profile contains all confirmation check-ups.
     */
    CONFIRMATIONS,

    /**
     * Price and volume action that indicates weakness. This profile is used to identify a weak Instrument that should
     * be sold due to its weakness.
     */
    SELLING_INTO_WEAKNESS,

    /**
     * Price and volume action that indicates the imminent end of an up-trend. This profile is used to sell an
     * Instrument at the end of a strong up-trend before the price turns down.
     */
    SELLING_INTO_STRENGTH,

    /**
     * All health checks except counting check-ups.
     */
    ALL_WITHOUT_COUNTING,

    /**
     * Price and volume action that confirms an up-trend. Counting check-ups are excluded.
     */
    CONFIRMATIONS_WITHOUT_COUNTING,

    /**
     * Price and volume action that indicates weakness. This profile is used to identify a weak Instrument that should
     * be sold due to its weakness. Counting check-ups are excluded.
     */
    WEAKNESS_WITHOUT_COUNTING,

    /**
     * Health check routines that are relevant during the first couple of days after a breakout has occurred.
     */
    AFTER_BREAKOUT,

    /**
     * Price and volume action that indicates a reversal in price after an up-trend.
     */
    REVERSAL_ALERT,

    /**
     * Price and volume action indicating buying or selling of institutional investors.
     */
    INSTITUTIONS
}
