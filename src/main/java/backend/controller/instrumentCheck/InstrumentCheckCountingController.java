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
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks that are based on counting specific characteristics.
 * 
 * @author Michael
 */
public class InstrumentCheckCountingController {
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
	 * Indicator calculator.
	 */
	private IndicatorCalculator indicatorCalculator;
	
	
	/**
	 * Default constructor.
	 */
	public InstrumentCheckCountingController() {
		this.indicatorCalculator = new IndicatorCalculator();
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
		startIndex = InstrumentCheckController.getIndexOfQuotationWithDate(quotationsSortedByDate, startDate);
		
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
		startIndex = InstrumentCheckController.getIndexOfQuotationWithDate(quotationsSortedByDate, startDate);
		
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
}
