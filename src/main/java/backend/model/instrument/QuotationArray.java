package backend.model.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import backend.tools.DateTools;

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
	 * Default constructor.
	 */
	public QuotationArray() {
		this.quotations = new ArrayList<>();
	}

	
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
	
	
	/**
	 * Gets the index of the Quotation with the given date.
	 * Intraday attributes of the date (hours, minutes, ...) are not taken into account for lookup of matching Quotation.
	 * If no Quotation exists on the given day, the index of the first Quotation coming afterwards is determined.
	 * 
	 * @param quotations A List of quotations.
	 * @param date The date.
	 * @return The index of the Quotation. -1, if no Quotation was found.
	 */
	public int getIndexOfQuotationWithDate(final Date date) {
		Quotation quotation;
		Date quotationDate, inputDate;
		int indexOfQuotation = -1;
		
		inputDate = DateTools.getDateWithoutIntradayAttributes(date);
		
		for(int i = 0; i < this.quotations.size(); i++) {
			quotation = this.quotations.get(i);
			quotationDate = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());
			
			if(inputDate.getTime() <= quotationDate.getTime())
				indexOfQuotation = i;
		}
		
		return indexOfQuotation;
	}
	
	
	/**
	 * Sorts all quotations by date.
	 */
	public void sortQuotationsByDate() {
		Collections.sort(this.quotations, new QuotationDateComparator());
	}
}
