package backend.model.instrument;

/**
 * Query parameter for instrument selection. Specifies the quotations of an instrument which are requested. 
 * 
 * @author Michael
 */
public enum InstrumentQuotationQueryParam {
	/**
	 * No quotations are requested.
	 */
	NONE,
	
	/**
	 * All quotations are requested.
	 */
	ALL,
	
	/**
	 * Only the most recent quotation is requested.
	 */
	MOST_RECENT
}
