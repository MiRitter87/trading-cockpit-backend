package backend.controller;

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

import backend.dao.DAOManager;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Controls the generation of statistical charts.
 * 
 * @author Michael
 */
public class StatisticChartController {
	/**
	 * DAO to access Statistic data.
	 */
	StatisticDAO statisticDAO;
	
	
	/**
	 * Initializes the StatisticChartController.
	 */
	public StatisticChartController() {
		this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
	}
	
	
	/**
	 * Gets a chart of the cumulative Advance/Decline Number.
	 * 
	 * @param instrumentType The InstrumentType for which the chart is created.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getAdvanceDeclineNumberChart(final InstrumentType instrumentType) throws Exception {
		XYDataset dataset = this.getAdvanceDeclineNumberDataset(instrumentType);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
			"!Advance/Decline Number (Cumulative)",
			"!Datum", "!Advance/Decline Number (Cumulative)",
			dataset,
			true,
			true,
			false
		);
		
		return chart;
	}
	
	
	/**
	 * Constructs a XYDataset for the cumulative Advance/Decline number chart.
	 * 
	 * @param instrumentType The InstrumentType for which the XYDataset is created.
	 * @return The XYDataset.
	 * @throws Exception XYDataset creation failed.
	 */
	private XYDataset getAdvanceDeclineNumberDataset(final InstrumentType instrumentType) throws Exception {
		List<Statistic> statistics;
		Statistic statistic;
		TimeSeries timeSeries = new TimeSeries("!Advance Decline Number (Cumulative)");
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
		ListIterator<Statistic> iterator;
		int cumulativeADNumber = 0;
		
		statistics = statisticDAO.getStatistics(instrumentType);
		
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
