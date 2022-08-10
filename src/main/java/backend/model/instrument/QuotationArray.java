package backend.model.instrument;

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
}
