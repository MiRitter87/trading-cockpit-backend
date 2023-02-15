package backend.model.protocol;

import java.util.Date;

/**
 * The entry of a protocol that informs about the health of an Instrument at a certain date.
 * The behavior of an instruments price and volume can confirm or violate the current trend.
 * 
 * @author Michael
 */
public class ProtocolEntry {
	/**
	 * The date.
	 */
	private Date date;
	
	/**
	 * The category.
	 */
	private ProtocolEntryCategory category;
	
	/**
	 * The protocol text.
	 */
	private String text;
	
	
	/**
	 * Default constructor.
	 */
	public ProtocolEntry() {
		
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

	/**
	 * @return the category
	 */
	public ProtocolEntryCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(ProtocolEntryCategory category) {
		this.category = category;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}