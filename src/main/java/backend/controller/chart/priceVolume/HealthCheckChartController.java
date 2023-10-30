package backend.controller.chart.priceVolume;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;

import backend.controller.NoQuotationsExistException;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.model.instrument.Instrument;

/**
 * Controller for the creation of a chart displaying an Instrument with health check events.
 *
 * @author Michael
 */
public class HealthCheckChartController extends PriceVolumeChartController {
    /**
     * Gets a chart of an Instrument marked with health check events.
     *
     * @param instrumentId   The ID of the Instrument used for chart creation.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getHealthCheckChart(final Integer instrumentId, final HealthCheckProfile profile,
            final Integer lookbackPeriod) throws NoQuotationsExistException, Exception {

        Instrument instrument = this.getInstrumentWithQuotations(instrumentId);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        final int candleStickPlotWeight = 4;

        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = this.getVolumePlot(instrument, dateAxis);
        // TODO Get subplot with number of health check events.

        // Build combined plot based on subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        // TODO Add subplot with number of health check events.
        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.
        combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        combinedPlot.setDomainAxis(dateAxis);

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }
}
