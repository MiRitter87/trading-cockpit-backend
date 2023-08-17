package backend.dao.quotation.provider;

import java.math.BigDecimal;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the theglobeandmail.com website.
 *
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAO extends AbstractQuotationProviderDAO implements QuotationProviderDAO {
    /**
     * Placeholder for the symbol used in a query URL.
     */
    private static final String PLACEHOLDER_SYMBOL = "{symbol}";

    /**
     * Placeholder for the stock exchange used in a query URL.
     */
    private static final String PLACEHOLDER_EXCHANGE = "{exchange}";

    /**
     * Placeholder for the number of requested daily quotes in a query URL.
     */
    private static final String PLACEHOLDER_DAYS = "{days}";

    /**
     * URL to quote theglobeandmail.com: Current quotation.
     */
    private static final String BASE_URL_CURRENT_QUOTATION = "https://www.theglobeandmail.com/investing/markets/stocks/"
            + PLACEHOLDER_SYMBOL + PLACEHOLDER_EXCHANGE + "/";

    /**
     * URL to quote theglobeandmail.com: Historical quotations.
     */
    private static final String BASE_URL_QUOTATION_HISTORY = "https://globeandmail.pl.barchart.com/proxies/timeseries/"
            + "queryeod.ashx?symbol=" + PLACEHOLDER_SYMBOL + PLACEHOLDER_EXCHANGE + "&data=daily&maxrecords="
            + PLACEHOLDER_DAYS + "&volume=contract&order=asc&dividends=false&backadjust=false";

    /**
     * Initializes the QuotationProviderGlobeAndMailDAO.
     */
    public QuotationProviderGlobeAndMailDAO() {
        this.disableHtmlUnitLogging();
    }

    /**
     * Gets the current Quotation of the given Instrument.
     */
    @Override
    public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
        String url = this.getQueryUrlCurrentQuotation(instrument);
        WebClient webClient = new WebClient();
        HtmlPage htmlPage;
        Quotation quotation;

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        try {
            htmlPage = webClient.getPage(url);

            quotation = this.getQuotationFromHtmlPage(htmlPage, instrument);
        } finally {
            webClient.close();
        }

        return quotation;
    }

    /**
     * Gets the Quotation history.
     */
    @Override
    public List<Quotation> getQuotationHistory(final String symbol, final StockExchange stockExchange,
            final InstrumentType instrumentType, final Integer years) throws Exception {

        throw new Exception("Method is not supported.");
    }

    /**
     * Gets the current Quotation from the HTML page.
     *
     * @param htmlPage   The HTML page containing the Quotation information.
     * @param instrument The Instrument for which Quotation data are extracted.
     * @return The current Quotation.
     * @throws Exception Failed to extract Quotation data from given HTML page.
     */
    protected Quotation getQuotationFromHtmlPage(final HtmlPage htmlPage, final Instrument instrument)
            throws Exception {
        Quotation quotation = new Quotation();
        String currentPrice = "";
        final List<DomElement> spans = htmlPage.getElementsByTagName("span");

        for (DomElement element : spans) {
            if (element.getAttribute("class").equals("barchart-overview-field-value")) {
                DomElement firstChild = element.getFirstElementChild();
                String nameAttribute = firstChild.getAttribute("name");

                if (nameAttribute.equals("lastPrice")) {
                    currentPrice = firstChild.getAttribute("value");
                }
            }
        }

        if ("".equals(currentPrice)) {
            throw new Exception("The price could not be determined.");
        }

        quotation.setClose(new BigDecimal(currentPrice));
        quotation.setCurrency(this.getCurrencyForStockExchange(instrument.getStockExchange()));

        return quotation;
    }

    /**
     * Gets the query URL for the current quotation of the given Instrument.
     *
     * @param instrument The Instrument for which the query URL is determined.
     * @return The query URL.
     * @throws Exception URL could not be created.
     */
    protected String getQueryUrlCurrentQuotation(final Instrument instrument) throws Exception {
        String queryUrl = new String(BASE_URL_CURRENT_QUOTATION);

        if (instrument.getStockExchange() == StockExchange.LSE) {
            throw new Error("The DAO for TheGlobeAndMail does not provide current quotations for the exchange: "
                    + instrument.getStockExchange());
        }

        queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, instrument.getSymbol());
        queryUrl = queryUrl.replace(PLACEHOLDER_EXCHANGE, this.getExchangeForQueryURLCurrent(instrument));

        return queryUrl;
    }

    /**
     * Gets the query URL for the quotation history of the given symbol and stock exchange.
     *
     * @param symbol        The symbol to be queried.
     * @param stockExchange The stock exchange where the symbol is listed.
     * @param years         The number of years to be queried.
     * @return The query URL.
     */
    protected String getQueryUrlQuotationHistory(final String symbol, final StockExchange stockExchange,
            final Integer years) {

        String queryUrl = new String(BASE_URL_QUOTATION_HISTORY);

        if (stockExchange == StockExchange.LSE) {
            throw new Error("The DAO for TheGlobeAndMail does not provide historical quotations for the exchange: "
                    + stockExchange);
        }

        queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, symbol);
        queryUrl = queryUrl.replace(PLACEHOLDER_EXCHANGE, this.getExchangeForQueryURLHistory(stockExchange));
        queryUrl = queryUrl.replace(PLACEHOLDER_DAYS, this.getDaysForQueryURLHistory(years));

        return queryUrl;
    }

    /**
     * Gets the stock exchange for construction of the query URL for current quotations.
     *
     * @param instrument The Instrument.
     * @return The stock exchange as used in the URL for current Quotation.
     */
    private String getExchangeForQueryURLCurrent(final Instrument instrument) {
        switch (instrument.getStockExchange()) {
        case NYSE:
            return "-N";
        case NDQ:
            return "-Q";
        case AMEX:
            return "-A";
        case OTC:
            return "";
        case TSX:
            return "-T";
        case TSXV:
            return "-X";
        case CSE:
            return "-CN";
        default:
            return "";
        }
    }

    /**
     * Gets the stock exchange for construction of the query URL for historical quotations.
     *
     * @param stockExchange The StockExchange the Instrument is listed at.
     * @return The stock exchange as used in the URL for historical quotations.
     */
    private String getExchangeForQueryURLHistory(final StockExchange stockExchange) {
        switch (stockExchange) {
        case NYSE:
            return "";
        default:
            return "";
        }
    }

    /**
     * Gets the number of days for construction of the query URL for historical quotations.
     *
     * @param years The number of years to be queried.
     * @return The number of days.
     */
    private String getDaysForQueryURLHistory(final Integer years) {
        final int tradingDaysPerYear = 252;
        final Integer requestedDays = years * tradingDaysPerYear;

        return requestedDays.toString();
    }
}
