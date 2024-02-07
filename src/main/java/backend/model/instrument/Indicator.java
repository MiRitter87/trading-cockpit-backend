package backend.model.instrument;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Indicator data that are calculated based on quotations.
 *
 * @author Michael
 */
@Table(name = "INDICATOR")
@Entity
public class Indicator {
    /**
     * The ID.
     */
    @Id
    @Column(name = "INDICATOR_ID")
    private Integer id;

    /**
     * The stage of an instrument.
     */
    @Column(name = "STAGE")
    private int stage;

    /**
     * The distance in percent to the 52 week high of an instruments trading history.
     */
    @Column(name = "DISTANCE_TO_52_WEEK_HIGH")
    private float distanceTo52WeekHigh;

    /**
     * The distance in percent to the 52 week low of an instruments trading history.
     */
    @Column(name = "DISTANCE_TO_52_WEEK_LOW")
    private float distanceTo52WeekLow;

    /**
     * The Bollinger Band Width (10,2).
     */
    @Column(name = "BOLLINGER_BAND_WIDTH")
    private float bollingerBandWidth;

    /**
     * The difference in percent between the average volume of the last 5 days compared to the SMA(30) of the volume.
     */
    @Column(name = "VOLUME_DIFFERENTIAL_5_DAYS")
    private float volumeDifferential5Days;

    /**
     * The difference in percent between the average volume of the last 10 days compared to the SMA(30) of the volume.
     */
    @Column(name = "VOLUME_DIFFERENTIAL_10_DAYS")
    private float volumeDifferential10Days;

    /**
     * The length of the most recent consolidation in weeks, beginning at the most recent 52-week high.
     */
    @Column(name = "BASE_LENGTH_WEEKS")
    private int baseLengthWeeks;

    /**
     * The ratio of the volume between up-days and down-days.
     */
    @Column(name = "UD_VOL_RATIO")
    private float upDownVolumeRatio;

    /**
     * The percentage price gain during the last 5 trading days.
     */
    @Column(name = "PERFORMANCE_5_DAYS")
    private float performance5Days;

    /**
     * The average liquidity during the last 20 trading days. That is average volume multiplied by average price.
     */
    @Column(name = "LIQUIDITY_20_DAYS")
    private float liquidity20Days;

    /**
     * Relative strength data.
     */
    @OneToOne(targetEntity = RelativeStrengthData.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RS_DATA_ID", nullable = true)
    private RelativeStrengthData relativeStrengthData;

    /**
     * Moving average data.
     */
    @OneToOne(targetEntity = MovingAverageData.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MA_DATA_ID", nullable = true)
    private MovingAverageData movingAverageData;

    /**
     * Default constructor.
     */
    public Indicator() {

    }

    /**
     * Initializes the Indicator.
     *
     * @param withRelativeStrengthData Initialize indicators RelativeStrengthData, if true.
     * @param withMovingAverageData    Initialize indicators MovingAverageData, if true.
     */
    public Indicator(final boolean withRelativeStrengthData, final boolean withMovingAverageData) {
        if (withRelativeStrengthData) {
            this.relativeStrengthData = new RelativeStrengthData();
        }

        if (withMovingAverageData) {
            this.movingAverageData = new MovingAverageData();
        }
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
     * @return the stage
     */
    public int getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(final int stage) {
        this.stage = stage;
    }

    /**
     * @return the distanceTo52WeekHigh
     */
    public float getDistanceTo52WeekHigh() {
        return distanceTo52WeekHigh;
    }

    /**
     * @param distanceTo52WeekHigh the distanceTo52WeekHigh to set
     */
    public void setDistanceTo52WeekHigh(final float distanceTo52WeekHigh) {
        this.distanceTo52WeekHigh = distanceTo52WeekHigh;
    }

    /**
     * @return the distanceTo52WeekLow
     */
    public float getDistanceTo52WeekLow() {
        return distanceTo52WeekLow;
    }

    /**
     * @param distanceTo52WeekLow the distanceTo52WeekLow to set
     */
    public void setDistanceTo52WeekLow(final float distanceTo52WeekLow) {
        this.distanceTo52WeekLow = distanceTo52WeekLow;
    }

    /**
     * @return the bollingerBandWidth
     */
    public float getBollingerBandWidth() {
        return bollingerBandWidth;
    }

    /**
     * @param bollingerBandWidth the bollingerBandWidth to set
     */
    public void setBollingerBandWidth(final float bollingerBandWidth) {
        this.bollingerBandWidth = bollingerBandWidth;
    }

    /**
     * @return the volumeDifferential5days
     */
    public float getVolumeDifferential5Days() {
        return volumeDifferential5Days;
    }

    /**
     * @param volumeDifferential5Days the volumeDifferential5days to set
     */
    public void setVolumeDifferential5Days(final float volumeDifferential5Days) {
        this.volumeDifferential5Days = volumeDifferential5Days;
    }

    /**
     * @return the volumeDifferential10days
     */
    public float getVolumeDifferential10Days() {
        return volumeDifferential10Days;
    }

    /**
     * @param volumeDifferential10Days the volumeDifferential10days to set
     */
    public void setVolumeDifferential10Days(final float volumeDifferential10Days) {
        this.volumeDifferential10Days = volumeDifferential10Days;
    }

    /**
     * @return the baseLengthWeeks
     */
    public int getBaseLengthWeeks() {
        return baseLengthWeeks;
    }

    /**
     * @param baseLengthWeeks the baseLengthWeeks to set
     */
    public void setBaseLengthWeeks(final int baseLengthWeeks) {
        this.baseLengthWeeks = baseLengthWeeks;
    }

    /**
     * @return the upDownVolumeRatio
     */
    public float getUpDownVolumeRatio() {
        return upDownVolumeRatio;
    }

    /**
     * @param upDownVolumeRatio the udVolRatio to set
     */
    public void setUpDownVolumeRatio(final float upDownVolumeRatio) {
        this.upDownVolumeRatio = upDownVolumeRatio;
    }

    /**
     * @return the performance5Days
     */
    public float getPerformance5Days() {
        return performance5Days;
    }

    /**
     * @param performance5Days the performance5Days to set
     */
    public void setPerformance5Days(final float performance5Days) {
        this.performance5Days = performance5Days;
    }

    /**
     * @return the liquidity20Days
     */
    public float getLiquidity20Days() {
        return liquidity20Days;
    }

    /**
     * @param liquidity20Days the liquidity20Days to set
     */
    public void setLiquidity20Days(final float liquidity20Days) {
        this.liquidity20Days = liquidity20Days;
    }

    /**
     * @return the relativeStrengthData
     */
    public RelativeStrengthData getRelativeStrengthData() {
        return relativeStrengthData;
    }

    /**
     * @param relativeStrengthData the relativeStrengthData to set
     */
    public void setRelativeStrengthData(final RelativeStrengthData relativeStrengthData) {
        this.relativeStrengthData = relativeStrengthData;

        if (this.relativeStrengthData != null) {
            this.relativeStrengthData.setId(this.id);
        }
    }

    /**
     * @return the movingAverageData
     */
    public MovingAverageData getMovingAverageData() {
        return movingAverageData;
    }

    /**
     * @param movingAverageData the movingAverageData to set
     */
    public void setMovingAverageData(final MovingAverageData movingAverageData) {
        this.movingAverageData = movingAverageData;

        if (this.movingAverageData != null) {
            this.movingAverageData.setId(this.id);
        }
    }

    /**
     * Calculates the hashCode of an Indicator.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bollingerBandWidth, distanceTo52WeekHigh, distanceTo52WeekLow, id, stage,
                volumeDifferential5Days, volumeDifferential10Days, baseLengthWeeks, upDownVolumeRatio, performance5Days,
                liquidity20Days);
    }

    /**
     * Indicates whether some other Indicator is "equal to" this one.
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
        Indicator other = (Indicator) obj;
        return Float.floatToIntBits(bollingerBandWidth) == Float.floatToIntBits(other.bollingerBandWidth)
                && Float.floatToIntBits(distanceTo52WeekHigh) == Float.floatToIntBits(other.distanceTo52WeekHigh)
                && Float.floatToIntBits(distanceTo52WeekLow) == Float.floatToIntBits(other.distanceTo52WeekLow)
                && Objects.equals(id, other.id)
                && Float.floatToIntBits(volumeDifferential5Days) == Float.floatToIntBits(other.volumeDifferential5Days)
                && Float.floatToIntBits(volumeDifferential10Days) == Float
                        .floatToIntBits(other.volumeDifferential10Days)
                && stage == other.stage && baseLengthWeeks == other.baseLengthWeeks
                && upDownVolumeRatio == other.upDownVolumeRatio && performance5Days == other.performance5Days
                && liquidity20Days == other.liquidity20Days;
    }
}
