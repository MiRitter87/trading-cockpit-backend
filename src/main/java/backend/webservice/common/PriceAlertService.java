package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.exception.ObjectUnchangedException;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertArray;
import backend.model.priceAlert.TriggerStatus;
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
	public static final Logger logger = LogManager.getLogger(PriceAlertService.class);
	
	
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
	
	
	/**
	 * Provides a list of all price alerts that match the given filter criteria.
	 * 
	 * @param triggerStatus Filter criterion for trigger status.
	 * @param confirmationStatus Filter criterion for confirmation status.
	 * @return A list of all price alerts.
	 */
	public WebServiceResult getPriceAlerts(final TriggerStatus triggerStatus, final ConfirmationStatus confirmationStatus) {
		PriceAlertArray priceAlerts = new PriceAlertArray();
		WebServiceResult getPriceAlertsResult = new WebServiceResult(null);
		
		try {
			priceAlerts.setPriceAlerts(this.priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.ID, triggerStatus, confirmationStatus));
			getPriceAlertsResult.setData(priceAlerts);
		} catch (Exception e) {
			getPriceAlertsResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("priceAlert.getPriceAlertsError")));
			
			logger.error(this.resources.getString("priceAlert.getPriceAlertsError"), e);
		}
		
		return getPriceAlertsResult;
	}
	
	
	/**
	 * Adds a price alert.
	 * 
	 * @param priceAlert The price alert to be added.
	 * @return The result of the add function.
	 */
	public WebServiceResult addPriceAlert(final PriceAlert priceAlert) {
		WebServiceResult addPriceAlertResult = new WebServiceResult();
		
		//Validate the given price alert.
		try {
			priceAlert.validate();
		} catch (Exception validationException) {
			addPriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return addPriceAlertResult;
		}
		
		//Insert price alert if validation is successful.
		try {
			this.priceAlertDAO.insertPriceAlert(priceAlert);
			addPriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, this.resources.getString("priceAlert.addSuccess")));
			addPriceAlertResult.setData(priceAlert.getId());
		} catch (Exception e) {
			addPriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("priceAlert.addError")));
			logger.error(this.resources.getString("account.addError"), e);
		}
		
		return addPriceAlertResult;
	}
	
	
	/**
	 * Deletes the price alert with the given id.
	 * 
	 * @param id The id of the price alert to be deleted.
	 * @return The result of the delete function.
	 */
	public WebServiceResult deletePriceAlert(final Integer id) {
		WebServiceResult deletePriceAlertResult = new WebServiceResult(null);
		PriceAlert priceAlert = null;
		
		//Check if a price alert with the given id exists.
		try {
			priceAlert = this.priceAlertDAO.getPriceAlert(id);
			
			if(priceAlert != null) {
				//Delete price alert if exists.
				this.priceAlertDAO.deletePriceAlert(priceAlert);
				deletePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
						MessageFormat.format(this.resources.getString("priceAlert.deleteSuccess"), id)));
			}
			else {
				//Price alert not found.
				deletePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("priceAlert.notFound"), id)));
			}
		}
		catch (Exception e) {
			deletePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("priceAlert.deleteError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("priceAlert.deleteError"), id), e);
		}
		
		return deletePriceAlertResult;
	}
	
	
	/**
	 * Updates an existing price alert.
	 * 
	 * @param priceAlert The price alert to be updated.
	 * @return The result of the update function.
	 */
	public WebServiceResult updatePriceAlert(final PriceAlert priceAlert) {
		WebServiceResult updatePriceAlertResult = new WebServiceResult(null);
		
		//Validation of the given price alert.
		try {
			priceAlert.validate();
		} catch (Exception validationException) {
			updatePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return updatePriceAlertResult;
		}
		
		//Update price alert if validation is successful.
		try {
			this.priceAlertDAO.updatePriceAlert(priceAlert);
			updatePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
					MessageFormat.format(this.resources.getString("priceAlert.updateSuccess"), priceAlert.getId())));
		} 
		catch(ObjectUnchangedException objectUnchangedException) {
			updatePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
					MessageFormat.format(this.resources.getString("priceAlert.updateUnchanged"), priceAlert.getId())));
		}
		catch (Exception e) {
			updatePriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
					MessageFormat.format(this.resources.getString("priceAlert.updateError"), priceAlert.getId())));
			
			logger.error(MessageFormat.format(this.resources.getString("priceAlert.updateError"), priceAlert.getId()), e);
		}
		
		return updatePriceAlertResult;
	}
}
