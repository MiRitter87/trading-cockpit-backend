package backend.webservice.rest;

import backend.webservice.common.TradingViewWidgetService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * WebService providing customized TradingView widgets using REST technology.
 *
 * @author Michael
 */
@Path("/widgets/tradingView")
public class TradingViewWidgetRestService {
    /**
     * Provides a HTML page embedding a TradingView chart widget.
     *
     * @param instrumentId The ID of the Instrument displayed in the chart widget.
     * @return A HTML page containing the chart widget.
     */
    @GET
    @Path("/chart/{instrumendId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getChartWidget(@PathParam("instrumendId") final Integer instrumentId) {
        TradingViewWidgetService widgetService = new TradingViewWidgetService();
        return widgetService.getChartWidget(instrumentId);
    }
}
