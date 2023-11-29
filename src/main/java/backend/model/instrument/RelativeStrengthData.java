package backend.model.instrument;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Relative Strength data that are calculated based on quotations and in some cases using Indicator data.
 *
 * @author Michael
 */
@Table(name = "RELATIVE_STRENGTH_DATA")
@Entity
public class RelativeStrengthData {
    /**
     * The ID.
     */
    @Id
    @Column(name = "RS_DATA_ID")
    private Integer id;

    /**
     * The RS number of the corresponding sector.
     */
    @Transient
    private int rsNumberSector;

    /**
     * The RS number of the corresponding industry group.
     */
    @Transient
    private int rsNumberIndustryGroup;

    /**
     * Composite RS number consisting of RS number of an Instrument and its referenced industry group.
     */
    @Transient
    private int rsNumberCompositeIg;

    /**
     * The Indicator this data belong to.
     */
    @Transient
    private Indicator indicator;

    /**
     * Default constructor.
     */
    public RelativeStrengthData() {

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
     * @return the rsNumberSector
     */
    public int getRsNumberSector() {
        return rsNumberSector;
    }

    /**
     * @param rsNumberSector the rsNumberSector to set
     */
    public void setRsNumberSector(final int rsNumberSector) {
        this.rsNumberSector = rsNumberSector;
    }

    /**
     * @return the rsNumberIndustryGroup
     */
    public int getRsNumberIndustryGroup() {
        return rsNumberIndustryGroup;
    }

    /**
     * @param rsNumberIndustryGroup the rsNumberIndustryGroup to set
     */
    public void setRsNumberIndustryGroup(final int rsNumberIndustryGroup) {
        this.rsNumberIndustryGroup = rsNumberIndustryGroup;
    }

    /**
     * @return the rsNumberCompositeIg
     */
    public int getRsNumberCompositeIg() {
        return rsNumberCompositeIg;
    }

    /**
     * @param rsNumberCompositeIg the rsNumberCompositeIg to set
     */
    public void setRsNumberCompositeIg(final int rsNumberCompositeIg) {
        this.rsNumberCompositeIg = rsNumberCompositeIg;
    }

    /**
     * @return the indicator
     */
    @JsonIgnore
    public Indicator getIndicator() {
        return indicator;
    }

    /**
     * @param indicator the indicator to set
     */
    public void setIndicator(final Indicator indicator) {
        this.indicator = indicator;
    }
}
