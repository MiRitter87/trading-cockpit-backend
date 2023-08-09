package backend.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
