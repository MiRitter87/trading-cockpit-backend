package backend.model.instrument;

import java.math.BigDecimal;
import java.util.Objects;

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
     * The sum of an instruments performance in different time frames used for rsNumber calculation.
     */
    @Column(name = "RS_PERCENT_SUM")
    private float rsPercentSum;

    /**
     * The relative strength percentile of the instrument in relation to a set of other instruments. This is a measure
     * of the price performance over a period of 3 months, 6 months, 9 months and 12 months.
     */
    @Column(name = "RS_NUMBER")
    private int rsNumber;

    /**
     * The relative strength percentile of the instrument in relation to a set of other instruments. This is a measure
     * of the distance to the 52-week high.
     */
    @Column(name = "RS_NUMBER_DISTANCE_52W_HIGH")
    private int rsNumberDistance52WeekHigh;

    /**
     * The relative strength percentile of the instrument in relation to a set of other instruments. This is a measure
     * of the Up/Down Volume Ratio.
     */
    @Column(name = "RS_NUMBER_UD_VOL_RATIO")
    private int rsNumberUpDownVolumeRatio;

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
     * The price of the relative strength line.
     */
    @Transient
    private BigDecimal rsLinePrice;

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
     * @return the rsPercentSum
     */
    public float getRsPercentSum() {
        return rsPercentSum;
    }

    /**
     * @param rsPercentSum the rsPercentSum to set
     */
    public void setRsPercentSum(final float rsPercentSum) {
        this.rsPercentSum = rsPercentSum;
    }

    /**
     * @return the rsNumber
     */
    public int getRsNumber() {
        return rsNumber;
    }

    /**
     * @param rsNumber the rsNumber to set
     */
    public void setRsNumber(final int rsNumber) {
        this.rsNumber = rsNumber;
    }

    /**
     * @return the rsNumberDistance52WeekHigh
     */
    public int getRsNumberDistance52WeekHigh() {
        return rsNumberDistance52WeekHigh;
    }

    /**
     * @param rsNumberDistance52WeekHigh the rsNumberDistance52WeekHigh to set
     */
    public void setRsNumberDistance52WeekHigh(final int rsNumberDistance52WeekHigh) {
        this.rsNumberDistance52WeekHigh = rsNumberDistance52WeekHigh;
    }

    /**
     * @return the rsNumberUpDownVolumeRatio
     */
    public int getRsNumberUpDownVolumeRatio() {
        return rsNumberUpDownVolumeRatio;
    }

    /**
     * @param rsNumberUpDownVolumeRatio the rsNumberUpDownVolumeRatio to set
     */
    public void setRsNumberUpDownVolumeRatio(final int rsNumberUpDownVolumeRatio) {
        this.rsNumberUpDownVolumeRatio = rsNumberUpDownVolumeRatio;
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
     * @return the rsLinePrice
     */
    public BigDecimal getRsLinePrice() {
        return rsLinePrice;
    }

    /**
     * @param rsLinePrice the rsLinePrice to set
     */
    public void setRsLinePrice(final BigDecimal rsLinePrice) {
        this.rsLinePrice = rsLinePrice;
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

    /**
     * Calculates the hashCode of the RelativeStrengthData.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, rsNumber, rsNumberDistance52WeekHigh, rsNumberUpDownVolumeRatio, rsPercentSum);
    }

    /**
     * Indicates whether some other RelativeStrengthData is "equal to" this one.
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
        RelativeStrengthData other = (RelativeStrengthData) obj;
        return Objects.equals(id, other.id) && rsNumber == other.rsNumber
                && rsNumberDistance52WeekHigh == other.rsNumberDistance52WeekHigh
                && rsNumberUpDownVolumeRatio == other.rsNumberUpDownVolumeRatio
                && Float.floatToIntBits(rsPercentSum) == Float.floatToIntBits(other.rsPercentSum);
    }
}
