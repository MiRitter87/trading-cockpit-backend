package backend.controller.chart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Constructs and provides customized Trading View widgets.
 *
 * @author Michael
 */
public class TradingViewWidgetController {
    /**
     * Provides a HTML page embedding a TradingView chart widget.
     *
     * @param instrumentId The ID of the Instrument displayed in the chart widget.
     * @return A HTML page containing the chart widget.
     * @throws IOException Failed to process widget template.
     */
    public InputStream getChartWidget(final Integer instrumentId) throws IOException {
        String htmlTemplate;
        InputStream htmlInputStream;

        htmlInputStream = getClass().getClassLoader()
                .getResourceAsStream("html-templates/tradingview-chart-widget.html");

        htmlTemplate = new String(htmlInputStream.readAllBytes(), StandardCharsets.UTF_8);

        // TODO Substitute placeholders in widget template.

        return new ByteArrayInputStream(htmlTemplate.getBytes(StandardCharsets.UTF_8));
    }
}
