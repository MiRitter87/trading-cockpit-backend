package backend.model.scan;

import java.util.Set;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.NoItemsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Performs attribute validations of the Scan model.
 *
 * @author Michael
 */
public class ScanValidator {
    /**
     * The Scan to be validated.
     */
    private Scan scan;

    /**
     * Initializes the ScanValidator.
     *
     * @param scan The Scan to be validated.
     */
    public ScanValidator(final Scan scan) {
        this.scan = scan;
    }

    /**
     * Validates the scan.
     *
     * @throws Exception In case a general validation error occurred.
     */
    public void validate() throws Exception {
        this.validateAnnotations();
        this.validateAdditionalCharacteristics();
    }

    /**
     * Validates the scan according to the annotations of the validation framework.
     *
     * @exception Exception In case the validation failed.
     */
    private void validateAnnotations() throws Exception {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                .constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Scan>> violations = validator.validate(this.scan);

        for (ConstraintViolation<Scan> violation : violations) {
            throw new Exception(violation.getMessage());
        }
    }

    /**
     * Validates additional characteristics of the scan besides annotations.
     *
     * @throws NoItemsException Indicates that the scan has no lists defined.
     */
    private void validateAdditionalCharacteristics() throws NoItemsException {
        this.validateListsDefined();
    }

    /**
     * Checks if lists are defined.
     *
     * @throws NoItemsException If no lists are defined.
     */
    private void validateListsDefined() throws NoItemsException {
        if (this.scan.getLists() == null || this.scan.getLists().size() == 0) {
            throw new NoItemsException();
        }
    }
}
