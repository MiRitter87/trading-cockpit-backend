package backend.controller.chart.priceVolume;

import org.jfree.chart.JFreeChart;

import backend.controller.NoQuotationsExistException;

/**
 * Controller for the creation of a miniature chart displaying an Instrument with price and volume. The miniature
 * version is intended to be used as a short-term version of the price/volume chart with fixed indicators and overlays.
 * One use-case may be to quickly browse through a lot of miniature charts to spot instruments with a distinct pattern.
 *
 * @author Michael
 */
public class MiniPriceVolumeChartController extends PriceVolumeChartController {
    /**
     * Gets a miniature price/volume chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getPocketPivotsChart(final Integer instrumentId) throws NoQuotationsExistException, Exception {
        return null;
    }
}
