package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentArray;
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
	 * Initializes the instrument service.
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
			instrument = this.instrumentDAO.getInstrument(id, false);
			
			if(instrument != null) {
				//Instrument found
				getInstrumentResult.setData(instrument);
			}
			else {
				//Instrument not found
				getInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("instrument.notFound"), id)));
			}
		}
		catch (Exception e) {
			getInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("instrument.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("instrument.getError"), id), e);
		}
		
		return getInstrumentResult;
	}
	
	
	/**
	 * Provides a list of all instruments.
	 * 
	 * @param withQuotations Load quotations of instruments, if set to true.
	 * @return A list of all instruments.
	 */
	public WebServiceResult getInstruments(final boolean withQuotations) {
		InstrumentArray instruments = new InstrumentArray();
		WebServiceResult getInstrumentsResult = new WebServiceResult(null);
		
		try {
			instruments.setInstruments(this.instrumentDAO.getInstruments(withQuotations));
			getInstrumentsResult.setData(instruments);
		} catch (Exception e) {
			getInstrumentsResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("instrument.getInstrumentsError")));
			
			logger.error(this.resources.getString("instrument.getInstrumentsError"), e);
		}
		
		return getInstrumentsResult;
	}
	
	
	/**
	 * Adds an instrument.
	 * 
	 * @param instrument The instrument to be added.
	 * @return The result of the add function.
	 */
	public WebServiceResult addInstrument(final Instrument instrument) {
		WebServiceResult addInstrumentResult = new WebServiceResult();
		
		//Validate the given instrument.
		try {
			instrument.validate();
		} catch (Exception validationException) {
			addInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return addInstrumentResult;
		}
		
		//Insert instrument if validation is successful.
		try {
			this.instrumentDAO.insertInstrument(instrument);
			addInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, this.resources.getString("instrument.addSuccess")));
			addInstrumentResult.setData(instrument.getId());
		} catch (DuplicateInstrumentException duplicateInstrumentException) {
			addInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("instrument.createDuplicate"), instrument.getSymbol(), instrument.getStockExchange())));
		} catch (Exception e) {
			addInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("instrument.addError")));
			logger.error(this.resources.getString("instrument.addError"), e);
		}
		
		return addInstrumentResult;
	}
	
	
	/**
	 * Deletes the instrument with the given id.
	 * 
	 * @param id The id of the instrument to be deleted.
	 * @return The result of the delete function.
	 */
	public WebServiceResult deleteInstrument(final Integer id) {
		WebServiceResult deleteInstrumentResult = new WebServiceResult(null);
		Instrument instrument = null;
		
		//Check if an instrument with the given id exists.
		try {
			instrument = this.instrumentDAO.getInstrument(id, true);
			
			if(instrument != null) {
				//Delete instrument if exists.
				this.instrumentDAO.deleteInstrument(instrument);
				deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
						MessageFormat.format(this.resources.getString("instrument.deleteSuccess"), id)));
			}
			else {
				//Instrument not found.
				deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("instrument.notFound"), id)));
			}
		}
		catch (Exception e) {
			deleteInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("instrument.deleteError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("instrument.deleteError"), id), e);
		}
		
		return deleteInstrumentResult;
	}
	
	
	/**
	 * Updates an existing instrument.
	 * 
	 * @param instrument The instrument to be updated.
	 * @return The result of the update function.
	 */
	public WebServiceResult updateInstrument(final Instrument instrument) {
		WebServiceResult updateInstrumentResult = new WebServiceResult(null);
		
		//Validation of the given instrument.
		try {
			instrument.validate();
		} catch (Exception validationException) {
			updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return updateInstrumentResult;
		}
		
		//Update instrument if validation is successful.
		try {
			this.instrumentDAO.updateInstrument(instrument);
			updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
					MessageFormat.format(this.resources.getString("instrument.updateSuccess"), instrument.getId())));
		} 
		catch(ObjectUnchangedException objectUnchangedException) {
			updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
					MessageFormat.format(this.resources.getString("instrument.updateUnchanged"), instrument.getId())));
		}
		catch (DuplicateInstrumentException duplicateInstrumentException) {
			updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("instrument.updateDuplicate"), instrument.getSymbol(), instrument.getStockExchange())));
		}
		catch (Exception e) {
			updateInstrumentResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
					MessageFormat.format(this.resources.getString("instrument.updateError"), instrument.getId())));
			
			logger.error(MessageFormat.format(this.resources.getString("instrument.updateError"), instrument.getId()), e);
		}
		
		return updateInstrumentResult;
	}
}
