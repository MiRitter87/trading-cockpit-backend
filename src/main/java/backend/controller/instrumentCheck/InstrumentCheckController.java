package backend.controller.instrumentCheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.dao.DAOManager;
import backend.dao.quotation.QuotationDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
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
	 * Default constructor.
	 */
	public InstrumentCheckController() {
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
		this.instrumentCheckCountingController = new InstrumentCheckCountingController();
		this.instrumentCheckExtremumController = new InstrumentCheckExtremumController();
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
		List<Quotation> quotations;
		Protocol protocol = new Protocol();
		
		quotations = this.quotationDAO.getQuotationsOfInstrument(instrumentId);
		this.checkQuotationsExistAfterStartDate(startDate, quotations);
		
		//Confirmations
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreUpThanDownDays(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreGoodThanBadCloses(startDate, quotations));
		
		//Violations
		protocol.getProtocolEntries().addAll(this.checkCloseBelowSma50(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestDownDay(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreDownThanUpDays(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckCountingController.checkMoreBadThanGoodCloses(startDate, quotations));
		
		//Uncertain
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestUpDay(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.instrumentCheckExtremumController.checkLargestDailySpread(startDate, quotations));
		
		protocol.sortEntriesByDate();
		
		return protocol;
	}
	
	
	/**
	 * Checks if the price has breached the SMA(50) on a closing basis.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * For each day on which the SMA(50) has been breached, a ProtocolEntry is provided with further information.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for all days on which the SMA(50) was breached.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkCloseBelowSma50(final Date startDate, final List<Quotation> quotations) throws Exception {
		Instrument instrument = new Instrument();
		List<Quotation> quotationsSortedByDate;
		int startIndex;
		Quotation currentDayQuotation, previousDayQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		startIndex = getIndexOfQuotationWithDate(quotationsSortedByDate, startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			if((i+1) < quotationsSortedByDate.size()) {
				previousDayQuotation = quotationsSortedByDate.get(i+1);
			}
			else {
				continue;
			}
			
			currentDayQuotation = quotationsSortedByDate.get(i);
			
			if(previousDayQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +previousDayQuotation.getId());
			
			if(currentDayQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +currentDayQuotation.getId());
			
			if(previousDayQuotation.getClose().floatValue() >= previousDayQuotation.getIndicator().getSma50() &&
					currentDayQuotation.getClose().floatValue() < currentDayQuotation.getIndicator().getSma50()) {
				
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
				protocolEntry.setText(this.resources.getString("protocol.closeBelowSma50"));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Gets the index of the Quotation with the given date.
	 * If no Quotation exists on the given day, the index of the first Quotation coming afterwards is determined.
	 * 
	 * @param quotations A List of quotations.
	 * @param date The date.
	 * @return The index of the Quotation. -1, if no Quotation was found.
	 */
	public static int getIndexOfQuotationWithDate(final List<Quotation> quotations, final Date date) {
		Quotation quotation;
		Date quotationDate, inputDate;
		int indexOfQuotation = -1;
		
		inputDate = DateTools.getDateWithoutIntradayAttributes(date);
		
		for(int i = 0; i < quotations.size(); i++) {
			quotation = quotations.get(i);
			quotationDate = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());
			
			if(inputDate.getTime() <= quotationDate.getTime())
				indexOfQuotation = i;
		}
		
		return indexOfQuotation;
	}
	
	
	/**
	 * Checks if quotations exist at and after the given start date.
	 * 
	 * @param startDate The start date.
	 * @param quotations A list of quotations.
	 * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
	 */
	public void checkQuotationsExistAfterStartDate(final Date startDate, final List<Quotation> quotations)  throws NoQuotationsExistException {
		int indexOfQuotationWithDate = getIndexOfQuotationWithDate(quotations, startDate);
		
		if(indexOfQuotationWithDate == -1)
			throw new NoQuotationsExistException(startDate);
	}
}
