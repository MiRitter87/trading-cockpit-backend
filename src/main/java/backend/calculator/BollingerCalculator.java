package backend.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

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
     * @param period             The number of quotations on which the calculation is based.
     * @param standardDeviations The standard deviation used for calculation of the upper and lower Bollinger Band.
     * @param quotation          The Quotation for which the Bollinger BandWidth is calculated.
     * @param sortedQuotations   A list of quotations sorted by date that build the trading history used for Bollinger
     *                           BandWidth calculation.
     * @return The Bollinger BandWidth.
     */
    public float getBollingerBandWidth(final int period, final float standardDeviations, final Quotation quotation,
            final QuotationArray sortedQuotations) {

        float standardDeviation = this.getStandardDeviation(this.getPricesAsArray(period, quotation, sortedQuotations));
        float simpleMovingAverage = this.movingAverageCalculator.getSimpleMovingAverage(period, quotation,
                sortedQuotations);
        float middleBand;
        float upperBand;
        float lowerBand;
        float bandWidth;
        BigDecimal roundedResult;
        final int multiplierOfFormula = 100;

        if (standardDeviation == 0 || simpleMovingAverage == 0) {
            return 0;
        }

        // Calculate the Bollinger Bands.
        middleBand = simpleMovingAverage;
        upperBand = simpleMovingAverage + (standardDeviation * standardDeviations);
        lowerBand = simpleMovingAverage - (standardDeviation * standardDeviations);

        // Calculate the Bollinger BandWidth.
        bandWidth = ((upperBand - lowerBand) / middleBand) * multiplierOfFormula;

        // Round to two decimal places.
        roundedResult = new BigDecimal(bandWidth);
        roundedResult = roundedResult.setScale(2, RoundingMode.HALF_UP);

        return roundedResult.floatValue();
    }

    /**
     * Calculates the threshold value based on a history of Bollinger BandWidth values. If a percentThreshold of 20 is
     * given, 20% of the Bollinger BandWidth values are less or equal than the calculated threshold.
     *
     * @param period             The number of quotations on which the Bollinger BandWidth calculation is based.
     * @param standardDeviations The standard deviation used for calculation of the upper and lower Bollinger Band.
     * @param percentThreshold   The percent value for threshold calculation.
     * @param quotation          The Quotation for which the Bollinger BandWidth is calculated.
     * @param sortedQuotations   A list of quotations sorted by date that build the trading history used for Bollinger
     *                           BandWidth calculation.
     * @return The threshold value of Bollinger BandWidth values.
     */
    public float getBollingerBandWidthThreshold(final int period, final float standardDeviations,
            final int percentThreshold, final Quotation quotation, final QuotationArray sortedQuotations) {

        int indexOfQuotation = 0;
        int thresholdIndex;
        float bollingerBandWidth;
        Quotation tempQuotation;
        ArrayList<Float> bbwValues = new ArrayList<>();
        final int hundredPercent = 100;

        // Get the starting point of calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for calculation.
        if ((sortedQuotations.getQuotations().size() - period - indexOfQuotation) < 0) {
            return 0;
        }

        // Calculate all Bollinger BandWidth values and store results.
        for (int i = indexOfQuotation; i <= (sortedQuotations.getQuotations().size() - period); i++) {
            tempQuotation = sortedQuotations.getQuotations().get(i);
            bollingerBandWidth = this.getBollingerBandWidth(period, standardDeviations, tempQuotation,
                    sortedQuotations);

            if (bollingerBandWidth > 0) {
                bbwValues.add(bollingerBandWidth);
            }
        }

        // Sort values descending.
        Collections.sort(bbwValues);
        Collections.reverse(bbwValues);

        // Get the index of the threshold value.
        thresholdIndex = bbwValues.size() - (bbwValues.size() * percentThreshold / hundredPercent) - 1;

        return bbwValues.get(thresholdIndex);
    }

    /**
     * Calculates the standard deviation based on the given input values.
     *
     * @param inputValues The values for standard deviation calculation.
     * @return The standard deviation.
     */
    public float getStandardDeviation(final float[] inputValues) {
        float sum = 0;
        float mean;
        float deviationFromMean;
        float deviationFromMeanSquared;
        float sumOfSquares = 0;
        float variance;
        float standardDeviation;
        BigDecimal roundedResult;
        final int scale = 4;

        if (inputValues.length == 0) {
            return 0;
        }

        // 1. Calculate the mean of all values.
        for (int i = 0; i < inputValues.length; i++) {
            sum += inputValues[i];
        }

        mean = sum / inputValues.length;

        for (int i = 0; i < inputValues.length; i++) {
            // 2. Get the deviation from the mean.
            deviationFromMean = inputValues[i] - mean;

            // 3. Square deviation from mean.
            deviationFromMeanSquared = (float) Math.pow(deviationFromMean, 2);

            // 4. Calculate the sum of squares.
            sumOfSquares += deviationFromMeanSquared;
        }

        // 5. Calculate the variance.
        variance = sumOfSquares / inputValues.length;

        // 6. Calculate the square root of the variance.
        standardDeviation = (float) Math.sqrt(variance);

        // Round the result to four decimal places. This precision is necessary when handling low priced instruments.
        roundedResult = new BigDecimal(standardDeviation);
        roundedResult = roundedResult.setScale(scale, RoundingMode.HALF_UP);

        return roundedResult.floatValue();
    }

    /**
     * Provides an array of prices for the given number of quotations.
     *
     * @param period           The number of quotations for which prices are provided.
     * @param quotation        The Quotation as starting point for prices.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history.
     * @return An array of prices.
     */
    private float[] getPricesAsArray(final int period, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        float[] prices = new float[period];
        int indexOfQuotation = 0;
        int j = 0;

        // Get the starting point of average calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for average calculation.
        if ((sortedQuotations.getQuotations().size() - period - indexOfQuotation) < 0) {
            return prices;
        }

        // Get the prices of the last x days or weeks.
        for (int i = indexOfQuotation; i < (period + indexOfQuotation); i++) {
            prices[j] = sortedQuotations.getQuotations().get(i).getClose().floatValue();
            j++;
        }

        return prices;
    }
}
