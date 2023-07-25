package backend.dao.quotation.provider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import backend.dao.quotation.provider.QuotationProviderInvestingDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Stub to simulate receiving of quotation data using the investing.com website.
 * A local HTML file is used instead of a live query to investing.com.
 * 
 * @author Michael
 */
public class QuotationProviderInvestingDAOStub extends QuotationProviderInvestingDAO {
	@Override
	public Quotation getCurrentQuotation(final Instrument instrument) throws Exception {
		String userPath = System.getProperty("user.dir");
		String htmlPath = "file:\\" + userPath;
		WebClient webClient = new WebClient();
		HtmlPage htmlPage;
		Quotation quotation;
		
		if(instrument.getSymbol().equals("AMZN") && instrument.getStockExchange().equals(StockExchange.NDQ) && 
				!instrument.getCompanyPathInvestingCom().equals("fallback")) {
			
			htmlPath = htmlPath + "\\src\\test\\resources\\Investing\\investingNYSEQuoteAMZN.htm";			
		}
		else if(instrument.getSymbol().equals("AMZN") && instrument.getStockExchange().equals(StockExchange.NDQ) && 
				instrument.getCompanyPathInvestingCom().equals("fallback")) {
			
			htmlPath = htmlPath + "\\src\\test\\resources\\Investing\\investingNYSEQuoteAMZNFallback.htm";
		}
		
		try {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(false);
			
			htmlPage = webClient.getPage(htmlPath);
			
			quotation = this.getQuotationFromHtmlPage(htmlPage, instrument);			
		}
		finally {
			webClient.close();
		}
		
		return quotation;
	}
}
