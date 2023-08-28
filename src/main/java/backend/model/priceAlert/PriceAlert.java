package backend.model.priceAlert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.Currency;
import backend.model.LocalizedException;
import backend.model.instrument.Instrument;

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
    private static final int MAX_MAIL_ADDRESS_LENGTH = 254;

    /**
     * The mimimum e-mail address field length allowed.
     */
    private static final int MIN_MAIL_ADDRESS_LENGTH = 5;

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
    @OneToOne
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
        if (alertType != other.alertType) {
            return false;
        }
        if (confirmationTime == null && other.confirmationTime != null) {
            return false;
        }
        if (confirmationTime != null && other.confirmationTime == null) {
            return false;
        }
        if (confirmationTime != null && other.confirmationTime != null) {
            if (confirmationTime.getTime() != other.confirmationTime.getTime()) {
                return false;
            }
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (price == null) {
            if (other.price != null) {
                return false;
            }
        } else if (price.compareTo(other.price) != 0) {
            return false;
        }
        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (currency.compareTo(other.currency) != 0) {
            return false;
        }
        if (instrument == null) {
            if (other.instrument != null) {
                return false;
            }
        } else if (!instrument.equals(other.instrument)) {
            return false;
        }
        if (triggerTime == null && other.triggerTime != null) {
            return false;
        }
        if (triggerTime != null && other.triggerTime == null) {
            return false;
        }
        if (triggerTime != null && other.triggerTime != null) {
            if (triggerTime.getTime() != other.triggerTime.getTime()) {
                return false;
            }
        }
        if (lastStockQuoteTime == null && other.lastStockQuoteTime != null) {
            return false;
        }
        if (lastStockQuoteTime != null && other.lastStockQuoteTime == null) {
            return false;
        }
        if (lastStockQuoteTime != null && other.lastStockQuoteTime != null) {
            if (lastStockQuoteTime.getTime() != other.lastStockQuoteTime.getTime()) {
                return false;
            }
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
        if (mailTransmissionTime == null && other.mailTransmissionTime != null) {
            return false;
        }
        if (mailTransmissionTime != null && other.mailTransmissionTime == null) {
            return false;
        }
        if (mailTransmissionTime != null && other.mailTransmissionTime != null) {
            if (mailTransmissionTime.getTime() != other.mailTransmissionTime.getTime()) {
                return false;
            }
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
        this.validateAnnotations();
        this.validateAdditionalCharacteristics();
    }

    /**
     * Validates the price alert according to the annotations of the validation framework.
     *
     * @exception Exception In case the validation failed.
     */
    private void validateAnnotations() throws Exception {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                .constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<PriceAlert>> violations = validator.validate(this);

        for (ConstraintViolation<PriceAlert> violation : violations) {
            throw new Exception(violation.getMessage());
        }
    }

    /**
     * Validates additional characteristics of the PriceAlert besides annotations.
     *
     * @throws LocalizedException A general exception containing a localized message.
     */
    private void validateAdditionalCharacteristics() throws LocalizedException {
        this.validateAlertMailAddress();
    }

    /**
     * Validates the alertMailAddress attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateAlertMailAddress() throws LocalizedException {
        if (this.sendMail && this.alertMailAddress == null) {
            throw new LocalizedException("priceAlert.alertMailAddress.notNull.message");
        }

        if (this.sendMail && this.alertMailAddress != null && (this.alertMailAddress.length() < MIN_MAIL_ADDRESS_LENGTH
                || this.alertMailAddress.length() > MAX_MAIL_ADDRESS_LENGTH)) {

            throw new LocalizedException("priceAlert.alertMailAddress.size.message", this.alertMailAddress.length(),
                    MIN_MAIL_ADDRESS_LENGTH, MAX_MAIL_ADDRESS_LENGTH);
        }
    }
}
