package backend.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.instrument.QuotationDAO;
import backend.dao.instrument.QuotationYahooDAO;
import backend.dao.scan.ScanDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.list.List;
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
	 * DAO for instrument persistence.
	 */
	InstrumentDAO instrumentDAO;
	
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
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
	}
	
	
	/**
	 * The main method of the thread that is executed.
	 */
	public void run() {
		Set<Instrument> instruments;
		Iterator<Instrument> instrumentIterator;
		Instrument instrument;
		
		logger.info("Starting execution of scan with ID: " +this.scan.getId());
		
		instruments = this.getInstrumentsOfScan(this.scan);
		instrumentIterator = instruments.iterator();
		
		while(instrumentIterator.hasNext()) {
			instrument = instrumentIterator.next();
			this.updateQuotationsOfInstrument(instrument);
			
			try {
				sleep(this.queryInterval * 1000);
			} catch (InterruptedException e) {
				logger.info("Sleeping scan thread has been interrupted.", e);
			}
		}
		
		this.setScanStatusFinished();
		logger.info("Finished execution of scan with ID: " +this.scan.getId());
	}
	
	
	/**
	 * Provides all instruments that are contained in the lists of the given scan.
	 * 
	 * @param scan The scan of which the instruments are extracted.
	 * @return The
	 */
	private Set<Instrument> getInstrumentsOfScan(final Scan scan) {
		Set<Instrument> instruments = new HashSet<>();
		Iterator<List> listIterator = scan.getLists().iterator();
		List tempList;
		
		while(listIterator.hasNext()) {
			tempList = listIterator.next();
			instruments.addAll(tempList.getInstruments());			
		}
		
		return instruments;
	}
	
	
	/**
	 * Queries a third party WebService to get historical quotations of the given instrument.
	 * Updates the instrument if new quotations are given.
	 * 
	 * @param instrument The Instrument to be updated.
	 */
	private void updateQuotationsOfInstrument(Instrument instrument) {
		Quotation databaseQuotation;
		boolean newQuotationsAdded = false;
		
		try {
			java.util.List<Quotation> wsQuotations = this.quotationDAO.getQuotationHistory(instrument.getSymbol(), instrument.getStockExchange(), 1);
			
			for(Quotation wsQuotation:wsQuotations) {
				databaseQuotation = instrument.getQuotationByDate(wsQuotation.getDate());
				
				if(databaseQuotation == null) {
					instrument.addQuotation(wsQuotation);
					newQuotationsAdded = true;
				}
			}
			
			if(newQuotationsAdded) {
				this.instrumentDAO.updateInstrument(instrument);
			}
		} catch (Exception e) {
			logger.error("Failed to update quotations of instrument with ID " +instrument.getId(), e);
		}
	}
	
	
	/**
	 * Sets the status of the scan to 'FINISHED'.
	 */
	private void setScanStatusFinished() {
		try {
			this.scan.setStatus(ScanStatus.FINISHED);			
			this.scanDAO.updateScan(this.scan);
		} catch (ObjectUnchangedException e) {
			logger.error("The scan was executed although being already in status 'FINISHED'.", e);
		} catch (Exception e) {
			logger.error("Failed to update scan status at the end of the scan process.", e);
		}
	}
}