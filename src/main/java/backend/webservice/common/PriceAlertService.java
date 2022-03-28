package backend.webservice.common;

import java.util.ResourceBundle;

import backend.dao.DAOManager;
import backend.dao.PriceAlertDAO;

/**
 * Common implementation of the price alert WebService that can be used by multiple service interfaces like SOAP or REST.
 * 
 * @author Michael
 */
public class PriceAlertService {
	/**
	 * DAO for price alert access.
	 */
	private PriceAlertDAO priceAlertDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Initializes the price alert service.
	 */
	public PriceAlertService() {
		this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
	}
}
