package backend.model.instrument;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
     * The daily Bollinger Band Width (10,2).
     */
    @Column(name = "BBW_10_DAYS")
    private float bollingerBandWidth10Days;

    /**
     * The weekly Bollinger Band Width (10,2).
     */
    @Column(name = "BBW_10_WEEKS")
    private float bollingerBandWidth10Weeks;

    /**
     * The Bollinger Band Width (10,2) threshold for which 25% of all daily values are less or equal within the trading
     * history.
     */
    @Column(name = "BBW_10_THRESHOLD_25_PERCENT")
    private float bbw10Threshold25Percent;

    /**
     * The difference in percent between the average volume of the last 5 days compared to the SMA(30) of the volume.
     */
    @Column(name = "VOLUME_DIFFERENTIAL_5_DAYS")
    private float volumeDifferential5Days;

    /**
     * The length of the most recent consolidation in weeks, beginning at the most recent 52-week high.
     */
    @Column(name = "BASE_LENGTH_WEEKS")
    private int baseLengthWeeks;

    /**
     * The ratio of the volume between up-days and down-days. The period is 50 days.
     */
    @Column(name = "UD_VOL_RATIO")
    private float upDownVolumeRatio;

    /**
     * The ratio of the performance * volume between up-days and down-days. The period is 30 days.
     */
    @Column(name = "ACC_DIS_RATIO_30_DAYS")
    private float accDisRatio30Days;

    /**
     * The ratio of the performance * volume between up-days and down-days. The period is 63 days (3 months).
     */
    @Column(name = "ACC_DIS_RATIO_63_DAYS")
    private float accDisRatio63Days;

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
     * The Average True Range Percent during the last 20 trading days.
     */
    @Column(name = "ATRP_20_DAYS")
    private float averageTrueRangePercent20;

    /**
     * The Slow Stochastic - 14 day period.
     */
    @Transient
    private float slowStochastic14Days;

    /**
     * Default constructor.
     */
    public Indicator() {

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
     * @return the bollingerBandWidth10Days
     */
    public float getBollingerBandWidth10Days() {
        return bollingerBandWidth10Days;
    }

    /**
     * @param bollingerBandWidth10Days the bollingerBandWidth10Days to set
     */
    public void setBollingerBandWidth10Days(final float bollingerBandWidth10Days) {
        this.bollingerBandWidth10Days = bollingerBandWidth10Days;
    }

    /**
     * @return the bollingerBandWidth10Weeks
     */
    public float getBollingerBandWidth10Weeks() {
        return bollingerBandWidth10Weeks;
    }

    /**
     * @param bollingerBandWidth10Weeks the bollingerBandWidth10Weeks to set
     */
    public void setBollingerBandWidth10Weeks(final float bollingerBandWidth10Weeks) {
        this.bollingerBandWidth10Weeks = bollingerBandWidth10Weeks;
    }

    /**
     * @return the bbw10Threshold25Percent
     */
    public float getBbw10Threshold25Percent() {
        return bbw10Threshold25Percent;
    }

    /**
     * @param bbw10Threshold25Percent the bbw10Threshold25Percent to set
     */
    public void setBbw10Threshold25Percent(final float bbw10Threshold25Percent) {
        this.bbw10Threshold25Percent = bbw10Threshold25Percent;
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
     * @return the accDisRatio30Days
     */
    public float getAccDisRatio30Days() {
        return accDisRatio30Days;
    }

    /**
     * @param accDisRatio30Days the accDisRatio30Days to set
     */
    public void setAccDisRatio30Days(final float accDisRatio30Days) {
        this.accDisRatio30Days = accDisRatio30Days;
    }

    /**
     * @return the accDisRatio63Days
     */
    public float getAccDisRatio63Days() {
        return accDisRatio63Days;
    }

    /**
     * @param accDisRatio63Days the accDisRatio63Days to set
     */
    public void setAccDisRatio63Days(final float accDisRatio63Days) {
        this.accDisRatio63Days = accDisRatio63Days;
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
     * @return the averageTrueRangePercent20
     */
    public float getAverageTrueRangePercent20() {
        return averageTrueRangePercent20;
    }

    /**
     * @param averageTrueRangePercent20 the averageTrueRangePercent20 to set
     */
    public void setAverageTrueRangePercent20(final float averageTrueRangePercent20) {
        this.averageTrueRangePercent20 = averageTrueRangePercent20;
    }

    /**
     * @return the slowStochastic14Days
     */
    public float getSlowStochastic14Days() {
        return slowStochastic14Days;
    }

    /**
     * @param slowStochastic14Days the slowStochastic14Days to set
     */
    public void setSlowStochastic14Days(final float slowStochastic14Days) {
        this.slowStochastic14Days = slowStochastic14Days;
    }

    /**
     * Calculates the hashCode of an Indicator.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bollingerBandWidth10Days, bollingerBandWidth10Weeks, distanceTo52WeekHigh,
                distanceTo52WeekLow, id, volumeDifferential5Days, baseLengthWeeks, upDownVolumeRatio, accDisRatio30Days,
                performance5Days, liquidity20Days, averageTrueRangePercent20, bbw10Threshold25Percent);
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
        return Float.floatToIntBits(bollingerBandWidth10Days) == Float.floatToIntBits(other.bollingerBandWidth10Days)
                && Float.floatToIntBits(bollingerBandWidth10Weeks) == Float
                        .floatToIntBits(other.bollingerBandWidth10Weeks)
                && Float.floatToIntBits(distanceTo52WeekHigh) == Float.floatToIntBits(other.distanceTo52WeekHigh)
                && Float.floatToIntBits(distanceTo52WeekLow) == Float.floatToIntBits(other.distanceTo52WeekLow)
                && Objects.equals(id, other.id)
                && Float.floatToIntBits(volumeDifferential5Days) == Float.floatToIntBits(other.volumeDifferential5Days)
                && baseLengthWeeks == other.baseLengthWeeks && upDownVolumeRatio == other.upDownVolumeRatio
                && performance5Days == other.performance5Days && liquidity20Days == other.liquidity20Days
                && averageTrueRangePercent20 == other.averageTrueRangePercent20
                && accDisRatio30Days == other.accDisRatio30Days
                && bbw10Threshold25Percent == other.bbw10Threshold25Percent;
    }
}
