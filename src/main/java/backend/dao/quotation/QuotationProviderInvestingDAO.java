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
}
