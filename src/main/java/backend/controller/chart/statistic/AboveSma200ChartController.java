package backend.controller.chart.statistic;

import java.util.ListIterator;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.model.statistic.Statistic;

/**
 * Controller for the creation of a chart displaying the percentage of instruments trading above the SMA(200).
 *
 * @author Michael
 */
public class AboveSma200ChartController extends StatisticChartController {
    /**
     * Initializes the AboveSma200ChartController.
     *
     * @param listId The ID of the list defining the instruments used for Statistic chart creation.
     * @throws Exception Failed to initialize data.
     */
    public AboveSma200ChartController(final Integer listId) throws Exception {
        super(listId);
    }

    /**
     * Gets a chart with the percentage of instruments trading above their SMA(200).
     *
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getInstrumentsAboveSma200Chart() throws Exception {
        XYDataset dataset = this.getInstrumentsAboveSma200Dataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                this.getResources().getString("chart.aboveSma200.titleName"), null, null, dataset, true, true, false);

        this.applyBackgroundTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Constructs a XYDataset for the percentage of instruments trading above their SMA(200) chart.
     *
     * @return The XYDataset.
     * @throws Exception XYDataset creation failed.
     */
    private XYDataset getInstrumentsAboveSma200Dataset() throws Exception {
        Statistic statistic;
        TimeSeries timeSeries = new TimeSeries(this.getResources().getString("chart.aboveSma200.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        ListIterator<Statistic> iterator;

        // Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
        iterator = this.getStatistics().listIterator(this.getStatistics().size());
        while (iterator.hasPrevious()) {
            statistic = iterator.previous();
            timeSeries.add(new Day(statistic.getDate()), statistic.getPercentAboveSma200());
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }
}
