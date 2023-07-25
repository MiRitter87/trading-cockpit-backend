package backend.dao.quotation.provider;

import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Interface for access to quotation data of Third-Party data providers.
 * 
 * @author Michael
 */
public interface QuotationProviderDAO {
	/**
	 * Provides a Quotation with the most recent data of an Instrument.
	 * 
	 * @param instrument The Instrument for which the current Quotation is requested.
	 * @return The most recent Quotation of an instrument.
	 * @throws Exception In case the Quotation retrieval failed.
	 */
	Quotation getCurrentQuotation(final Instrument instrument) throws Exception;
	
	
	/**
	 * Gets historical quotations of an instrument.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @param instrumentType The InstrumentType.
	 * @param years The number of years for which quotations are provided.
	 * @return A list of historical quotations.
	 * @throws Exception In case the quotation retrieval failed.
	 */
	List<Quotation> getQuotationHistory(final String symbol, final StockExchange stockExchange, final InstrumentType instrumentType, 
			Integer years) throws Exception;
}
