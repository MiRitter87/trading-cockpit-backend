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
        // Handle possible null values of Indicator.
        if (quotation1.getIndicator() == null || quotation2.getIndicator() == null) {
            return 0;
        }

        // Handle possible null values of RelativeStrengthData.
        if (quotation1.getIndicator().getRelativeStrengthData() == null
                || quotation2.getIndicator().getRelativeStrengthData() == null) {
            return 0;
        }

        // Compare if both indicators are defined.
        if (quotation1.getIndicator().getRelativeStrengthData().getRsPercentSum() > quotation2.getIndicator()
                .getRelativeStrengthData().getRsPercentSum()) {
            return -1;
        } else if (quotation1.getIndicator().getRelativeStrengthData().getRsPercentSum() < quotation2.getIndicator()
                .getRelativeStrengthData().getRsPercentSum()) {
            return 1;
        } else {
            return 0;
        }
    }
}
