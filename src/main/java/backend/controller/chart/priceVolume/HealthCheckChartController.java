package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;

import backend.controller.NoQuotationsExistException;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.controller.instrumentCheck.InstrumentCheckController;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;

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

        Instrument instrument = this.getInstrumentWithQuotations(instrumentId, TRADING_DAYS_PER_YEAR);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        final int candleStickPlotWeight = 4;

        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        this.addMovingAveragesPrice(instrument, candleStickSubplot);

        XYPlot volumeSubplot = this.getVolumePlot(instrument, dateAxis);
        this.getChartOverlayProvider().addMovingAverageVolume(instrument, volumeSubplot);
        this.clipVolumeAt2TimesAverage(volumeSubplot, instrument);

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
        IntervalXYDataset healthEventData = this.getHealthEventDataset(instrument, healthProtocol, profile,
                lookbackPeriod);
        NumberAxis healthEventAxis = new NumberAxis();

        XYBarRenderer healthEventRenderer = new XYBarRenderer();
        healthEventRenderer.setShadowVisible(false);
        this.setHealthEventBarColor(healthEventRenderer, profile);

        XYPlot healthEventSubplot = new XYPlot(healthEventData, timeAxis, healthEventAxis, healthEventRenderer);
        healthEventSubplot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return healthEventSubplot;
    }

    /**
     * Adds moving averages of the price to the candlestick plot.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param candleStickSubplot The Plot to which moving averages are added.
     */
    private void addMovingAveragesPrice(final Instrument instrument, final XYPlot candleStickSubplot) {
        List<String> overlays = new ArrayList<>();

        overlays.add(ChartOverlay.EMA_21.toString());
        overlays.add(ChartOverlay.SMA_50.toString());

        this.addMovingAveragesPrice(instrument, overlays, candleStickSubplot);
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
    public Protocol getHealthProtocol(final Instrument instrument, final HealthCheckProfile profile,
            final Integer lookbackPeriod) throws Exception {

        InstrumentCheckController instrumentCheckController = new InstrumentCheckController();
        QuotationArray sortedQuotations = instrument.getQuotationArray();
        Date startDate;
        Protocol healthCheckProtocol;

        sortedQuotations.sortQuotationsByDate();
        startDate = instrumentCheckController.getStartDate(lookbackPeriod, sortedQuotations);

        healthCheckProtocol = instrumentCheckController.checkInstrument(instrument.getId(), startDate, profile);

        return healthCheckProtocol;
    }

    /**
     * Gets a dataset containing the daily sum of health check events of the given Instrument.
     *
     * @param instrument     The Instrument.
     * @param protocol       The Protocol that contains the health check events.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return A dataset containing the daily sum of health check events.
     * @throws Exception Dataset creation failed.
     */
    private IntervalXYDataset getHealthEventDataset(final Instrument instrument, final Protocol protocol,
            final HealthCheckProfile profile, final Integer lookbackPeriod) throws Exception {

        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries healthEventTimeSeries;
        String timeSeriesName = this.getHealthEventTimeSeriesName(profile, lookbackPeriod);
        int eventNumber;

        healthEventTimeSeries = new TimeSeries(timeSeriesName);

        for (Quotation tempQuotation : quotationsSortedByDate) {
            eventNumber = this.getEventNumber(protocol, profile, tempQuotation);
            healthEventTimeSeries.add(new Day(tempQuotation.getDate()), eventNumber);
        }

        dataset.addSeries(healthEventTimeSeries);

        return dataset;
    }

    /**
     * Determines the number of events in the given Protocol for the day of the given Quotation. The calculation method
     * is based on the given HealthCheckProfile.
     *
     * @param protocol  The Protocol that contains the health check events.
     * @param profile   The HealthCheckProfile that is used.
     * @param quotation The Quotation containing the date information.
     * @return The calculated health check event number.
     */
    public int getEventNumber(final Protocol protocol, final HealthCheckProfile profile, final Quotation quotation) {
        List<ProtocolEntry> entriesOfDate;
        int eventNumber = 0;

        entriesOfDate = protocol.getEntriesOfDate(quotation.getDate());

        for (ProtocolEntry tempEntry : entriesOfDate) {
            if ((profile == HealthCheckProfile.CONFIRMATIONS
                    || profile == HealthCheckProfile.CONFIRMATIONS_WITHOUT_COUNTING)
                    && tempEntry.getCategory() == ProtocolEntryCategory.CONFIRMATION) {
                eventNumber++;
            }

            if ((profile == HealthCheckProfile.SELLING_INTO_WEAKNESS
                    || profile == HealthCheckProfile.WEAKNESS_WITHOUT_COUNTING)
                    && tempEntry.getCategory() == ProtocolEntryCategory.VIOLATION) {
                eventNumber++;
            }

            if (profile == HealthCheckProfile.SELLING_INTO_STRENGTH
                    && tempEntry.getCategory() == ProtocolEntryCategory.UNCERTAIN) {
                eventNumber++;
            }

            if (profile == HealthCheckProfile.ALL || profile == HealthCheckProfile.ALL_WITHOUT_COUNTING) {
                switch (tempEntry.getCategory()) {
                case CONFIRMATION:
                    eventNumber++;
                    break;
                case VIOLATION:
                    eventNumber--;
                    break;
                case UNCERTAIN:
                    eventNumber--;
                    break;
                default:
                    break;
                }
            }
        }

        return eventNumber;
    }

    /**
     * Sets the color of the health check event bars depending on the HealthCheckProfile.
     *
     * @param healthEventRenderer The renderer for health check events.
     * @param profile             The HealthCheckProfile
     */
    private void setHealthEventBarColor(final XYBarRenderer healthEventRenderer, final HealthCheckProfile profile) {
        switch (profile) {
        case ALL:
        case ALL_WITHOUT_COUNTING:
            healthEventRenderer.setSeriesPaint(0, Color.BLUE);
            break;
        case CONFIRMATIONS:
        case CONFIRMATIONS_WITHOUT_COUNTING:
            healthEventRenderer.setSeriesPaint(0, Color.GREEN);
            break;
        case SELLING_INTO_STRENGTH:
            healthEventRenderer.setSeriesPaint(0, Color.YELLOW);
            break;
        case SELLING_INTO_WEAKNESS:
        case WEAKNESS_WITHOUT_COUNTING:
            healthEventRenderer.setSeriesPaint(0, Color.RED);
            break;
        default:
            break;
        }
    }

    /**
     * Gets the name for the health event time series.
     *
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The name for the health event time series.
     */
    private String getHealthEventTimeSeriesName(final HealthCheckProfile profile, final Integer lookbackPeriod) {
        String timeSeriesName;

        switch (profile) {
        case ALL:
        case ALL_WITHOUT_COUNTING:
            timeSeriesName = MessageFormat
                    .format(this.getResources().getString("chart.healthCheck.timeSeriesEventName.all"), lookbackPeriod);
            break;
        case CONFIRMATIONS:
            timeSeriesName = MessageFormat.format(
                    this.getResources().getString("chart.healthCheck.timeSeriesEventName.confirmations"),
                    lookbackPeriod);
            break;
        case CONFIRMATIONS_WITHOUT_COUNTING:
            timeSeriesName = MessageFormat.format(
                    this.getResources().getString("chart.healthCheck.timeSeriesEventName.confirmationsWithoutCounting"),
                    lookbackPeriod);
            break;
        case SELLING_INTO_STRENGTH:
            timeSeriesName = MessageFormat.format(
                    this.getResources().getString("chart.healthCheck.timeSeriesEventName.strength"), lookbackPeriod);
            break;
        case SELLING_INTO_WEAKNESS:
            timeSeriesName = MessageFormat.format(
                    this.getResources().getString("chart.healthCheck.timeSeriesEventName.weakness"), lookbackPeriod);
            break;
        case WEAKNESS_WITHOUT_COUNTING:
            timeSeriesName = MessageFormat.format(
                    this.getResources().getString("chart.healthCheck.timeSeriesEventName.weaknessWithoutCounting"),
                    lookbackPeriod);
            break;
        default:
            timeSeriesName = this.getResources().getString("chart.healthCheck.timeSeriesEventName");
            break;
        }

        return timeSeriesName;
    }
}
