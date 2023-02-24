package backend.dao.quotation;

import backend.controller.DataProvider;
import backend.controller.MainController;
import okhttp3.OkHttpClient;

/**
 * Factory class for QuotationProviderDAO implementations.
 * 
 * @author Michael
 */
public class QuotationProviderDAOFactory {
	/**
	 * Returns a QuotationProviderDAO implementation based on the given DataProvider.
	 * 
	 * @param dataProvider The DataProvider for which the QuotationProviderDAO is requested.
	 * @return The QuotationProviderDAO for the given DataProvider.
	 */
	public static QuotationProviderDAO getQuotationProviderDAO(final DataProvider dataProvider) {
		OkHttpClient okHttpClient = MainController.getInstance().getOkHttpClient();	
		
		switch(dataProvider) {
			case YAHOO:
				return new QuotationProviderYahooDAO(okHttpClient);
			case MARKETWATCH:
				return new QuotationProviderMarketWatchDAO(okHttpClient);
			case CNBC:
				return new QuotationProviderCNBCDAO(okHttpClient);
			case INVESTING:
				return new QuotationProviderInvestingDAO();
			case GLOBEANDMAIL:
				return new QuotationProviderGlobeAndMailDAO();
		}
		
		return null;
	}
}
