package backend.webservice.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.chart.data.PriceVolumeDataController;
import backend.model.instrument.QuotationArray;
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
        QuotationArray quotations;

        try {
            quotations = priceVolumeDataController.getPriceVolumeData(instrumentId);
            getPriceVolumeDataResult.setData(quotations);
        } catch (Exception e) {
            getPriceVolumeDataResult.addMessage(new WebServiceMessage(WebServiceMessageType.E,
                    this.resources.getString("chartData.priceVolume.getError")));
            LOGGER.error(MessageFormat.format(this.resources.getString("chartData.priceVolume.getError"), e));
        }

        return getPriceVolumeDataResult;
    }
}
