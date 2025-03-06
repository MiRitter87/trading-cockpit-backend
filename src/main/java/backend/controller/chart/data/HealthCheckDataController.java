package backend.controller.chart.data;

import backend.controller.chart.priceVolume.HealthCheckChartController;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.dao.DAOManager;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.chart.HealthCheckChartData;
import backend.model.instrument.Instrument;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.Protocol;

/**
 * Controller to provide data for the construction of a price/volume chart with health check information.
 *
 * @author Michael
 */
public class HealthCheckDataController {
    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * Initializes the HealthCheckDataController.
     */
    public HealthCheckDataController() {
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    /**
     * Provides quotations and health check data to construct a price/volume chart with health check information.
     *
     * @param instrumentId   The ID of the Instrument used for data determination.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The chart data.
     * @throws Exception Chart data generation failed.
     */
    public HealthCheckChartData getHealthCheckData(final Integer instrumentId, final HealthCheckProfile profile,
            final Integer lookbackPeriod) throws Exception {

        HealthCheckChartData chartData = new HealthCheckChartData();

        chartData.setQuotations(this.getQuotationArray(instrumentId));
        chartData.setProtocol(this.getHealthProtocol(chartData.getQuotations(), instrumentId, profile, lookbackPeriod));

        return chartData;
    }

    /**
     * Determines the quotations of the Instrument with the given ID.
     *
     * @param instrumentId The ID of the Instrument used for data determination
     * @return The quotations.
     * @throws Exception Quotation datermination failed.
     */
    private QuotationArray getQuotationArray(final Integer instrumentId) throws Exception {
        QuotationArray quotations = new QuotationArray(this.quotationDAO.getQuotationsOfInstrument(instrumentId));

        quotations.sortQuotationsByDate();

        return quotations;
    }

    /**
     * Determines the Protocol of the health check.
     *
     * @param quotations     The quotations building the trading history.
     * @param instrumentId   The ID of the Instrument used for data determination.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The Protocol.
     * @throws Exception Failed to perform health check.
     */
    private Protocol getHealthProtocol(final QuotationArray quotations, final Integer instrumentId,
            final HealthCheckProfile profile, final Integer lookbackPeriod) throws Exception {

        HealthCheckChartController healthCheckChartController = new HealthCheckChartController();
        Instrument instrument = new Instrument();
        instrument.setId(instrumentId);
        instrument.setQuotations(quotations.getQuotations());

        return healthCheckChartController.getHealthProtocol(instrument, profile, lookbackPeriod);
    }
}
