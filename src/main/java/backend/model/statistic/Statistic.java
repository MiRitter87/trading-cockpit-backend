package backend.model.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import backend.model.instrument.InstrumentType;

/**
 * Statistical data of all instruments of an InstrumentType at a certain point in time.
 *
 * @author Michael
 */
@Table(name = "STATISTIC")
@Entity
@SequenceGenerator(name = "statisticSequence", initialValue = 1, allocationSize = 1)
public class Statistic {
    /**
     * The maximum InstrumentType field length allowed.
     */
    private static final int MAX_TYPE_LENGTH = 10;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statisticSequence")
    @Column(name = "STATISTIC_ID")
    private Integer id;

    /**
     * The date.
     */
    @Column(name = "DATE")
    private Date date;

    /**
     * The InstrumentType.
     */
    @Column(name = "INSTRUMENT_TYPE", length = MAX_TYPE_LENGTH)
    @Enumerated(EnumType.STRING)
    private InstrumentType instrumentType;

    /**
     * The total number of instruments the Statistic is based on.
     */
    @Column(name = "NUMBER_OF_INSTRUMENTS")
    private int numberOfInstruments;

    /**
     * The number of instruments which advanced since the last data point.
     */
    @Column(name = "NUMBER_ADVANCE")
    private int numberAdvance;

    /**
     * The number of instruments which declined since the last data point.
     */
    @Column(name = "NUMBER_DECLINE")
    private int numberDecline;

    /**
     * The number of advancing minus declining instruments since the last data point.
     */
    @Column(name = "ADVANCE_DECLINE_NUMBER")
    private int advanceDeclineNumber;

    /**
     * The number of instruments which are trading above the 50-day Simple Moving Average.
     */
    @Column(name = "NUMBER_ABOVE_SMA50")
    private int numberAboveSma50;

    /**
     * The number of instruments which are trading at or below the 50-day Simple Moving Average.
     */
    @Column(name = "NUMBER_AT_OR_BELOW_SMA50")
    private int numberAtOrBelowSma50;

    /**
     * The percentage of instruments which are trading above the 50-day Simple Moving Average.
     */
    @Column(name = "PERCENT_ABOVE_SMA50")
    private float percentAboveSma50;

    /**
     * The number of the "Ritter Market Trend" indicator.
     */
    @Column(name = "NUMBER_RITTER_MARKET_TREND")
    private int numberRitterMarketTrend;

    /**
     * Default constructor.
     */
    public Statistic() {

    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final Integer id) {
        this.id = id;
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
     * @return the numberAdvance
     */
    public int getNumberAdvance() {
        return numberAdvance;
    }

    /**
     * @param numberAdvance the numberAdvance to set
     */
    public void setNumberAdvance(final int numberAdvance) {
        this.numberAdvance = numberAdvance;

        this.advanceDeclineNumber = this.numberAdvance - this.numberDecline;
    }

    /**
     * @return the numberDecline
     */
    public int getNumberDecline() {
        return numberDecline;
    }

    /**
     * @param numberDecline the numberDecline to set
     */
    public void setNumberDecline(final int numberDecline) {
        this.numberDecline = numberDecline;

        this.advanceDeclineNumber = this.numberAdvance - this.numberDecline;
    }

    /**
     * @return the advanceDeclineNumber
     */
    public int getAdvanceDeclineNumber() {
        return advanceDeclineNumber;
    }

    /**
     * @return the numberAboveSma50
     */
    public int getNumberAboveSma50() {
        return numberAboveSma50;
    }

    /**
     * @param numberAboveSma50 the numberAboveSma50 to set
     */
    public void setNumberAboveSma50(final int numberAboveSma50) {
        this.numberAboveSma50 = numberAboveSma50;
        this.updatePercentAboveSma50();
    }

    /**
     * @return the numberAtOrBelowSma50
     */
    public int getNumberAtOrBelowSma50() {
        return numberAtOrBelowSma50;
    }

    /**
     * @param numberAtOrBelowSma50 the numberAtOrBelowSma50 to set
     */
    public void setNumberAtOrBelowSma50(final int numberAtOrBelowSma50) {
        this.numberAtOrBelowSma50 = numberAtOrBelowSma50;
        this.updatePercentAboveSma50();
    }

    /**
     * @return the percentAboveSma50
     */
    public float getPercentAboveSma50() {
        return percentAboveSma50;
    }

    /**
     * @return the numberRitterMarketTrend
     */
    public int getNumberRitterMarketTrend() {
        return numberRitterMarketTrend;
    }

    /**
     * @param numberRitterMarketTrend the numberRitterMarketTrend to set
     */
    public void setNumberRitterMarketTrend(final int numberRitterMarketTrend) {
        this.numberRitterMarketTrend = numberRitterMarketTrend;
    }

    /**
     * @return the numberOfInstruments
     */
    public int getNumberOfInstruments() {
        return numberOfInstruments;
    }

    /**
     * @param numberOfInstruments the numberOfInstruments to set
     */
    public void setNumberOfInstruments(final int numberOfInstruments) {
        this.numberOfInstruments = numberOfInstruments;
    }

    /**
     * Updates the percentage above SMA(50).
     */
    private void updatePercentAboveSma50() {
        BigDecimal pctAboveSma50;
        BigDecimal numAboveSma50;
        BigDecimal totalNumber;
        final int hundredPercent = 100;

        if ((this.numberAboveSma50 + this.numberAtOrBelowSma50) != 0) {
            numAboveSma50 = new BigDecimal(this.numberAboveSma50);
            totalNumber = new BigDecimal(this.numberAboveSma50 + this.numberAtOrBelowSma50);

            pctAboveSma50 = numAboveSma50.multiply(new BigDecimal(hundredPercent)).divide(totalNumber, 0,
                    RoundingMode.HALF_UP);
            this.percentAboveSma50 = pctAboveSma50.floatValue();
        }
    }

    /**
     * Calculates the hashCode of a Statistic.
     */
    @Override
    public int hashCode() {
        return Objects.hash(advanceDeclineNumber, date, id, instrumentType, numberAboveSma50, numberAdvance,
                numberAtOrBelowSma50, numberDecline, numberOfInstruments, numberRitterMarketTrend, percentAboveSma50);
    }

    /**
     * Indicates whether some other Statistic is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Statistic other = (Statistic) obj;
        if (date == null && other.date != null) {
            return false;
        }
        if (date != null && other.date == null) {
            return false;
        }
        if (date != null && other.date != null) {
            if (date.getTime() != other.date.getTime()) {
                return false;
            }
        }

        return advanceDeclineNumber == other.advanceDeclineNumber && Objects.equals(id, other.id)
                && instrumentType == other.instrumentType && numberAboveSma50 == other.numberAboveSma50
                && numberAdvance == other.numberAdvance && numberAtOrBelowSma50 == other.numberAtOrBelowSma50
                && numberDecline == other.numberDecline && numberOfInstruments == other.numberOfInstruments
                && numberRitterMarketTrend == other.numberRitterMarketTrend
                && Float.floatToIntBits(percentAboveSma50) == Float.floatToIntBits(other.percentAboveSma50);
    }
}
