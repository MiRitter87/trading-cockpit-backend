package backend.model.list;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.instrument.Instrument;

/**
 * A list of instruments.
 * 
 * For example this can be a personal Watchlist or all stocks of an ETF or index.
 * 
 * @author Michael
 */
public class List {
	/**
	 * The ID.
	 */
	@Min(value = 1, message = "{list.id.min.message}")
	private Integer id;
	
	/**
	 * The name.
	 */
	@NotNull(message = "{list.name.notNull.message}")
	@Size(min = 1, max = 50, message = "{list.name.size.message}")
	private String name;
	
	/**
	 * The description.
	 */
	@Size(min = 0, max = 250, message = "{list.description.size.message}")
	private String description;
	
	/**
	 * The instruments of the list.
	 */
	private Set<Instrument> instruments;
	
	
	/**
	 * Default constructor.
	 */
	public List() {
		this.instruments = new HashSet<Instrument>();
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the instruments
	 */
	public Set<Instrument> getInstruments() {
		return instruments;
	}


	/**
	 * @param instruments the instruments to set
	 */
	public void setInstruments(Set<Instrument> instruments) {
		this.instruments = instruments;
	}
	
	
	/**
	 * Adds an instrument to the list.
	 * 
	 * @param instrument The instrument to be added.
	 */
	public void addInstrument(final Instrument instrument) {
		this.instruments.add(instrument);
	}
	
	
	/**
	 * Validates the list.
	 * 
	 * @throws Exception In case a general validation error occurred.
	 */
	public void validate() throws Exception {
		this.validateAnnotations();
		this.validateAdditionalCharacteristics();
	}
	
	
	/**
	 * Validates the list according to the annotations of the validation framework.
	 * 
	 * @exception Exception In case the validation failed.
	 */
	private void validateAnnotations() throws Exception {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)   
                .configure().constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<List>> violations = validator.validate(this);

		for(ConstraintViolation<List> violation:violations) {
			throw new Exception(violation.getMessage());
		}
	}
	
	
	/**
	 * Validates additional characteristics of the instrument besides annotations.
	 * 
	 * @throws NoItemsException Indicates that the instrument has no items defined.
	 */
	private void validateAdditionalCharacteristics() throws NoItemsException {
		this.validateItemsDefined();
	}
	
	
	/**
	 * Checks if items are defined.
	 * 
	 * @throws NoItemsException If no items are defined.
	 */
	private void validateItemsDefined() throws NoItemsException {
		if(this.instruments == null || this.instruments.size() == 0)
			throw new NoItemsException();
	}
}
