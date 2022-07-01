package backend.model.scan;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.NoItemsException;
import backend.model.list.List;

/**
 * A scan that retrieves quotes and calculates indicators of multiple instrument lists.
 * 
 * @author Michael
 *
 */
@Table(name="SCAN")
@Entity
@SequenceGenerator(name = "scanSequence", initialValue = 1, allocationSize = 1)
public class Scan {
	/**
	 * The ID.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scanSequence")
	@Column(name="SCAN_ID")
	@Min(value = 1, message = "{scan.id.min.message}")
	private Integer id;
	
	/**
	 * The name.
	 */
	@Column(name="NAME", length = 50)
	@NotNull(message = "{scan.name.notNull.message}")
	@Size(min = 1, max = 50, message = "{scan.name.size.message}")
	private String name;
	
	/**
	 * The description.
	 */
	@Column(name="DESCRIPTION", length = 250)
	@Size(min = 0, max = 250, message = "{scan.description.size.message}")
	private String description;
	
	/**
	 * The date at which the scan has been started the last time.
	 */
	@Column(name="LAST_SCAN")
	private Date lastScan;
	
	/**
	 * The status.
	 */
	@Column(name="STATUS", length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "{scan.status.notNull.message}")
	private ScanStatus status;
	
	/**
	 * The percentage value indicating how much of the scan has been executed.
	 */
	@Column(name="PERCENT_COMPLETED")
	@Min(value = 0, message = "{scan.percentCompleted.min.message}")
	@Max(value = 100, message = "{scan.percentCompleted.max.message}")
	@NotNull(message = "{scan.percentCompleted.notNull.message}")
	private Integer percentCompleted;
	
	/**
	 * The lists whose instruments are scanned.
	 */
	@ManyToMany
	@JoinTable(name = "SCAN_LIST", 
    	joinColumns = { @JoinColumn(name = "SCAN_ID") }, 
    	inverseJoinColumns = { @JoinColumn(name = "LIST_ID") })
	private Set<List> lists;
	
	
	/**
	 * Default constructor.
	 */
	public Scan() {
		this.status = ScanStatus.FINISHED;
		this.percentCompleted = 0;
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
	 * @return the status
	 */
	public ScanStatus getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(ScanStatus status) {
		this.status = status;
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
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((lastScan == null) ? 0 : lastScan.hashCode());
		result = prime * result + ((lists == null) ? 0 : lists.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((percentCompleted == null) ? 0 : percentCompleted.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
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
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (status != other.status) {
			return false;
		}
		if (lastScan == null && other.lastScan != null)
			return false;
		if (lastScan != null && other.lastScan == null)
			return false;
		if(lastScan != null && other.lastScan != null) {
			if (lastScan.getTime() != other.lastScan.getTime())
				return false;
		}	
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (percentCompleted == null) {
			if (other.percentCompleted != null) {
				return false;
			}
		} else if (!percentCompleted.equals(other.percentCompleted)) {
			return false;
		}
		
		if(this.areListsEqual(other) == false)
			return false;
		
		return true;
	}
	
	
	/**
	 * Checks if the list of lists is equal.
	 * 
	 * @param other The other scan for comparison.
	 * @return true, if lists are equal; false otherwise.
	 */
	private boolean areListsEqual(Scan other) {
		if (this.lists == null && other.lists != null)
			return false;
		
		if (this.lists != null && other.lists == null)
			return false;
		
		if(this.lists.size() != other.lists.size())
			return false;
		
		for(List tempList:this.lists) {
			List otherList = other.getListWithId(tempList.getId());
			
			if(otherList == null)
				return false;
			
			if(!tempList.equals(otherList))
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Gets the list with the given id.
	 * 
	 * @param id The id of the list.
	 * @return The list with the given id, if found.
	 */
	public List getListWithId(Integer id) {
		for(List tempList:this.lists) {
			if(tempList.getId().equals(id))
				return tempList;
		}
		
		return null;
	}


	/**
	 * Adds a list to the scan.
	 * 
	 * @param list The list to be added.
	 */
	public void addList(final List list) {
		this.lists.add(list);
	}
	
	
	/**
	 * Validates the scan.
	 * 
	 * @throws Exception In case a general validation error occurred.
	 */
	public void validate() throws Exception {
		this.validateAnnotations();
		this.validateAdditionalCharacteristics();
	}
	
	
	/**
	 * Validates the scan according to the annotations of the validation framework.
	 * 
	 * @exception Exception In case the validation failed.
	 */
	private void validateAnnotations() throws Exception {
		ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)   
                .configure().constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

		Validator validator = validatorFactory.getValidator();
		Set<ConstraintViolation<Scan>> violations = validator.validate(this);

		for(ConstraintViolation<Scan> violation:violations) {
			throw new Exception(violation.getMessage());
		}
	}
	
	
	/**
	 * Validates additional characteristics of the scan besides annotations.
	 * 
	 * @throws NoItemsException Indicates that the scan has no lists defined.
	 */
	private void validateAdditionalCharacteristics() throws NoItemsException {
		this.validateListsDefined();
	}
	
	
	/**
	 * Checks if lists are defined.
	 * 
	 * @throws NoItemsException If no lists are defined.
	 */
	private void validateListsDefined() throws NoItemsException {
		if(this.lists == null || this.lists.size() == 0)
			throw new NoItemsException();
	}
}
