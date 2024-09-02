package backend.controller.chart.statistic;

import java.awt.Color;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.controller.AggregateIndicatorCalculator;
import backend.controller.NoQuotationsExistException;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;

/**
 * Controller for the creation of a chart displaying the Aggregate Indicator of a sector or industry group.
 *
 * @author Michael
 */
public class AggregateIndicatorChartController extends StatisticChartController {
    /**
     * Calculator of the Aggregate Indicator.
     */
    private AggregateIndicatorCalculator calculator;

    /**
     * DAO to access Instrument data.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * Initializes the AggregateIndicatorChartController.
     */
    public AggregateIndicatorChartController() {
        this.calculator = new AggregateIndicatorCalculator();
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    /**
     * Gets a chart of the Aggregate Indicator of a sector or industry group.
     *
     * @param instrumentId The ID of the sector or industry group.
     * @return The chart.
     * @throws NoQuotationsExistException No quotations or statistics exist for the Instrument with the given ID.
     * @throws Exception                  Chart generation failed.
     */
    public JFreeChart getAggregateIndicatorChart(final Integer instrumentId)
            throws NoQuotationsExistException, Exception {
        Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);
        List<Statistic> statistics;
        XYDataset dataset;
        JFreeChart chart;
        final int thresholdBottom = 15;
        final int thresholdTop = 85;

        this.validateInstrumentType(instrument);

        statistics = this.calculator.getStatistics(instrument, null);
        instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
        dataset = this.getAggregateIndicatorDataset(instrument, statistics);

        chart = ChartFactory.createTimeSeriesChart(instrument.getName(), null, null, dataset, true, true, false);

        this.addHorizontalLine(chart.getXYPlot(), thresholdBottom, Color.GREEN);
        this.addHorizontalLine(chart.getXYPlot(), thresholdTop, Color.RED);
        this.applyBackgroundTheme(chart);
        chart.getXYPlot().setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);

        return chart;
    }

    /**
     * Validates the InstrumentType of the given Instrument. The Aggregate Indicator can only be determined for
     * instruments of type sector or industry group.
     *
     * @param instrument The Instrument.
     * @throws Exception In case the InstrumentType is not allowed.
     */
    private void validateInstrumentType(final Instrument instrument) throws Exception {
        if (instrument.getType() != InstrumentType.SECTOR && instrument.getType() != InstrumentType.IND_GROUP) {
            throw new Exception();
        }
    }

    /**
     * Constructs a XYDataset for the Aggregate Indicator chart.
     *
     * @param instrument The Instrument with quotations.
     * @param statistics The statistics used for calculation.
     * @return The XYDataset.
     * @throws NoQuotationsExistException No quotations or statistics exist for the Instrument with the given ID.
     * @throws Exception                  XYDataset creation failed.
     */
    private XYDataset getAggregateIndicatorDataset(final Instrument instrument, final List<Statistic> statistics)
            throws NoQuotationsExistException, Exception {

        TimeSeries timeSeries = new TimeSeries(
                this.getResources().getString("chart.aggregateIndicator.timeSeriesName"));
        TimeZone timeZone = TimeZone.getDefault();
        TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
        List<Quotation> quotations = instrument.getQuotationsSortedByDate();
        Quotation currentQuotation;
        int aggregateIndicator;
        final int minQuotations = 70;

        // Statistics are needed for calculation.
        if (statistics == null || statistics.size() == 0) {
            throw new NoQuotationsExistException();
        }

        // At least 70 quotations are needed for calculation of Slow Stochastic weekly.
        if (quotations.size() < minQuotations) {
            throw new NoQuotationsExistException();
        }

        // Iterate quotations backwards because XYDatasets are constructed from oldest to newest value.
        for (int i = quotations.size() - minQuotations - 1; i >= 0; i--) {
            currentQuotation = quotations.get(i);
            aggregateIndicator = this.calculator.getAggregateIndicator(quotations, statistics, currentQuotation,
                    instrument);

            if (aggregateIndicator > -1) {
                timeSeries.add(new Day(currentQuotation.getDate()), aggregateIndicator);
            }
        }

        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);

        return timeSeriesColleciton;
    }
}
