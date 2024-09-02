package backend.webservice.rest;

import backend.model.webservice.WebServiceResult;
import backend.webservice.common.DashboardService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * WebService providing dashboard functionality using REST technology.
 *
 * @author Michael
 */
@Path("/dashboard")
public class DashboardRestService {
    /**
     * Determines the health status of the given market (sector or industry group).
     *
     * @param instrumentId The ID of the sector or industry group.
     * @param listId       The ID of the list defining the instruments used to calculate % of stocks above SMA(50)
     *                     (optional).
     * @return The health status.
     */
    @GET
    @Path("/marketHealthStatus/{instrumentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getMarketHealthStatus(@PathParam("instrumentId") final Integer instrumentId,
            @QueryParam("listId") final Integer listId) {
        DashboardService dashboardService = new DashboardService();
        return dashboardService.getMarketHealthStatus(instrumentId, listId);
    }
}
