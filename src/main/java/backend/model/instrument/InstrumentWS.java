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
     * The List that serves as a data source for the calculation of quotations. If a List is referenced, the quotations
     * are being calculated using the quotations of all instruments of the referenced List.
     */
    private Integer dataSourceListId;

    /**
     * The internal ID of the Instrument used at investing.com.
     */
    private String investingId;

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
     * @return the investingId
     */
    public String getInvestingId() {
        return investingId;
    }

    /**
     * @param investingId the investingId to set
     */
    public void setInvestingId(final String investingId) {
        this.investingId = investingId;
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

    /**
     * @return the dataSourceListId
     */
    public Integer getDataSourceListId() {
        return dataSourceListId;
    }

    /**
     * @param dataSourceListId the dataSourceListId to set
     */
    public void setDataSourceListId(final Integer dataSourceListId) {
        this.dataSourceListId = dataSourceListId;
    }
}
