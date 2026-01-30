package backend.dao.quotation.provider;

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
    // @Test
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

                // Cookies can be retrieved via the webClient
                cookies = cookieManager.getCookies();

                cookieIterator = cookies.iterator();
                while (cookieIterator.hasNext()) {
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
        } finally {
            webClient.close();
        }
    }

    // @Test
    /**
     * Requests EOD data using the cookies from the initial request.
     */
    public void testGetEodDataUsingCookie() {
        String url = "https://www.barchart.com/";
        WebClient webClient = new WebClient();
        HtmlPage htmlPage;
        CookieManager cookieManager = webClient.getCookieManager();
        WebRequest eodDataRequest;

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        cookieManager.setCookiesEnabled(true);

        try {
            try {
                htmlPage = webClient.getPage(url);

                // Query historical EOD data.
                URL eodRequestUrl = new URL("https://www.barchart.com/proxies/timeseries/historical/queryeod.ashx?"
                        + "symbol=MSFT&data=daily&maxrecords=640&volume=contract&order=asc&dividends=false"
                        + "&backadjust=false&daystoexpiration=1&contractroll=combined&splits=true&padded=false");

                // Set header parameter x-xsrf-token from previously retrieved cookie
                eodDataRequest = this.getEodDataRequest(eodRequestUrl, cookieManager);

                htmlPage = webClient.getPage(eodDataRequest);
            } catch (FailingHttpStatusCodeException e) {
                fail(e.getMessage());
            } catch (MalformedURLException e) {
                fail(e.getMessage());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        } finally {
            webClient.close();
        }
    }

    /**
     * Gets a WebRequest based on the given URL.
     *
     * @param eodRequestUrl The request URL.
     * @param cookieManager The CookieManager.
     * @return The WebRequest.
     */
    private WebRequest getEodDataRequest(final URL eodRequestUrl, final CookieManager cookieManager) {
        WebRequest eodDataRequest = new WebRequest(eodRequestUrl);
        Set<Cookie> cookies;

        // Cookies can be retrieved via the webClient
        cookies = cookieManager.getCookies();

        eodDataRequest.setAdditionalHeader("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64; rv:147.0) Gecko/20100101 Firefox/147.0");
        eodDataRequest.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        eodDataRequest.setAdditionalHeader("Accept-Language", "de,en-US;q=0.9,en;q=0.8");
        eodDataRequest.setAdditionalHeader("Accept-Encoding", "gzip, deflate, br, zstd");
        eodDataRequest.setAdditionalHeader("Sec-GPC", "1");
        eodDataRequest.setAdditionalHeader("Connection", "keep-alive");
        eodDataRequest.setAdditionalHeader("Cookie", this.getCookiesAsString(cookies));
        eodDataRequest.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
        eodDataRequest.setAdditionalHeader("Sec-Fetch-Dest", "document");
        eodDataRequest.setAdditionalHeader("Sec-Fetch-Mode", "navigate");
        eodDataRequest.setAdditionalHeader("Sec-Fetch-Site", "none");
        eodDataRequest.setAdditionalHeader("Sec-Fetch-User", "?1");

        eodDataRequest.setAdditionalHeader("x-xsrf-token", this.getXsrfToken(cookies));

        return eodDataRequest;
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
        while (cookieIterator.hasNext()) {
            cookie = cookieIterator.next();

            if (cookie.getName().equals("XSRF-TOKEN")) {
                xsrfToken = cookie.getValue();
            }
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
        while (cookieIterator.hasNext()) {
            cookie = cookieIterator.next();

            cookieString += cookie.getName();
            cookieString += "=";
            cookieString += cookie.getValue();
            cookieString += "; ";

            if (!cookieIterator.hasNext()) {
                cookieString += "bcFreeUserPageView=0; webinar152WebinarClosed=true";
            }
        }

        return cookieString;
    }
}
