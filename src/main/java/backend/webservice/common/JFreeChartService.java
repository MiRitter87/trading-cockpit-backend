package backend.webservice.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import backend.controller.NoQuotationsExistException;
import backend.controller.chart.priceVolume.DistributionDaysChartController;
import backend.controller.chart.priceVolume.FollowThroughDaysChartController;
import backend.controller.chart.priceVolume.HealthCheckChartController;
import backend.controller.chart.priceVolume.MiniPriceVolumeChartController;
import backend.controller.chart.priceVolume.PocketPivotChartController;
import backend.controller.chart.priceVolume.PriceVolumeChartController;
import backend.controller.chart.statistic.AboveSma200ChartController;
import backend.controller.chart.statistic.AboveSma50ChartController;
import backend.controller.chart.statistic.AdvanceDeclineNumberChartController;
import backend.controller.chart.statistic.AggregateIndicatorChartController;
import backend.controller.chart.statistic.RitterMarketTrendChartController;
import backend.controller.chart.statistic.RitterPatternIndicatorChartController;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.webservice.Indicator;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;

/**
 * Common implementation of the Chart WebService that can be used by multiple service interfaces like SOAP or REST. This
 * service provides several types of charts as PNG images. JFreeChart is used to render the charts.
 *
 * @author Michael
 */
public class JFreeChartService {
    /**
     * The standard width of charts.
     */
    private static final int CHART_WIDTH = 1600;

    /**
     * The standard height of charts.
     */
    private static final int CHART_HEIGHT = 700;

    /**
     * The width of miniature charts.
     */
    private static final int CHART_WIDTH_MINI = 600;

    /**
     * The height of miniature charts.
     */
    private static final int CHART_HEIGHT_MINI = 400;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(StatisticService.class);

    /**
     * Provides a chart with the cumulative Advance Decline Number.
     *
     * @param listId The ID of the list defining the instruments used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getAdvanceDeclineNumberChart(final Integer listId) {
        AdvanceDeclineNumberChartController adChartController;
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            adChartController = new AdvanceDeclineNumberChartController(listId);
            chart = adChartController.getAdvanceDeclineNumberChart();

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.adNumber.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart with the percentage of instruments trading above their SMA(50).
     *
     * @param listId The ID of the list defining the instruments used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getInstrumentsAboveSma50Chart(final Integer listId) {
        AboveSma50ChartController aboveSma50ChartController;
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            aboveSma50ChartController = new AboveSma50ChartController(listId);
            chart = aboveSma50ChartController.getInstrumentsAboveSma50Chart();

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.aboveSma50.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart with the percentage of instruments trading above their SMA(200).
     *
     * @param listId The ID of the list defining the instruments used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getInstrumentsAboveSma200Chart(final Integer listId) {
        AboveSma200ChartController aboveSma200ChartController;
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            aboveSma200ChartController = new AboveSma200ChartController(listId);
            chart = aboveSma200ChartController.getInstrumentsAboveSma200Chart();

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.aboveSma200.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of an Instrument marked with Distribution Days.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getDistributionDaysChart(final Integer instrumentId) {
        DistributionDaysChartController distributionDaysChartController = new DistributionDaysChartController();
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        if (instrumentId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            chart = distributionDaysChartController.getDistributionDaysChart(instrumentId);

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.distributionDays.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.distributionDays.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of an Instrument marked with Follow-Through Days.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getFollowThroughDaysChart(final Integer instrumentId) {
        FollowThroughDaysChartController ftdChartController = new FollowThroughDaysChartController();
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        if (instrumentId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            chart = ftdChartController.getFollowThroughDaysChart(instrumentId);

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.followThroughDays.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.followThroughDays.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of the Ritter Market Trend.
     *
     * @param listId The ID of the list defining the instruments used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getRitterMarketTrendChart(final Integer listId) {
        RitterMarketTrendChartController rmtChartController;
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            rmtChartController = new RitterMarketTrendChartController(listId);
            chart = rmtChartController.getRitterMarketTrendChart();

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.ritterMarketTrend.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of the Ritter Pattern Indicator.
     *
     * @param listId The ID of the list defining the instruments used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getRitterPatternIndicatorChart(final Integer listId) {
        RitterPatternIndicatorChartController rpiChartController;
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            rpiChartController = new RitterPatternIndicatorChartController(listId);
            chart = rpiChartController.getRitterPatternIndicatorChart();

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.ritterPatternIndicator.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of an Instrument marked with Pocket Pivots.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getPocketPivotsChart(final Integer instrumentId) {
        PocketPivotChartController pocketPivotChartController = new PocketPivotChartController();
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            chart = pocketPivotChartController.getPocketPivotsChart(instrumentId);

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.pocketPivots.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.pocketPivots.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of an Instrument with price and volume. Additional overlays and subplots can be added to the
     * chart on demand.
     *
     * @param instrumentId   The ID of the Instrument used for chart creation.
     * @param overlays       The requested chart overlays.
     * @param withVolume     Show volume information.
     * @param indicator      The Indicator that is being displayed above the chart.
     * @param rsInstrumentId The ID of the Instrument used to build the RS line (only used if type of Indicator is
     *                       RS_LINE).
     * @return A Response containing the generated chart.
     */
    public Response getPriceVolumeChart(final Integer instrumentId, final List<String> overlays,
            final boolean withVolume, final Indicator indicator, final Integer rsInstrumentId) {

        PriceVolumeChartController priceVolumeChartController = new PriceVolumeChartController();
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            chart = priceVolumeChartController.getPriceVolumeChart(instrumentId, overlays, withVolume, indicator,
                    rsInstrumentId);

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.priceVolume.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.priceVolume.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a miniature price/volume chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @return A Response containing the generated chart.
     */
    public Response getMiniPriceVolumeChart(final Integer instrumentId) {
        MiniPriceVolumeChartController miniPriceVolumeChartController = new MiniPriceVolumeChartController();
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            chart = miniPriceVolumeChartController.getMiniPriceVolumeChart(instrumentId);

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH_MINI, CHART_HEIGHT_MINI);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.priceVolumeMini.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.priceVolumeMini.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of an Instrument marked with health check events.
     *
     * @param instrumentId   The ID of the Instrument used for chart creation.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The chart.
     */
    public Response getHealthCheckChart(final Integer instrumentId, final HealthCheckProfile profile,
            final Integer lookbackPeriod) {

        HealthCheckChartController healthCheckChartController = new HealthCheckChartController();
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            chart = healthCheckChartController.getHealthCheckChart(instrumentId, profile, lookbackPeriod);

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.healthCheck.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.healthCheck.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }

    /**
     * Provides a chart of the Aggregate Indicator of an Instrument.
     *
     * @param instrumentId The ID of the Instrument used for chart creation.
     * @param listId       The ID of the list defining the instruments used to calculate % of stocks above SMA(50)
     *                     (optional).
     * @return A Response containing the generated chart.
     */
    public Response getAggregateIndicatorChart(final Integer instrumentId, final Integer listId) {
        AggregateIndicatorChartController aggregateIndicatorChartController;
        JFreeChart chart;
        StreamingOutput streamingOutput = null;

        try {
            aggregateIndicatorChartController = new AggregateIndicatorChartController(instrumentId, listId);
            chart = aggregateIndicatorChartController.getAggregateIndicatorChart();

            streamingOutput = new StreamingOutput() {
                @Override
                public void write(final OutputStream output) throws IOException, WebApplicationException {
                    ChartUtils.writeChartAsPNG(output, chart, CHART_WIDTH, CHART_HEIGHT);
                }
            };
        } catch (NoQuotationsExistException noQuotationsExistException) {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    this.resources.getString("chart.aggregateIndicator.noQuotationsError")).build();
        } catch (Exception exception) {
            LOGGER.error(this.resources.getString("chart.aggregateIndicator.getError"), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(streamingOutput).build();
    }
}
