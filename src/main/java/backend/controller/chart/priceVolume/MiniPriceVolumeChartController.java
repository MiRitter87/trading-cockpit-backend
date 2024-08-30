package backend.controller.chart.priceVolume;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;

import backend.controller.NoQuotationsExistException;
import backend.model.instrument.Instrument;

/**
 * Controller for the creation of a miniature chart displaying an Instrument with price and volume. The miniature
 * version is intended to be used as a short-term version of the price/volume chart with fixed indicators and overlays.
 * One use-case may be to quickly browse through a lot of miniature charts to spot instruments with a distinct pattern.
 *
 * @author Michael
 */
public class MiniPriceVolumeChartController extends PriceVolumeChartController {
    /**
     * The number of trading days to be displayed (3 month period).
     */
    public static final Integer TRADING_DAYS_3_MONTHS = 63;

    /**
     * The number of additional days needed for calculation of Bollinger BandWidth.
     */
    public static final Integer ADDITIONAL_DAYS_BBW = 9;

    /**
     * Gets a miniature price/volume chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getMiniPriceVolumeChart(final Integer instrumentId) throws NoQuotationsExistException, Exception {
        Instrument instrument = this.getInstrumentWithQuotations(instrumentId, TRADING_DAYS_3_MONTHS);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        final int candleStickPlotWeight = 4;
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();

        XYPlot volumeSubplot = this.getVolumePlot(instrument, dateAxis);
        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);

        this.getChartOverlayProvider().addEma21(instrument, candleStickSubplot);
        this.getChartOverlayProvider().addSma50(instrument, candleStickSubplot);

        this.getChartOverlayProvider().addMovingAverageVolume(instrument, true, volumeSubplot);
        this.clipVolumeAt2TimesAverage(volumeSubplot, instrument);

        // The whole years trading history is needed to calculate the threshold of the Bollinger BandWidth.
        instrument = this.getInstrumentWithQuotations(instrumentId, TRADING_DAYS_PER_YEAR);
        XYPlot indicatorSubplot = this.getChartIndicatorProvider().getBollingerBandWidthPlot(instrument, dateAxis,
                TRADING_DAYS_3_MONTHS);

        // Build combined plot based on subplots.
        combinedPlot.setDomainAxis(dateAxis);
        combinedPlot.add(indicatorSubplot, 1); // Indicator Plot takes 1 vertical size unit.
        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.
        combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }
}
