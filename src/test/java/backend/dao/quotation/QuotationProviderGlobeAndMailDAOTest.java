package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests the GlobeAndMail Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAOTest {
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the TSX/V.
	 */
	public void testGetCurrentQuotationTSXV() {
		String url = "https://www.theglobeandmail.com/investing/markets/stocks/PMET-X/";
	    WebClient webClient = new WebClient();
	    HtmlPage htmlPage;

	    webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);

		try {
			htmlPage = webClient.getPage(url);
			
			final List<DomElement> spans = htmlPage.getElementsByTagName("span");
			for (DomElement element : spans) {

			    if (element.getAttribute("class").equals("barchart-overview-field-value")) {
			    	DomElement firstChild = element.getFirstElementChild();
			    	String nameAttribute = firstChild.getAttribute("name");

			    	if(nameAttribute.equals("lastPrice"))
			    		System.out.println(firstChild.getAttribute("value"));
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
	}
}
