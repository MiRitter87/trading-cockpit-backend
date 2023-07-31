package backend.controller.scan;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs calculations of Stochastic values based on the instruments quotations.
 * 
 * @author Michael
 */
public class StochasticCalculator {
	/**
	 * Smoothing period used for Slow Stochastic.
	 */
	private static final int SLOW_STOCHASTIC_SMOOTHING_PERIOD = 3;
	
	
	/**
	 * Calculates the Slow Stochastic for the given number of days.
	 * The Slow Stochastic uses a default smoothing period of 3.
	 * 
	 * @param days The number of days used for calculation.
	 * @param quotation The Quotation for which the Slow Stochastic is calculated.
	 * @param sortedQuotations A list of quotations sorted by date that build the trading history used for calculation.
	 * @return The Slow Stochastic.
	 */
	public float getSlowStochastic(final int days, final Quotation quotation, final QuotationArray sortedQuotations) {
		float slowStochastic, stochastic, sum = 0;
		int indexOfQuotation = 0;
		Quotation currentQuotation;
		
		//Get the starting point of Slow Stochastic calculation.
		indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);
		
		//Check if enough quotations exist for Slow Stochastic calculation.
		if((sortedQuotations.getQuotations().size() - days - indexOfQuotation - SLOW_STOCHASTIC_SMOOTHING_PERIOD) < 0)
			return 0;
		
		for(int i = indexOfQuotation; i < (SLOW_STOCHASTIC_SMOOTHING_PERIOD + indexOfQuotation); i++) {
			currentQuotation = sortedQuotations.getQuotations().get(i);
			stochastic = this.getStochastic(days, currentQuotation, sortedQuotations);
			sum += stochastic;
		}
		
		slowStochastic = sum / SLOW_STOCHASTIC_SMOOTHING_PERIOD;
		
		//Round result to two decimal places.
		slowStochastic = (float) (Math.round(slowStochastic * 100.0) / 100.0);
		
		return slowStochastic;
	}
	
	
	/**
	 * Calculates the Stochastic for the given number of days.
	 * 
	 * @param days The number of days used for calculation.
	 * @param quotation The Quotation for which the Stochastic is calculated.
	 * @param sortedQuotations A list of quotations sorted by date that build the trading history used for calculation.
	 * @return The Stochastic.
	 */
	public float getStochastic(final int days, final Quotation quotation, final QuotationArray sortedQuotations) {
		float lowestLow, highestHigh, stochastic, close;
		int indexOfQuotation = 0;
		
		//Get the starting point of Slow Stochastic calculation.
		indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);
		
		//Check if enough quotations exist for high and low calculation.
		if((sortedQuotations.getQuotations().size() - days - indexOfQuotation) < 0)
			return 0;
		
		lowestLow = this.getLowestLow(days, quotation, sortedQuotations);
		highestHigh = this.getHighestHigh(days, quotation, sortedQuotations);
		close = quotation.getClose().floatValue();
		
		if((highestHigh - lowestLow) == 0)
			return 0;	//Prevent possible division by zero.
		
		stochastic = (close - lowestLow) / (highestHigh - lowestLow) * 100;
		
		//Round result to two decimal places.
		stochastic = (float) (Math.round(stochastic * 100.0) / 100.0);
		
		return stochastic;
	}
	
	
	/**
	 * Gets the lowest low of the given period.
	 * 
	 * @param days The number of days taken into account for low determination.
	 * @param quotation The Quotation as starting point for low determination.
	 * @param sortedQuotations A list of quotations sorted by date that build the trading history.
	 * @return The lowest low of the period.
	 */
	private float getLowestLow(final int days, final Quotation quotation, final QuotationArray sortedQuotations) {
		float lowestLow = 0;
		int indexOfQuotation = 0;
		Quotation currentQuotation;
		
		//Get the starting point of low calculation.
		indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);
		
		//Check if enough quotations exist for high calculation.
		if((sortedQuotations.getQuotations().size() - days - indexOfQuotation) < 0)
			return 0;
		
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			if(i == indexOfQuotation)
				lowestLow = currentQuotation.getLow().floatValue();	//Initially set the lowest low.
			
			if(currentQuotation.getLow().floatValue() < lowestLow)
				lowestLow = currentQuotation.getLow().floatValue();
		}
		
		return lowestLow;
	}
	
	
	/**
	 * Gets the highest high of the given period.
	 * 
	 * @param days The number of days taken into account for high determination.
	 * @param quotation The Quotation as starting point for high determination.
	 * @param sortedQuotations A list of quotations sorted by date that build the trading history.
	 * @return The highest high of the period.
	 */
	private float getHighestHigh(final int days, final Quotation quotation, final QuotationArray sortedQuotations) {
		float highestHigh = 0;
		int indexOfQuotation = 0;
		Quotation currentQuotation;
		
		//Get the starting point of high calculation.
		indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);
		
		//Check if enough quotations exist for high calculation.
		if((sortedQuotations.getQuotations().size() - days - indexOfQuotation) < 0)
			return 0;
		
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			if(currentQuotation.getHigh().floatValue() > highestHigh)
				highestHigh = currentQuotation.getHigh().floatValue();
		}
		
		return highestHigh;
	}
}