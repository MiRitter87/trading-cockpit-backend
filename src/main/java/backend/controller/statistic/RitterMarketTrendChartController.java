package backend.controller.statistic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
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
	 * Gets a chart of the Ritter Market Trend.
	 * 
	 * @param instrumentType The InstrumentType for which the chart is created.
	 * @param listId The ID of the list defining the instruments used for chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getRitterMarketTrendChart(final InstrumentType instrumentType, final Integer listId) throws Exception {
		List<Statistic> statistics = this.getStatistics(instrumentType, listId);
		XYDataset dataset = this.getRitterMarketTrendDataset(statistics);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
			this.resources.getString("statistic.chartRitterMarketTrend.titleName"),
			null, null,	dataset, true, true, false
		);
		
		this.addHorizontalLine(chart.getXYPlot(), 0);
		
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
		TimeSeries timeSeries = new TimeSeries(this.resources.getString("statistic.chartRitterMarketTrend.timeSeriesName"));
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
		float movingAverage;
		
		//Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
		for(int i = statistics.size() - 1; i >= 0; i--) {
			try {
				statistic = statistics.get(i);
				movingAverage = this.getMovingAverageOfRitterMarketTrend(statistics, 10, i);
				timeSeries.add(new Day(statistic.getDate()), movingAverage);
			}
			catch(Exception exception) {
				continue;
			}
		}
		
        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);
        
        return timeSeriesColleciton;
	}
	
	
	/**
	 * Calculates the moving average of the Ritter Market Trend for the given number of days.
	 * 
	 * @param statistics A List of statistical values containing the raw values of the Ritter Market Trend for each day.
	 * @param period The period in days for Moving Average calculation.
	 * @param beginIndex The begin index for calculation.
	 * @return The moving average of the Ritter Market Trend.
	 * @throws Exception Calculation of Moving Average failed.
	 */
	private float getMovingAverageOfRitterMarketTrend(final List<Statistic> statistics, final int period, final int beginIndex) throws Exception {
		int endIndex = beginIndex + period - 1;
		int sum = 0;
		Statistic statistic;
		BigDecimal movingAverage;
		
		if((beginIndex + 29 - 1) >= statistics.size())
			throw new Exception("The RMT is not available for the oldest 29 days of the statistic because SMA(30) of volume is not available.");
		
		if(endIndex >= statistics.size())
			throw new Exception("Not enough historical statistical values available to calculate moving average for the given period.");
		
		for(int i = beginIndex; i <= endIndex; i++) {
			statistic = statistics.get(i);
			sum += statistic.getNumberRitterMarketTrend();
		}
		
		movingAverage = new BigDecimal(sum).divide(new BigDecimal(period), 1, RoundingMode.HALF_UP);
		
		return movingAverage.floatValue();
	}
	
	
	/**
	 * Adds a horizontal line to the given XYPlot.
	 * 
	 * @param plot The XYPlot to which the horizontal line is added.
	 * @param double horizontalLinePosition The value on the y-axis at which the horizontal line is being drawn.
	 */
	private void addHorizontalLine(XYPlot plot, final double horizontalLinePosition) {
		ValueMarker valueMarker = new ValueMarker(horizontalLinePosition, Color.BLACK, new BasicStroke(2), null, null, 1.0f);
		plot.addRangeMarker(valueMarker);
	}
}
