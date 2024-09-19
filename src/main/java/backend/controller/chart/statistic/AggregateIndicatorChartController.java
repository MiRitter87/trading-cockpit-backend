package backend.controller.chart.statistic;

import java.awt.Color;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.controller.AggregateIndicatorCalculator;
import backend.controller.NoQuotationsExistException;
import backend.controller.scan.StatisticCalculationController;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Controller for the creation of a chart displaying the Aggregate Indicator of a sector or industry group.
 *
 * @author Michael
 */
public class AggregateIndicatorChartController extends StatisticChartController {
    /**
     * The Instrument for which the Aggregate Indicator is being calculated.
     */
    private Instrument instrument;

    /**
     * Initializes the AggregateIndicatorChartController.
     *
     * @param instrumentId The ID of the sector or industry group.
     * @param listId       The ID of the list defining the instruments used to calculate % of stocks above SMA(50)
     *                     (optional).
     * @throws Exception Failed to initialize data.
     */
    public AggregateIndicatorChartController(final Integer instrumentId, final Integer listId) throws Exception {
        super();

        StatisticCalculationController statisticCalculationController = new StatisticCalculationController();

        this.instrument = this.getInstrumentDAO().getInstrument(instrumentId);
        this.validateInstrumentType();
        this.instrument.setQuotations(this.getQuotationDAO().getQuotationsOfInstrument(this.instrument.getId()));

        if (listId != null) {
            this.setList(this.getListDAO().getList(listId));
        }

        this.setStatistics(
                statisticCalculationController.getStatisticsForSectorOrIg(this.instrument, this.getList(), null));
    }

    /**
     * Gets a chart of the Aggregate Indicator of a sector or industry group.
     *
     * @return The chart.
     * @throws NoQuotationsExistException No quotations or statistics exist for the Instrument with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getAggregateIndicatorChart() throws NoQuotationsExistException, Exception {

        XYDataset dataset = this.getAggregateIndicatorDataset();
        JFreeChart chart;

        chart = ChartFactory.createTimeSeriesChart(this.instrument.getName(), null, null, dataset, true, true, false);

        this.addTriggerLines(chart.getXYPlot());
        this.applyBackgroundTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Validates the InstrumentType of the given Instrument. The Aggregate Indicator can only be determined for
     * instruments of type sector or industry group.
     *
     * @throws Exception In case the InstrumentType is not allowed.
     */
    private void validateInstrumentType() throws Exception {
        if (this.instrument.getType() != InstrumentType.SECTOR
                && this.instrument.getType() != InstrumentType.IND_GROUP) {
            throw new Exception();
        }
    }

    /**
     * Constructs a XYDataset for the Aggregate Indicator chart.
     *
     * @return The XYDataset.
     * @throws NoQuotationsExistException No quotations or statistics exist for the Instrument with the given ID.
     * @throws Exception                  XYDataset creation failed.
     */
    private XYDataset getAggregateIndicatorDataset() throws NoQuotationsExistException, Exception {
        AggregateIndicatorCalculator calculator = new AggregateIndicatorCalculator();
        TimeSeries timeSeries = new TimeSeries(
                this.getResources().getString("chart.aggregateIndicator.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        List<Quotation> quotations = this.instrument.getQuotationsSortedByDate();
        Quotation currentQuotation;
        int aggregateIndicator;
        final int minQuotations = 70;

        // Statistics are needed for calculation.
        if (this.getStatistics() == null || this.getStatistics().size() == 0) {
            throw new NoQuotationsExistException();
        }

        // At least 70 quotations are needed for calculation of Slow Stochastic weekly.
        if (quotations.size() < minQuotations) {
            throw new NoQuotationsExistException();
        }

        // Iterate quotations backwards because XYDatasets are constructed from oldest to newest value.
        for (int i = quotations.size() - minQuotations - 1; i >= 0; i--) {
            currentQuotation = quotations.get(i);
            aggregateIndicator = calculator.getAggregateIndicator(quotations, this.getStatistics(), currentQuotation,
                    this.instrument);

            if (aggregateIndicator > -1) {
                timeSeries.add(new Day(currentQuotation.getDate()), aggregateIndicator);
            }
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }

    /**
     * Adds trigger lines for buy and sell levels to the chart.
     *
     * @param plot The XYPlot to which the trigger lines are added.
     */
    private void addTriggerLines(final XYPlot plot) {
        final int thresholdBottom = 15;
        final int thresholdTop = 85;

        this.addHorizontalLine(plot, thresholdBottom, Color.GREEN);
        this.addHorizontalLine(plot, thresholdTop, Color.RED);
    }
}
