package backend.model.instrument;

import backend.model.StockExchange;

/**
 * A lean version of an Instrument that is used by the WebService to transfer object data.
 * The main difference to the regular Instrument is that IDs are used instead of object references.
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
	 * The path of the URL that specifies the company at investing.com.
	 * 
	 * Example: In the URL "https://www.investing.com/equities/apple-computer-inc" the company path would be "apple-computer-inc".
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
	 * @return the sectorId
	 */
	public Integer getSectorId() {
		return sectorId;
	}


	/**
	 * @param sectorId the sectorId to set
	 */
	public void setSectorId(Integer sectorId) {
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
	public void setIndustryGroupId(Integer industryGroupId) {
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
	public void setCompanyPathInvestingCom(String companyPathInvestingCom) {
		this.companyPathInvestingCom = companyPathInvestingCom;
	}
}
