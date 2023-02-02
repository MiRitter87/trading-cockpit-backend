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
 * Provides access to quotation data using the investing.com website.
 * 
 * @author Michael
 */
public class QuotationProviderInvestingDAO implements QuotationProviderDAO {
	/**
	 * Placeholder for the type used in a query URL.
	 */
	private static final String PLACEHOLDER_TYPE = "{type}";
	
	/**
	 * Placeholder for the company used in a query URL.
	 */
	private static final String PLACEHOLDER_COMPANY = "{company}";
	
	/**
	 * URL to quote investing.com: Current quotation.
	 */
	private static final String BASE_URL_CURRENT_QUOTATION = "https://www.investing.com/" + PLACEHOLDER_TYPE + "/" + PLACEHOLDER_COMPANY;
	
	/**
	 * Type used in the URL for equities.
	 */
	private static final String URL_TYPE_EQUITY = "equities";
	
	/**
	 * Type used in the URL for ETFs.
	 */
	private static final String URL_TYPE_ETF = "etfs";
	

	@Override
	public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
		//TODO Build correct url based on Instrument attributes.
		String url = "https://www.investing.com/equities/amazon-com-inc";
	    WebClient webClient = new WebClient();
	    HtmlPage htmlPage;
	    Quotation quotation;
	    
	    webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);
	    
		try {
			htmlPage = webClient.getPage(url);
			
			quotation = this.getQuotationFromHtmlPage(htmlPage);
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
	 * @return The current Quotation.
	 * @throws Exception Failed to extract Quotation data from given HTML page.
	 */
	protected Quotation getQuotationFromHtmlPage(final HtmlPage htmlPage) throws Exception {
		Quotation quotation = new Quotation();
		String currentPrice = "";
		
		final List<DomElement> spans = htmlPage.getElementsByTagName("span");
		for (DomElement element : spans) {

		    if (element.getAttribute("data-test").equals("instrument-price-last") && element.getAttribute("class").equals("text-2xl")) {
		    	currentPrice = element.getFirstChild().asNormalizedText();
		    }
		}
		
		if("".equals(currentPrice))
			throw new Exception("The price could not be determined.");
		
		quotation.setClose(new BigDecimal(currentPrice));
		//TODO Set currency.
		
		return quotation;
	}
	
	
	/**
	 * Gets the query URL for the current quotation of the given Instrument.
	 * 
	 * @param instrument The Instrument for which the query URL is determined.
	 * @return The query URL.
	 */
	protected String getQueryUrlCurrentQuotation(final Instrument instrument) {
		String queryUrl = new String(BASE_URL_CURRENT_QUOTATION);
		
		queryUrl = queryUrl.replace(PLACEHOLDER_TYPE, this.getTypeForQueryURL(instrument));
		queryUrl = queryUrl.replace(PLACEHOLDER_COMPANY, instrument.getCompanyPathInvestingCom());
		
		return queryUrl;
	}
	
	
	/**
	 * Gets the Instrument type for construction of the query URL.
	 * 
	 * @param instrument The Instrument.
	 * @return The type used in the URL.
	 */
	private String getTypeForQueryURL(final Instrument instrument) {
		switch(instrument.getType()) {
			case STOCK:
				return URL_TYPE_EQUITY;
			case ETF:
			case SECTOR:
			case IND_GROUP:
				return URL_TYPE_ETF;
			default:
				return "";
		}
	}
}
