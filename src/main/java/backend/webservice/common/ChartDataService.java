package backend.webservice.common;

import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the chart data WebService that can be used by multiple service interfaces like SOAP or REST.
 *
 * @author Michael
 */
public class ChartDataService {
    /**
     * Provides data to build a price/volume chart of an Instrument.
     *
     * @param instrumentId   The ID of the Instrument whose data are requested.
     * @return The chart data.
     */
    public WebServiceResult getPriceVolumeData(final Integer instrumentId) {
        return null;
    }
}
