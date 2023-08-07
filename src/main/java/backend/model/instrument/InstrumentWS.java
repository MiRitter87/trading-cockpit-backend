package backend.model.instrument;

import backend.model.StockExchange;

/**
 * A lean version of an Instrument that is used by the WebService to transfer object data. The main difference to the
 * regular Instrument is that IDs are used instead of object references.
 *
 * @author Michael
 */
public class InstrumentWS {
    /**
     * The ID.
     */
    private Integer id;

    /**
     * The symbol.
     */
    private String symbol;

    /**
     * The type of the instrument.
     */
    private InstrumentType type;

    /**
     * The exchange at which the instrument is traded.
     */
    private StockExchange stockExchange;

    /**
     * The name.
     */
    private String name;

    /**
     * The sector the Instrument is part of.
     */
    private Integer sectorId;

    /**
     * The industry group the Instrument is part of.
     */
    private Integer industryGroupId;

    /**
     * The dividend of a ratio.
     */
    private Integer dividendId;

    /**
     * The divisor of a ratio.
     */
    private Integer divisorId;

    /**
     * The path of the URL that specifies the company at investing.com.
     *
     * Example: In the URL "https://www.investing.com/equities/apple-computer-inc" the company path would be
     * "apple-computer-inc".
     */
    private String companyPathInvestingCom;

    /**
     * Constructor.
     */
    public InstrumentWS() {

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
     * @return the sectorId
     */
    public Integer getSectorId() {
        return sectorId;
    }

    /**
     * @param sectorId the sectorId to set
     */
    public void setSectorId(final Integer sectorId) {
        this.sectorId = sectorId;
    }

    /**
     * @return the industryGroupId
     */
    public Integer getIndustryGroupId() {
        return industryGroupId;
    }

    /**
     * @param industryGroupId the industryGroupId to set
     */
    public void setIndustryGroupId(final Integer industryGroupId) {
        this.industryGroupId = industryGroupId;
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
     * @return the dividendId
     */
    public Integer getDividendId() {
        return dividendId;
    }

    /**
     * @param dividendId the dividendId to set
     */
    public void setDividendId(final Integer dividendId) {
        this.dividendId = dividendId;
    }

    /**
     * @return the divisorId
     */
    public Integer getDivisorId() {
        return divisorId;
    }

    /**
     * @param divisorId the divisorId to set
     */
    public void setDivisorId(final Integer divisorId) {
        this.divisorId = divisorId;
    }
}
