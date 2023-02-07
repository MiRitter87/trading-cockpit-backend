package backend.dao.quotation;

import java.math.BigDecimal;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the theglobeandmail.com website.
 * 
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAO extends AbstractQuotationProviderDAO implements QuotationProviderDAO {
	/**
	 * Placeholder for the symbol used in a query URL.
	 */
	private static final String PLACEHOLDER_SYMBOL = "{symbol}";
	
	/**
	 * Placeholder for the stock exchange used in a query URL.
	 */
	private static final String PLACEHOLDER_EXCHANGE = "{exchange}";
	
	/**
	 * URL to quote theglobeandmail.com: Current quotation.
	 */
	private static final String BASE_URL_CURRENT_QUOTATION = "https://www.theglobeandmail.com/investing/markets/stocks/" + 
			PLACEHOLDER_SYMBOL + "-" + PLACEHOLDER_EXCHANGE + "/";
	

	@Override
	public Quotation getCurrentQuotation(Instrument instrument) throws Exception {
		String url = this.getQueryUrlCurrentQuotation(instrument);
	    WebClient webClient = new WebClient();
	    HtmlPage htmlPage;
	    Quotation quotation;
	    
	    webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);
	    
		try {
			htmlPage = webClient.getPage(url);
			
			quotation = this.getQuotationFromHtmlPage(htmlPage, instrument);
		}
	    finally {
	    	webClient.close();
	    }
		
		return quotation;
	}

	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange,
			InstrumentType instrumentType, Integer years) throws Exception {

		throw new Exception("Method is not supported.");
	}

	
	/**
	 * Gets the current Quotation from the HTML page.
	 * 
	 * @param htmlPage The HTML page containing the Quotation information.
	 * @param instrument The Instrument for which Quotation data are extracted.
	 * @return The current Quotation.
	 * @throws Exception Failed to extract Quotation data from given HTML page.
	 */
	protected Quotation getQuotationFromHtmlPage(final HtmlPage htmlPage, final Instrument instrument) throws Exception {
		Quotation quotation = new Quotation();
		String currentPrice = "";
		final List<DomElement> spans = htmlPage.getElementsByTagName("span");
		
		for (DomElement element : spans) {
		    if (element.getAttribute("class").equals("barchart-overview-field-value")) {
		    	DomElement firstChild = element.getFirstElementChild();
		    	String nameAttribute = firstChild.getAttribute("name");

		    	if(nameAttribute.equals("lastPrice"))
		    		currentPrice = firstChild.getAttribute("value");
		    }
		}
		
		if("".equals(currentPrice))
			throw new Exception("The price could not be determined.");
		
		quotation.setClose(new BigDecimal(currentPrice));
		quotation.setCurrency(this.getCurrencyForStockExchange(instrument.getStockExchange()));
		
		return quotation;
	}
	
	
	/**
	 * Gets the query URL for the current quotation of the given Instrument.
	 * 
	 * @param instrument The Instrument for which the query URL is determined.
	 * @return The query URL.
	 * @throws Exception URL could not be created.
	 */
	protected String getQueryUrlCurrentQuotation(final Instrument instrument) throws Exception {
		String queryUrl = new String(BASE_URL_CURRENT_QUOTATION);
		
		if(instrument.getStockExchange() == StockExchange.LSE || instrument.getStockExchange() == StockExchange.NYSE)
			throw new Error("The DAO for TheGlobeAndMail does not provide current quotations for the exchange: " + instrument.getStockExchange());
		
		queryUrl = queryUrl.replace(PLACEHOLDER_SYMBOL, instrument.getSymbol());
		queryUrl = queryUrl.replace(PLACEHOLDER_EXCHANGE, this.getExchangeForQueryURL(instrument));
		
		return queryUrl;
	}
	
	
	/**
	 * Gets the stock exchange for construction of the query URL.
	 * 
	 * @param instrument The Instrument.
	 * @return The stock exchange used in the URL.
	 */
	private String getExchangeForQueryURL(final Instrument instrument) {
		switch(instrument.getStockExchange()) {
			case TSX:
				return "T";
			case TSXV:
				return "X";
			case CSE:
				return "CN";
			default:
				return "";
		}
	}
}
