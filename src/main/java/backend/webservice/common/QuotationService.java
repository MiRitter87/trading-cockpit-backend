package backend.webservice.common;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.quotation.QuotationDAO;
import backend.model.instrument.QuotationArray;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the Quotation WebService that can be used by multiple service interfaces like SOAP or REST.
 * 
 * @author Michael
 */
public class QuotationService {
	/**
	 * DAO for Quotation access.
	 */
	private QuotationDAO quotationDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(QuotationService.class);
	
	
	/**
	 * Initializes the Quotation service.
	 */
	public QuotationService() {
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
	}
	
	
	/**
	 * Provides a list of the most recent Quotation of each Instrument.
	 * Only those quotations are provided that have an Indicator associated with them.
	 * 
	 * @return A list of the most recent Quotation of each Instrument.
	 */
	public WebServiceResult getQuotations() {
		QuotationArray quotations = new QuotationArray();
		WebServiceResult getRecentQuotationsResult = new WebServiceResult(null);
		
		try {
			quotations.setQuotations(this.quotationDAO.getRecentQuotations());
			getRecentQuotationsResult.setData(quotations);
		} catch (Exception e) {
			getRecentQuotationsResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("quotation.getRecentQuotationsError")));
			
			logger.error(this.resources.getString("quotation.getRecentQuotationsError"), e);
		}
		
		return getRecentQuotationsResult;
	}
}
