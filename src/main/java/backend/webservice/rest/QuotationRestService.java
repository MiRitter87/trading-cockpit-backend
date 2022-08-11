package backend.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import backend.model.webservice.WebServiceResult;
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
	 * @return A list of all quotations.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getQuotations() {
		QuotationService quotationService = new QuotationService();
		return quotationService.getQuotations();
	}
}
