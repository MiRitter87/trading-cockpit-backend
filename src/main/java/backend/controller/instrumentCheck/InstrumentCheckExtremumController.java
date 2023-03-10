package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.controller.scan.IndicatorCalculator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks that are based on extreme daily price and volume behavior.
 * For example this can be the highest volume day of the year or the largest up- or down-day of the year.
 * 
 * @author Michael
 */
public class InstrumentCheckExtremumController {
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
	public InstrumentCheckExtremumController() {
		this.indicatorCalculator = new IndicatorCalculator();
	}
	
	
	/**
	 * Checks for the largest down-day of the year.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for the day of the largest down-day of the year after the start date.
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
	 * Checks for the largest up-day of the year.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for the day of the largest up-day of the year after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkLargestUpDay(final Date startDate, final List<Quotation> quotations) throws Exception {
		Instrument instrument = new Instrument();
		List<Quotation> quotationsSortedByDate;
		Quotation largestUpQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		float largestUpDayPerformance;
		
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		largestUpQuotation = this.getLargestUpDay(quotationsSortedByDate);
		largestUpDayPerformance = this.indicatorCalculator.getPricePerformanceForDays(1, largestUpQuotation, quotationsSortedByDate);
		
		if(largestUpQuotation.getDate().getTime() >= startDate.getTime()) {
			protocolEntry = new ProtocolEntry();
			protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
			protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestUpQuotation.getDate()));
			protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestUpDay"), largestUpDayPerformance));
			protocolEntries.add(protocolEntry);
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks for the largest daily high/low-spread of the year.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for the day of the largest high/low-spread of the year after the start date.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkLargestDailySpread(final Date startDate, final List<Quotation> quotations) throws Exception {
		Quotation largestSpreadQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		
		largestSpreadQuotation = this.getLargestDailySpread(quotations);
		
		if(largestSpreadQuotation.getDate().getTime() >= startDate.getTime()) {
			protocolEntry = new ProtocolEntry();
			protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
			protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestSpreadQuotation.getDate()));
			protocolEntry.setText(this.resources.getString("protocol.largestDailySpread"));
			protocolEntries.add(protocolEntry);
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Determines the largest down-day of the given trading history.
	 * 
	 * @param quotations A list of quotations.
	 * @return The largest down-day.
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
	 * Determines the largest up-day of the given trading history.
	 * 
	 * @param quotations A list of quotations.
	 * @return The largest up-day.
	 */
	private Quotation getLargestUpDay(final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		float largestUpPerformance = 0, performance;
		List<Quotation> quotationsSortedByDate;
		Quotation largestUpQuotation = null;
		Quotation currentQuotation, previousQuotation;
		
		//Sort the quotations by date for calculation of price performance.
		instrument.setQuotations(quotations);
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		//Determine the Quotation with the largest positive performance.
		for(int i = 0; i < quotationsSortedByDate.size() - 1; i++) {
			currentQuotation = quotationsSortedByDate.get(i);
			previousQuotation = quotationsSortedByDate.get(i+1);
			
			performance = this.indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
			
			if(performance > largestUpPerformance) {
				largestUpPerformance = performance;
				largestUpQuotation = currentQuotation;
			}
		}
		
		return largestUpQuotation;
	}
	
	
	/**
	 * Determines the largest daily high/low-spread of the given trading history.
	 * 
	 * @param quotations A list of quotations.
	 * @return The largest daily high/low-spread.
	 */
	private Quotation getLargestDailySpread(final List<Quotation> quotations) {
		float largestDailySpread = 0, currentSpread;
		Quotation largestSpreadQuotation = null;
		
		//Determine the Quotation with the largest daily high/low-spread.
		for(Quotation currentQuotation: quotations) {
			currentSpread = currentQuotation.getHigh().floatValue() - currentQuotation.getLow().floatValue();
			
			if(currentSpread > largestDailySpread) {
				largestDailySpread = currentSpread;
				largestSpreadQuotation = currentQuotation;
			}
		}
		
		return largestSpreadQuotation;
	}
}
