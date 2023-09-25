package backend.dao.quotation.persistence;

import java.util.Date;
import java.util.List;

import backend.controller.scan.IndicatorCalculator;
import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.tools.DateTools;

/**
 * Performs processing tasks during the determination of scan results based on the "RS_SINCE_DATE" ScanTemplate.
 *
 * @author Michael
 */
public class TemplateRsSinceDateProcessor {
    /**
     * DAO to access Quotation data.
     */
    private QuotationHibernateDAO quotationHibernateDAO;

    /**
     * Initializes the TemplateRsSinceDateProcessor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public TemplateRsSinceDateProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
    }

    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "RS_SINCE_DATE". The method
     * calculates and sets the RS number beginning from the given date.
     *
     * @param startDateAsString The start date for calculation of the RS number.
     * @param quotations        The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    public void postProcessingRsSinceDate(final String startDateAsString, final List<Quotation> quotations)
            throws Exception {

        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
        PerformanceCalculator performanceCalculator = new PerformanceCalculator();
        Date startDate = DateTools.convertStringToDate(startDateAsString);
        QuotationArray quotationsOfInstrument = new QuotationArray();
        Quotation quotationOfDate;
        int quotationOfDateIndex;
        float rsPercent;

        // Calculate the price performance from the start date to the current date.
        for (Quotation currentQuotation : quotations) {
            quotationsOfInstrument.setQuotations(
                    this.quotationHibernateDAO.getQuotationsOfInstrument(currentQuotation.getInstrument().getId()));
            quotationOfDateIndex = quotationsOfInstrument.getIndexOfQuotationWithDate(startDate);
            quotationOfDate = quotationsOfInstrument.getQuotations().get(quotationOfDateIndex);

            rsPercent = performanceCalculator.getPerformance(currentQuotation, quotationOfDate);
            currentQuotation.getIndicator().setRsPercentSum(rsPercent);
        }

        // Calculate the RS numbers based on the newly calculated performance.
        indicatorCalculator.calculateRsNumbers(quotations);
    }
}
