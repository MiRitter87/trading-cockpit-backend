package backend.model.dashboard;

/**
 * The traffic-light status of the Swingtrading Environment.
 *
 * @author Michael
 */
public enum SwingTradingEnvironmentStatus {
    /**
     * The market trades in the Swingtrading Environment. A favorable environment for breakouts is given.
     */
    GREEN,

    /**
     * The market trades in a transition phase. Breakouts should be bought with special caution.
     */
    YELLOW,

    /**
     * The market does not trade in the Swingtrading Environment. An unfavorable environment for breakouts is given.
     */
    RED
}
