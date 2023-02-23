package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import backend.model.Currency;
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
	 * Calculates indicators for the given Quotation.
	 * 
	 * @param instrument The Instrument to which the Quotation belongs to.
	 * @param quotation The Quotation for which indicators are calculated.
	 * @param mostRecent Calculates all indicators if true (the most recent Quotation). Calculates only indicators relevant for history if false.
	 * @return The Quotation with the calculated indicators.
	 */
	public Quotation calculateIndicators(final Instrument instrument, final Quotation quotation, boolean mostRecent) {
		java.util.List<Quotation> sortedQuotations = instrument.getQuotationsSortedByDate();
		Indicator indicator;
		
		if(quotation.getIndicator() == null)
			indicator = new Indicator();
		else
			indicator = quotation.getIndicator();
		
		if(mostRecent) {
			//These indicators are calculated only for the most recent Quotation.
			indicator.setRsPercentSum(this.getRSPercentSum(quotation, sortedQuotations));
			indicator.setSma50(this.getSimpleMovingAverage(50, quotation, sortedQuotations));
			indicator.setSma150(this.getSimpleMovingAverage(150, quotation, sortedQuotations));
			indicator.setSma200(this.getSimpleMovingAverage(200, quotation, sortedQuotations));
			indicator.setDistanceTo52WeekHigh(this.getDistanceTo52WeekHigh(quotation, sortedQuotations));
			indicator.setDistanceTo52WeekLow(this.getDistanceTo52WeekLow(quotation, sortedQuotations));
			indicator.setBollingerBandWidth(this.getBollingerBandWidth(10, 2, quotation, sortedQuotations));
			indicator.setVolumeDifferential5Days(this.getVolumeDifferential(30, 5, quotation, sortedQuotations));
			indicator.setVolumeDifferential10Days(this.getVolumeDifferential(30, 10, quotation, sortedQuotations));
			indicator.setBaseLengthWeeks(this.getBaseLengthWeeks(quotation, sortedQuotations));
			indicator.setUpDownVolumeRatio(this.getUpDownVolumeRatio(50, quotation, sortedQuotations));
			indicator.setPerformance5Days(this.getPricePerformanceForDays(5, quotation, sortedQuotations));
			indicator.setLiquidity20Days(this.getLiquidityForDays(20, quotation, sortedQuotations));
		}
		else {
			//These indicators are calculated for historical quotations.
			indicator.setSma50(this.getSimpleMovingAverage(50, quotation, sortedQuotations));
		}
		
		quotation.setIndicator(indicator);
		
		return quotation;
	}
	
	
	/**
	 * Calculates the percentage sum needed for calculation of the RS number.
	 * 
	 * @param quotation The quotation of the date on which the percentage sum is calculated.
	 * @param quotations A list of quotations that build the trading history used for percentage sum calculation.
	 * @return The percentage sum.
	 */
	public float getRSPercentSum(final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		int indexOfQuotation = 0;
		BigDecimal rsPercentSum = BigDecimal.valueOf(0);
		
		//Sort the quotations by date for determination of RS percent sum.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the staring point of RS percent sum calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, 3));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, 3));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, 6));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, 9));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, 12));
		
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
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			sum = sum.add(sortedQuotations.get(i).getClose());
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
			
			if(tempQuotation.getClose().compareTo(highPrice52Weeks) == 1)
				highPrice52Weeks = tempQuotation.getClose();
		}
		
		//Calculate the percent distance based on the quotation price and the 52 week high.
		percentDistance = quotation.getClose().divide(highPrice52Weeks, 4, RoundingMode.HALF_UP);
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
		BigDecimal lowPrice52Weeks = quotation.getClose(), percentDistance = new BigDecimal(0);
		
		//Sort the quotations by date for determination of last 252 quotes.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of 52 week low calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Get the lowest price of the last 52 weeks.
		//If the trading history does not span a whole year, take all data available.
		for(int i = indexOfQuotation; i < (252 + indexOfQuotation) && i < sortedQuotations.size(); i++) {
			tempQuotation = sortedQuotations.get(i);
			
			if(tempQuotation.getClose().compareTo(lowPrice52Weeks) == -1)
				lowPrice52Weeks = tempQuotation.getClose();
		}
		
		//Calculate the percent distance based on the quotation price and the 52 week low.
		percentDistance = quotation.getClose().divide(lowPrice52Weeks, 4, RoundingMode.HALF_UP);
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
	 * Calculates the Bollinger BandWidth.
	 * 
	 * @param days The number of days on which the calculation is based.
	 * @param standardDeviations The standard deviation used for calculation of the upper and lower Bollinger Band.
	 * @param quotation The Quotation for which the Bollinger BandWidth is calculated.
	 * @param quotations A list of quotations that build the trading history used for Bollinger BandWidth calculation.
	 * @return The Bollinger BandWidth.
	 */
	public float getBollingerBandWidth(final int days, final float standardDeviations, final Quotation quotation, final List<Quotation> quotations) {
		float standardDeviation = this.getStandardDeviation(this.getPricesAsArray(days, quotation, quotations));
		float simpleMovingAverage = this.getSimpleMovingAverage(days, quotation, quotations);
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
	 * Returns the Simple Moving Average of the Volume.
	 * 
	 * @param days The number of days on which the Simple Moving Average Volume is based.
	 * @param quotation The Quotation for which the Simple Moving Average Volume is calculated.
	 * @param quotations A list of quotations that build the trading history used for Simple Moving Average Volume calculation.
	 * @return The Simple Moving Average Volume.
	 */
	public long getSimpleMovingAverageVolume(final int days, final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		int indexOfQuotation = 0;
		long sum = 0;
		BigDecimal average;
		
		//Sort the quotations by date for calculation of average based on last x days.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of average calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Check if enough quotations exist for average calculation.
		if((sortedQuotations.size() - days - indexOfQuotation) < 0)
			return 0;
		
		//Calculate the sum of the volume of the last x days.
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			sum = sum += sortedQuotations.get(i).getVolume();
		}
		
		//Build the average.
		average = (new BigDecimal(sum)).divide(BigDecimal.valueOf(days), 0, RoundingMode.HALF_UP);
		
		return average.longValue();
	}
	
	
	/**
	 * Calculates the difference in percent between the average volume of two periods.
	 * 
	 * @param daysPeriod1 The first period in days on which the Simple Moving Average Volume is based. Usually the longer period.
	 * @param daysPeriod2 The second period in days on which the Simple Moving Average Volume is based. Usually the shorter period.
	 * @param quotation quotation The Quotation for which the volume differential is calculated.
	 * @param quotations A list of quotations that build the trading history used for volume differential calculation.
	 * @return The volume differential.
	 */
	public float getVolumeDifferential(final int daysPeriod1, final int daysPeriod2, final Quotation quotation, final List<Quotation> quotations) {
		BigDecimal averageVolumePeriod1, averageVolumePeriod2, volumeDifferential;
		
		averageVolumePeriod1 = new BigDecimal(this.getSimpleMovingAverageVolume(daysPeriod1, quotation, quotations));
		averageVolumePeriod2 = new BigDecimal(this.getSimpleMovingAverageVolume(daysPeriod2, quotation, quotations));
		
		if(averageVolumePeriod1.equals(new BigDecimal(0)))
			return 0;
		
		volumeDifferential = averageVolumePeriod2.divide(averageVolumePeriod1, 4, RoundingMode.HALF_UP);
		volumeDifferential = volumeDifferential.subtract(new BigDecimal(1));
		volumeDifferential = volumeDifferential.multiply(new BigDecimal(100));
		
		return volumeDifferential.floatValue();
	}
	
	
	/**
	 * Calculates the length of the most recent consolidation in weeks, beginning at the most recent 52-week high.
	 * 
	 * @param quotation The Quotation for which the base length is calculated.
	 * @param quotations A list of quotations that build the trading history used for base length calculation.
	 * @return The base length in weeks.
	 */
	public int getBaseLengthWeeks(final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		Quotation tempQuotation;
		List<Quotation> sortedQuotations;
		BigDecimal highPrice52Weeks = new BigDecimal(0), baseLengthWeeks = new BigDecimal(0);
		int indexOfQuotation = 0, indexOf52WeekHigh = 0, baseLengthDays;
		
		//Sort the quotations by date for determination of last 252 quotes.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of 52 week high calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Get index of 52w high based on quotation within history.
		//If the trading history does not span a whole year, take all data available.
		for(int i = indexOfQuotation; i < (252 + indexOfQuotation) && i < sortedQuotations.size(); i++) {
			tempQuotation = sortedQuotations.get(i);
			
			if(tempQuotation.getClose().compareTo(highPrice52Weeks) == 1) {
				indexOf52WeekHigh = i;
				highPrice52Weeks = tempQuotation.getClose();
			}
		}
		
		//Count number of days between quotation and 52 week high.
		baseLengthDays = indexOf52WeekHigh - indexOfQuotation;
		
		//Divide result by 5 to get number in weeks.
		baseLengthWeeks = new BigDecimal(baseLengthDays).divide(new BigDecimal(5), 0, RoundingMode.HALF_UP);
		
		return baseLengthWeeks.intValue();
	}
	
	
	/**
	 * Calculates the volume ratio between up-days and down-days for the given number of days.
	 * 
	 * @param days The number of the last trading days that are taken into account for calculation.
	 * @param quotation The Quotation for which the U/D Volume Ratio is calculated.
	 * @param quotations A list of quotations that build the trading history used for U/D Volume Ratio calculation.
	 * @return The U/D Volume Ratio.
	 */
	public float getUpDownVolumeRatio(final int days, final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		Quotation currentDayQuotation, previousDayQuotation;
		int indexOfQuotation = 0;
		long upVolumeSum = 0, downVolumeSum = 0;
		BigDecimal upDownVolumeRatio;
		
		//Sort the quotations by date for calculation of volume sums based on last x days.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of sum calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Check if enough quotations exist for sum calculation.
		if((sortedQuotations.size() - days - indexOfQuotation - 1) < 0)
			return 0;
		
		//Calculate the sum of the prices of the last x days.
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			currentDayQuotation = sortedQuotations.get(i);
			previousDayQuotation = sortedQuotations.get(i+1);
			
			if(currentDayQuotation.getClose().compareTo(previousDayQuotation.getClose()) == 1)
				upVolumeSum = upVolumeSum + currentDayQuotation.getVolume();
			else if(currentDayQuotation.getClose().compareTo(previousDayQuotation.getClose()) == -1)
				downVolumeSum = downVolumeSum + currentDayQuotation.getVolume();
		}
		
		//Build the ratio.
		upDownVolumeRatio = new BigDecimal(upVolumeSum).divide(new BigDecimal(downVolumeSum), 2, RoundingMode.HALF_UP);
		
		return upDownVolumeRatio.floatValue();
	}
	
	
	/**
	 * Provides the price performance for the given number of days.
	 * 
	 * @param days The number of days for performance calculation.
	 * @param quotation The Quotation for which the price performance is calculated.
	 * @param quotations A list of quotations that build the trading history used for price performance calculation.
	 * @return The performance of the given interval in percent.
	 */
	public float getPricePerformanceForDays( final int days, final Quotation quotation, final List<Quotation> quotations) {
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		BigDecimal divisionResult = BigDecimal.valueOf(0);
		int indexOfQuotation = 0;
		
		//Sort the quotations by date for calculation of price performance based on last x days.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of price performance calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Check if enough quotations exist for price performance calculation.
		if((sortedQuotations.size() - days - indexOfQuotation - 1) < 0)
			return 0;
		
		divisionResult = sortedQuotations.get(indexOfQuotation).getClose().divide
				(sortedQuotations.get(indexOfQuotation + days).getClose(), 4, RoundingMode.HALF_UP);
		divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
		divisionResult = divisionResult.multiply(BigDecimal.valueOf(100));
		
		return divisionResult.floatValue();
	}
	
	
	/**
	 * Provides the average trading liquidity for the given number of days.
	 * 
	 * @param days The number of days for liquidity calculation.
	 * @param quotation The Quotation for which the liquidity is calculated.
	 * @param quotations A list of quotations that build the trading history used for liquidity calculation.
	 * @return The liquidity of the given interval.
	 */
	public float getLiquidityForDays(final int days, final Quotation quotation, final List<Quotation> quotations) {
		float averagePrice, liquidity;
		long averageVolume;
		
		averagePrice = this.getSimpleMovingAverage(days, quotation, quotations);
		averageVolume = this.getSimpleMovingAverageVolume(days, quotation, quotations);
		
		liquidity = averagePrice * averageVolume;
		
		//Divide by 100 to convert price from pence to pounds.
		if(quotation.getCurrency() == Currency.GBP)
			liquidity = liquidity / 100;
		
		return liquidity;
	}
	
	
	/**
	 * Provides the performance of a given interval for relative strength calculation.
	 * 
	 * @param sortedQuotations The quotations containing date and price information for performance calculation.
	 * @param indexOfQuotation The starting point from which the performance is calculated.
	 * @param months The number of months for performance calculation.
	 * @return The performance of the given interval in percent.
	 */
	private BigDecimal getPerformanceOfIntervalForRS(final List<Quotation> sortedQuotations, final int indexOfQuotation, final int months) {
		BigDecimal divisionResult = BigDecimal.valueOf(0);
		//The offset -1 is used because most APIs only provide 252 data sets for a whole trading year.
		//Without the offset, 253 data sets would be needed to calculate the one year performance.
		int indexOfQuotationForInterval = indexOfQuotation + (TRADING_DAYS_PER_MONTH * months) -1;
		
		if(indexOfQuotationForInterval >= sortedQuotations.size())
			return divisionResult;
		
		divisionResult = sortedQuotations.get(indexOfQuotation).getClose().divide
				(sortedQuotations.get(indexOfQuotationForInterval).getClose(), 4, RoundingMode.HALF_UP);
		divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
		divisionResult = divisionResult.multiply(BigDecimal.valueOf(100));
		
		return divisionResult;
	}
	
	
	/**
	 * Provides an array of prices for the given days.
	 * 
	 * @param days The number of days for which prices are provided.
	 * @param quotation The Quotation as starting point for prices.
	 * @param quotations A list of quotations that build the trading history.
	 * @return An array of prices.
	 */
	private float[] getPricesAsArray(final int days, final Quotation quotation, final List<Quotation> quotations) {
		float[] prices = new float[days];
		Instrument instrument = new Instrument();
		List<Quotation> sortedQuotations;
		int indexOfQuotation = 0, j = 0;
		
		//Sort the quotations by date for extraction of prices based on last x days.
		instrument.setQuotations(quotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		//Get the starting point of average calculation.
		indexOfQuotation = sortedQuotations.indexOf(quotation);
		
		//Check if enough quotations exist for average calculation.
		if((sortedQuotations.size() - days - indexOfQuotation) < 0)
			return prices;
		
		//Get the prices of the last x days.
		for(int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
			prices[j] = sortedQuotations.get(i).getClose().floatValue();
			j++;
		}
		
		return prices;
	}
}
