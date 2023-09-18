package backend.dao.quotation.persistence;

import java.util.List;

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


    }
}
