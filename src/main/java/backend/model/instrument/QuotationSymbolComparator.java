package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by the symbol of their Instrument.
 *
 * @author Michael
 */
public class QuotationSymbolComparator implements Comparator<Quotation> {
    /**
     * Compares its two quotations for order by the symbol of their Instrument.
     */
    @Override
    public int compare(final Quotation quotation1, final Quotation quotation2) {
        if (quotation1.getInstrument() == null || quotation2.getInstrument() == null) {
            return 0;
        }

        return quotation1.getInstrument().getSymbol().compareTo(quotation2.getInstrument().getSymbol());
    }
}
