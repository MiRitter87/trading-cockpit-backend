package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.model.instrument.Quotation;

/**
 * Performs processing tasks during the determination of scan results based on the "MA_PRICE_CONVERGENCE" ScanTemplate.
 *
 * @author Michael
 */
public class TemplateMaPriceConvergenceProcessor {
    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "MA_PRICE_CONVERGENCE". The
     * method checks for each Instrument of the given quotations if the closing price, EMA(21) and SMA(50) trade within
     * 2 times ATRP. Those quotations that do not match the template are removed from the List of quotations.
     *
     * @param quotations The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    public void postProcessingMaPriceConvergence(final List<Quotation> quotations) {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        Quotation currentQuotation;

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();

            if (!this.isConvergent(currentQuotation)) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Checks if the given quotation matches the convergence criteria for closing price, EMA(21) and SMA(50).
     *
     * @param quotation The Quotation to be checked.
     * @return true, if Quotation is convergent.
     */
    private boolean isConvergent(final Quotation quotation) {
        float min;
        float max;
        float minMaxPercentage;
        float twoTimesAtrp = quotation.getIndicator().getAverageTrueRangePercent20() * 2;
        final float hundredPercent = 100;

        min = Math.min(quotation.getMovingAverageData().getEma21(), quotation.getMovingAverageData().getSma50());
        min = Math.min(min, quotation.getClose().floatValue());

        max = Math.max(quotation.getMovingAverageData().getEma21(), quotation.getMovingAverageData().getSma50());
        max = Math.max(max, quotation.getClose().floatValue());

        if (min == 0 || max == 0) {
            return false;
        }

        minMaxPercentage = (max / min) - 1;
        minMaxPercentage = minMaxPercentage * hundredPercent;

        if (minMaxPercentage <= twoTimesAtrp) {
            return true;
        }

        return false;
    }
}
