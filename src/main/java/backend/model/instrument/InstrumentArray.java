package backend.model.instrument;

import java.util.ArrayList;
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
	 * Default constructor.
	 */
	public InstrumentArray() {
		this.instruments = new ArrayList<>();
	}

	
	/**
	 * @return the instruments
	 */
	@XmlElementWrapper(name="instruments")
    @XmlElement(name="instrument")
	public List<Instrument> getInstruments() {
		return instruments;
	}

	
	/**
	 * @param instruments the instruments to set
	 */
	public void setInstruments(List<Instrument> instruments) {
		this.instruments = instruments;
	}
	
	
	/**
	 * Gets the Instrument with the given ID.
	 * 
	 * @param id The Instrument with the given ID.
	 */
	public Instrument getInstrumentById(final Integer id) {
		for(Instrument instrument:this.instruments) {
			if(instrument.getId() == id)
				return instrument;
		}
		
		return null;
	}
}
