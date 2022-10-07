package backend.dao.quotation;

import java.util.List;

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
	void insertQuotations(final List<Quotation> quotations) throws Exception;
	
	
	/**
	 * Deletes the given quotations.
	 * 
	 * @param quotations The quotations to be deleted.
	 * @throws Exception Deletion failed.
	 */
	void deleteQuotations(final List<Quotation> quotations) throws Exception;
	
	
	/**
	 * Updates the given quotations.
	 * 
	 * @param quotations The quotations to be updated.
	 * @throws Exception Updating failed.
	 */
	void updateQuotations(final List<Quotation> quotations) throws Exception;
	
	
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
	
	
	/**
	 * Gets the most recent Quotation of each Instrument.
	 * Only those quotations are provided that have an Indicator associated with them.
	 * 
	 * @return The most recent Quotation of each Instrument.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getRecentQuotations() throws Exception;
	
	
	/**
	 * Gets the most recent Quotation for each Instrument of the given List.
	 * 
	 * @param list The List containing instruments.
	 * @return The most recent Quotation of each Instrument of the List.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getRecentQuotationsForList(final backend.model.list.List list) throws Exception;
	
	
	/**
	 * Gets the most recent Quotation of each Instrument that matches the given Template.
	 * Only those quotations are provided that have an Indicator associated with them.
	 * 
	 * @param scanTemplate The Template for Quotation query.
	 * @return The most recent Quotation of each Instrument that matches the given Template.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getQuotationsByTemplate(final ScanTemplate scanTemplate) throws Exception;
}
