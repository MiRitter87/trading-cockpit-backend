package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationRsPercentSumComparator;

/**
 * Performs calculations of indicators based on the instruments quotations.
 * 
 * @author Michael
 */
public class IndicatorCalculator {
	/**
	 * The number of trading days per month.
	 */
	private static final int TRADING_DAYS_PER_MONTH = 21;
	
	/**
	 * Calculates the percentage sum needed for calculation of the RS number.
	 * 
	 * @param instrument The instrument whose quotations are used for calculation.
	 * @param quotation The quotation of the date on which the percentage sum is calculated.
	 * @return The percentage sum.
	 */
	public float getRSPercentSum(final Instrument instrument, final Quotation quotation) {
		List<Quotation> sortedQuotations = instrument.getQuotationsSortedByDate();
		int indexOfQuotation = sortedQuotations.indexOf(quotation);
		BigDecimal rsPercentSum = BigDecimal.valueOf(0);
		
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 3));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 3));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 6));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 9));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 12));
		
		rsPercentSum.setScale(2);
		
		return rsPercentSum.floatValue();
	}
	
	
	/**
	 * Returns the Simple Moving Average.
	 * 
	 * @param days The number of days on which the Simple Moving Average is based.
	 * @param quotation The Quotation for which the Simple Moving Average is calculated.
	 * @param quotations A list of quotations that build the trading history used for Simple Moving Average calculation.
	 * @return
	 */
	public float getSimpleMovingAverage(final int days, final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		Set<Quotation> quotationSet = new HashSet<>(quotations);
		int indexOfQuotation = 0;
		BigDecimal sum = new BigDecimal(0), average;
		
		//Sort the quotations by date for calculation of average based on last x days.
		instrument.setQuotations(quotationSet);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of average calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Check if enough quotations exist for average calculation.
		if((sortedQuotations.size() - days - indexOfQuotation) < 0)
			return 0;
		
		//Calculate the sum of the prices of the last x days.
		for(int i = indexOfQuotation; i<days; i++) {
			sum = sum.add(sortedQuotations.get(i).getPrice());
		}
		
		//Build the average.
		average = sum.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
		
		return average.floatValue();
	}
	
	
	/**
	 * Calculates the RS number for each Quotation.
	 * 
	 * @param quotations The quotations on which the calculation of the RS number is based.
	 */
	public void calculateRsNumbers(List<Quotation> quotations) {
		Indicator indicator;
		BigDecimal rsNumber, dividend, numberOfElements;
		
		Collections.sort(quotations, new QuotationRsPercentSumComparator());
		numberOfElements = BigDecimal.valueOf(quotations.size());
		
		for(int i = 0; i < quotations.size(); i++) {
			dividend = numberOfElements.subtract(BigDecimal.valueOf(i));
			rsNumber = dividend.divide(numberOfElements, 2, RoundingMode.HALF_UP);
			rsNumber = rsNumber.multiply(BigDecimal.valueOf(100));
			
			indicator = quotations.get(i).getIndicator();
			if(indicator != null)
				indicator.setRsNumber(rsNumber.intValue());
		}
	}
	
	
	/**
	 * Provides the performance of a given interval.
	 * 
	 * @param sortedQuotations The quotations containing date and price information for performance calculation.
	 * @param indexOfQuotation The starting point from which the performance is calculated.
	 * @param months The number of months for performance calculation.
	 * @return The performance of the given interval in percent.
	 */
	private BigDecimal getPerformanceOfInterval(final List<Quotation> sortedQuotations, final int indexOfQuotation, final int months) {
		BigDecimal divisionResult = BigDecimal.valueOf(0);
		int indexOfQuotationForInterval = indexOfQuotation + (TRADING_DAYS_PER_MONTH * months) -1;
		
		if(indexOfQuotationForInterval >= sortedQuotations.size())
			return divisionResult;
		
		divisionResult = sortedQuotations.get(indexOfQuotation).getPrice().divide
				(sortedQuotations.get(indexOfQuotationForInterval).getPrice(), 4, RoundingMode.HALF_UP);
		divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
		divisionResult = divisionResult.multiply(BigDecimal.valueOf(100));
		
		return divisionResult;
	}
}
