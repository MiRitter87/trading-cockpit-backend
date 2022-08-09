package backend.dao.quotation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Quotation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Provides access to quotation data using the finance API of Yahoo.
 * 
 * @author Michael
 */
public class QuotationYahooDAO implements QuotationDAO {
	/**
	 * Placeholder for the symbol used in a query URL.
	 */
	private static final String PLACEHOLDER_SYMBOL = "{symbol}";
	
	/**
	 * Placeholder for the number of years used in a query URL.
	 */
	private static final String PLACEHOLDER_YEARS = "{years}";
	
	/**
	 * URL to quote API of Yahoo finance: Historical quotations.
	 */
	private static final String BASE_URL_QUOTATION_HISTORY = "https://query1.finance.yahoo.com/v7/finance/chart/"
			+ PLACEHOLDER_SYMBOL + "?range=" + PLACEHOLDER_YEARS + "y&interval=1d&indicators=quote&includeTimestamps=true";
	
	/**
	 * The HTTP client used for data queries.
	 */
	private OkHttpClient httpClient;
	
	
	/**
	 * Default constructor.
	 */
	public QuotationYahooDAO() {
		
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param httpClient The HTTP client used for data queries.
	 */
	public QuotationYahooDAO(final OkHttpClient  httpClient) {
		this.httpClient = httpClient;
	}
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		// TODO The functionality of the StockQuoteYahooDAO, method "getStockQuote" can be integrated into this method.
		return null;
	}

	
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		String jsonQuotationHistory = this.getQuotationHistoryJSONFromYahoo(symbol, stockExchange, years);
		List<Quotation> quotationHistory = this.convertJSONToQuotations(jsonQuotationHistory);
		
		return quotationHistory;
	}
	
	
	/**
	 * Gets the quotation history data from Yahoo finance as JSON String.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @param years The number of years to be queried.
	 * @return The quotation history as JSON string.
	 * @throws Exception Quotation history determination failed.
	 */
	protected String getQuotationHistoryJSONFromYahoo(final String symbol, final StockExchange stockExchange, final Integer years) throws Exception {
		Request request = new Request.Builder()
				.url(this.getQueryUrlQuotationHistory(symbol, stockExchange, years))
				.header("Connection", "close")
				.build();
		Response response;
		String jsonResult;
		
		try {
			response = this.httpClient.newCall(request).execute();
			jsonResult = response.body().string();
			response.close();
		} catch (IOException e) {
			throw new Exception(e);
		}
		
		return jsonResult;
	}
	
	
	/**
	 * Converts a JSON string containing the quotation history into a List of Quotation objects.
	 * 
	 * @param quotationHistoryAsJSON The quotation history as JSON String.
	 * @return A List of Quotation objects.
	 * @throws Exception Quotation conversion failed.
	 */
	protected List<Quotation> convertJSONToQuotations(final String quotationHistoryAsJSON) throws Exception {
		List<Quotation> quotationHistory = new ArrayList<>();
		Quotation quotation;
		Map<?, ?> map;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			map = mapper.readValue(quotationHistoryAsJSON, Map.class);
			LinkedHashMap<?, ?> quoteResponse = (LinkedHashMap<?, ?>) map.get("chart");
			ArrayList<?> result = (ArrayList<?>) quoteResponse.get("result");
			LinkedHashMap<?, ?> resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			ArrayList<?> timestampData = (ArrayList<?>) resultAttributes.get("timestamp");
			LinkedHashMap<?, ?> metaAttributes = (LinkedHashMap<?, ?>) resultAttributes.get("meta");
			LinkedHashMap<?, ?> indicators = (LinkedHashMap<?, ?>) resultAttributes.get("indicators");
			ArrayList<?> quote = (ArrayList<?>) indicators.get("quote");
			LinkedHashMap<?, ?> quoteAttributes = (LinkedHashMap<?, ?>) quote.get(0);
			ArrayList<?> volumeData = (ArrayList<?>) quoteAttributes.get("volume");
			ArrayList<?> adjClose = (ArrayList<?>) indicators.get("adjclose");
			LinkedHashMap<?, ?> adjCloseAttributes = (LinkedHashMap<?, ?>) adjClose.get(0);
			ArrayList<?> adjCloseData = (ArrayList<?>) adjCloseAttributes.get("adjclose");
			
			for(int i = timestampData.size(); i>0; i--) {
				quotation = new Quotation();
				quotation.setDate(this.getDate(timestampData.get(i-1)));
				quotation.setCurrency(this.getCurrency((String) metaAttributes.get("currency")));
				quotation.setVolume(this.getVolumeFromQuotationHistoryResponse(volumeData, i-1));
				quotation.setPrice(this.getAdjustedCloseFromQuotationHistoryResponse(adjCloseData, i-1));
				quotationHistory.add(quotation);
			}
		}
		catch (JsonMappingException e) {
			throw new Exception(e);
		} 
		catch (JsonProcessingException e) {
			throw new Exception(e);
		}
		
		return quotationHistory;
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
		StringBuilder symbolForUrl = new StringBuilder();
		String queryUrl = new String(BASE_URL_QUOTATION_HISTORY);
		
		symbolForUrl.append(symbol);
		symbolForUrl.append(this.getExchangeForQueryURL(stockExchange));
		
		queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, symbolForUrl.toString());
		queryUrl = queryUrl.replace(PLACEHOLDER_YEARS, years.toString());
		
		return queryUrl;
	}
	
	
	/**
	 * Gets the stock exchange for construction of the query URL.
	 * 
	 * @param stockExchange The stock exchange of the internal data model.
	 * @return The stock exchange as used in the query URL.
	 */
	protected String getExchangeForQueryURL(final StockExchange stockExchange) {
		StringBuilder stockExchangeBuilder = new StringBuilder("");
		
		switch(stockExchange) {
			case TSX:
				stockExchangeBuilder.append(".");
				stockExchangeBuilder.append("TO");
				return stockExchangeBuilder.toString();
			case TSXV:
				stockExchangeBuilder.append(".");
				stockExchangeBuilder.append("V");
				return stockExchangeBuilder.toString();
			case NYSE:
			default:
				return stockExchangeBuilder.toString();
		}
	}
	
	
	/**
	 * Provides a date based on the given timestamp.
	 * 
	 * @param timestamp Integer value representing date in seconds since 01.01.1970.
	 * @return A date object.
	 */
	protected Date getDate(Object timestamp) {
		long timestampInSeconds = Long.valueOf((Integer)timestamp);
		long timestampInMilliseconds = timestampInSeconds * 1000;
		
		return new Date(timestampInMilliseconds);
	}
	
	
	/**
	 * Gets the currency from the Yahoo finance API.
	 * 
	 * @param apiCurrency The currency as provided by Yahoo finance.
	 * @return The currency as used by the backend.
	 */
	protected Currency getCurrency(String apiCurrency) {
		switch(apiCurrency) {
			case "USD":
				return Currency.USD;
			case "CAD":
				return Currency.CAD;
			default:
				return null;
		}
	}
	
	
	/**
	 * Gets the volume data from the Yahoo finance API.
	 * 
	 * @param volume A list of historical volume data.
	 * @param index The index at which the volume data are to be extracted.
	 * @return The volume.
	 */
	protected long getVolumeFromQuotationHistoryResponse(final ArrayList<?> volume, final int index) {
		return Long.valueOf((Integer)volume.get(index));
	}
	
	
	/**
	 * Gets the adjusted closing price from the Yahoo finance API.
	 * 
	 * @param adjustedClose A list of historical adjusted closing prices.
	 * @param index The index at which the adjusted closing price is to be extracted.
	 * @return The adjusted closing price.
	 */
	protected BigDecimal getAdjustedCloseFromQuotationHistoryResponse(final ArrayList<?> adjustedClose, final int index) {
		double adjustedCloseRaw = (double) adjustedClose.get(index);
		BigDecimal adjustedClosingPrice = BigDecimal.valueOf(adjustedCloseRaw);
		
		adjustedClosingPrice = adjustedClosingPrice.setScale(2, RoundingMode.HALF_UP);
		
		
		return adjustedClosingPrice;
	}


	@Override
	public void insertQuotations(List<Quotation> quotations) throws Exception {
		throw new Exception("Operation not supported.");		
	}


	@Override
	public void deleteQuotations(List<Quotation> quotations) throws Exception {
		throw new Exception("Operation not supported.");
	}


	@Override
	public Quotation getQuotation(Integer id) throws Exception {
		throw new Exception("Operation not supported.");
	}


	@Override
	public List<Quotation> getQuotationsOfInstrument(Integer instrumentId) throws Exception {
		throw new Exception("Operation not supported.");
	}
}
