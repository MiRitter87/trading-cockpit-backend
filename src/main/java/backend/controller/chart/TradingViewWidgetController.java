package backend.controller.chart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;

/**
 * Constructs and provides customized Trading View widgets.
 *
 * @author Michael
 */
public class TradingViewWidgetController {
    /**
     * Placeholder for the symbol used in a template.
     */
    private static final String PLACEHOLDER_SYMBOL = "{symbol}";

    /**
     * Placeholder for the stock exchange used in a template.
     */
    private static final String PLACEHOLDER_EXCHANGE = "{exchange}";

    /**
     * Placeholder for the company name used in a template.
     */
    private static final String PLACEHOLDER_COMPANY = "{company-name}";

    /**
     * Provides a HTML page embedding a TradingView chart widget.
     *
     * @param instrumentId The ID of the Instrument displayed in the chart widget.
     * @return A HTML page containing the chart widget.
     * @throws Exception Failed to create chart widget.
     */
    public InputStream getChartWidget(final Integer instrumentId) throws Exception {
        String htmlTemplate;
        InputStream htmlInputStream;
        Instrument instrument = this.getInstrument(instrumentId);

        htmlInputStream = getClass().getClassLoader()
                .getResourceAsStream("html-templates/tradingview-chart-widget.html");

        htmlTemplate = new String(htmlInputStream.readAllBytes(), StandardCharsets.UTF_8);
        htmlTemplate = this.replacePlaceholders(htmlTemplate, instrument);

        return new ByteArrayInputStream(htmlTemplate.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Provides the Instrument based on its ID.
     *
     * @param instrumentId The Instrument ID.
     * @return The Instrument.
     * @throws Exception Failed to fetch Instrument.
     */
    private Instrument getInstrument(final Integer instrumentId) throws Exception {
        InstrumentDAO instrumentDAO = DAOManager.getInstance().getInstrumentDAO();

        return instrumentDAO.getInstrument(instrumentId);
    }

    /**
     * Replaces instrument-specific placeholders in the template.
     *
     * @param htmlTemplate The HTML template.
     * @param instrument   The Instrument.
     * @return The instrument-specific HTML template.
     */
    private String replacePlaceholders(final String htmlTemplate, final Instrument instrument) {
        String replacedTemplate;

        replacedTemplate = htmlTemplate.replace(PLACEHOLDER_SYMBOL, instrument.getSymbol());
        replacedTemplate = replacedTemplate.replace(PLACEHOLDER_EXCHANGE,
                this.getStringOfExchange(instrument.getStockExchange()));
        replacedTemplate = replacedTemplate.replace(PLACEHOLDER_COMPANY, instrument.getName());

        return replacedTemplate;
    }

    /**
     * Gets the TradingView-specific stock exchange string.
     *
     * @param stockExchange The StockExchange of the Instrument.
     * @return The TradingView-specific stock exchange string.
     */
    private String getStringOfExchange(final StockExchange stockExchange) {
        String exchangeString = "";

        switch (stockExchange) {
        case NYSE:
            exchangeString = "NYSE";
            break;
        case NDQ:
            exchangeString = "NASDAQ";
            break;
        case AMEX:
            exchangeString = "AMEX";
            break;
        case OTC:
            exchangeString = "OTC";
            break;
        case TSX:
            exchangeString = "TSX";
            break;
        case TSXV:
            exchangeString = "TSXV";
            break;
        case CSE:
            exchangeString = "CSE";
            break;
        case LSE:
            exchangeString = "LSE";
            break;
        default:
            break;
        }

        return exchangeString;
    }
}
