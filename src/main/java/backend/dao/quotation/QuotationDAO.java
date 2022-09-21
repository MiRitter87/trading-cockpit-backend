package backend.dao.quotation;

import java.util.List;

import backend.model.instrument.Quotation;

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
	 * Gets the most recent Quotation of each Instrument that matches the Minervini Trend Template.
	 * Only those quotations are provided that have an Indicator associated with them.
	 * 
	 * @return The most recent Quotation of each Instrument that matches the Minervini Trend Template.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getQuotationsMinerviniTrendTemplate() throws Exception;
	
	
	/**
	 * Gets the most recent Quotation of each Instrument that matches the Volatility Contraction Template.
	 * Only those quotations are provided that have an Indicator associated with them.
	 * 
	 * @return The most recent Quotation of each Instrument that matches the Volatility Contraction Template.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getQuotationsVolatilityContraction10Days() throws Exception;
	
	
	/**
	 * Gets the most recent Quotation of each Instrument that matches the Breakout Candidates Template.
	 * Only those quotations are provided that have an Indicator associated with them.
	 * 
	 * @return The most recent Quotation of each Instrument that matches the Breakout Candidates Template.
	 * @throws Exception Quotation determination failed.
	 */
	List<Quotation> getQuotationsBreakoutCandidates() throws Exception;
}
