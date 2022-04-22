package backend.model.priceAlert;

import java.math.BigDecimal;
import java.util.Date;
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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.StockExchange;

/**
 * An alert that a stock has reached a certain price at a stock exchange.
 * 
 * @author Michael
 */
@Table(name="PRICE_ALERT")
@Entity
@SequenceGenerator(name = "priceAlertSequence", initialValue = 1, allocationSize = 1)
public class PriceAlert {
	/**
	 * The ID.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priceAlertSequence")
	@Column(name="PRICE_ALERT_ID")
	@Min(value = 1, message = "{priceAlert.id.min.message}")
	private Integer id;
	
	/**
	 * The stock symbol.
	 */
	@Column(name="SYMBOL", length = 6)
	@NotNull(message = "{priceAlert.symbol.notNull.message}")
	@Size(min = 1, max = 6, message = "{priceAlert.symbol.size.message}")
	private String symbol;
	
	/**
	 * The exchange where the stock is listed.
	 */
	@Column(name="STOCK_EXCHANGE", length = 4)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{priceAlert.stockExchange.notNull.message}")
	private StockExchange stockExchange;
	
	/**
	 * The type of the price alert.
	 */
	@Column(name="ALERT_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{priceAlert.alertType.notNull.message}")
	private PriceAlertType alertType;
	
	/**
	 * The price at which the alert is activated.
	 */
	@Column(name="PRICE")
	@NotNull(message = "{priceAlert.price.notNull.message}")
	@DecimalMin(value = "0.01", inclusive = true, message = "{priceAlert.price.decimalMin.message}")
	@Max(value = 100000, message = "{priceAlert.price.max.message}")
	private BigDecimal price;
	
	/**
	 * The time at which the alert has been triggered.
	 */
	@Column(name="TRIGGER_TIME")
	private Date triggerTime;
	
	/**
	 * The time at which the user as confirmed the alert.
	 */
	@Column(name="CONFIRMATION_TIME")
	private Date confirmationTime;
	
	
	/**
	 * Default constructor.
	 */
	public PriceAlert() {
		
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
	 * @return the alertType
	 */
	public PriceAlertType getAlertType() {
		return alertType;
	}


	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(PriceAlertType alertType) {
		this.alertType = alertType;
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
	 * @return the triggerTime
	 */
	public Date getTriggerTime() {
		return triggerTime;
	}


	/**
	 * @param triggerTime the triggerTime to set
	 */
	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}


	/**
	 * @return the confirmationTime
	 */
	public Date getConfirmationTime() {
		return confirmationTime;
	}


	/**
	 * @param confirmationTime the confirmationTime to set
	 */
	public void setConfirmationTime(Date confirmationTime) {
		this.confirmationTime = confirmationTime;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alertType == null) ? 0 : alertType.hashCode());
		result = prime * result + ((confirmationTime == null) ? 0 : confirmationTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((stockExchange == null) ? 0 : stockExchange.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((triggerTime == null) ? 0 : triggerTime.hashCode());
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
		PriceAlert other = (PriceAlert) obj;
		if (alertType != other.alertType) {
			return false;
		}
		if (confirmationTime == null && other.confirmationTime != null)
			return false;
		if (confirmationTime != null && other.confirmationTime == null)
			return false;
		if(confirmationTime != null && other.confirmationTime != null) {
			if (confirmationTime.getTime() != other.confirmationTime.getTime())
				return false;
		}		
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (price.compareTo(other.price) != 0) {
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
		if (triggerTime == null && other.triggerTime != null)
			return false;
		if (triggerTime != null && other.triggerTime == null)
			return false;
		if(triggerTime != null && other.triggerTime != null) {
			if (triggerTime.getTime() != other.triggerTime.getTime())
				return false;
		}	
		return true;
	}


	/**
	 * Validates the price alert.
	 * 
	 * @throws Exception In case a general validation error occurred.
	 */
	public void validate() throws Exception {
		this.validateAnnotations();
	}
	
	
	/**
	 * Validates the price alert according to the annotations of the validation framework.
	 * 
	 * @exception Exception In case the validation failed.
	 */
	private void validateAnnotations() throws Exception {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)   
                .configure().constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<PriceAlert>> violations = validator.validate(this);

		for(ConstraintViolation<PriceAlert> violation:violations) {
			throw new Exception(violation.getMessage());
		}
	}
}
