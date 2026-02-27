package backend.dao.quotation.provider;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import backend.model.instrument.Quotation;

/**
 * Tests the QuotationProviderGoogleDAO.
 *
 * @author MiRitter87
 */
public class QuotationProviderGoogleDAOTest {
    /**
     * Explorative test trying to get the current Quotation from Google Finance.
     */
    // @Test
    public void testGetCurrentQuotation() {
        final String url = "https://www.google.com/finance/quote/GLEN:LON";
        WebClient webClient = new WebClient();
        HtmlPage htmlPage;
        CookieManager cookieManager = webClient.getCookieManager();
        WebRequest eodDataRequest;
        Quotation quotation;

        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        cookieManager.setCookiesEnabled(true);

        try {
            try {
                htmlPage = webClient.getPage(url);

                System.out.println(htmlPage.asXml());
                quotation = this.getQuotationFromHtmlPage(htmlPage);
            } catch (FailingHttpStatusCodeException e) {
                fail(e.getMessage());
            } catch (MalformedURLException e) {
                fail(e.getMessage());
            } catch (IOException e) {
                fail(e.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            webClient.close();
        }
    }

    /**
     * Extract the current Quotation from the HTML page.
     *
     * @param htmlPage The HTML page.
     * @return The current price.
     * @throws Exception Failed to extract price.
     */
    private Quotation getQuotationFromHtmlPage(final HtmlPage htmlPage) throws Exception {
        Quotation quotation = new Quotation();
        String currentPrice = "";
        final List<DomElement> divs = htmlPage.getByXPath("//div");

        for (DomElement element : divs) {
            if (element.getAttribute("class").equals("YMlKec fxKbKc")) {
                DomElement firstChild = element.getFirstElementChild();
                String nameAttribute = firstChild.getAttribute("name");

                if (nameAttribute.equals("lastPrice")) {
                    currentPrice = firstChild.getAttribute("value");
                }
            }
        }

        if ("".equals(currentPrice)) {
            throw new Exception("The price could not be determined.");
        }

        quotation.setClose(new BigDecimal(currentPrice));

        return quotation;
    }
}
