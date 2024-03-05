package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs the calculation of the Average True Range (Percent) indicator.
 *
 * @author Michael
 */
public class AverageTrueRangeCalculator {
    /**
     * Calculates the Average True Range Percent.
     *
     * @param days             The number of days on which the calculation is based.
     * @param quotation        The Quotation for which the ATRP is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history.
     * @return The Average True Range Percent.
     */
    public float getAverageTrueRangePercent(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {

        // Round to two decimal places.

        return 0;
    }

    /**
     * Calculates the True Range.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The True Range.
     */
    public float getTrueRange(final Quotation currentQuotation, final Quotation previousQuotation) {
        float highLow;
        float highClose;
        float lowClose;
        float maxValue;
        BigDecimal trueRange;

        highLow = currentQuotation.getHigh().subtract(currentQuotation.getLow()).floatValue();
        highClose = currentQuotation.getHigh().subtract(previousQuotation.getClose()).floatValue();
        lowClose = currentQuotation.getLow().subtract(previousQuotation.getClose()).floatValue();

        // Use absolute values for further calculations.
        highClose = Math.abs(highClose);
        lowClose = Math.abs(lowClose);

        // Get the maximum value of the triple.
        maxValue = Math.max(highClose, lowClose);
        maxValue = Math.max(maxValue, highLow);

        // Round result to two decimal places
        trueRange = new BigDecimal(maxValue);
        trueRange = trueRange.setScale(2, RoundingMode.HALF_UP);

        return trueRange.floatValue();
    }
}
