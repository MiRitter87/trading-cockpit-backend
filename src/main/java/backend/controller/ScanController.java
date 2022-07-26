package backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.scan.ScanDAO;
import backend.model.scan.Scan;
import backend.model.scan.ScanStatus;

/**
 * Controls the whole scan process.
 * 
 * @author Michael
 */
public class ScanController {
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(ScanController.class);
	
	
	/**
	 * Checks if the execution of a scan is requested.
	 * Starts execution if necessary.
	 * 
	 * @param scan The scan being updated.
	 * @param databaseScan The database state of the scan before the update has been performed.
	 */
	public void checkAndExecute(final Scan scan, final Scan databaseScan) {
		if(scan.getStatus() == ScanStatus.IN_PROGRESS && databaseScan.getStatus() == ScanStatus.FINISHED)
			this.execute(scan);			
	}
	
	/**
	 * Executes the given scan.
	 * 
	 * @param scan The scan to be executed.
	 */
	public void execute(final Scan scan) {		
		//TODO The whole scan process should be performed in a thread to prevent blocking of the calling method.
		ScanDAO scanDAO = DAOManager.getInstance().getScanDAO();
		
		logger.info("Starting execution of scan with ID: " +scan.getId());
		
		//1. Determine all instruments that are part of the scan.
		//2. Start thread with instruments
		//...
		
		logger.info("Finished execution of scan with ID: " +scan.getId());
		
		//Finally the status of the scan has to be set to FINISHED again.
		scan.setStatus(ScanStatus.FINISHED);
		
		try {
			scanDAO.updateScan(scan);
		} catch (ObjectUnchangedException e) {
			logger.error("The scan was executed although being in status 'FINISHED'.", e);
		} catch (Exception e) {
			logger.error("Failed to update scan status at the end of the scan process.", e);
		}
	}
}
