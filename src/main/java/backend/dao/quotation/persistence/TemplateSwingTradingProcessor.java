package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Performs processing tasks during the determination of scan results based on the "SWING_TRADING_ENVIRONMENT"
 * ScanTemplate.
 *
 * @author Michael
 */
public class TemplateSwingTradingProcessor {
    /**
     * DAO to access Quotation data.
     */
    private QuotationHibernateDAO quotationHibernateDAO;

    /**
     * Initializes the TemplateSwingTradingProcessor.
     *
     * @param quotationHibernateDAO DAO to access Quotation data.
     */
    public TemplateSwingTradingProcessor(final QuotationHibernateDAO quotationHibernateDAO) {
        this.quotationHibernateDAO = quotationHibernateDAO;
    }

    /**
     * Performs post processing tasks for the given quotations based on the ScanTemplate "SWING_TRADING_ENVIRONMENT".
     * The method checks for each Instrument of the given quotations if the SMA(10) as well as the SMA(20) is rising.
     * Those quotations that do not match the template are removed from the List of quotations.
     *
     * @param quotations The quotations on which the post processing is performed.
     * @throws Exception Post processing failed.
     */
    public void postProcessingSwingTradingEnvironment(final List<Quotation> quotations) throws Exception {
        Iterator<Quotation> quotationIterator = quotations.iterator();
        QuotationArray quotationArray;
        Quotation currentQuotation;

        while (quotationIterator.hasNext()) {
            currentQuotation = quotationIterator.next();
            quotationArray = new QuotationArray(
                    this.quotationHibernateDAO.getQuotationsOfInstrument(currentQuotation.getInstrument().getId()));

            if (!this.isSwingTradingEnvironment(quotationArray)) {
                quotationIterator.remove();
            }
        }
    }

    /**
     * Checks if the given quotations constitute a "Swing Trading Environment".
     *
     * @param quotationArray The trading history of an Instrument.
     * @return true, if trading history constitutes a Swing Trading Environment.
     */
    private boolean isSwingTradingEnvironment(final QuotationArray quotationArray) {
        Quotation currentQuotation;
        Quotation previousQuotation;

        if (quotationArray.getQuotations().size() < 2) {
            return false;
        }

        quotationArray.sortQuotationsByDate();
        currentQuotation = quotationArray.getQuotations().get(0);
        previousQuotation = quotationArray.getQuotations().get(1);

        if (currentQuotation.getIndicator() == null || previousQuotation.getIndicator() == null) {
            return false;
        }

        if ((currentQuotation.getIndicator().getSma10() > previousQuotation.getIndicator().getSma10())
                && (currentQuotation.getIndicator().getSma20() > previousQuotation.getIndicator().getSma20())) {
            return true;
        }

        return false;
    }
}
