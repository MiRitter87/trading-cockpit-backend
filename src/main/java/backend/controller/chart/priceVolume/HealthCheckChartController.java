package backend.controller.chart.priceVolume;

import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;

import backend.controller.NoQuotationsExistException;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.controller.instrumentCheck.InstrumentCheckController;
import backend.model.instrument.Instrument;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.Protocol;

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
        XYPlot healthSubplot = this.getHealthPlot(instrument, dateAxis, profile, lookbackPeriod);

        // Build combined plot based on subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        combinedPlot.add(healthSubplot, 1); // Health Plot takes 1 vertical size unit.
        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.
        combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        combinedPlot.setDomainAxis(dateAxis);

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }

    /**
     * Builds a plot to display health check events.
     *
     * @param instrument     The Instrument.
     * @param timeAxis       The x-Axis (time).
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return A XYPlot depicting health check events.
     * @throws Exception Plot generation failed.
     */
    private XYPlot getHealthPlot(final Instrument instrument, final ValueAxis timeAxis,
            final HealthCheckProfile profile, final Integer lookbackPeriod) throws Exception {

        Protocol healthProtocol = this.getHealthProtocol(instrument, profile, lookbackPeriod);

        // 2. Evaluate protocol and build plot dataset

        // 3. Construct plot based on dataset

        return null;
    }

    /**
     * Performs a health check and returns the Protocol.
     *
     * @param instrument     The Instrument.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The Protocol.
     * @throws Exception Failed to perform health check.
     */
    private Protocol getHealthProtocol(final Instrument instrument, final HealthCheckProfile profile,
            final Integer lookbackPeriod) throws Exception {

        InstrumentCheckController instrumentCheckController = new InstrumentCheckController();
        QuotationArray sortedQuotations = instrument.getQuotationArray();
        Date startDate;
        Protocol healthCheckProtocol;

        sortedQuotations.sortQuotationsByDate();
        startDate = instrumentCheckController.getStartDate(lookbackPeriod, sortedQuotations);

        healthCheckProtocol = instrumentCheckController.checkInstrumentWithProfile(instrument.getId(), startDate,
                profile);

        return healthCheckProtocol;
    }
}
