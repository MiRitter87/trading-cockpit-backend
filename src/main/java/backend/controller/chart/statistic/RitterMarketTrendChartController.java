package backend.controller.chart.statistic;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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
 * Controller for the creation of a chart displaying the Ritter Market Trend.
 *
 * @author Michael
 */
public class RitterMarketTrendChartController extends StatisticChartController {
    /**
     * Initializes the RitterMarketTrendChartController.
     *
     * @param listId The ID of the list defining the instruments used for Statistic chart creation.
     * @throws Exception Failed to initialize data.
     */
    public RitterMarketTrendChartController(final Integer listId) throws Exception {
        super(listId);
    }

    /**
     * Gets a chart of the Ritter Market Trend.
     *
     * @param instrumentType The InstrumentType for which the chart is created.
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getRitterMarketTrendChart(final InstrumentType instrumentType) throws Exception {
        List<Statistic> statistics = this.getStatisticsForList(instrumentType, TRADING_DAYS_PER_YEAR);
        XYDataset dataset = this.getRitterMarketTrendDataset(statistics);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                this.getResources().getString("chart.ritterMarketTrend.titleName"), null, null, dataset, true, true,
                false);

        this.addHorizontalLine(chart.getXYPlot(), 0, Color.BLACK);
        this.applyBackgroundTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Constructs a XYDataset for the Ritter Market Trend chart.
     *
     * @param statistics The statistics for which the chart is calculated.
     * @return The XYDataset.
     */
    private XYDataset getRitterMarketTrendDataset(final List<Statistic> statistics) {
        Statistic statistic;
        TimeSeries timeSeries = new TimeSeries(this.getResources().getString("chart.ritterMarketTrend.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        float movingAverage;
        final int periodOfMovingAverage = 10;

        // Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
        for (int i = statistics.size() - 1; i >= 0; i--) {
            try {
                statistic = statistics.get(i);
                movingAverage = this.getMovingAverageOfRitterMarketTrend(statistics, periodOfMovingAverage, i);
                timeSeries.add(new Day(statistic.getDate()), movingAverage);
            } catch (Exception exception) {
                continue;
            }
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }

    /**
     * Calculates the moving average of the Ritter Market Trend for the given number of days. The moving average is
     * normalized to a value between -100 and 100 based on the number of instruments.
     *
     * @param statistics A List of statistical values containing the raw values of the Ritter Market Trend for each day.
     * @param period     The period in days for Moving Average calculation.
     * @param beginIndex The begin index for calculation.
     * @return The normalized moving average of the Ritter Market Trend.
     * @throws Exception Calculation of Moving Average failed.
     */
    private float getMovingAverageOfRitterMarketTrend(final List<Statistic> statistics, final int period,
            final int beginIndex) throws Exception {
        int endIndex = beginIndex + period - 1;
        int sum = 0;
        int normalizedDailyValue;
        int lastValidBeginIndex;
        Statistic statistic;
        BigDecimal movingAverage;
        final int numberAdditionalDaysForSmaVolume = 29;
        final int hundredPercent = 100;

        // At least "30 + period" days have to exist to calculate the Moving Average of the RMT.
        // 30 days are needed because this is the minimum number of values needed for availability of SMA(30) of volume.
        // The period is added, because this is the number of days needed for moving average calculation of RMT.
        lastValidBeginIndex = statistics.size() - numberAdditionalDaysForSmaVolume - period;

        if (beginIndex > lastValidBeginIndex) {
            throw new Exception("Not enough statistical values exist to calculate moving average of RMT.");
        }

        for (int i = beginIndex; i <= endIndex; i++) {
            statistic = statistics.get(i);
            normalizedDailyValue = statistic.getNumberRitterMarketTrend() * hundredPercent
                    / statistic.getNumberOfInstruments();
            sum += normalizedDailyValue;
        }

        movingAverage = new BigDecimal(sum).divide(new BigDecimal(period), 1, RoundingMode.HALF_UP);

        return movingAverage.floatValue();
    }
}
