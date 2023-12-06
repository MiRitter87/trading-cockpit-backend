package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by the upDownVolumeRatio of their Indicator.
 *
 * @author Michael
 */
public class QuotationUpDownVolumeRatioComparator implements Comparator<Quotation> {
    /**
     * Compares two quotations for order by the upDownVolumeRatio of their Indicator.
     */
    @Override
    public int compare(final Quotation quotation1, final Quotation quotation2) {
        // Handle possible null values of Indicator.
        if (quotation1.getIndicator() == null || quotation2.getIndicator() == null) {
            return 0;
        }

        if (quotation1.getIndicator().getUpDownVolumeRatio() > quotation2.getIndicator().getUpDownVolumeRatio()) {
            return -1;
        } else if (quotation1.getIndicator().getUpDownVolumeRatio() < quotation2.getIndicator()
                .getUpDownVolumeRatio()) {
            return 1;
        } else {
            return 0;
        }
    }
}
