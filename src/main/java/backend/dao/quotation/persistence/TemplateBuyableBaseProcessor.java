package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.model.instrument.Quotation;

/**
 * Performs post-processing tasks during the determination of scan results based on the "BUYABLE_BASE" ScanTemplate.
 *
 * @author Michael
 */
public class TemplateBuyableBaseProcessor {
    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "BUYABLE_BASE". The method
     * checks for each Instrument of the given quotations if the price is at least 1,5 * ATRP(20) above the EMA(21).
     * Extended quotations are removed from the List of quotations.
     *
     * @param quotations The quotations on which the post processing is performed.
     */
    public void postProcessingBuyableBase(final List<Quotation> quotations) {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        Quotation currentQuotation;

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();

            if (this.isExtendedAboveEma21(currentQuotation)) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Checks if the given quotation is extended by at least 1,5 * ATRP(20) above the EMA(21).
     *
     * @param quotation The Quotation to be checked.
     * @return true, if Quotation is extended above EMA(21).
     */
    private boolean isExtendedAboveEma21(final Quotation quotation) {
        final int hundredPercent = 100;
        final float atrpFactor = 1.5f;
        final float extendedFactor = 1
                + (quotation.getIndicator().getAverageTrueRangePercent20() * atrpFactor / hundredPercent);
        float extendedPrice = quotation.getMovingAverageData().getEma21() * extendedFactor;

        if (quotation.getClose().floatValue() >= extendedPrice) {
            return true;
        }

        return false;
    }
}
