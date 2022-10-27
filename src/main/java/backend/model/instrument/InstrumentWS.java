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
	private Integer sector_id;
	
	/**
	 * The industry group the Instrument is part of.
	 */
	private Integer industry_group_id;
	
	
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
	 * @return the sector_id
	 */
	public Integer getSector_id() {
		return sector_id;
	}


	/**
	 * @param sector_id the sector_id to set
	 */
	public void setSector_id(Integer sector_id) {
		this.sector_id = sector_id;
	}


	/**
	 * @return the industry_group_id
	 */
	public Integer getIndustry_group_id() {
		return industry_group_id;
	}


	/**
	 * @param industry_group_id the industry_group_id to set
	 */
	public void setIndustry_group_id(Integer industry_group_id) {
		this.industry_group_id = industry_group_id;
	}
}
