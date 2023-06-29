package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.chart.ChartObjectDAO;
import backend.dao.instrument.InstrumentDAO;
import backend.model.chart.HorizontalLine;
import backend.model.chart.HorizontalLineArray;
import backend.model.chart.HorizontalLineWS;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Common implementation of the chart object WebService that can be used by multiple service interfaces like SOAP or REST.
 * This service provides functions to manage objects like lines that can be drawn onto charts.
 * 
 * @author Michael
 */
public class ChartObjectService {
	/**
	 * DAO to access chart object data.
	 */
	private ChartObjectDAO chartObjectDAO;
	
	/**
	 * DAO for Instrument access.
	 */
	private InstrumentDAO instrumentDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(PriceAlertService.class);
	
	
	/**
	 * Initializes the ChartObjectService.
	 */
	public ChartObjectService() {
		this.chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
	}
	
	
	/**
	 * Provides the HorizontalLine with the given id.
	 * 
	 * @param id The id of the HorizontalLine.
	 * @return The HorizontalLine with the given id, if found.
	 */
	public WebServiceResult getHorizontalLine(final Integer id) {
		HorizontalLine horizontalLine = null;
		WebServiceResult getHorizontalLineResult = new WebServiceResult(null);
		
		try {
			horizontalLine = this.chartObjectDAO.getHorizontalLine(id);
			
			if(horizontalLine != null) {
				//HorizontalLine found
				getHorizontalLineResult.setData(horizontalLine);
			}
			else {
				//HorizontalLine not found
				getHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("horizontalLine.notFound"), id)));
			}
		}
		catch (Exception e) {
			getHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("horizontalLine.getHorizontalLineError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("horizontalLine.getHorizontalLineError"), id), e);
		}
		
		return getHorizontalLineResult;
	}
	
	
	/**
	 * Provides a list of all horizontal lines.
	 * The Instrument id can be given optionally to only get horizontal lines of a certain Instrument.
	 * 
	 * @param instrumentId The Instrument id (can be null)
	 * @return A list of all horizontal lines.
	 */
	public WebServiceResult getHorizontalLines(final Integer instrumentId) {
		HorizontalLineArray horizontalLines = new HorizontalLineArray();
		WebServiceResult getHorizontalLinesResult = new WebServiceResult(null);
		
		try {
			horizontalLines.setHorizontalLines(this.chartObjectDAO.getHorizontalLines(instrumentId));
			getHorizontalLinesResult.setData(horizontalLines);
		} catch (Exception e) {
			getHorizontalLinesResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("horizontalLine.getHorizontalLinesError")));
			
			logger.error(this.resources.getString("horizontalLine.getHorizontalLinesError"), e);
		}
		
		return getHorizontalLinesResult;
	}
	
	
	/**
	 * Deletes the HorizontalLine with the given id.
	 * 
	 * @param id The id of the HorizontalLine to be deleted.
	 * @return The result of the delete function.
	 */
	public WebServiceResult deleteHorizontalLine(final Integer id) {
		WebServiceResult deleteHorizontalLineResult = new WebServiceResult(null);
		HorizontalLine horizontalLine = null;
		
		//Check if a HorizontalLine with the given id exists.
		try {
			horizontalLine = this.chartObjectDAO.getHorizontalLine(id);
			
			if(horizontalLine != null) {
				//Delete HorizontalLine if exists.
				this.chartObjectDAO.deleteHorizontalLine(horizontalLine);
				deleteHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
						MessageFormat.format(this.resources.getString("horizontalLine.deleteSuccess"), id)));
			}
			else {
				//HorizontalLine not found.
				deleteHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("horizontalLine.notFound"), id)));
			}
		}
		catch (Exception e) {
			deleteHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("horizontalLine.deleteError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("horizontalLine.deleteError"), id), e);
		}
		
		return deleteHorizontalLineResult;
	}
	
	
	/**
	 * Updates an existing HorizontalLine.
	 * 
	 * @param horizontalLine The HorizontalLine to be updated.
	 * @return The result of the update function.
	 */
	public WebServiceResult updateHorizontalLine(final HorizontalLineWS horizontalLine) {
		HorizontalLine convertedHorizontalLine;
		WebServiceResult updateHorizontalLineResult = new WebServiceResult(null);
		
		//Convert the WebService data transfer object to the internal data model.
		try {
			convertedHorizontalLine = this.convertHorizontalLine(horizontalLine);
		}
		catch(Exception exception) {
			updateHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("horizontalLine.updateError")));	
			logger.error(this.resources.getString("horizontalLine.updateError"), exception);
			return updateHorizontalLineResult;
		}
		
		//Validation of the given HorizontalLine.
		this.validateHorizontalLine(convertedHorizontalLine, updateHorizontalLineResult);
		if(WebServiceTools.resultContainsErrorMessage(updateHorizontalLineResult))
			return updateHorizontalLineResult;
		
		//Update HorizontalLine if validation is successful.
		try {
			this.chartObjectDAO.updateHorizontalLine(convertedHorizontalLine);
			updateHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
					MessageFormat.format(this.resources.getString("horizontalLine.updateSuccess"), convertedHorizontalLine.getId())));
		}
		catch(ObjectUnchangedException objectUnchangedException) {
			updateHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
					MessageFormat.format(this.resources.getString("horizontalLine.updateUnchanged"), convertedHorizontalLine.getId())));
		}
		catch (Exception e) {
			updateHorizontalLineResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
					MessageFormat.format(this.resources.getString("horizontalLine.updateError"), convertedHorizontalLine.getId())));
			
			logger.error(MessageFormat.format(this.resources.getString("horizontalLine.updateError"), convertedHorizontalLine.getId()), e);
		}
		
		return updateHorizontalLineResult;
	}
	
	
	/**
	 * Converts the lean HorizontalLine representation that is provided by the WebService to the internal data model for further processing.
	 * 
	 * @param horizontalLineWS The lean HorizontalLine representation provided by the WebService.
	 * @return The HorizontalLine model that is used by the backend internally.
	 * @throws Exception In case the conversion fails.
	 */
	private HorizontalLine convertHorizontalLine(final HorizontalLineWS horizontalLineWS) throws Exception {
		HorizontalLine horizontalLine = new HorizontalLine();
		
		//Simple object attributes.
		horizontalLine.setId(horizontalLineWS.getId());
		horizontalLine.setPrice(horizontalLineWS.getPrice());
		
		//Convert Instrument ID to Instrument object.
		if(horizontalLineWS.getInstrumentId() != null)
			horizontalLine.setInstrument(this.instrumentDAO.getInstrument(horizontalLineWS.getInstrumentId()));
		
		return horizontalLine;
	}
	
	
	/**
	 * Validates the given HorizontalLine and adds potential error messages to the given WebServiceResult.
	 * 
	 * @param horizontalLine The HorizontalLine to be validated.
	 * @param webServiceResult The WebServiceResult containing potential validation error messages.
	 */
	private void validateHorizontalLine(final HorizontalLine horizontalLine, WebServiceResult webServiceResult) {
		try {
			horizontalLine.validate();
		}
		catch(Exception validationException) {
			webServiceResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
		}
	}
}
