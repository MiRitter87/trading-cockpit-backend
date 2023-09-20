package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.controller.RatioCalculationController;
import backend.controller.scan.IndicatorCalculator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.webservice.ScanTemplate;

/**
 * Performs processing tasks during the determination of scan results based on the various ScanTemplates for Relative
 * Strength Line near 52-week high.
 *
 * @author Michael
 */
public class TemplateRsNearHighProcessor {
    /**
     * DAO to access Quotation data.
     */
    private QuotationHibernateDAO quotationHibernateDAO;

    /**
     * Initializes the TemplateRsNearHighProcessor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public TemplateRsNearHighProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
    }

    /**
     * Performs post processing tasks for the given quotations based on the given ScanTemplate. The method checks for
     * each Instrument if the RS-Line trades near the 52-week high. Those quotations that do not match the template are
     * removed from the List of quotations.
     *
     * @param scanTemplate The ScanTemplate.
     * @param quotations   The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    public void postProcessingRsNearHigh(final ScanTemplate scanTemplate, final List<Quotation> quotations)
            throws Exception {

        Iterator<Quotation> quotationIterator = quotations.iterator();
        Quotation currentQuotation;
        Instrument divisor;

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();
            divisor = this.getRsLineDivisor(scanTemplate, currentQuotation.getInstrument());

            if (!this.isRsLineNearHigh(currentQuotation.getInstrument(), divisor)) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Determines the Instrument used for RS-Line calculation based on the given ScanTemplate.
     *
     * @param scanTemplate The ScanTemplate.
     * @param instrument   The Instrument for which the RS-Line is being determined.
     * @return The Instrument used as divisor for RS-Line calculation.
     */
    private Instrument getRsLineDivisor(final ScanTemplate scanTemplate, final Instrument instrument) {
        if (scanTemplate == ScanTemplate.RS_NEAR_HIGH_IG) {
            return instrument.getIndustryGroup();
        }

        return null;
    }

    /**
     * Checks if the RS-Line trades near its 52-week high.
     *
     * @param dividend The Instrument used as dividend for RS-Line calculation.
     * @param divisor  The Instrument used as divisor for RS-Line calculation.
     * @return true, if RS-Line trades near its 52-week high; false, if not.
     * @throws Exception Failed to calculate RS-Line.
     */
    private boolean isRsLineNearHigh(final Instrument dividend, final Instrument divisor) throws Exception {
        IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
        final float percentNearHighThreshold = -5;
        QuotationArray rsLineQuotations = this.getRsLineQuotations(dividend, divisor);
        float distanceTo52WeekHigh = indicatorCalculator
                .getDistanceTo52WeekHigh(rsLineQuotations.getQuotations().get(0), rsLineQuotations);

        if (distanceTo52WeekHigh >= percentNearHighThreshold) {
            return true;
        }

        return false;
    }

    /**
     * Calculates the quotations that build the RS-Line.
     *
     * @param dividend The Instrument used as dividend for RS-Line calculation.
     * @param divisor  The Instrument used as divisor for RS-Line calculation.
     * @return The quotations that build the RS-Line.
     * @throws Exception Failed to calculate RS-Line.
     */
    private QuotationArray getRsLineQuotations(final Instrument dividend, final Instrument divisor) throws Exception {
        RatioCalculationController ratioCalculator = new RatioCalculationController();
        QuotationArray rsLineQuotations = new QuotationArray();

        if (divisor == null) {
            throw new Exception(
                    "No divisor Instrument defined to calculate RS-Line of intrument with ID " + dividend.getId());
        }

        dividend.setQuotations(this.quotationHibernateDAO.getQuotationsOfInstrument(dividend.getId()));
        divisor.setQuotations(this.quotationHibernateDAO.getQuotationsOfInstrument(divisor.getId()));

        rsLineQuotations.setQuotations(ratioCalculator.getRatios(dividend, divisor));
        rsLineQuotations.sortQuotationsByDate();

        return rsLineQuotations;
    }
}
