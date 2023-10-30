package backend.controller.chart.priceVolume;

import java.math.BigDecimal;
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
 * Controller for the creation of a chart displaying an Instrument with Follow-Through Days.
 *
 * @author Michael
 */
public class FollowThroughDaysChartController extends PriceVolumeChartController {
    /**
     * The performance threshold that defines a Follow-Through Day.
     */
    private static final float FTD_PERCENT_THRESHOLD = (float) 1.7;

    /**
     * Gets a chart of an Instrument marked with Follow-Through Days.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getFollowThroughDaysChart(final Integer instrumentId)
            throws NoQuotationsExistException, Exception {
        Instrument instrument = this.getInstrumentWithQuotations(instrumentId);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        final int candleStickPlotWeight = 4;

        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = this.getVolumePlot(instrument, dateAxis);
        XYPlot failedFTDSubplot = this.getFailedFTDPlot(instrument, dateAxis);

        this.addAnnotationsToCandlestickPlot(candleStickSubplot, instrument);

        // Build combined plot based on subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        combinedPlot.add(failedFTDSubplot, 1); // Failed FTD Plot takes 1 vertical size unit.
        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.
        combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        combinedPlot.setDomainAxis(dateAxis);

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }

    /**
     * Adds text annotations for Follow-Through Days to the given plot.
     *
     * @param candlestickPlot The Plot to which annotations are added.
     * @param instrument      The Instrument whose price data are displayed.
     * @throws Exception Annotation creation failed.
     */
    private void addAnnotationsToCandlestickPlot(final XYPlot candlestickPlot, final Instrument instrument)
            throws Exception {

        OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
        XYTextAnnotation textAnnotation;
        List<Integer> indexOfFollowThroughDays;
        List<Integer> indexOfFailedFollowThroughDays;
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        final float factorPercentCalculation = 1.02f;

        indexOfFollowThroughDays = this.getIndexOfFollowThroughDays(quotationsSortedByDate);
        indexOfFailedFollowThroughDays = this.getIndexOfFailedFollowThroughDays(indexOfFollowThroughDays,
                quotationsSortedByDate);

        // Do not draw annotations for failed Follow-Through Days.
        indexOfFollowThroughDays.removeAll(indexOfFailedFollowThroughDays);

        for (Integer indexOfFollowThroughDay : indexOfFollowThroughDays) {
            // Show annotation 2 percent above high price.
            textAnnotation = new XYTextAnnotation("F", instrumentPriceData.getXValue(0, indexOfFollowThroughDay),
                    instrumentPriceData.getHighValue(0, indexOfFollowThroughDay) * factorPercentCalculation);

            candlestickPlot.addAnnotation(textAnnotation);
        }
    }

    /**
     * Determines a List of index numbers of quotations that constitute a Follow-Through Day.
     *
     * @param quotationsSortedByDate A List of Quotations sorted by Date.
     * @return A List of index numbers of the given List that constitute a Follow-Through Day.
     */
    private List<Integer> getIndexOfFollowThroughDays(final List<Quotation> quotationsSortedByDate) {
        List<Integer> indexOfFollowThroughDays = new ArrayList<>();
        Quotation currentQuotation;
        Quotation previousQuotation;
        boolean isFollowThroughDay;

        for (int i = 0; i < quotationsSortedByDate.size() - 1; i++) {
            currentQuotation = quotationsSortedByDate.get(i);
            previousQuotation = quotationsSortedByDate.get(i + 1);
            isFollowThroughDay = this.isFollowThroughDay(currentQuotation, previousQuotation);

            if (isFollowThroughDay) {
                indexOfFollowThroughDays.add(i);
            }
        }

        return indexOfFollowThroughDays;
    }

    /**
     * Determines a List of index numbers of quotations that constitute a failed Follow-Through Day.
     *
     * @param indexOfFollowThroughDays Index numbers of Follow-Through Days.
     * @param quotationsSortedByDate   A List of Quotations sorted by Date.
     * @return A List of index numbers of the given List that constitute a failed Follow-Through Day.
     */
    private List<Integer> getIndexOfFailedFollowThroughDays(final List<Integer> indexOfFollowThroughDays,
            final List<Quotation> quotationsSortedByDate) {

        List<Integer> indexOfFailedFollowThroughDays = new ArrayList<>();
        Quotation currentQuotation;
        boolean isDistributionDayFollowing;
        boolean isLowBeforeFTDUndercut;
        final int numberDaysForDistributionDayCheck = 3;
        final int daysBeforeFTD = 10;
        final int daysAfterFTD = 10;

        // Check for each Follow-Through Day if it failed.
        for (Integer indexOfFollowThroughDay : indexOfFollowThroughDays) {
            currentQuotation = quotationsSortedByDate.get(indexOfFollowThroughDay);

            // A Follow-Through Day is considered failed if a Distribution Day follows within 3 days after the FTD.
            isDistributionDayFollowing = this.isDistributionDayFollowing(currentQuotation, quotationsSortedByDate,
                    numberDaysForDistributionDayCheck);

            if (isDistributionDayFollowing) {
                indexOfFailedFollowThroughDays.add(indexOfFollowThroughDay);
            }

            // A Follow-Through Day is considered failed if the price undercuts the low established within 10 days
            // before the FTD.
            isLowBeforeFTDUndercut = this.isLowBeforeFTDUndercut(currentQuotation, quotationsSortedByDate,
                    daysBeforeFTD, daysAfterFTD);
            if (isLowBeforeFTDUndercut && !indexOfFailedFollowThroughDays.contains(indexOfFollowThroughDay)) {
                indexOfFailedFollowThroughDays.add(indexOfFollowThroughDay);
            }
        }

        return indexOfFailedFollowThroughDays;
    }

    /**
     * Builds a plot to display failed Follow-Through Days.
     *
     * @param instrument The Instrument.
     * @param timeAxis   The x-Axis (time).
     * @return A XYPlot depicting failed Follow-Through Days.
     * @throws Exception Plot generation failed.
     */
    private XYPlot getFailedFTDPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
        IntervalXYDataset distributionDaySumData = this.getFailedFTDDataset(instrument);
        NumberAxis failedFTDAxis = new NumberAxis();

        // Only use integers as tick units. Otherwise values like 0.2 Follow-Through Days would be generated which is
        // useless.
        failedFTDAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        XYBarRenderer failedFTDRenderer = new XYBarRenderer();
        failedFTDRenderer.setShadowVisible(false);

        XYPlot failedFTDSubplot = new XYPlot(distributionDaySumData, timeAxis, failedFTDAxis, failedFTDRenderer);
        failedFTDSubplot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return failedFTDSubplot;
    }

    /**
     * Gets a dataset containing failed Follow-Through Days of the given Instrument.
     *
     * @param instrument The Instrument.
     * @return A dataset with the failed Follow-Through Days.
     * @throws Exception Dataset creation failed.
     */
    private IntervalXYDataset getFailedFTDDataset(final Instrument instrument) throws Exception {
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        List<Integer> indexOfFollowThroughDays;
        List<Integer> indexOfFailedFollowThroughDays;
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries failedFTDTimeSeries = new TimeSeries(
                this.getResources().getString("chart.followThroughDays.timeSeriesFailedFTDName"));
        Quotation currentQuotation;

        indexOfFollowThroughDays = this.getIndexOfFollowThroughDays(quotationsSortedByDate);
        indexOfFailedFollowThroughDays = this.getIndexOfFailedFollowThroughDays(indexOfFollowThroughDays,
                quotationsSortedByDate);

        // Construct dataset of failed Follow-Through Days.
        for (int indexOfFailedFTD : indexOfFailedFollowThroughDays) {
            currentQuotation = quotationsSortedByDate.get(indexOfFailedFTD);
            failedFTDTimeSeries.add(new Day(currentQuotation.getDate()), 1);
        }

        dataset.addSeries(failedFTDTimeSeries);

        return dataset;
    }

    /**
     * Checks if a Distribution Day is following during the next days after the given Quotation.
     *
     * @param quotation              The Quotation which is reference point for the checkup.
     * @param quotationsSortedByDate A sorted List of quotations building the trading history.
     * @param numberDays             The number of days checked after the given Quotation.
     * @return true, if Distribution Day follows in the given number of days after the given Quotation. False, if not.
     */
    private boolean isDistributionDayFollowing(final Quotation quotation, final List<Quotation> quotationsSortedByDate,
            final int numberDays) {
        Quotation currentQuotation;
        Quotation nextQuotation;
        int indexStart;
        int indexEnd;

        indexStart = quotationsSortedByDate.indexOf(quotation);

        if ((indexStart - numberDays) < 0) {
            indexEnd = 0;
        } else {
            indexEnd = indexStart - numberDays;
        }

        indexStart--; // Do not start Distribution Day check at the date of the current Quotation but a day later.

        if (indexStart < 0) {
            return false; // quotation is already the newest price.
        }

        for (int i = indexStart; i >= indexEnd; i--) {
            currentQuotation = quotationsSortedByDate.get(i + 1);
            nextQuotation = quotationsSortedByDate.get(i);

            if (this.isDistributionDay(nextQuotation, currentQuotation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the day of the current Quotation constitutes a Follow-Through Day.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if day of current Quotation is Follow-Through Day; false, if not.
     */
    private boolean isFollowThroughDay(final Quotation currentQuotation, final Quotation previousQuotation) {
        float performance;

        performance = this.getPerformanceCalculator().getPerformance(currentQuotation, previousQuotation);

        if (performance >= FTD_PERCENT_THRESHOLD && (currentQuotation.getVolume() > previousQuotation.getVolume())) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the low price established before the Follow-Through Day has been undercut after the FTD occurred.
     *
     * @param quotation              The Quotation that constitutes the Follow-Through Day.
     * @param quotationsSortedByDate A sorted List of quotations building the trading history.
     * @param daysBeforeFTD          The number of days before the FTD to define for the low price.
     * @param daysAfterFTD           The number of days after the FTD to check if the low price has been undercut.
     * @return true, if low price before FTD has been undercut; false, if not.
     */
    private boolean isLowBeforeFTDUndercut(final Quotation quotation, final List<Quotation> quotationsSortedByDate,
            final int daysBeforeFTD, final int daysAfterFTD) {

        BigDecimal lowBeforeFTD = this.getLowPrice(quotation, quotationsSortedByDate, daysBeforeFTD);
        BigDecimal lowAfterFTD = this.getLowPrice(quotation, quotationsSortedByDate, -daysAfterFTD);

        if (lowBeforeFTD.compareTo(lowAfterFTD) < 1) {
            return false;
        }

        return true;
    }

    /**
     * Gets the low price for the given number of days.
     *
     * @param quotation              The Quotation from which the low price determination is started.
     * @param quotationsSortedByDate A sorted List of quotations building the trading history.
     * @param days                   The number of days in the past (positive number) or future (negative number).
     * @return The low price of the period.
     */
    private BigDecimal getLowPrice(final Quotation quotation, final List<Quotation> quotationsSortedByDate,
            final int days) {
        BigDecimal lowPrice = quotation.getLow();
        Quotation currentQuotation;
        int indexStart;
        int indexEnd;

        indexStart = quotationsSortedByDate.indexOf(quotation);

        if ((indexStart + days) >= quotationsSortedByDate.size() - 1) {
            indexEnd = quotationsSortedByDate.size() - 1;
        } else if ((indexStart + days) < 0) {
            indexEnd = 0;
        } else {
            indexEnd = indexStart + days;
        }

        // Calculate low price of past quotations.
        if (days > 0) {
            for (int i = indexStart; i <= indexEnd; i++) {
                currentQuotation = quotationsSortedByDate.get(i);
                if (currentQuotation.getLow().compareTo(lowPrice) == -1) {
                    lowPrice = currentQuotation.getLow();
                }
            }
        }

        // Calculate low price of future quotations.
        if (days < 0) {
            for (int i = indexStart; i >= indexEnd; i--) {
                currentQuotation = quotationsSortedByDate.get(i);
                if (currentQuotation.getLow().compareTo(lowPrice) == -1) {
                    lowPrice = currentQuotation.getLow();
                }
            }
        }

        return lowPrice;
    }
}
