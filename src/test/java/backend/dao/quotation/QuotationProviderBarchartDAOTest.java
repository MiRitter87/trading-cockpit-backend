package backend.dao.quotation;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
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
		String url = "https://www.barchart.com/";
		WebClient webClient = new WebClient();
	    HtmlPage htmlPage;
	    CookieManager cookieManager = webClient.getCookieManager();
	    Set<Cookie> cookies;
	    
	    webClient.getOptions().setUseInsecureSSL(true);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setJavaScriptEnabled(false);
	    cookieManager.setCookiesEnabled(true);
	    
		try {
			try {
				htmlPage = webClient.getPage(url);
				
				//Cookies can be retrieved via the webClient
				cookies = cookieManager.getCookies();
				
				//Query historical EOD data.
				URL eodRequestUrl = new URL("https://www.barchart.com/proxies/timeseries/queryeod.ashx?symbol=AMZN&data=daily&maxrecords=640"
			    		+ "&volume=contract&order=asc&dividends=false&backadjust=false&daystoexpiration=1&contractroll=expiration");
				
				//Set header parameter x-xsrf-token from previously retrieved cookie
				WebRequest eodDataRequest = new WebRequest(eodRequestUrl);
				
				eodDataRequest.setAdditionalHeader("Connection", "keep-alive");
				eodDataRequest.setAdditionalHeader("Cookie", this.getCookiesAsString(cookies));
				eodDataRequest.setAdditionalHeader("Host", "www.barchart.com");
				eodDataRequest.setAdditionalHeader("Referer", "https://www.barchart.com/stocks/quotes/AMZN/overview");
				eodDataRequest.setAdditionalHeader("Sec-Fetch-Dest", "empty");
				eodDataRequest.setAdditionalHeader("Sec-Fetch-Mode", "cors");
				eodDataRequest.setAdditionalHeader("Sec-Fetch-Site", "same-origin");
				eodDataRequest.setAdditionalHeader("TE", "trailers");
				eodDataRequest.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/115.0");
				eodDataRequest.setAdditionalHeader("x-xsrf-token", this.getXsrfToken(cookies));
				
				htmlPage = webClient.getPage(eodRequestUrl);
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
	
	
	/**
	 * Gets the XSRF-Token from the Set of cookies.
	 * 
	 * @param cookies A Set of cookies.
	 * @return The XSRF-Token.
	 */
	private String getXsrfToken(final Set<Cookie> cookies) {
		Iterator<Cookie> cookieIterator;
		Cookie cookie;
		String xsrfToken = "";
		
		cookieIterator = cookies.iterator();
		while(cookieIterator.hasNext()) {
			cookie = cookieIterator.next();
			
			if(cookie.getName().equals("XSRF-TOKEN"))
				xsrfToken = cookie.getValue();
		}
		
		return xsrfToken;
	}
	
	
	/**
	 * Gets the cookies as String. The String is formatted for sending via a request header.
	 * 
	 * @param cookies A Set of cookies.
	 * @return The cookies as String.
	 */
	private String getCookiesAsString(final Set<Cookie> cookies) {
		Iterator<Cookie> cookieIterator;
		Cookie cookie;
		String cookieString = "";
		
		cookieIterator = cookies.iterator();
		while(cookieIterator.hasNext()) {
			cookie = cookieIterator.next();
			
			cookieString += cookie.getName();
			cookieString += "=";
			cookieString += cookie.getValue();
			cookieString += "; ";
			
			if(!cookieIterator.hasNext())
				cookieString += "bcFreeUserPageView=0; webinar152WebinarClosed=true";
		}
		
		return cookieString;
	}
}
