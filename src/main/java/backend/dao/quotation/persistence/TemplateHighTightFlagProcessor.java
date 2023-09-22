package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs processing tasks during the determination of scan results based on the "HIGH_TIGHT_FLAG" ScanTemplate.
 *
 * @author Michael
 */
public class TemplateHighTightFlagProcessor {
    /**
     * DAO to access Quotation data.
     */
    private QuotationHibernateDAO quotationHibernateDAO;

    /**
     * The percentage threshold that defines the minimum performance required for a "High Tight Flag". This value takes
     * a possible decline of up to 25% into account that may happen during the consolidation.
     */
    private static final float HIGH_TIGHT_FLAG_THRESHOLD = 75;

    /**
     * Initializes the TemplateHighTightFlagProcessor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public TemplateHighTightFlagProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
    }

    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "HIGH_TIGHT_FLAG". The method
     * checks for each Instrument of the given quotations if the price has advanced at least 75% within the last 14
     * weeks. Those quotations that do not match the template are removed from the List of quotations.
     *
     * @param quotations The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    public void postProcessingHighTightFlag(final List<Quotation> quotations) throws Exception {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        QuotationArray quotationArray;
        Quotation currentQuotation;

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();
            quotationArray = new QuotationArray(
                    this.quotationHibernateDAO.getQuotationsOfInstrument(currentQuotation.getInstrument().getId()));

            if (!this.isHighTightFlag(quotationArray)) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Checks if the given quotations constitute the pattern of a Hight Tight Flag.
     *
     * @param quotationArray The trading history of an Instrument.
     * @return true, if trading history constitutes a High Tight Flag.
     */
    private boolean isHighTightFlag(final QuotationArray quotationArray) {
        PerformanceCalculator performanceCalculator = new PerformanceCalculator();
        final int tradingDaysPerWeek = 5;
        final int maxWeeksForPattern = 14;
        final int maxDaysForPattern = tradingDaysPerWeek * maxWeeksForPattern;
        Quotation currentQuotation;
        Quotation maxQuotationForPattern;
        float performanceMaxQuotation;

        // Not enough trading days to evaluate pattern.
        if (quotationArray.getQuotations().size() <= maxDaysForPattern) {
            return false;
        }

        quotationArray.sortQuotationsByDate();
        currentQuotation = quotationArray.getQuotations().get(0);
        maxQuotationForPattern = quotationArray.getQuotations().get(maxDaysForPattern);

        performanceMaxQuotation = performanceCalculator.getPerformance(currentQuotation, maxQuotationForPattern);

        if (performanceMaxQuotation < HIGH_TIGHT_FLAG_THRESHOLD) {
            return false;
        }

        return true;
    }
}
