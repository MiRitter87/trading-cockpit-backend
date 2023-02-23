package backend.dao.quotation;

import java.util.List;

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
		return null;
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
}
