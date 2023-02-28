package backend.controller.statistic;

import org.jfree.chart.JFreeChart;

/**
 * Controller for the creation of a chart displaying an Instrument with Follow-Through Days.
 * 
 * @author Michael
 */
public class FollowThroughDaysChartController extends StatisticChartController {
	/**
	 * Gets a chart of an Instrument marked with Follow-Through Days.
	 * 
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getFollowThroughDaysChart(final Integer instrumentId) throws Exception {
		return null;
	}
}
