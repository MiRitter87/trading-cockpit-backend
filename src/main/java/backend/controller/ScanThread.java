package backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.QuotationDAO;
import backend.dao.instrument.QuotationYahooDAO;
import backend.dao.scan.ScanDAO;
import backend.model.scan.Scan;
import backend.model.scan.ScanStatus;
import okhttp3.OkHttpClient;

/**
 * Queries historical stock quotes of instruments that are part of a scan.
 * Furthermore calculates indicators. 
 * 
 * @author Michael
 */
public class ScanThread extends Thread {
	/**
	 * The interval in seconds between queries of historical quotations.
	 */
	private int queryInterval;
	
	/**
	 * The scan that is executed.
	 */
	private Scan scan;
	
	/**
	 * DAO to access quotations.
	 */
	QuotationDAO quotationDAO;
	
	/**
	 * DAO for scan persistence.
	 */
	ScanDAO scanDAO;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(ScanThread.class);
	
	
	/**
	 * Initializes the scan thread.
	 * 
	 * @param queryInterval The interval in seconds between each historical quotation query.
	 * @param scan The scan that is executed by the thread.
	 */
	public ScanThread(final int queryInterval, final Scan scan) {
		this.queryInterval = queryInterval;
		this.scan = scan;
		
		this.quotationDAO = new QuotationYahooDAO(new OkHttpClient());
		this.scanDAO = DAOManager.getInstance().getScanDAO();
	}
	
	
	/**
	 * The main method of the thread that is executed.
	 */
	public void run() {
		logger.info("Starting execution of scan with ID: " +this.scan.getId());
		
		//1. Determine all instruments that are part of the scan.
		//2. Query quotations of instrument.
		//...
		
		logger.info("Finished execution of scan with ID: " +this.scan.getId());
		
		try {
			//Finally the status of the scan has to be set to FINISHED again.
			this.scan.setStatus(ScanStatus.FINISHED);
			
			this.scanDAO.updateScan(this.scan);
		} catch (ObjectUnchangedException e) {
			logger.error("The scan was executed although being already in status 'FINISHED'.", e);
		} catch (Exception e) {
			logger.error("Failed to update scan status at the end of the scan process.", e);
		}
	}
}
