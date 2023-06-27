package backend.webservice.common;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;

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
}
