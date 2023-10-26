package backend.controller.chart.priceVolume;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataset;

import backend.controller.NoQuotationsExistException;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Controller for the creation of a chart displaying an Instrument with Pocket Pivots.
 *
 * @author Michael
 */
public class PocketPivotChartController extends PriceVolumeChartController {
    /**
     * Gets a chart of an Instrument marked with Pocket Pivots.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getPocketPivotsChart(final Integer instrumentId) throws NoQuotationsExistException, Exception {
        Instrument instrument = this.getInstrumentWithQuotations(instrumentId);
        JFreeChart chart;
        DateAxis dateAxis = this.getDateAxis(instrument); // The shared time axis of all subplots.
        final int candleStickPlotWeight = 4;

        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = this.getVolumePlot(instrument, dateAxis);

        this.addAnnotationsToCandlestickPlot(candleStickSubplot, instrument);

        // Build combined plot based on subplots.
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        combinedPlot.add(candleStickSubplot, candleStickPlotWeight); // Price Plot takes 4 vertical size units.
        combinedPlot.add(volumeSubplot, 1); // Volume Plot takes 1 vertical size unit.
        combinedPlot.setDomainAxis(dateAxis);

        // Build chart based on combined Plot.
        chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

        return chart;
    }

    /**
     * Adds text annotations for Pocket Pivots to the given plot.
     *
     * @param candlestickPlot The Plot to which annotations are added.
     * @param instrument      The Instrument whose price data are displayed.
     * @throws Exception Annotation creation failed.
     */
    private void addAnnotationsToCandlestickPlot(final XYPlot candlestickPlot, final Instrument instrument)
            throws Exception {
        OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
        XYTextAnnotation textAnnotation;
        List<Integer> indexOfPocketPivots = new ArrayList<>();
        List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        final float factorPercentCalculation = 1.02f;

        indexOfPocketPivots = this.getIndexOfPocketPivots(quotationsSortedByDate);

        for (Integer indexOfPocketPivot : indexOfPocketPivots) {
            // Show annotation 2 percent above high price.
            textAnnotation = new XYTextAnnotation("P", instrumentPriceData.getXValue(0, indexOfPocketPivot),
                    instrumentPriceData.getHighValue(0, indexOfPocketPivot) * factorPercentCalculation);

            candlestickPlot.addAnnotation(textAnnotation);
        }
    }

    /**
     * Determines a List of index numbers of quotations that constitute a Pocket Pivot.
     *
     * @param quotationsSortedByDate A List of Quotations sorted by Date.
     * @return A List of index numbers of the given List that constitute a Pocket Pivot.
     */
    private List<Integer> getIndexOfPocketPivots(final List<Quotation> quotationsSortedByDate) {
        List<Integer> indexOfPocketPivots = new ArrayList<>();
        boolean isPocketPivot;
        final int lookbackDaysForPPCalculation = 11;

        for (int i = 0; i < quotationsSortedByDate.size() - lookbackDaysForPPCalculation; i++) {
            isPocketPivot = this.isPocketPivot(quotationsSortedByDate, i);

            if (isPocketPivot) {
                indexOfPocketPivots.add(i);
            }
        }

        return indexOfPocketPivots;
    }

    /**
     * Checks if the Quotation defined by the given index constitutes a Pocket Pivot.
     *
     * @param quotationsSortedByDate A List of Quotations sorted by Date.
     * @param quotationIndex         The index of the Quotation which is checked.
     * @return true, if Quotation with given index is Pocket Pivot; false, if not.
     */
    private boolean isPocketPivot(final List<Quotation> quotationsSortedByDate, final int quotationIndex) {
        Quotation currentQuotation;
        Quotation previousQuotation;
        float performance;
        long largestDownVolume = 0;
        long quotationVolume;
        final int lookbackDaysForPPCalculation = 11;
        final int lookbackDaysForVolume = 10;

        // No Pocket Pivot, if not at least 11 historical trading days exist after the given quotationIndex.
        if (quotationIndex + lookbackDaysForPPCalculation >= quotationsSortedByDate.size()) {
            return false;
        }

        currentQuotation = quotationsSortedByDate.get(quotationIndex);
        previousQuotation = quotationsSortedByDate.get(quotationIndex + 1);

        performance = this.getPerformanceCalculator().getPerformance(currentQuotation, previousQuotation);
        if (performance <= 0) {
            return false; // A Pocket Pivot only occurs on up-days.
        }

        // The volume of the potential pocket pivot.
        quotationVolume = currentQuotation.getVolume();

        // Check if the volume of the current Quotation is higher than the highest down-volume of the last 10 trading
        // days.
        for (int i = quotationIndex + 1; i <= quotationIndex + lookbackDaysForVolume; i++) {
            currentQuotation = quotationsSortedByDate.get(i);
            previousQuotation = quotationsSortedByDate.get(i + 1);
            performance = this.getPerformanceCalculator().getPerformance(currentQuotation, previousQuotation);

            if (performance < 0 && currentQuotation.getVolume() > largestDownVolume) {
                largestDownVolume = currentQuotation.getVolume();
            }
        }

        if (quotationVolume > largestDownVolume) {
            return true;
        }

        return false;
    }
}
