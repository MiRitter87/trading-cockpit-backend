package backend.model.webservice;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlSeeAlso;

import backend.model.chart.HorizontalLineArray;
import backend.model.instrument.InstrumentArray;
import backend.model.instrument.QuotationArray;
import backend.model.list.ListArray;
import backend.model.priceAlert.PriceAlertArray;
import backend.model.protocol.Protocol;
import backend.model.scan.ScanArray;
import backend.model.statistic.StatisticArray;

/**
 * The result of a WebService call. A result consists of n messages and data.
 *
 * @author Michael
 *
 */
@XmlSeeAlso({ PriceAlertArray.class, InstrumentArray.class, QuotationArray.class, ListArray.class, ScanArray.class,
        StatisticArray.class, HorizontalLineArray.class, Protocol.class })
public class WebServiceResult {
    /**
     * A list of messages.
     */
    private List<WebServiceMessage> messages;

    /**
     * The result data of a WebService call.
     */
    private Object data;

    /**
     * Default Constructor.
     */
    public WebServiceResult() {
        this.messages = new ArrayList<WebServiceMessage>();
    }

    /**
     * Creates and initializes a new WebService result.
     *
     * @param data The result data to be set.
     */
    public WebServiceResult(final Object data) {
        this.data = data;
        this.messages = new ArrayList<WebServiceMessage>();
    }

    /**
     * Adds the given message to the message list of the WebService result.
     *
     * @param message The message to be added.
     */
    public void addMessage(final WebServiceMessage message) {
        this.messages.add(message);
    }

    /**
     * Adds a list of messages to the message list of the WebService result.
     *
     * @param messagesToAdd A list of messages to be added.
     */
    public void addMessages(final List<WebServiceMessage> messagesToAdd) {
        this.messages.addAll(messagesToAdd);
    }

    /**
     * @return the messages
     */
    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    public List<WebServiceMessage> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(final List<WebServiceMessage> messages) {
        this.messages = messages;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(final Object data) {
        this.data = data;
    }
}
