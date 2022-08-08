package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.QuotationDAO;
import backend.dao.quotation.QuotationYahooDAO;
import backend.dao.scan.ScanDAO;
import backend.model.instrument.Indicator;
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
	 * Indicator Calculator.
	 */
	IndicatorCalculator indicatorCalculator;
	
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
		
		this.indicatorCalculator = new IndicatorCalculator();
	}
	
	
	/**
	 * The main method of the thread that is executed.
	 */
	public void run() {
		Set<Instrument> instruments;
		Iterator<Instrument> instrumentIterator;
		Instrument instrument;
		int instrumentsProcessed = 0;
		
		logger.info("Starting execution of scan with ID: " +this.scan.getId());
		
		instruments = this.getInstrumentsOfScan(this.scan);
		instrumentIterator = instruments.iterator();
		
		while(instrumentIterator.hasNext()) {
			instrument = instrumentIterator.next();
			this.updateInstrument(instrument);
			
			instrumentsProcessed++;
			//this.updateScanPercentCompleted(instrumentsProcessed, instruments.size());
			
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
	 * Updates quotations and indicators of the given instrument.
	 * 
	 * @param instrument The instrument to be updated.
	 */
	private void updateInstrument(Instrument instrument) {
		this.updateQuotationsOfInstrument(instrument);
		this.updateIndicatorsOfInstrument(instrument.getId());
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
			
			if(newQuotationsAdded)
				this.persistInstrumentChanges(instrument);
		} catch (Exception e) {
			logger.error("Failed to retrieve quotations of instrument with ID " +instrument.getId(), e);
		}
	}
	
	
	/**
	 * Updates the indicators of the most recent quotation of the given Instrument.
	 * 
	 * @param instrumentId The id of the Instrument to be updated.
	 */
	private void updateIndicatorsOfInstrument(final Integer instrumentId) {
		Indicator indicator;
		java.util.List<Quotation> sortedQuotations;
		Quotation mostRecentQuotation;
		Instrument instrument;
		
		try {
			//Read instrument from database to get Quotations with IDs needed for setting Indicator ID.
			instrument = this.instrumentDAO.getInstrument(instrumentId, true);	
			sortedQuotations = instrument.getQuotationsSortedByDate();
			
			if(sortedQuotations.size() == 0)
				return;
			
			mostRecentQuotation = sortedQuotations.get(0);
			
			if(mostRecentQuotation.getIndicator() == null)
				indicator = new Indicator();
			else
				indicator = mostRecentQuotation.getIndicator();
			
			indicator.setRsPercentSum(this.indicatorCalculator.getRSPercentSum(instrument, mostRecentQuotation));
			mostRecentQuotation.setIndicator(indicator);
			
			this.persistInstrumentChanges(instrument);
		}
		catch(Exception exception) {
			logger.error("Failed to retrieve update indicators of instrument with ID " +instrumentId, exception);
		}
	}
	
	
	/**
	 * Persists changes of an instrument if data have changed.
	 * 
	 * @param instrument The instrument to persist.
	 */
	private void persistInstrumentChanges(final Instrument instrument) {
		try {
			this.instrumentDAO.updateInstrument(instrument, true);
		} catch (ObjectUnchangedException e) {
			logger.error("Scanner found new quotations or indicators of but database did not detect any changes in instrument ID: "
					+instrument.getId(), e);
		} catch (DuplicateInstrumentException e) {
			logger.error("Update would have resulted in duplicate instrument with ID: " +instrument.getId(), e);
		} catch (Exception e) {
			logger.error("Scanner failed to update instrument with ID: " +instrument.getId(), e);
		}
	}
	
	
	/**
	 * Updates the status field 'percentCompleted' of the running scan.
	 * 
	 * @param numberOfInstrumentsCompleted The number of instruments that already have been scanned.
	 * @param totalNumberOfInstruments The total number of instruments of the scan.
	 */
	private void updateScanPercentCompleted(final int numberOfInstrumentsCompleted, final int totalNumberOfInstruments) {
		BigDecimal percentCompleted, instrumentsCompleted, numberOfInstruments;
		int roundedPercentCompleted = 0;
		
		instrumentsCompleted = BigDecimal.valueOf(numberOfInstrumentsCompleted);
		numberOfInstruments = BigDecimal.valueOf(totalNumberOfInstruments);
		
		percentCompleted = instrumentsCompleted.divide(numberOfInstruments, 2, RoundingMode.HALF_UP);
		percentCompleted = percentCompleted.multiply(BigDecimal.valueOf(100));
		roundedPercentCompleted = percentCompleted.intValue();
		
		if(this.scan.getPercentCompleted().equals(roundedPercentCompleted)) {
			return;
		}
		
		try {			
			this.scan.setPercentCompleted(roundedPercentCompleted);
			this.scanDAO.updateScan(this.scan);
		} catch (ObjectUnchangedException e) {
			logger.error("Tried to update 'percentCompleted' in scan process but value did not change.", e);
		} catch (Exception e) {
			logger.error("Failed to update 'percentCompleted' of scan during scan process.", e);
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