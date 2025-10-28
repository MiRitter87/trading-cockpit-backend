package backend.controller.chart.statistic;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ListIterator;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.calculator.MovingAverageCalculator;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.statistic.Statistic;

/**
 * Controller for the creation of a chart displaying the percentage of instruments trading above the SMA(50).
 *
 * @author Michael
 */
public class AboveSma50ChartController extends StatisticChartController {
    /**
     * Initializes the AboveSma50ChartController.
     *
     * @param listId The ID of the list defining the instruments used for Statistic chart creation.
     * @throws Exception Failed to initialize data.
     */
    public AboveSma50ChartController(final Integer listId) throws Exception {
        super(listId);
    }

    /**
     * Gets a chart with the percentage of instruments trading above their SMA(50).
     *
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getInstrumentsAboveSma50Chart() throws Exception {
        XYDataset dataset = this.getInstrumentsAboveSma50Dataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                this.getResources().getString("chart.aboveSma50.titleName"), null, null, dataset, true, true, false);

        this.addSma10(chart);
        this.applyStatisticalTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Constructs a XYDataset for the percentage of instruments trading above their SMA(50) chart.
     *
     * @return The XYDataset.
     * @throws Exception XYDataset creation failed.
     */
    private XYDataset getInstrumentsAboveSma50Dataset() throws Exception {
        Statistic statistic;
        TimeSeries timeSeries = new TimeSeries(this.getResources().getString("chart.aboveSma50.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        ListIterator<Statistic> iterator;

        // Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
        iterator = this.getStatistics().listIterator(this.getStatistics().size());
        while (iterator.hasPrevious()) {
            statistic = iterator.previous();
            timeSeries.add(new Day(statistic.getDate()), statistic.getPercentAboveSma50());
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }

    /**
     * Adds the SMA(10) to the chart.
     *
     * @param chart The chart with the percentage of instruments trading above their SMA(50).
     */
    private void addSma10(final JFreeChart chart) {
        TimeSeriesCollection sma10TimeSeriesCollection;
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        int index = plot.getDatasetCount();

        sma10TimeSeriesCollection = this.getSma10TimeSeriesCollection();

        plot.setDataset(index, sma10TimeSeriesCollection);
        plot.mapDatasetToRangeAxis(index, 0);

        smaRenderer.setSeriesPaint(0, Color.BLUE);

        plot.setRenderer(index, smaRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Provides a TimeSeriesCollection with the values of the SMA(10) of the % above SMA(50).
     *
     * @return The TimeSeriesCollection with the values of the SMA(10) of the % above SMA(50).
     */
    private TimeSeriesCollection getSma10TimeSeriesCollection() {
        MovingAverageCalculator maCalculator = new MovingAverageCalculator();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma10TimeSeries = new TimeSeries(
                this.getResources().getString("chart.aboveSma50.timeSeriesSma10Name"));
        QuotationArray quotationArray = this.getPercentAboveSma50AsQuotationArray();
        ListIterator<Quotation> iterator;
        Quotation quotation;
        float sma10;
        final int days10 = 10;
        int indexOfQuotation;

        quotationArray.sortQuotationsByDate();

        // Iterate quotations backwards because XYDatasets are constructed from oldest to newest value.
        iterator = quotationArray.getQuotations().listIterator(quotationArray.getQuotations().size());
        while (iterator.hasPrevious()) {
            quotation = iterator.previous();
            indexOfQuotation = quotationArray.getQuotations().indexOf(quotation);

            // Skip SMA(10) calculation for the oldest 9 days.
            if ((quotationArray.getQuotations().size() - days10 - indexOfQuotation) < 0) {
                continue;
            }

            sma10 = maCalculator.getSimpleMovingAverage(days10, quotation, quotationArray);
            sma10TimeSeries.add(new Day(quotation.getDate()), sma10);
        }

        timeSeriesCollection.addSeries(sma10TimeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesCollection;
    }

    /**
     * Provides a QuotationArray where the % above SMA(50) is stored in the closing price of each Quotation. This way
     * the SMA(10) can be calculated easily using existing methods.
     *
     * @return A QuotationArray.
     */
    private QuotationArray getPercentAboveSma50AsQuotationArray() {
        Quotation quotation;
        QuotationArray quotationArray = new QuotationArray();
        ListIterator<Statistic> iterator;
        Statistic statistic;

        iterator = this.getStatistics().listIterator(this.getStatistics().size());
        while (iterator.hasPrevious()) {
            statistic = iterator.previous();
            quotation = new Quotation();
            quotation.setClose(new BigDecimal(statistic.getPercentAboveSma50()));
            quotation.setDate(statistic.getDate());
            quotationArray.getQuotations().add(quotation);
        }

        return quotationArray;
    }
}
