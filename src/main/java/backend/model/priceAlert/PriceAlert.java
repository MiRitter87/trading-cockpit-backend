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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

import backend.model.Currency;
import backend.model.instrument.Instrument;

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
	 * The Instrument.
	 */
	@OneToOne
	@JoinColumn(name="INSTRUMENT_ID")
	@NotNull(message = "{priceAlert.instrument.notNull.message}")
	private Instrument instrument;
	
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
	 * The currency.
	 */
	@Column(name="CURRENCY", length = 3)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{priceAlert.currency.notNull.message}")
	private Currency currency;
	
	/**
	 * The distance between the current price and the trigger level in percent.
	 */
	@Column(name="TRIGGER_DISTANCE_PERCENT")
	private float triggerDistancePercent;
	
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
	 * The time of the last stock quote query.
	 */
	@Column(name="LAST_STOCK_QUOTE_TIME")
	private Date lastStockQuoteTime;
	
	
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
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}


	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}


	/**
	 * @return the triggerDistancePercent
	 */
	public float getTriggerDistancePercent() {
		return triggerDistancePercent;
	}


	/**
	 * @param triggerDistancePercent the triggerDistancePercent to set
	 */
	public void setTriggerDistancePercent(float triggerDistancePercent) {
		this.triggerDistancePercent = triggerDistancePercent;
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
	
	
	/**
	 * @return the lastStockQuoteTime
	 */
	public Date getLastStockQuoteTime() {
		return lastStockQuoteTime;
	}


	/**
	 * @param lastStockQuoteTime the lastStockQuoteTime to set
	 */
	public void setLastStockQuoteTime(Date lastStockQuoteTime) {
		this.lastStockQuoteTime = lastStockQuoteTime;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alertType == null) ? 0 : alertType.hashCode());
		result = prime * result + ((confirmationTime == null) ? 0 : confirmationTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
		result = prime * result + ((triggerTime == null) ? 0 : triggerTime.hashCode());
		result = prime * result + ((lastStockQuoteTime == null) ? 0 : lastStockQuoteTime.hashCode());
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
		if (currency == null) {
			if (other.currency != null) {
				return false;
			}
		} else if (currency.compareTo(other.currency) != 0) {
			return false;
		}
		if (instrument == null) {
			if (other.instrument != null) {
				return false;
			}
		} else if (!instrument.equals(other.instrument)) {
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
		if (lastStockQuoteTime == null && other.lastStockQuoteTime != null)
			return false;
		if (lastStockQuoteTime != null && other.lastStockQuoteTime == null)
			return false;
		if(lastStockQuoteTime != null && other.lastStockQuoteTime != null) {
			if (lastStockQuoteTime.getTime() != other.lastStockQuoteTime.getTime())
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
