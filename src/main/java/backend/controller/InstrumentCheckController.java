package backend.controller;

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
 * Performs health checks for an Instrument.
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
	 * Default constructor.
	 */
	public InstrumentCheckController() {
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
	}
	
	
	/**
	 * Checks the health of the given Instrument beginning at the given start date.
	 * 
	 * @param instrumentId The id of the Instrument.
	 * @param startDate The start date of the health check.
	 * @return A protocol containing the health information from the start date until the most recent quotation.
	 * @throws Exception Health check failed.
	 */
	public Protocol checkInstrument(final Integer instrumentId, final Date startDate) throws Exception {
		List<Quotation> quotations;
		Protocol protocol = new Protocol();
		
		quotations = this.quotationDAO.getQuotationsOfInstrument(instrumentId);
		
		protocol.getProtocolEntries().addAll(this.checkCloseBelowSma50(startDate, quotations));
		
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
	 * @return List of ProtocolEntry, for all days on which the SMA(50) was breached.#
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
		startIndex = this.getIndexOfQuotationWithDate(quotationsSortedByDate, startDate);
		
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
	private int getIndexOfQuotationWithDate(final List<Quotation> quotations, final Date date) {
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
}
