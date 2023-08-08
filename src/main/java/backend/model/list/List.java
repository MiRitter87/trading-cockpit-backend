package backend.model.list;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import backend.model.NoItemsException;
import backend.model.instrument.Instrument;

/**
 * A list of instruments.
 *
 * For example this can be a personal Watchlist or all stocks of an ETF or index.
 *
 * @author Michael
 */
@Table(name = "LIST")
@Entity
@SequenceGenerator(name = "listSequence", initialValue = 1, allocationSize = 1)
public class List {
    /**
     * The maximum name field length allowed.
     */
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * The maximum description field length allowed.
     */
    private static final int MAX_DESCRIPTION_LENGTH = 250;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listSequence")
    @Column(name = "LIST_ID")
    @Min(value = 1, message = "{list.id.min.message}")
    private Integer id;

    /**
     * The name.
     */
    @Column(name = "NAME", length = MAX_NAME_LENGTH)
    @NotNull(message = "{list.name.notNull.message}")
    @Size(min = 1, max = MAX_NAME_LENGTH, message = "{list.name.size.message}")
    private String name;

    /**
     * The description.
     */
    @Column(name = "DESCRIPTION", length = MAX_DESCRIPTION_LENGTH)
    @Size(min = 0, max = MAX_DESCRIPTION_LENGTH, message = "{list.description.size.message}")
    private String description;

    /**
     * The instruments of the list.
     */
    @ManyToMany
    @JoinTable(name = "LIST_INSTRUMENT", joinColumns = { @JoinColumn(name = "LIST_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "INSTRUMENT_ID") })
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
     * @return the instruments
     */
    public Set<Instrument> getInstruments() {
        return instruments;
    }

    /**
     * @param instruments the instruments to set
     */
    public void setInstruments(final Set<Instrument> instruments) {
        this.instruments = instruments;
    }

    /**
     * Adds an instrument to the list.
     *
     * @param instrument The instrument to be added.
     */
    public void addInstrument(final Instrument instrument) {
        this.instruments.add(instrument);
    }

    /**
     * Removes the given Instrument from the List.
     *
     * @param instrument The Instrument to be removed.
     */
    public void removeInstrument(final Instrument instrument) {
        this.instruments.remove(instrument);
    }

    /**
     * Calculates the hashCode of a List.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((instruments == null) ? 0 : instruments.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * Indicates whether some other List is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        List other = (List) obj;
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
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (!this.areInstrumentsEqual(other)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the list of instruments is equal.
     *
     * @param other The other list for comparison.
     * @return true, if instruments are equal; false otherwise.
     */
    private boolean areInstrumentsEqual(final List other) {
        if (this.instruments == null && other.instruments != null) {
            return false;
        }

        if (this.instruments != null && other.instruments == null) {
            return false;
        }

        if (this.instruments.size() != other.instruments.size()) {
            return false;
        }

        for (Instrument tempInstrument : this.instruments) {
            Instrument otherInstrument = other.getInstrumentWithId(tempInstrument.getId());

            if (otherInstrument == null) {
                return false;
            }

            if (!tempInstrument.equals(otherInstrument)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the instrument with the given id.
     *
     * @param instrumentId The id of the instrument.
     * @return The instrument with the given id, if found.
     */
    public Instrument getInstrumentWithId(final Integer instrumentId) {
        for (Instrument tempInstrument : this.instruments) {
            if (tempInstrument.getId().equals(instrumentId)) {
                return tempInstrument;
            }
        }

        return null;
    }

    /**
     * Validates the list.
     *
     * @throws Exception In case a general validation error occurred.
     */
    public void validate() throws Exception {
        this.validateAnnotations();
        this.validateAdditionalCharacteristics();
    }

    /**
     * Validates the list according to the annotations of the validation framework.
     *
     * @exception Exception In case the validation failed.
     */
    private void validateAnnotations() throws Exception {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure()
                .constraintExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.BEAN_METHODS)
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<List>> violations = validator.validate(this);

        for (ConstraintViolation<List> violation : violations) {
            throw new Exception(violation.getMessage());
        }
    }

    /**
     * Validates additional characteristics of the list besides annotations.
     *
     * @throws NoItemsException Indicates that the list has no instruments defined.
     */
    private void validateAdditionalCharacteristics() throws NoItemsException {
        this.validateInstrumentsDefined();
    }

    /**
     * Checks if instruments are defined.
     *
     * @throws NoItemsException If no instruments are defined.
     */
    private void validateInstrumentsDefined() throws NoItemsException {
        if (this.instruments == null || this.instruments.size() == 0) {
            throw new NoItemsException();
        }
    }
}
