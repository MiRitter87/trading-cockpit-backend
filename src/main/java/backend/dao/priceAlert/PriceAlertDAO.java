package backend.dao.priceAlert;

import java.util.List;

import backend.dao.ObjectUnchangedException;
import backend.model.LocalizedException;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
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
	void insertPriceAlert(final PriceAlert priceAlert) throws Exception;
	
	
	/**
	 * Deletes a price alert.
	 * 
	 * @param priceAlert The price alert to be deleted.
	 * @throws Exception Deletion failed.
	 */
	void deletePriceAlert(final PriceAlert priceAlert) throws Exception;
	
	
	/**
	 * Gets all price alerts.
	 * 
	 * @param priceAlertOrderAttribute The attribute by which the price alerts are ordered.
	 * @param triggerStatus Defines which alerts are selected based on the status of the triggerTime attribute.
	 * @param confirmationStatus Defines which alerts are selected based on the status of the confirmationTime attribute.
	 * @return All price alerts.
	 * @throws Exception Price alert retrieval failed.
	 */
	List<PriceAlert> getPriceAlerts(final PriceAlertOrderAttribute priceAlertOrderAttribute, 
			final TriggerStatus triggerStatus, final ConfirmationStatus confirmationStatus) throws Exception;
	
	
	/**
	 * Gets the price alert with the given id.
	 * 
	 * @param id The id of the price alert.
	 * @return The price alert with the given id.
	 * @throws Exception Price alert retrieval failed.
	 */
	PriceAlert getPriceAlert(final Integer id) throws Exception;
	
	
	/**
	 * Updates the given price alert.
	 * 
	 * @param priceAlert The price alert to be updated.
	 * @throws ObjectUnchangedException Object data did not change.
	 * @throws LocalizedException A general exception containing a localized message.
	 * @throws Exception Price alert update failed.
	 */
	void updatePriceAlert(final PriceAlert priceAlert) throws ObjectUnchangedException, LocalizedException, Exception;
}
