package backend.model.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import backend.model.LocalizedException;
import backend.model.StockExchange;
import backend.tools.DateTools;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A trading vehicle like a stock or an ETF.
 *
 * @author Michael
 */
@Table(name = "INSTRUMENT")
@Entity
@SequenceGenerator(name = "instrumentSequence", initialValue = 1, allocationSize = 1)
public class Instrument {
    /**
     * The maximum symbol field length allowed.
     */
    protected static final int MAX_SYMBOL_LENGTH = 6;

    /**
     * The maximum InstrumentType field length allowed.
     */
    private static final int MAX_TYPE_LENGTH = 10;

    /**
     * The maximum StockExchange field length allowed.
     */
    private static final int MAX_EXCHANGE_LENGTH = 4;

    /**
     * The maximum name field length allowed.
     */
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * The maximum 'Company Path Investing.com' field length allowed.
     */
    protected static final int MAX_COMP_PATH_INVESTING_LENGTH = 50;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instrumentSequence")
    @Column(name = "INSTRUMENT_ID")
    @Min(value = 1, message = "{instrument.id.min.message}")
    private Integer id;

    /**
     * The symbol.
     */
    @Column(name = "SYMBOL", length = MAX_SYMBOL_LENGTH)
    private String symbol;

    /**
     * The type of the instrument.
     */
    @Column(name = "TYPE", length = MAX_TYPE_LENGTH)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{instrument.type.notNull.message}")
    private InstrumentType type;

    /**
     * The exchange at which the instrument is traded.
     */
    @Column(name = "STOCK_EXCHANGE", length = MAX_EXCHANGE_LENGTH)
    @Enumerated(EnumType.STRING)
    private StockExchange stockExchange;

    /**
     * The name.
     */
    @Column(name = "NAME", length = MAX_NAME_LENGTH)
    @Size(min = 0, max = MAX_NAME_LENGTH, message = "{instrument.name.size.message}")
    private String name;

    /**
     * The sector the Instrument is part of.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTOR_ID")
    private Instrument sector;

    /**
     * The industry group the Instrument is part of.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDUSTRY_GROUP_ID")
    private Instrument industryGroup;

    /**
     * The path of the URL that specifies the company at investing.com.
     *
     * Example: In the URL "https://www.investing.com/equities/apple-computer-inc" the company path would be
     * "apple-computer-inc".
     */
    @Column(name = "COMPANY_PATH_INVESTING_COM", length = MAX_COMP_PATH_INVESTING_LENGTH)
    private String companyPathInvestingCom;

    /**
     * The dividend of a ratio.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIVIDEND_ID")
    private Instrument dividend;

    /**
     * The divisor of a ratio.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIVISOR_ID")
    private Instrument divisor;

    /**
     * The List that serves as a data source for the calculation of quotations. If a List is referenced, the quotations
     * are being calculated using the quotations of all instruments of the referenced List.
     */
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DATA_SOURCE_LIST_ID")
    private backend.model.list.List dataSourceList;

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
     * @param symbol        The symbol of the Instrument.
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
    public void setId(final Integer id) {
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
    public void setSymbol(final String symbol) {
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
    public void setType(final InstrumentType type) {
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
    public void setStockExchange(final StockExchange stockExchange) {
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
    public void setName(final String name) {
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
    public void setQuotations(final List<Quotation> quotations) {
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
    public void setSector(final Instrument sector) {
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
    public void setIndustryGroup(final Instrument industryGroup) {
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
    public void setCompanyPathInvestingCom(final String companyPathInvestingCom) {
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
    public void setDividend(final Instrument dividend) {
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
    public void setDivisor(final Instrument divisor) {
        this.divisor = divisor;
    }

    /**
     * @return the dataSourceList
     */
    public backend.model.list.List getDataSourceList() {
        return dataSourceList;
    }

    /**
     * @param dataSourceList the dataSourceList to set
     */
    public void setDataSourceList(final backend.model.list.List dataSourceList) {
        this.dataSourceList = dataSourceList;
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

        while (quotationIterator.hasNext()) {
            quotation = quotationIterator.next();

            if (quotation.getDate().getTime() == date.getTime()) {
                return quotation;
            }
        }

        return null;
    }

    /**
     * Determines the newest Quotation of the given date. If no Quotation is found, provide the next older one.
     *
     * @param date The date of the requested Quotation.
     * @return The newest Quotation of the given date or the next older one. Null, if no Quotation of the given date or
     *         older exists.
     */
    public Quotation getNewestQuotation(final Date date) {
        Collections.sort(this.quotations, new QuotationDateComparator());
        Date quotationDate;

        for (Quotation quotation : this.quotations) {
            quotationDate = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());

            if (date.getTime() == quotationDate.getTime()) {
                return quotation;
            }

            if (quotationDate.getTime() < date.getTime()) {
                return quotation;
            }
        }

        return null;
    }

    /**
     * Calculates the hashCode of an Instrument.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((stockExchange == null) ? 0 : stockExchange.hashCode());
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((sector == null) ? 0 : sector.getId().hashCode());
        result = prime * result + ((industryGroup == null) ? 0 : industryGroup.getId().hashCode());
        result = prime * result + ((dividend == null) ? 0 : dividend.getId().hashCode());
        result = prime * result + ((divisor == null) ? 0 : divisor.getId().hashCode());
        result = prime * result + ((dataSourceList == null) ? 0 : dataSourceList.getId().hashCode());
        result = prime * result + ((companyPathInvestingCom == null) ? 0 : companyPathInvestingCom.hashCode());
        return result;
    }

    /**
     * Indicates whether some other Instrument is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        InstrumentValidator validator = new InstrumentValidator(this);

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

        if (!validator.areAttributesEqual(other)) {
            return false;
        }

        if (!validator.areReferencesEqual(other)) {
            return false;
        }

        return true;
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
     * Gets all quotations as QuotationArray object.
     *
     * @return The quotations as QuotationArray.
     */
    @JsonIgnore
    public QuotationArray getQuotationArray() {
        QuotationArray quotationArray = new QuotationArray(this.quotations);

        return quotationArray;
    }

    /**
     * Validates the instrument.
     *
     * @throws LocalizedException A general exception containing a localized message.
     * @throws Exception          In case a general validation error occurred.
     */
    public void validate() throws LocalizedException, Exception {
        InstrumentValidator validator = new InstrumentValidator(this);
        validator.validate();
    }
}
