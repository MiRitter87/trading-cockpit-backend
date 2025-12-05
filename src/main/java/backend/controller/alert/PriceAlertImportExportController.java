package backend.controller.alert;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import backend.dao.DAOManager;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.TriggerStatus;

/**
 * This class is used to export price alerts from the database to a json file. Additionally price alerts from a json
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
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(PriceAlertImportExportController.class);

    /**
     * Initializes the PriceAlertImportExportController.
     */
    public PriceAlertImportExportController() {
        this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
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
}
