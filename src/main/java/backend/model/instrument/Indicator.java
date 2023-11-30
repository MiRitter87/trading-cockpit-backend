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
     * The exponential moving average price of the last 21 trading days.
     */
    @Column(name = "EMA21")
    private float ema21;

    /**
     * The simple moving average price of the last 10 trading days.
     */
    @Column(name = "SMA10")
    private float sma10;

    /**
     * The simple moving average price of the last 20 trading days.
     */
    @Column(name = "SMA20")
    private float sma20;

    /**
     * The simple moving average price of the last 50 trading days.
     */
    @Column(name = "SMA50")
    private float sma50;

    /**
     * The simple moving average price of the last 150 trading days.
     */
    @Column(name = "SMA150")
    private float sma150;

    /**
     * The simple moving average price of the last 200 trading days.
     */
    @Column(name = "SMA200")
    private float sma200;

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
     * The simple moving average volume of the last 30 trading days.
     */
    @Column(name = "SMA30_VOLUME")
    private long sma30Volume;

    /**
     * Relative strength data.
     */
    @OneToOne(targetEntity = RelativeStrengthData.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RS_DATA_ID")
    private RelativeStrengthData relativeStrengthData;

    /**
     * Default constructor.
     */
    public Indicator() {
        this.relativeStrengthData = new RelativeStrengthData();
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
     * @return the sma10
     */
    public float getSma10() {
        return sma10;
    }

    /**
     * @param sma10 the sma10 to set
     */
    public void setSma10(final float sma10) {
        this.sma10 = sma10;
    }

    /**
     * @return the sma20
     */
    public float getSma20() {
        return sma20;
    }

    /**
     * @param sma20 the sma20 to set
     */
    public void setSma20(final float sma20) {
        this.sma20 = sma20;
    }

    /**
     * @return the sma50
     */
    public float getSma50() {
        return sma50;
    }

    /**
     * @param sma50 the sma50 to set
     */
    public void setSma50(final float sma50) {
        this.sma50 = sma50;
    }

    /**
     * @return the sma150
     */
    public float getSma150() {
        return sma150;
    }

    /**
     * @param sma150 the sma150 to set
     */
    public void setSma150(final float sma150) {
        this.sma150 = sma150;
    }

    /**
     * @return the sma200
     */
    public float getSma200() {
        return sma200;
    }

    /**
     * @param sma200 the sma200 to set
     */
    public void setSma200(final float sma200) {
        this.sma200 = sma200;
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
     * @return the sma30Volume
     */
    public long getSma30Volume() {
        return sma30Volume;
    }

    /**
     * @param sma30Volume the sma30Volume to set
     */
    public void setSma30Volume(final long sma30Volume) {
        this.sma30Volume = sma30Volume;
    }

    /**
     * @return the ema21
     */
    public float getEma21() {
        return ema21;
    }

    /**
     * @param ema21 the ema21 to set
     */
    public void setEma21(final float ema21) {
        this.ema21 = ema21;
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
     * Calculates the hashCode of an Indicator.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bollingerBandWidth, distanceTo52WeekHigh, distanceTo52WeekLow, ema21, id, sma150, sma200,
                sma50, sma10, sma20, stage, volumeDifferential5Days, volumeDifferential10Days, baseLengthWeeks,
                upDownVolumeRatio, performance5Days, liquidity20Days, sma30Volume);
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
                && Objects.equals(id, other.id) && Float.floatToIntBits(sma150) == Float.floatToIntBits(other.sma150)
                && Float.floatToIntBits(sma200) == Float.floatToIntBits(other.sma200)
                && Float.floatToIntBits(sma50) == Float.floatToIntBits(other.sma50)
                && Float.floatToIntBits(sma10) == Float.floatToIntBits(other.sma10)
                && Float.floatToIntBits(sma20) == Float.floatToIntBits(other.sma20)
                && Float.floatToIntBits(ema21) == Float.floatToIntBits(other.ema21)
                && Float.floatToIntBits(volumeDifferential5Days) == Float.floatToIntBits(other.volumeDifferential5Days)
                && Float.floatToIntBits(volumeDifferential10Days) == Float
                        .floatToIntBits(other.volumeDifferential10Days)
                && stage == other.stage && baseLengthWeeks == other.baseLengthWeeks
                && upDownVolumeRatio == other.upDownVolumeRatio && performance5Days == other.performance5Days
                && liquidity20Days == other.liquidity20Days && sma30Volume == other.sma30Volume;
    }
}
