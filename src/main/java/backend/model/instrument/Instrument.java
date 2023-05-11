package backend.model.instrument;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import backend.model.LocalizedException;
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
	private StockExchange stockExchange;
	
	/**
	 * The name.
	 */
	@Column(name="NAME", length = 50)
	@Size(min = 0, max = 50, message = "{instrument.name.size.message}")
	private String name;
	
	/**
	 * The sector the Instrument is part of.
	 */
	@OneToOne(targetEntity = Instrument.class)
	@JoinColumn(name="SECTOR_ID")
	private Instrument sector;
	
	/**
	 * The industry group the Instrument is part of.
	 */
	@OneToOne(targetEntity = Instrument.class)
	@JoinColumn(name="INDUSTRY_GROUP_ID")
	private Instrument industryGroup;
	
	/**
	 * The path of the URL that specifies the company at investing.com.
	 * 
	 * Example: In the URL "https://www.investing.com/equities/apple-computer-inc" the company path would be "apple-computer-inc".
	 */
	@Column(name="COMPANY_PATH_INVESTING_COM", length = 50)
	@Size(min = 0, max = 50, message = "{instrument.companyPathInvestingCom.size.message}")
	private String companyPathInvestingCom;
	
	/**
	 * The dividend of a ratio.
	 */
	@OneToOne(targetEntity = Instrument.class)
	@JoinColumn(name="DIVIDEND_ID")
	private Instrument dividend;
	
	/**
	 * The divisor of a ratio.
	 */
	@OneToOne(targetEntity = Instrument.class)
	@JoinColumn(name="DIVISOR_ID")
	private Instrument divisor;
	
	/**
	 * The quotations.
	 */
	@Transient
	private List<Quotation> quotations;
	
	
	/**
	 * Default constructor.
	 */
	public Instrument() {
		this.quotations = new ArrayList<>();
	}
	
	
	/**
	 * Initializes the Instrument.
	 * 
	 * @param symbol The symbol of the Instrument.
	 * @param stockExchange The stock exchange of the Instrument.
	 */
	public Instrument(final String symbol, final StockExchange stockExchange) {
		this.symbol = symbol;
		this.stockExchange = stockExchange;
		
		this.quotations = new ArrayList<>();
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
	public List<Quotation> getQuotations() {
		return quotations;
	}


	/**
	 * @param quotations the quotations to set
	 */
	public void setQuotations(List<Quotation> quotations) {
		this.quotations = quotations;
	}
	
	
	/**
	 * @return the sector
	 */
	public Instrument getSector() {
		return sector;
	}


	/**
	 * @param sector the sector to set
	 */
	public void setSector(Instrument sector) {
		this.sector = sector;
	}


	/**
	 * @return the industryGroup
	 */
	public Instrument getIndustryGroup() {
		return industryGroup;
	}


	/**
	 * @param industryGroup the industry_group to set
	 */
	public void setIndustryGroup(Instrument industryGroup) {
		this.industryGroup = industryGroup;
	}


	/**
	 * @return the companyPathInvestingCom
	 */
	public String getCompanyPathInvestingCom() {
		return companyPathInvestingCom;
	}


	/**
	 * @param companyPathInvestingCom the companyPathInvestingCom to set
	 */
	public void setCompanyPathInvestingCom(String companyPathInvestingCom) {
		this.companyPathInvestingCom = companyPathInvestingCom;
	}


	/**
	 * @return the dividend
	 */
	public Instrument getDividend() {
		return dividend;
	}


	/**
	 * @param dividend the dividend to set
	 */
	public void setDividend(Instrument dividend) {
		this.dividend = dividend;
	}


	/**
	 * @return the divisor
	 */
	public Instrument getDivisor() {
		return divisor;
	}


	/**
	 * @param divisor the divisor to set
	 */
	public void setDivisor(Instrument divisor) {
		this.divisor = divisor;
	}


	/**
	 * Adds a quotation to the instrument.
	 * 
	 * @param quotation The quotation to be added.
	 */
	public void addQuotation(final Quotation quotation) {
		this.quotations.add(quotation);
	}
	
	
	/**
	 * Gets the quotation with the given date.
	 * 
	 * @param date The date of the quotation.
	 * @return The quotation with the given date, if found.
	 */
	public Quotation getQuotationByDate(final Date date) {
		Iterator<Quotation> quotationIterator = this.getQuotations().iterator();
		Quotation quotation;
		
		while(quotationIterator.hasNext()) {
			quotation = quotationIterator.next();
			
			if(quotation.getDate().getTime() == date.getTime())
				return quotation;
		}
		
		return null;
	}
	
	
	/**
	 * Gets the quotations that are older than the given date but are still at the same day.
	 * 
	 * @param date The date for which older quotations of the same day are requested.
	 * @return Older quotations of the same day.
	 */
	public List<Quotation> getOlderQuotationsOfSameDay(final Date date) {
		List<Quotation> quotationsSortedByDate = this.getQuotationsSortedByDate();
		List<Quotation> olderQuotationsSameDay = new ArrayList<>();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate tempQuotationLocalDate;
		
		for(Quotation tempQuotation:quotationsSortedByDate) {
			tempQuotationLocalDate = tempQuotation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			if(localDate.equals(tempQuotationLocalDate)) {
				if(tempQuotation.getDate().getTime() < date.getTime()) {
					olderQuotationsSameDay.add(tempQuotation);					
				}
			}
			else {
				break;	//The quotations are sorted by date. Therefore all quotations coming afterwards are not at the same day.
			}
		}
		
		return olderQuotationsSameDay;
	}
	
	
	/**
	 * Gets the age of the newest Quotation in days.
	 * 
	 * @return The age of the newest Quotation in days.
	 */
	public long getAgeOfNewestQuotationInDays() {
		List<Quotation> quotationsSortedByDate = this.getQuotationsSortedByDate();
		Quotation newestQuotation;
		Date currentDate = new Date();
		LocalDate currentDateLocal = LocalDate.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
		LocalDate newestQuotationDateLocal;
		long days;
		
		if(this.quotations == null || quotations.size() == 0)
			return 0;
		
		quotationsSortedByDate = this.getQuotationsSortedByDate();
		newestQuotation = quotationsSortedByDate.get(0);
		newestQuotationDateLocal = LocalDate.ofInstant(newestQuotation.getDate().toInstant(), ZoneId.systemDefault());
		
		days = ChronoUnit.DAYS.between(newestQuotationDateLocal, currentDateLocal);
		
		return days;
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
		result = prime * result + ((sector == null) ? 0 : sector.hashCode());
		result = prime * result + ((industryGroup == null) ? 0 : industryGroup.hashCode());
		result = prime * result + ((companyPathInvestingCom == null) ? 0 : companyPathInvestingCom.hashCode());
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
		if (sector == null) {
			if (other.sector != null) {
				return false;
			}
		} else if (!sector.equals(other.sector)) {
			return false;
		}
		if (industryGroup == null) {
			if (other.industryGroup != null) {
				return false;
			}
		} else if (!industryGroup.equals(other.industryGroup)) {
			return false;
		}
		if (companyPathInvestingCom == null) {
			if (other.companyPathInvestingCom != null) {
				return false;
			}
		} else if (!companyPathInvestingCom.equals(other.companyPathInvestingCom)) {
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
	 * Gets all quotations sorted by date.
	 * 
	 * @return All quotations sorted by date.
	 */
	@JsonIgnore
	public List<Quotation> getQuotationsSortedByDate() {
		Collections.sort(this.quotations, new QuotationDateComparator());
				
		return this.quotations;
	}


	/**
	 * Validates the instrument.
	 * 
	 * @throws InstrumentReferenceException In case the Instrument is associated with another Instrument of the wrong type.
	 * @throws A general exception containing a localized message.
	 * @throws Exception In case a general validation error occurred.
	 */
	public void validate() throws InstrumentReferenceException, LocalizedException, Exception {
		this.validateAnnotations();
		this.validateAdditionalCharacteristics();
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
	
	
	/**
	 * Validates additional characteristics of the Instrument besides annotations.
	 * 
	 * @throws InstrumentReferenceException In case the validation failed.
	 * @throws A general exception containing a localized message.
	 */
	private void validateAdditionalCharacteristics() throws InstrumentReferenceException, LocalizedException {
		this.validateSectorReference();
		this.validateIndustryGroupReference();
		this.validateStockExchange();
		this.validateSymbol();
		this.validateDividend();
		this.validateDivisor();
	}
	
	
	/**
	 * Validates the sector reference.
	 * 
	 * @throws InstrumentReferenceException In case the sector is associated with an Instrument of the wrong type.
	 */
	private void validateSectorReference() throws InstrumentReferenceException {
		if(this.sector == null)
			return;
		
		if(this.type == InstrumentType.SECTOR)
			throw new InstrumentReferenceException(null, InstrumentType.SECTOR);
		
		if(this.sector.getType() != InstrumentType.SECTOR) {
			throw new InstrumentReferenceException(InstrumentType.SECTOR, this.sector.getType());
		}
	}
	
	
	/**
	 * Validates the industry group reference.
	 * 
	 * @throws InstrumentReferenceException In case the industry group is associated with an Instrument of the wrong type.
	 */
	private void validateIndustryGroupReference() throws InstrumentReferenceException {
		if(this.industryGroup == null)
			return;
		
		if(this.type == InstrumentType.IND_GROUP)
			throw new InstrumentReferenceException(null, InstrumentType.IND_GROUP);
		
		if(this.industryGroup.getType() != InstrumentType.IND_GROUP) {
			throw new InstrumentReferenceException(InstrumentType.IND_GROUP, this.industryGroup.getType());
		}
	}
	
	
	/**
	 * Validates the stock exchange attribute.
	 * 
	 * @throws LocalizedException If validation failed.
	 */
	private void validateStockExchange() throws LocalizedException {
		if(this.type == InstrumentType.RATIO && this.stockExchange != null)
			throw new LocalizedException("instrument.exchangeDefinedOnTypeRatio");
		
		if(this.type != InstrumentType.RATIO && this.stockExchange == null)
			throw new LocalizedException("instrument.stockExchange.notNull.message");
	}
	
	
	/**
	 * Validates the symbol attribute.
	 * 
	 * @throws LocalizedException If validation failed.
	 */
	private void validateSymbol() throws LocalizedException {
		if(this.type == InstrumentType.RATIO && this.symbol != null && this.symbol.length() > 0)
			throw new LocalizedException("instrument.symbolDefinedOnTypeRatio");
		
		if(this.type != InstrumentType.RATIO && this.symbol == null)
			throw new LocalizedException("instrument.symbol.notNull.message");
		
		if(this.type != InstrumentType.RATIO && this.symbol != null && (this.symbol.length() < 1 || this.symbol.length() > 6))
			throw new LocalizedException("instrument.symbol.size.message", this.symbol.length(), "1", "6");
	}
	
	
	/**
	 * Validates the dividend attribute. 
	 * 
	 * @throws LocalizedException If validation failed.
	 */
	private void validateDividend() throws LocalizedException {
		if(this.type == InstrumentType.RATIO && this.dividend == null)
			throw new LocalizedException("instrument.dividend.notNull.message");
		
		if(this.type != InstrumentType.RATIO && this.dividend != null)
			throw new LocalizedException("instrument.dividendDefinedOnTypeNotRatio");
	}
	
	
	/**
	 * Validates the divisor attribute. 
	 * 
	 * @throws LocalizedException If validation failed.
	 */
	private void validateDivisor() throws LocalizedException {
		if(this.type == InstrumentType.RATIO && this.divisor == null)
			throw new LocalizedException("instrument.divisor.notNull.message");
		
		if(this.type != InstrumentType.RATIO && this.divisor != null)
			throw new LocalizedException("instrument.divisorDefinedOnTypeNotRatio");
	}
}
