package backend.dao.quotation;

import backend.model.Currency;
import backend.model.StockExchange;

/**
 * Abstract base class of the different QuotationProviderDAO implementations.
 * This class provides some methods that are mutually used by the concrete DAOs.
 * 
 * @author Michael
 */
public abstract class AbstractQuotationProviderDAO {
	/**
	 * Gets the Currency for the given StockExchange.
	 * 
	 * @param stockExchange The StockExchange.
	 * @return
	 */
	protected Currency getCurrencyForStockExchange(final StockExchange stockExchange) {
		switch(stockExchange) {
			case TSX:
			case TSXV:
			case CSE:
				return Currency.CAD;
			case NYSE:
				return Currency.USD;
			case LSE:
				return Currency.GBP;
			default:
				break;
		}
		
		return null;
	}
}
