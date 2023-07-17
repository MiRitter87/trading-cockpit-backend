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
	 * Provides a list of all horizontal lines.
	 * The Instrument id can be given optionally to only get horizontal lines of a certain Instrument.
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
