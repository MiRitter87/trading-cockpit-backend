package backend.webservice.rest;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

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
     * @param overlays        The requested chart overlays.
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
            @QueryParam("overlays") final List<String> overlays, @QueryParam("withVolume") final boolean withVolume,
            @QueryParam("withSma30Volume") final boolean withSma30Volume,
            @QueryParam("indicator") final Indicator indicator,
            @QueryParam("rsInstrumentId") final Integer rsInstrumentId) {

        ChartService chartService = new ChartService();
        return chartService.getPriceVolumeChart(instrumentId, overlays, withVolume, withSma30Volume, indicator,
                rsInstrumentId);
    }
}
