package backend.webservice.rest;

import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlertWS;
import backend.model.priceAlert.TriggerStatus;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.PriceAlertService;
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
import jakarta.ws.rs.core.Response;

/**
 * WebService for price alert access using REST technology.
 *
 * @author Michael
 */
@Path("/priceAlerts")
public class PriceAlertRestService {
    /**
     * Provides the price alert with the given ID.
     *
     * @param id The ID of the price alert.
     * @return The price alert with the given ID.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getPriceAlert(@PathParam("id") final Integer id) {
        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.getPriceAlert(id);
    }

    /**
     * Provides a list of all price alerts.
     *
     * @param triggerStatus      The TriggerStatus of the requested price alerts.
     * @param confirmationStatus The ConfirmationStatus of the requested price alerts.
     * @return A list of all price alerts.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getPriceAlerts(@QueryParam("triggerStatusQuery") final TriggerStatus triggerStatus,
            @QueryParam("confirmationStatusQuery") final ConfirmationStatus confirmationStatus) {

        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.getPriceAlerts(triggerStatus, confirmationStatus);
    }

    /**
     * Adds a price alert.
     *
     * @param priceAlert The price alert to be added.
     * @return The result of the add function.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult addPriceAlert(final PriceAlertWS priceAlert) {
        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.addPriceAlert(priceAlert);
    }

    /**
     * Updates an existing price alert.
     *
     * @param priceAlert The price alert to be updated.
     * @return The result of the update function.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult updatePriceAlert(final PriceAlertWS priceAlert) {
        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.updatePriceAlert(priceAlert);
    }

    /**
     * Deletes the price alert with the given id.
     *
     * @param id The id of the price alert to be deleted.
     * @return The result of the delete function.
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult deletePriceAlert(@PathParam("id") final Integer id) {
        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.deletePriceAlert(id);
    }

    /**
     * Exports all price alerts in a serialized form.
     *
     * @return All price alerts.
     */
    @GET
    @Path("/export")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportPriceAlerts() {
        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.exportPriceAlerts();
    }

    /**
     * Imports the price alerts provided.
     *
     * @param priceAlertsAsJson A JSON String containing all price alerts to be imported.
     * @return The result of the import function.
     */
    @POST
    @Path("/import")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult importPriceAlerts(final String priceAlertsAsJson) {
        PriceAlertService priceAlertService = new PriceAlertService();
        return priceAlertService.importPriceAlerts(priceAlertsAsJson);
    }
}
