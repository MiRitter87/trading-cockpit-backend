package backend.webservice.common;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.dashboard.MarketHealthStatus;
import backend.model.instrument.Instrument;
import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the Dashboard service that can be used by multiple service interfaces like SOAP or REST.
 *
 * @author Michael
 */
public class DashboardService {
    /**
     * DAO to access Instrument data.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * Initializes the DashboardService.
     */
    public DashboardService() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
    }

    /**
     * Determines the health status of the given market (sector or industry group).
     *
     * @param instrumentId The ID of the sector or industry group.
     * @return The health status.
     */
    public WebServiceResult getMarketHealthStatus(final Integer instrumentId) {
        WebServiceResult getStatusResult = new WebServiceResult(null);
        MarketHealthStatus marketHealthStatus = new MarketHealthStatus();

        try {
            this.fillInstrumentData(instrumentId, marketHealthStatus);
            getStatusResult.setData(marketHealthStatus);
        } catch (Exception e) {
            // TODO Implement handling. Write error message to result.
        }

        return getStatusResult;
    }

    /**
     * Fills the Instrument data of the MarketHealthStatus.
     *
     * @param instrumentId       The ID of the Instrument.
     * @param marketHealthStatus The MarketHealthStatus whose data are filled.
     * @throws Exception Could not fill Instrument data.
     */
    private void fillInstrumentData(final Integer instrumentId, final MarketHealthStatus marketHealthStatus)
            throws Exception {
        Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);

        marketHealthStatus.setSymbol(instrument.getSymbol());
        marketHealthStatus.setName(instrument.getName());
    }
}
