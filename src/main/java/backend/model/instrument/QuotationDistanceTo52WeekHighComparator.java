package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by their distance to the 52-week high.
 *
 * @author Michael
 */
public class QuotationDistanceTo52WeekHighComparator implements Comparator<Quotation> {
    /**
     * Compares two quotations for order by the distance to the 52-week high of their Indicator.
     */
    @Override
    public int compare(final Quotation quotation1, final Quotation quotation2) {
        // Handle possible null values of Indicator.
        if (quotation1.getIndicator() == null || quotation2.getIndicator() == null) {
            return 0;
        }

        if (quotation1.getIndicator().getDistanceTo52WeekHigh() > quotation2.getIndicator().getDistanceTo52WeekHigh()) {
            return -1;
        } else if (quotation1.getIndicator().getDistanceTo52WeekHigh() < quotation2.getIndicator()
                .getDistanceTo52WeekHigh()) {
            return 1;
        } else {
            return 0;
        }
    }
}
