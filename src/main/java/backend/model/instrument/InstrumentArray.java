package backend.model.instrument;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of instruments.
 * 
 * @author Michael
 */
public class InstrumentArray {
	/**
	 * A list of instruments.
	 */
	private List<Instrument> instruments;

	
	/**
	 * @return the instruments
	 */
	@XmlElementWrapper(name="instruments")
    @XmlElement(name="isntrument")
	public List<Instrument> getInstruments() {
		return instruments;
	}

	
	/**
	 * @param instruments the instruments to set
	 */
	public void setInstruments(List<Instrument> instruments) {
		this.instruments = instruments;
	}
}
