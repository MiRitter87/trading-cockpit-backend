package backend.model.priceAlert;

import java.util.Set;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.LocalizedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Performs attribute validations of the PriceAlert model.
 *
 * @author Michael
 */
public class PriceAlertValidator {
    /**
     * The PriceAlert to be validated.
     */
    private PriceAlert priceAlert;

    /**
     * Initializes the PriceAlertValidator.
     *
     * @param priceAlert The PriceAlert to be validated.
     */
    public PriceAlertValidator(final PriceAlert priceAlert) {
        this.priceAlert = priceAlert;
    }

    /**
     * Checks if the timestamps of this PriceAlert are equal to the other PriceAlert.
     *
     * @param other The other PriceAlert.
     * @return true, if timestamps are equal; false, if not.
     */
    public boolean areTimestampsEqual(final PriceAlert other) {
        if (this.priceAlert.getConfirmationTime() == null && other.getConfirmationTime() != null) {
            return false;
        }
        if (this.priceAlert.getConfirmationTime() != null && other.getConfirmationTime() == null) {
            return false;
        }
        if (this.priceAlert.getConfirmationTime() != null && other.getConfirmationTime() != null) {
            if (this.priceAlert.getConfirmationTime().getTime() != other.getConfirmationTime().getTime()) {
                return false;
            }
        }
        if (this.priceAlert.getTriggerTime() == null && other.getTriggerTime() != null) {
            return false;
        }
        if (this.priceAlert.getTriggerTime() != null && other.getTriggerTime() == null) {
            return false;
        }
        if (this.priceAlert.getTriggerTime() != null && other.getTriggerTime() != null) {
            if (this.priceAlert.getTriggerTime().getTime() != other.getTriggerTime().getTime()) {
                return false;
            }
        }
        if (this.priceAlert.getLastStockQuoteTime() == null && other.getLastStockQuoteTime() != null) {
            return false;
        }
        if (this.priceAlert.getLastStockQuoteTime() != null && other.getLastStockQuoteTime() == null) {
            return false;
        }
        if (this.priceAlert.getLastStockQuoteTime() != null && other.getLastStockQuoteTime() != null) {
            if (this.priceAlert.getLastStockQuoteTime().getTime() != other.getLastStockQuoteTime().getTime()) {
                return false;
            }
        }
        if (this.priceAlert.getMailTransmissionTime() == null && other.getMailTransmissionTime() != null) {
            return false;
        }
        if (this.priceAlert.getMailTransmissionTime() != null && other.getMailTransmissionTime() == null) {
            return false;
        }
        if (this.priceAlert.getMailTransmissionTime() != null && other.getMailTransmissionTime() != null) {
            if (this.priceAlert.getMailTransmissionTime().getTime() != other.getMailTransmissionTime().getTime()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if basic attributes of this PriceAlert are equal to the other PriceAlert.
     *
     * @param other The other PriceAlert.
     * @return true, if basic attribtues are equal; false, if not.
     */
    public boolean areBasicAttributesEqual(final PriceAlert other) {
        if (this.priceAlert.getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!this.priceAlert.getId().equals(other.getId())) {
            return false;
        }

        if (this.priceAlert.getAlertType() != other.getAlertType()) {
            return false;
        }

        if (this.priceAlert.getPrice() == null) {
            if (other.getPrice() != null) {
                return false;
            }
        } else if (this.priceAlert.getPrice().compareTo(other.getPrice()) != 0) {
            return false;
        }

        if (this.priceAlert.getCurrency() == null) {
            if (other.getCurrency() != null) {
                return false;
            }
        } else if (this.priceAlert.getCurrency().compareTo(other.getCurrency()) != 0) {
            return false;
        }

        if (this.priceAlert.getInstrument() == null) {
            if (other.getInstrument() != null) {
                return false;
            }
        } else if (!this.priceAlert.getInstrument().equals(other.getInstrument())) {
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
        Set<ConstraintViolation<PriceAlert>> violations = validator.validate(this.priceAlert);

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
        if (this.priceAlert.isSendMail() && this.priceAlert.getAlertMailAddress() == null) {
            throw new LocalizedException("priceAlert.alertMailAddress.notNull.message");
        }

        if (this.priceAlert.isSendMail() && this.priceAlert.getAlertMailAddress() != null
                && (this.priceAlert.getAlertMailAddress().length() < PriceAlert.MIN_MAIL_ADDRESS_LENGTH
                        || this.priceAlert.getAlertMailAddress().length() > PriceAlert.MAX_MAIL_ADDRESS_LENGTH)) {

            throw new LocalizedException("priceAlert.alertMailAddress.size.message",
                    this.priceAlert.getAlertMailAddress().length(), PriceAlert.MIN_MAIL_ADDRESS_LENGTH,
                    PriceAlert.MAX_MAIL_ADDRESS_LENGTH);
        }
    }
}
