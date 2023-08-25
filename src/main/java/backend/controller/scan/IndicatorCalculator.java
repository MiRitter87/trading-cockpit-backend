package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import backend.model.Currency;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.QuotationRsPercentSumComparator;

/**
 * Performs calculations of indicators based on the instruments quotations.
 *
 * @author Michael
 */
public class IndicatorCalculator {
    /**
     * The number of trading days per month.
     */
    private static final int TRADING_DAYS_PER_MONTH = 21;

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
     * Calculator for moving averages of price and volume.
     */
    private MovingAverageCalculator movingAverageCalculator;

    /**
     * Calculator for Bollinger indicators.
     */
    private BollingerCalculator bollingerCalculator;

    /**
     * Default constructor.
     */
    public IndicatorCalculator() {
        this.movingAverageCalculator = new MovingAverageCalculator();
        this.bollingerCalculator = new BollingerCalculator();
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
        final int daysEma21 = 21;
        final int daysSma10 = 10;
        final int daysSma20 = 20;
        final int daysSma50 = 50;
        final int daysSma150 = 150;
        final int daysSma200 = 200;
        final int daysBbw = 10;
        final int daysLongPeriodVolumeDifferential = 30;
        final int daysVolumeDifferential5 = 5;
        final int daysVolumeDifferential10 = 10;
        final int daysUpDownVolumeRatio = 50;
        final int daysPerformance5 = 5;
        final int daysLiquidity20 = 20;
        final int daysSmaVolume30 = 30;

        if (quotation.getIndicator() == null) {
            indicator = new Indicator();
        } else {
            indicator = quotation.getIndicator();
        }

        if (mostRecent) {
            // These indicators are calculated only for the most recent Quotation.
            indicator.setRsPercentSum(this.getRSPercentSum(quotation, sortedQuotations));
            indicator.setEma21(
                    this.movingAverageCalculator.getExponentialMovingAverage(daysEma21, quotation, sortedQuotations));
            indicator.setSma10(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma10, quotation, sortedQuotations));
            indicator.setSma20(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma20, quotation, sortedQuotations));
            indicator.setSma50(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma50, quotation, sortedQuotations));
            indicator.setSma150(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma150, quotation, sortedQuotations));
            indicator.setSma200(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma200, quotation, sortedQuotations));
            indicator.setDistanceTo52WeekHigh(this.getDistanceTo52WeekHigh(quotation, sortedQuotations));
            indicator.setDistanceTo52WeekLow(this.getDistanceTo52WeekLow(quotation, sortedQuotations));
            indicator.setBollingerBandWidth(
                    this.bollingerCalculator.getBollingerBandWidth(daysBbw, 2, quotation, sortedQuotations));
            indicator.setVolumeDifferential5Days(this.getVolumeDifferential(daysLongPeriodVolumeDifferential,
                    daysVolumeDifferential5, quotation, sortedQuotations));
            indicator.setVolumeDifferential10Days(this.getVolumeDifferential(daysLongPeriodVolumeDifferential,
                    daysVolumeDifferential10, quotation, sortedQuotations));
            indicator.setBaseLengthWeeks(this.getBaseLengthWeeks(quotation, sortedQuotations));
            indicator.setUpDownVolumeRatio(
                    this.getUpDownVolumeRatio(daysUpDownVolumeRatio, quotation, sortedQuotations));
            indicator.setPerformance5Days(
                    this.getPricePerformanceForDays(daysPerformance5, quotation, sortedQuotations));
            indicator.setLiquidity20Days(this.getLiquidityForDays(daysLiquidity20, quotation, sortedQuotations));
            indicator.setSma30Volume(this.movingAverageCalculator.getSimpleMovingAverageVolume(daysSmaVolume30,
                    quotation, sortedQuotations));
        } else {
            // These indicators are calculated for historical quotations too.
            indicator.setSma10(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma10, quotation, sortedQuotations));
            indicator.setSma20(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma20, quotation, sortedQuotations));
            indicator.setSma50(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma50, quotation, sortedQuotations));
            indicator.setSma150(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma150, quotation, sortedQuotations));
            indicator.setSma200(
                    this.movingAverageCalculator.getSimpleMovingAverage(daysSma200, quotation, sortedQuotations));
            indicator.setEma21(
                    this.movingAverageCalculator.getExponentialMovingAverage(daysEma21, quotation, sortedQuotations));
            indicator.setSma30Volume(this.movingAverageCalculator.getSimpleMovingAverageVolume(daysSmaVolume30,
                    quotation, sortedQuotations));
        }

        quotation.setIndicator(indicator);

        return quotation;
    }

    /**
     * Calculates the percentage sum needed for calculation of the RS number.
     *
     * @param quotation        The quotation of the date on which the percentage sum is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for percentage
     *                         sum calculation.
     * @return The percentage sum.
     */
    public float getRSPercentSum(final Quotation quotation, final QuotationArray sortedQuotations) {
        int indexOfQuotation = 0;
        BigDecimal rsPercentSum = BigDecimal.valueOf(0);
        final int months3 = 3;
        final int months6 = 6;
        final int months9 = 9;
        final int months12 = 12;

        // Get the staring point of RS percent sum calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months3));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months3));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months6));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months9));
        rsPercentSum = rsPercentSum
                .add(this.getPerformanceOfIntervalForRS(sortedQuotations, indexOfQuotation, months12));

        rsPercentSum.setScale(2);

        return rsPercentSum.floatValue();
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
     * Calculates the RS number for each Quotation.
     *
     * @param quotations The quotations on which the calculation of the RS number is based.
     */
    public void calculateRsNumbers(final List<Quotation> quotations) {
        Indicator indicator;
        BigDecimal rsNumber;
        BigDecimal dividend;
        BigDecimal numberOfElements;

        Collections.sort(quotations, new QuotationRsPercentSumComparator());
        numberOfElements = BigDecimal.valueOf(quotations.size());

        for (int i = 0; i < quotations.size(); i++) {
            dividend = numberOfElements.subtract(BigDecimal.valueOf(i));
            rsNumber = dividend.divide(numberOfElements, 2, RoundingMode.HALF_UP);
            rsNumber = rsNumber.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

            indicator = quotations.get(i).getIndicator();
            if (indicator != null) {
                indicator.setRsNumber(rsNumber.intValue());
            }
        }
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
            performance = this.getPerformance(tempQuotation, quotation52WeekHigh);

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
     * Provides the price performance for the given number of days.
     *
     * @param days             The number of days for performance calculation.
     * @param quotation        The Quotation for which the price performance is calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history used for price
     *                         performance calculation.
     * @return The performance of the given interval in percent.
     */
    public float getPricePerformanceForDays(final int days, final Quotation quotation,
            final QuotationArray sortedQuotations) {
        BigDecimal divisionResult = BigDecimal.valueOf(0);
        int indexOfQuotation = 0;
        final int scale = 4;

        // Get the starting point of price performance calculation.
        indexOfQuotation = sortedQuotations.getQuotations().indexOf(quotation);

        // Check if enough quotations exist for price performance calculation.
        // The -1 is needed because a performance can only be calculated against a previous day. Therefore an additional
        // Quotation has to exist.
        if ((sortedQuotations.getQuotations().size() - days - indexOfQuotation - 1) < 0) {
            return 0;
        }

        divisionResult = sortedQuotations.getQuotations().get(indexOfQuotation).getClose().divide(
                sortedQuotations.getQuotations().get(indexOfQuotation + days).getClose(), scale, RoundingMode.HALF_UP);
        divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
        divisionResult = divisionResult.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

        return divisionResult.floatValue();
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
     * Calculates the price performance between the current Quotation and the previous Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The price performance.
     */
    public float getPerformance(final Quotation currentQuotation, final Quotation previousQuotation) {
        float performance;
        final int scale = 4;

        performance = currentQuotation.getClose().divide(previousQuotation.getClose(), scale, RoundingMode.HALF_UP)
                .floatValue() - 1;
        performance = performance * HUNDRED_PERCENT; // Get performance in percent.

        return performance;
    }

    /**
     * Provides the performance of a given interval for relative strength calculation.
     *
     * @param sortedQuotations The quotations containing date and price information for performance calculation.
     * @param indexOfQuotation The starting point from which the performance is calculated.
     * @param months           The number of months for performance calculation.
     * @return The performance of the given interval in percent.
     */
    private BigDecimal getPerformanceOfIntervalForRS(final QuotationArray sortedQuotations, final int indexOfQuotation,
            final int months) {
        BigDecimal divisionResult = BigDecimal.valueOf(0);
        // The offset -1 is used because most APIs only provide 252 data sets for a whole trading year.
        // Without the offset, 253 data sets would be needed to calculate the one year performance.
        int indexOfQuotationForInterval = indexOfQuotation + (TRADING_DAYS_PER_MONTH * months) - 1;
        final int scale = 4;

        if (indexOfQuotationForInterval >= sortedQuotations.getQuotations().size()) {
            return divisionResult;
        }

        divisionResult = sortedQuotations.getQuotations().get(indexOfQuotation).getClose().divide(
                sortedQuotations.getQuotations().get(indexOfQuotationForInterval).getClose(), scale,
                RoundingMode.HALF_UP);
        divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
        divisionResult = divisionResult.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

        return divisionResult;
    }
}
