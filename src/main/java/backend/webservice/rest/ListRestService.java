package backend.webservice.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.annotation.JacksonFeatures;

import com.fasterxml.jackson.databind.SerializationFeature;

import backend.model.list.ListWS;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.ListService;

/**
 * WebService for list access using REST technology.
 *
 * @author Michael
 */
@Path("/lists")
public class ListRestService {
    /**
     * Provides the list with the given ID.
     *
     * @param id The ID of the list.
     * @return The list with the given ID.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getList(@PathParam("id") final Integer id) {
        ListService listService = new ListService();
        return listService.getList(id);
    }

    /**
     * Provides a list of all lists.
     *
     * @return A list of all lists.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonFeatures(serializationDisable = { SerializationFeature.FAIL_ON_EMPTY_BEANS })
    public WebServiceResult getLists() {
        ListService listService = new ListService();
        return listService.getLists();
    }

    /**
     * Provides an Excel file that contains Quotation data of each Instrument of the List with the given ID. Those data
     * include Symbol, Date, Price, RS number and ATR% of the most recent Quotation of each Instrument.
     *
     * @param id The ID of the List.
     * @return The Excel file.
     */
    @GET
    @Path("/{id}/excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getQuotationDataOfListAsExcel(@PathParam("id") final Integer id) {
        ListService listService = new ListService();
        return listService.getQuotationDataOfListAsExcel(id);
    }

    /**
     * Adds a list.
     *
     * @param list The list to be added.
     * @return The result of the add function.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult addList(final ListWS list) {
        ListService listService = new ListService();
        return listService.addList(list);
    }

    /**
     * Updates an existing list.
     *
     * @param list The list to be updated.
     * @return The result of the update function.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult updateList(final ListWS list) {
        ListService listService = new ListService();
        return listService.updateList(list);
    }

    /**
     * Deletes the list with the given id.
     *
     * @param id The id of the list to be deleted.
     * @return The result of the delete function.
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult deleteList(@PathParam("id") final Integer id) {
        ListService listService = new ListService();
        return listService.deleteList(id);
    }
}
