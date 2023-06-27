package backend.model.chart;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

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
public class HorizontalLine {
	/**
	 * The ID.
	 */
	@Min(value = 1, message = "{horizontalLine.id.min.message}")
	private Integer id;
	
	/**
	 * The price at which the horizontal line is drawn.
	 */
	@NotNull(message = "{horizontalLine.price.notNull.message}")
	@DecimalMin(value = "0.01", inclusive = true, message = "{horizontalLine.price.decimalMin.message}")
	@Max(value = 100000, message = "{horizontalLine.price.max.message}")
	private BigDecimal price;
	
	/**
	 * The Instrument this line belongs to.
	 */
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
	public void setId(Integer id) {
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
	public void setPrice(BigDecimal price) {
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
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, instrument, price);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		HorizontalLine other = (HorizontalLine) obj;
		
		if(price == null && other.price != null)
			return false;
		if(price != null && other.price == null)
			return false;
		if(price != null && other.price != null && price.compareTo(other.price) != 0)
			return false;
		
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
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)   
                .configure().constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<HorizontalLine>> violations = validator.validate(this);

		for(ConstraintViolation<HorizontalLine> violation:violations) {
			throw new Exception(violation.getMessage());
		}
	}
}
