package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.LocalizedException;
import backend.model.dashboard.MarketHealthStatus;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
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
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

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
        Instrument instrument;

        try {
            instrument = this.instrumentDAO.getInstrument(instrumentId);

            if (instrument == null) {
                getStatusResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                        MessageFormat.format(this.resources.getString("instrument.notFound"), instrumentId)));
                return getStatusResult;
            }

            this.validateInstrumentType(instrument);
            this.fillInstrumentData(instrument, marketHealthStatus);
            getStatusResult.setData(marketHealthStatus);
        } catch (LocalizedException localizedException) {
            getStatusResult.addMessage(
                    new WebServiceMessage(WebServiceMessageType.E, localizedException.getLocalizedMessage()));
        } catch (Exception e) {
            getStatusResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("dashboard.getMarketHealthStatusError")));
        }

        return getStatusResult;
    }

    /**
     * Fills the Instrument data of the MarketHealthStatus.
     *
     * @param instrument         The Instrument.
     * @param marketHealthStatus The MarketHealthStatus whose data are filled.
     */
    private void fillInstrumentData(final Instrument instrument, final MarketHealthStatus marketHealthStatus) {
        marketHealthStatus.setSymbol(instrument.getSymbol());
        marketHealthStatus.setName(instrument.getName());
    }

    /**
     * Validates the InstrumentType of the given Instrument. The market health status can only be determined for
     * instruments of type sector or industry group.
     *
     * @param instrument The Instrument.
     * @throws LocalizedException In case the InstrumentType is not allowed.
     */
    private void validateInstrumentType(Instrument instrument) throws LocalizedException {
        if (instrument.getType() != InstrumentType.SECTOR && instrument.getType() != InstrumentType.IND_GROUP) {
            throw new LocalizedException("dashboard.wrongInstrumentType");
        }
    }
}
