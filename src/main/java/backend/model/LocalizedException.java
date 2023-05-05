package backend.model;

import java.util.ResourceBundle;

/**
 * An Exception that supports localization of message texts.
 * 
 * @author Michael
 */
public class LocalizedException extends Exception {
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 3599943255472729482L;

	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * The key of the localized resource.
	 */
	private String messageKey;
	
	
	/**
	 * Constructor.
	 * 
	 * @param messageKey The key of the exception message.
	 */
	public LocalizedException(final String messageKey) {
		this.messageKey = messageKey;
	}
	
	
	@Override
	public String getLocalizedMessage() {
		return this.resources.getString(this.messageKey);
	}
}
