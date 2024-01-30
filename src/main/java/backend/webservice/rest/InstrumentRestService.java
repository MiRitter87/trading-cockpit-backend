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
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.InstrumentWS;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.InstrumentService;

/**
 * WebService for instrument access using REST technology.
 *
 * @author Michael
 */
@Path("/instruments")
public class InstrumentRestService {
    /**
     * Provides the instrument with the given ID.
     *
     * @param id The ID of the instrument.
     * @return The instrument with the given ID.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getInstrument(@PathParam("id") final Integer id) {
        InstrumentService instrumentService = new InstrumentService();
        return instrumentService.getInstrument(id);
    }

    /**
     * Provides a list of all instruments.
     *
     * @param instrumentType The type of the instruments requested.
     * @return A list of all instruments.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getInstruments(@QueryParam("instrumentType") final InstrumentType instrumentType) {
        InstrumentService instrumentService = new InstrumentService();
        return instrumentService.getInstruments(instrumentType);
    }

    /**
     * Adds an instrument.
     *
     * @param instrument The instrument to be added.
     * @return The result of the add function.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult addInstrument(final InstrumentWS instrument) {
        InstrumentService instrumentService = new InstrumentService();
        return instrumentService.addInstrument(instrument);
    }

    /**
     * Updates an existing instrument.
     *
     * @param instrument The instrument to be updated.
     * @return The result of the update function.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult updateInstrument(final InstrumentWS instrument) {
        InstrumentService instrumentService = new InstrumentService();
        return instrumentService.updateInstrument(instrument);
    }

    /**
     * Deletes the instrument with the given id.
     *
     * @param id The id of the instrument to be deleted.
     * @return The result of the delete function.
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult deleteInstrument(@PathParam("id") final Integer id) {
        InstrumentService instrumentService = new InstrumentService();
        return instrumentService.deleteInstrument(id);
    }

    /**
     * Checks the health of the Instrument with the given id.
     *
     * @param id        The ID of the instrument.
     * @param startDate The start date for the health check. Format used: yyyy-MM-dd
     * @param profile   The HealthCheckProfile that is being used.
     * @return A Protocol with health information about the given Instrument.
     */
    @GET
    @Path("/{id}/health/startDate")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getHealthProtocolWithStartDate(@PathParam("id") final Integer id,
            @QueryParam("startDate") final String startDate, @QueryParam("profile") final HealthCheckProfile profile) {
        InstrumentService instrumentService = new InstrumentService();
        return instrumentService.getHealthProtocolWithStartDate(id, startDate, profile);
    }
}
