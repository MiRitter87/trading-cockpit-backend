package backend.model;

import java.text.MessageFormat;
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
	 * Arguments of the message.
	 */
	private Object[] arguments;
	
	
	/**
	 * Constructor.
	 * 
	 * @param messageKey The key of the exception message.
	 */
	public LocalizedException(final String messageKey) {
		this.messageKey = messageKey;
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param messageKey The key of the exception message.
	 * @param arguments Message arguments.
	 */
	public LocalizedException(final String messageKey, final Object ... arguments) {
		this.messageKey = messageKey;
		this.arguments = arguments;
	}
	
	
	@Override
	public String getLocalizedMessage() {
		if(this.arguments == null)
			return this.resources.getString(this.messageKey);
		else
			return MessageFormat.format(this.resources.getString(this.messageKey), this.arguments);
	}
}
