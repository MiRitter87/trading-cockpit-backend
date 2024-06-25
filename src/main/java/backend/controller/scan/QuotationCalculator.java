package backend.controller.scan;

import java.util.List;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Calculates quotations based on existing quotations. A possible use-case is to calculate the quotations of an index
 * based on the index components quotations.
 *
 * @author Michael
 */
public class QuotationCalculator {
    /**
     * Provides a List of calculated quotations based on the given List of instruments with their quotations.
     *
     * @param instruments A List of instruments with their quotations.
     * @return A List of calculated quotations.
     */
    public List<Quotation> getCalculatedQuotations(final List<Instrument> instruments) {
        return null;
    }
}
