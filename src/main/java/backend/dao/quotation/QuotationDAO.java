package backend.dao.quotation;

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
	
	
	/**
	 * Inserts the given quotations.
	 * 
	 * @param quotations The quotations to be inserted.
	 * @throws Exception Insertion failed.
	 */
	void insertQuotations(final List<Quotation> quotations) throws Exception;
	
	
	/**
	 * Deletes the given quotations.
	 * 
	 * @param quotations The quotations to be deleted.
	 * @throws Exception Deletion failed.
	 */
	void deleteQuotations(final List<Quotation> quotations) throws Exception;
	
	
	/**
	 * Gets the Quotation with the given ID.
	 * 
	 * @param id The ID of the Quotation.
	 * @return The Quotation with the given ID.
	 * @throws Exception Quotation determination failed.
	 */
	Quotation getQuotation(final Integer id) throws Exception;
	
	
	/**
	 * Gets the quotations of the Instrument with the given ID.
	 * 
	 * @param instrumentId The ID of the Instrument.
	 * @return The quotations of that Instrument.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getQuotationsOfInstrument(final Integer instrumentId) throws Exception;
}
