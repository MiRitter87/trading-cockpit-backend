package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs processing tasks during the determination of scan results based on the "THREE_WEEKS_TIGHT" ScanTemplate.
 *
 * @author Michael
 */
public class TemplateThreeWeeksTightProcessor {
    /**
     * The percentage threshold that defines a trading range that constitutes a "three weeks tight" pattern.
     */
    private static final float THREE_WEEKS_TIGHT_THRESHOLD = 1.015f;

    /**
     * DAO to access Quotation data.
     */
    private QuotationHibernateDAO quotationHibernateDAO;

    /**
     * Initializes the TemplateThreeWeeksTightProcessor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public TemplateThreeWeeksTightProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
    }

    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "THREE_WEEKS_TIGHT". The method
     * checks for each Instrument of the given quotations if the Instrument has closed three weeks in a row within a
     * tight range. Those quotations that do not match the template are removed from the List of quotations.
     *
     * @param quotations The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    public void postProcessingThreeWeeksTight(final List<Quotation> quotations) throws Exception {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        QuotationArray quotationArray;
        List<Quotation> weeklyQuotations;
        Quotation currentQuotation;

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();
            quotationArray = new QuotationArray(
                    this.quotationHibernateDAO.getQuotationsOfInstrument(currentQuotation.getInstrument().getId()));
            weeklyQuotations = quotationArray.getWeeklyQuotations(null);

            if (!this.isThreeWeeksTight(weeklyQuotations)) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Checks if the most recent three weekly quotations have closed within a tight range.
     *
     * @param weeklyQuotations Weekly quotations.
     * @return true, if last three quotations close within a tight range; false if not.
     */
    private boolean isThreeWeeksTight(final List<Quotation> weeklyQuotations) {
        double upperBound = 0;
        double lowerBound = 0;
        Quotation currentQuotation;
        final int numberOfWeeks = 3;

        if (weeklyQuotations.size() < numberOfWeeks) {
            return false;
        }

        for (int i = 0; i < numberOfWeeks; i++) {
            currentQuotation = weeklyQuotations.get(i);

            // Initialize the upper and lower boundary for the trading range based on the newest weekly Quotation.
            if (i == 0) {
                lowerBound = currentQuotation.getClose().doubleValue() * (2 - THREE_WEEKS_TIGHT_THRESHOLD);
                upperBound = currentQuotation.getClose().doubleValue() * THREE_WEEKS_TIGHT_THRESHOLD;
            } else {
                if (currentQuotation.getClose().doubleValue() < lowerBound
                        || currentQuotation.getClose().doubleValue() > upperBound) {
                    return false;
                }
            }
        }

        return true;
    }
}
