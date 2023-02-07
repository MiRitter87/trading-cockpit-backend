package backend.dao.quotation;

import java.math.BigDecimal;
import java.util.List;

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
}
