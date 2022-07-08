package backend.model.list;

import java.util.ArrayList;

/**
 * A lean version of a list that is used by the WebService to transfer object data.
 * The main difference to the regular List is that IDs are used instead of object references.
 * 
 * @author Michael
 */
public class ListWS {
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
	private java.util.List<Integer> instrumentIds;
	
	
	/**
	 * Constructor.
	 */
	public ListWS() {
		this.instrumentIds = new ArrayList<Integer>();
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
	 * @return the instrumentIds
	 */
	public java.util.List<Integer> getInstrumentIds() {
		return instrumentIds;
	}


	/**
	 * @param instrumentIds the instrumentIds to set
	 */
	public void setInstrumentIds(java.util.List<Integer> instrumentIds) {
		this.instrumentIds = instrumentIds;
	}
}
