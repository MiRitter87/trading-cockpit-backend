package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the theglobeandmail.com website.
 * A local HTML file is used instead of a live query to theglobeandmail.com.
 * 
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAOStub extends QuotationProviderGlobeAndMailDAO {
	@Override
	public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
		String userPath = System.getProperty("user.dir");
		String htmlPath = "file:\\" + userPath;
		WebClient webClient = new WebClient();
		HtmlPage htmlPage;
		Quotation quotation = new Quotation();
		
		if(instrument.getSymbol().equals("PMET") && instrument.getStockExchange().equals(StockExchange.TSXV))
			htmlPath = htmlPath + "\\src\\test\\resources\\GlobeAndMailTSXVQuotePMET.htm";
		
		try {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(false);
			
			htmlPage = webClient.getPage(htmlPath);
			
			final List<DomElement> spans = htmlPage.getElementsByTagName("span");
			for (DomElement element : spans) {

			    if (element.getAttribute("class").equals("barchart-overview-field-value")) {
			    	DomElement firstChild = element.getFirstElementChild();
			    	String nameAttribute = firstChild.getAttribute("name");

			    	if(nameAttribute.equals("lastPrice"))
			    		quotation.setClose(new BigDecimal(firstChild.getAttribute("value")));
			    		quotation.setCurrency(Currency.CAD);
			    }
			}
		} 
		catch(FailingHttpStatusCodeException e) {
			fail(e.getMessage());
		} 
		catch(MalformedURLException e) {
			fail(e.getMessage());
		} 
		catch(IOException e) {
			fail(e.getMessage());
		}
	    finally {
	    	webClient.close();
	    }
		
		return quotation;
	}
}
