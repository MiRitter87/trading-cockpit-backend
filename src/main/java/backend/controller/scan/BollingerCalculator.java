package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs calculations of indicators developed by John Bollinger based on the instruments quotations.
 * 
 * @author Michael
 */
public class BollingerCalculator {
	/**
	 * Calculator for moving averages of price and volume.
	 */
	private MovingAverageCalculator movingAverageCalculator;
	
	
	/**
	 * Default constructor.
	 */
	public BollingerCalculator() {
		this.movingAverageCalculator = new MovingAverageCalculator();
	}
	
	
	/**
	 * Calculates the Bollinger BandWidth.
	 * 
	 * @param days The number of days on which the calculation is based.
	 * @param standardDeviations The standard deviation used for calculation of the upper and lower Bollinger Band.
	 * @param quotation The Quotation for which the Bollinger BandWidth is calculated.
	 * @param sortedQuotations A list of quotations sorted by date that build the trading history used for Bollinger BandWidth calculation.
	 * @return The Bollinger BandWidth.
	 */
	public float getBollingerBandWidth(final int days, final float standardDeviations, final Quotation quotation, 
			final QuotationArray sortedQuotations) {
		
		float standardDeviation = this.getStandardDeviation(this.getPricesAsArray(days, quotation, sortedQuotations));
		float simpleMovingAverage = this.movingAverageCalculator.getSimpleMovingAverage(days, quotation, sortedQuotations);
		float middleBand, upperBand, lowerBand, bandWidth;
		BigDecimal roundedResult;
		
		if(standardDeviation == 0 || simpleMovingAverage == 0)
			return 0;
		
		//Calculate the Bollinger Bands.
		middleBand = simpleMovingAverage;
		upperBand = simpleMovingAverage + (standardDeviation * standardDeviations);
		lowerBand = simpleMovingAverage - (standardDeviation * standardDeviations);
		
		//Calculate the Bollinger BandWidth.
		bandWidth = ((upperBand - lowerBand) / middleBand) * 100;
		
		//Round to two decimal places.
		roundedResult = new BigDecimal(bandWidth);
		roundedResult = roundedResult.setScale(2, RoundingMode.HALF_UP);
		
		return roundedResult.floatValue();
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
		
		if(inputValues.length == 0)
			return 0;
		
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
		
		//Round the result to four decimal places. This precision is necessary when handling low priced instruments.
		roundedResult = new BigDecimal(standardDeviation);
		roundedResult = roundedResult.setScale(4, RoundingMode.HALF_UP);
		
		return roundedResult.floatValue();
	}
	
	
	/**
	 * Provides an array of prices for the given days.
	 * 
	 * @param days The number of days for which prices are provided.
	 * @param quotation The Quotation as starting point for prices.
	 * @param sortedQuotations A list of quotations sorted by date that build the trading history.
	 * @return An array of prices.
	 */
	private float[] getPricesAsArray(final int days, final Quotation quotation, final QuotationArray sortedQuotations) {
		float[] prices = new float[days];
		int indexOfQuotation = 0, j = 0;
				
		//Get the starting point of average calculation.
		indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);
		
		//Check if enough quotations exist for average calculation.
		if((sortedQuotations.getQuotations().size() - days - indexOfQuotation) < 0)
			return prices;
		
		//Get the prices of the last x days.
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			prices[j] = sortedQuotations.getQuotations().get(i).getClose().floatValue();
			j++;
		}
		
		return prices;
	}
}
