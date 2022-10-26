package backend.model.instrument;

/**
 * Indicates a faulty reference between an instrument and its sector or industry group.
 * 
 * @author Michael
 */
public class InstrumentReferenceException extends Exception {
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 6159505688740156923L;
	
	/**
	 * The expected type of the reference.
	 */
	private InstrumentType expectedType;
	
	/**
	 * The actual type of the reference.
	 */
	private InstrumentType actualType;

	
	/**
	 * Cosntructor.
	 * 
	 * @param expectedType The expected type of the reference.
	 * @param actualType The actual type of the reference.
	 */
	public InstrumentReferenceException(final InstrumentType expectedType, final InstrumentType actualType) {
		this.expectedType = expectedType;
		this.actualType = actualType;
	}


	/**
	 * @return the expectedType
	 */
	public InstrumentType getExpectedType() {
		return expectedType;
	}


	/**
	 * @param expectedType the expectedType to set
	 */
	public void setExpectedType(InstrumentType expectedType) {
		this.expectedType = expectedType;
	}


	/**
	 * @return the actualType
	 */
	public InstrumentType getActualType() {
		return actualType;
	}


	/**
	 * @param actualType the actualType to set
	 */
	public void setActualType(InstrumentType actualType) {
		this.actualType = actualType;
	}
}
