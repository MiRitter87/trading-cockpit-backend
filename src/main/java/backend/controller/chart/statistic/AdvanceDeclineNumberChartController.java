package backend.controller.chart.statistic;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
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

import backend.controller.scan.MovingAverageCalculator;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.statistic.Statistic;

/**
 * Controller for the creation of a chart displaying the cumulative Advance/Decline Number.
 *
 * @author Michael
 */
public class AdvanceDeclineNumberChartController extends StatisticChartController {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Gets a chart of the cumulative Advance/Decline Number.
     *
     * @param instrumentType The InstrumentType for which the chart is created.
     * @param listId         The ID of the list defining the instruments used for Statistic chart creation.
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getAdvanceDeclineNumberChart(final InstrumentType instrumentType, final Integer listId)
            throws Exception {
        List<Statistic> statistics = this.getStatisticsForList(instrumentType, listId, TRADING_DAYS_PER_YEAR);
        XYDataset dataset = this.getAdvanceDeclineNumberDataset(statistics);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(this.getResources().getString("chart.adNumber.titleName"),
                null, null, dataset, true, true, false);

        this.addSma50(chart, statistics);
        this.applyBackgroundTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Constructs a XYDataset for the cumulative Advance/Decline number chart.
     *
     * @param statistics The statistics for which the chart is calculated.
     * @return The XYDataset.
     * @throws Exception XYDataset creation failed.
     */
    private XYDataset getAdvanceDeclineNumberDataset(final List<Statistic> statistics) throws Exception {
        Statistic statistic;
        TimeSeries timeSeries = new TimeSeries(this.getResources().getString("chart.adNumber.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);
        ListIterator<Statistic> iterator;
        int cumulativeADNumber = 0;

        // Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
        iterator = statistics.listIterator(statistics.size());
        while (iterator.hasPrevious()) {
            statistic = iterator.previous();
            cumulativeADNumber += statistic.getAdvanceDeclineNumber();
            timeSeries.add(new Day(statistic.getDate()), cumulativeADNumber);
        }

        timeSeriesCollection.addSeries(timeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesCollection;
    }

    /**
     * Adds the SMA(50) to the chart.
     *
     * @param chart      The cumulative Advance/Decline number chart.
     * @param statistics Statistics used for cumulative A/D number calculation.
     */
    private void addSma50(final JFreeChart chart, final List<Statistic> statistics) {
        TimeSeriesCollection sma50TimeSeriesCollection;
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
        int index = plot.getDatasetCount();

        sma50TimeSeriesCollection = this.getSma50TimeSeriesCollection(statistics);

        plot.setDataset(index, sma50TimeSeriesCollection);
        plot.mapDatasetToRangeAxis(index, 0);

        smaRenderer.setSeriesPaint(0, Color.BLUE);

        plot.setRenderer(index, smaRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    /**
     * Provides a TimeSeriesCollection with the values of the SMA(50) of the cumulative A/D number.
     *
     * @param statistics Statistics used for cumulative A/D number calculation.
     * @return The TimeSeriesCollection with the values of the SMA(50).
     */
    private TimeSeriesCollection getSma50TimeSeriesCollection(final List<Statistic> statistics) {
        MovingAverageCalculator maCalculator = new MovingAverageCalculator();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries sma50TimeSeries = new TimeSeries(this.resources.getString("chart.adNumber.timeSeriesSma50Name"));
        QuotationArray quotationArray = this.getAdNumbersAsQuotationArray(statistics);
        ListIterator<Quotation> iterator;
        Quotation quotation;
        float sma50;
        final int days50 = 50;
        int indexOfQuotation;

        quotationArray.sortQuotationsByDate();

        // Iterate quotations backwards because XYDatasets are constructed from oldest to newest value.
        iterator = quotationArray.getQuotations().listIterator(quotationArray.getQuotations().size());
        while (iterator.hasPrevious()) {
            quotation = iterator.previous();
            indexOfQuotation = quotationArray.getQuotations().indexOf(quotation);

            // Skip SMA(50) calculation for the oldest 49 days.
            if ((quotationArray.getQuotations().size() - days50 - indexOfQuotation) < 0) {
                continue;
            }

            sma50 = maCalculator.getSimpleMovingAverage(days50, quotation, quotationArray);
            sma50TimeSeries.add(new Day(quotation.getDate()), sma50);
        }

        timeSeriesCollection.addSeries(sma50TimeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesCollection;
    }

    /**
     * Provides a QuotationArray where the cumulative Advance/Decline number is stored in the closing price of each
     * Quotation. This way the SMA(50) can be calculated easily using existing methods.
     *
     * @param statistics Statistics used for cumulative A/D number calculation.
     * @return A QuotationArray.
     */
    private QuotationArray getAdNumbersAsQuotationArray(final List<Statistic> statistics) {
        Quotation quotation;
        QuotationArray quotationArray = new QuotationArray();
        ListIterator<Statistic> iterator;
        int cumulativeADNumber = 0;
        Statistic statistic;

        iterator = statistics.listIterator(statistics.size());
        while (iterator.hasPrevious()) {
            statistic = iterator.previous();
            cumulativeADNumber += statistic.getAdvanceDeclineNumber();
            quotation = new Quotation();
            quotation.setClose(new BigDecimal(cumulativeADNumber));
            quotation.setDate(statistic.getDate());
            quotationArray.getQuotations().add(quotation);
        }

        return quotationArray;
    }
}
