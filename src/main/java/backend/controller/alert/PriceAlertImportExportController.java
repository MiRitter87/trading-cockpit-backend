package backend.controller.alert;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.model.LocalizedException;
import backend.model.instrument.Instrument;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.TriggerStatus;

/**
 * This class is used to export price alerts from the database to a JSON file. Additionally price alerts from a JSON
 * file can be imported into the database.
 *
 * @author MiRitter87
 */
public class PriceAlertImportExportController {
    /**
     * DAO for PriceAlert access.
     */
    private PriceAlertDAO priceAlertDAO;

    /**
     * DAO for Instrument access.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(PriceAlertImportExportController.class);

    /**
     * Initializes the PriceAlertImportExportController.
     */
    public PriceAlertImportExportController() {
        this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
    }

    /**
     * Exports all price alerts of the database.
     *
     * @return A JSON String containing all price alerts.
     * @throws Exception Export failed.
     */
    public String exportPriceAlerts() throws Exception {
        List<PriceAlert> priceAlerts = this.priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.ID, TriggerStatus.ALL,
                ConfirmationStatus.ALL);
        ObjectMapper mapper = new ObjectMapper();
        String alertsAsJson = mapper.writeValueAsString(priceAlerts);

        return alertsAsJson;
    }

    /**
     * Imports the price alerts provided.
     *
     * @param priceAlertsAsJson A JSON String containing all price alerts to be imported.
     * @throws LocalizedException A specific error the user is notified about.
     * @throws Exception          Import failed.
     */
    public void importPriceAlerts(final String priceAlertsAsJson) throws LocalizedException, Exception {
        List<PriceAlert> deserializedAlerts;
        ObjectMapper mapper = new ObjectMapper();
        boolean warningsOccurred = false;

        try {
            deserializedAlerts = mapper.readValue(priceAlertsAsJson, new TypeReference<List<PriceAlert>>() {
            });

            if (deserializedAlerts.size() == 0) {
                throw new LocalizedException("priceAlert.importEmptyList");
            }

            for (PriceAlert priceAlert : deserializedAlerts) {
                if (!this.isInstrumentExisting(priceAlert.getInstrument())) {
                    warningsOccurred = true;
                    LOGGER.warn("The Instrument with ID " + priceAlert.getInstrument().getId() + " and Symbol "
                            + priceAlert.getInstrument().getSymbol()
                            + " was not found on the database. Skipping import of this price alert.");
                    continue;
                }

                if (!this.insertPriceAlert(priceAlert)) {
                    warningsOccurred = true;
                }
            }
        } catch (JsonParseException parseException) {
            throw new LocalizedException("priceAlert.importJsonMalformed");
        }

        if (warningsOccurred) {
            throw new LocalizedException("priceAlert.importWithWarnings");
        }
    }

    /**
     * Checks if the given Instrument exists on the database.
     *
     * @param jsonInstrument The Instrument from the JSON String to be checked.
     * @return true, if Instrument exists; false, if not.
     * @throws Exception Failed to check if Instrument exists.
     */
    private boolean isInstrumentExisting(final Instrument jsonInstrument) throws Exception {
        Instrument databaseInstrument = this.instrumentDAO.getInstrument(jsonInstrument.getId());

        if (databaseInstrument == null) {
            return false;
        }

        if (databaseInstrument.getId().equals(jsonInstrument.getId())
                && databaseInstrument.getSymbol().equals(jsonInstrument.getSymbol())
                && databaseInstrument.getStockExchange().equals(jsonInstrument.getStockExchange())) {
            return true;
        }

        return false;
    }

    /**
     * Tries to insert the given PriceAlert into the database.
     *
     * @param priceAlert The PriceAlert to be inserted.
     * @return true, if insert succeeded; false, if insert failed.
     */
    private boolean insertPriceAlert(final PriceAlert priceAlert) {
        Integer currentAlertId;

        currentAlertId = priceAlert.getId();
        priceAlert.setId(null);

        try {
            this.priceAlertDAO.insertPriceAlert(priceAlert);
            return true;
        } catch (LocalizedException localizedException) {
            LOGGER.warn(localizedException.getLocalizedMessage());
        } catch (Exception exception) {
            LOGGER.error("Failed to import price alert with ID " + currentAlertId.toString(), exception.getMessage());
        }

        return false;
    }
}
