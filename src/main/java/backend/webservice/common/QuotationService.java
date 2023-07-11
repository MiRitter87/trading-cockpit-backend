package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.quotation.QuotationDAO;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.QuotationArray;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.webservice.ScanTemplate;

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
	 * @param scanTemplate The template that defines the parameters applied to the Scan results.
	 * @param instrumentType The InstrumentType.
	 * @param startDate The start date for the RS number determination. Format used: yyyy-MM-dd
	 * @return A list of the most recent Quotation of each Instrument.
	 */
	public WebServiceResult getQuotations(final ScanTemplate scanTemplate, final InstrumentType instrumentType, final String startDate) {
		QuotationArray quotations = new QuotationArray();
		WebServiceResult getRecentQuotationsResult = new WebServiceResult(null);
		
		if(scanTemplate == null) {
			getRecentQuotationsResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("quotation.noTemplateError")));
			return getRecentQuotationsResult;
		}
		
		try {
			quotations.setQuotations(this.quotationDAO.getQuotationsByTemplate(scanTemplate, instrumentType, startDate));
			getRecentQuotationsResult.setData(quotations);
		} catch (Exception e) {
			getRecentQuotationsResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("quotation.getRecentQuotationsError")));
			
			logger.error(this.resources.getString("quotation.getRecentQuotationsError"), e);
		}
		
		return getRecentQuotationsResult;
	}
	
	
	/**
	 * Provides a List of all quotations of the Instrument with the given ID.
	 * 
	 * @param instrumentId The ID of the Instrument.
	 * @return A List of quotations of the Instrument with the given ID.
	 */
	public WebServiceResult getQuotationsOfInstrument(final Integer instrumentId) {
		QuotationArray quotations = new QuotationArray();
		WebServiceResult getQuotationsResult = new WebServiceResult(null);
		
		try {
			quotations.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
			quotations.sortQuotationsByDate();
			getQuotationsResult.setData(quotations);
		} catch (Exception e) {
			getQuotationsResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("quotation.getQuotationsOfInstrumentError"), instrumentId)));
			
			logger.error(MessageFormat.format(this.resources.getString("quotation.getQuotationsOfInstrumentError"), instrumentId), e);
		}
		
		return getQuotationsResult;
	}
}
