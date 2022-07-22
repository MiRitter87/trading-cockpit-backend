package backend.dao.instrument;

import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Interface for quotation persistence.
 * 
 * @author Michael
 */
public interface QuotationDAO {
	/**
	 * Provides a quotation with the most recent data of an instrument.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @return The most recent quotation of an instrument.
	 * @throws Exception In case the quotation retrieval failed.
	 */
	Quotation getCurrentQuotation(final String symbol, final StockExchange stockExchange) throws Exception;
	
	
	/**
	 * Gets historical quotations of an instrument.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @param years The number of years for which quotations are provided.
	 * @return A list of historical quotations.
	 * @throws Exception In case the quotation retrieval failed.
	 */
	List<Quotation> getQuotationHistory(final String symbol, final StockExchange stockExchange, final Integer years) throws Exception;
}
