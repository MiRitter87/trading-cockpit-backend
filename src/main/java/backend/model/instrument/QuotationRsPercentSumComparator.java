package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by the rsPercentSum of their Indicator.
 *
 * @author Michael
 */
public class QuotationRsPercentSumComparator implements Comparator<Quotation> {
    /**
     * Compares its two quotations for order by the rsPercentSum of their Indicator.
     */
    @Override
    public int compare(final Quotation quotation1, final Quotation quotation2) {
        // Handle possible null values of RelativeStrengthData.
        if (quotation1.getRelativeStrengthData() == null || quotation2.getRelativeStrengthData() == null) {
            return 0;
        }

        if (quotation1.getRelativeStrengthData().getRsPercentSum() > quotation2.getRelativeStrengthData()
                .getRsPercentSum()) {
            return -1;
        } else if (quotation1.getRelativeStrengthData().getRsPercentSum() < quotation2.getRelativeStrengthData()
                .getRsPercentSum()) {
            return 1;
        } else {
            return 0;
        }
    }
}
