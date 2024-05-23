package backend.webservice.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceResult;
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
     * @param instrumentType  The InstrumentType.
     * @param sectorId        The id of the sector the statistics belong to (can be null).
     * @param industryGroupId The id of the industry group the statistics belong to (can be null).
     * @return A list of all statistics of the given InstrumentType.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public WebServiceResult getStatistics(@QueryParam("instrumentType") final InstrumentType instrumentType,
            @QueryParam("sectorId") final Integer sectorId,
            @QueryParam("industryGroupId") final Integer industryGroupId) {

        StatisticService statisticService = new StatisticService();
        return statisticService.getStatistics(instrumentType, sectorId, industryGroupId);
    }
}
