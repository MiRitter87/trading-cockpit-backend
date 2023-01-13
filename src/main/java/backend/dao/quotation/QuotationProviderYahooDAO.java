package backend.dao.quotation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Provides access to quotation data using the finance API of Yahoo.
 * 
 * @author Michael
 */
public class QuotationProviderYahooDAO implements QuotationProviderDAO {
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
	 * URL to quote API of Yahoo finance: Current quotation.
	 */
	private static final String BASE_URL_CURRENT_QUOTATION = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=";
	
	/**
	 * The HTTP client used for data queries.
	 */
	private OkHttpClient httpClient;
	
	/**
	 * Application logging.
	 */
	public static final Logger logger = LogManager.getLogger(QuotationProviderYahooDAO.class);
	
	
	/**
	 * Default constructor.
	 */
	public QuotationProviderYahooDAO() {
		
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param httpClient The HTTP client used for data queries.
	 */
	public QuotationProviderYahooDAO(final OkHttpClient  httpClient) {
		this.httpClient = httpClient;
	}
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		String jsonQuotation = this.getCurrentQuotationJSONFromYahoo(symbol, stockExchange);
		Quotation quotation = this.convertJSONToQuotation(jsonQuotation);
		
		return quotation;
	}

	
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, InstrumentType instrumentType, 
			Integer years) throws Exception {
		
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
		String symbol = "";
		boolean historyIsIncomplete = false;
		
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
			ArrayList<?> openData = (ArrayList<?>) quoteAttributes.get("open");
			ArrayList<?> highData = (ArrayList<?>) quoteAttributes.get("high");
			ArrayList<?> lowData = (ArrayList<?>) quoteAttributes.get("low");
			ArrayList<?> closeData = (ArrayList<?>) quoteAttributes.get("close");
			
			symbol = (String) metaAttributes.get("symbol");
			
			for(int i = timestampData.size(); i>0; i--) {
				try {
					quotation = new Quotation();
					quotation.setDate(this.getDate(timestampData.get(i-1)));
					quotation.setCurrency(this.getCurrency((String) metaAttributes.get("currency")));
					quotation.setVolume(this.getVolumeFromQuotationHistoryResponse(volumeData, i-1));
					quotation.setOpen(this.getPriceFromQuotationHistoryResponse(openData, i-1, quotation.getCurrency()));
					quotation.setHigh(this.getPriceFromQuotationHistoryResponse(highData, i-1, quotation.getCurrency()));
					quotation.setLow(this.getPriceFromQuotationHistoryResponse(lowData, i-1, quotation.getCurrency()));
					quotation.setClose(this.getPriceFromQuotationHistoryResponse(closeData, i-1, quotation.getCurrency()));	
					quotationHistory.add(quotation);
				}
				catch(Exception exception) {
					historyIsIncomplete = true;
					continue;
				}
			}
		}
		catch (JsonMappingException e) {
			throw new Exception(e);
		} 
		catch (JsonProcessingException e) {
			throw new Exception(e);
		}
		
		if(historyIsIncomplete) {
			logger.info(MessageFormat.format("The history of symbol {0} is incomplete. {1} quotations could be gathered.", 
					symbol, quotationHistory.size()));			
		}
		
		return quotationHistory;
	}
	
	
	/**
	 * Gets the current quotation data from Yahoo finance as JSON String.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @return The quotation data as JSON string.
	 * @throws Exception Quotation data determination failed.
	 */
	protected String getCurrentQuotationJSONFromYahoo(final String symbol, final StockExchange stockExchange) throws Exception {
		Request request = new Request.Builder()
				.url(this.getQueryUrlCurrentQuotation(symbol, stockExchange))
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
	 * Converts the quotation data from Yahoo provided as JSON String to a Quotation object.
	 * 
	 * @param quotationDataAsJSON The quotation data as JSON String.
	 * @return The Quotation.
	 * @throws Exception Quotation conversion failed.
	 */
	protected Quotation convertJSONToQuotation(final String quotationDataAsJSON) throws Exception {
		Quotation quotation = new Quotation();
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?> map;
		LinkedHashMap<?, ?> quoteResponse;
		ArrayList<?> result;
		LinkedHashMap<?, ?> resultAttributes;
		
		try {
			map = mapper.readValue(quotationDataAsJSON, Map.class);
			quoteResponse = (LinkedHashMap<?, ?>) map.get("quoteResponse");
			result = (ArrayList<?>) quoteResponse.get("result");
			resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			
			quotation.setCurrency(this.getCurrency((String) resultAttributes.get("currency")));
			quotation.setClose(this.getPrice((double) resultAttributes.get("regularMarketPrice"), quotation.getCurrency()));
		} catch (JsonMappingException e) {
			throw new Exception(e);
		} catch (JsonProcessingException e) {
			throw new Exception(e);
		}
		
		return quotation;
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
	 * Gets the query URL for the current quotation of the given symbol and stock exchange.
	 * 
	 * @param symbol The symbol to be queried.
	 * @param stockExchange The stock exchange where the symbol is listed.
	 * @return The query URL.
	 */
	protected String getQueryUrlCurrentQuotation(final String symbol, final StockExchange stockExchange) {
		StringBuilder urlBuilder = new StringBuilder(BASE_URL_CURRENT_QUOTATION);
		
		urlBuilder.append(symbol);
		urlBuilder.append(this.getExchangeForQueryURL(stockExchange));
		
		return urlBuilder.toString();
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
			case CSE:
				stockExchangeBuilder.append(".");
				stockExchangeBuilder.append("CN");
				return stockExchangeBuilder.toString();
			case LSE:
				stockExchangeBuilder.append(".");
				stockExchangeBuilder.append("L");
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
			case "GBp":
				return Currency.GBP;
			default:
				return null;
		}
	}
	
	
	/**
	 * Gets the price from the Yahoo finance API.
	 * 
	 * @param apiPrice The price as provided by Yahoo finance.
	 * @param currency The Currency of the price.
	 * @return The price as used by the backend
	 */
	protected BigDecimal getPrice(double apiPrice, final Currency currency) {
		BigDecimal price = BigDecimal.valueOf(apiPrice);
		
		//Yahoo provides prices in pence. To get prices in pounds, divide price in pence by 100.
		if(currency == Currency.GBP)
			price = price.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		
		return price;
	}
	
	
	/**
	 * Gets the volume data from the Yahoo finance API.
	 * 
	 * @param volume A list of historical volume data.
	 * @param index The index at which the volume data are to be extracted.
	 * @return The volume.
	 * @throws Exception Failed to read volume data.
	 */
	protected long getVolumeFromQuotationHistoryResponse(final ArrayList<?> volume, final int index) throws Exception {
		if(volume.get(index) == null)
			throw new Exception("Volume data contains null values.");
		
		return Long.valueOf((Integer)volume.get(index));
	}
	
	
	/**
	 * Gets a price from the Yahoo finance API.
	 * 
	 * @param prices A list of historical prices.
	 * @param index The index at which the price is to be extracted.
	 * @param currency The Currency of the price.
	 * @return The price.
	 * @throws Exception Failed to read price data.
	 */
	protected BigDecimal getPriceFromQuotationHistoryResponse(final ArrayList<?> prices, final int index, final Currency currency) throws Exception {
		double priceRaw;
		BigDecimal price;
		
		if(prices.get(index) == null)
			throw new Exception("Price data contains null values.");
		
		priceRaw = (double) prices.get(index);
		price = BigDecimal.valueOf(priceRaw);
		price = price.setScale(2, RoundingMode.HALF_UP);
		
		//Yahoo provides prices in pence. To get prices in pounds, divide price in pence by 100.
		if(currency == Currency.GBP)
			price = price.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		
		return price;
	}
}
