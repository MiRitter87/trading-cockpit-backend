package backend.controller;

import java.util.Map;

import backend.dao.quotation.provider.QuotationProviderDAO;
import backend.dao.quotation.provider.QuotationProviderDAOFactory;
import backend.model.StockExchange;

/**
 * A Thread that retrieves data from a third-party data provider.
 * 
 * @author Michael
 */
public class DataRetrievalThread extends Thread {
	/**
	 * A Map of stock exchanges and their corresponding data providers.
	 */
	private Map<StockExchange, DataProvider> dataProviders;
	

	/**
	 * Gets the QuotationProviderDAO that is configured to be used for the given StockExchange.
	 * 
	 * @param stockExchange The StockExchange.
	 * @return The QuotationProviderDAO that is used for the given StockExchange.
	 * @throws Exception Failed to determine QuotationProviderDAO for the given StockExchange.
	 */
	public QuotationProviderDAO getQuotationProviderDAO(final StockExchange stockExchange) throws Exception {
		DataProvider dataProvider;
		QuotationProviderDAO quotationProviderDAO;
		
		dataProvider = this.dataProviders.get(stockExchange);
		
		if(dataProvider == null)
			throw new Exception("There is no data provider defined for the stock exchange: " + stockExchange.toString());
		
		quotationProviderDAO = QuotationProviderDAOFactory.getInstance().getQuotationProviderDAO(dataProvider);
		
		return quotationProviderDAO;
	}
	
	
	/**
	 * @return the dataProviders
	 */
	public Map<StockExchange, DataProvider> getDataProviders() {
		return dataProviders;
	}


	/**
	 * @param dataProviders the dataProviders to set
	 */
	public void setDataProviders(Map<StockExchange, DataProvider> dataProviders) {
		this.dataProviders = dataProviders;
	}
}
