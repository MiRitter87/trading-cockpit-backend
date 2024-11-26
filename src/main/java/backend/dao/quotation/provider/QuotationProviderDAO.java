package backend.dao.quotation.provider;

import java.util.List;

import backend.model.instrument.Instrument;
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
    Quotation getCurrentQuotation(Instrument instrument) throws Exception;

    /**
     * Gets historical quotations of an instrument.
     *
     * @param instrument The Instrument for which historical quotations are requested.
     * @param years      The number of years for which quotations are provided.
     * @return A list of historical quotations.
     * @throws Exception In case the quotation retrieval failed.
     */
    List<Quotation> getQuotationHistory(Instrument instrument, Integer years) throws Exception;
}
