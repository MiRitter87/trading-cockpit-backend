package backend.dao.quotation.persistence;

import java.util.List;

import backend.model.LocalizedException;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.webservice.ScanTemplate;

/**
 * Interface for Quotation persistence.
 *
 * @author Michael
 */
public interface QuotationDAO {
    /**
     * Inserts the given quotations.
     *
     * @param quotations The quotations to be inserted.
     * @throws Exception Insertion failed.
     */
    void insertQuotations(List<Quotation> quotations) throws Exception;

    /**
     * Deletes the given quotations.
     *
     * @param quotations The quotations to be deleted.
     * @throws Exception Deletion failed.
     */
    void deleteQuotations(List<Quotation> quotations) throws Exception;

    /**
     * Updates the given quotations.
     *
     * @param quotations The quotations to be updated.
     * @throws Exception Updating failed.
     */
    void updateQuotations(List<Quotation> quotations) throws Exception;

    /**
     * Gets the Quotation with the given ID.
     *
     * @param id The ID of the Quotation.
     * @return The Quotation with the given ID.
     * @throws Exception Quotation determination failed.
     */
    Quotation getQuotation(Integer id) throws Exception;

    /**
     * Gets the quotations of the Instrument with the given ID.
     *
     * @param instrumentId The ID of the Instrument.
     * @return The quotations of that Instrument.
     * @throws Exception Quotation determination failed.
     */
    List<Quotation> getQuotationsOfInstrument(Integer instrumentId) throws Exception;

    /**
     * Gets the most recent Quotation of each Instrument with the given InstrumentType. Only those quotations are
     * provided that have an Indicator associated with them.
     *
     * @param instrumentType The InstrumentType.
     * @return The most recent Quotation of each Instrument.
     * @throws Exception Quotation determination failed.
     */
    List<Quotation> getRecentQuotations(InstrumentType instrumentType) throws Exception;

    /**
     * Gets the most recent Quotation for each Instrument of the given List.
     *
     * @param list The List containing instruments.
     * @return The most recent Quotation of each Instrument of the List.
     * @throws Exception Quotation determination failed.
     */
    List<Quotation> getRecentQuotationsForList(backend.model.list.List list) throws Exception;

    /**
     * Gets the most recent Quotation of each Instrument that matches the given Template and InstrumentType. Only those
     * quotations are provided that have an Indicator associated with them.
     *
     * @param scanTemplate   The Template for Quotation query.
     * @param instrumentType The InstrumentType.
     * @param startDate      The start date for the RS number determination. Format used: yyyy-MM-dd. Parameter can be
     *                       omitted (null).
     * @param minLiquidity   The minimum trading liquidity that is required. Parameter can be omitted (null).
     * @return The most recent Quotation of each Instrument that matches the given Template.
     * @throws LocalizedException Exception with error message to be displayed to the user.
     * @throws Exception          Quotation determination failed.
     */
    List<Quotation> getQuotationsByTemplate(ScanTemplate scanTemplate, InstrumentType instrumentType, String startDate,
            Float minLiquidity) throws LocalizedException, Exception;
}
