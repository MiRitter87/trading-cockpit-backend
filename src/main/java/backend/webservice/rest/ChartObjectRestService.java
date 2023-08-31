package backend.webservice.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import backend.model.chart.HorizontalLineWS;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.ChartObjectService;

/**
 * WebService for chart object access using REST technology.
 *
 * @author Michael
 */
@Path("/chartObjects")
public class ChartObjectRestService {
    /**
     * Provides the HorizontalLine with the given ID.
     *
     * @param id The ID of the HorizontalLine.
     * @return The HorizontalLine with the given ID.
     */
    @GET
    @Path("/horizontalLine/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getHorizontalLine(@PathParam("id") final Integer id) {
        ChartObjectService chartObjectService = new ChartObjectService();
        return chartObjectService.getHorizontalLine(id);
    }

    /**
     * Provides a list of all horizontal lines. The Instrument id can be given optionally to only get horizontal lines
     * of a certain Instrument.
     *
     * @param instrumentId The Instrument id (can be null).
     * @return A list of all horizontal lines.
     */
    @GET
    @Path("/horizontalLine")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getHorizontalLines(@QueryParam("instrumentId") final Integer instrumentId) {
        ChartObjectService chartObjectService = new ChartObjectService();
        return chartObjectService.getHorizontalLines(instrumentId);
    }

    /**
     * Adds a HorizontalLine.
     *
     * @param horizontalLine The HorizontalLine to be added.
     * @return The result of the add function.
     */
    @POST
    @Path("/horizontalLine")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult addHorizontalLine(final HorizontalLineWS horizontalLine) {
        ChartObjectService chartObjectService = new ChartObjectService();
        return chartObjectService.addHorizontalLine(horizontalLine);
    }

    /**
     * Updates an existing HorizontalLine.
     *
     * @param horizontalLine The HorizontalLine to be updated.
     * @return The result of the update function.
     */
    @PUT
    @Path("/horizontalLine")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult updateHorizontalLine(final HorizontalLineWS horizontalLine) {
        ChartObjectService chartObjectService = new ChartObjectService();
        return chartObjectService.updateHorizontalLine(horizontalLine);
    }

    /**
     * Deletes the HorizontalLine with the given id.
     *
     * @param id The id of the HorizontalLine to be deleted.
     * @return The result of the delete function.
     */
    @DELETE
    @Path("/horizontalLine/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult deleteHorizontalLine(@PathParam("id") final Integer id) {
        ChartObjectService chartObjectService = new ChartObjectService();
        return chartObjectService.deleteHorizontalLine(id);
    }
}
