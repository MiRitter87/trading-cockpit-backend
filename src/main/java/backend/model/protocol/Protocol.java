package backend.model.protocol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
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
	 * The percentage of protocol entries that constitute confirmations.
	 */
	private int confirmationPercentage;
	
	/**
	 * The percentage of protocol entries that constitute uncertainty.
	 */
	private int uncertainPercentage;
	
	/**
	 * The percentage of protocol entries that constitute violations.
	 */
	private int violationPercentage;
	
	
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
	
	
	/**
	 * @return the confirmationPercentage
	 */
	public int getConfirmationPercentage() {
		return confirmationPercentage;
	}


	/**
	 * @param confirmationPercentage the confirmationPercentage to set
	 */
	public void setConfirmationPercentage(int confirmationPercentage) {
		this.confirmationPercentage = confirmationPercentage;
	}


	/**
	 * @return the uncertainPercentage
	 */
	public int getUncertainPercentage() {
		return uncertainPercentage;
	}


	/**
	 * @param uncertainPercentage the uncertainPercentage to set
	 */
	public void setUncertainPercentage(int uncertainPercentage) {
		this.uncertainPercentage = uncertainPercentage;
	}


	/**
	 * @return the violationPercentage
	 */
	public int getViolationPercentage() {
		return violationPercentage;
	}


	/**
	 * @param violationPercentage the violationPercentage to set
	 */
	public void setViolationPercentage(int violationPercentage) {
		this.violationPercentage = violationPercentage;
	}


	/**
	 * Sorts the protocol entries by their date.
	 */
	public void sortEntriesByDate() {
		Collections.sort(this.protocolEntries, new ProtocolEntryDateComparator());
	}
	
	
	/**
	 * Calculates percentage values for confirmations, violations and uncertainties based on all protocol entries.
	 */
	public void calculatePercentages() {
		int numberOfConfirmations = 0, numberOfViolations = 0, numberOfUncertainties = 0;
		
		for(ProtocolEntry entry : this.protocolEntries) {
			if(entry.getCategory() == ProtocolEntryCategory.CONFIRMATION)
				numberOfConfirmations++;
			else if(entry.getCategory() == ProtocolEntryCategory.VIOLATION)
				numberOfViolations++;
			else if(entry.getCategory() == ProtocolEntryCategory.UNCERTAIN)
				numberOfUncertainties++;
		}
		
		this.confirmationPercentage = new BigDecimal(numberOfConfirmations).divide(
				new BigDecimal(this.protocolEntries.size()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();
		this.violationPercentage = new BigDecimal(numberOfViolations).divide(
				new BigDecimal(this.protocolEntries.size()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();
		this.uncertainPercentage = new BigDecimal(numberOfUncertainties).divide(
				new BigDecimal(this.protocolEntries.size()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();
	}
}
