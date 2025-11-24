package backend.dao.quotation.provider;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.opencsv.CSVReader;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
     * The HTTP client used for data queries.
     */
    private OkHttpClient httpClient;

    /**
     * Initializes the QuotationProviderGlobeAndMailDAO.
     */
    public QuotationProviderGlobeAndMailDAO() {
        this.disableHtmlUnitLogging();
    }

    /**
     * Initializes the QuotationProviderGlobeAndMailDAO.
     *
     * @param httpClient The HTTP client used for data queries.
     */
    public QuotationProviderGlobeAndMailDAO(final OkHttpClient httpClient) {
        this.disableHtmlUnitLogging();
        this.httpClient = httpClient;
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
    public List<Quotation> getQuotationHistory(final Instrument instrument, final Integer years) throws Exception {
        String csvQuotationHistory = this.getQuotationHistoryCSVFromGlobeAndMail(instrument.getSymbol(),
                instrument.getStockExchange(), years);

        if ("".equals(csvQuotationHistory)) {
            throw new Exception(
                    MessageFormat.format("The server returned empty CSV data for symbol {0}.", instrument.getSymbol()));
        }

        List<Quotation> quotationHistory = this.convertCSVToQuotations(csvQuotationHistory,
                instrument.getStockExchange());

        return quotationHistory;
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
     * Converts a CSV string containing the quotation history into a List of Quotation objects.
     *
     * @param quotationHistoryAsCSV The quotation history as CSV String.
     * @param stockExchange         The StockExchange at which the instrument is traded.
     * @return A List of Quotation objects.
     * @throws Exception Quotation conversion failed.
     */
    protected List<Quotation> convertCSVToQuotations(final String quotationHistoryAsCSV,
            final StockExchange stockExchange) throws Exception {

        StringReader stringReader = new StringReader(quotationHistoryAsCSV);
        CSVReader csvReader = new CSVReader(stringReader);
        Iterator<String[]> csvLineIterator = csvReader.iterator();
        List<Quotation> quotations = new ArrayList<>();
        Currency currency = this.getCurrencyForStockExchange(stockExchange);
        Quotation quotation;

        while (csvLineIterator.hasNext()) {
            quotation = this.getQuotationFromCsvLine(csvLineIterator.next(), currency);
            quotation.setCurrency(currency);
            quotations.add(quotation);
        }

        csvReader.close();

        return quotations;
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
            throw new Exception("The DAO for TheGlobeAndMail does not provide current quotations for the exchange: "
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
     * @throws Exception URL could not be created.
     */
    protected String getQueryUrlQuotationHistory(final String symbol, final StockExchange stockExchange,
            final Integer years) throws Exception {

        String queryUrl = new String(BASE_URL_QUOTATION_HISTORY);

        if (stockExchange == StockExchange.LSE) {
            throw new Exception("The DAO for TheGlobeAndMail does not provide historical quotations for the exchange: "
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
        case NDQ:
        case AMEX:
        case OTC:
            return "";
        case TSX:
            return ".TO";
        case TSXV:
            return ".VN";
        case CSE:
            return ".CN";
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

    /**
     * Gets the quotation history data from GlobeAndMail as CSV String.
     *
     * @param symbol        The symbol.
     * @param stockExchange The stock exchange.
     * @param years         The number of years to be queried.
     * @return The quotation history as CSV string.
     * @throws Exception Quotation history determination failed.
     */
    private String getQuotationHistoryCSVFromGlobeAndMail(final String symbol, final StockExchange stockExchange,
            final Integer years) throws Exception {

        Request request = new Request.Builder().url(this.getQueryUrlQuotationHistory(symbol, stockExchange, years))
                .header("Connection", "close").build();
        Response response;
        String csvResult;

        try {
            response = this.httpClient.newCall(request).execute();
            csvResult = response.body().string();
            response.close();
        } catch (IOException e) {
            throw new Exception(e);
        }

        return csvResult;
    }

    /**
     * Returns a Quotation based on the content of the given CSV line string.
     *
     * @param lineContent A CSV line containing Quotation data.
     * @param currency    The Currency.
     * @return The Quotation.
     * @throws ParseException Error while trying to parse data.
     */
    private Quotation getQuotationFromCsvLine(final String[] lineContent, final Currency currency)
            throws ParseException {
        Quotation quotation = new Quotation();
        final int indexDate = 1;
        final int indexOpen = 2;
        final int indexHigh = 3;
        final int indexLow = 4;
        final int indexClose = 5;
        final int indexVolume = 6;

        quotation.setDate(this.getDate(lineContent[indexDate]));
        quotation.setOpen(this.getPrice(lineContent[indexOpen], currency));
        quotation.setHigh(this.getPrice(lineContent[indexHigh], currency));
        quotation.setLow(this.getPrice(lineContent[indexLow], currency));
        quotation.setClose(this.getPrice(lineContent[indexClose], currency));
        quotation.setVolume(this.getVolume(lineContent[indexVolume]));

        return quotation;
    }

    /**
     * Gets the date of the given CSV cell value.
     *
     * @param dateCellValue The value of the date cell from the CSV file.
     * @return The date.
     * @throws ParseException Error while trying to parse date.
     */
    private Date getDate(final String dateCellValue) throws ParseException {
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        date = dateFormat.parse(dateCellValue);

        return date;
    }

    /**
     * Gets the price of the given CSV cell value.
     *
     * @param priceCellValue The value of the price cell from the CSV file.
     * @param currency       The currency.
     * @return The price.
     * @throws ParseException Error while trying to parse price data.
     */
    private BigDecimal getPrice(final String priceCellValue, final Currency currency) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        BigDecimal priceResult;
        Number priceNumber = 0;

        priceNumber = numberFormat.parse(priceCellValue);
        priceResult = new BigDecimal(priceNumber.floatValue());
        priceResult = priceResult.setScale(2, RoundingMode.HALF_UP);

        return priceResult;
    }

    /**
     * Gets the volume of the given CSV cell value.
     *
     * @param volumeCellValue The value of the volume cell from the CSV file.
     * @return The volume.
     */
    private long getVolume(final String volumeCellValue) {
        return Long.valueOf(volumeCellValue);
    }
}
