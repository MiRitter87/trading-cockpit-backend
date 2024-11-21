package backend.model.instrument;

import java.util.Set;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.LocalizedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Performs attribute validations of the Instrument model.
 *
 * @author Michael
 */
public class InstrumentValidator {
    /**
     * The Instrument to be validated.
     */
    private Instrument instrument;

    /**
     * Initializes the InstrumentValidator.
     *
     * @param instrument The Instrument to be validated.
     */
    public InstrumentValidator(final Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Checks if the attributes of this Instrument are equal to the attributes of the other instrument.
     *
     * @param otherInstrument The other Instrument checked for equality.
     * @return true, if both objects are equal in their attributes.
     */
    public boolean areAttributesEqual(final Instrument otherInstrument) {
        if (this.instrument.getId() == null) {
            if (otherInstrument.getId() != null) {
                return false;
            }
        } else if (!this.instrument.getId().equals(otherInstrument.getId())) {
            return false;
        }
        if (this.instrument.getName() == null) {
            if (otherInstrument.getName() != null) {
                return false;
            }
        } else if (!this.instrument.getName().equals(otherInstrument.getName())) {
            return false;
        }
        if (this.instrument.getStockExchange() != otherInstrument.getStockExchange()) {
            return false;
        }
        if (this.instrument.getSymbol() == null) {
            if (otherInstrument.getSymbol() != null) {
                return false;
            }
        } else if (!instrument.getSymbol().equals(otherInstrument.getSymbol())) {
            return false;
        }
        if (this.instrument.getType() != otherInstrument.getType()) {
            return false;
        }
        if (this.instrument.getInvestingId() == null) {
            if (otherInstrument.getInvestingId() != null) {
                return false;
            }
        } else if (!this.instrument.getInvestingId().equals(otherInstrument.getInvestingId())) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the references of this Instrument are equal to the attributes of the other instrument.
     *
     * @param otherInstrument The other Instrument checked for equality.
     * @return true, if both objects are equal in their references.
     */
    public boolean areReferencesEqual(final Instrument otherInstrument) {
        if (this.instrument.getSector() == null && otherInstrument.getSector() != null) {
            return false;
        } else if (this.instrument.getSector() != null && otherInstrument.getSector() == null) {
            return false;
        } else if (this.instrument.getSector() != null && otherInstrument.getSector() != null
                && !this.instrument.getSector().getId().equals(otherInstrument.getSector().getId())) {
            return false;
        }
        if (this.instrument.getIndustryGroup() == null && otherInstrument.getIndustryGroup() != null) {
            return false;
        } else if (this.instrument.getIndustryGroup() != null && otherInstrument.getIndustryGroup() == null) {
            return false;
        } else if (this.instrument.getIndustryGroup() != null && otherInstrument.getIndustryGroup() != null
                && !this.instrument.getIndustryGroup().getId().equals(otherInstrument.getIndustryGroup().getId())) {
            return false;
        }
        if (this.instrument.getDividend() == null && otherInstrument.getDividend() != null) {
            return false;
        } else if (this.instrument.getDividend() != null && otherInstrument.getDividend() == null) {
            return false;
        } else if (this.instrument.getDividend() != null && otherInstrument.getDividend() != null
                && !this.instrument.getDividend().getId().equals(otherInstrument.getDividend().getId())) {
            return false;
        }
        if (this.instrument.getDivisor() == null && otherInstrument.getDivisor() != null) {
            return false;
        } else if (this.instrument.getDivisor() != null && otherInstrument.getDivisor() == null) {
            return false;
        } else if (this.instrument.getDivisor() != null && otherInstrument.getDivisor() != null
                && !this.instrument.getDivisor().getId().equals(otherInstrument.getDivisor().getId())) {
            return false;
        }
        if (this.instrument.getDataSourceList() == null && otherInstrument.getDataSourceList() != null) {
            return false;
        } else if (this.instrument.getDataSourceList() != null && otherInstrument.getDataSourceList() == null) {
            return false;
        } else if (this.instrument.getDataSourceList() != null && otherInstrument.getDataSourceList() != null
                && !this.instrument.getDataSourceList().getId().equals(otherInstrument.getDataSourceList().getId())) {
            return false;
        }

        return true;
    }

    /**
     * Validates the instrument.
     *
     * @throws LocalizedException A general exception containing a localized message.
     * @throws Exception          In case a general validation error occurred.
     */
    public void validate() throws LocalizedException, Exception {
        this.validateAnnotations();
        this.validateAdditionalCharacteristics();
    }

    /**
     * Validates the instrument according to the annotations of the validation framework.
     *
     * @exception Exception In case the validation failed.
     */
    private void validateAnnotations() throws Exception {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                .constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Instrument>> violations = validator.validate(this.instrument);

        for (ConstraintViolation<Instrument> violation : violations) {
            throw new Exception(violation.getMessage());
        }
    }

    /**
     * Validates additional characteristics of the Instrument besides annotations.
     *
     * @throws LocalizedException A general exception containing a localized message.
     */
    private void validateAdditionalCharacteristics() throws LocalizedException {
        this.validateSectorReference();
        this.validateIndustryGroupReference();
        this.validateStockExchange();
        this.validateSymbol();
        this.validateDividend();
        this.validateDivisor();
        this.validateDataSourceList();
        this.validateInvestingId();
    }

    /**
     * Validates the sector reference.
     *
     * @throws LocalizedException In case the sector is associated with an Instrument of the wrong type.
     */
    private void validateSectorReference() throws LocalizedException {
        if (this.instrument.getSector() == null) {
            return;
        }

        if (this.instrument.getType() == InstrumentType.SECTOR) {
            throw new LocalizedException("instrument.sector.sectorReference");
        }

        if (this.instrument.getSector().getType() != InstrumentType.SECTOR) {
            throw new LocalizedException("instrument.sector.wrongReference");
        }
    }

    /**
     * Validates the industry group reference.
     *
     * @throws LocalizedException In case the industry group is associated with an Instrument of the wrong type.
     */
    private void validateIndustryGroupReference() throws LocalizedException {
        if (this.instrument.getIndustryGroup() == null) {
            return;
        }

        if (this.instrument.getType() == InstrumentType.IND_GROUP) {
            throw new LocalizedException("instrument.ig.igReference");
        }

        if (this.instrument.getIndustryGroup().getType() != InstrumentType.IND_GROUP) {
            throw new LocalizedException("instrument.ig.wrongReference");
        }
    }

    /**
     * Validates the stock exchange attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateStockExchange() throws LocalizedException {
        if (this.instrument.getDataSourceList() != null) {
            if (this.instrument.getStockExchange() != null) {
                throw new LocalizedException("instrument.stockExchange.dataSourceListDefined");
            } else {
                return; // No stock exchange required, therefore no additional validation needed.
            }
        }

        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getStockExchange() != null) {
            throw new LocalizedException("instrument.stockExchange.definedOnTypeRatio");
        }

        if (this.instrument.getType() != InstrumentType.RATIO && this.instrument.getStockExchange() == null) {
            throw new LocalizedException("instrument.stockExchange.notNull.message");
        }
    }

    /**
     * Validates the symbol attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateSymbol() throws LocalizedException {
        if (this.instrument.getDataSourceList() != null) {
            if (this.instrument.getSymbol() != null && this.instrument.getSymbol().length() > 0) {
                throw new LocalizedException("instrument.symbol.dataSourceListDefined");
            } else {
                return; // No symbol required, therefore no additional validation needed.
            }
        }

        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getSymbol() != null
                && this.instrument.getSymbol().length() > 0) {
            throw new LocalizedException("instrument.symbol.definedOnTypeRatio");
        }

        if (this.instrument.getType() != InstrumentType.RATIO && this.instrument.getSymbol() == null) {
            throw new LocalizedException("instrument.symbol.notNull.message");
        }

        if (this.instrument.getType() != InstrumentType.RATIO && this.instrument.getSymbol() != null
                && (this.instrument.getSymbol().length() < 1
                        || this.instrument.getSymbol().length() > Instrument.MAX_SYMBOL_LENGTH)) {
            throw new LocalizedException("instrument.symbol.size.message", this.instrument.getSymbol().length(), "1",
                    Instrument.MAX_SYMBOL_LENGTH);
        }
    }

    /**
     * Validates the dividend attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateDividend() throws LocalizedException {
        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getDividend() == null) {
            throw new LocalizedException("instrument.dividend.notNull.message");
        }

        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getDividend() != null
                && this.instrument.getDividend().getType() == InstrumentType.RATIO) {
            throw new LocalizedException("instrument.dividend.typeRatio");
        }

        if (this.instrument.getType() != InstrumentType.RATIO && this.instrument.getDividend() != null) {
            throw new LocalizedException("instrument.dividend.definedOnTypeNotRatio");
        }
    }

    /**
     * Validates the divisor attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateDivisor() throws LocalizedException {
        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getDivisor() == null) {
            throw new LocalizedException("instrument.divisor.notNull.message");
        }

        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getDivisor() != null
                && this.instrument.getDivisor().getType() == InstrumentType.RATIO) {
            throw new LocalizedException("instrument.divisor.typeRatio");
        }

        if (this.instrument.getType() != InstrumentType.RATIO && this.instrument.getDivisor() != null) {
            throw new LocalizedException("instrument.divisor.definedOnTypeNotRatio");
        }
    }

    /**
     * Validates the dataSourceList attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateDataSourceList() throws LocalizedException {
        if (this.instrument.getDataSourceList() != null && this.instrument.getType() != InstrumentType.ETF
                && this.instrument.getType() != InstrumentType.SECTOR
                && this.instrument.getType() != InstrumentType.IND_GROUP) {
            throw new LocalizedException("instrument.dataSourceList.wrongType");
        }
    }

    /**
     * Validates the investingId attribute.
     *
     * @throws LocalizedException If validation failed.
     */
    private void validateInvestingId() throws LocalizedException {
        if (this.instrument.getDataSourceList() != null) {
            if (this.instrument.getInvestingId() != null && this.instrument.getInvestingId().length() > 0) {
                throw new LocalizedException("instrument.investingId.dataSourceListeDefined");
            } else {
                return; // No investingId required, therefore no additional validation needed.
            }
        }

        if (this.instrument.getType() == InstrumentType.RATIO && this.instrument.getInvestingId() != null
                && this.instrument.getInvestingId().length() > 0) {
            throw new LocalizedException("instrument.investingId.typeRatio");
        }

        if (this.instrument.getInvestingId() != null
                && this.instrument.getInvestingId().length() > Instrument.MAX_INVESTING_ID_LENGTH) {
            throw new LocalizedException("instrument.investingId.size.message",
                    this.instrument.getInvestingId().length(), "0", Instrument.MAX_INVESTING_ID_LENGTH);
        }
    }
}
