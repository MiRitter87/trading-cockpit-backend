package backend.model.protocol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import backend.tools.DateTools;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

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
    @XmlElementWrapper(name = "protocolEntries")
    @XmlElement(name = "protocolEntry")
    public List<ProtocolEntry> getProtocolEntries() {
        return protocolEntries;
    }

    /**
     * @param protocolEntries the protocolEntries to set
     */
    public void setProtocolEntries(final List<ProtocolEntry> protocolEntries) {
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
    public void setConfirmationPercentage(final int confirmationPercentage) {
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
    public void setUncertainPercentage(final int uncertainPercentage) {
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
    public void setViolationPercentage(final int violationPercentage) {
        this.violationPercentage = violationPercentage;
    }

    /**
     * Sorts the protocol entries by their date.
     */
    public void sortEntriesByDate() {
        Collections.sort(this.protocolEntries, new ProtocolEntryDateComparator());
    }

    /**
     * Determines all protocol entries of the given date. Intraday attributes (hours, minutes, seconds, ...) of the date
     * are not taken into account.
     *
     * @param date The date.
     * @return A List of protocol entries of the given date.
     */
    public List<ProtocolEntry> getEntriesOfDate(final Date date) {
        List<ProtocolEntry> entriesOfDate = new ArrayList<>();
        Date inputDate;
        Date entryDate;

        inputDate = DateTools.getDateWithoutIntradayAttributes(date);

        for (ProtocolEntry entry : this.protocolEntries) {
            entryDate = DateTools.getDateWithoutIntradayAttributes(entry.getDate());

            if (entryDate.getTime() == inputDate.getTime()) {
                entriesOfDate.add(entry);
            }
        }

        return entriesOfDate;
    }

    /**
     * Calculates percentage values for confirmations, violations and uncertainties based on all protocol entries.
     */
    public void calculatePercentages() {
        int numberOfConfirmations = 0;
        int numberOfViolations = 0;
        int numberOfUncertainties = 0;
        final int hundredPercent = 100;

        for (ProtocolEntry entry : this.protocolEntries) {
            if (entry.getCategory() == ProtocolEntryCategory.CONFIRMATION) {
                numberOfConfirmations++;
            } else if (entry.getCategory() == ProtocolEntryCategory.VIOLATION) {
                numberOfViolations++;
            } else if (entry.getCategory() == ProtocolEntryCategory.UNCERTAIN) {
                numberOfUncertainties++;
            }
        }

        this.confirmationPercentage = new BigDecimal(numberOfConfirmations)
                .divide(new BigDecimal(this.protocolEntries.size()), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(hundredPercent)).intValue();
        this.violationPercentage = new BigDecimal(numberOfViolations)
                .divide(new BigDecimal(this.protocolEntries.size()), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(hundredPercent)).intValue();
        this.uncertainPercentage = new BigDecimal(numberOfUncertainties)
                .divide(new BigDecimal(this.protocolEntries.size()), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(hundredPercent)).intValue();
    }
}
