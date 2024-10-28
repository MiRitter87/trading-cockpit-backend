package backend.model.statistic;

import java.util.Date;
import java.util.Objects;

import backend.model.LocalizedException;
import backend.model.instrument.InstrumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Statistical data of all instruments of an InstrumentType at a certain point in time.
 *
 * @author Michael
 */
@Table(name = "STATISTIC")
@Entity
@SequenceGenerator(name = "statisticSequence", initialValue = 1, allocationSize = 1)
public class Statistic {
    /**
     * The maximum InstrumentType field length allowed.
     */
    private static final int MAX_TYPE_LENGTH = 10;

    /**
     * The ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statisticSequence")
    @Column(name = "STATISTIC_ID")
    private Integer id;

    /**
     * The date.
     */
    @Column(name = "DATE")
    private Date date;

    /**
     * The InstrumentType of the instruments this Statistic is based on.
     */
    @Column(name = "INSTRUMENT_TYPE", length = MAX_TYPE_LENGTH)
    @Enumerated(EnumType.STRING)
    private InstrumentType instrumentType;

    /**
     * The ID of the sector this Statistic is based on.
     */
    @Column(name = "SECTOR_ID")
    private Integer sectorId;

    /**
     * The ID of the industry group this Statistic is based on.
     */
    @Column(name = "INDUSTRY_GROUP_ID")
    private Integer industryGroupId;

    /**
     * The total number of instruments this Statistic is based on.
     */
    @Column(name = "NUMBER_OF_INSTRUMENTS")
    private int numberOfInstruments;

    /**
     * The number of instruments which advanced since the last data point.
     */
    @Column(name = "NUMBER_ADVANCE")
    private int numberAdvance;

    /**
     * The number of instruments which declined since the last data point.
     */
    @Column(name = "NUMBER_DECLINE")
    private int numberDecline;

    /**
     * The number of advancing minus declining instruments since the last data point.
     */
    @Column(name = "ADVANCE_DECLINE_NUMBER")
    private int advanceDeclineNumber;

    /**
     * The number of instruments which are trading above the 50-day Simple Moving Average.
     */
    @Column(name = "NUMBER_ABOVE_SMA50")
    private int numberAboveSma50;

    /**
     * The number of instruments which are trading at or below the 50-day Simple Moving Average.
     */
    @Column(name = "NUMBER_AT_OR_BELOW_SMA50")
    private int numberAtOrBelowSma50;

    /**
     * The percentage of instruments which are trading above the 50-day Simple Moving Average.
     */
    @Column(name = "PERCENT_ABOVE_SMA50")
    private float percentAboveSma50;

    /**
     * The number of instruments which are trading above the 200-day Simple Moving Average.
     */
    @Column(name = "NUMBER_ABOVE_SMA200")
    private int numberAboveSma200;

    /**
     * The number of instruments which are trading at or below the 200-day Simple Moving Average.
     */
    @Column(name = "NUMBER_AT_OR_BELOW_SMA200")
    private int numberAtOrBelowSma200;

    /**
     * The percentage of instruments which are trading above the 200-day Simple Moving Average.
     */
    @Column(name = "PERCENT_ABOVE_SMA200")
    private float percentAboveSma200;

    /**
     * The number of the "Ritter Market Trend" indicator.
     */
    @Column(name = "NUMBER_RITTER_MARKET_TREND")
    private int numberRitterMarketTrend;

    /**
     * The number of instruments that traded up on volume.
     */
    @Column(name = "NUMBER_UP_ON_VOLUME")
    private int numberUpOnVolume;

    /**
     * The number of instruments that traded down on volume.
     */
    @Column(name = "NUMBER_DOWN_ON_VOLUME")
    private int numberDownOnVolume;

    /**
     * The number of instruments that made a bearish high-volume reversal.
     */
    @Column(name = "NUMBER_BEARISH_REVERSAL")
    private int numberBearishReversal;

    /**
     * The number of instruments that made a bullish high-volume reversal.
     */
    @Column(name = "NUMBER_BULLISH_REVERSAL")
    private int numberBullishReversal;

    /**
     * The number of instruments that are churning.
     */
    @Column(name = "NUMBER_CHURNING")
    private int numberChurning;

    /**
     * Default constructor.
     */
    public Statistic() {

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
     * @return the instrumentType
     */
    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    /**
     * @param instrumentType the instrumentType to set
     */
    public void setInstrumentType(final InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }

    /**
     * @return the sectorId
     */
    public Integer getSectorId() {
        return sectorId;
    }

    /**
     * @param sectorId the sectorId to set
     */
    public void setSectorId(final Integer sectorId) {
        this.sectorId = sectorId;
    }

    /**
     * @return the industryGroupId
     */
    public Integer getIndustryGroupId() {
        return industryGroupId;
    }

    /**
     * @param industryGroupId the industryGroupId to set
     */
    public void setIndustryGroupId(final Integer industryGroupId) {
        this.industryGroupId = industryGroupId;
    }

    /**
     * @return the numberAdvance
     */
    public int getNumberAdvance() {
        return numberAdvance;
    }

    /**
     * @param numberAdvance the numberAdvance to set
     */
    public void setNumberAdvance(final int numberAdvance) {
        this.numberAdvance = numberAdvance;

        this.advanceDeclineNumber = this.numberAdvance - this.numberDecline;
    }

    /**
     * @return the numberDecline
     */
    public int getNumberDecline() {
        return numberDecline;
    }

    /**
     * @param numberDecline the numberDecline to set
     */
    public void setNumberDecline(final int numberDecline) {
        this.numberDecline = numberDecline;

        this.advanceDeclineNumber = this.numberAdvance - this.numberDecline;
    }

    /**
     * @return the advanceDeclineNumber
     */
    public int getAdvanceDeclineNumber() {
        return advanceDeclineNumber;
    }

    /**
     * @return the numberAboveSma50
     */
    public int getNumberAboveSma50() {
        return numberAboveSma50;
    }

    /**
     * @param numberAboveSma50 the numberAboveSma50 to set
     */
    public void setNumberAboveSma50(final int numberAboveSma50) {
        this.numberAboveSma50 = numberAboveSma50;
        this.updatePercentAboveSma50();
    }

    /**
     * @return the numberAtOrBelowSma50
     */
    public int getNumberAtOrBelowSma50() {
        return numberAtOrBelowSma50;
    }

    /**
     * @param numberAtOrBelowSma50 the numberAtOrBelowSma50 to set
     */
    public void setNumberAtOrBelowSma50(final int numberAtOrBelowSma50) {
        this.numberAtOrBelowSma50 = numberAtOrBelowSma50;
        this.updatePercentAboveSma50();
    }

    /**
     * @return the percentAboveSma50
     */
    public float getPercentAboveSma50() {
        return percentAboveSma50;
    }

    /**
     * @return the numberAboveSma200
     */
    public int getNumberAboveSma200() {
        return numberAboveSma200;
    }

    /**
     * @param numberAboveSma200 the numberAboveSma200 to set
     */
    public void setNumberAboveSma200(final int numberAboveSma200) {
        this.numberAboveSma200 = numberAboveSma200;
        this.updatePercentAboveSma200();
    }

    /**
     * @return the numberAtOrBelowSma200
     */
    public int getNumberAtOrBelowSma200() {
        return numberAtOrBelowSma200;
    }

    /**
     * @param numberAtOrBelowSma200 the numberAtOrBelowSma200 to set
     */
    public void setNumberAtOrBelowSma200(final int numberAtOrBelowSma200) {
        this.numberAtOrBelowSma200 = numberAtOrBelowSma200;
        this.updatePercentAboveSma200();
    }

    /**
     * @return the percentAboveSma200
     */
    public float getPercentAboveSma200() {
        return percentAboveSma200;
    }

    /**
     * @return the numberRitterMarketTrend
     */
    public int getNumberRitterMarketTrend() {
        return numberRitterMarketTrend;
    }

    /**
     * @param numberRitterMarketTrend the numberRitterMarketTrend to set
     */
    public void setNumberRitterMarketTrend(final int numberRitterMarketTrend) {
        this.numberRitterMarketTrend = numberRitterMarketTrend;
    }

    /**
     * @return the numberOfInstruments
     */
    public int getNumberOfInstruments() {
        return numberOfInstruments;
    }

    /**
     * @param numberOfInstruments the numberOfInstruments to set
     */
    public void setNumberOfInstruments(final int numberOfInstruments) {
        this.numberOfInstruments = numberOfInstruments;
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

    /**
     * @return the numberBearishReversal
     */
    public int getNumberBearishReversal() {
        return numberBearishReversal;
    }

    /**
     * @param numberBearishReversal the numberBearishReversal to set
     */
    public void setNumberBearishReversal(final int numberBearishReversal) {
        this.numberBearishReversal = numberBearishReversal;
    }

    /**
     * @return the numberBullishReversal
     */
    public int getNumberBullishReversal() {
        return numberBullishReversal;
    }

    /**
     * @param numberBullishReversal the numberBullishReversal to set
     */
    public void setNumberBullishReversal(final int numberBullishReversal) {
        this.numberBullishReversal = numberBullishReversal;
    }

    /**
     * @return the numberChurning
     */
    public int getNumberChurning() {
        return numberChurning;
    }

    /**
     * @param numberChurning the numberChurning to set
     */
    public void setNumberChurning(final int numberChurning) {
        this.numberChurning = numberChurning;
    }

    /**
     * Updates the percentage above SMA(50).
     */
    private void updatePercentAboveSma50() {
        StatisticHelper helper = new StatisticHelper();
        this.percentAboveSma50 = helper.getPercentAboveSma50(this);
    }

    /**
     * Updates the percentage above SMA(200).
     */
    private void updatePercentAboveSma200() {
        StatisticHelper helper = new StatisticHelper();
        this.percentAboveSma200 = helper.getPercentAboveSma200(this);
    }

    /**
     * Calculates the hashCode of a Statistic.
     */
    @Override
    public int hashCode() {
        return Objects.hash(advanceDeclineNumber, date, id, industryGroupId, instrumentType, numberAboveSma50,
                numberAdvance, numberAtOrBelowSma50, numberDecline, numberOfInstruments, numberRitterMarketTrend,
                percentAboveSma50, numberAboveSma200, numberAtOrBelowSma200, percentAboveSma200, numberUpOnVolume,
                numberDownOnVolume, numberBearishReversal, numberBullishReversal, numberChurning, sectorId);
    }

    /**
     * Indicates whether some other Statistic is "equal to" this one.
     */
    @Override
    public boolean equals(final Object obj) {
        StatisticValidator validator = new StatisticValidator(this);
        return validator.isStatisticEqual(obj);
    }

    /**
     * Validates the Statistic.
     *
     * @throws LocalizedException A general exception containing a localized message.
     */
    public void validate() throws LocalizedException {
        StatisticValidator validator = new StatisticValidator(this);
        validator.validate();
    }
}
