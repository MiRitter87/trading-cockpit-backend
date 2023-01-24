package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.scan.ScanController;
import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.list.ListDAO;
import backend.dao.scan.ScanDAO;
import backend.dao.scan.ScanInProgressException;
import backend.model.list.List;
import backend.model.scan.Scan;
import backend.model.scan.ScanArray;
import backend.model.scan.ScanWS;
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
	 * DAO for list access.
	 */
	private ListDAO listDAO;
	
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
		this.listDAO = DAOManager.getInstance().getListDAO();
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
		
		//Check if a scan with the given id exists.
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
	public WebServiceResult updateScan(final ScanWS scan) {
		Scan convertedScan = new Scan();
		WebServiceResult updateScanResult = new WebServiceResult(null);
		
		//Convert the WebService data transfer object to the internal data model.
		try {
			convertedScan = this.convertScan(scan);
		}
		catch(Exception exception) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("scan.updateError")));	
			logger.error(this.resources.getString("scan.updateError"), exception);
			return updateScanResult;
		}
		
		//Validation of the given scan.
		try {
			convertedScan.validate();
		} catch (Exception validationException) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return updateScanResult;
		}
		
		//Update scan if validation is successful.
		updateScanResult = this.update(convertedScan);
		
		return updateScanResult;
	}
	
	
	/**
	 * Adds a scan.
	 * 
	 * @param scan The scan to be added.
	 * @return The result of the add function.
	 */
	public WebServiceResult addScan(final ScanWS scan) {
		Scan convertedScan = new Scan();
		WebServiceResult addScanResult = new WebServiceResult();
		
		//Convert the WebService data transfer object to the internal data model.
		try {
			convertedScan = this.convertScan(scan);
		}
		catch(Exception exception) {
			addScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("scan.addError")));	
			logger.error(this.resources.getString("scan.addError"), exception);
			return addScanResult;
		}
		
		//Validate the given scan.
		try {
			convertedScan.validate();
		} catch (Exception validationException) {
			addScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, validationException.getMessage()));
			return addScanResult;
		}
		
		//Insert scan if validation is successful.
		try {
			this.scanDAO.insertScan(convertedScan);
			addScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, this.resources.getString("scan.addSuccess")));
			addScanResult.setData(convertedScan.getId());
		} 
		catch (Exception e) {
			addScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, this.resources.getString("scan.addError")));
			logger.error(this.resources.getString("scan.addError"), e);
		}
		
		return addScanResult;
	}
	
	
	/**
	 * Converts the lean Scan representation that is provided by the WebService to the internal data model for further processing.
	 * 
	 * @param scanWS The lean Scan representation provided by the WebService.
	 * @return The Scan model that is used by the backend internally.
	 * @throws Exception In case the conversion fails.
	 */
	private Scan convertScan(final ScanWS scanWS) throws Exception {
		Scan scan = new Scan();
		List list;
		
		//Simple object attributes.
		scan.setId(scanWS.getId());
		scan.setName(scanWS.getName());
		scan.setDescription(scanWS.getDescription());
		scan.setExecutionStatus(scanWS.getExecutionStatus());
		scan.setCompletionStatus(scanWS.getCompletionStatus());
		scan.setPercentCompleted(scanWS.getPercentCompleted());
		scan.setLastScan(scanWS.getLastScan());
		
		//Convert the list IDs into list objects.
		for(Integer listId:scanWS.getListIds()) {
			list = this.listDAO.getList(listId);
			scan.addList(list);
		}
		
		return scan;
	}
	
	
	/**
	 * Updates the given scan.
	 * 
	 * @param scan The scan to be updated.
	 * @return The result of the update function.
	 */
	private WebServiceResult update(final Scan scan) {
		Scan databaseScan;
		ScanController scanController;
		WebServiceResult updateScanResult = new WebServiceResult();
		
		try {
			scanController = new ScanController();
			databaseScan = this.scanDAO.getScan(scan.getId());
			this.scanDAO.updateScan(scan);
			scanController.checkAndExecute(scan, databaseScan);
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.S, 
					MessageFormat.format(this.resources.getString("scan.updateSuccess"), scan.getId())));
		} 
		catch(ObjectUnchangedException objectUnchangedException) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
					MessageFormat.format(this.resources.getString("scan.updateUnchanged"), scan.getId())));
		}
		catch(ScanInProgressException scanInProgressException) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.I, 
					MessageFormat.format(this.resources.getString("scan.updateScansInProgressExist"), scanInProgressException.getScanId())));
		}
		catch (Exception e) {
			updateScanResult.addMessage(new WebServiceMessage(WebServiceMessageType.E, 
					MessageFormat.format(this.resources.getString("scan.updateError"), scan.getId())));
			
			logger.error(MessageFormat.format(this.resources.getString("scan.updateError"), scan.getId()), e);
		}
		
		return updateScanResult;
	}
}
