package backend.dao.quotation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the API of CNBC.
 * 
 * @author Michael
 */
public class QuotationProviderCNBCDAO implements QuotationProviderDAO {
	/**
	 * Placeholder for the symbol used in a query URL.
	 */
	private static final String PLACEHOLDER_SYMBOL = "{symbol}";
	
	/**
	 * Placeholder for the country code used in a query URL.
	 */
	private static final String PLACEHOLDER_COUNTRY_CODE = "{country_code}";
	
	/**
	 * URL to quote API of CNBC: Current quotation.
	 */
	private static final String BASE_URL_CURRENT_QUOTATION = "https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?"
			+ "symbols=" + PLACEHOLDER_SYMBOL + PLACEHOLDER_COUNTRY_CODE + 
			"&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1";
	
	
	@Override
	public Quotation getCurrentQuotation(Instrument instrument) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange,
			InstrumentType instrumentType, Integer years) throws Exception {
		
		throw new Exception("Method is not supported.");
	}
	
	
	/**
	 * Converts the quotation data from CNBC provided as JSON String to a Quotation object.
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
			quoteResponse = (LinkedHashMap<?, ?>) map.get("FormattedQuoteResult");
			result = (ArrayList<?>) quoteResponse.get("FormattedQuote");
			resultAttributes = (LinkedHashMap<?, ?>) result.get(0);
			
			quotation.setCurrency(this.getCurrency((String) resultAttributes.get("currencyCode")));
			quotation.setClose(this.getPrice((String) resultAttributes.get("last"), quotation.getCurrency()));
		} catch (JsonMappingException e) {
			throw new Exception(e);
		} catch (JsonProcessingException e) {
			throw new Exception(e);
		}
		
		return quotation;
	}

	
	/**
	 * Gets the query URL for the current quotation of the given symbol and stock exchange.
	 * 
	 * @param symbol The symbol to be queried.
	 * @param stockExchange The stock exchange where the symbol is listed.
	 * @return The query URL.
	 * @throws Exception In case the URL could not be determined.
	 */
	protected String getQueryUrlCurrentQuotation(final String symbol, final StockExchange stockExchange) throws Exception  {
		String queryUrl = new String(BASE_URL_CURRENT_QUOTATION);
		
		queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, symbol);
		queryUrl = queryUrl.replace(PLACEHOLDER_COUNTRY_CODE, this.getCountryCodeParameter(stockExchange));
		
		return queryUrl;
	}
	
	
	/**
	 * Provides the country code URL parameter for the given StockExchange.
	 * 
	 * @param stockExchange The StockExchange.
	 * @return The country code URL parameter.
	 * @Exception In case the StockExchange is not supported.
	 */
	protected String getCountryCodeParameter(final StockExchange stockExchange) throws Exception {
		String countryCode = "";
		
		switch(stockExchange) {
			case TSX:
				countryCode = "-CA";
				break;
			case TSXV:
				countryCode = "-V";
				break;
			case CSE:
				throw new Exception("Stock Exchange 'CSE' is not supported by CNBC.");
			case LSE:
				countryCode = "-GB";
				break;
		default:
			break;
		}
		
		return countryCode;
	}
	
	
	/**
	 * Gets the currency from the CNBC API.
	 * 
	 * @param apiCurrency The currency as provided by CNBC.
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
	 * Gets the price from the CNBC API.
	 * 
	 * @param apiPrice The price as provided by CNBC.
	 * @param currency The Currency of the price.
	 * @return The price as used by the backend
	 */
	protected BigDecimal getPrice(final String apiPrice, final Currency currency) {
		BigDecimal price = new BigDecimal(apiPrice.replaceAll(",", ""));
		
		return price;
	}
}
