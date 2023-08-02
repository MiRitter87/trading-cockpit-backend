package backend.controller.chart;

import java.util.List;
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

import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Controller for the creation of a chart displaying the percentage of instruments trading above the SMA(50).
 *
 * @author Michael
 */
public class AboveSma50ChartController extends StatisticChartController {
    /**
     * Gets a chart with the percentage of instruments trading above their SMA(50).
     *
     * @param instrumentType The InstrumentType for which the chart is created.
     * @param listId         The ID of the list defining the instruments used for Statistic chart creation.
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getInstrumentsAboveSma50Chart(final InstrumentType instrumentType, final Integer listId)
            throws Exception {
        List<Statistic> statistics = this.getStatistics(instrumentType, listId);
        XYDataset dataset = this.getInstrumentsAboveSma50Dataset(statistics);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                this.getResources().getString("chart.aboveSma50.titleName"), null, null, dataset, true, true, false);

        this.applyBackgroundTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Constructs a XYDataset for the percentage of instruments trading above their SMA(50) chart.
     *
     * @param statistics The statistics for which the chart is calculated.
     * @return The XYDataset.
     * @throws Exception XYDataset creation failed.
     */
    private XYDataset getInstrumentsAboveSma50Dataset(final List<Statistic> statistics) throws Exception {
        Statistic statistic;
        TimeSeries timeSeries = new TimeSeries(this.getResources().getString("chart.aboveSma50.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        ListIterator<Statistic> iterator;

        // Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
        iterator = statistics.listIterator(statistics.size());
        while (iterator.hasPrevious()) {
            statistic = iterator.previous();
            timeSeries.add(new Day(statistic.getDate()), statistic.getPercentAboveSma50());
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }
}
