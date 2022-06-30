package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.scan.ScanDAO;
import backend.model.scan.Scan;
import backend.model.scan.ScanArray;
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
				//Scan not found
				getScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("scan.notFound"), id)));
			}
		}
		catch (Exception e) {
			getScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("scan.getError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("scan.getError"), id), e);
		}
		
		return getScanResult;
	}
	
	
	/**
	 * Provides a list of all scans.
	 * 
	 * @return A list of all scans.
	 */
	public WebServiceResult getScans() {
		ScanArray scans = new ScanArray();
		WebServiceResult getScansResult = new WebServiceResult(null);
		
		try {
			scans.setScans(scanDAO.getScans());
			getScansResult.setData(scans);
		} catch (Exception e) {
			getScansResult.addMessage(new WebServiceMessage(
					WebServiceMessageType.E, this.resources.getString("scan.getScansError")));
			
			logger.error(this.resources.getString("scan.getScansError"), e);
		}
		
		return getScansResult;
	}
	
	
	/**
	 * Deletes the scan with the given id.
	 * 
	 * @param id The id of the scan to be deleted.
	 * @return The result of the delete function.
	 */
	public WebServiceResult deleteScan(final Integer id) {
		WebServiceResult deleteScanResult = new WebServiceResult(null);
		Scan scan = null;
		
		//Check if a list with the given id exists.
		try {
			scan = this.scanDAO.getScan(id);
			
			if(scan != null) {
				//Delete scan if exists.
				this.scanDAO.deleteScan(scan);
				deleteScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
						MessageFormat.format(this.resources.getString("scan.deleteSuccess"), id)));
			}
			else {
				//Scan not found.
				deleteScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
						MessageFormat.format(this.resources.getString("scan.notFound"), id)));
			}
		}
		catch (Exception e) {
			deleteScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
					MessageFormat.format(this.resources.getString("scan.deleteError"), id)));
			
			logger.error(MessageFormat.format(this.resources.getString("scan.deleteError"), id), e);
		}
		
		return deleteScanResult;
	}
	
	
	/**
	 * Updates an existing scan.
	 * 
	 * @param scan The scan to be updated.
	 * @return The result of the update function.
	 */
	public WebServiceResult updateScan(final Scan scan) {
		WebServiceResult updateScanResult = new WebServiceResult(null);
		
		//Validation of the given list.
		try {
			scan.validate();
		} catch (Exception validationException) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return updateScanResult;
		}
		
		//Update scan if validation is successful.
		try {
			this.scanDAO.updateScan(scan);
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
					MessageFormat.format(this.resources.getString("scan.updateSuccess"), scan.getId())));
		} 
//		catch(ObjectUnchangedException objectUnchangedException) {
//			updateListResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
//					MessageFormat.format(this.resources.getString("list.updateUnchanged"), list.getId())));
//		}
		catch (Exception e) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
					MessageFormat.format(this.resources.getString("scan.updateError"), scan.getId())));
			
			logger.error(MessageFormat.format(this.resources.getString("scan.updateError"), scan.getId()), e);
		}
		
		return updateScanResult;
	}
}
