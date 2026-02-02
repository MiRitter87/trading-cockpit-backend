package backend.model.instrument;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Moving averages that are calculated based on price and volume of quotations.
 *
 * @author Michael
 */
@Table(name = "MOVING_AVERAGE_DATA")
@Entity
public class MovingAverageData {
    /**
     * The ID.
     */
    @Id
    @Column(name = "MA_DATA_ID")
    private Integer id;

    /**
     * The exponential moving average price of the last 10 trading days.
     */
    @Column(name = "EMA10")
    private float ema10;

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
     * The simple moving average volume of the last 30 trading days.
     */
    @Column(name = "SMA30_VOLUME")
    private long sma30Volume;

    /**
     * Default constructor.
     */
    public MovingAverageData() {

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
     * @return the ema10
     */
    public float getEma10() {
        return ema10;
    }

    /**
     * @param ema10 the ema10 to set
     */
    public void setEma10(final float ema10) {
        this.ema10 = ema10;
    }

    /**
     * Calculates the hashCode of MovingAverageData.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ema10, ema21, id, sma10, sma150, sma20, sma200, sma30Volume, sma50);
    }

    /**
     * Indicates whether some other MovingAverageData is "equal to" this one.
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
        MovingAverageData other = (MovingAverageData) obj;
        return Float.floatToIntBits(ema21) == Float.floatToIntBits(other.ema21) && Objects.equals(id, other.id)
                && Float.floatToIntBits(ema10) == Float.floatToIntBits(other.ema10)
                && Float.floatToIntBits(sma10) == Float.floatToIntBits(other.sma10)
                && Float.floatToIntBits(sma150) == Float.floatToIntBits(other.sma150)
                && Float.floatToIntBits(sma20) == Float.floatToIntBits(other.sma20)
                && Float.floatToIntBits(sma200) == Float.floatToIntBits(other.sma200)
                && sma30Volume == other.sma30Volume && Float.floatToIntBits(sma50) == Float.floatToIntBits(other.sma50);
    }
}
