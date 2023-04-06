package backend.controller.instrumentCheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.dao.DAOManager;
import backend.dao.quotation.QuotationDAO;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks.
 * 
 * @author Michael
 */
public class InstrumentCheckController {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * DAO to access Quotation data of Instrument.
	 */
	private QuotationDAO quotationDAO;
	
	/**
	 * Controller used for counting-related Instrument health checks.
	 */
	private InstrumentCheckCountingController instrumentCheckCountingController;
	
	/**
	 * Controller used to detect extreme daily price and volume behavior of an Instrument.
	 */
	private InstrumentCheckExtremumController instrumentCheckExtremumController;
	
	/**
	 * Controller used to detect price and volume patterns of an Instrument.
	 */
	private InstrumentCheckPatternController instrumentCheckPatternController;
	
	
	/**
	 * Default constructor.
	 */
	public InstrumentCheckController() {
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
		this.instrumentCheckCountingController = new InstrumentCheckCountingController();
		this.instrumentCheckExtremumController = new InstrumentCheckExtremumController();
		this.instrumentCheckPatternController = new InstrumentCheckPatternController();
	}
	
	
	/**
	 * Checks the health of the given Instrument beginning at the given start date.
	 * 
	 * @param instrumentId The id of the Instrument.
	 * @param startDate The start date of the health check.
	 * @return A protocol containing the health information from the start date until the most recent quotation.
	 * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
	 * @throws Exception Health check failed.
	 */
	public Protocol checkInstrument(final Integer instrumentId, final Date startDate) throws NoQuotationsExistException, Exception {
		QuotationArray quotations = new QuotationArray();
		Protocol protocol = new Protocol();
		
		quotations.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
		quotations.sortQuotationsByDate();
		this.checkQuotationsExistAfterStartDate(startDate, quotations);
		
		//Confirmations
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreUpThanDownDays(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreGoodThanBadCloses(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckPatternController.checkUpOnVolume(startDate, quotations));
		
		//Violations
		protocol.getProtocolEntries().addAll(this.checkCloseBelowSma50(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestDownDay(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreDownThanUpDays(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreBadThanGoodCloses(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckPatternController.checkDownOnVolume(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckPatternController.checkHighVolumeReversal(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkThreeLowerCloses(startDate, quotations));
		
		//Uncertain
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestUpDay(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestDailySpread(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestDailyVolume(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckPatternController.checkChurning(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkTimeClimax(startDate, quotations));
		
		protocol.sortEntriesByDate();
		protocol.calculatePercentages();
		
		return protocol;
	}
	
	
	/**
	 * Checks if the price has breached the SMA(50) on a closing basis.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * For each day on which the SMA(50) has been breached, a ProtocolEntry is provided with further information.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for all days on which the SMA(50) was breached.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkCloseBelowSma50(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		int startIndex;
		Quotation currentDayQuotation, previousDayQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			if((i+1) < sortedQuotations.getQuotations().size()) {
				previousDayQuotation = sortedQuotations.getQuotations().get(i+1);
			}
			else {
				continue;
			}
			
			currentDayQuotation = sortedQuotations.getQuotations().get(i);
			
			if(previousDayQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +previousDayQuotation.getId());
			
			if(currentDayQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +currentDayQuotation.getId());
			
			if(previousDayQuotation.getClose().floatValue() >= previousDayQuotation.getIndicator().getSma50() &&
					currentDayQuotation.getClose().floatValue() < currentDayQuotation.getIndicator().getSma50()) {
				
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
				
				if(currentDayQuotation.getVolume() >= currentDayQuotation.getIndicator().getSma30Volume())
					protocolEntry.setText(this.resources.getString("protocol.closeBelowSma50HighVolume"));
				else
					protocolEntry.setText(this.resources.getString("protocol.closeBelowSma50LowVolume"));
				
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks if quotations exist at and after the given start date.
	 * 
	 * @param startDate The start date.
	 * @param quotations A list of quotations.
	 * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
	 */
	public void checkQuotationsExistAfterStartDate(final Date startDate, final QuotationArray quotations)  throws NoQuotationsExistException {
		int indexOfQuotationWithDate = quotations.getIndexOfQuotationWithDate(startDate);
		
		if(indexOfQuotationWithDate == -1)
			throw new NoQuotationsExistException(startDate);
	}
}
