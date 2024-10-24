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
