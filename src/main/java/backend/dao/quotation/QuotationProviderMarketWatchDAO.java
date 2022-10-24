package backend.dao.quotation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

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
			"%2023:59:59&daterange=d30&frequency=p1d&csvdownload=true&downloadpartial=false&newdates=false&countrycode=" + PLACEHOLDER_COUNTRY_CODE;
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
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
		queryUrl = queryUrl.replace(PLACEHOLDER_COUNTRY_CODE, this.getCountryCode(stockExchange));
		
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
		 * if the current day is a Saturday, Sunday or Monday.
		 */
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -2);
		else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
			calendar.add(Calendar.DAY_OF_MONTH, -3);
		
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
	 * Provides the country code for the given StockExchange.
	 * 
	 * @param stockExchange The StockExchange.
	 * @return The country code.
	 */
	protected String getCountryCode(final StockExchange stockExchange) {
		String countryCode = "";
		
		switch(stockExchange) {
			case TSX:
			case TSXV:
				countryCode = "CA";
				break;
		default:
			break;
		}
		
		return countryCode;
	}
}
