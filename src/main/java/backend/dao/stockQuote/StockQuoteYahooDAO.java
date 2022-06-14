package backend.dao.stockQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.stockQuote.StockQuote;

/**
 * Retrieves stock data using the finance API of Yahoo.
 * 
 * @author Michael
 *
 */
public class StockQuoteYahooDAO implements StockQuoteDAO {
	/**
	 * URL to quote API of Yahoo finance.
	 */
	private static final String BASE_URL = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=";
	
	/**
	 * The HTTP client used for data queries.
	 */
	private HttpClient httpClient;
	
	
	/**
	 * Default constructor.
	 */
	public StockQuoteYahooDAO() {
		
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param httpClient The HTTP client used for data queries.
	 */
	public StockQuoteYahooDAO(final HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	
	@Override
	public StockQuote getStockQuote(String symbol, StockExchange stockExchange) throws Exception {
		String jsonStockQuote = this.getStockQuoteJSONFromYahoo(symbol, stockExchange);
		StockQuote stockQuote = this.convertJSONToStockQuote(jsonStockQuote);
		
		return stockQuote;
	}
	
	
	/**
	 * Gets the stock quote data from Yahoo finance as JSON String.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @return The stock quote as JSON string.
	 * @throws Exception Stock quote determination failed.
	 */
	protected String getStockQuoteJSONFromYahoo(final String symbol, final StockExchange stockExchange) throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.getQueryUrl(symbol, stockExchange))).build();
		HttpResponse<String> response;
		
		try {
			response = this.httpClient.send(request, BodyHandlers.ofString());
		} catch (IOException e) {
			throw new Exception(e);
		} catch (InterruptedException e) {
			throw new Exception(e);
		}
		
		return response.body();
	}
	
	
	/**
	 * Converts the stock quote from Yahoo provided as JSON String to a StockQuote object.
	 * 
	 * @param stockQuoteAsJSON The quote data as JSON String.
	 * @return The StockQuote.
	 * @throws Exception Stock quote conversion failed.
	 */
	protected StockQuote convertJSONToStockQuote(final String stockQuoteAsJSON) throws Exception {
		StockQuote stockQuote = new StockQuote();
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?> map;
		LinkedHashMap<?, ?> quoteResponse;
		ArrayList<?> result;
		LinkedHashMap<?, ?> resultAttributes;
		
		try {
			map = mapper.readValue(stockQuoteAsJSON, Map.class);
			quoteResponse = (LinkedHashMap<?, ?>) map.get("quoteResponse");
			result = (ArrayList<?>) quoteResponse.get("result");
			resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			
			stockQuote.setSymbol(this.getSymbol((String) resultAttributes.get("symbol")));
			stockQuote.setStockExchange(this.getExchange((String) resultAttributes.get("exchange")));
			stockQuote.setPrice(this.getPrice((double) resultAttributes.get("regularMarketPrice")));
			stockQuote.setCurrency(this.getCurrency((String) resultAttributes.get("financialCurrency")));
		} catch (JsonMappingException e) {
			throw new Exception(e);
		} catch (JsonProcessingException e) {
			throw new Exception(e);
		}
		
		return stockQuote;
	}

	
	/**
	 * Gets the exchange as used by the backend based on the exchange provided by Yahoo finance.
	 * 
	 * @param apiExchange The exchange as provided by Yahoo finance.
	 * @return The exchange as used by the backend
	 */
	protected StockExchange getExchange(String apiExchange) {
		switch(apiExchange) {
			case "VAN":
				return StockExchange.TSXV;
			case "TOR":
				return StockExchange.TSX;
			case "NYQ":	//NYSE
			case "NMS":	//Nasdaq
				return StockExchange.NYSE;
			default:
				return null;
		}
	}
	
	
	/**
	 * Gets the symbol from the Yahoo finance API.
	 * 
	 * @param apiSymbol The symbol as provided by Yahoo finance.
	 * @return Ther symbol as used by the backend.
	 */
	protected String getSymbol(String apiSymbol) {
		return apiSymbol.split("\\.")[0];
	}
	
	
	/**
	 * Gets the price from the Yahoo finance API.
	 * 
	 * @param apiPrice The price as provided by Yahoo finance.
	 * @return The price as used by the backend
	 */
	protected BigDecimal getPrice(double apiPrice) {
		return BigDecimal.valueOf(apiPrice);
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
	 * Gets the query URL for the given symbol and stock exchange.
	 * 
	 * @param symbol The symbol to be queried.
	 * @param stockExchange The stock exchange where the symbol is listed.
	 * @return The query URL.
	 */
	protected String getQueryUrl(final String symbol, final StockExchange stockExchange) {
		StringBuilder urlBuilder = new StringBuilder(BASE_URL);
		
		urlBuilder.append(symbol);
		urlBuilder.append(this.getExchangeForQueryURL(stockExchange));
		
		return urlBuilder.toString();
	}
}
