package backend.dao.quotation.provider;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
     * Placeholder for the ID of the instrument used in a query URL.
     */
    private static final String PLACEHOLDER_INVESTING_ID = "{investing_id}";

    /**
     * Placeholder for the quotation interval used in a query URL.
     */
    private static final String PLACEHOLDER_INTERVAL = "{interval}";

    /**
     * Placeholder for the URL used in a cURL command.
     */
    private static final String PLACEHOLDER_URL = "{URL}";

    /**
     * URL to quote investing.com: Current quotation.
     */
    private static final String BASE_URL_CURRENT_QUOTATION = "https://api.investing.com/api/financialdata/"
            + PLACEHOLDER_INVESTING_ID + "/historical/chart/?interval=" + PLACEHOLDER_INTERVAL + "&pointscount=60";

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

    }

    /**
     * Gets the current Quotation of the given Instrument.
     */
    @Override
    public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
        String command = this.getCurlCommandCurrentQuotation(instrument);
        Quotation quotation;
        Process process = null;
        final InputStream resultStream;
        String jsonResult;

        try {
            process = Runtime.getRuntime().exec(command);
            resultStream = process.getInputStream();

            jsonResult = IOUtils.toString(resultStream, StandardCharsets.UTF_8);
            quotation = this.convertJSONtoCurrentQuotation(jsonResult, instrument);
        } finally {
            if (process != null) {
                process.destroy();
            }
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
     * Converts a String containing Quotation data into a Quotation with the most recent data.
     *
     * @param jsonString A JSON string containing multiple quotations.
     * @param instrument The Instrument the quotation data are related to.
     * @return The most recent Quotation.
     * @throws JsonMappingException    JSON Mapping failed.
     * @throws JsonProcessingException JSON processing failed.
     */
    @SuppressWarnings("unchecked")
    protected Quotation convertJSONtoCurrentQuotation(final String jsonString, final Instrument instrument)
            throws JsonMappingException, JsonProcessingException {

        String price;
        String volume;
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> map;
        ArrayList<ArrayList<Object>> quotations;
        ArrayList<Object> mostRecentQuotation;
        Quotation quotation = new Quotation();
        final int indexOpen = 1;
        final int indexHigh = 2;
        final int indexLow = 3;
        final int indexClose = 4;
        final int indexVolume = 5;

        quotation.setCurrency(this.getCurrencyForStockExchange(instrument.getStockExchange()));

        // Get the data of the most recent Quotation.
        map = mapper.readValue(jsonString, Map.class);
        quotations = (ArrayList<ArrayList<Object>>) map.get("data");
        mostRecentQuotation = quotations.get(quotations.size() - 1);

        // Convert raw data to Quotation object.
        quotation.setDate(new Date((long) mostRecentQuotation.get(0)));

        price = mostRecentQuotation.get(indexOpen).toString();
        quotation.setOpen(new BigDecimal(price));
        price = mostRecentQuotation.get(indexHigh).toString();
        quotation.setHigh(new BigDecimal(price));
        price = mostRecentQuotation.get(indexLow).toString();
        quotation.setLow(new BigDecimal(price));
        price = mostRecentQuotation.get(indexClose).toString();
        quotation.setClose(new BigDecimal(price));

        volume = mostRecentQuotation.get(indexVolume).toString();
        quotation.setVolume(Long.parseLong(volume));

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

        queryUrl = queryUrl.replace(PLACEHOLDER_INVESTING_ID, instrument.getCompanyPathInvestingCom());
        queryUrl = queryUrl.replace(PLACEHOLDER_INTERVAL, this.getInterval(instrument));

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
     * Determines the query interval based on the stock exchange of the given Instrument.
     *
     * @param instrument The Instrument.
     * @return The query interval.
     */
    private String getInterval(final Instrument instrument) {
        switch (instrument.getStockExchange()) {
        case NYSE:
        case NDQ:
        case AMEX:
        case TSX:
        case LSE:
            return "PT1M";
        case OTC:
        case TSXV:
        case CSE:
            return "PT5M";
        default:
            return "";
        }
    }
}
