package backend.controller.instrumentCheck;

/**
 * A HealthCheckProfile is a bundle of multiple Instrument health checks.
 *
 * @author Michael
 */
public enum HealthCheckProfile {
    /**
     * Price and volume action that confirms an up-trend.
     */
    CONFIRMATIONS,

    /**
     * Price and volume action that indicates the imminent end of an up-trend. This profile is used to sell an
     * Instrument at the end of a strong up-trend before the price turns down.
     */
    SELLING_INTO_STRENGTH,

    /**
     * Price and volume action that indicates weakness. This profile is used to identify a weak Instrument that should
     * be sold due to its weakness.
     */
    SELLING_INTO_WEAKNESS
}
