package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * Explorative tests trying to query EOD data from barchart service that is being protected via session cookies.
 * 
 * @author Michael
 */
public class QuotationProviderBarchartDAOTest {
	//@Test
	/**
	 * Tests the retrieval of session cookies from the Website.
	 */
	public void testGetSessionCookies() {
		String url = "https://www.barchart.com/";
		WebClient webClient = new WebClient();
	    HtmlPage htmlPage;
	    CookieManager cookieManager = webClient.getCookieManager();
	    Set<Cookie> cookies;
	    Iterator<Cookie> cookieIterator;
	    Cookie cookie;
	    
	    webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);
	    cookieManager.setCookiesEnabled(true);
	    
		try {
			try {
				htmlPage = webClient.getPage(url);
				
				//Cookies can be retrieved via the webClient
				cookies = cookieManager.getCookies();
				
				cookieIterator = cookies.iterator();
				while(cookieIterator.hasNext()) {
					cookie = cookieIterator.next();
					System.out.println(cookie.getName());
					System.out.println(cookie.getValue());
				}
			} catch (FailingHttpStatusCodeException e) {
				fail(e.getMessage());
			} catch (MalformedURLException e) {
				fail(e.getMessage());
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
	    finally {
	    	webClient.close();
	    }
	}
	
	
	//@Test
	/**
	 * Requests EOD data using the cookies from the initial request.
	 */
	public void testGetEodDataUsingCookie() {
		String url = "https://www.barchart.com/proxies/timeseries/queryeod.ashx?symbol=AAPL&data=daily&maxrecords=640"
	    		+ "&volume=contract&order=asc&dividends=false&backadjust=false&daystoexpiration=1&contractroll=expiration";
				
		//TODO Set header parameter x-xsrf-token from previously retrieved cookie
		
		//htmlPage = webClient.getPage(url);
	}
}
