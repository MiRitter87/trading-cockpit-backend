package backend.model.instrument;

import java.util.HashSet;
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
import javax.persistence.Transient;
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
	 * The quotations.
	 */
//	@ManyToMany
//	@JoinTable(name = "INSTRUMENT_QUOTATION", 
//    	joinColumns = { @JoinColumn(name = "INSTRUMENT_ID") }, 
//    	inverseJoinColumns = { @JoinColumn(name = "QUOTATION_ID") })
	@Transient	//Remove transient annotation as soon as JPA annotations have been added to Quotation and Indicator.
	private Set<Quotation> quotations;
	
	
	/**
	 * Default constructor.
	 */
	public Instrument() {
		this.quotations = new HashSet<Quotation>();
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
	 * @return the quotations
	 */
	public Set<Quotation> getQuotations() {
		return quotations;
	}


	/**
	 * @param quotations the quotations to set
	 */
	public void setQuotations(Set<Quotation> quotations) {
		this.quotations = quotations;
	}
	
	
	/**
	 * Adds a quotation to the instrument.
	 * 
	 * @param quotation The quotation to be added.
	 */
	public void addQuotation(final Quotation quotation) {
		this.quotations.add(quotation);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((stockExchange == null) ? 0 : stockExchange.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((quotations == null) ? 0 : quotations.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Instrument other = (Instrument) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (stockExchange != other.stockExchange) {
			return false;
		}
		if (symbol == null) {
			if (other.symbol != null) {
				return false;
			}
		} else if (!symbol.equals(other.symbol)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		
		if(this.areQuotationsEqual(other) == false)
			return false;
		
		return true;
	}
	
	
	/**
	 * Checks if the list of quotations is equal.
	 * 
	 * @param other The other instrument for comparison.
	 * @return true, if quotations are equal; false otherwise.
	 */
	private boolean areQuotationsEqual(Instrument other) {
		if (this.quotations == null && other.quotations != null)
			return false;
		
		if (this.quotations != null && other.quotations == null)
			return false;
		
		if(this.quotations.size() != other.quotations.size())
			return false;
		
		for(Quotation tempQuotation:this.quotations) {
			Quotation otherQuotation = other.getQuotationWithId(tempQuotation.getId());
			
			if(otherQuotation == null)
				return false;
			
			if(!tempQuotation.equals(otherQuotation))
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Gets the quotation with the given id.
	 * 
	 * @param id The id of the quotation.
	 * @return The quotation with the given id, if found.
	 */
	public Quotation getQuotationWithId(Integer id) {
		for(Quotation tempQuotation:this.quotations) {
			if(tempQuotation.getId().equals(id))
				return tempQuotation;
		}
		
		return null;
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
