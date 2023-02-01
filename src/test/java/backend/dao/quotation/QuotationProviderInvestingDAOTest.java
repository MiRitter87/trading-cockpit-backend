package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
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
			System.out.println(htmlPage.getTitleText());
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
