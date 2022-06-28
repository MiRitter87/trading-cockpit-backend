package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.scan.ScanDAO;
import backend.model.scan.Scan;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the scan WebService that can be used by multiple service interfaces like SOAP or REST.
 * 
 * @author Michael
 */
public class ScanService {
	/**
	 * DAO for scan access.
	 */
	private ScanDAO scanDAO;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(ScanService.class);
	
	
	/**
	 * Initializes the scan service.
	 */
	public ScanService() {
		this.scanDAO = DAOManager.getInstance().getScanDAO();
	}
	
	
	/**
	 * Provides the scan with the given id.
	 * 
	 * @param id The id of the scan.
	 * @return The scan with the given id, if found.
	 */
	public WebServiceResult getScan(final Integer id) {
		Scan scan = null;
		WebServiceResult getScanResult = new WebServiceResult(null);
		
		try {
			scan = this.scanDAO.getScan(id);
			
			if(scan != null) {
				//Scan found
				getScanResult.setData(scan);
			}
			else {
				//List not found
//				getListResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
//						MessageFormat.format(this.resources.getString("list.notFound"), id)));
			}
		}
		catch (Exception e) {
			getScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("scan.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("scan.getError"), id), e);
		}
		
		return getScanResult;
	}
}
