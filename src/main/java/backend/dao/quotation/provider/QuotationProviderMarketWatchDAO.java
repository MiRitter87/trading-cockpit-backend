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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.opencsv.CSVReaderHeaderAware;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Provides access to quotation data using the CSV download function of MarketWatch.
 *
 * @author Michael
 */
public class QuotationProviderMarketWatchDAO extends AbstractQuotationProviderDAO implements QuotationProviderDAO {
    /**
     * Placeholder for the symbol used in a query URL.
     */
    private static final String PLACEHOLDER_SYMBOL = "{symbol}";

    /**
     * Placeholder for the type used in a query URL.
     */
    private static final String PLACEHOLDER_TYPE = "{type}";

    /**
     * Placeholder for the start date used in a query URL. The date format is mm/dd/yyyy.
     */
    private static final String PLACEHOLDER_START_DATE = "{start_date}";

    /**
     * Placeholder for the end date used in a query URL. The date format is mm/dd/yyyy.
     */
    private static final String PLACEHOLDER_END_DATE = "{end_date}";

    /**
     * Placeholder for the country code used in a query URL.
     */
    private static final String PLACEHOLDER_COUNTRY_CODE = "{country_code}";

    /**
     * URL to CSV API of MarketWatch: Historical quotations.
     */
    private static final String BASE_URL_QUOTATION_HISTORY = "https://www.marketwatch.com/investing/" + PLACEHOLDER_TYPE
            + "/" + PLACEHOLDER_SYMBOL + "/downloaddatapartial?startdate=" + PLACEHOLDER_START_DATE
            + "%2000:00:00&enddate=" + PLACEHOLDER_END_DATE
            + "%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&downloadpartial=false&newdates=false"
            + PLACEHOLDER_COUNTRY_CODE;

    /**
     * The HTTP client used for data queries.
     */
    private OkHttpClient httpClient;

    /**
     * Default constructor.
     */
    public QuotationProviderMarketWatchDAO() {

    }

    /**
     * Constructor.
     *
     * @param httpClient The HTTP client used for data queries.
     */
    public QuotationProviderMarketWatchDAO(final OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Gets the current Quotation of the given Instrument. Method not supported by MarketWatch.
     */
    @Override
    public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
        throw new Exception("Method is not supported.");
    }

    /**
     * Gets the Quotation history.
     */
    @Override
    public List<Quotation> getQuotationHistory(final String symbol, final StockExchange stockExchange,
            final InstrumentType instrumentType, final Integer years) throws Exception {

        String csvQuotationHistory = this.getQuotationHistoryCSVFromMarketWatch(symbol, stockExchange, instrumentType,
                years);

        if ("".equals(csvQuotationHistory)) {
            throw new Exception(MessageFormat.format("The server returned empty CSV data for symbol {0}.", symbol));
        }

        List<Quotation> quotationHistory = this.convertCSVToQuotations(csvQuotationHistory, stockExchange);

        return quotationHistory;
    }

    /**
     * Gets the quotation history data from MarketWatch as CSV String.
     *
     * @param symbol         The symbol.
     * @param stockExchange  The stock exchange.
     * @param instrumentType The InstrumentType.
     * @param years          The number of years to be queried.
     * @return The quotation history as CSV string.
     * @throws Exception Quotation history determination failed.
     */
    protected String getQuotationHistoryCSVFromMarketWatch(final String symbol, final StockExchange stockExchange,
            final InstrumentType instrumentType, final Integer years) throws Exception {

        Request request = new Request.Builder()
                .url(this.getQueryUrlQuotationHistory(symbol, stockExchange, instrumentType, years))
                .header("Connection", "close")
                .header("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Accept-Language", "de,en-US;q=0.7,en;q=0.3").header("Accept-Encoding", "gzip, deflate, br")
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0")
                .build();
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
        CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(stringReader);
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
     * Gets the query URL for the quotation history of the given symbol and stock exchange.
     *
     * @param symbol         The symbol to be queried.
     * @param stockExchange  The stock exchange where the symbol is listed.
     * @param instrumentType The InstrumentType.
     * @param years          The number of years to be queried.
     * @return The query URL.
     */
    protected String getQueryUrlQuotationHistory(final String symbol, final StockExchange stockExchange,
            final InstrumentType instrumentType, final Integer years) {

        String queryUrl = new String(BASE_URL_QUOTATION_HISTORY);

        queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, symbol);
        queryUrl = queryUrl.replace(PLACEHOLDER_TYPE, this.getTypeParameter(instrumentType));
        queryUrl = queryUrl.replace(PLACEHOLDER_START_DATE, this.getDateForHistory(-1));
        queryUrl = queryUrl.replace(PLACEHOLDER_END_DATE, this.getDateForHistory(0));
        queryUrl = queryUrl.replace(PLACEHOLDER_COUNTRY_CODE, this.getCountryCodeParameter(stockExchange));

        return queryUrl;
    }

    /**
     * Determines the date for the quotation history.
     *
     * @param yearOffset The offset allows for definition of the year. An offset of -1 subtracts 1 from the current
     *                   year.
     * @return The date in the format mm/dd/yyyy.
     */
    protected String getDateForHistory(final int yearOffset) {
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = this.getCalendarForHistory(yearOffset);
        int day;
        int month;
        int year;
        final int doubleDigitNumber = 10;

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH) + 1; // Add 1 because the first month of the year is returned as 0 by the
                                                  // Calendar.
        year = calendar.get(Calendar.YEAR);

        // Add a leading zero if day or month is returned as single-digit number.
        if (month < doubleDigitNumber) {
            stringBuilder.append("0");
        }

        stringBuilder.append(month);
        stringBuilder.append("/");

        if (day < doubleDigitNumber) {
            stringBuilder.append("0");
        }

        stringBuilder.append(day);
        stringBuilder.append("/");
        stringBuilder.append(year);

        return stringBuilder.toString();
    }

    /**
     * Provides the country code URL parameter for the given StockExchange.
     *
     * @param stockExchange The StockExchange.
     * @return The country code URL parameter.
     */
    protected String getCountryCodeParameter(final StockExchange stockExchange) {
        String countryCode = "";

        switch (stockExchange) {
        case TSX:
        case TSXV:
        case CSE:
            countryCode = "&countrycode=CA";
            break;
        case LSE:
            countryCode = "&countrycode=UK";
            break;
        default:
            break;
        }

        return countryCode;
    }

    /**
     * Provides the type URL parameter for the given InstrumentType.
     *
     * @param instrumentType The InstrumentType
     * @return The type URL parameter.
     */
    protected String getTypeParameter(final InstrumentType instrumentType) {
        String type;

        switch (instrumentType) {
        case STOCK:
            type = InstrumentTypeMarketWatch.STOCK.toString();
            break;
        case ETF:
        case SECTOR:
        case IND_GROUP:
            type = InstrumentTypeMarketWatch.FUND.toString();
            break;
        case RATIO:
        default:
            type = "";
            break;
        }

        return type;
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
        final int indexDate = 0;
        final int indexOpen = 1;
        final int indexHigh = 2;
        final int indexLow = 3;
        final int indexClose = 4;
        final int indexVolume = 5;

        quotation.setDate(this.getDate(lineContent[indexDate]));
        quotation.setOpen(this.getPrice(lineContent[indexOpen], currency));
        quotation.setHigh(this.getPrice(lineContent[indexHigh], currency));
        quotation.setLow(this.getPrice(lineContent[indexLow], currency));
        quotation.setClose(this.getPrice(lineContent[indexClose], currency));
        quotation.setVolume(this.getVolume(lineContent[indexVolume]));

        return quotation;
    }

    /**
     * Gets the price of the given CSV cell value.
     *
     * @param priceCellValue The value of the price cell from the CSV file.
     * @param currency       The currency.
     * @return The price.
     * @throws ParseException Error while trying to parse price data.
     */
    protected BigDecimal getPrice(final String priceCellValue, final Currency currency) throws ParseException {
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
     * @throws ParseException Error while trying to parse volume data.
     */
    protected long getVolume(final String volumeCellValue) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        Number volume = numberFormat.parse(volumeCellValue);

        return volume.longValue();
    }

    /**
     * Gets the date of the given CSV cell value.
     *
     * @param dateCellValue The value of the date cell from the CSV file.
     * @return The date.
     * @throws ParseException Error while trying to parse date.
     */
    protected Date getDate(final String dateCellValue) throws ParseException {
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        date = dateFormat.parse(dateCellValue);

        return date;
    }

    /**
     * Initializes and provides a Calendar for the query of historical quotations.
     *
     * @param yearOffset The offset allows for definition of the year. An offset of -1 subtracts 1 from the current
     *                   year.
     * @return A Calendar.
     */
    private Calendar getCalendarForHistory(final int yearOffset) {
        Calendar calendar = Calendar.getInstance();
        final int oneDayBefore = -1;
        final int twoDaysBefore = -2;
        final int threeDaysBefore = -3;

        calendar.setTime(new Date());

        /*
         * The MarketWatch CSV API only supports the definition of a start and end date. A query of a full year of data
         * regardless of the current date is not supported. Therefore in order to get the full 252 trading days of a
         * year, the start and end date has to be set to the last Friday, if the current day is a Sunday or Monday. The
         * API only provides data after the close of the trading day. Therefore always take at least the date of the
         * previous day for the query.
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
