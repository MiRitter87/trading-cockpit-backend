package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import backend.model.Currency;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.RelativeStrengthData;

/**
 * Performs calculations of indicators based on the instruments quotations.
 *
 * @author Michael
 */
public class IndicatorCalculator {
    /**
     * The number of trading days per year.
     */
    private static final int TRADING_DAYS_PER_YEAR = 252;

    /**
     * The number of trading days per week.
     */
    private static final int TRADING_DAYS_PER_WEEK = 5;

    /**
     * Factor used to format results as percent.
     */
    private static final int HUNDRED_PERCENT = 100;

    /**
     * Number of days used to calculate the EMA(21).
     */
    private static final int DAYS_EMA21 = 21;

    /**
     * Number of days used to calculate the SMA(10).
     */
    private static final int DAYS_SMA10 = 10;

    /**
     * Number of days used to calculate the SMA(20).
     */
    private static final int DAYS_SMA20 = 20;

    /**
     * Number of days used to calculate the SMA(50).
     */
    private static final int DAYS_SMA50 = 50;

    /**
     * Number of days used to calculate the SMA(150).
     */
    private static final int DAYS_SMA150 = 150;

    /**
     * Number of days used to calculate the SMA(200).
     */
    private static final int DAYS_SMA200 = 200;

    /**
     * Number of days used to calculate the Bollinger BandWidth(10,2).
     */
    private static final int DAYS_BBW10 = 10;

    /**
     * Number of days used for the long period of the volume differential.
     */
    private static final int DAYS_LONG_PERIOD_VOLUME_DIFFERENTIAL = 30;

    /**
     * Number of days used for 5-day period of the volume differential.
     */
    private static final int DAYS_VOLUME_DIFFERENTIAL_5 = 5;

    /**
     * Number of days used for 10-day period of the volume differential.
     */
    private static final int DAYS_VOLUME_DIFFERENTIAL_10 = 10;

    /**
     * Number of days used to calculate the Up/Down Volume Ratio.
     */
    private static final int DAYS_UP_DOWN_VOLUME_RATIO = 50;

    /**
     * Number of days used to calculate the 5 day price performance.
     */
    private static final int DAYS_PERFORMANCE_5 = 5;

    /**
     * Number of days used to calculate the 20 day liquidity.
     */
    private static final int DAYS_LIQUIDITY_20 = 20;

    /**
     * Number of days used to calculate the SMA(30) of the volume.
     */
    private static final int DAYS_SMA_VOLUME_30 = 30;

    /**
     * Calculator for moving averages of price and volume.
     */
    private MovingAverageCalculator movingAverageCalculator;

    /**
     * Calculator for Bollinger indicators.
     */
    private BollingerCalculator bollingerCalculator;

    /**
     * Calculator for price performance.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Calculator for relative strength values.
     */
    private RelativeStrengthCalculator relativeStrengthCalculator;

    /**
     * Default constructor.
     */
    public IndicatorCalculator() {
        this.movingAverageCalculator = new MovingAverageCalculator();
        this.bollingerCalculator = new BollingerCalculator();
        this.performanceCalculator = new PerformanceCalculator();
        this.relativeStrengthCalculator = new RelativeStrengthCalculator();
    }

    /**
     * Calculates indicators for the given Quotation.
     *
     * @param instrument The Instrument to which the Quotation belongs to.
     * @param quotation  The Quotation for which indicators are calculated.
     * @param mostRecent Calculates all indicators if true (the most recent Quotation). Calculates only indicators
     *                   relevant for history if false.
     * @return The Quotation with the calculated indicators.
     */
    public Quotation calculateIndicators(final Instrument instrument, final Quotation quotation,
            final boolean mostRecent) {

        QuotationArray sortedQuotations = new QuotationArray(instrument.getQuotationsSortedByDate());
        Indicator indicator;

        if (quotation.getIndicator() == null) {
            indicator = new Indicator();
        } else {
            indicator = quotation.getIndicator();
        }

        this.initMovingAverageData(quotation, sortedQuotations, indicator);

        if (mostRecent) {
            indicator.setRelativeStrengthData(new RelativeStrengthData());
            this.calculateMostRecentIndicators(indicator, sortedQuotations, quotation);
        } else {
            this.calculateHistoricalIndicators(indicator, sortedQuotations, quotation);
        }

        quotation.setIndicator(indicator);

        return quotation;
    }

    /**
     * Calculates the indicators that are only relevant for the most recent Quotation.
     *
     * @param indicator        The Indicator whose values are calculated.
     * @param sortedQuotations The quotations that build the trading history.
     * @param quotation        The Quotation for which indicators are calculated.
     */
    private void calculateMostRecentIndicators(final Indicator indicator, final QuotationArray sortedQuotations,
            final Quotation quotation) {

        indicator.getRelativeStrengthData()
                .setRsPercentSum(this.performanceCalculator.getRSPercentSum(quotation, sortedQuotations));
        indicator.getMovingAverageData().setEma21(
                this.movingAverageCalculator.getExponentialMovingAverage(DAYS_EMA21, quotation, sortedQuotations));
        indicator.getMovingAverageData()
                .setSma10(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA10, quotation, sortedQuotations));
        indicator.getMovingAverageData()
                .setSma20(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA20, quotation, sortedQuotations));
        indicator.getMovingAverageData()
                .setSma50(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA50, quotation, sortedQuotations));
        indicator.getMovingAverageData().setSma150(
                this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA150, quotation, sortedQuotations));
        indicator.getMovingAverageData().setSma200(
                this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA200, quotation, sortedQuotations));
        indicator.setDistanceTo52WeekHigh(this.getDistanceTo52WeekHigh(quotation, sortedQuotations));
        indicator.setDistanceTo52WeekLow(this.getDistanceTo52WeekLow(quotation, sortedQuotations));
        indicator.setBollingerBandWidth(
                this.bollingerCalculator.getBollingerBandWidth(DAYS_BBW10, 2, quotation, sortedQuotations));
        indicator.setVolumeDifferential5Days(this.getVolumeDifferential(DAYS_LONG_PERIOD_VOLUME_DIFFERENTIAL,
                DAYS_VOLUME_DIFFERENTIAL_5, quotation, sortedQuotations));
        indicator.setVolumeDifferential10Days(this.getVolumeDifferential(DAYS_LONG_PERIOD_VOLUME_DIFFERENTIAL,
                DAYS_VOLUME_DIFFERENTIAL_10, quotation, sortedQuotations));
        indicator.setBaseLengthWeeks(this.getBaseLengthWeeks(quotation, sortedQuotations));
        indicator.setUpDownVolumeRatio(
                this.getUpDownVolumeRatio(DAYS_UP_DOWN_VOLUME_RATIO, quotation, sortedQuotations));
        indicator.setPerformance5Days(
                this.performanceCalculator.getPricePerformanceForDays(DAYS_PERFORMANCE_5, quotation, sortedQuotations));
        indicator.setLiquidity20Days(this.getLiquidityForDays(DAYS_LIQUIDITY_20, quotation, sortedQuotations));
        indicator.getMovingAverageData().setSma30Volume(this.movingAverageCalculator
                .getSimpleMovingAverageVolume(DAYS_SMA_VOLUME_30, quotation, sortedQuotations));
    }

    /**
     * Calculates the indicators that are relevant for historical quotations.
     *
     * @param indicator        The Indicator whose values are calculated.
     * @param sortedQuotations The quotations that build the trading history.
     * @param quotation        The Quotation for which indicators are calculated.
     */
    private void calculateHistoricalIndicators(final Indicator indicator, final QuotationArray sortedQuotations,
            final Quotation quotation) {

        if (indicator.getMovingAverageData() == null) {
            return;
        }

        indicator.getMovingAverageData()
                .setSma10(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA10, quotation, sortedQuotations));
        indicator.getMovingAverageData()
                .setSma20(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA20, quotation, sortedQuotations));
        indicator.getMovingAverageData()
                .setSma50(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA50, quotation, sortedQuotations));
        indicator.getMovingAverageData().setSma150(
                this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA150, quotation, sortedQuotations));
        indicator.getMovingAverageData().setSma200(
                this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA200, quotation, sortedQuotations));
        indicator.getMovingAverageData().setEma21(
                this.movingAverageCalculator.getExponentialMovingAverage(DAYS_EMA21, quotation, sortedQuotations));
        indicator.getMovingAverageData().setSma30Volume(this.movingAverageCalculator
                .getSimpleMovingAverageVolume(DAYS_SMA_VOLUME_30, quotation, sortedQuotations));
    }

    /**
     * Returns the distance of the current Quotation to the 52 week high.
     *
     * @param quotation        The current Quotation for which the distance to the 52 week high is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history
     * @return The distance of the quotation to the 52 week high.
     */
    public float getDistanceTo52WeekHigh(final Quotation quotation, final QuotationArray sortedQuotations) {
        Quotation tempQuotation;
        int indexOfQuotation = 0;
        BigDecimal highPrice52Weeks = new BigDecimal(0);
        BigDecimal percentDistance = new BigDecimal(0);
        final int scale = 4;

        // Get the starting point of 52 week high calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Get the highest price of the last 52 weeks.
        // If the trading history does not span a whole year, take all data available.
        for (int i = indexOfQuotation; i < (TRADING_DAYS_PER_YEAR + indexOfQuotation)
                && i < sortedQuotations.getQuotations().size(); i++) {
            tempQuotation = sortedQuotations.getQuotations().get(i);

            if (tempQuotation.getClose().compareTo(highPrice52Weeks) == 1) {
                highPrice52Weeks = tempQuotation.getClose();
            }
        }

        // Calculate the percent distance based on the quotation price and the 52 week high.
        percentDistance = quotation.getClose().divide(highPrice52Weeks, scale, RoundingMode.HALF_UP);
        percentDistance = percentDistance.subtract(BigDecimal.valueOf(1));
        percentDistance = percentDistance.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

        return percentDistance.floatValue();
    }

    /**
     * Returns the distance of the current Quotation to the 52 week low.
     *
     * @param quotation        The current Quotation for which the distance to the 52 week low is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history
     * @return The distance of the quotation to the 52 week low.
     */
    public float getDistanceTo52WeekLow(final Quotation quotation, final QuotationArray sortedQuotations) {
        Quotation tempQuotation;
        int indexOfQuotation = 0;
        BigDecimal lowPrice52Weeks = quotation.getClose();
        BigDecimal percentDistance = new BigDecimal(0);
        final int scale = 4;

        // Get the starting point of 52 week low calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Get the lowest price of the last 52 weeks.
        // If the trading history does not span a whole year, take all data available.
        for (int i = indexOfQuotation; i < (TRADING_DAYS_PER_YEAR + indexOfQuotation)
                && i < sortedQuotations.getQuotations().size(); i++) {
            tempQuotation = sortedQuotations.getQuotations().get(i);

            if (tempQuotation.getClose().compareTo(lowPrice52Weeks) == -1) {
                lowPrice52Weeks = tempQuotation.getClose();
            }
        }

        // Calculate the percent distance based on the quotation price and the 52 week low.
        percentDistance = quotation.getClose().divide(lowPrice52Weeks, scale, RoundingMode.HALF_UP);
        percentDistance = percentDistance.subtract(BigDecimal.valueOf(1));
        percentDistance = percentDistance.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

        return percentDistance.floatValue();
    }

    /**
     * Calculates the various RS numbers for each Quotation.
     *
     * @param quotations The quotations on which the calculation of the RS numbers is based.
     */
    public void calculateRsNumbers(final List<Quotation> quotations) {
        this.relativeStrengthCalculator.calculateRsNumber(quotations);
        this.relativeStrengthCalculator.calculateRsNumberDistanceTo52wHigh(quotations);
        this.relativeStrengthCalculator.calculateRsNumberUpDownVolumeRatio(quotations);
    }

    /**
     * Calculates the difference in percent between the average volume of two periods.
     *
     * @param daysPeriod1      The first period in days on which the Simple Moving Average Volume is based. Usually the
     *                         longer period.
     * @param daysPeriod2      The second period in days on which the Simple Moving Average Volume is based. Usually the
     *                         shorter period.
     * @param quotation        quotation The Quotation for which the volume differential is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for volume
     *                         differential calculation.
     * @return The volume differential.
     */
    public float getVolumeDifferential(final int daysPeriod1, final int daysPeriod2, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        BigDecimal averageVolumePeriod1;
        BigDecimal averageVolumePeriod2;
        BigDecimal volumeDifferential;
        final int scale = 4;

        averageVolumePeriod1 = new BigDecimal(
                this.movingAverageCalculator.getSimpleMovingAverageVolume(daysPeriod1, quotation, sortedQuotations));
        averageVolumePeriod2 = new BigDecimal(
                this.movingAverageCalculator.getSimpleMovingAverageVolume(daysPeriod2, quotation, sortedQuotations));

        if (averageVolumePeriod1.equals(new BigDecimal(0))) {
            return 0;
        }

        volumeDifferential = averageVolumePeriod2.divide(averageVolumePeriod1, scale, RoundingMode.HALF_UP);
        volumeDifferential = volumeDifferential.subtract(new BigDecimal(1));
        volumeDifferential = volumeDifferential.multiply(new BigDecimal(HUNDRED_PERCENT));

        return volumeDifferential.floatValue();
    }

    /**
     * Calculates the length of the most recent consolidation in weeks, beginning at the most recent 52-week high.
     *
     * @param quotation        The Quotation for which the base length is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for base length
     *                         calculation.
     * @return The base length in weeks.
     */
    public int getBaseLengthWeeks(final Quotation quotation, final QuotationArray sortedQuotations) {
        Quotation tempQuotation;
        Quotation quotation52WeekHigh;
        BigDecimal highPrice52Weeks = new BigDecimal(0);
        BigDecimal baseLengthWeeks = new BigDecimal(0);
        int indexOfQuotation = 0;
        int indexOf52WeekHigh = 0;
        int baseLengthDays;
        float performance;
        final int thresholdPercentOff52wHigh = -5;

        // Get the starting point of 52 week high calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Get index of 52w high based on quotation within history.
        // If the trading history does not span a whole year, take all data available.
        for (int i = indexOfQuotation; i < (TRADING_DAYS_PER_YEAR + indexOfQuotation)
                && i < sortedQuotations.getQuotations().size(); i++) {
            tempQuotation = sortedQuotations.getQuotations().get(i);

            if (tempQuotation.getClose().compareTo(highPrice52Weeks) == 1) {
                indexOf52WeekHigh = i;
                highPrice52Weeks = tempQuotation.getClose();
            }
        }

        quotation52WeekHigh = sortedQuotations.getQuotations().get(indexOf52WeekHigh);

        // Now check the days before the 52-week high.
        // Any day that closes within 5% of the absolute 52-week high is considered for calculation of base length, too.
        for (int i = indexOf52WeekHigh + 1; i < (TRADING_DAYS_PER_YEAR + indexOfQuotation)
                && i < sortedQuotations.getQuotations().size(); i++) {
            tempQuotation = sortedQuotations.getQuotations().get(i);
            performance = this.performanceCalculator.getPerformance(tempQuotation, quotation52WeekHigh);

            if (performance >= thresholdPercentOff52wHigh) {
                indexOf52WeekHigh = i;
            }
        }

        // Count number of days between quotation and extended 52-week high.
        baseLengthDays = indexOf52WeekHigh - indexOfQuotation;

        // Divide result by 5 to get number in weeks.
        baseLengthWeeks = new BigDecimal(baseLengthDays).divide(new BigDecimal(TRADING_DAYS_PER_WEEK), 0,
                RoundingMode.HALF_UP);

        return baseLengthWeeks.intValue();
    }

    /**
     * Calculates the volume ratio between up-days and down-days for the given number of days.
     *
     * @param days             The number of the last trading days that are taken into account for calculation.
     * @param quotation        The Quotation for which the U/D Volume Ratio is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for U/D Volume
     *                         Ratio calculation.
     * @return The U/D Volume Ratio.
     */
    public float getUpDownVolumeRatio(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        Quotation currentDayQuotation;
        Quotation previousDayQuotation;
        int indexOfQuotation = 0;
        long upVolumeSum = 0;
        long downVolumeSum = 0;
        BigDecimal upDownVolumeRatio;

        // Get the starting point of sum calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for sum calculation.
        // The -1 is needed because a up or down day can only be calculated against a previous day. Therefore an
        // additional Quotation has to exist.
        if ((sortedQuotations.getQuotations().size() - days - indexOfQuotation - 1) < 0) {
            return 0;
        }

        // Calculate the sum of the prices of the last x days.
        for (int i = indexOfQuotation; i < (days + indexOfQuotation); i++) {
            currentDayQuotation = sortedQuotations.getQuotations().get(i);
            previousDayQuotation = sortedQuotations.getQuotations().get(i + 1);

            if (currentDayQuotation.getClose().compareTo(previousDayQuotation.getClose()) == 1) {
                upVolumeSum = upVolumeSum + currentDayQuotation.getVolume();
            } else if (currentDayQuotation.getClose().compareTo(previousDayQuotation.getClose()) == -1) {
                downVolumeSum = downVolumeSum + currentDayQuotation.getVolume();
            }
        }

        if (downVolumeSum == 0) {
            return 0; // Prevent division by zero.
        }

        // Build the ratio.
        upDownVolumeRatio = new BigDecimal(upVolumeSum).divide(new BigDecimal(downVolumeSum), 2, RoundingMode.HALF_UP);

        return upDownVolumeRatio.floatValue();
    }

    /**
     * Provides the average trading liquidity for the given number of days.
     *
     * @param days             The number of days for liquidity calculation.
     * @param quotation        The Quotation for which the liquidity is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for liquidity
     *                         calculation.
     * @return The liquidity of the given interval.
     */
    public float getLiquidityForDays(final int days, final Quotation quotation, final QuotationArray sortedQuotations) {
        float averagePrice;
        float liquidity;
        long averageVolume;
        final int pencePerPound = 100;

        averagePrice = this.movingAverageCalculator.getSimpleMovingAverage(days, quotation, sortedQuotations);
        averageVolume = this.movingAverageCalculator.getSimpleMovingAverageVolume(days, quotation, sortedQuotations);

        liquidity = averagePrice * averageVolume;

        // Divide by 100 to convert price from pence to pounds.
        if (quotation.getCurrency() == Currency.GBP) {
            liquidity = liquidity / pencePerPound;
        }

        return liquidity;
    }

    /**
     * Initializes the MovingAverageData of the Indicator based on the current quotation and the quotation history.
     * MovingAverageData are only initialized if the history is big enough to allow at least the calculation of the
     * shortest Simple Moving Average. In the actual case this is the SMA(10). Therefore at least 9 quotations have to
     * exist that are older than the given quotation.
     *
     * @param currentQuotation The current Quotation for which indicators are to be calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history
     * @param indicator        The Indicator whose values are calculated.
     */
    private void initMovingAverageData(final Quotation currentQuotation, final QuotationArray sortedQuotations,
            final Indicator indicator) {

        int indexOfCurrentQuotation = sortedQuotations.getQuotations().indexOf(currentQuotation);
        int numberOfQuotations = sortedQuotations.getQuotations().size();
        final int minNumberOfQuotations = 10;

        if ((numberOfQuotations - indexOfCurrentQuotation) >= minNumberOfQuotations) {
            if (indicator.getMovingAverageData() == null) {
                indicator.setMovingAverageData(new MovingAverageData());
            }
        }
    }
}
