package backend.dao.quotation.provider;

import java.io.IOException;

import backend.model.Currency;
import backend.model.StockExchange;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Abstract base class of the different QuotationProviderDAO implementations. This class provides some methods that are
 * mutually used by the concrete DAOs.
 *
 * @author Michael
 */
public abstract class AbstractQuotationProviderDAO {
    /**
     * Gets the Currency for the given StockExchange.
     *
     * @param stockExchange The StockExchange.
     * @return The Currency of the given StockExchange.
     */
    protected Currency getCurrencyForStockExchange(final StockExchange stockExchange) {
        switch (stockExchange) {
        case TSX:
        case TSXV:
        case CSE:
            return Currency.CAD;
        case NYSE:
        case NDQ:
        case AMEX:
            return Currency.USD;
        case LSE:
            return Currency.GBP;
        default:
            break;
        }

        return null;
    }

    /**
     * Gets the current quotation data from the given URL as JSON String.
     *
     * @param queryUrl     The query URL.
     * @param okHttpClient The HTML client performing the request.
     * @return The quotation data as JSON string.
     * @throws Exception Quotation data determination failed.
     */
    protected String getCurrentQuotationJSON(final String queryUrl, final OkHttpClient okHttpClient) throws Exception {
        Request request = new Request.Builder().url(queryUrl).header("Connection", "close").build();
        Response response;
        String jsonResult;

        try {
            response = okHttpClient.newCall(request).execute();
            jsonResult = response.body().string();
            response.close();
        } catch (IOException e) {
            throw new Exception(e);
        }

        return jsonResult;
    }
}
