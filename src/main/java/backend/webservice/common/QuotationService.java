package backend.webservice.common;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.quotation.QuotationDAO;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
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
	 * @return A list of the most recent Quotation of each Instrument.
	 */
	public WebServiceResult getQuotations(final ScanTemplate scanTemplate, final InstrumentType instrumentType) {
		QuotationArray quotations = new QuotationArray();
		WebServiceResult getRecentQuotationsResult = new WebServiceResult(null);
		
		try {
			quotations.setQuotations(this.getQuotationsByTemplate(scanTemplate, instrumentType));
			getRecentQuotationsResult.setData(quotations);
		} catch (Exception e) {
			getRecentQuotationsResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("quotation.getRecentQuotationsError")));
			
			logger.error(this.resources.getString("quotation.getRecentQuotationsError"), e);
		}
		
		return getRecentQuotationsResult;
	}
	
	
	/**
	 * Provides a list of quotations based on the given Scan template and InstrumentType.
	 * 
	 * @param scanTemplate The template that defines the parameters applied to the Scan results.
	 * @param instrumentType The InstrumentType.
	 * @return A List of quotations that match the template.
	 * @throws Exception Quotation determination failed.
	 */
	private List<Quotation> getQuotationsByTemplate(final ScanTemplate scanTemplate, final InstrumentType instrumentType) throws Exception {
		if(scanTemplate == null)
			return this.quotationDAO.getRecentQuotations(instrumentType);
		else
			return this.quotationDAO.getQuotationsByTemplate(scanTemplate, instrumentType);
	}
}
