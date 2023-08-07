package backend.model.chart;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
