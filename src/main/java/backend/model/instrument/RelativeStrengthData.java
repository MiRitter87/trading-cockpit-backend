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
     * The RS number of the corresponding industry group.
     */
    @Transient
    private int rsNumberIndustryGroup;

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
