package backend.controller.statistic;

import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Controller for the creation of a chart displaying the cumulative Advance/Decline Number.
 * 
 * @author Michael
 */
public class AdvanceDeclineNumberChartController extends StatisticChartController {
	/**
	 * Gets a chart of the cumulative Advance/Decline Number.
	 * 
	 * @param instrumentType The InstrumentType for which the chart is created.
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getAdvanceDeclineNumberChart(final InstrumentType instrumentType, final Integer listId) throws Exception {
		List<Statistic> statistics = this.getStatistics(instrumentType, listId);
		XYDataset dataset = this.getAdvanceDeclineNumberDataset(statistics);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
			this.resources.getString("statistic.chartADNumber.titleName"),
			null, null,	dataset, true, true, false
		);
		
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
		TimeSeries timeSeries = new TimeSeries(this.resources.getString("statistic.chartADNumber.timeSeriesName"));
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
		ListIterator<Statistic> iterator;
		int cumulativeADNumber = 0;
		
		//Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
		iterator = statistics.listIterator(statistics.size());
		while(iterator.hasPrevious()) {
			statistic = iterator.previous();
			cumulativeADNumber += statistic.getAdvanceDeclineNumber();
			timeSeries.add(new Day(statistic.getDate()), cumulativeADNumber);
		}
		
        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);
        
        return timeSeriesColleciton;
	}
}
