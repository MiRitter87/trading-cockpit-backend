package backend.controller.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ValueMarker;
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
import backend.controller.scan.PerformanceCalculator;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Provides methods that are collectively used for chart generation.
 *
 * @author Michael
 */
public abstract class ChartController {
    /**
     * The performance threshold that defines a Distribution Day.
     */
    private static final float DD_PERCENT_THRESHOLD = (float) -0.2;

    /**
     * DAO to access Instrument data.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private QuotationDAO quotationDAO;

    /**
     * DAO to access List data.
     */
    private ListDAO listDAO;

    /**
     * Performance calculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Initializes the ChartController.
     */
    public ChartController() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.listDAO = DAOManager.getInstance().getListDAO();

        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * @return the instrumentDAO
     */
    public InstrumentDAO getInstrumentDAO() {
        return instrumentDAO;
    }

    /**
     * @return the quotationDAO
     */
    public QuotationDAO getQuotationDAO() {
        return quotationDAO;
    }

    /**
     * @return the listDAO
     */
    public ListDAO getListDAO() {
        return listDAO;
    }

    /**
     * @return the performanceCalculator
     */
    public PerformanceCalculator getPerformanceCalculator() {
        return performanceCalculator;
    }

    /**
     * @return the resources
     */
    public ResourceBundle getResources() {
        return resources;
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

        return new DefaultHighLowDataset(this.resources.getString("chart.general.timeSeriesPriceName"), date, high, low,
                open, close, volume);
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
        CandlestickRenderer candlestickRenderer = new CandlestickRenderer();

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
        TimeSeries volumeTimeSeries = new TimeSeries(this.resources.getString("chart.general.timeSeriesVolumeName"));

        for (Quotation tempQuotation : quotationsSortedByDate) {
            volumeTimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getVolume());
        }

        dataset.addSeries(volumeTimeSeries);

        return dataset;

    }

    /**
     * Checks if the day of the current Quotation constitutes a Distribution Day.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if day of current Quotation is Distribution Day; false, if not.
     */
    protected boolean isDistributionDay(final Quotation currentQuotation, final Quotation previousQuotation) {
        float performance;

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance < DD_PERCENT_THRESHOLD && (currentQuotation.getVolume() > previousQuotation.getVolume())) {
            return true;
        }

        return false;
    }

    /**
     * Adds a horizontal line to the given XYPlot.
     *
     * @param plot                   The XYPlot to which the horizontal line is added.
     * @param horizontalLinePosition The value on the y-axis at which the horizontal line is being drawn.
     */
    protected void addHorizontalLine(final XYPlot plot, final double horizontalLinePosition) {
        ValueMarker valueMarker = new ValueMarker(horizontalLinePosition, Color.BLACK, new BasicStroke(2), null, null,
                1.0f);
        plot.addRangeMarker(valueMarker);
    }

    /**
     * Adds the date of the most recent Quotation of the Instrument to the given plot.
     *
     * @param plot       The XYPlot to which the date is added.
     * @param instrument The instrument containing the quotations.
     */
    protected void addMostRecentDate(final XYPlot plot, final Instrument instrument) {
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
     * Returns the Instrument with its quotations based on the given Instrument ID.
     *
     * @param instrumentId The ID of the Instrument.
     * @return The Instrument with its quotations.
     * @throws NoQuotationsExistException No Quotations exist.
     * @throws Exception                  Error during data retrieval.
     */
    protected Instrument getInstrumentWithQuotations(final Integer instrumentId)
            throws NoQuotationsExistException, Exception {
        Instrument instrument;

        instrument = this.instrumentDAO.getInstrument(instrumentId);
        instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));

        if (instrument.getQuotations().size() == 0) {
            throw new NoQuotationsExistException();
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
    protected List<Date> getTimelineExclusionDates(final Instrument instrument) {
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
     * Applies a custom theme to the given chart.
     *
     * @param chart The chart.
     */
    protected void applyBackgroundTheme(final JFreeChart chart) {
        XYPlot chartPlot;

        chartPlot = chart.getXYPlot();
        chartPlot.setBackgroundPaint(Color.WHITE);
        chartPlot.setDomainGridlinePaint(Color.BLACK);
        chartPlot.setRangeGridlinePaint(Color.BLACK);
    }

    /**
     * Determines the highest average volume within the trading history of the given Instrument.
     *
     * @param instrument The Instrument.
     * @return The highest average volume of the trading history.
     */
    protected long getHighestAverageVolume(final Instrument instrument) {
        long highestAverageVolume = 0;
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();

        for (Quotation tempQuotation : quotationsSortedByDate) {
            if (tempQuotation.getIndicator().getSma30Volume() > highestAverageVolume) {
                highestAverageVolume = tempQuotation.getIndicator().getSma30Volume();
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
}
