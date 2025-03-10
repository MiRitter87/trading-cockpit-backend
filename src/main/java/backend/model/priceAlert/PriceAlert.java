package backend.model.priceAlert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import backend.model.Currency;
import backend.model.LocalizedException;
import backend.model.instrument.Instrument;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * An alert that a stock has reached a certain price at a stock exchange.
 *
 * @author Michael
 */
@Table(name = "PRICE_ALERT")
@Entity
@SequenceGenerator(name = "priceAlertSequence", initialValue = 1, allocationSize = 1)
public class PriceAlert {
    /**
     * The maximum PriceAlertType field length allowed.
     */
    private static final int MAX_TYPE_LENGTH = 20;

    /**
     * The maximum price allowed.
     */
    private static final int MAX_PRICE = 100000;

    /**
     * The maximum Currency field length allowed.
     */
    private static final int MAX_CURRENCY_LENGTH = 3;

    /**
     * The maximum e-mail address field length allowed.
     */
    public static final int MAX_MAIL_ADDRESS_LENGTH = 254;

    /**
     * The minimum e-mail address field length allowed.
     */
    public static final int MIN_MAIL_ADDRESS_LENGTH = 5;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priceAlertSequence")
    @Column(name = "PRICE_ALERT_ID")
    @Min(value = 1, message = "{priceAlert.id.min.message}")
    private Integer id;

    /**
     * The Instrument.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTRUMENT_ID")
    @NotNull(message = "{priceAlert.instrument.notNull.message}")
    private Instrument instrument;

    /**
     * The type of the price alert.
     */
    @Column(name = "ALERT_TYPE", length = MAX_TYPE_LENGTH)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{priceAlert.alertType.notNull.message}")
    private PriceAlertType alertType;

    /**
     * The price at which the alert is activated.
     */
    @Column(name = "PRICE")
    @NotNull(message = "{priceAlert.price.notNull.message}")
    @DecimalMin(value = "0.01", inclusive = true, message = "{priceAlert.price.decimalMin.message}")
    @Max(value = MAX_PRICE, message = "{priceAlert.price.max.message}")
    private BigDecimal price;

    /**
     * The currency.
     */
    @Column(name = "CURRENCY", length = MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{priceAlert.currency.notNull.message}")
    private Currency currency;

    /**
     * The distance between the current price and the trigger level in percent.
     */
    @Column(name = "TRIGGER_DISTANCE_PERCENT")
    private float triggerDistancePercent;

    /**
     * The time at which the alert has been triggered.
     */
    @Column(name = "TRIGGER_TIME")
    private Date triggerTime;

    /**
     * The time at which the user as confirmed the alert.
     */
    @Column(name = "CONFIRMATION_TIME")
    private Date confirmationTime;

    /**
     * The time of the last stock quote query.
     */
    @Column(name = "LAST_STOCK_QUOTE_TIME")
    private Date lastStockQuoteTime;

    /**
     * Controls if E-Mail has to be sent if the alert is triggered.
     */
    @Column(name = "SEND_MAIL")
    private boolean sendMail;

    /**
     * The E-Mail address to which a notification is sent, if the alert is triggered.
     */
    @Column(name = "ALERT_MAIL_ADDRESS", length = MAX_MAIL_ADDRESS_LENGTH)
    private String alertMailAddress;

    /**
     * The time at which the E-Mail notification has been sent.
     */
    @Column(name = "MAIL_TRANSMISSION_TIME")
    private Date mailTransmissionTime;

    /**
     * Default constructor.
     */
    public PriceAlert() {
        this.setSendMail(false);
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
     * @return the alertType
     */
    public PriceAlertType getAlertType() {
        return alertType;
    }

    /**
     * @param alertType the alertType to set
     */
    public void setAlertType(final PriceAlertType alertType) {
        this.alertType = alertType;
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(final BigDecimal price) {
        this.price = price;
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
     * @return the triggerDistancePercent
     */
    public float getTriggerDistancePercent() {
        return triggerDistancePercent;
    }

    /**
     * @param triggerDistancePercent the triggerDistancePercent to set
     */
    public void setTriggerDistancePercent(final float triggerDistancePercent) {
        this.triggerDistancePercent = triggerDistancePercent;
    }

    /**
     * @return the triggerTime
     */
    public Date getTriggerTime() {
        return triggerTime;
    }

    /**
     * @param triggerTime the triggerTime to set
     */
    public void setTriggerTime(final Date triggerTime) {
        this.triggerTime = triggerTime;
    }

    /**
     * @return the confirmationTime
     */
    public Date getConfirmationTime() {
        return confirmationTime;
    }

    /**
     * @param confirmationTime the confirmationTime to set
     */
    public void setConfirmationTime(final Date confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    /**
     * @return the lastStockQuoteTime
     */
    public Date getLastStockQuoteTime() {
        return lastStockQuoteTime;
    }

    /**
     * @param lastStockQuoteTime the lastStockQuoteTime to set
     */
    public void setLastStockQuoteTime(final Date lastStockQuoteTime) {
        this.lastStockQuoteTime = lastStockQuoteTime;
    }

    /**
     * @return the sendMail
     */
    public boolean isSendMail() {
        return sendMail;
    }

    /**
     * @param sendMail the sendMail to set
     */
    public void setSendMail(final boolean sendMail) {
        this.sendMail = sendMail;
    }

    /**
     * @return the alertMailAddress
     */
    public String getAlertMailAddress() {
        return alertMailAddress;
    }

    /**
     * @param alertMailAddress the alertMailAddress to set
     */
    public void setAlertMailAddress(final String alertMailAddress) {
        this.alertMailAddress = alertMailAddress;
    }

    /**
     * @return the mailTransmissionTime
     */
    public Date getMailTransmissionTime() {
        return mailTransmissionTime;
    }

    /**
     * @param mailTransmissionTime the mailTransmissionTime to set
     */
    public void setMailTransmissionTime(final Date mailTransmissionTime) {
        this.mailTransmissionTime = mailTransmissionTime;
    }

    /**
     * Calculates the hashCode of a PriceAlert.
     */
    @Override
    public int hashCode() {
        return Objects.hash(alertMailAddress, alertType, confirmationTime, currency, id, instrument, lastStockQuoteTime,
                mailTransmissionTime, price, sendMail, triggerDistancePercent, triggerTime);
    }

    /**
     * Indicates whether some other PriceAlert is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        PriceAlertValidator validator = new PriceAlertValidator(this);

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PriceAlert other = (PriceAlert) obj;

        if (!validator.areTimestampsEqual(other)) {
            return false;
        }

        if (!validator.areBasicAttributesEqual(other)) {
            return false;
        }

        if (Float.floatToIntBits(triggerDistancePercent) != Float.floatToIntBits(other.triggerDistancePercent)) {
            return false;
        }
        if (sendMail != other.sendMail) {
            return false;
        }
        if (alertMailAddress == null) {
            if (other.alertMailAddress != null) {
                return false;
            }
        } else if (!alertMailAddress.equals(other.alertMailAddress)) {
            return false;
        }

        return true;
    }

    /**
     * Validates the price alert.
     *
     * @throws LocalizedException A general exception containing a localized message.
     * @throws Exception          In case a general validation error occurred.
     */
    public void validate() throws LocalizedException, Exception {
        PriceAlertValidator validator = new PriceAlertValidator(this);
        validator.validate();
    }
}
