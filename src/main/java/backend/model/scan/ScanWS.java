package backend.model.scan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A lean version of a scan that is used by the WebService to transfer object data. The main difference to the regular
 * Scan is that IDs are used instead of object references.
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
     * The instruments whose data could not be retrieved successfully during the last scan run.
     */
    private List<Integer> incompleteInstrumentIds;

    /**
     * Constructor.
     */
    public ScanWS() {
        this.listIds = new ArrayList<Integer>();
        this.incompleteInstrumentIds = new ArrayList<Integer>();
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
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
    public void setLastScan(final Date lastScan) {
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
    public void setExecutionStatus(final ScanExecutionStatus executionStatus) {
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
    public void setCompletionStatus(final ScanCompletionStatus completionStatus) {
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
    public void setProgress(final Integer progress) {
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
    public void setListIds(final List<Integer> listIds) {
        this.listIds = listIds;
    }

    /**
     * @return the incompleteInstrumentIds
     */
    public List<Integer> getIncompleteInstrumentIds() {
        return incompleteInstrumentIds;
    }

    /**
     * @param incompleteInstrumentIds the incompleteInstrumentIds to set
     */
    public void setIncompleteInstrumentIds(final List<Integer> incompleteInstrumentIds) {
        this.incompleteInstrumentIds = incompleteInstrumentIds;
    }
}
