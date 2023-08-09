package backend.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import backend.webservice.Indicator;
import backend.webservice.common.ChartService;

/**
 * WebService for Chart access using REST technology.
 *
 * @author Michael
 */
@Path("/charts")
public class ChartRestService {

    /**
     * Provides a chart with the cumulative Advance Decline Number.
     *
     * @param listId The ID of the list defining the instruments used chart creation (optional).
     * @return The chart.
     */
    @GET
    @Path("/cumulativeADNumber")
    @Produces("image/png")
    public Response getAdvanceDeclineNumberChart(@QueryParam("listId") final Integer listId) {
        ChartService chartService = new ChartService();
        return chartService.getAdvanceDeclineNumberChart(listId);
    }

    /**
     * Provides a chart with the percentage of instruments trading above their SMA(50).
     *
     * @param listId The ID of the list defining the instruments used chart creation (optional).
     * @return The chart.
     */
    @GET
    @Path("/instrumentsAboveSma50")
    @Produces("image/png")
    public Response getInstrumentsAboveSma50Chart(@QueryParam("listId") final Integer listId) {
        ChartService chartService = new ChartService();
        return chartService.getInstrumentsAboveSma50Chart(listId);
    }

    /**
     * Provides a chart of an Instrument marked with Distribution Days.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     */
    @GET
    @Path("/distributionDays/{instrumentId}")
    @Produces("image/png")
    public Response getDistributionDaysChart(@PathParam("instrumentId") final Integer instrumentId) {
        ChartService chartService = new ChartService();
        return chartService.getDistributionDaysChart(instrumentId);
    }

    /**
     * Provides a chart of an Instrument marked with Follow-Through Days.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     */
    @GET
    @Path("/followThroughDays/{instrumentId}")
    @Produces("image/png")
    public Response getFollowThroughDaysChart(@PathParam("instrumentId") final Integer instrumentId) {
        ChartService chartService = new ChartService();
        return chartService.getFollowThroughDaysChart(instrumentId);
    }

    /**
     * Provides a chart of the Ritter Market Trend.
     *
     * @param listId The ID of the list defining the instruments used chart creation (optional).
     * @return The chart.
     */
    @GET
    @Path("/ritterMarketTrend")
    @Produces("image/png")
    public Response getRitterMarketTrendChart(@QueryParam("listId") final Integer listId) {
        ChartService chartService = new ChartService();
        return chartService.getRitterMarketTrendChart(listId);
    }

    /**
     * Provides a chart of the Ritter Pattern Indicator.
     *
     * @param listId The ID of the list defining the instruments used chart creation (optional).
     * @return The chart.
     */
    @GET
    @Path("/ritterPatternIndicator")
    @Produces("image/png")
    public Response getRitterPatternIndicatorChart(@QueryParam("listId") final Integer listId) {
        ChartService chartService = new ChartService();
        return chartService.getRitterPatternIndicatorChart(listId);
    }

    /**
     * Provides a chart of an Instrument marked with Pocket Pivots.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     */
    @GET
    @Path("/pocketPivots/{instrumentId}")
    @Produces("image/png")
    public Response getPocketPivotsChart(@PathParam("instrumentId") final Integer instrumentId) {
        ChartService chartService = new ChartService();
        return chartService.getPocketPivotsChart(instrumentId);
    }

    /**
     * Provides a chart of an Instrument marked with Pocket Pivots.
     *
     * @param instrumentId    The ID of the Instrument used for chart creation.
     * @param withEma21       Show EMA(21) as overlay.
     * @param withSma50       Show SMA(50) as overlay.
     * @param withSma150      Show SMA(150) as overlay.
     * @param withSma200      Show SMA(200) as overlay.
     * @param withVolume      Show volume information.
     * @param withSma30Volume Show SMA(30) of volume.
     * @param indicator       The Indicator that is being displayed above the chart.
     * @param rsInstrumentId  The ID of the Instrument used to build the RS line (only used if type of Indicator is
     *                        RS_LINE).
     * @return The chart.
     */
    @GET
    @Path("/priceVolume/{instrumentId}")
    @Produces("image/png")
    public Response getPriceVolumeChart(@PathParam("instrumentId") final Integer instrumentId,
            @QueryParam("withEma21") final boolean withEma21, @QueryParam("withSma50") final boolean withSma50,
            @QueryParam("withSma150") final boolean withSma150, @QueryParam("withSma200") final boolean withSma200,
            @QueryParam("withVolume") final boolean withVolume,
            @QueryParam("withSma30Volume") final boolean withSma30Volume,
            @QueryParam("indicator") final Indicator indicator,
            @QueryParam("rsInstrumentId") final Integer rsInstrumentId) {

        ChartService chartService = new ChartService();
        return chartService.getPriceVolumeChart(instrumentId, withEma21, withSma50, withSma150, withSma200, withVolume,
                withSma30Volume, indicator, rsInstrumentId);
    }
}
