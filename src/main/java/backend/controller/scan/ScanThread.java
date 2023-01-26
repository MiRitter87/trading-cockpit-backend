package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.DataProvider;
import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.QuotationDAO;
import backend.dao.quotation.QuotationProviderDAO;
import backend.dao.quotation.QuotationProviderDAOFactory;
import backend.dao.scan.ScanDAO;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.list.List;
import backend.model.scan.Scan;
import backend.model.scan.ScanCompletionStatus;
import backend.model.scan.ScanExecutionStatus;

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
	 * Indication to only scan incomplete instruments of the scan.
	 */
	private boolean scanOnlyIncompleteInstruments;
	
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
	 * Controller for statistical data.
	 */
	StatisticCalculationController statisticCalculationController;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(ScanThread.class);
	
	
	/**
	 * Initializes the scan thread.
	 * 
	 * @param queryInterval The interval in seconds between each historical quotation query.
	 * @param dataProvider The DataProvider for historical quotation data.
	 * @param scan The scan that is executed by the thread.
	 * @param scanOnlyIncompleteInstruments Indication to only scan incomplete instruments of the scan.
	 */
	public ScanThread(final int queryInterval, final DataProvider dataProvider, final Scan scan, final boolean scanOnlyIncompleteInstruments) {
		this.queryInterval = queryInterval;
		this.scan = scan;
		this.scanOnlyIncompleteInstruments = scanOnlyIncompleteInstruments;
		
		this.quotationProviderDAO = QuotationProviderDAOFactory.getQuotationProviderDAO(dataProvider);
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
		this.scanDAO = DAOManager.getInstance().getScanDAO();
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
		
		this.indicatorCalculator = new IndicatorCalculator();
		this.statisticCalculationController = new StatisticCalculationController();
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
		
		instruments = this.getInstrumentsOfScan();
		instrumentIterator = instruments.iterator();
		
		while(instrumentIterator.hasNext()) {
			instrument = instrumentIterator.next();
			this.updateInstrument(instrument);
			
			instrumentsProcessed++;
			this.updateScanProgress(instrumentsProcessed, instruments.size());
			
			try {
				sleep(this.queryInterval * 1000);
			} catch (InterruptedException e) {
				logger.info("Sleeping scan thread has been interrupted.", e);
			}
		}
		
		this.updateRSNumbers();
		this.updateStatistics();
		this.setScanToFinished();
		logger.info("Finished execution of scan with ID: " + this.scan.getId());
	}
	
	
	/**
	 * Provides all instruments that are to be updated during the current scan run.
	 * 
	 * @return The instruments for the current scan run.
	 */
	private Set<Instrument> getInstrumentsOfScan() {
		if(this.scanOnlyIncompleteInstruments)
			return this.getIncompleteInstrumentsFromScan();	
		else
			return this.getInstrumentsFromScanLists();
	}
	
	
	/**
	 * Provides all instruments based on the lists that are defined in the current scan.
	 * 
	 * @return All instruments based on the lists that are defined in the current scan.
	 */
	private Set<Instrument> getInstrumentsFromScanLists() {
		Set<Instrument> instruments = new HashSet<>();
		Iterator<List> listIterator = this.scan.getLists().iterator();
		List tempList;
		
		while(listIterator.hasNext()) {
			tempList = listIterator.next();
			instruments.addAll(tempList.getInstruments());			
		}
		
		return instruments;
	}
	
	
	/**
	 * Provides all incomplete instruments defined in the current scan.
	 * 
	 * @return All incomplete instruments defined in the current scan.
	 */
	private Set<Instrument> getIncompleteInstrumentsFromScan() {
		return this.scan.getIncompleteInstruments();
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
		java.util.List<Quotation> databaseQuotations = new ArrayList<>();
		java.util.List<Quotation> newQuotations = new ArrayList<>();
		Set<Quotation> obsoleteQuotations = new HashSet<>();
		
		try {
			databaseQuotations.addAll(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
			instrument.setQuotations(databaseQuotations);
			java.util.List<Quotation> wsQuotations = 
					this.quotationProviderDAO.getQuotationHistory(instrument.getSymbol(), instrument.getStockExchange(), instrument.getType(), 1);
			
			for(Quotation wsQuotation:wsQuotations) {
				obsoleteQuotations.addAll(instrument.getOlderQuotationsOfSameDay(wsQuotation.getDate()));
				databaseQuotation = instrument.getQuotationByDate(wsQuotation.getDate());
				
				if(databaseQuotation == null) {
					wsQuotation.setInstrument(instrument);
					newQuotations.add(wsQuotation);
				}
			}
			
			if(newQuotations.size() > 0) 
				this.quotationDAO.insertQuotations(newQuotations);
			
			if(obsoleteQuotations.size() > 0)
				this.quotationDAO.deleteQuotations(new ArrayList<>(obsoleteQuotations));
			
			this.checkAgeOfNewestQuotation(instrument.getSymbol(), wsQuotations, 5);
			
			this.scan.getIncompleteInstruments().remove(instrument);
		} catch (Exception e) {
			this.scan.addIncompleteInstrument(instrument);			
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
		java.util.List<Quotation> databaseQuotations = new ArrayList<>();
		Quotation quotation;
		
		try {
			//Read quotations of Instrument from database to get quotations with IDs needed for setting the Indicator ID.
			databaseQuotations.addAll(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
			instrument.setQuotations(databaseQuotations);
			sortedQuotations = instrument.getQuotationsSortedByDate();
			
			if(sortedQuotations.size() == 0)
				return;
			
			for(int i = 0; i < sortedQuotations.size(); i++) {
				quotation = sortedQuotations.get(i);
				
				if(i == 0)
					quotation = this.calculateIndicators(instrument, quotation, true);
				else
					quotation = this.calculateIndicators(instrument, quotation, false);
				
				modifiedQuotations.add(quotation);
			}
			
			this.quotationDAO.updateQuotations(modifiedQuotations);
		}
		catch(Exception exception) {
			logger.error("Failed to retrieve or update indicators of instrument with ID " +instrument.getId(), exception);
		}
	}
	
	
	/**
	 * Calculates indicators for the given Quotation.
	 * 
	 * @param instrument The Instrument to which the Quotation belongs to.
	 * @param quotation The Quotation for which indicators are calculated.
	 * @param mostRecent Calculates all indicators if true (the most recent Quotation). Calculates only indicators relevant for history if false.
	 * @return The Quotation with the calculated indicators.
	 */
	private Quotation calculateIndicators(final Instrument instrument, final Quotation quotation, boolean mostRecent) {
		java.util.List<Quotation> sortedQuotations = instrument.getQuotationsSortedByDate();
		Indicator indicator;
		
		if(quotation.getIndicator() == null)
			indicator = new Indicator();
		else
			indicator = quotation.getIndicator();
		
		if(mostRecent) {
			//These indicators are calculated only for the most recent Quotation.
			indicator.setRsPercentSum(this.indicatorCalculator.getRSPercentSum(instrument, quotation));
			indicator.setSma50(this.indicatorCalculator.getSimpleMovingAverage(50, quotation, sortedQuotations));
			indicator.setSma150(this.indicatorCalculator.getSimpleMovingAverage(150, quotation, sortedQuotations));
			indicator.setSma200(this.indicatorCalculator.getSimpleMovingAverage(200, quotation, sortedQuotations));
			indicator.setDistanceTo52WeekHigh(this.indicatorCalculator.getDistanceTo52WeekHigh(quotation, sortedQuotations));
			indicator.setDistanceTo52WeekLow(this.indicatorCalculator.getDistanceTo52WeekLow(quotation, sortedQuotations));
			indicator.setBollingerBandWidth(this.indicatorCalculator.getBollingerBandWidth(10, 2, quotation, sortedQuotations));
			indicator.setVolumeDifferential5Days(this.indicatorCalculator.getVolumeDifferential(30, 5, quotation, sortedQuotations));
			indicator.setVolumeDifferential10Days(this.indicatorCalculator.getVolumeDifferential(30, 10, quotation, sortedQuotations));
			indicator.setBaseLengthWeeks(this.indicatorCalculator.getBaseLengthWeeks(quotation, sortedQuotations));
			indicator.setUpDownVolumeRatio(this.indicatorCalculator.getUpDownVolumeRatio(50, quotation, sortedQuotations));
			indicator.setPerformance5Days(this.indicatorCalculator.getPricePerformanceForDays(quotation, sortedQuotations, 5));
			indicator.setLiquidity20Days(this.indicatorCalculator.getLiquidityForDays(quotation, sortedQuotations, 20));
		}
		else {
			//These indicators are calculated for historical quotations.
			indicator.setSma50(this.indicatorCalculator.getSimpleMovingAverage(50, quotation, sortedQuotations));
		}
		
		quotation.setIndicator(indicator);
		
		return quotation;
	}
	
	
	/**
	 * Updates the RS number of all instruments that have quotations and an Indicator for the most recent date defined.
	 * Separate RS numbers are calculated based on the InstrumentType.
	 */
	private void updateRSNumbers() {
		try {
			java.util.List<Quotation> allQuotations = new ArrayList<>();
			java.util.List<Quotation> quotationsTypeStock = this.quotationDAO.getRecentQuotations(InstrumentType.STOCK);
			java.util.List<Quotation> quotationsTypeETF = this.quotationDAO.getRecentQuotations(InstrumentType.ETF);
			java.util.List<Quotation> quotationsTypeSector = this.quotationDAO.getRecentQuotations(InstrumentType.SECTOR);
			java.util.List<Quotation> quotationsTypeIndustryGroup = this.quotationDAO.getRecentQuotations(InstrumentType.IND_GROUP);

			this.indicatorCalculator.calculateRsNumbers(quotationsTypeStock);
			this.indicatorCalculator.calculateRsNumbers(quotationsTypeETF);
			this.indicatorCalculator.calculateRsNumbers(quotationsTypeSector);
			this.indicatorCalculator.calculateRsNumbers(quotationsTypeIndustryGroup);
			
			allQuotations.addAll(quotationsTypeStock);
			allQuotations.addAll(quotationsTypeETF);
			allQuotations.addAll(quotationsTypeSector);
			allQuotations.addAll(quotationsTypeIndustryGroup);
			this.quotationDAO.updateQuotations(allQuotations);
		} catch (Exception e) {
			logger.error("Failed to calculate RS numbers.", e);
		}
	}
	
	
	/**
	 * Updates the status field 'progress' of the running scan.
	 * 
	 * @param numberOfInstrumentsCompleted The number of instruments that already have been scanned.
	 * @param totalNumberOfInstruments The total number of instruments of the scan.
	 */
	private void updateScanProgress(final int numberOfInstrumentsCompleted, final int totalNumberOfInstruments) {
		BigDecimal progress, instrumentsCompleted, numberOfInstruments;
		int roundedProgress = 0;
		
		instrumentsCompleted = BigDecimal.valueOf(numberOfInstrumentsCompleted);
		numberOfInstruments = BigDecimal.valueOf(totalNumberOfInstruments);
		
		progress = instrumentsCompleted.divide(numberOfInstruments, 2, RoundingMode.HALF_UP);
		progress = progress.multiply(BigDecimal.valueOf(100));
		roundedProgress = progress.intValue();
		
		if(this.scan.getProgress().equals(roundedProgress)) {
			return;
		}
		
		try {			
			this.scan.setProgress(roundedProgress);
			this.scanDAO.updateScan(this.scan);
		} catch (ObjectUnchangedException e) {
			logger.error("Tried to update 'progress' in scan process but value did not change.", e);
		} catch (Exception e) {
			logger.error("Failed to update 'progress' of scan during scan process.", e);
		}			
	}
	
	
	/**
	 * Sets the status of the scan to 'FINISHED' and updates the date of the last scan.
	 */
	private void setScanToFinished() {
		try {
			this.scan.setLastScan(new Date());
			this.scan.setExecutionStatus(ScanExecutionStatus.FINISHED);
			
			if(this.scan.getIncompleteInstruments().size() == 0)
				this.scan.setCompletionStatus(ScanCompletionStatus.COMPLETE);
			else
				this.scan.setCompletionStatus(ScanCompletionStatus.INCOMPLETE);
			
			this.scanDAO.updateScan(this.scan);
		} catch (ObjectUnchangedException e) {
			logger.error("The scan was executed although being already in status 'FINISHED'.", e);
		} catch (Exception e) {
			logger.error("Failed to update scan status at the end of the scan process.", e);
		}
	}
	
	
	/**
	 * Updates the statistics.
	 */
	private void updateStatistics() {
		try {
			this.statisticCalculationController.updateStatistic();
		} catch (Exception e) {
			logger.error("Failed to update the statistics.", e);
		}
	}
	
	
	/**
	 * Checks the age of the newest Quotation.
	 * Logs a message if the newest Quotation is older than dayThreshold days.
	 * 
	 * @param symbol The symbol of the Instrument.
	 * @param quotations The quotations whose dates are checked.
	 * @param dayThreshold The threshold in days used to log.
	 */
	private void checkAgeOfNewestQuotation(final String symbol, final java.util.List<Quotation> quotations, final int dayThreshold) {
		Instrument instrument = new Instrument();
		long quotationAgeDays;
				
		instrument.setQuotations(quotations);
		quotationAgeDays = instrument.getAgeOfNewestQuotationInDays();
		
		if(quotationAgeDays >= dayThreshold)
			logger.warn(MessageFormat.format("The newest Quotation data of symbol {0} are {1} days old.", symbol, quotationAgeDays));
	}
}