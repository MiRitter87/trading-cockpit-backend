package backend.model.instrument;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of quotations.
 * 
 * @author Michael
 */
public class QuotationArray {
	/**
	 * A list of quotations.
	 */
	private List<Quotation> quotations;

	
	/**
	 * @return the quotations
	 */
	@XmlElementWrapper(name="quotations")
    @XmlElement(name="quotation")
	public List<Quotation> getQuotations() {
		return quotations;
	}

	
	/**
	 * @param quotations the quotations to set
	 */
	public void setQuotations(List<Quotation> quotations) {
		this.quotations = quotations;
	}
	
	
	/**
	 * Returns all quotations of the Instrument with the given Id.
	 * 
	 * @param instrumentId The instrument ID.
	 * @return All quotations of the Instrument with the given ID.
	 */
	public List<Quotation> getQuotationsByInstrumentId(final Integer instrumentId) {
		List<Quotation> quotationsOfInstrument = new ArrayList<>();
		
		for(Quotation quotation : this.quotations) {
			if(quotation.getInstrument() != null && quotation.getInstrument().getId().equals(instrumentId))
				quotationsOfInstrument.add(quotation);
		}
		
		return quotationsOfInstrument;
	}
}
