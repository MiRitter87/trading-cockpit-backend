package backend.dao.quotation.persistence;

import java.util.Iterator;
import java.util.List;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.webservice.ScanTemplate;

/**
 * Performs processing tasks during the determination of scan results based on the various ScanTemplates for Relative
 * Strength Line near 52-week high.
 *
 * @author Michael
 */
public class TemplateRsNearHighProcessor {
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

            // 3. Check if RS-Line trades within 5% of 52w-high

            // 4. Remove Quotation from results if not
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
}
