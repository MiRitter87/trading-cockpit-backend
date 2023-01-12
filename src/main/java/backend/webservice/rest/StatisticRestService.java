package backend.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceResult;
import backend.webservice.ChartType;
import backend.webservice.common.StatisticService;

/**
 * WebService for Statistic access using REST technology.
 * 
 * @author Michael
 */
@Path("/statistics")
public class StatisticRestService {
	/**
	 * Provides a list of all statistics of the given InstrumentType.
	 * 
	 * @param instrumentType The InstrumentType.
	 * @return A list of all statistics of the given InstrumentType.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public WebServiceResult getStatistics(@QueryParam("instrumentType") final InstrumentType instrumentType) {
		StatisticService statisticService = new StatisticService();
		return statisticService.getStatistics(instrumentType);
	}
	
	
	/**
	 * Provides a chart of the requested statistical data.
	 * 
	 * @param chartType The type of the requested chart.
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return The chart.
	 */
    @GET
    @Path("/chart")
    @Produces("image/png")
	public Response getStatisticsChart(@QueryParam("chartType") final ChartType chartType, @QueryParam("listId") final Integer listId,
			@QueryParam("instrumentId") final Integer instrumentId) {
    	
    	StatisticService statisticService = new StatisticService();
    	return statisticService.getStatisticsChart(chartType, listId, instrumentId);
	}
}
