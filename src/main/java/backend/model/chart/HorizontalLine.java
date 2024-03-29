package backend.model.chart;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.instrument.Instrument;

/**
 * A horizontal line that is drawn on a chart.
 *
 * @author Michael
 */
@Table(name = "HORIZONTAL_LINE")
@Entity
@SequenceGenerator(name = "horizontalLineSequence", initialValue = 1, allocationSize = 1)
public class HorizontalLine {
    /**
     * The maximum price allowed.
     */
    private static final int MAX_PRICE = 100000;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "horizontalLineSequence")
    @Column(name = "HORIZONTAL_LINE_ID")
    @Min(value = 1, message = "{horizontalLine.id.min.message}")
    private Integer id;

    /**
     * The price at which the horizontal line is drawn.
     */
    @Column(name = "PRICE")
    @NotNull(message = "{horizontalLine.price.notNull.message}")
    @DecimalMin(value = "0.01", inclusive = true, message = "{horizontalLine.price.decimalMin.message}")
    @Max(value = MAX_PRICE, message = "{horizontalLine.price.max.message}")
    private BigDecimal price;

    /**
     * The Instrument this line belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTRUMENT_ID")
    @NotNull(message = "{horizontalLine.instrument.notNull.message}")
    private Instrument instrument;

    /**
     * Default constructor.
     */
    public HorizontalLine() {

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
     * Calculates the hashCode of a HorizontalLine.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, instrument, price);
    }

    /**
     * Indicates whether some other HorizontalLine is "equal to" this one.
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

        HorizontalLine other = (HorizontalLine) obj;

        if (price == null && other.price != null) {
            return false;
        }
        if (price != null && other.price == null) {
            return false;
        }
        if (price != null && other.price != null && price.compareTo(other.price) != 0) {
            return false;
        }

        return Objects.equals(id, other.id) && Objects.equals(instrument, other.instrument);
    }

    /**
     * Validates the HorizontalLine.
     *
     * @throws Exception In case a general validation error occurred.
     */
    public void validate() throws Exception {
        this.validateAnnotations();
    }

    /**
     * Validates the HorizontalLine according to the annotations of the validation framework.
     *
     * @exception Exception In case the validation failed.
     */
    private void validateAnnotations() throws Exception {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                .constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<HorizontalLine>> violations = validator.validate(this);

        for (ConstraintViolation<HorizontalLine> violation : violations) {
            throw new Exception(violation.getMessage());
        }
    }
}
