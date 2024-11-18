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
     * URL to quote investing.com: Current quotation.
     */
    private static final String BASE_URL_CURRENT_QUOTATION = "https://api.investing.com/api/financialdata/"
            + PLACEHOLDER_COMPANY + "/historical/chart/?interval=PT1M&pointscount=60\\";

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

//    /**
//     * Gets the
//     *
//     * @param instrument
//     * @return
//     * @throws Exception
//     */
//    protected String getCurlCommandCurrentQuotation(final Instrument instrument) throws Exception {
//
//    }

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
