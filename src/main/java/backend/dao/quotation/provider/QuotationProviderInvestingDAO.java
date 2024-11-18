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
 * Provides access to quotation data using the investing.com website.
 *
 * @author Michael
 */
public class QuotationProviderInvestingDAO extends AbstractQuotationProviderDAO implements QuotationProviderDAO {
    /**
     * Placeholder for the company used in a query URL.
     */
    private static final String PLACEHOLDER_COMPANY = "{company}";

    /**
     * Placeholder for the URL used in a cURL command.
     */
    private static final String PLACEHOLDER_URL = "{URL}";

    /**
     * URL to quote investing.com: Current quotation.
     */
    private static final String BASE_URL_CURRENT_QUOTATION = "https://api.investing.com/api/financialdata/"
            + PLACEHOLDER_COMPANY + "/historical/chart/?interval=PT1M&pointscount=60\\";

    /**
     * The cURL command used to query the current Quotation.
     */
    private static final String CURL_CURRENT_QUOTATION = "curl \"" + PLACEHOLDER_URL + "\" --compressed "
            + "-H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0\" "
            + "-H \"Accept: */*\" -H \"Accept-Language: de,en-US;q=0.7,en;q=0.3\" "
            + "-H \"Accept-Encoding: gzip, deflate\" -H \"Referer: https://www.investing.com/\" "
            + "-H \"content-type: application/json\" -H \"domain-id: www\" "
            + "-H \"Origin: https://www.investing.com\" -H \"DNT: 1\" -H \"Sec-GPC: 1\" "
            + "-H \"Connection: keep-alive\" -H \"Sec-Fetch-Dest: empty\" -H \"Sec-Fetch-Mode: cors\" "
            + "-H \"Sec-Fetch-Site: same-site\" -H \"Priority: u=4\" -H \"TE: trailers\"";

    /**
     * Initializes the QuotationProviderInvestingDAO.
     */
    public QuotationProviderInvestingDAO() {
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
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

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
        Quotation quotation;

        quotation = this.getQuotationUsingSpans(htmlPage, instrument);

        if (quotation == null) {
            throw new Exception("The price could not be determined.");
        }

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

        if (instrument.getCompanyPathInvestingCom() == null || "".equals(instrument.getCompanyPathInvestingCom())) {
            throw new Exception("Query URL for investing.com could not be created "
                    + "because attribute 'companyPathInvestingCom' is not defined.");
        }

        queryUrl = queryUrl.replace(PLACEHOLDER_COMPANY, instrument.getCompanyPathInvestingCom());

        return queryUrl;
    }

    /**
     * Gets the cURL command for retrieval of the current Quotation.
     *
     * @param instrument The Instrument for which the command is determined.
     * @return The cUrl command.
     * @throws Exception Command could not be created.
     */
    private String getCurlCommandCurrentQuotation(final Instrument instrument) throws Exception {
        final String queryUrl = this.getQueryUrlCurrentQuotation(instrument);
        String command = new String(CURL_CURRENT_QUOTATION);

        command = command.replace(PLACEHOLDER_URL, queryUrl);

        return command;
    }

    /**
     * Extract Quotation data from HtmlPage using 'span' element.
     *
     * @param htmlPage   The HTML page containing the Quotation information.
     * @param instrument The Instrument for which Quotation data are extracted.
     * @return The current Quotation.
     */
    private Quotation getQuotationUsingSpans(final HtmlPage htmlPage, final Instrument instrument) {
        Quotation quotation = null;
        String currentPrice = "";

        final List<DomElement> spans = htmlPage.getElementsByTagName("span");
        for (DomElement element : spans) {
            if (element.getAttribute("data-test").equals("instrument-price-last")
                    && element.getAttribute("class").equals("text-2xl")) {
                quotation = new Quotation();
                quotation.setCurrency(this.getCurrencyForStockExchange(instrument.getStockExchange()));
                currentPrice = element.getFirstChild().asNormalizedText();
                quotation.setClose(new BigDecimal(currentPrice));
            }
        }

        return quotation;
    }
}
