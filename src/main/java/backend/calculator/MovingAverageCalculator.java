package backend.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs calculations of moving averages based on the instruments quotations.
 *
 * @author Michael
 */
public class MovingAverageCalculator {
    /**
     * The number of decimals used for Moving Averages of price.
     */
    private static final int NUMBER_DECIMALS_PRICE = 3;

    /**
     * Returns the Simple Moving Average for the given period.
     *
     * @param period           The number of quotations on which the Simple Moving Average is based.
     * @param quotation        The Quotation for which the Simple Moving Average is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for Simple Moving
     *                         Average calculation.
     * @return The Simple Moving Average.
     */
    public float getSimpleMovingAverage(final int period, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        int indexOfQuotation = 0;
        BigDecimal sum = new BigDecimal(0);
        BigDecimal average;

        // Get the starting point of average calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for average calculation.
        if ((sortedQuotations.getQuotations().size() - period - indexOfQuotation) < 0) {
            return 0;
        }

        // Calculate the sum of the prices for the given number of quotations.
        for (int i = indexOfQuotation; i < (period + indexOfQuotation); i++) {
            sum = sum.add(sortedQuotations.getQuotations().get(i).getClose());
        }

        // Build the average.
        average = sum.divide(BigDecimal.valueOf(period), NUMBER_DECIMALS_PRICE, RoundingMode.HALF_UP);

        return average.floatValue();
    }

    /**
     * Returns the Exponential Moving Average.
     *
     * @param period           The number of quotations on which the Exponential Moving Average is based.
     * @param quotation        The Quotation for which the Exponential Moving Average is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history.
     * @return The Exponential Moving Average.
     */
    public float getExponentialMovingAverage(final int period, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        int indexOfQuotation = 0;
        int indexForSmaCalculation;
        float smoothingMultiplier;
        float sma;
        float previousEma;
        float currentEma = 0;
        Quotation currentQuotation;
        BigDecimal roundedEma;

        // Get the index of the Quotation for which the EMA has to be calculated.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for moving average calculation.
        if ((sortedQuotations.getQuotations().size() - period - indexOfQuotation) < 0) {
            return 0;
        }

        smoothingMultiplier = 2.0f / (period + 1);
        indexForSmaCalculation = this.getIndexForSmaCalculation(sortedQuotations, indexOfQuotation, period);

        sma = this.getSimpleMovingAverage(period, sortedQuotations.getQuotations().get(indexForSmaCalculation),
                sortedQuotations);

        // The initial EMA is initialized with the SMA.
        previousEma = sma;
        currentEma = sma;

        // Calculate the EMA approximation for the number of days after the starting point as defined by the SMA.
        // Start the day after the one for which the SMA has been calculated.
        for (int i = indexForSmaCalculation - 1; i >= indexOfQuotation; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            currentEma = smoothingMultiplier * (currentQuotation.getClose().floatValue() - previousEma) + previousEma;
            previousEma = currentEma;
        }

        // Round result to three decimal places.
        roundedEma = new BigDecimal(currentEma);
        roundedEma = roundedEma.setScale(NUMBER_DECIMALS_PRICE, RoundingMode.HALF_UP);

        return roundedEma.floatValue();
    }

    /**
     * Returns the Simple Moving Average of the Volume.
     *
     * @param days             The number of days on which the Simple Moving Average Volume is based.
     * @param quotation        The Quotation for which the Simple Moving Average Volume is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for Simple Moving
     *                         Average Volume calculation.
     * @return The Simple Moving Average Volume.
     */
    public long getSimpleMovingAverageVolume(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        int indexOfQuotation = 0;
        long sum = 0;
        BigDecimal average;

        // Get the starting point of average calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for average calculation.
        if ((sortedQuotations.getQuotations().size() - days - indexOfQuotation) < 0) {
            return 0;
        }

        // Calculate the sum of the volume of the last x days.
        for (int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
            sum += sortedQuotations.getQuotations().get(i).getVolume();
        }

        // Build the average.
        average = (new BigDecimal(sum)).divide(BigDecimal.valueOf(days), 0, RoundingMode.HALF_UP);

        return average.longValue();
    }

    /**
     * Determines the index within the array of sorted quotations that is used for calculation of SMA. This SMA later
     * serves as a starting point for calculation of the EMA.
     *
     * @param sortedQuotations A list of quotations sorted by date that build the trading history
     * @param indexOfQuotation The index of the Quotation for which the EMA has to be calculated.
     * @param period           The number of quotations on which the Exponential Moving Average is based.
     * @return The starting index for SMA calculation.
     */
    private int getIndexForSmaCalculation(final QuotationArray sortedQuotations, final int indexOfQuotation,
            final int period) {
        int indexForSmaCalculation;

        // Check if enough historical quotations exist for EMA calculation.
        // days*2 is used because the SMA has to be calculated first as starting point for EMA calculation.
        // Additional days are needed afterwards for the EMA approximation based on the SMA; therefore days*2 is used.
        if ((sortedQuotations.getQuotations().size() - period * 2 - indexOfQuotation) >= 0) {
            indexForSmaCalculation = indexOfQuotation + period;
        } else if ((indexOfQuotation + period) == sortedQuotations.getQuotations().size()) {
            // EMA = SMA, if the history only provides enough quotations to calculate the SMA.
            indexForSmaCalculation = indexOfQuotation;
        } else {
            // If the history does not contain enough quotations for a whole additional period, use as many as there are
            // available.
            indexForSmaCalculation = sortedQuotations.getQuotations().size() - indexOfQuotation - period;
        }

        return indexForSmaCalculation;
    }
}
