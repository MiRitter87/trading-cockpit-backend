package backend.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceResult;
import backend.webservice.ScanTemplate;
import backend.webservice.common.QuotationService;

/**
 * WebService for Quotation access using REST technology.
 * 
 * @author Michael
 */
@Path("/quotations")
public class QuotationRestService {
	/**
	 * Provides a list of all quotations.
	 * 
	 * @param scanTemplate The template that defines the parameters applied to the Scan results. Parameter can be omitted.
	 * @param instrumentType The type of Instrument that is requested.
	 * @param startDate The start date for the RS number determination. Format used: yyyy-MM-dd. Parameter can be omitted.
	 * @return A list of all quotations.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getQuotations(@QueryParam("scanTemplate") final ScanTemplate scanTemplate, 
			@QueryParam("instrumentType") final InstrumentType instrumentType,
			@QueryParam("startDate") final String startDate) {
		
		QuotationService quotationService = new QuotationService();
		return quotationService.getQuotations(scanTemplate, instrumentType, startDate);
	}
	
	
	/**
	 * Provides a List of all quotations of the Instrument with the given ID.
	 * 
	 * @param instrumentId The ID of the instrument.
	 * @return A List of all quotations of the Instrument with the given ID.
	 */
	@GET
	@Path("/{instrumentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getQuotations(@PathParam("instrumentId") final Integer instrumentId) {
		QuotationService quotationService = new QuotationService();
		return quotationService.getQuotationsOfInstrument(instrumentId);
	}
}