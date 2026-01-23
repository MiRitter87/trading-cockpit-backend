package backend.webservice;

/**
 * A template that defines which parameters are applied to the Scan results.
 *
 * @author Michael
 */
public enum ScanTemplate {
    /**
     * All Instruments.
     */
    ALL,

    /**
     * Minervini Trend Template.
     */
    MINERVINI_TREND_TEMPLATE,

    /**
     * A contraction in price and volume in the last a 10-weeks as well as the last 10 trading days.
     */
    CONSOLIDATION_10_WEEKS,

    /**
     * A contraction in price and volume within the last 10 trading days.
     */
    CONSOLIDATION_10_DAYS,

    /**
     * Instruments consolidating in Buyable Bases near the 52 week high.
     */
    BREAKOUT_CANDIDATES,

    /**
     * Instruments making a big price advance on increased volume.
     */
    UP_ON_VOLUME,

    /**
     * Instruments making a big price decline on increased volume.
     */
    DOWN_ON_VOLUME,

    /**
     * Instruments trading near their 52-week high.
     */
    NEAR_52_WEEK_HIGH,

    /**
     * Instruments trading near their 52-week low.
     */
    NEAR_52_WEEK_LOW,

    /**
     * Instruments with their RS number since the given date.
     */
    RS_SINCE_DATE,

    /**
     * Instruments having three weekly closes within a tight range.
     */
    THREE_WEEKS_TIGHT,

    /**
     * Instruments having potentially build a high tight flag.
     */
    HIGH_TIGHT_FLAG,

    /**
     * Instruments (mainly Sectors, Industry Groups and Indices) trading in a way that is conductive to a breakout-style
     * of Swing Trading.
     */
    SWING_TRADING_ENVIRONMENT,

    /**
     * Instruments whose RS-line is trading near the 52-week high. The referenced industry group is used for ratio
     * calculation.
     */
    RS_NEAR_HIGH_IG,

    /**
     * Instruments whose price and volume characteristics provide a Buyable Base. In contrast to the
     * "BREAKOUT_CANDIDATES" template the Buyable Base does not have to be near the 52-week high.
     */
    BUYABLE_BASE,

    /**
     * Instruments whose price, SMA(50) and EMA(21) trade near each other.
     */
    MA_PRICE_CONVERGENCE
}
