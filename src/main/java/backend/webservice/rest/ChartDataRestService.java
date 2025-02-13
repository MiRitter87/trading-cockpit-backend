package backend.webservice.rest;

import backend.model.webservice.WebServiceResult;
import backend.webservice.common.ChartDataService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * WebService for chart data access using REST technology. The service provides raw data to create multiple charts by
 * the consumer.
 *
 * @author Michael
 */
@Path("/charts/data")
public class ChartDataRestService {
    /**
     * Provides data to build a price/volume chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument whose data are requested.
     * @return The chart data.
     */
    @GET
    @Path("/priceVolume/{instrumentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getPriceVolumeData(@PathParam("instrumentId") final Integer instrumentId) {
        ChartDataService chartDataService = new ChartDataService();
        return chartDataService.getPriceVolumeData(instrumentId);
    }
}
