package backend.controller.instrumentCheck;

import java.util.Date;

/**
 * Exception indicating that no quotations exist.
 * Optionally a date can be defined after which no quotations exist.
 * 
 * @author Michael
 *
 */
public class NoQuotationsExistException extends Exception {
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = -2309668483767916515L;
	
	/**
	 * The date after which no quotations exist.
	 */
	private Date date;
	
	
	/**
	 * Default constructor.
	 */
	public NoQuotationsExistException() {
		
	}
	
	
	/**
	 * Initializes the Exception with the given date.
	 * 
	 * @param date The date.
	 */
	public NoQuotationsExistException(final Date date) {
		this.date = date;
	}


	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}	
}
