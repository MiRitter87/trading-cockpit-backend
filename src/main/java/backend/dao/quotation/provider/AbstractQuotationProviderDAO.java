package backend.dao.quotation.provider;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

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
        case OTC:
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

    /**
     * Disables logging of the HTMLUnit sub-package "com.gargoylesoftware.htmlunit.html".
     * <p>
     *
     * HTMLUnit fails to load iframes containing tracking URLS with the message:<br>
     * "IOException when getting content for iframe: url=[https://www.googletagmanager.com/ns.html?id=GTM-TL4VHVZ]"
     *
     * Those errors do not compromise the function of the application. They only fill log files unnecessarily. Therefore
     * the log messages from the defined package can be disabled using this method.
     */
    protected void disableHtmlUnitLogging() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit.html").setLevel(Level.OFF);
    }

    /**
     * Initializes and provides a Calendar for the query of historical quotations.
     *
     * @param yearOffset The offset allows for definition of the year. An offset of -1 subtracts 1 from the current
     *                   year.
     * @return A Calendar.
     */
    protected Calendar getCalendarForHistory(final int yearOffset) {
        Calendar calendar = Calendar.getInstance();
        final int oneDayBefore = -1;
        final int twoDaysBefore = -2;
        final int threeDaysBefore = -3;

        calendar.setTime(new Date());

        /*
         * The MarketWatch CSV API and the investing.com API request only supports the definition of a start and end
         * date. A query of a full year of data regardless of the current date is not supported. Therefore in order to
         * get the full 252 trading days of a year, the start and end date has to be set to the last Friday, if the
         * current day is a Sunday or Monday. The API only provides data after the close of the trading day. Therefore
         * always take at least the date of the previous day for the query.
         */
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, twoDaysBefore);
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, threeDaysBefore);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, oneDayBefore);
        }

        calendar.add(Calendar.YEAR, yearOffset);

        return calendar;
    }
}
