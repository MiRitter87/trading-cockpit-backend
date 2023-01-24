package backend.model.scan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * A lean version of a scan that is used by the WebService to transfer object data.
 * The main difference to the regular Scan is that IDs are used instead of object references.
 * 
 * @author Michael
 */
public class ScanWS {
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
	 * The execution status.
	 */
	private ScanExecutionStatus executionStatus;
	
	/**
	 * The completion status.
	 */
	private ScanCompletionStatus completionStatus;
	
	/**
	 * The percentage value indicating how much of the scan has been executed.
	 */
	private Integer progress;
	
	/**
	 * The lists whose instruments are scanned.
	 */
	private List<Integer> listIds;
	
	
	/**
	 * Constructor.
	 */
	public ScanWS() {
		this.listIds = new ArrayList<Integer>();
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
	 * @return the execution status
	 */
	public ScanExecutionStatus getExecutionStatus() {
		return executionStatus;
	}


	/**
	 * @param executionStatus the status to set
	 */
	public void setExecutionStatus(ScanExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}


	/**
	 * @return the completionStatus
	 */
	public ScanCompletionStatus getCompletionStatus() {
		return completionStatus;
	}


	/**
	 * @param completionStatus the completionStatus to set
	 */
	public void setCompletionStatus(ScanCompletionStatus completionStatus) {
		this.completionStatus = completionStatus;
	}


	/**
	 * @return the progress
	 */
	public Integer getProgress() {
		return progress;
	}


	/**
	 * @param progress the progress to set
	 */
	public void setProgress(Integer progress) {
		this.progress = progress;
	}


	/**
	 * @return the listIds
	 */
	public List<Integer> getListIds() {
		return listIds;
	}


	/**
	 * @param listIds the listIds to set
	 */
	public void setListIds(List<Integer> listIds) {
		this.listIds = listIds;
	}
}
