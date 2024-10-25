package backend.model.scan;

import java.util.Set;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.NoItemsException;
import backend.model.instrument.Instrument;
import backend.model.list.List;
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
     * Checks if the attributes of this Scan are equal to the attributes of the other Scan.
     *
     * @param other The other Scan checked for equality.
     * @return true, if both objects are equal in their attributes.
     */
    public boolean areAttributesEqual(final Scan other) {
        if (this.scan.getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!this.scan.getId().equals(other.getId())) {
            return false;
        }
        if (this.scan.getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!this.scan.getName().equals(other.getName())) {
            return false;
        }
        if (this.scan.getDescription() == null) {
            if (other.getDescription() != null) {
                return false;
            }
        } else if (!this.scan.getDescription().equals(other.getDescription())) {
            return false;
        }
        if (this.scan.getExecutionStatus() != other.getExecutionStatus()) {
            return false;
        }
        if (this.scan.getCompletionStatus() != other.getCompletionStatus()) {
            return false;
        }
        if (this.scan.getProgress() == null) {
            if (other.getProgress() != null) {
                return false;
            }
        } else if (!this.scan.getProgress().equals(other.getProgress())) {
            return false;
        }
        if (this.scan.getLastScan() == null && other.getLastScan() != null) {
            return false;
        }
        if (this.scan.getLastScan() != null && other.getLastScan() == null) {
            return false;
        }
        if (this.scan.getLastScan() != null && other.getLastScan() != null) {
            if (this.scan.getLastScan().getTime() != other.getLastScan().getTime()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the referenced lists are equal.
     *
     * @param other The other scan for comparison.
     * @return true, if lists are equal; false otherwise.
     */
    public boolean areListsEqual(final Scan other) {
        if (this.scan.getLists() == null && other.getLists() != null) {
            return false;
        }

        if (this.scan.getLists() != null && other.getLists() == null) {
            return false;
        }

        if (this.scan.getLists().size() != other.getLists().size()) {
            return false;
        }

        for (List tempList : this.scan.getLists()) {
            List otherList = other.getListWithId(tempList.getId());

            if (otherList == null) {
                return false;
            }

            if (!tempList.equals(otherList)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the referenced incomplete instruments are equal.
     *
     * @param other The other scan for comparison.
     * @return true, if incomplete instruments are equal; false otherwise.
     */
    public boolean areIncompleteInstrumentsEqual(final Scan other) {
        if (this.scan.getIncompleteInstruments() == null && other.getIncompleteInstruments() != null) {
            return false;
        }

        if (this.scan.getIncompleteInstruments() != null && other.getIncompleteInstruments() == null) {
            return false;
        }

        if (this.scan.getIncompleteInstruments().size() != other.getIncompleteInstruments().size()) {
            return false;
        }

        for (Instrument tempInstrument : this.scan.getIncompleteInstruments()) {
            Instrument otherInstrument = other.getIncompleteInstrumentWithId(tempInstrument.getId());

            if (otherInstrument == null) {
                return false;
            }

            if (!tempInstrument.equals(otherInstrument)) {
                return false;
            }
        }

        return true;
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
