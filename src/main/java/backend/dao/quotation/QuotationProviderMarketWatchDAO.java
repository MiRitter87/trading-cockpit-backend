package backend.dao.quotation;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
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
public class QuotationProviderMarketWatchDAO implements QuotationProviderDAO {
	/**
	 * Placeholder for the symbol used in a query URL.
	 */
	private static final String PLACEHOLDER_SYMBOL = "{symbol}";	
	
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
	private static final String BASE_URL_QUOTATION_HISTORY = "https://www.marketwatch.com/investing/stock/" + PLACEHOLDER_SYMBOL + 
			"/downloaddatapartial?startdate=" + PLACEHOLDER_START_DATE + "%2000:00:00&enddate=" + PLACEHOLDER_END_DATE + 
			"%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&downloadpartial=false&newdates=false" + PLACEHOLDER_COUNTRY_CODE;

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
	public QuotationProviderMarketWatchDAO(final OkHttpClient  httpClient) {
		this.httpClient = httpClient;
	}
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		throw new Exception("Method is not supported.");
	}

	
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, InstrumentType instrumentType, 
			Integer years) throws Exception {
		
		String csvQuotationHistory = this.getQuotationHistoryCSVFromMarketWatch(symbol, stockExchange, years);
		
		if("".equals(csvQuotationHistory))
			throw new Exception(MessageFormat.format("The server returned empty CSV data for symbol {0}.", symbol));
		
		List<Quotation> quotationHistory = this.convertCSVToQuotations(csvQuotationHistory, stockExchange);
		
		return quotationHistory;
	}
	
	
	/**
	 * Gets the quotation history data from MarketWatch as CSV String.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @param years The number of years to be queried.
	 * @return The quotation history as CSV string.
	 * @throws Exception Quotation history determination failed.
	 */
	protected String getQuotationHistoryCSVFromMarketWatch(final String symbol, final StockExchange stockExchange, final Integer years) throws Exception {
		Request request = new Request.Builder()
				.url(this.getQueryUrlQuotationHistory(symbol, stockExchange, years))
				.header("Connection", "close")
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
	 * @param stockExchange The StockExchange at which the instrument is traded.
	 * @return A List of Quotation objects.
	 * @throws Exception Quotation conversion failed.
	 */
	protected List<Quotation> convertCSVToQuotations(final String quotationHistoryAsCSV, final StockExchange stockExchange) throws Exception {
		StringReader stringReader = new StringReader(quotationHistoryAsCSV);
		CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(stringReader);
		Iterator<String[]> csvLineIterator = csvReader.iterator();
		List<Quotation> quotations = new ArrayList<>();
		Currency currency = this.getCurrencyForStockExchange(stockExchange);
		Quotation quotation;
		
		while(csvLineIterator.hasNext()) {
			quotation = this.getQuotationFromCsvLine(csvLineIterator.next());
			quotation.setCurrency(currency);
			quotations.add(quotation);
		}
		
		csvReader.close();
		
		return quotations;
	}

	
	/**
	 * Gets the query URL for the quotation history of the given symbol and stock exchange.
	 * 
	 * @param symbol The symbol to be queried.
	 * @param stockExchange The stock exchange where the symbol is listed.
	 * @param years The number of years to be queried.
	 * @return The query URL.
	 */
	protected String getQueryUrlQuotationHistory(final String symbol, final StockExchange stockExchange, final Integer years) {
		String queryUrl = new String(BASE_URL_QUOTATION_HISTORY);
		
		queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, symbol);
		queryUrl = queryUrl.replace(PLACEHOLDER_START_DATE, this.getDateForHistory(-1));
		queryUrl = queryUrl.replace(PLACEHOLDER_END_DATE, this.getDateForHistory(0));
		queryUrl = queryUrl.replace(PLACEHOLDER_COUNTRY_CODE, this.getCountryCodeParameter(stockExchange));
		
		return queryUrl;
	}
	
	
	/**
	 * Determines the date for the quotation history.
	 * 
	 * @param The offset allows for definition of the year. An offset of -1 subtracts 1 from the current year.
	 * @return The date in the format mm/dd/yyyy.
	 */
	protected String getDateForHistory(final int yearOffset) {
		StringBuilder stringBuilder = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		int day, month, year;
		
		calendar.setTime(new Date());
		
		/*
		 * The MarketWatch CSV API only supports the definition of a start and end date.
		 * A query of a full year of data regardless of the current date is not supported.
		 * Therefore in order to get the full 252 trading days of a year, the start and end date has to be set to the last Friday,
		 * if the current day is a Sunday or Monday.
		 * The API only provides data after the close of the trading day. Therefore always take at least the date of the previous day for the query.
		 */
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -2);
		else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -3);
		else
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		calendar.add(Calendar.YEAR, yearOffset);
		
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH) + 1; //Add 1 because the first month of the year is returned as 0 by the Calendar.
		year = calendar.get(Calendar.YEAR);
		
		//Add a leading zero if day or month is returned as single-digit number.
		if(month < 10)
			stringBuilder.append("0");
		
		stringBuilder.append(month);
		stringBuilder.append("/");
		
		if(day < 10)
			stringBuilder.append("0");
		
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
		
		switch(stockExchange) {
			case TSX:
			case TSXV:
			case CSE:
				countryCode = "&countrycode=CA";
				break;
		default:
			break;
		}
		
		return countryCode;
	}
	
	
	/**
	 * Gets the Currency for the given StockExchange.
	 * 
	 * @param stockExchange The StockExchange.
	 * @return
	 */
	protected Currency getCurrencyForStockExchange(final StockExchange stockExchange) {
		switch(stockExchange) {
			case TSX:
			case TSXV:
			case CSE:
				return Currency.CAD;
			case NYSE:
				return Currency.USD;
			default:
				break;
		}
		
		return null;
	}
	
	
	/**
	 * Returns a Quotation based on the content of the given CSV line string.
	 * 
	 * @param lineContent A CSV line containing Quotation data.
	 * @return The Quotation.
	 * @throws ParseException Error while trying to parse data.
	 */
	protected Quotation getQuotationFromCsvLine(final String[] lineContent) throws ParseException {
		Quotation quotation = new Quotation();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        Number number = 0;
        		
		quotation.setDate(dateFormat.parse(lineContent[0]));
		quotation.setPrice(new BigDecimal(lineContent[4]));
		
		number = numberFormat.parse(lineContent[5]);
		quotation.setVolume(number.longValue());
		
		return quotation;
	}
}
