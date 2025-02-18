package backend.controller.chart.data;

import java.util.List;

import backend.model.instrument.Quotation;

/**
 * Controller to provide data for the construction of a price/volume chart.
 *
 * @author Michael
 */
public class PriceVolumeDataController {
    /**
     * Provides price and volume data as well as indicators to construct a price/volume chart.
     *
     * @param instrumentId The ID of the Instrument used for chart data creation.
     * @return The price/volume chart data.
     * @throws Exception Chart data generation failed.
     */
    public List<Quotation> getPriceVolumeData(final Integer instrumentId) throws Exception {
        // Query quotations of Instrument with ID

        // Calculate BBW(10,2)

        // Calculate SlowSto(14,3)

        return null;
    }
}
