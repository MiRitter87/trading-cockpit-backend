package backend.controller.chart.priceVolume;

/**
 * An overlay that can be plotted onto a chart.
 *
 * @author Michael
 */
public enum ChartOverlay {
    /**
     * Exponential Moving Average - Period 21.
     */
    EMA_21,

    /**
     * Simple Moving Average - Period 50.
     */
    SMA_50,

    /**
     * Simple Moving Average - Period 150.
     */
    SMA_150,

    /**
     * Simple Moving Average - Period 200.
     */
    SMA_200
}
