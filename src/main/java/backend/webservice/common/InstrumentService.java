package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.instrument.Instrument;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the instrument WebService that can be used by multiple service interfaces like SOAP or REST.
 * 
 * @author Michael
 */
public class InstrumentService {
	/**
	 * DAO for instrument access.
	 */
	private InstrumentDAO instrumentDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(InstrumentService.class);
	
	
	/**
	 * Initializes the instrumnet service.
	 */
	public InstrumentService() {
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
	}
	
	
	/**
	 * Provides the instrument with the given id.
	 * 
	 * @param id The id of the instrument.
	 * @return The instrument with the given id, if found.
	 */
	public WebServiceResult getInstrument(final Integer id) {
		Instrument instrument = null;
		WebServiceResult getInstrumentResult = new WebServiceResult(null);
		
		try {
			instrument = this.instrumentDAO.getInstrument(id);
			
			if(instrument != null) {
				//Instrument found
				getInstrumentResult.setData(instrument);
			}
			else {
//				//Price Alert not found
//				getPriceAlertResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
//						MessageFormat.format(this.resources.getString("priceAlert.notFound"), id)));
			}
		}
		catch (Exception e) {
			getInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("instrument.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("instrument.getError"), id), e);
		}
		
		return getInstrumentResult;
	}
}
