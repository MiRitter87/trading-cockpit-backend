package backend.webservice.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

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
     * @param scanTemplate   The template that defines the parameters applied to the Scan results. Parameter can be
     *                       omitted.
     * @param instrumentType The type of Instrument that is requested.
     * @param startDate      The start date for the RS number determination. Format used: yyyy-MM-dd. Parameter can be
     *                       omitted (null)..
     * @param minLiquidity   The minimum trading liquidity that is required. Parameter can be omitted (null)..
     * @return A list of all quotations.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getQuotations(@QueryParam("scanTemplate") final ScanTemplate scanTemplate,
            @QueryParam("instrumentType") final InstrumentType instrumentType,
            @QueryParam("startDate") final String startDate, @QueryParam("minLiquidity") final Float minLiquidity) {

        QuotationService quotationService = new QuotationService();
        return quotationService.getQuotations(scanTemplate, instrumentType, startDate, minLiquidity);
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
