package backend.dao.priceAlert;

import java.util.List;

import backend.exception.ObjectUnchangedException;
import backend.model.priceAlert.PriceAlert;

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
	 * @param onlyNotTriggered Only return alerts that have not been triggered if set to true. Return all if false.
	 * @return All price alerts.
	 * @throws Exception Price alert retrieval failed.
	 */
	List<PriceAlert> getPriceAlerts(final boolean onlyNotTriggered) throws Exception;
	
	
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
	 * @throws Exception Price alert update failed.
	 */
	void updatePriceAlert(final PriceAlert priceAlert) throws ObjectUnchangedException, Exception;
}
