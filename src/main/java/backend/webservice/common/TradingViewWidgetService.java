package backend.webservice.common;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import backend.controller.chart.TradingViewWidgetController;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Common implementation of the TradingView widget WebService that can be used by multiple service interfaces like SOAP
 * or REST.
 *
 * @author Michael
 */
public class TradingViewWidgetService {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Application logging.
     */
    public static final Logger LOGGER = LogManager.getLogger(TradingViewWidgetService.class);

    /**
     * Provides a HTML page embedding a TradingView chart widget.
     *
     * @param instrumentId The ID of the Instrument displayed in the chart widget.
     * @return A HTML page containing the chart widget.
     */
    public Response getChartWidget(final Integer instrumentId) {
        TradingViewWidgetController widgetController = new TradingViewWidgetController();
        InputStream inputStream = null;

        try {
            inputStream = widgetController.getChartWidget(instrumentId);
        } catch (Exception exception) {
            LOGGER.error(MessageFormat.format(this.resources.getString("widgets.chart.getError"), instrumentId),
                    exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(inputStream).type(MediaType.TEXT_HTML).build();
    }
}
