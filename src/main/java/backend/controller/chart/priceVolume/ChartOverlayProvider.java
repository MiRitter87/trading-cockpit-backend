package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import backend.model.instrument.Instrument;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;

/**
 * Provides overlays for plots that are used in a Price Volume chart of an Instrument.
 *
 * @author Michael
 */
public class ChartOverlayProvider {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Adds the EMA(21) to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param candleStickSubplot The Plot to which the EMA(21) is added.
     */
    public void addEma21(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries ema21TimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesEma21Name"));
        int index = candleStickSubplot.getDatasetCount();
        MovingAverageData maData;

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getIndicator().getMovingAverageData();

            if (maData == null || maData.getEma21() == 0) {
                continue;
            }

            ema21TimeSeries.add(new Day(tempQuotation.getDate()), maData.getEma21());
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
    public void addSma50(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma50TimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesSma50Name"));
        int index = candleStickSubplot.getDatasetCount();
        MovingAverageData maData;

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getIndicator().getMovingAverageData();

            if (maData == null || maData.getSma50() == 0) {
                continue;
            }

            sma50TimeSeries.add(new Day(tempQuotation.getDate()), maData.getSma50());
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
    public void addSma150(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma150TimeSeries = new TimeSeries(
                this.resources.getString("chart.priceVolume.timeSeriesSma150Name"));
        int index = candleStickSubplot.getDatasetCount();
        MovingAverageData maData;

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getIndicator().getMovingAverageData();

            if (maData == null || maData.getSma150() == 0) {
                continue;
            }

            sma150TimeSeries.add(new Day(tempQuotation.getDate()), maData.getSma150());
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
    public void addSma200(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma200TimeSeries = new TimeSeries(
                this.resources.getString("chart.priceVolume.timeSeriesSma200Name"));
        int index = candleStickSubplot.getDatasetCount();
        MovingAverageData maData;

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getIndicator().getMovingAverageData();

            if (maData == null || maData.getSma200() == 0) {
                continue;
            }

            sma200TimeSeries.add(new Day(tempQuotation.getDate()), maData.getSma200());
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
    public void addMovingAverageVolume(final Instrument instrument, final boolean withSma30Volume,
            final XYPlot volumeSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma30VolumeTimeSeries = new TimeSeries(
                this.resources.getString("chart.priceVolume.timeSeriesSma30VolumeName"));
        int index = volumeSubplot.getDatasetCount();
        MovingAverageData maData;

        if (!withSma30Volume) {
            return;
        }

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getIndicator().getMovingAverageData();

            if (maData == null || maData.getSma30Volume() == 0) {
                continue;
            }

            sma30VolumeTimeSeries.add(new Day(tempQuotation.getDate()), maData.getSma30Volume());
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
     * Adds the SMA(10) to the chart.
     *
     * @param instrument         The Instrument with quotations.
     * @param candleStickSubplot The Plot to which the SMA(10) is added.
     */
    public void addSma10(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma10TimeSeries = new TimeSeries(this.resources.getString("chart.pocketPivots.timeSeriesSma10Name"));
        int index = candleStickSubplot.getDatasetCount();
        MovingAverageData maData;

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getIndicator().getMovingAverageData();

            if (maData == null || maData.getSma10() == 0) {
                continue;
            }

            sma10TimeSeries.add(new Day(tempQuotation.getDate()), maData.getSma10());
        }

        timeSeriesCollection.addSeries(sma10TimeSeries);

        candleStickSubplot.setDataset(index, timeSeriesCollection);
        candleStickSubplot.mapDatasetToRangeAxis(index, 0);

        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        smaRenderer.setSeriesPaint(0, Color.BLACK);
        candleStickSubplot.setRenderer(index, smaRenderer);
        candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }
}
