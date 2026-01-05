package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by the accDisRatio63Days of their Indicator.
 *
 * @author Michael
 */
public class QuotationAccDisRatioComparator implements Comparator<Quotation> {
    /**
     * Compares two quotations for order by the accDisRatio63Days of their Indicator.
     */
    @Override
    public int compare(final Quotation quotation1, final Quotation quotation2) {
        // Handle possible null values of Indicator.
        if (quotation1.getIndicator() == null || quotation2.getIndicator() == null) {
            return 0;
        }

        if (quotation1.getIndicator().getAccDisRatio63Days() > quotation2.getIndicator().getAccDisRatio63Days()) {
            return -1;
        } else if (quotation1.getIndicator().getAccDisRatio63Days() < quotation2.getIndicator()
                .getAccDisRatio63Days()) {
            return 1;
        } else {
            return 0;
        }
    }
}
