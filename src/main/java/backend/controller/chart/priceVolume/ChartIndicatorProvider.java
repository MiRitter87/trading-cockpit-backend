package backend.controller.chart.priceVolume;

import java.awt.Color;
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

import backend.calculator.BollingerCalculator;
import backend.calculator.RatioCalculator;
import backend.calculator.StochasticCalculator;
import backend.controller.scan.IndicatorCalculationController;
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
     * The Bollinger BandWidth period in days.
     */
    private static final int BOLLINGER_BAND_WIDTH_PERIOD = 10;

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
        rsLineRenderer.setSeriesPaint(0, Color.BLACK);

        // Do not begin y-Axis at zero. Use lowest value of provided dataset instead.
        valueAxis.setAutoRangeIncludesZero(false);

        rsLinePlot = new XYPlot(dataset, timeAxis, valueAxis, null);
        rsLinePlot.setRenderer(rsLineRenderer);
        rsLinePlot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        this.addEma21(rsLinePlot, rsInstrumentId, instrument);

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
        List<Quotation> ratioQuotations = this.getRatioQuotations(rsInstrumentId, instrument);
        TimeSeries timeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesRsLineName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);

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
     * @param instrument      The Instrument for which the Bollinger BandWidth is being calculated.
     * @param timeAxis        The x-Axis (time).
     * @param requestedValues The number of requested Bollinger BandWidth values.
     * @return A XYPlot depicting the Bollinger BandWidth.
     * @throws Exception Failed to create Bollinger BandWidth plot.
     */
    public XYPlot getBollingerBandWidthPlot(final Instrument instrument, final ValueAxis timeAxis,
            final int requestedValues) throws Exception {
        XYDataset dataset;
        XYPlot bbwPlot;
        NumberAxis valueAxis = new NumberAxis("");
        XYLineAndShapeRenderer bbwRenderer = new XYLineAndShapeRenderer(true, false);
        QuotationArray quotations = new QuotationArray(instrument.getQuotationsSortedByDate());
        final int percentBbwThreshold = 25;
        float threshold;

        dataset = this.getBollingerBandWidthDataset(instrument, requestedValues);
        bbwRenderer.setSeriesPaint(0, Color.BLACK);

        // Do not begin y-Axis at zero. Use lowest value of provided dataset instead.
        valueAxis.setAutoRangeIncludesZero(false);

        bbwPlot = new XYPlot(dataset, timeAxis, valueAxis, null);
        bbwPlot.setRenderer(bbwRenderer);
        bbwPlot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        threshold = this.bollingerCalculator.getBollingerBandWidthThreshold(BOLLINGER_BAND_WIDTH_PERIOD, 2,
                percentBbwThreshold, instrument.getQuotationsSortedByDate().get(0), quotations);
        this.addBollingerBandWidthTriggerLine(bbwPlot, threshold);

        return bbwPlot;
    }

    /**
     * Constructs a XYDataset for the Bollinger BandWidth.
     *
     * @param instrument      The Instrument for which the Price Volume chart is displayed.
     * @param requestedValues The number of requested Bollinger BandWidth values.
     * @return A dataset building the values of the Bollinger BandWidth.
     * @throws Exception Failed to construct dataset of Bollinger BandWidth.
     */
    private XYDataset getBollingerBandWidthDataset(final Instrument instrument, final int requestedValues)
            throws Exception {
        TimeSeries timeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesBbwName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);
        QuotationArray quotationArray = instrument.getQuotationArray();
        float bollingerBandWidth;
        int addedValues = 0;

        quotationArray.sortQuotationsByDate();

        for (Quotation quotation : quotationArray.getQuotations()) {
            bollingerBandWidth = this.bollingerCalculator.getBollingerBandWidth(BOLLINGER_BAND_WIDTH_PERIOD, 2,
                    quotation, quotationArray);

            if (bollingerBandWidth > 0) {
                timeSeries.add(new Day(quotation.getDate()), bollingerBandWidth);
                addedValues++;
            }

            if (addedValues == requestedValues) {
                break;
            }
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
        slowStochasticRenderer.setSeriesPaint(0, Color.BLACK);

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

            if (slowStochastic > 0) {
                timeSeries.add(new Day(quotation.getDate()), slowStochastic);
            }
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
     * @param triggerLineValue       The value at which a horizontal trigger line is being drawn.
     */
    private void addBollingerBandWidthTriggerLine(final XYPlot bollingerBandWidthPlot, final double triggerLineValue) {
        ValueMarker valueMarker;

        // Add value marker depicting a trigger line.
        valueMarker = new ValueMarker(triggerLineValue);
        bollingerBandWidthPlot.addRangeMarker(valueMarker);
    }

    /**
     * Adds the EMA(21) to the RS-Line.
     *
     * @param rsLinePlot     The RS-Line plot.
     * @param rsInstrumentId The ID of the Instrument building the ratio divisor.
     * @param instrument     The Instrument for which the RS-Line is being calculated.
     * @throws Exception Failed to add EMA(21).
     */
    private void addEma21(final XYPlot rsLinePlot, final Integer rsInstrumentId, final Instrument instrument)
            throws Exception {
        ChartOverlayProvider overlayProvider = new ChartOverlayProvider();
        IndicatorCalculationController indicatorCalculator = new IndicatorCalculationController();
        Instrument rsLineInstrument = new Instrument();
        QuotationArray rsLineQuotations = new QuotationArray(this.getRatioQuotations(rsInstrumentId, instrument));
        Quotation quotation;

        // Initialize new Instrument with RS-line values as closing prices.
        rsLineQuotations.sortQuotationsByDate();
        rsLineInstrument.setQuotations(rsLineQuotations.getQuotations());

        // Calculate the EMA(21) for the Instrument. This later becomes the EMA(21) of the RS-line.
        for (int i = 0; i < rsLineQuotations.getQuotations().size(); i++) {
            quotation = rsLineQuotations.getQuotations().get(i);

            // Calculate moving averages.
            indicatorCalculator.calculateIndicators(rsLineInstrument, quotation, false);
        }

        // Add EMA(21) overlay to RS-line plot
        overlayProvider.addEma21(rsLineInstrument, rsLinePlot);
    }

    /**
     * Determines a List of quotations that build the ratio between the given Instrument as the dividend and Instrument
     * of the given rsInstrumentId.
     *
     * @param rsInstrumentId The ID of the ratio divisor.
     * @param instrument     The dividend instrument.
     * @return A List of ratio prices.
     * @throws Exception Ratio calculation failed.
     */
    private List<Quotation> getRatioQuotations(final Integer rsInstrumentId, final Instrument instrument)
            throws Exception {
        List<Quotation> ratioQuotations;
        RatioCalculator ratioCalculator = new RatioCalculator();
        Instrument divisorInstrument = new Instrument();

        divisorInstrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(rsInstrumentId));
        ratioQuotations = ratioCalculator.getRatios(instrument, divisorInstrument);

        return ratioQuotations;
    }
}
