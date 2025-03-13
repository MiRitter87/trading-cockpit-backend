package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.chart.data.HealthCheckDataController;
import backend.controller.chart.data.PriceVolumeDataController;
import backend.controller.instrumentCheck.HealthCheckProfile;
import backend.model.chart.HealthCheckChartData;
import backend.model.chart.PriceVolumeChartData;
import backend.model.webservice.WebServiceMessage;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;

/**
 * Common implementation of the chart data WebService that can be used by multiple service interfaces like SOAP or REST.
 *
 * @author Michael
 */
public class ChartDataService {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(ChartDataService.class);

    /**
     * Provides data to build a price/volume chart of an Instrument.
     *
     * @param instrumentId The ID of the Instrument whose data are requested.
     * @return The chart data.
     */
    public WebServiceResult getPriceVolumeData(final Integer instrumentId) {
        PriceVolumeDataController priceVolumeDataController = new PriceVolumeDataController();
        WebServiceResult getPriceVolumeDataResult = new WebServiceResult(null);
        PriceVolumeChartData chartData = new PriceVolumeChartData();

        try {
            chartData.setQuotations(priceVolumeDataController.getPriceVolumeData(instrumentId));
            getPriceVolumeDataResult.setData(chartData);
        } catch (Exception e) {
            getPriceVolumeDataResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("chartData.priceVolume.getError")));
            LOGGER.error(MessageFormat.format(this.resources.getString("chartData.priceVolume.getError"), e));
        }

        return getPriceVolumeDataResult;
    }

    /**
     * Provides data to build a price/volume chart of an Instrument. Additionally, events of a health check are
     * provided.
     *
     * @param instrumentId   The ID of the Instrument used for data determination.
     * @param profile        The HealthCheckProfile that is used.
     * @param lookbackPeriod The number of days taken into account for health check routines.
     * @return The chart data.
     */
    public WebServiceResult getHealthCheckData(final Integer instrumentId, final HealthCheckProfile profile,
            final Integer lookbackPeriod) {

        HealthCheckDataController healthCheckDataController = new HealthCheckDataController();
        WebServiceResult getHealthCheckDataResult = new WebServiceResult(null);
        HealthCheckChartData chartData;

        try {
            chartData = healthCheckDataController.getHealthCheckData(instrumentId, profile, lookbackPeriod);
            getHealthCheckDataResult.setData(chartData);
        } catch (Exception e) {
            getHealthCheckDataResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("chartData.healthCheck.getError")));
            LOGGER.error(MessageFormat.format(this.resources.getString("chartData.healthCheck.getError"), e));
        }

        return getHealthCheckDataResult;
    }
}
