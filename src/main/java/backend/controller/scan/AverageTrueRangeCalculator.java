package backend.controller.scan;

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
     * @param days The number of days on which the calculation is based.
     * @param quotation The Quotation for which the ATRP is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history.
     * @return The Average True Range Percent.
     */
    public float getAverageTrueRangePercent(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {

        // Round to two decimal places.

        return 0;
    }
}
