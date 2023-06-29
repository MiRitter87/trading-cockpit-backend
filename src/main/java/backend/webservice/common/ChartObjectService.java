package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;
import backend.model.chart.HorizontalLine;
import backend.model.chart.HorizontalLineArray;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

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
}
