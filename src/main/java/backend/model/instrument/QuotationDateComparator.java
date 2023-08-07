package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by their date.
 *
 * @author Michael
 */
public class QuotationDateComparator implements Comparator<Quotation> {
    /**
     * Compares its two quotations for order by their date.
     */
    @Override
    public int compare(final Quotation quotation1, final Quotation quotation2) {
        if (quotation1.getDate().getTime() < quotation2.getDate().getTime()) {
            return 1;
        } else if (quotation1.getDate().getTime() > quotation2.getDate().getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
