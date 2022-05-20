package backend.model.instrument;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.StockExchange;

/**
 * A trading vehicle like a stock or an ETF.
 * 
 * @author Michael
 */
@Table(name="INSTRUMENT")
@Entity
@SequenceGenerator(name = "instrumentSequence", initialValue = 1, allocationSize = 1)
public class Instrument {
	/**
	 * The ID.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instrumentSequence")
	@Column(name="INSTRUMENT_ID")
	@Min(value = 1, message = "{instrument.id.min.message}")
	private Integer id;
	
	/**
	 * The symbol.
	 */
	@Column(name="SYMBOL", length = 6)
	@NotNull(message = "{instrument.symbol.notNull.message}")
	@Size(min = 1, max = 6, message = "{instrument.symbol.size.message}")
	private String symbol;
	
	/**
	 * The type of the instrument.
	 */
	@Column(name="TYPE", length = 10)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{instrument.type.notNull.message}")
	private InstrumentType type;
	
	/**
	 * The exchange at which the instrument is traded.
	 */
	@Column(name="STOCK_EXCHANGE", length = 4)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{instrument.stockExchange.notNull.message}")
	private StockExchange stockExchange;
	
	/**
	 * The name.
	 */
	@Column(name="NAME", length = 50)
	@Size(min = 0, max = 50, message = "{instrument.name.size.message}")
	private String name;
	
	
	/**
	 * Default constructor.
	 */
	public Instrument() {
		
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
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}


	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	/**
	 * @return the type
	 */
	public InstrumentType getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(InstrumentType type) {
		this.type = type;
	}


	/**
	 * @return the stockExchange
	 */
	public StockExchange getStockExchange() {
		return stockExchange;
	}


	/**
	 * @param stockExchange the stockExchange to set
	 */
	public void setStockExchange(StockExchange stockExchange) {
		this.stockExchange = stockExchange;
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
	 * Validates the instrument.
	 * 
	 * @throws Exception In case a general validation error occurred.
	 */
	public void validate() throws Exception {
		this.validateAnnotations();
	}
	
	
	/**
	 * Validates the instrument according to the annotations of the validation framework.
	 * 
	 * @exception Exception In case the validation failed.
	 */
	private void validateAnnotations() throws Exception {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)   
                .configure().constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Instrument>> violations = validator.validate(this);

		for(ConstraintViolation<Instrument> violation:violations) {
			throw new Exception(violation.getMessage());
		}
	}
}
