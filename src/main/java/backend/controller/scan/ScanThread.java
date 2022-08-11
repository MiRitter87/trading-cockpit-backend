package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.QuotationDAO;
import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderYahooDAO;
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
	 * DAO to access quotations from a third-party data provider of quotation data.
	 */
	QuotationProviderDAO quotationProviderDAO;
	
	/**
	 * DAO to access quotations of the database.
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
		
		this.quotationProviderDAO = new QuotationProviderYahooDAO(new OkHttpClient());
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
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
			this.updateScanPercentCompleted(instrumentsProcessed, instruments.size());
			
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
		this.updateIndicatorsOfInstrument(instrument);
	}
	
	
	/**
	 * Queries a third party WebService to get historical quotations of the given instrument.
	 * Persists new quotations.
	 * 
	 * @param instrument The Instrument to be updated.
	 */
	private void updateQuotationsOfInstrument(Instrument instrument) {
		Quotation databaseQuotation;
		Set<Quotation> databaseQuotations = new HashSet<>();
		java.util.List<Quotation> newQuotations = new ArrayList<>();
		
		try {
			databaseQuotations.addAll(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
			instrument.setQuotations(databaseQuotations);
			java.util.List<Quotation> wsQuotations = this.quotationProviderDAO.getQuotationHistory(instrument.getSymbol(), instrument.getStockExchange(), 1);
			
			for(Quotation wsQuotation:wsQuotations) {
				databaseQuotation = instrument.getQuotationByDate(wsQuotation.getDate());
				
				if(databaseQuotation == null) {
					newQuotations.add(wsQuotation);
					instrument.addQuotation(wsQuotation);
				}
			}
			
			if(newQuotations.size() > 0) {
				this.quotationDAO.insertQuotations(newQuotations);
			}
		} catch (Exception e) {
			logger.error("Failed to update quotations of instrument with ID " +instrument.getId(), e);
		}
	}
	
	
	/**
	 * Updates the indicators of the most recent quotation of the given Instrument.
	 * 
	 * @param instrument The Instrument to be updated.
	 */
	private void updateIndicatorsOfInstrument(Instrument instrument) {
		java.util.List<Quotation> sortedQuotations;
		java.util.List<Quotation> modifiedQuotations = new ArrayList<>();
		Set<Quotation> databaseQuotations = new HashSet<>();
		Indicator indicator;
		Quotation mostRecentQuotation;
		
		try {
			//Read quotations of Instrument from database to get quotations with IDs needed for setting the Indicator ID.
			databaseQuotations.addAll(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
			instrument.setQuotations(databaseQuotations);
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
			
			modifiedQuotations.add(mostRecentQuotation);
			this.quotationDAO.updateQuotations(modifiedQuotations);
		}
		catch(Exception exception) {
			logger.error("Failed to retrieve or update indicators of instrument with ID " +instrument.getId(), exception);
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