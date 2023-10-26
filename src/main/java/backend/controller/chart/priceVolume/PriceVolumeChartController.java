package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import backend.controller.NoQuotationsExistException;
import backend.controller.chart.ChartController;
import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.webservice.Indicator;

/**
 * Controller for the creation of a chart displaying an Instrument with price and volume. The chart can be configured to
 * add additional overlays and plots.
 *
 * @author Michael
 */
public class PriceVolumeChartController extends ChartController {
    /**
     * DAO to access chart object data.
     */
    private ChartObjectDAO chartObjectDAO;

    /**
     * Provider of indicator plots.
     */
    private ChartIndicatorProvider chartIndicatorProvider;

    /**
     * Initializes the PriceVolumeChartController.
     */
    public PriceVolumeChartController() {
        this.chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
        this.chartIndicatorProvider = new ChartIndicatorProvider(this.getQuotationDAO());
    }

    /**
     * Gets a chart of an Instrument with volume information.
     *
     * @param instrumentId    The ID of the Instrument used for chart creation.
     * @param overlays        The requested chart overlays.
     * @param withVolume      Show volume information.
     * @param withSma30Volume Show SMA(30) of volume.
     * @param indicator       The Indicator that is being displayed above the chart.
     * @param rsInstrumentId  The ID of the Instrument used to build the RS line (only used if type of Indicator is
     *                        RS_LINE).
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getPriceVolumeChart(final Integer instrumentId, final List<String> overlays,
            final boolean withVolume, final boolean withSma30Volume, final Indicator indicator,
            final Integer rsInstrumentId) throws NoQuotationsExistException, Exception {

        Instrument instrument = this.getInstrumentWithQuotations(instrumentId);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = null;
        XYPlot indicatorSubplot = null;
        final int candleStickPlotWeight = 4;

        if (withVolume) {
            volumeSubplot = this.getVolumePlot(instrument, dateAxis);
            this.addMovingAverageVolume(instrument, withSma30Volume, volumeSubplot);
            this.clipVolumeAt2TimesAverage(volumeSubplot, instrument);
        }

        indicatorSubplot = this.getIndicatorPlot(indicator, rsInstrumentId, instrument, dateAxis);

        this.addMovingAveragesPrice(instrument, overlays, candleStickSubplot);
        this.addHorizontalLines(instrument, candleStickSubplot);

        // Build combined plot based on subplots.
        combinedPlot.setDomainAxis(dateAxis);

        if (indicatorSubplot != null) {
            combinedPlot.add(indicatorSubplot, 1); // Indicator Plot takes 1 vertical size unit.
        }

        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.

        if (withVolume) {
            combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        }

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }

    /**
     * Adds moving averages of the price to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param overlays           The requested chart overlays.
     * @param candleStickSubplot The Plot to which moving averages are added.
     */
    private void addMovingAveragesPrice(final Instrument instrument, final List<String> overlays,
            final XYPlot candleStickSubplot) {

        if (overlays == null || overlays.isEmpty()) {
            return;
        }

        if (overlays.contains(ChartOverlay.EMA_21.toString())) {
            this.addEma21(instrument, candleStickSubplot);
        }

        if (overlays.contains(ChartOverlay.SMA_50.toString())) {
            this.addSma50(instrument, candleStickSubplot);
        }

        if (overlays.contains(ChartOverlay.SMA_150.toString())) {
            this.addSma150(instrument, candleStickSubplot);
        }

        if (overlays.contains(ChartOverlay.SMA_200.toString())) {
            this.addSma200(instrument, candleStickSubplot);
        }
    }

    /**
     * Adds the EMA(21) to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param candleStickSubplot The Plot to which the EMA(21) is added.
     */
    private void addEma21(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries ema21TimeSeries = new TimeSeries(
                this.getResources().getString("chart.priceVolume.timeSeriesEma21Name"));
        int index = candleStickSubplot.getDatasetCount();

        for (Quotation tempQuotation : quotationsSortedByDate) {
            if (tempQuotation.getIndicator().getEma21() == 0) {
                continue;
            }

            ema21TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getEma21());
        }

        timeSeriesCollection.addSeries(ema21TimeSeries);

        candleStickSubplot.setDataset(index, timeSeriesCollection);
        candleStickSubplot.mapDatasetToRangeAxis(index, 0);

        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        smaRenderer.setSeriesPaint(0, Color.ORANGE);
        candleStickSubplot.setRenderer(index, smaRenderer);
        candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Adds the SMA(50) to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param candleStickSubplot The Plot to which the SMA(50) is added.
     */
    private void addSma50(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma50TimeSeries = new TimeSeries(
                this.getResources().getString("chart.priceVolume.timeSeriesSma50Name"));
        int index = candleStickSubplot.getDatasetCount();

        for (Quotation tempQuotation : quotationsSortedByDate) {
            if (tempQuotation.getIndicator().getSma50() == 0) {
                continue;
            }

            sma50TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma50());
        }

        timeSeriesCollection.addSeries(sma50TimeSeries);

        candleStickSubplot.setDataset(index, timeSeriesCollection);
        candleStickSubplot.mapDatasetToRangeAxis(index, 0);

        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        smaRenderer.setSeriesPaint(0, Color.BLUE);
        candleStickSubplot.setRenderer(index, smaRenderer);
        candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Adds the SMA(150) to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param candleStickSubplot The Plot to which the SMA(150) is added.
     */
    private void addSma150(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma150TimeSeries = new TimeSeries(
                this.getResources().getString("chart.priceVolume.timeSeriesSma150Name"));
        int index = candleStickSubplot.getDatasetCount();

        for (Quotation tempQuotation : quotationsSortedByDate) {
            if (tempQuotation.getIndicator().getSma150() == 0) {
                continue;
            }

            sma150TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma150());
        }

        timeSeriesCollection.addSeries(sma150TimeSeries);

        candleStickSubplot.setDataset(index, timeSeriesCollection);
        candleStickSubplot.mapDatasetToRangeAxis(index, 0);

        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        smaRenderer.setSeriesPaint(0, Color.RED);
        candleStickSubplot.setRenderer(index, smaRenderer);
        candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Adds the SMA(200) to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param candleStickSubplot The Plot to which the SMA(200) is added.
     */
    private void addSma200(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma200TimeSeries = new TimeSeries(
                this.getResources().getString("chart.priceVolume.timeSeriesSma200Name"));
        int index = candleStickSubplot.getDatasetCount();

        for (Quotation tempQuotation : quotationsSortedByDate) {
            if (tempQuotation.getIndicator().getSma200() == 0) {
                continue;
            }

            sma200TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma200());
        }

        timeSeriesCollection.addSeries(sma200TimeSeries);

        candleStickSubplot.setDataset(index, timeSeriesCollection);
        candleStickSubplot.mapDatasetToRangeAxis(index, 0);

        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        smaRenderer.setSeriesPaint(0, Color.GREEN);
        candleStickSubplot.setRenderer(index, smaRenderer);
        candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Adds the SMA(30) of the volume to the chart.
     *
     * @param instrument      The Instrument whose price and volume data are displayed.
     * @param withSma30Volume Show SMA(30) of volume.
     * @param volumeSubplot   The Plot to which the SMA(30) of the volume is added.
     */
    private void addMovingAverageVolume(final Instrument instrument, final boolean withSma30Volume,
            final XYPlot volumeSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma30VolumeTimeSeries = new TimeSeries(
                this.getResources().getString("chart.priceVolume.timeSeriesSma30VolumeName"));
        int index = volumeSubplot.getDatasetCount();

        if (!withSma30Volume) {
            return;
        }

        for (Quotation tempQuotation : quotationsSortedByDate) {
            if (tempQuotation.getIndicator().getSma30Volume() == 0) {
                continue;
            }

            sma30VolumeTimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma30Volume());
        }

        timeSeriesCollection.addSeries(sma30VolumeTimeSeries);

        volumeSubplot.setDataset(index, timeSeriesCollection);
        volumeSubplot.mapDatasetToRangeAxis(index, 0);

        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        smaRenderer.setSeriesPaint(0, Color.BLACK);
        volumeSubplot.setRenderer(index, smaRenderer);
        volumeSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Builds a plot to display Indicator data of an Instrument.
     *
     * @param indicator      The requested Indicator.
     * @param rsInstrumentId The ID of the Instrument used to calculate the RS line.
     * @param instrument     The Instrument for which the indicator is being calculated.
     * @param timeAxis       The x-Axis (time).
     * @return A XYPlot depicting Indicator data.
     * @throws Exception Failed to construct indicator plot.
     */
    private XYPlot getIndicatorPlot(final Indicator indicator, final Integer rsInstrumentId,
            final Instrument instrument, final ValueAxis timeAxis) throws Exception {

        switch (indicator) {
        case RS_LINE:
            return this.chartIndicatorProvider.getRsLinePlot(rsInstrumentId, instrument, timeAxis);
        case BBW:
            return this.chartIndicatorProvider.getBollingerBandWidthPlot(instrument, timeAxis);
        case SLOW_STOCHASTIC:
            return this.chartIndicatorProvider.getSlowStochasticPlot(instrument, timeAxis);
        case NONE:
            return null;
        default:
            return null;
        }
    }

    /**
     * Adds horizontal lines to the candlestick subplot of the given Instrument.
     *
     * @param instrument         The Instrument.
     * @param candleStickSubplot The candlestick plot of the Instrument.
     * @throws Exception Failed to add horizontal lines.
     */
    private void addHorizontalLines(final Instrument instrument, final XYPlot candleStickSubplot) throws Exception {
        List<HorizontalLine> horizontalLines = this.chartObjectDAO.getHorizontalLines(instrument.getId());

        for (HorizontalLine horizontalLine : horizontalLines) {
            this.addHorizontalLine(candleStickSubplot, horizontalLine.getPrice().doubleValue());
        }
    }
}
