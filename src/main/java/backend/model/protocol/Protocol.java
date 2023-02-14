package backend.model.protocol;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A protocol that consists of multiple protocol entries.
 * 
 * @author Michael
 */
public class Protocol {
	/**
	 * A list of protocol entries.
	 */
	private List<ProtocolEntry> protocolEntries;
	
	
	/**
	 * Default constructor.
	 */
	public Protocol() {
		this.protocolEntries = new ArrayList<>();
	}

	
	/**
	 * @return the protocolEntries
	 */
	@XmlElementWrapper(name="protocolEntries")
    @XmlElement(name="protocolEntry")
	public List<ProtocolEntry> getProtocolEntries() {
		return protocolEntries;
	}

	
	/**
	 * @param protocolEntries the protocolEntries to set
	 */
	public void setProtocolEntries(List<ProtocolEntry> protocolEntries) {
		this.protocolEntries = protocolEntries;
	}
}
