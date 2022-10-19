package backend.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
	 * @param scanTemplate The template that defines the parameters applied to the Scan results.
	 * @param instrumentType The type of Instrument that is requested.
	 * @return A list of all quotations.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getQuotations(@QueryParam("scanTemplate") final ScanTemplate scanTemplate, 
			@QueryParam("instrumentType") final InstrumentType instrumentType) {
		
		QuotationService quotationService = new QuotationService();
		return quotationService.getQuotations(scanTemplate, instrumentType);
	}
}
