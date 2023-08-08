package backend.model.webservice;

/**
 * A message as part of a WebService result.
 *
 * @author Michael
 */
public class WebServiceMessage {
    /**
     * The type of the message.
     */
    private WebServiceMessageType type;

    /**
     * The text of the WebService message.
     */
    private String text;

    /**
     * Default Constructor.
     */
    public WebServiceMessage() {

    }

    /**
     * Creates and initializes a new WebService message.
     *
     * @param type The type of the message.
     * @param text The message text.
     */
    public WebServiceMessage(final WebServiceMessageType type, final String text) {
        this.type = type;
        this.text = text;
    }

    /**
     * @return the type
     */
    public WebServiceMessageType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final WebServiceMessageType type) {
        this.type = type;
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
    public void setText(final String text) {
        this.text = text;
    }
}
