package backend.webservice.rest;

import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.ChartDataService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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

    /**
     * Provides data to build a price/volume chart of an Instrument. Additionally, events of a health check are
     * provided.
     *
     * @param instrumentId   The ID of the Instrument used for data determination.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The chart data.
     */
    @GET
    @Path("/healthCheck/{instrumentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getHealthCheckChart(@PathParam("instrumentId") final Integer instrumentId,
            @QueryParam("profile") final HealthCheckProfile profile,
            @QueryParam("lookbackPeriod") final Integer lookbackPeriod) {

        ChartDataService chartDataService = new ChartDataService();
        return chartDataService.getHealthCheckData(instrumentId, profile, lookbackPeriod);
    }
}
