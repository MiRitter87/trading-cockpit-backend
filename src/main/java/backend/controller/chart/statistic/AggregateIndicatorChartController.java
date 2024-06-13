package backend.controller.chart.statistic;

import java.util.List;

import org.jfree.chart.JFreeChart;

import backend.controller.AggregateIndicatorCalculator;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
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
     * @throws Exception Chart generation failed.
     */
    public JFreeChart getAggregateIndicatorChart(final Integer instrumentId) throws Exception {
        Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);
        List<Statistic> statistics;

        this.validateInstrumentType(instrument);
        statistics = this.calculator.getStatistics(instrument);
        instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));

        return null;
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
}
