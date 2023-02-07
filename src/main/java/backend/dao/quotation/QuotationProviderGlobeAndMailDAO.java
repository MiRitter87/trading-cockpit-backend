package backend.dao.quotation;

import java.util.List;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Provides access to quotation data using the theglobeandmail.com website.
 * 
 * @author Michael
 */
public class QuotationProviderGlobeAndMailDAO extends AbstractQuotationProviderDAO implements QuotationProviderDAO {

	@Override
	public Quotation getCurrentQuotation(Instrument instrument) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange,
			InstrumentType instrumentType, Integer years) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
