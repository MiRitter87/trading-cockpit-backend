package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import backend.controller.scan.IndicatorCalculator;
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
	 * Key of the Map entry containing the number of up-days.
	 */
	public final static String MAP_ENTRY_UP_DAYS = "NUMBER_UP_DAYS";
	
	/**
	 * Key of the Map entry containing the number of down-days.
	 */
	public final static String MAP_ENTRY_DOWN_DAYS = "NUMBER_DOWN_DAYS";
	
	/**
	 * Key of the Map entry containing the total number of days.
	 */
	public final static String MAP_ENTRY_DAYS_TOTAL = "DAYS_TOTAL";
	
	/**
	 * Key of the Map entry containing the number of good closes.
	 */
	public final static String MAP_ENTRY_GOOD_CLOSES = "NUMBER_GOOD_CLOSES";
	
	/**
	 * Key of the Map entry containing the number of bad closes.
	 */
	public final static String MAP_ENTRY_BAD_CLOSES = "NUMBER_BAD_CLOSES";
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * DAO to access Quotation data of Instrument.
	 */
	private QuotationDAO quotationDAO;
	
	/**
	 * Indicator calculator.
	 */
	private IndicatorCalculator indicatorCalculator;
	
	
	/**
	 * Default constructor.
	 */
	public InstrumentCheckController() {
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
		this.indicatorCalculator = new IndicatorCalculator();
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
		
		protocol.getProtocolEntries().addAll(this.checkCloseBelowSma50(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.checkLargestDownDay(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.checkMoreDownThanUpDays(startDate, quotations));
		protocol.getProtocolEntries().addAll(this.checkMoreBadThanGoodCloses(startDate, quotations));
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
	 * Checks for the largest down day of the year.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for the day of the largest down day of the year after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkLargestDownDay(final Date startDate, final List<Quotation> quotations) throws Exception {
		Instrument instrument = new Instrument();
		List<Quotation> quotationsSortedByDate;
		Quotation largestDownQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		float largestDownDayPerformance;
		
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		largestDownQuotation = this.getLargestDownDay(quotationsSortedByDate);
		largestDownDayPerformance = this.indicatorCalculator.getPricePerformanceForDays(1, largestDownQuotation, quotationsSortedByDate);
		
		if(largestDownQuotation.getDate().getTime() >= startDate.getTime()) {
			protocolEntry = new ProtocolEntry();
			protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
			protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestDownQuotation.getDate()));
			protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"), largestDownDayPerformance));
			protocolEntries.add(protocolEntry);
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks if there are more down days than up days.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the number of down-days exceeds the number of up-days after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkMoreDownThanUpDays(final Date startDate, final List<Quotation> quotations) throws Exception {
		Instrument instrument = new Instrument();
		List<Quotation> quotationsSortedByDate;
		Quotation startQuotation, currentQuotation;
		int startIndex, numberOfUpDays, numberOfDownDays, numberOfDaysTotal;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> upDownDaySums;
		
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		startIndex = this.getIndexOfQuotationWithDate(quotationsSortedByDate, startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		startQuotation = quotationsSortedByDate.get(startIndex);
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip the first day, because more down than up days can only be calculated for at least two quotations.
			if(i == startIndex)
				continue;
			
			currentQuotation = quotationsSortedByDate.get(i);			
			
			upDownDaySums = this.getNumberOfUpAndDownDays(startQuotation, currentQuotation, quotationsSortedByDate);
			numberOfUpDays = upDownDaySums.get(MAP_ENTRY_UP_DAYS);
			numberOfDownDays = upDownDaySums.get(MAP_ENTRY_DOWN_DAYS);
			numberOfDaysTotal = upDownDaySums.get(MAP_ENTRY_DAYS_TOTAL);
			
			if(numberOfDownDays > numberOfUpDays) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreDownDays"), numberOfDownDays, numberOfDaysTotal));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks if there are more bad closes than good closes.
	 * A close is 'bad' if it occurs in the lower half of the days trading range.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the number of bad closes exceeds the number of good closes after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkMoreBadThanGoodCloses(final Date startDate, final List<Quotation> quotations) throws Exception {
		Instrument instrument = new Instrument();
		List<Quotation> quotationsSortedByDate;
		Quotation startQuotation, currentQuotation;
		int startIndex, numberOfGoodCloses, numberOfBadCloses, numberOfDaysTotal;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> goodBadCloseSums;
		
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		startIndex = this.getIndexOfQuotationWithDate(quotationsSortedByDate, startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		startQuotation = quotationsSortedByDate.get(startIndex);
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip the first day, because more bad than good closes can only be calculated for at least two quotations.
			if(i == startIndex)
				continue;
			
			currentQuotation = quotationsSortedByDate.get(i);
			
			goodBadCloseSums = this.getNumberOfGoodAndBadCloses(startQuotation, currentQuotation, quotationsSortedByDate);
			numberOfGoodCloses = goodBadCloseSums.get(MAP_ENTRY_GOOD_CLOSES);
			numberOfBadCloses = goodBadCloseSums.get(MAP_ENTRY_BAD_CLOSES);
			numberOfDaysTotal = goodBadCloseSums.get(MAP_ENTRY_DAYS_TOTAL);
			
			if(numberOfBadCloses > numberOfGoodCloses) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreBadCloses"), numberOfBadCloses, numberOfDaysTotal));
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
	
	
	/**
	 * Checks if quotations exist at and after the given start date.
	 * 
	 * @param startDate The start date.
	 * @param quotations A list of quotations.
	 * @throws NoQuotationsExistException Exception indicating no Quotations exist at and after given start date.
	 */
	public void checkQuotationsExistAfterStartDate(final Date startDate, final List<Quotation> quotations)  throws NoQuotationsExistException {
		int indexOfQuotationWithDate = this.getIndexOfQuotationWithDate(quotations, startDate);
		
		if(indexOfQuotationWithDate == -1)
			throw new NoQuotationsExistException(startDate);
	}
	
	
	
	/**
	 * Determines the largest down day of the given trading history.
	 * 
	 * @param quotations A list of quotations.
	 * @return The largest down day.
	 */
	private Quotation getLargestDownDay(final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		float largestDownPerformance = 0, performance;
		List<Quotation> quotationsSortedByDate;
		Quotation largestDownQuotation = null;
		Quotation currentQuotation, previousQuotation;
		
		//Sort the quotations by date for calculation of price performance.
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		//Determine the Quotation with the largest negative performance.
		for(int i = 0; i < quotationsSortedByDate.size() - 1; i++) {
			currentQuotation = quotationsSortedByDate.get(i);
			previousQuotation = quotationsSortedByDate.get(i+1);
			
			performance = this.indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
			
			if(performance < largestDownPerformance) {
				largestDownPerformance = performance;
				largestDownQuotation = currentQuotation;
			}
		}
		
		return largestDownQuotation;
	}
		
	
	/**
	 * Counts the number of up- and down-days from startQuotation to endQuotation.
	 * 
	 * @param startQuotation The first Quotation used for counting.
	 * @param endQuotation The last Quotation used for counting.
	 * @param sortedQuotations The quotations that build the trading history.
	 * @return A Map containing the number of up- and down-days.
	 */
	public Map<String, Integer> getNumberOfUpAndDownDays(final Quotation startQuotation, final Quotation endQuotation, 
			final List<Quotation> sortedQuotations) {
		
		int indexOfStartQuotation, indexOfEndQuotation;
		int numberOfUpDays = 0, numberOfDownDays = 0, numberOfDaysTotal = 0;
		Quotation currentQuotation, previousQuotation;
		float performance;
		Map<String, Integer> resultMap = new HashMap<>(3);
		
		indexOfStartQuotation = sortedQuotations.indexOf(startQuotation);
		indexOfEndQuotation = sortedQuotations.indexOf(endQuotation);
		
		for(int i = indexOfStartQuotation; i >= indexOfEndQuotation; i--) {
			if(sortedQuotations.size() <= (i+1))
				continue;	//Can't calculate performance for oldest Quotation because no previous Quotation exists for this one.
			
			previousQuotation = sortedQuotations.get(i+1);
			currentQuotation = sortedQuotations.get(i);
			performance = this.indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
			
			if(performance > 0)
				numberOfUpDays++;
			else if(performance < 0)
				numberOfDownDays++;
			
			numberOfDaysTotal++;
		}
		
		resultMap.put(MAP_ENTRY_UP_DAYS, numberOfUpDays);
		resultMap.put(MAP_ENTRY_DOWN_DAYS, numberOfDownDays);
		resultMap.put(MAP_ENTRY_DAYS_TOTAL, numberOfDaysTotal);
		
		return resultMap;
	}
	
	
	/**
	 * Counts the number of good and bad closes from startQuotation to endQuotation.
	 * 
	 * @param startQuotation The first Quotation used for counting.
	 * @param endQuotation The last Quotation used for counting.
	 * @param sortedQuotations The quotations that build the trading history.
	 * @return A Map containing the number of good and bad closes.
	 */
	public Map<String, Integer> getNumberOfGoodAndBadCloses(final Quotation startQuotation, final Quotation endQuotation, 
			final List<Quotation> sortedQuotations) {
		
		int indexOfStartQuotation, indexOfEndQuotation;
		int numberOfGoodCloses = 0, numberOfBadCloses = 0, numberOfDaysTotal = 0;
		Quotation currentQuotation;
		BigDecimal medianPrice;
		Map<String, Integer> resultMap = new HashMap<>(3);
		
		indexOfStartQuotation = sortedQuotations.indexOf(startQuotation);
		indexOfEndQuotation = sortedQuotations.indexOf(endQuotation);
		
		for(int i = indexOfStartQuotation; i >= indexOfEndQuotation; i--) {
			currentQuotation = sortedQuotations.get(i);
						
			medianPrice = currentQuotation.getLow().add(currentQuotation.getHigh()).divide(new BigDecimal(2), 3, RoundingMode.HALF_UP);
			
			//A close exactly in the middle of the range is considered a bad close.
			if(currentQuotation.getClose().compareTo(medianPrice) == 1)
				numberOfGoodCloses++;
			else
				numberOfBadCloses++;
			
			numberOfDaysTotal++;
		}
		
		resultMap.put(MAP_ENTRY_GOOD_CLOSES, numberOfGoodCloses);
		resultMap.put(MAP_ENTRY_BAD_CLOSES, numberOfBadCloses);
		resultMap.put(MAP_ENTRY_DAYS_TOTAL, numberOfDaysTotal);
		
		return resultMap;
	}
}
