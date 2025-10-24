package backend.controller.chart.statistic;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.controller.chart.ChartController;
import backend.controller.instrumentCheck.PatternControllerHelper;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.tools.DateTools;

/**
 * Controller for the creation of a chart displaying the Ritter Pattern Indicator.
 *
 * @author Michael
 */
public class RitterPatternIndicatorChartController extends ChartController {
    /**
     * The number of instruments per date the pattern indicator is based on.
     */
    private TreeMap<Date, Integer> numberOfInstrumentsPerDate;

    /**
     * The InstrumentType of the instruments depicted in the chart.
     */
    private InstrumentType instrumentType;

    /**
     * The list used as basis for the chart.
     */
    private backend.model.list.List list;

    /**
     * All instruments with their quotations that are used for chart creation.
     */
    private List<Instrument> instruments;

    /**
     * Initializes the RitterPatternIndicatorChartController.
     *
     * @param listId The ID of the list defining the instruments used for chart creation.
     * @throws Exception Failed to initialize data.
     */
    public RitterPatternIndicatorChartController(final Integer listId) throws Exception {
        this.instrumentType = InstrumentType.STOCK;

        if (listId != null) {
            this.list = this.getListDAO().getList(listId);
        }

        this.initializeInstrumentsWithQuotations(TRADING_DAYS_PER_YEAR);
        this.initializeNumberOfInstrumentsPerDate();
    }

    /**
     * Gets a chart of the Ritter Pattern Indicator.
     *
     * @return The chart.
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getRitterPatternIndicatorChart() throws Exception {
        TreeMap<Date, Integer> patternIndicatorValues;
        XYDataset dataset;

        patternIndicatorValues = this.getPatternIndicatorValues();
        dataset = this.getRitterPatternIndicatorDataset(patternIndicatorValues);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                this.getResources().getString("chart.ritterPatternIndicator.titleName"), null, null, dataset, true,
                true, false);

        this.addHorizontalLine(chart.getXYPlot(), 0, Color.BLACK);
        this.applyStatisticalTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Initializes all instruments with their quotations based on the given instrumentType or listId.
     *
     * @param maxQuotations The maximum number of quotations returned with each Instrument.
     * @throws Exception Instrument or Quotation retrieval failed.
     */
    private void initializeInstrumentsWithQuotations(final Integer maxQuotations) throws Exception {
        List<Quotation> requestedNumberOfQuotations;

        this.instruments = new ArrayList<>();

        // Initialize instruments.
        if (this.list != null) {
            this.instruments.addAll(this.list.getInstruments());
        } else {
            this.instruments.addAll(this.getInstrumentDAO().getInstruments(this.instrumentType));
        }

        // Initialize quotations of each Instrument.
        for (Instrument instrument : this.instruments) {
            instrument.setQuotations(this.getQuotationDAO().getQuotationsOfInstrument(instrument.getId()));

            requestedNumberOfQuotations = instrument.getQuotationsSortedByDate();

            if (requestedNumberOfQuotations.size() > maxQuotations) {
                requestedNumberOfQuotations = requestedNumberOfQuotations.subList(0, maxQuotations);
                instrument.setQuotations(requestedNumberOfQuotations);
            }
        }
    }

    /**
     * Initializes the number of instruments for each day on which quotations exist.
     *
     */
    private void initializeNumberOfInstrumentsPerDate() {
        List<Quotation> quotationsSortedByDate;
        Integer numberOfInstrumentsOfDate;
        Date currentQuotationDate;

        this.numberOfInstrumentsPerDate = new TreeMap<>();

        for (Instrument instrument : this.instruments) {
            quotationsSortedByDate = instrument.getQuotationsSortedByDate();

            for (Quotation currentQuotation : quotationsSortedByDate) {
                currentQuotationDate = DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate());

                // Check if instrument count of current date already exists.
                numberOfInstrumentsOfDate = this.numberOfInstrumentsPerDate.get(currentQuotationDate);

                if (numberOfInstrumentsOfDate == null) {
                    numberOfInstrumentsOfDate = 0;
                }

                numberOfInstrumentsOfDate++;
                this.numberOfInstrumentsPerDate.put(currentQuotationDate, numberOfInstrumentsOfDate);
            }
        }
    }

    /**
     * Determines the pattern indicator values for the given instruments.
     *
     * @return The pattern indicator values.
     * @throws Exception Indicator value determination failed.
     */
    private TreeMap<Date, Integer> getPatternIndicatorValues() throws Exception {
        List<Quotation> quotationsSortedByDate;
        TreeMap<Date, Integer> patternIndicatorValues = new TreeMap<>();
        Quotation previousQuotation;
        int currentQuotationIndex;
        Integer patternIndicatorValue;
        Date currentQuotationDate;

        for (Instrument instrument : this.instruments) {
            quotationsSortedByDate = instrument.getQuotationsSortedByDate();

            for (Quotation currentQuotation : quotationsSortedByDate) {
                currentQuotationIndex = quotationsSortedByDate.indexOf(currentQuotation);

                // Stop pattern indicator calculation for the current Instrument if no previous Quotation exists.
                if (currentQuotationIndex == (quotationsSortedByDate.size() - 1)) {
                    break;
                }

                previousQuotation = quotationsSortedByDate.get(currentQuotationIndex + 1);
                currentQuotationDate = DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate());

                // Check if pattern indicator of given day already exists.
                patternIndicatorValue = patternIndicatorValues.get(currentQuotationDate);

                if (patternIndicatorValue == null) {
                    patternIndicatorValue = 0;
                }

                patternIndicatorValue += this.getPatternIndicatorValue(currentQuotation, previousQuotation);
                patternIndicatorValues.put(currentQuotationDate, patternIndicatorValue);
            }
        }

        return patternIndicatorValues;
    }

    /**
     * Calculates the value of the pattern indicator based on the given quotations.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The value of the pattern indicator.
     * @throws Exception Value determination failed.
     */
    private int getPatternIndicatorValue(final Quotation currentQuotation, final Quotation previousQuotation)
            throws Exception {
        PatternControllerHelper patternControllerHelper = new PatternControllerHelper();
        int patternIndicatorValue = 0;

        if (patternControllerHelper.isUpOnVolume(currentQuotation, previousQuotation)) {
            patternIndicatorValue++;
        }

        if (patternControllerHelper.isDownOnVolume(currentQuotation, previousQuotation)) {
            patternIndicatorValue--;
        }

        return patternIndicatorValue;
    }

    /**
     * Constructs a XYDataset for the Ritter Pattern Indicator chart.
     *
     * @param patternIndicatorValues The pattern indicator values for which the chart is calculated.
     * @return The XYDataset.
     */
    private XYDataset getRitterPatternIndicatorDataset(final TreeMap<Date, Integer> patternIndicatorValues) {
        TimeSeries timeSeries = new TimeSeries(
                this.getResources().getString("chart.ritterPatternIndicator.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        ArrayList<Entry<Date, Integer>> valueList = new ArrayList<>(patternIndicatorValues.entrySet());
        Map.Entry<Date, Integer> mapEntry;
        float movingAverage;
        final int periodOfMovingAverage = 10;

        // The values have to be sorted from newest to oldest.
        Collections.reverse(valueList);

        // Iterate patternIndicatorValues backwards because XYDatasets are constructed from oldest to newest value.
        for (int i = valueList.size() - 1; i >= 0; i--) {
            try {
                mapEntry = valueList.get(i);
                movingAverage = this.getMovingAverageOfRitterPatternIndicator(valueList, periodOfMovingAverage, i);
                timeSeries.add(new Day(mapEntry.getKey()), movingAverage);
            } catch (Exception exception) {
                continue;
            }
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }

    /**
     * Calculates the moving average of the Ritter Pattern Indicator for the given number of days. The moving average is
     * normalized to a value between -100 and 100 based on the number of instruments.
     *
     * @param patternIndicatorValues The pattern indicator values for which the chart is calculated.
     * @param period                 The period in days for Moving Average calculation.
     * @param beginIndex             The begin index for calculation.
     * @return Then normalized moving average of the Ritter Pattern Indicator.
     * @throws Exception Calculation of Moving Average failed.
     */
    private float getMovingAverageOfRitterPatternIndicator(final ArrayList<Entry<Date, Integer>> patternIndicatorValues,
            final int period, final int beginIndex) throws Exception {

        int endIndex = beginIndex + period - 1;
        int sum = 0;
        int normalizedDailyValue;
        int lastValidBeginIndex;
        Integer instrumentsPerDate;
        Map.Entry<Date, Integer> patternIndicatorValue;
        BigDecimal movingAverage;
        final int numberAdditionalDaysForSmaVolume = 29;
        final int hundredPercent = 100;

        // At least "30 + period" days have to exist to calculate the Moving Average of the RPI.
        // 30 days are needed because this is the minimum number of values needed for availability of SMA(30) of volume.
        // The period is added, because this is the number of days needed for moving average calculation of RPI.
        lastValidBeginIndex = patternIndicatorValues.size() - numberAdditionalDaysForSmaVolume - period;

        if (beginIndex > lastValidBeginIndex) {
            throw new Exception("Not enough statistical values exist to calculate moving average of RPI.");
        }

        for (int i = beginIndex; i <= endIndex; i++) {
            patternIndicatorValue = patternIndicatorValues.get(i);
            instrumentsPerDate = this.numberOfInstrumentsPerDate.get(patternIndicatorValue.getKey());

            if (instrumentsPerDate == null) {
                continue;
            }

            normalizedDailyValue = patternIndicatorValue.getValue() * hundredPercent / instrumentsPerDate;
            sum += normalizedDailyValue;
        }

        movingAverage = new BigDecimal(sum).divide(new BigDecimal(period), 1, RoundingMode.HALF_UP);

        return movingAverage.floatValue();
    }
}
