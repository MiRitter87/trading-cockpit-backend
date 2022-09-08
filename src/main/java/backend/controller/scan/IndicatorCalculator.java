package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

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
	 * @return The Simple Moving Average.
	 */
	public float getSimpleMovingAverage(final int days, final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		int indexOfQuotation = 0;
		BigDecimal sum = new BigDecimal(0), average;
		
		//Sort the quotations by date for calculation of average based on last x days.
		instrument.setQuotations(quotations);
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
	 * Returns the distance of the current Quotation to the 52 week high.
	 * 
	 * @param quotation The current Quotation for which the distance to the 52 week high is calculated.
	 * @param quotations A list of quotations that build the trading history
	 * @return The distance of the quotation to the 52 week high.
	 */
	public float getDistanceTo52WeekHigh(final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		Quotation tempQuotation;
		List<Quotation> sortedQuotations;
		int indexOfQuotation = 0;
		BigDecimal highPrice52Weeks = new BigDecimal(0), percentDistance = new BigDecimal(0);
		
		//Sort the quotations by date for determination of last 252 quotes.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of 52 week high calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Get the highest price of the last 52 weeks.
		//If the trading history does not span a whole year, take all data available.
		for(int i = indexOfQuotation; i < (252 + indexOfQuotation) && i < sortedQuotations.size(); i++) {
			tempQuotation = sortedQuotations.get(i);
			
			if(tempQuotation.getPrice().compareTo(highPrice52Weeks) == 1)
				highPrice52Weeks = tempQuotation.getPrice();
		}
		
		//Calculate the percent distance based on the quotation price and the 52 week high.
		percentDistance = quotation.getPrice().divide(highPrice52Weeks, 4, RoundingMode.HALF_UP);
		percentDistance = percentDistance.subtract(BigDecimal.valueOf(1));
		percentDistance = percentDistance.multiply(BigDecimal.valueOf(100));
		
		return percentDistance.floatValue();
	}
	
	
	/**
	 * Returns the distance of the current Quotation to the 52 week low.
	 * 
	 * @param quotation The current Quotation for which the distance to the 52 week low is calculated.
	 * @param quotations A list of quotations that build the trading history
	 * @return The distance of the quotation to the 52 week low.
	 */
	public float getDistanceTo52WeekLow(final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		Quotation tempQuotation;
		List<Quotation> sortedQuotations;
		int indexOfQuotation = 0;
		BigDecimal lowPrice52Weeks = quotation.getPrice(), percentDistance = new BigDecimal(0);
		
		//Sort the quotations by date for determination of last 252 quotes.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of 52 week low calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Get the lowest price of the last 52 weeks.
		//If the trading history does not span a whole year, take all data available.
		for(int i = indexOfQuotation; i < (252 + indexOfQuotation) && i < sortedQuotations.size(); i++) {
			tempQuotation = sortedQuotations.get(i);
			
			if(tempQuotation.getPrice().compareTo(lowPrice52Weeks) == -1)
				lowPrice52Weeks = tempQuotation.getPrice();
		}
		
		//Calculate the percent distance based on the quotation price and the 52 week low.
		percentDistance = quotation.getPrice().divide(lowPrice52Weeks, 4, RoundingMode.HALF_UP);
		percentDistance = percentDistance.subtract(BigDecimal.valueOf(1));
		percentDistance = percentDistance.multiply(BigDecimal.valueOf(100));
		
		return percentDistance.floatValue();
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
	 * Calculates the standard deviation based on the given input values.
	 * 
	 * @param inputValues The values for standard deviation calculation.
	 * @return The standard deviation.
	 */
	public float getStandardDeviation(float[] inputValues) {
		float sum = 0, mean, deviationFromMean, deviationFromMeanSquared, sumOfSquares = 0, variance, standardDeviation;
		BigDecimal roundedResult;
		
		//1. Calculate the mean of all values.
		for(int i = 0; i< inputValues.length; i++)
			sum+=inputValues[i];
		
		mean = sum / inputValues.length;
		
		for(int i = 0; i< inputValues.length; i++) {
			//2. Get the deviation from the mean.
			deviationFromMean = inputValues[i] - mean;
			
			//3. Square deviation from mean.
			deviationFromMeanSquared = (float) Math.pow(deviationFromMean, 2);
			
			//4. Calculate the sum of squares.
			sumOfSquares += deviationFromMeanSquared;
		}
		
		//5. Calculate the variance.
		variance = sumOfSquares / inputValues.length;
		
		//6. Calculate the square root of the variance.
		standardDeviation = (float) Math.sqrt(variance);
		
		//Round the result to two decimal places.
		roundedResult = new BigDecimal(standardDeviation);
		roundedResult = roundedResult.setScale(2, RoundingMode.HALF_UP);
		
		return roundedResult.floatValue();
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
