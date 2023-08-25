package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs price performance related calculations.
 *
 * @author Michael
 */
public class PerformanceCalculator {
    /**
     * The number of trading days per month.
     */
    private static final int TRADING_DAYS_PER_MONTH = 21;

    /**
     * Factor used to format results as percent.
     */
    private static final int HUNDRED_PERCENT = 100;

    /**
     * Calculates the price performance between the current Quotation and the previous Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The price performance.
     */
    public float getPerformance(final Quotation currentQuotation, final Quotation previousQuotation) {
        float performance;
        BigDecimal roundedPerformance;
        final int scaleDivision = 4;
        final int scaleResult = 2;

        performance = currentQuotation.getClose()
                .divide(previousQuotation.getClose(), scaleDivision, RoundingMode.HALF_UP).floatValue() - 1;
        performance = performance * HUNDRED_PERCENT; // Get performance in percent.

        // Round result to two decimal places.
        roundedPerformance = new BigDecimal(performance).setScale(scaleResult, RoundingMode.HALF_UP);

        return roundedPerformance.floatValue();
    }

    /**
     * Provides the price performance for the given number of days.
     *
     * @param days             The number of days for performance calculation.
     * @param quotation        The Quotation for which the price performance is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for price
     *                         performance calculation.
     * @return The performance of the given interval in percent.
     */
    public float getPricePerformanceForDays(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        BigDecimal divisionResult = BigDecimal.valueOf(0);
        int indexOfQuotation = 0;
        final int scale = 4;

        // Get the starting point of price performance calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for price performance calculation.
        // The -1 is needed because a performance can only be calculated against a previous day. Therefore an additional
        // Quotation has to exist.
        if ((sortedQuotations.getQuotations().size() - days - indexOfQuotation - 1) < 0) {
            return 0;
        }

        divisionResult = sortedQuotations.getQuotations().get(indexOfQuotation).getClose().divide(
                sortedQuotations.getQuotations().get(indexOfQuotation + days).getClose(), scale, RoundingMode.HALF_UP);
        divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
        divisionResult = divisionResult.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

        return divisionResult.floatValue();
    }

    /**
     * Calculates the percentage sum needed for calculation of the RS number.
     *
     * @param quotation        The quotation of the date on which the percentage sum is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for percentage
     *                         sum calculation.
     * @return The percentage sum.
     */
    public float getRSPercentSum(final Quotation quotation, final QuotationArray sortedQuotations) {
        int indexOfQuotation = 0;
        BigDecimal rsPercentSum = BigDecimal.valueOf(0);
        final int months3 = 3;
        final int months6 = 6;
        final int months9 = 9;
        final int months12 = 12;

        // Get the staring point of RS percent sum calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months3));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months3));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months6));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months9));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months12));

        rsPercentSum.setScale(2);

        return rsPercentSum.floatValue();
    }

    /**
     * Provides the performance of a given interval for relative strength calculation.
     *
     * @param sortedQuotations The quotations containing date and price information for performance calculation.
     * @param indexOfQuotation The starting point from which the performance is calculated.
     * @param months           The number of months for performance calculation.
     * @return The performance of the given interval in percent.
     */
    private BigDecimal getPerformanceOfIntervalForRS(final QuotationArray sortedQuotations, final int indexOfQuotation,
            final int months) {
        BigDecimal divisionResult = BigDecimal.valueOf(0);
        // The offset -1 is used because most APIs only provide 252 data sets for a whole trading year.
        // Without the offset, 253 data sets would be needed to calculate the one year performance.
        int indexOfQuotationForInterval = indexOfQuotation + (TRADING_DAYS_PER_MONTH * months) - 1;
        final int scale = 4;

        if (indexOfQuotationForInterval >= sortedQuotations.getQuotations().size()) {
            return divisionResult;
        }

        divisionResult = sortedQuotations.getQuotations().get(indexOfQuotation).getClose().divide(
                sortedQuotations.getQuotations().get(indexOfQuotationForInterval).getClose(), scale,
                RoundingMode.HALF_UP);
        divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
        divisionResult = divisionResult.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

        return divisionResult;
    }
}
