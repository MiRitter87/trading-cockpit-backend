package backend.webservice.common;

import jakarta.ws.rs.core.Response;

/**
 * Common implementation of the TradingView widget WebService that can be used by multiple service interfaces like SOAP
 * or REST.
 *
 * @author Michael
 */
public class TradingViewWidgetService {
    /**
     * Provides a HTML page embedding a TradingView chart widget.
     *
     * @param instrumentId The ID of the Instrument displayed in the chart widget.
     * @return A HTML page containing the chart widget.
     */
    public Response getChartWidget(final Integer instrumentId) {
        return null;
    }
}
