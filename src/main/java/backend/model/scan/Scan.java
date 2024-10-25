package backend.model.scan;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import backend.model.instrument.Instrument;
import backend.model.list.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A scan that retrieves quotes and calculates indicators of multiple instrument lists.
 *
 * @author Michael
 *
 */
@Table(name = "SCAN")
@Entity
@SequenceGenerator(name = "scanSequence", initialValue = 1, allocationSize = 1)
public class Scan {
    /**
     * The maximum name field length allowed.
     */
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * The maximum description field length allowed.
     */
    private static final int MAX_DESCRIPTION_LENGTH = 250;

    /**
     * The maximum ScanExecutionStatus field length allowed.
     */
    private static final int MAX_EXECUTION_STATUS_LENGTH = 20;

    /**
     * The maximum ScanCompletionStatus field length allowed.
     */
    private static final int MAX_COMPLETION_STATUS_LENGTH = 20;

    /**
     * The maximum progress value allowed.
     */
    private static final int MAX_PROGRESS_VALUE = 100;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scanSequence")
    @Column(name = "SCAN_ID")
    @Min(value = 1, message = "{scan.id.min.message}")
    private Integer id;

    /**
     * The name.
     */
    @Column(name = "NAME", length = MAX_NAME_LENGTH)
    @NotNull(message = "{scan.name.notNull.message}")
    @Size(min = 1, max = MAX_NAME_LENGTH, message = "{scan.name.size.message}")
    private String name;

    /**
     * The description.
     */
    @Column(name = "DESCRIPTION", length = MAX_DESCRIPTION_LENGTH)
    @Size(min = 0, max = MAX_DESCRIPTION_LENGTH, message = "{scan.description.size.message}")
    private String description;

    /**
     * The date at which the scan has been started the last time.
     */
    @Column(name = "LAST_SCAN")
    private Date lastScan;

    /**
     * The execution status.
     */
    @Column(name = "EXECUTION_STATUS", length = MAX_EXECUTION_STATUS_LENGTH)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{scan.executionStatus.notNull.message}")
    private ScanExecutionStatus executionStatus;

    /**
     * The completion status.
     */
    @Column(name = "COMPLETION_STATUS", length = MAX_COMPLETION_STATUS_LENGTH)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{scan.completionStatus.notNull.message}")
    private ScanCompletionStatus completionStatus;

    /**
     * The percentage value indicating how much of the scan has been executed.
     */
    @Column(name = "PROGRESS")
    @Min(value = 0, message = "{scan.progress.min.message}")
    @Max(value = MAX_PROGRESS_VALUE, message = "{scan.progress.max.message}")
    @NotNull(message = "{scan.progress.notNull.message}")
    private Integer progress;

    /**
     * The lists whose instruments are scanned.
     */
    @ManyToMany
    @JoinTable(name = "SCAN_LIST", joinColumns = { @JoinColumn(name = "SCAN_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "LIST_ID") })
    private Set<List> lists;

    /**
     * Instruments whose data could not be retrieved correctly during the scan process.
     */
    @ManyToMany
    @JoinTable(name = "SCAN_INCOMPLETE_INSTRUMENT", joinColumns = {
            @JoinColumn(name = "SCAN_ID") }, inverseJoinColumns = { @JoinColumn(name = "INSTRUMENT_ID") })
    private Set<Instrument> incompleteInstruments;

    /**
     * Default constructor.
     */
    public Scan() {
        this.executionStatus = ScanExecutionStatus.NOT_EXECUTED;
        this.completionStatus = ScanCompletionStatus.COMPLETE;
        this.progress = 0;
        this.lists = new HashSet<List>();
        this.incompleteInstruments = new HashSet<Instrument>();
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
     * @param executionStatus the execution status to set
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
     * @return the lists
     */
    public Set<List> getLists() {
        return lists;
    }

    /**
     * @param lists the lists to set
     */
    public void setLists(final Set<List> lists) {
        this.lists = lists;
    }

    /**
     * @return the incompleteInstruments
     */
    public Set<Instrument> getIncompleteInstruments() {
        return incompleteInstruments;
    }

    /**
     * @param incompleteInstruments the incompleteInstruments to set
     */
    public void setIncompleteInstruments(final Set<Instrument> incompleteInstruments) {
        this.incompleteInstruments = incompleteInstruments;
    }

    /**
     * Calculates the hashCode of a Scan.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((executionStatus == null) ? 0 : executionStatus.hashCode());
        result = prime * result + ((completionStatus == null) ? 0 : completionStatus.hashCode());
        result = prime * result + ((lastScan == null) ? 0 : lastScan.hashCode());
        result = prime * result + ((lists == null) ? 0 : lists.hashCode());
        result = prime * result + ((incompleteInstruments == null) ? 0 : incompleteInstruments.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((progress == null) ? 0 : progress.hashCode());
        return result;
    }

    /**
     * Indicates whether some other Scan is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        ScanValidator validator = new ScanValidator(this);

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Scan other = (Scan) obj;

        if (!validator.areAttributesEqual(other)) {
            return false;
        }

        if (!validator.areListsEqual(other)) {
            return false;
        }

        if (!validator.areIncompleteInstrumentsEqual(other)) {
            return false;
        }

        return true;
    }

    /**
     * Gets the list with the given id.
     *
     * @param listId The id of the List.
     * @return The list with the given id, if found.
     */
    public List getListWithId(final Integer listId) {
        for (List tempList : this.lists) {
            if (tempList.getId().equals(listId)) {
                return tempList;
            }
        }

        return null;
    }

    /**
     * Gets the incomplete Instrument with the given id.
     *
     * @param instrumentId The id of the Instrument.
     * @return The Instrument with the given id, if found.
     */
    public Instrument getIncompleteInstrumentWithId(final Integer instrumentId) {
        for (Instrument tempInstrument : this.incompleteInstruments) {
            if (tempInstrument.getId().equals(instrumentId)) {
                return tempInstrument;
            }
        }

        return null;
    }

    /**
     * Adds a list to the Scan.
     *
     * @param list The list to be added.
     */
    public void addList(final List list) {
        this.lists.add(list);
    }

    /**
     * Adds an incomplete Instrument to the Scan.
     *
     * @param instrument The Instrument to be added.
     */
    public void addIncompleteInstrument(final Instrument instrument) {
        this.incompleteInstruments.add(instrument);
    }

    /**
     * Provides all instruments based on the lists that are defined in the scan.
     *
     * @return All instruments based on the lists that are defined in the scan.
     */
    @JsonIgnore
    public Set<Instrument> getInstrumentsFromScanLists() {
        Set<Instrument> instruments = new HashSet<>();
        Iterator<List> listIterator = this.getLists().iterator();
        List tempList;

        while (listIterator.hasNext()) {
            tempList = listIterator.next();
            instruments.addAll(tempList.getInstruments());
        }

        return instruments;
    }

    /**
     * Checks if the scan has the List with the given ID.
     *
     * @param listId The ID of the List.
     * @return true, if Scan has List with ID; false, otherwise.
     */
    public boolean hasListWithId(final Integer listId) {
        Iterator<List> listIterator = this.getLists().iterator();
        List list;

        while (listIterator.hasNext()) {
            list = listIterator.next();

            if (list.getId().equals(listId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Validates the scan.
     *
     * @throws Exception In case a general validation error occurred.
     */
    public void validate() throws Exception {
        ScanValidator validator = new ScanValidator(this);
        validator.validate();
    }
}
