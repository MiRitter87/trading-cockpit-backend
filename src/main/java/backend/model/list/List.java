package backend.model.list;

import java.util.HashSet;
import java.util.Set;

import backend.model.instrument.Instrument;

/**
 * A list of instruments.
 * 
 * For example this can be a personal Watchlist or all stocks of an ETF or index.
 * 
 * @author Michael
 */
public class List {
	/**
	 * The ID.
	 */
	private Integer id;
	
	/**
	 * The name.
	 */
	private String name;
	
	/**
	 * The description.
	 */
	private String description;
	
	/**
	 * The instruments of the list.
	 */
	private Set<Instrument> instruments;
	
	
	/**
	 * Default constructor.
	 */
	public List() {
		this.instruments = new HashSet<Instrument>();
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the instruments
	 */
	public Set<Instrument> getInstruments() {
		return instruments;
	}


	/**
	 * @param instruments the instruments to set
	 */
	public void setInstruments(Set<Instrument> instruments) {
		this.instruments = instruments;
	}
}
