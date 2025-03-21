package backend.webservice.rest;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.webservice.Indicator;
import backend.webservice.common.JFreeChartService;

/**
 * WebService for Chart access using REST technology.
 *
 * @author Michael
 */
@Path("/charts/jFreeChart")
public class JFreeChartRestService {
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
        JFreeChartService chartService = new JFreeChartService();
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
        JFreeChartService chartService = new JFreeChartService();
        return chartService.getInstrumentsAboveSma50Chart(listId);
    }

    /**
     * Provides a chart with the percentage of instruments trading above their SMA(200).
     *
     * @param listId The ID of the list defining the instruments used chart creation (optional).
     * @return The chart.
     */
    @GET
    @Path("/instrumentsAboveSma200")
    @Produces("image/png")
    public Response getInstrumentsAboveSma200Chart(@QueryParam("listId") final Integer listId) {
        JFreeChartService chartService = new JFreeChartService();
        return chartService.getInstrumentsAboveSma200Chart(listId);
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
        JFreeChartService chartService = new JFreeChartService();
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
        JFreeChartService chartService = new JFreeChartService();
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
        JFreeChartService chartService = new JFreeChartService();
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
        JFreeChartService chartService = new JFreeChartService();
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
        JFreeChartService chartService = new JFreeChartService();
        return chartService.getPocketPivotsChart(instrumentId);
    }

    /**
     * Provides a price/volume chart of an Instrument.
     *
     * @param instrumentId   The ID of the Instrument used for chart creation.
     * @param overlays       The requested chart overlays.
     * @param withVolume     Show volume information.
     * @param indicator      The Indicator that is being displayed above the chart.
     * @param rsInstrumentId The ID of the Instrument used to build the RS line (only used if type of Indicator is
     *                       RS_LINE).
     * @return The chart.
     */
    @GET
    @Path("/priceVolume/{instrumentId}")
    @Produces("image/png")
    public Response getPriceVolumeChart(@PathParam("instrumentId") final Integer instrumentId,
            @QueryParam("overlays") final List<String> overlays, @QueryParam("withVolume") final boolean withVolume,
            @QueryParam("indicator") final Indicator indicator,
            @QueryParam("rsInstrumentId") final Integer rsInstrumentId) {

        JFreeChartService chartService = new JFreeChartService();
        return chartService.getPriceVolumeChart(instrumentId, overlays, withVolume, indicator, rsInstrumentId);
    }

    /**
     * Provides a miniature price/volume chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return The chart.
     */
    @GET
    @Path("/priceVolume/mini/{instrumentId}")
    @Produces("image/png")
    public Response getMiniPriceVolumeChart(@PathParam("instrumentId") final Integer instrumentId) {
        JFreeChartService chartService = new JFreeChartService();
        return chartService.getMiniPriceVolumeChart(instrumentId);
    }

    /**
     * Provides a chart of an Instrument marked with health check events.
     *
     * @param instrumentId   The ID of the Instrument used for chart creation.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The chart.
     */
    @GET
    @Path("/healthCheck/{instrumentId}")
    @Produces("image/png")
    public Response getHealthCheckChart(@PathParam("instrumentId") final Integer instrumentId,
            @QueryParam("profile") final HealthCheckProfile profile,
            @QueryParam("lookbackPeriod") final Integer lookbackPeriod) {

        JFreeChartService chartService = new JFreeChartService();
        return chartService.getHealthCheckChart(instrumentId, profile, lookbackPeriod);
    }

    /**
     * Provides a Aggregate Indicator chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @param listId       The ID of the list defining the instruments used to calculate % of stocks above SMA(50)
     *                     (optional).
     * @return The chart.
     */
    @GET
    @Path("/aggregateIndicator/{instrumentId}")
    @Produces("image/png")
    public Response getAggregateIndicatorChart(@PathParam("instrumentId") final Integer instrumentId,
            @QueryParam("listId") final Integer listId) {

        JFreeChartService chartService = new JFreeChartService();
        return chartService.getAggregateIndicatorChart(instrumentId, listId);
    }
}
