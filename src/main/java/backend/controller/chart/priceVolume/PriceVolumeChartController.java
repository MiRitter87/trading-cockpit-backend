package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;

import backend.controller.NoQuotationsExistException;
import backend.controller.chart.ChartController;
import backend.controller.scan.PerformanceCalculator;
import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.webservice.Indicator;

/**
 * Controller for the creation of a chart displaying an Instrument with price and volume. The chart can be configured to
 * add additional overlays and plots.
 *
 * @author Michael
 */
public class PriceVolumeChartController extends ChartController {
    /**
     * The factor used to calculate the performance threshold that defines a Distribution Day.
     */
    private static final float DD_THRESHOLD_FACTOR = (float) 0.274;

    /**
     * DAO to access chart object data.
     */
    private ChartObjectDAO chartObjectDAO;

    /**
     * Provider of indicator plots.
     */
    private ChartIndicatorProvider chartIndicatorProvider;

    /**
     * Provider of chart overlays.
     */
    private ChartOverlayProvider chartOverlayProvider;

    /**
     * Performance calculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Initializes the PriceVolumeChartController.
     */
    public PriceVolumeChartController() {
        this.chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();

        this.chartIndicatorProvider = new ChartIndicatorProvider(this.getQuotationDAO());
        this.chartOverlayProvider = new ChartOverlayProvider();
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * @return the chartOverlayProvider
     */
    public ChartOverlayProvider getChartOverlayProvider() {
        return chartOverlayProvider;
    }

    /**
     * @return the performanceCalculator
     */
    public PerformanceCalculator getPerformanceCalculator() {
        return performanceCalculator;
    }

    /**
     * @return the chartIndicatorProvider
     */
    public ChartIndicatorProvider getChartIndicatorProvider() {
        return chartIndicatorProvider;
    }

    /**
     * Gets a chart of an Instrument with volume information.
     *
     * @param instrumentId    The ID of the Instrument used for chart creation.
     * @param overlays        The requested chart overlays.
     * @param withVolume      Show volume information.
     * @param withSma30Volume Show SMA(30) of volume.
     * @param indicator       The Indicator that is being displayed above the chart.
     * @param rsInstrumentId  The ID of the Instrument used to build the RS line (only used if type of Indicator is
     *                        RS_LINE).
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getPriceVolumeChart(final Integer instrumentId, final List<String> overlays,
            final boolean withVolume, final boolean withSma30Volume, final Indicator indicator,
            final Integer rsInstrumentId) throws NoQuotationsExistException, Exception {

        Instrument instrument = this.getInstrumentWithQuotations(instrumentId, TRADING_DAYS_PER_YEAR);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = null;
        XYPlot indicatorSubplot = null;
        final int candleStickPlotWeight = 4;

        if (withVolume) {
            volumeSubplot = this.getVolumePlot(instrument, dateAxis);
            this.chartOverlayProvider.addMovingAverageVolume(instrument, withSma30Volume, volumeSubplot);
            this.clipVolumeAt2TimesAverage(volumeSubplot, instrument);
        }

        indicatorSubplot = this.getIndicatorPlot(indicator, rsInstrumentId, instrument, dateAxis);

        this.addMovingAveragesPrice(instrument, overlays, candleStickSubplot);
        this.addHorizontalLines(instrument, candleStickSubplot);

        // Build combined plot based on subplots.
        combinedPlot.setDomainAxis(dateAxis);

        if (indicatorSubplot != null) {
            combinedPlot.add(indicatorSubplot, 1); // Indicator Plot takes 1 vertical size unit.
        }

        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.

        if (withVolume) {
            combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        }

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }

    /**
     * Returns the Instrument with its quotations based on the given Instrument ID.
     *
     * @param instrumentId  The ID of the Instrument.
     * @param maxQuotations The maximum number of quotations returned with the Instrument.
     * @return The Instrument with its quotations.
     * @throws NoQuotationsExistException No Quotations exist.
     * @throws Exception                  Error during data retrieval.
     */
    protected Instrument getInstrumentWithQuotations(final Integer instrumentId, final Integer maxQuotations)
            throws NoQuotationsExistException, Exception {
        Instrument instrument;
        List<Quotation> requestedNumberOfQuotations;

        instrument = this.getInstrumentDAO().getInstrument(instrumentId);
        instrument.setQuotations(this.getQuotationDAO().getQuotationsOfInstrument(instrumentId));

        if (instrument.getQuotations().size() == 0) {
            throw new NoQuotationsExistException();
        }

        requestedNumberOfQuotations = instrument.getQuotationsSortedByDate();

        if (requestedNumberOfQuotations.size() > maxQuotations) {
            requestedNumberOfQuotations = requestedNumberOfQuotations.subList(0, maxQuotations);
            instrument.setQuotations(requestedNumberOfQuotations);
        }

        return instrument;
    }

    /**
     * Gets the DateAxis for the given Instrument.
     *
     * @param instrument The Instrument.
     * @return The DateAxis.
     */
    protected DateAxis getDateAxis(final Instrument instrument) {
        DateAxis dateAxis = new DateAxis();
        SegmentedTimeline timeline = SegmentedTimeline.newMondayThroughFridayTimeline();
        List<Date> exclusionDates = this.getTimelineExclusionDates(instrument);

        timeline.setAdjustForDaylightSaving(true);

        for (Date exclusionDate : exclusionDates) {
            timeline.addException(exclusionDate);
        }

        dateAxis.setTimeline(timeline);

        return dateAxis;
    }

    /**
     * Determines all weekdays on which no trading took place.
     *
     * @param instrument The Instrument.
     * @return A list of dates.
     */
    private List<Date> getTimelineExclusionDates(final Instrument instrument) {
        List<Date> exclusionDates = new ArrayList<>();
        QuotationArray quotations = new QuotationArray(instrument.getQuotationsSortedByDate());
        Date oldestDate = quotations.getQuotations().get(quotations.getQuotations().size() - 1).getDate();
        Date newestDate = quotations.getQuotations().get(0).getDate();
        LocalDate startDate = oldestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = newestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (LocalDate currentDate = startDate; currentDate.isBefore(endDate); currentDate = currentDate.plusDays(1)) {
            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            if (!quotations.isQuotationOfDateExisting(
                    Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                exclusionDates.add(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        }

        return exclusionDates;
    }

    /**
     * Builds a plot to display prices of an Instrument using a Candlestick chart.
     *
     * @param instrument The Instrument.
     * @param timeAxis   The x-Axis (time).
     * @return A XYPlot depicting prices using a Candlestick chart.
     * @throws Exception Plot generation failed.
     */
    protected XYPlot getCandlestickPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
        OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
        LogAxis valueAxisCandlestick = new LogAxis("");
        NumberFormat logAxisNumberFormat = NumberFormat.getInstance();
        final float candleWidth = 3.89f;
        CandlestickRenderer candlestickRenderer = new CandlestickRenderer(candleWidth);

        // Customize LogarithmicAxis for price.
        logAxisNumberFormat.setMaximumFractionDigits(2);
        valueAxisCandlestick.setNumberFormatOverride(logAxisNumberFormat);

        candlestickRenderer.setDrawVolume(false);

        // Candles having a black border.
        candlestickRenderer.setSeriesPaint(0, Color.BLACK);

        XYPlot candleStickSubplot = new XYPlot(instrumentPriceData, timeAxis, valueAxisCandlestick, null);
        candleStickSubplot.setRenderer(candlestickRenderer);
        candleStickSubplot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        this.addMostRecentDate(candleStickSubplot, instrument);

        return candleStickSubplot;
    }

    /**
     * Gets a dataset of OHLC prices for the given Instrument.
     *
     * @param instrument The Instrument.
     * @return A dataset of OHLC prices.
     * @throws Exception Dataset creation failed
     */
    protected OHLCDataset getInstrumentOHLCDataset(final Instrument instrument) throws Exception {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();

        int numberOfQuotations = instrument.getQuotations().size();
        int quotationIndex;

        Date[] date = new Date[numberOfQuotations];
        double[] open = new double[numberOfQuotations];
        double[] high = new double[numberOfQuotations];
        double[] low = new double[numberOfQuotations];
        double[] close = new double[numberOfQuotations];
        double[] volume = new double[numberOfQuotations];

        for (Quotation tempQuotation : quotationsSortedByDate) {
            quotationIndex = quotationsSortedByDate.indexOf(tempQuotation);

            date[quotationIndex] = tempQuotation.getDate();
            open[quotationIndex] = tempQuotation.getOpen().doubleValue();
            high[quotationIndex] = tempQuotation.getHigh().doubleValue();
            low[quotationIndex] = tempQuotation.getLow().doubleValue();
            close[quotationIndex] = tempQuotation.getClose().doubleValue();
            volume[quotationIndex] = tempQuotation.getVolume();
        }

        return new DefaultHighLowDataset(this.getResources().getString("chart.general.timeSeriesPriceName"), date, high,
                low, open, close, volume);
    }

    /**
     * Adds the date of the most recent Quotation of the Instrument to the given plot.
     *
     * @param plot       The XYPlot to which the date is added.
     * @param instrument The instrument containing the quotations.
     */
    private void addMostRecentDate(final XYPlot plot, final Instrument instrument) {
        QuotationArray quotationArray = new QuotationArray(instrument.getQuotationsSortedByDate());
        String datePattern = "dd.MM.yyyy";
        String formattedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        Quotation oldestQuotation;
        Quotation newestQuotation;
        double priceHigh;
        XYTextAnnotation dateAnnotation;

        if (instrument.getQuotations().size() == 0) {
            return;
        }

        newestQuotation = quotationArray.getQuotations().get(0);
        formattedDate = dateFormat.format(newestQuotation.getDate());

        oldestQuotation = quotationArray.getQuotations().get(quotationArray.getQuotations().size() - 1);
        priceHigh = quotationArray.getPriceHigh().doubleValue();

        dateAnnotation = new XYTextAnnotation(formattedDate, oldestQuotation.getDate().getTime(), priceHigh);
        plot.addAnnotation(dateAnnotation);
    }

    /**
     * Builds a plot to display volume data of an Instrument.
     *
     * @param instrument The Instrument.
     * @param timeAxis   The x-Axis (time).
     * @return A XYPlot depicting volume data.
     * @throws Exception Plot generation failed.
     */
    protected XYPlot getVolumePlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
        IntervalXYDataset volumeData = this.getInstrumentVolumeDataset(instrument);
        XYBarRenderer volumeRenderer = new XYBarRenderer();
        NumberAxis volumeAxis = new NumberAxis();

        volumeRenderer.setShadowVisible(false); // Volume bars without shadow.
        volumeRenderer.setSeriesPaint(0, Color.BLUE); // Blue volume bars.

        XYPlot volumeSubplot = new XYPlot(volumeData, timeAxis, volumeAxis, volumeRenderer);
        volumeSubplot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return volumeSubplot;
    }

    /**
     * Gets a dataset of volume information for the given Instrument.
     *
     * @param instrument The Instrument.
     * @return A dataset of volume information.
     * @throws Exception Dataset creation failed
     */
    private IntervalXYDataset getInstrumentVolumeDataset(final Instrument instrument) throws Exception {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries volumeTimeSeries = new TimeSeries(
                this.getResources().getString("chart.general.timeSeriesVolumeName"));

        for (Quotation tempQuotation : quotationsSortedByDate) {
            volumeTimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getVolume());
        }

        dataset.addSeries(volumeTimeSeries);

        return dataset;

    }

    /**
     * Determines the highest average volume within the trading history of the given Instrument.
     *
     * @param instrument The Instrument.
     * @return The highest average volume of the trading history.
     */
    private long getHighestAverageVolume(final Instrument instrument) {
        long highestAverageVolume = 0;
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        MovingAverageData maData;

        for (Quotation tempQuotation : quotationsSortedByDate) {
            maData = tempQuotation.getMovingAverageData();

            if (maData == null) {
                continue;
            }

            if (maData.getSma30Volume() > highestAverageVolume) {
                highestAverageVolume = maData.getSma30Volume();
            }
        }

        return highestAverageVolume;
    }

    /**
     * Clips the volume bars of the volume plot at two times of the maximum average volume. This prevents a volume
     * scale, where large "skyscraper" volume bars dominate the picture and the user is not able to properly read the
     * average volume.
     *
     * @param volumeSubplot The sub plot of the chart showing the volume.
     * @param instrument    The Instrument containing the trading history.
     */
    protected void clipVolumeAt2TimesAverage(final XYPlot volumeSubplot, final Instrument instrument) {
        double upperVolumeAxisRange = this.getHighestAverageVolume(instrument) * 2;
        NumberAxis volumeAxis = (NumberAxis) volumeSubplot.getRangeAxis();

        if (upperVolumeAxisRange > 0) {
            volumeAxis.setRange(0, upperVolumeAxisRange);
        }
    }

    /**
     * Adds moving averages of the price to the chart.
     *
     * @param instrument         The Instrument whose price and volume data are displayed.
     * @param overlays           The requested chart overlays.
     * @param candleStickSubplot The Plot to which moving averages are added.
     */
    protected void addMovingAveragesPrice(final Instrument instrument, final List<String> overlays,
            final XYPlot candleStickSubplot) {

        if (overlays == null || overlays.isEmpty()) {
            return;
        }

        if (overlays.contains(ChartOverlay.EMA_21.toString())) {
            this.chartOverlayProvider.addEma21(instrument, candleStickSubplot);
        }

        if (overlays.contains(ChartOverlay.SMA_50.toString())) {
            this.chartOverlayProvider.addSma50(instrument, candleStickSubplot);
        }

        if (overlays.contains(ChartOverlay.SMA_150.toString())) {
            this.chartOverlayProvider.addSma150(instrument, candleStickSubplot);
        }

        if (overlays.contains(ChartOverlay.SMA_200.toString())) {
            this.chartOverlayProvider.addSma200(instrument, candleStickSubplot);
        }
    }

    /**
     * Builds a plot to display Indicator data of an Instrument.
     *
     * @param indicator      The requested Indicator.
     * @param rsInstrumentId The ID of the Instrument used to calculate the RS line.
     * @param instrument     The Instrument for which the indicator is being calculated.
     * @param timeAxis       The x-Axis (time).
     * @return A XYPlot depicting Indicator data.
     * @throws Exception Failed to construct indicator plot.
     */
    private XYPlot getIndicatorPlot(final Indicator indicator, final Integer rsInstrumentId,
            final Instrument instrument, final ValueAxis timeAxis) throws Exception {

        switch (indicator) {
        case RS_LINE:
            return this.chartIndicatorProvider.getRsLinePlot(rsInstrumentId, instrument, timeAxis);
        case BBW:
            return this.chartIndicatorProvider.getBollingerBandWidthPlot(instrument, timeAxis, TRADING_DAYS_PER_YEAR);
        case SLOW_STOCHASTIC:
            return this.chartIndicatorProvider.getSlowStochasticPlot(instrument, timeAxis);
        case NONE:
            return null;
        default:
            return null;
        }
    }

    /**
     * Adds horizontal lines to the candlestick subplot of the given Instrument.
     *
     * @param instrument         The Instrument.
     * @param candleStickSubplot The candlestick plot of the Instrument.
     * @throws Exception Failed to add horizontal lines.
     */
    private void addHorizontalLines(final Instrument instrument, final XYPlot candleStickSubplot) throws Exception {
        List<HorizontalLine> horizontalLines = this.chartObjectDAO.getHorizontalLines(instrument.getId());

        for (HorizontalLine horizontalLine : horizontalLines) {
            this.addHorizontalLine(candleStickSubplot, horizontalLine.getPrice().doubleValue(), Color.BLACK);
        }
    }

    /**
     * Checks if the day of the current Quotation constitutes a Distribution Day.
     *
     * @param currentQuotation       The current Quotation.
     * @param previousQuotation      The previous Quotation.
     * @param quotationsSortedByDate A List of Quotations sorted by Date.
     * @return true, if day of current Quotation is Distribution Day; false, if not.
     */
    public boolean isDistributionDay(final Quotation currentQuotation, final Quotation previousQuotation,
            final List<Quotation> quotationsSortedByDate) {

        float performance;
        float averagePerformance;
        float performanceThreshold;
        final int minDaysForAveragePerformance = 50;
        final int maxDaysForAveragePerformance = 200;

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);
        averagePerformance = this.performanceCalculator.getAveragePerformanceOfDownDays(currentQuotation,
                new QuotationArray(quotationsSortedByDate), minDaysForAveragePerformance, maxDaysForAveragePerformance);

        if (averagePerformance == 0) {
            return false;
        }

        performanceThreshold = averagePerformance * DD_THRESHOLD_FACTOR;

        if (performance <= performanceThreshold && (currentQuotation.getVolume() > previousQuotation.getVolume())) {
            return true;
        }

        return false;
    }
}
