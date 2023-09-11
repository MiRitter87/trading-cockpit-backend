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

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.annotation.JacksonFeatures;

import com.fasterxml.jackson.databind.SerializationFeature;

import backend.model.scan.ScanWS;
import backend.model.webservice.WebServiceResult;
import backend.webservice.common.ScanService;

/**
 * WebService for scan access using REST technology.
 *
 * @author Michael
 */
@Path("/scans")
public class ScanRestService {
    /**
     * Provides the scan with the given ID.
     *
     * @param id The ID of the scan.
     * @return The scan with the given ID.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getScan(@PathParam("id") final Integer id) {
        ScanService scanService = new ScanService();
        return scanService.getScan(id);
    }

    /**
     * Provides a list of all scans.
     *
     * @return A list of all scans.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonFeatures(serializationDisable = {SerializationFeature.FAIL_ON_EMPTY_BEANS})
    public WebServiceResult getScans() {
        ScanService scanService = new ScanService();
        return scanService.getScans();
    }

    /**
     * Adds a scan.
     *
     * @param scan The scan to be added.
     * @return The result of the add function.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult addScan(final ScanWS scan) {
        ScanService scanService = new ScanService();
        return scanService.addScan(scan);
    }

    /**
     * Updates an existing scan.
     *
     * @param scan The scan to be updated.
     * @return The result of the update function.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult updateScan(final ScanWS scan) {
        ScanService scanService = new ScanService();
        return scanService.updateScan(scan);
    }

    /**
     * Deletes the scan with the given id.
     *
     * @param id The id of the scan to be deleted.
     * @return The result of the delete function.
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult deleteScan(@PathParam("id") final Integer id) {
        ScanService scanService = new ScanService();
        return scanService.deleteScan(id);
    }
}
