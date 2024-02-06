package backend.model.instrument;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import backend.model.Currency;

/**
 * Quotation data of an Instrument.
 *
 * @author Michael
 */
@Table(name = "QUOTATION")
@Entity
@SequenceGenerator(name = "quotationSequence", initialValue = 1, allocationSize = 1)
public class Quotation {
    /**
     * The precision of a price column.
     */
    private static final int PRICE_PRECISION = 10;

    /**
     * The scale of a price column.
     */
    private static final int PRICE_SCALE = 3;

    /**
     * The maximum currency field length allowed.
     */
    private static final int MAX_CURRENCY_LENGTH = 3;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quotationSequence")
    @Column(name = "QUOTATION_ID")
    private Integer id;

    /**
     * The date.
     */
    @Column(name = "DATE")
    private Date date;

    /**
     * The opening price.
     */
    @Column(name = "OPEN", precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal open;

    /**
     * The high price.
     */
    @Column(name = "HIGH", precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal high;

    /**
     * The low price.
     */
    @Column(name = "LOW", precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal low;

    /**
     * The closing price.
     */
    @Column(name = "CLOSE", precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal close;

    /**
     * The currency.
     */
    @Column(name = "CURRENCY", length = MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * The number of instruments traded.
     */
    @Column(name = "VOLUME")
    private long volume;

    /**
     * The Instrument this Quotation belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTRUMENT_ID")
    private Instrument instrument;

    /**
     * Indicator data.
     */
    @OneToOne(targetEntity = Indicator.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "INDICATOR_ID")
    private Indicator indicator;

    /**
     * Default constructor.
     */
    public Quotation() {

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
     * @return the open
     */
    public BigDecimal getOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(final BigDecimal open) {
        this.open = open;
    }

    /**
     * @return the high
     */
    public BigDecimal getHigh() {
        return high;
    }

    /**
     * @param high the high to set
     */
    public void setHigh(final BigDecimal high) {
        this.high = high;
    }

    /**
     * @return the low
     */
    public BigDecimal getLow() {
        return low;
    }

    /**
     * @param low the low to set
     */
    public void setLow(final BigDecimal low) {
        this.low = low;
    }

    /**
     * @return the closing price
     */
    public BigDecimal getClose() {
        return close;
    }

    /**
     * @param close the closing price to set
     */
    public void setClose(final BigDecimal close) {
        this.close = close;
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the volume
     */
    public long getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(final long volume) {
        this.volume = volume;
    }

    /**
     * @return the instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * @param instrument the instrument to set
     */
    public void setInstrument(final Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * @return the indicator
     */
    public Indicator getIndicator() {
        return indicator;
    }

    /**
     * @param indicator the indicator to set
     */
    public void setIndicator(final Indicator indicator) {
        this.indicator = indicator;

        if (this.indicator != null) {
            this.indicator.setId(this.id);

            if (this.indicator.getRelativeStrengthData() != null) {
                this.indicator.getRelativeStrengthData().setId(this.id);
            }

            if (this.indicator.getMovingAverageData() != null) {
                this.indicator.getMovingAverageData().setId(this.id);
            }
        }
    }

    /**
     * Calculates the hashCode of a Quotation.
     */
    @Override
    public int hashCode() {
        return Objects.hash(currency, date, id, indicator, instrument, open, high, low, close, volume);
    }

    /**
     * Indicates whether some other Quotation is "equal to" this one.
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
        Quotation other = (Quotation) obj;

        if (open == null && other.open != null) {
            return false;
        }
        if (open != null && other.open == null) {
            return false;
        }
        if (open != null && other.open != null && open.compareTo(other.open) != 0) {
            return false;
        }

        if (high == null && other.high != null) {
            return false;
        }
        if (high != null && other.high == null) {
            return false;
        }
        if (high != null && other.high != null && high.compareTo(other.high) != 0) {
            return false;
        }

        if (low == null && other.low != null) {
            return false;
        }
        if (low != null && other.low == null) {
            return false;
        }
        if (low != null && other.low != null && low.compareTo(other.low) != 0) {
            return false;
        }

        if (close == null && other.close != null) {
            return false;
        }
        if (close != null && other.close == null) {
            return false;
        }
        if (close != null && other.close != null && close.compareTo(other.close) != 0) {
            return false;
        }

        return currency == other.currency && Objects.equals(date, other.date) && Objects.equals(id, other.id)
                && Objects.equals(indicator, other.indicator) && Objects.equals(instrument, other.instrument)
                && volume == other.volume;
    }
}
