package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Tests the Investing Quotation DAO.
 * 
 * @author Michael
 */
public class QuotationProviderInvestingDAOTest {
	@Test
	/**
	 * Tests getting current Quotation data from a stock listed at the NYSE.
	 */
	public void testGetCurrentQuotationNYSE() {
		String url = "https://www.investing.com/equities/amazon-com-inc";
	    WebClient webClient = new WebClient();
	    HtmlPage htmlPage;
	    
	    webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);
	    
		try {
			htmlPage = webClient.getPage(url);
			
			final List<DomElement> spans = htmlPage.getElementsByTagName("span");
			for (DomElement element : spans) {

			    if (element.getAttribute("data-test").equals("instrument-price-last") && element.getAttribute("class").equals("text-2xl")) {
			    	System.out.println(element.getFirstChild().asNormalizedText());
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
	
	
	@Test
	/**
	 * Tests initializing the WebClient from a local html page.
	 */
	public void testLoadLocalHtmlPage() {
		String userPath = System.getProperty("user.dir");
		String htmlPath = "file:\\" + userPath +  "\\src\\test\\resources\\investingNYSEQuoteAMZN.htm";
		String expectedTitleText, actualTitleText;
		HtmlPage htmlPage;
		WebClient webClient = new WebClient();
		
		webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);
		
		try {
			htmlPage = webClient.getPage(htmlPath);
			expectedTitleText = "Amazon Stock Price Today | NASDAQ AMZN Live Ticker - Investing.com";
			actualTitleText = htmlPage.getTitleText();
			
			assertEquals(expectedTitleText, actualTitleText);
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
