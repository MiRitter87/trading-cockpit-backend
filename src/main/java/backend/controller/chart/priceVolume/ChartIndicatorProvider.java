package backend.controller.chart.priceVolume;

import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.controller.RatioCalculationController;
import backend.controller.scan.BollingerCalculator;
import backend.controller.scan.StochasticCalculator;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Builds plots that are used as indicators of an Instrument in a Price Volume chart.
 *
 * @author Michael
 */
public class ChartIndicatorProvider {
    /**
     * Bollinger calculator.
     */
    private BollingerCalculator bollingerCalculator;

    /**
     * Stochastic calculator.
     */
    private StochasticCalculator stochasticCalculator;

    /**
     * DAO to access Quotation data.
     */
    private QuotationDAO quotationDAO;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Initializes the ChartIndicatorProvider.
     *
     * @param quotationDAO DAO to access Quotation data.
     */
    public ChartIndicatorProvider(final QuotationDAO quotationDAO) {
        this.quotationDAO = quotationDAO;

        this.bollingerCalculator = new BollingerCalculator();
        this.stochasticCalculator = new StochasticCalculator();
    }

    /**
     * Builds a plot to display the RS line of an Instrument.
     *
     * @param rsInstrumentId The ID of the Instrument used to calculate the RS line (the divisor of the price ratio).
     * @param instrument     The Instrument for which the RS line is being calculated.
     * @param timeAxis       The x-Axis (time).
     * @return A XYPlot depicting the RS line.
     * @throws Exception Failed to create RS line plot.
     */
    public XYPlot getRsLinePlot(final Integer rsInstrumentId, final Instrument instrument, final ValueAxis timeAxis)
            throws Exception {
        XYDataset dataset;
        XYPlot rsLinePlot;
        NumberAxis valueAxis = new NumberAxis("");
        XYLineAndShapeRenderer rsLineRenderer = new XYLineAndShapeRenderer(true, false);

        dataset = this.getRsLineDataset(rsInstrumentId, instrument);

        // Do not begin y-Axis at zero. Use lowest value of provided dataset instead.
        valueAxis.setAutoRangeIncludesZero(false);

        rsLinePlot = new XYPlot(dataset, timeAxis, valueAxis, null);
        rsLinePlot.setRenderer(rsLineRenderer);
        rsLinePlot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return rsLinePlot;
    }

    /**
     * Constructs a XYDataset for the RS line.
     *
     * @param rsInstrumentId The ID of the Instrument for RS determination.
     * @param instrument     The Instrument for which the Price Volume chart is displayed.
     * @return A dataset building the values of the RS line (price ratio).
     * @throws Exception Failed to construct dataset of RS line.
     */
    private XYDataset getRsLineDataset(final Integer rsInstrumentId, final Instrument instrument) throws Exception {
        List<Quotation> ratioQuotations;
        RatioCalculationController ratioCalculationController = new RatioCalculationController();
        Instrument divisorInstrument = new Instrument();
        TimeSeries timeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesRsLineName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);

        divisorInstrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(rsInstrumentId));
        ratioQuotations = ratioCalculationController.getRatios(instrument, divisorInstrument);

        for (Quotation quotation : ratioQuotations) {
            timeSeries.add(new Day(quotation.getDate()), quotation.getClose());
        }

        timeSeriesCollection.addSeries(timeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesCollection;
    }

    /**
     * Builds a plot to display the Bollinger BandWidth of an Instrument.
     *
     * @param instrument The Instrument for which the Bollinger BandWidth is being calculated.
     * @param timeAxis   The x-Axis (time).
     * @return A XYPlot depicting the Bollinger BandWidth.
     * @throws Exception Failed to create Bollinger BandWidth plot.
     */
    public XYPlot getBollingerBandWidthPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
        XYDataset dataset;
        XYPlot bbwPlot;
        NumberAxis valueAxis = new NumberAxis("");
        XYLineAndShapeRenderer bbwRenderer = new XYLineAndShapeRenderer(true, false);

        dataset = this.getBollingerBandWidthDataset(instrument);

        // Do not begin y-Axis at zero. Use lowest value of provided dataset instead.
        valueAxis.setAutoRangeIncludesZero(false);

        bbwPlot = new XYPlot(dataset, timeAxis, valueAxis, null);
        bbwPlot.setRenderer(bbwRenderer);
        bbwPlot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        this.addBollingerBandWidthTriggerLine(bbwPlot);

        return bbwPlot;
    }

    /**
     * Constructs a XYDataset for the Bollinger BandWidth.
     *
     * @param instrument The Instrument for which the Price Volume chart is displayed.
     * @return A dataset building the values of the Bollinger BandWidth.
     * @throws Exception Failed to construct dataset of Bollinger BandWidth.
     */
    private XYDataset getBollingerBandWidthDataset(final Instrument instrument) throws Exception {
        TimeSeries timeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesBbwName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);
        QuotationArray quotationArray = instrument.getQuotationArray();
        float bollingerBandWidth;
        final int bbwPeriodDays = 10;

        quotationArray.sortQuotationsByDate();

        for (Quotation quotation : quotationArray.getQuotations()) {
            bollingerBandWidth = this.bollingerCalculator.getBollingerBandWidth(bbwPeriodDays, 2, quotation,
                    quotationArray);
            timeSeries.add(new Day(quotation.getDate()), bollingerBandWidth);
        }

        timeSeriesCollection.addSeries(timeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesCollection;
    }

    /**
     * Builds a plot to display the Slow Stochastic of an Instrument.
     *
     * @param instrument The Instrument for which the Slow Stochastic is being calculated.
     * @param timeAxis   The x-Axis (time).
     * @return A XYPlot depicting the Slow Stochastic.
     * @throws Exception Failed to create Slow Stochastic plot.
     */
    public XYPlot getSlowStochasticPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
        XYDataset dataset;
        XYPlot slowStochasticPlot;
        NumberAxis valueAxis = new NumberAxis("");
        XYLineAndShapeRenderer slowStochasticRenderer = new XYLineAndShapeRenderer(true, false);
        final int slowStochasticPeriodDays = 14;
        final int smoothingPeriodDays = 3;

        dataset = this.getSlowStochasticDataset(instrument, slowStochasticPeriodDays, smoothingPeriodDays);

        slowStochasticPlot = new XYPlot(dataset, timeAxis, valueAxis, null);
        slowStochasticPlot.setRenderer(slowStochasticRenderer);
        slowStochasticPlot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        this.addSlowStochasticTriggerLines(slowStochasticPlot);

        return slowStochasticPlot;
    }

    /**
     * Constructs a XYDataset for the Slow Stochastic.
     *
     * @param instrument      The Instrument for which the Price Volume chart is displayed.
     * @param daysPeriod      The number of days used for calculation.
     * @param smoothingPeriod The number of days used for smoothing of Slow Stochastic.
     * @return A dataset building the values of the Slow Stochastic.
     * @throws Exception Failed to construct dataset of Slow Stochastic.
     */
    private XYDataset getSlowStochasticDataset(final Instrument instrument, final int daysPeriod,
            final int smoothingPeriod) throws Exception {
        TimeSeries timeSeries = new TimeSeries(
                this.resources.getString("chart.priceVolume.timeSeriesSlowStochasticName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);
        QuotationArray quotationArray = instrument.getQuotationArray();
        float slowStochastic;

        quotationArray.sortQuotationsByDate();

        for (Quotation quotation : quotationArray.getQuotations()) {
            slowStochastic = this.stochasticCalculator.getSlowStochastic(daysPeriod, smoothingPeriod, quotation,
                    quotationArray);
            timeSeries.add(new Day(quotation.getDate()), slowStochastic);
        }

        timeSeriesCollection.addSeries(timeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesCollection;
    }

    /**
     * Adds horizontal trigger lines to the Slow Stochastic plot.
     *
     * @param slowStochasticPlot The Slow Stochastic plot.
     */
    private void addSlowStochasticTriggerLines(final XYPlot slowStochasticPlot) {
        ValueMarker valueMarker;
        final double lowerTriggerLine = 15;
        final double middleLine = 50;
        final double upperTriggerLine = 85;

        // Add value marker at 15, 50 and 85.
        valueMarker = new ValueMarker(lowerTriggerLine);
        slowStochasticPlot.addRangeMarker(valueMarker);

        valueMarker = new ValueMarker(middleLine);
        slowStochasticPlot.addRangeMarker(valueMarker);

        valueMarker = new ValueMarker(upperTriggerLine);
        slowStochasticPlot.addRangeMarker(valueMarker);
    }

    /**
     * Adds a horizontal trigger line to the Bollinger BandWidth plot.
     *
     * @param bollingerBandWidthPlot The Bollinger BandWidth plot.
     */
    private void addBollingerBandWidthTriggerLine(final XYPlot bollingerBandWidthPlot) {
        ValueMarker valueMarker;
        final double triggerLine = 10;

        // Add value marker at 10.
        valueMarker = new ValueMarker(triggerLine);
        bollingerBandWidthPlot.addRangeMarker(valueMarker);
    }
}
