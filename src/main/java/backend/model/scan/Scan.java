package backend.model.scan;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import backend.model.list.List;

/**
 * A scan that retrieves quotes and calculates indicators of multiple instrument lists.
 * 
 * @author Michael
 *
 */
public class Scan {
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
	 * The date at which the scan has been started the last time.
	 */
	private Date lastScan;
	
	/**
	 * Indicates if the scan is currently running.
	 */
	private boolean isRunning;
	
	/**
	 * The percentage value indicating how much of the scan has been executed.
	 */
	private Integer percentCompleted;
	
	/**
	 * The lists whose instruments are scanned.
	 */
	private Set<List> lists;
	
	
	/**
	 * Default constructor.
	 */
	public Scan() {
		this.lists = new HashSet<List>();
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
	 * @return the lastScan
	 */
	public Date getLastScan() {
		return lastScan;
	}


	/**
	 * @param lastScan the lastScan to set
	 */
	public void setLastScan(Date lastScan) {
		this.lastScan = lastScan;
	}


	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}


	/**
	 * @param isRunning the isRunning to set
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}


	/**
	 * @return the percentCompleted
	 */
	public Integer getPercentCompleted() {
		return percentCompleted;
	}


	/**
	 * @param percentCompleted the percentCompleted to set
	 */
	public void setPercentCompleted(Integer percentCompleted) {
		this.percentCompleted = percentCompleted;
	}


	/**
	 * @return the lists
	 */
	public Set<List> getLists() {
		return lists;
	}


	/**
	 * @param lists the lists to set
	 */
	public void setLists(Set<List> lists) {
		this.lists = lists;
	}
}
