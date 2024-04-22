package backend.model.dashboard;

import java.util.Date;

/**
 * The MarketHealthStatus contains multiple attributes that provide information about the current health of the given
 * market. A market is typically an Industry Group or sector that contains a list of stocks.
 *
 * @author Michael
 */
public class MarketHealthStatus {
    /**
     * The symbol of the Instrument.
     */
    private String symbol;

    /**
     * The name of the Instrument.
     */
    private String name;

    /**
     * The date on which the MarketHealthStatus is based. This is the date of the most recent Quotation of the given
     * symbol.
     */
    private Date date;

    /**
     * The traffic-light status of the Swingtrading Environment.
     */
    private SwingTradingEnvironmentStatus swingTradingEnvironmentStatus;

    /**
     * The sum of Distribution Days within the last 25 trading days.
     */
    private int distributionDaysSum;

    /**
     * The ratio of the volume between up-days and down-days.
     */
    private float upDownVolumeRatio;

    /**
     * The relative strength percentile of the instrument in relation to a set of other instruments. This is a measure
     * of the price performance over a period of 3 months, 6 months, 9 months and 12 months.
     */
    private int rsNumber;

    /**
     * The number of instruments trading within 5% of the 52-week high.
     */
    private int numberNear52wHigh;

    /**
     * The number of instruments trading within 5% of the 52-week low.
     */
    private int numberNear52wLow;

    /**
     * The number of instruments that have a 5-day performance of at least 10% with a volume that is at least 25% above
     * the 30-day average.
     */
    private int numberUpOnVolume;

    /**
     * The number of instruments that have a 5-day performance of -10% or lower with a volume that is at least 25% above
     * the 30-day average.
     */
    private int numberDownOnVolume;

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(final String symbol) {
        this.symbol = symbol;
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
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * @return the swingTradingEnvironmentStatus
     */
    public SwingTradingEnvironmentStatus getSwingTradingEnvironmentStatus() {
        return swingTradingEnvironmentStatus;
    }

    /**
     * @param swingTradingEnvironmentStatus the swingTradingEnvironmentStatus to set
     */
    public void setSwingTradingEnvironmentStatus(final SwingTradingEnvironmentStatus swingTradingEnvironmentStatus) {
        this.swingTradingEnvironmentStatus = swingTradingEnvironmentStatus;
    }

    /**
     * @return the distributionDaysSum
     */
    public int getDistributionDaysSum() {
        return distributionDaysSum;
    }

    /**
     * @param distributionDaysSum the distributionDaysSum to set
     */
    public void setDistributionDaysSum(final int distributionDaysSum) {
        this.distributionDaysSum = distributionDaysSum;
    }

    /**
     * @return the upDownVolumeRatio
     */
    public float getUpDownVolumeRatio() {
        return upDownVolumeRatio;
    }

    /**
     * @param upDownVolumeRatio the upDownVolumeRatio to set
     */
    public void setUpDownVolumeRatio(final float upDownVolumeRatio) {
        this.upDownVolumeRatio = upDownVolumeRatio;
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
     * @return the numberNear52wHigh
     */
    public int getNumberNear52wHigh() {
        return numberNear52wHigh;
    }

    /**
     * @param numberNear52wHigh the numberNear52wHigh to set
     */
    public void setNumberNear52wHigh(final int numberNear52wHigh) {
        this.numberNear52wHigh = numberNear52wHigh;
    }

    /**
     * @return the numberNear52wLow
     */
    public int getNumberNear52wLow() {
        return numberNear52wLow;
    }

    /**
     * @param numberNear52wLow the numberNear52wLow to set
     */
    public void setNumberNear52wLow(final int numberNear52wLow) {
        this.numberNear52wLow = numberNear52wLow;
    }

    /**
     * @return the numberUpOnVolume
     */
    public int getNumberUpOnVolume() {
        return numberUpOnVolume;
    }

    /**
     * @param numberUpOnVolume the numberUpOnVolume to set
     */
    public void setNumberUpOnVolume(final int numberUpOnVolume) {
        this.numberUpOnVolume = numberUpOnVolume;
    }

    /**
     * @return the numberDownOnVolume
     */
    public int getNumberDownOnVolume() {
        return numberDownOnVolume;
    }

    /**
     * @param numberDownOnVolume the numberDownOnVolume to set
     */
    public void setNumberDownOnVolume(final int numberDownOnVolume) {
        this.numberDownOnVolume = numberDownOnVolume;
    }
}
