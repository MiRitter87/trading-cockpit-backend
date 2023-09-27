package backend.controller.chart;

import org.jfree.chart.JFreeChart;

import backend.model.instrument.InstrumentType;

/**
 * Controller for the creation of a chart displaying the percentage of instruments trading above the SMA(200).
 *
 * @author Michael
 */
public class AboveSma200ChartController extends StatisticChartController {
    /**
     * Gets a chart with the percentage of instruments trading above their SMA(200).
     *
     * @param instrumentType The InstrumentType for which the chart is created.
     * @param listId         The ID of the list defining the instruments used for Statistic chart creation.
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getInstrumentsAboveSma200Chart(final InstrumentType instrumentType, final Integer listId)
            throws Exception {

        return null;
    }
}
