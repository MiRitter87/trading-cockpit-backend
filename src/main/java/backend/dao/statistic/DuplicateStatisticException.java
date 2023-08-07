package backend.dao.statistic;

import java.util.Date;

import backend.model.instrument.InstrumentType;

/**
 * Exception that indicates that a Statistic already exists.
 *
 * @author Michael
 */
public class DuplicateStatisticException extends Exception {
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = 1021017028262320197L;

    /**
     * The InstrumentType of the duplicate.
     */
    private InstrumentType instrumentType;

    /**
     * The date of the duplicate.
     */
    private Date date;

    /**
     * Default constructor.
     */
    public DuplicateStatisticException() {

    }

    /**
     * Initializes the DuplicateStatisticException.
     *
     * @param instrumentType The InstrumentType.
     * @param date           The Date.
     */
    public DuplicateStatisticException(final InstrumentType instrumentType, final Date date) {
        this.instrumentType = instrumentType;
        this.date = date;
    }

    /**
     * @return the instrumentType
     */
    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    /**
     * @param instrumentType the instrumentType to set
     */
    public void setInstrumentType(final InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }
}
