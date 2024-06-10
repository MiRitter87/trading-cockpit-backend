package backend.controller;

import java.util.List;

import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;

/**
 * Calculates the Aggregate Indicator.
 *
 * @author Michael
 */
public class AggregateIndicatorCalculator {
    /**
     * Calculates the Aggregate Indicator for the given Quotation.
     *
     * @param quotationsSortedByDate A List of quotations that build the trading history.
     * @param statistics             A List of statistics.
     * @param quotation              The Quotation for which the Aggregate Indicator is calculated.
     * @return The value of the aggregate indicator.
     */
    public int getAggregateIndicator(final List<Quotation> quotationsSortedByDate, final List<Statistic> statistics,
            final Quotation quotation) {
        return -1;
    }
}
