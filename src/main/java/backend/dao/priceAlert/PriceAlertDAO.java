package backend.dao.priceAlert;

import java.math.BigDecimal;
import java.util.List;

import backend.dao.ObjectUnchangedException;
import backend.model.LocalizedException;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;

/**
 * Interface for price alert persistence.
 *
 * @author Michael
 */
public interface PriceAlertDAO {
    /**
     * Inserts a price alert.
     *
     * @param priceAlert The price alert to be inserted.
     * @throws Exception Insertion failed.
     */
    void insertPriceAlert(PriceAlert priceAlert) throws Exception;

    /**
     * Deletes a price alert.
     *
     * @param priceAlert The price alert to be deleted.
     * @throws Exception Deletion failed.
     */
    void deletePriceAlert(PriceAlert priceAlert) throws Exception;

    /**
     * Gets all price alerts.
     *
     * @param priceAlertOrderAttribute The attribute by which the price alerts are ordered.
     * @param triggerStatus            Defines which alerts are selected based on the status of the triggerTime
     *                                 attribute.
     * @param confirmationStatus       Defines which alerts are selected based on the status of the confirmationTime
     *                                 attribute.
     * @return All price alerts.
     * @throws Exception PriceAlert retrieval failed.
     */
    List<PriceAlert> getPriceAlerts(PriceAlertOrderAttribute priceAlertOrderAttribute, TriggerStatus triggerStatus,
            ConfirmationStatus confirmationStatus) throws Exception;

    /**
     * Gets all price alerts.
     *
     * @param instrumentId   The ID of the Instrument the PriceAlert is based on.
     * @param priceAlertType The PriceAlertType.
     * @param price          The price.
     * @param triggerStatus  The TriggerStatus.
     * @return A list of price alerts that match the given criteria.
     * @throws Exception PriceAlert retrieval failed.
     */
    List<PriceAlert> getPriceAlerts(Integer instrumentId, PriceAlertType priceAlertType, BigDecimal price,
            TriggerStatus triggerStatus) throws Exception;

    /**
     * Gets the price alert with the given id.
     *
     * @param id The id of the price alert.
     * @return The price alert with the given id.
     * @throws Exception Price alert retrieval failed.
     */
    PriceAlert getPriceAlert(Integer id) throws Exception;

    /**
     * Updates the given price alert.
     *
     * @param priceAlert The price alert to be updated.
     * @throws ObjectUnchangedException Object data did not change.
     * @throws LocalizedException       A general exception containing a localized message.
     * @throws Exception                Price alert update failed.
     */
    void updatePriceAlert(PriceAlert priceAlert) throws ObjectUnchangedException, LocalizedException, Exception;
}
