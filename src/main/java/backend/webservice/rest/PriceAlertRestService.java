package backend.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlertWS;
import backend.model.priceAlert.TriggerStatus;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.PriceAlertService;

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
     * @param triggerStatus The TriggerStatus of the requested price alerts.
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
}
