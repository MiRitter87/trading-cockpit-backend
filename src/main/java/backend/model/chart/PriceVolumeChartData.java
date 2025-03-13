package backend.model.chart;

import backend.model.instrument.QuotationArray;

/**
 * A collection of data that are used to construct a price/volume chart.
 *
 * @author Michael
 */
public class PriceVolumeChartData {
    /**
     * The quotations.
     */
    private QuotationArray quotations;

    /**
     * @return the quotations
     */
    public QuotationArray getQuotations() {
        return quotations;
    }

    /**
     * @param quotations the quotations to set
     */
    public void setQuotations(final QuotationArray quotations) {
        this.quotations = quotations;
    }
}
