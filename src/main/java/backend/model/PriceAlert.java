package backend.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

/**
 * An alert that a stock has reached a certain price at a stock exchange.
 * 
 * @author Michael
 */
public class PriceAlert {
	/**
	 * The ID.
	 */
	@Min(value = 1, message = "{priceAlert.id.min.message}")
	private Integer id;
	
	/**
	 * The stock symbol.
	 */
	private String symbol;
	
	/**
	 * The exchange where the stock is listed.
	 */
	private StockExchange stockExchange;
	
	/**
	 * The type of the price alert.
	 */
	private PriceAlertType alertType;
	
	/**
	 * The price at which the alert is activated.
	 */
	private BigDecimal price;
	
	/**
	 * The time at which the alert has been triggered.
	 */
	private Date triggerTime;
	
	/**
	 * The time at which the user as confirmed the alert.
	 */
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
