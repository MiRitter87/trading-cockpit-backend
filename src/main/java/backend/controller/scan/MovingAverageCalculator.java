package backend.controller.scan;

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
     * Returns the Simple Moving Average.
     *
     * @param days             The number of days on which the Simple Moving Average is based.
     * @param quotation        The Quotation for which the Simple Moving Average is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for Simple Moving
     *                         Average calculation.
     * @return The Simple Moving Average.
     */
    public float getSimpleMovingAverage(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        int indexOfQuotation = 0;
        BigDecimal sum = new BigDecimal(0);
        BigDecimal average;

        // Get the starting point of average calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for average calculation.
        if ((sortedQuotations.getQuotations().size() - days - indexOfQuotation) < 0) {
            return 0;
        }

        // Calculate the sum of the prices of the last x days.
        for (int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
            sum = sum.add(sortedQuotations.getQuotations().get(i).getClose());
        }

        // Build the average.
        average = sum.divide(BigDecimal.valueOf(days), NUMBER_DECIMALS_PRICE, RoundingMode.HALF_UP);

        return average.floatValue();
    }

    /**
     * Returns the Exponential Moving Average.
     *
     * @param days             The number of days on which the Exponential Moving Average is based.
     * @param quotation        The Quotation for which the Exponential Moving Average is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history.
     * @return The Exponential Moving Average.
     */
    public float getExponentialMovingAverage(final int days, final Quotation quotation,
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

        // Check if enough historical quotations exist for EMA calculation.
        // days*2 is used because the SMA has to be calculated first as starting point for EMA calculation.
        // Additional days are needed afterwards for the EMA approximation based on the SMA; therefore days*2 is used.
        if ((sortedQuotations.getQuotations().size() - days * 2 - indexOfQuotation) < 0) {
            return 0;
        }

        smoothingMultiplier = 2.0f / (days + 1);

        // Calculate the SMA as starting point for EMA calculation.
        indexForSmaCalculation = indexOfQuotation + days;
        sma = this.getSimpleMovingAverage(days, sortedQuotations.getQuotations().get(indexForSmaCalculation),
                sortedQuotations);

        // The initial EMA is initialized with the SMA.
        previousEma = sma;

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
}
