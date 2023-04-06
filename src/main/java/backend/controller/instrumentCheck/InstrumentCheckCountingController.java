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
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
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
	 * A close is considered 'bad' if it occurs in the lower half of the days trading range.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the number of bad closes exceeds the number of good closes after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkMoreBadThanGoodCloses(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		Quotation startQuotation, currentQuotation;
		int startIndex, numberOfGoodCloses, numberOfBadCloses, numberOfDaysTotal;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> goodBadCloseSums;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		startQuotation = sortedQuotations.getQuotations().get(startIndex);
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip the first day, because more bad than good closes can only be calculated for at least two quotations.
			if(i == startIndex)
				continue;
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			goodBadCloseSums = this.getNumberOfGoodAndBadCloses(startQuotation, currentQuotation, sortedQuotations);
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
	 * Checks if there are more good closes than bad closes.
	 * A close is considered 'good' if it occurs in the upper half of the days trading range.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the number of good closes exceeds the number of bad closes after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkMoreGoodThanBadCloses(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		Quotation startQuotation, currentQuotation;
		int startIndex, numberOfGoodCloses, numberOfBadCloses, numberOfDaysTotal;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> goodBadCloseSums;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		startQuotation = sortedQuotations.getQuotations().get(startIndex);
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip the first day, because more good than bad closes can only be calculated for at least two quotations.
			if(i == startIndex)
				continue;
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			goodBadCloseSums = this.getNumberOfGoodAndBadCloses(startQuotation, currentQuotation, sortedQuotations);
			numberOfGoodCloses = goodBadCloseSums.get(MAP_ENTRY_GOOD_CLOSES);
			numberOfBadCloses = goodBadCloseSums.get(MAP_ENTRY_BAD_CLOSES);
			numberOfDaysTotal = goodBadCloseSums.get(MAP_ENTRY_DAYS_TOTAL);
			
			if(numberOfGoodCloses > numberOfBadCloses) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreGoodCloses"), numberOfGoodCloses, numberOfDaysTotal));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks if there are more down-days than up-days.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the number of down-days exceeds the number of up-days after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkMoreDownThanUpDays(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		Quotation startQuotation, currentQuotation;
		int startIndex, numberOfUpDays, numberOfDownDays, numberOfDaysTotal;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> upDownDaySums;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		startQuotation = sortedQuotations.getQuotations().get(startIndex);
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip the first day, because more down than up days can only be calculated for at least two quotations.
			if(i == startIndex)
				continue;
			
			currentQuotation = sortedQuotations.getQuotations().get(i);			
			
			upDownDaySums = this.getNumberOfUpAndDownDays(startQuotation, currentQuotation, sortedQuotations);
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
	 * Checks if there are more up-days than down-days.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the number of up-days exceeds the number of down-days after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkMoreUpThanDownDays(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		Quotation startQuotation, currentQuotation;
		int startIndex, numberOfUpDays, numberOfDownDays, numberOfDaysTotal;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> upDownDaySums;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		startQuotation = sortedQuotations.getQuotations().get(startIndex);
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip the first day, because more up than down days can only be calculated for at least two quotations.
			if(i == startIndex)
				continue;
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			upDownDaySums = this.getNumberOfUpAndDownDays(startQuotation, currentQuotation, sortedQuotations);
			numberOfUpDays = upDownDaySums.get(MAP_ENTRY_UP_DAYS);
			numberOfDownDays = upDownDaySums.get(MAP_ENTRY_DOWN_DAYS);
			numberOfDaysTotal = upDownDaySums.get(MAP_ENTRY_DAYS_TOTAL);
			
			if(numberOfUpDays > numberOfDownDays) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreUpDays"), numberOfUpDays, numberOfDaysTotal));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks if there is a time-wise climax movement.
	 * A time-wise climax move is given, if at least 7 of the last 10 trading days are up-days.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which there is a time-wise climax movement.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkTimeClimax(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		Quotation currentQuotation;
		int startIndex, numberOfUpDays;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		Map<String, Integer> upDownDaySums;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip this Quotation, if not at least 10 previous days of trading history exist.
			if((i+10) >= sortedQuotations.getQuotations().size())
				continue;
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			upDownDaySums = this.getNumberOfUpAndDownDays(sortedQuotations.getQuotations().get(i+9), currentQuotation, sortedQuotations);
			numberOfUpDays = upDownDaySums.get(MAP_ENTRY_UP_DAYS);
			
			if(numberOfUpDays >= 7) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(this.resources.getString("protocol.timeClimax"));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks if three lower closes have occurred.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which at least three lower closes have occurred.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkThreeLowerCloses(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		int startIndex, numberOfDownDays, numberOfDownDaysWithHighVolume;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		float performance;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			//Skip this Quotation, if not at least 3 previous days of trading history exist.
			if((i+3) >= sortedQuotations.getQuotations().size())
				continue;
			
			numberOfDownDays = 0;
			numberOfDownDaysWithHighVolume = 0;
			
			//Count the number of lower closes within the last three trading days.
			for(int j = 0; j < 3; j++) {
				performance = this.indicatorCalculator.getPerformance(
						sortedQuotations.getQuotations().get(i+j), sortedQuotations.getQuotations().get(i+j+1));
				
				if(performance < 0)
					numberOfDownDays++;
				
				if(sortedQuotations.getQuotations().get(i+j).getVolume() > sortedQuotations.getQuotations().get(i+j).getIndicator().getSma30Volume())
					numberOfDownDaysWithHighVolume++;
			}
			
			if(numberOfDownDays == 3) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(sortedQuotations.getQuotations().get(i).getDate()));
				
				if(numberOfDownDaysWithHighVolume == 3)
					protocolEntry.setText(this.resources.getString("protocol.threeLowerClosesHighVolume"));
				else
					protocolEntry.setText(this.resources.getString("protocol.threeLowerClosesLowVolume"));
				
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
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return A Map containing the number of good and bad closes.
	 */
	public Map<String, Integer> getNumberOfGoodAndBadCloses(final Quotation startQuotation, final Quotation endQuotation, 
			final QuotationArray sortedQuotations) {
		
		int indexOfStartQuotation, indexOfEndQuotation;
		int numberOfGoodCloses = 0, numberOfBadCloses = 0, numberOfDaysTotal = 0;
		Quotation currentQuotation;
		BigDecimal medianPrice;
		Map<String, Integer> resultMap = new HashMap<>(3);
		
		indexOfStartQuotation = sortedQuotations.getQuotations().indexOf(startQuotation);
		indexOfEndQuotation = sortedQuotations.getQuotations().indexOf(endQuotation);
		
		for(int i = indexOfStartQuotation; i >= indexOfEndQuotation; i--) {
			currentQuotation = sortedQuotations.getQuotations().get(i);
						
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
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return A Map containing the number of up- and down-days.
	 */
	public Map<String, Integer> getNumberOfUpAndDownDays(final Quotation startQuotation, final Quotation endQuotation, 
			final QuotationArray sortedQuotations) {
		
		int indexOfStartQuotation, indexOfEndQuotation;
		int numberOfUpDays = 0, numberOfDownDays = 0, numberOfDaysTotal = 0;
		Quotation currentQuotation, previousQuotation;
		float performance;
		Map<String, Integer> resultMap = new HashMap<>(3);
		
		indexOfStartQuotation = sortedQuotations.getQuotations().indexOf(startQuotation);
		indexOfEndQuotation = sortedQuotations.getQuotations().indexOf(endQuotation);
		
		for(int i = indexOfStartQuotation; i >= indexOfEndQuotation; i--) {
			if(sortedQuotations.getQuotations().size() <= (i+1))
				continue;	//Can't calculate performance for oldest Quotation because no previous Quotation exists for this one.
			
			previousQuotation = sortedQuotations.getQuotations().get(i+1);
			currentQuotation = sortedQuotations.getQuotations().get(i);
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
