package backend.webservice.rest;

import java.util.Date;

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
	 * @param id The ID of the instrument.
	 * @param startDate The start date for the health check.
	 * @return A Protocol with health information about the given Instrument.
	 */
	@GET
	@Path("/{id}/health")
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getInstrumentHealthProtocol(@PathParam("id") final Integer id, @QueryParam("startDate") final Date startDate) {
		InstrumentService instrumentService = new InstrumentService();
		return instrumentService.getInstrument(id);
	}
}
