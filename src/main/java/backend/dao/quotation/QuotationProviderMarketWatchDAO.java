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
		queryUrl = queryUrl.replace(PLACEHOLDER_START_DATE, this.getStartDateForHistory());
		queryUrl = queryUrl.replace(PLACEHOLDER_END_DATE, this.getEndDateForHistory());
		queryUrl = queryUrl.replace(PLACEHOLDER_COUNTRY_CODE, this.getCountryCode(stockExchange));
		
		return queryUrl;
	}
	
	
	/**
	 * Determines the start date for the quotation history.
	 * 
	 * @return The start date in the format mm/dd/yyyy.
	 */
	protected String getStartDateForHistory() {
		StringBuilder stringBuilder = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		int day, month, year;
		
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, -1);
		
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH) + 1; //Add 1 because the first month of the year is returned as 0.
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
	 * Determines the end date for the quotation history.
	 * 
	 * @return The start date in the format mm/dd/yyyy.
	 */
	protected String getEndDateForHistory() {
		StringBuilder stringBuilder = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		int day, month, year;
		
		calendar.setTime(new Date());
		
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH) + 1; //Add 1 because the first month of the year is returned as 0.
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
