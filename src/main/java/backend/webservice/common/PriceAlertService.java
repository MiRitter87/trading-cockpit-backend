package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.PriceAlertDAO;
import backend.model.PriceAlert;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

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
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(PriceAlert.class);
	
	
	/**
	 * Initializes the price alert service.
	 */
	public PriceAlertService() {
		this.priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
	}
	
	
	/**
	 * Provides the price alert with the given id.
	 * 
	 * @param id The id of the price alert.
	 * @return The price alert with the given id, if found.
	 */
	public WebServiceResult getPriceAlert(final Integer id) {
		PriceAlert priceAlert = null;
		WebServiceResult getPriceAlertResult = new WebServiceResult(null);
		
		try {
			priceAlert = this.priceAlertDAO.getPriceAlert(id);
			
			if(priceAlert != null) {
				//Price alert found
				getPriceAlertResult.setData(priceAlert);
			}
			else {
				//Price Alert not found
				getPriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("priceAlert.notFound"), id)));
			}
		}
		catch (Exception e) {
			getPriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("priceAlert.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("priceAlert.getError"), id), e);
		}
		
		return getPriceAlertResult;
	}
}
