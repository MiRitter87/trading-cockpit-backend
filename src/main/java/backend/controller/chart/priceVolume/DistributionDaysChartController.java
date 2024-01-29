package backend.controller.chart.priceVolume;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
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
import org.jfree.data.xy.OHLCDataset;

import backend.controller.NoQuotationsExistException;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Controller for the creation of a chart displaying an Instrument with Distribution Days.
 *
 * @author Michael
 */
public class DistributionDaysChartController extends PriceVolumeChartController {
    /**
     * Gets a chart of an Instrument marked with Distribution Days.
     *
     * @param instrumentId The ID of the Instrument used for Statistic chart creation.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getDistributionDaysChart(final Integer instrumentId)
            throws NoQuotationsExistException, Exception {
        Instrument instrument = this.getInstrumentWithQuotations(instrumentId, TRADING_DAYS_PER_YEAR);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        final int candleStickPlotWeight = 4;

        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = this.getVolumePlot(instrument, dateAxis);
        XYPlot distributionDaySumSubplot = this.getDistributionDaySumPlot(instrument, dateAxis);

        this.addAnnotationsToCandlestickPlot(candleStickSubplot, instrument);

        // Build combined plot based on subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        combinedPlot.add(distributionDaySumSubplot, 1); // Distribution Day Sum Plot takes 1 vertical size unit.
        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.
        combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        combinedPlot.setDomainAxis(dateAxis);

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }

    /**
     * Builds a plot to display the rolling 25-day sum of Distribution Days.
     *
     * @param instrument The Instrument.
     * @param timeAxis   The x-Axis (time).
     * @return A XYPlot depicting the Distribution Day sum.
     * @throws Exception Plot generation failed.
     */
    private XYPlot getDistributionDaySumPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
        IntervalXYDataset distributionDaySumData = this.getDistributionDaySumDataset(instrument);
        NumberAxis distributionDaySumAxis = new NumberAxis();

        XYBarRenderer distributionDaySumRenderer = new XYBarRenderer();
        distributionDaySumRenderer.setShadowVisible(false);

        XYPlot distributionDaySumSubplot = new XYPlot(distributionDaySumData, timeAxis, distributionDaySumAxis,
                distributionDaySumRenderer);
        distributionDaySumSubplot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return distributionDaySumSubplot;
    }

    /**
     * Gets a dataset containing the rolling 25-day sum of distribution days of the given Instrument.
     *
     * @param instrument The Instrument.
     * @return A dataset with the rolling 25-day sum of distribution days.
     * @throws Exception Dataset creation failed.
     */
    private IntervalXYDataset getDistributionDaySumDataset(final Instrument instrument) throws Exception {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        List<Integer> indexOfDistributionDays = new ArrayList<>();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries distributionDaysTimeSeries = new TimeSeries(
                this.getResources().getString("chart.distributionDays.timeSeriesDDSumName"));
        int indexStart;
        int indexEnd;
        int numberOfDistributionDays;
        final int additionalDaysForRollingSum = 24;

        indexOfDistributionDays = this.getIndexOfDistributionDays(quotationsSortedByDate);

        // Determine the rolling 25-day sum of distribution days.
        for (Quotation tempQuotation : quotationsSortedByDate) {
            indexStart = quotationsSortedByDate.indexOf(tempQuotation);

            if ((indexStart + additionalDaysForRollingSum) < quotationsSortedByDate.size()) {
                indexEnd = indexStart + additionalDaysForRollingSum;
            } else {
                indexEnd = quotationsSortedByDate.size() - 1;
            }

            // Count the number of distribution days from indexStart to indexEnd
            numberOfDistributionDays = 0;
            for (int distributionDayIndex : indexOfDistributionDays) {
                if (distributionDayIndex >= indexStart && distributionDayIndex <= indexEnd) {
                    numberOfDistributionDays++;
                }
            }

            distributionDaysTimeSeries.add(new Day(tempQuotation.getDate()), numberOfDistributionDays);
        }

        dataset.addSeries(distributionDaysTimeSeries);

        return dataset;
    }

    /**
     * Determines a List of index numbers of quotations that constitute a Distribution Day.
     *
     * @param quotationsSortedByDate A List of Quotations sorted by Date.
     * @return A List of index numbers of the given List that constitute a Distribution Day.
     */
    private List<Integer> getIndexOfDistributionDays(final List<Quotation> quotationsSortedByDate) {
        List<Integer> indexOfDistributionDays = new ArrayList<>();
        Quotation currentQuotation;
        Quotation previousQuotation;
        boolean priceHasAdvanced;
        boolean isDistributionDay;
        final int numberOfAdditionalDays = 25;
        final float percentAdvanceNegatingDistributionDay = 5;

        for (int i = 0; i < quotationsSortedByDate.size() - 1; i++) {
            currentQuotation = quotationsSortedByDate.get(i);
            previousQuotation = quotationsSortedByDate.get(i + 1);
            isDistributionDay = this.isDistributionDay(currentQuotation, previousQuotation, quotationsSortedByDate);
            priceHasAdvanced = this.hasPriceAdvancedPercent(quotationsSortedByDate, currentQuotation,
                    numberOfAdditionalDays, percentAdvanceNegatingDistributionDay);

            if (isDistributionDay && !priceHasAdvanced) {
                indexOfDistributionDays.add(i);
            }
        }

        return indexOfDistributionDays;
    }

    /**
     * Checks if the price has advanced a certain amount in the days after the current Quotation.
     *
     * @param quotationsSortedByDate A list of quotations building the trading history.
     * @param currentQuotation       The current Quotation against which the performance is being calculated.
     * @param days                   The number of days checked to see if price has advanced.
     * @param percent                The percentage amount of price increase which is required.
     * @return True if price has advanced the given percentage amount in the number of days; false, if not.
     */
    private boolean hasPriceAdvancedPercent(final List<Quotation> quotationsSortedByDate,
            final Quotation currentQuotation, final int days, final float percent) {

        Quotation futureQuotation;
        float performance;
        int indexStart;
        int indexEnd;

        indexStart = quotationsSortedByDate.indexOf(currentQuotation);

        if ((indexStart - days) < 0) {
            indexEnd = 0;
        } else {
            indexEnd = indexStart - days;
        }

        for (int i = indexStart - 1; i >= indexEnd; i--) {
            futureQuotation = quotationsSortedByDate.get(i);
            performance = this.getPerformanceCalculator().getPerformance(futureQuotation, currentQuotation);

            if (performance >= percent) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds text annotations for Distribution Days to the given plot.
     *
     * @param candlestickPlot The Plot to which annotations are added.
     * @param instrument      The Instrument whose price data are displayed.
     * @throws Exception Annotation creation failed.
     */
    private void addAnnotationsToCandlestickPlot(final XYPlot candlestickPlot, final Instrument instrument)
            throws Exception {
        OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
        XYTextAnnotation textAnnotation;
        List<Integer> indexOfDistributionDays = new ArrayList<>();
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        final float factorPercentCalculation = 1.02f;

        indexOfDistributionDays = this.getIndexOfDistributionDays(quotationsSortedByDate);

        for (Integer indexOfDistributionDay : indexOfDistributionDays) {
            // Show annotation 2 percent above high price.
            textAnnotation = new XYTextAnnotation("D", instrumentPriceData.getXValue(0, indexOfDistributionDay),
                    instrumentPriceData.getHighValue(0, indexOfDistributionDay) * factorPercentCalculation);

            candlestickPlot.addAnnotation(textAnnotation);
        }
    }
}
