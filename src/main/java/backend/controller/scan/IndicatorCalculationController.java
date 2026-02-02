package backend.controller.scan;

import java.util.List;

import backend.calculator.AverageTrueRangeCalculator;
import backend.calculator.BollingerCalculator;
import backend.calculator.IndicatorCalculator;
import backend.calculator.MovingAverageCalculator;
import backend.calculator.PerformanceCalculator;
import backend.calculator.RelativeStrengthCalculator;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.RelativeStrengthData;

/**
 * This controller arranges the calculation of sets of indicators during multiple parts of the application.
 *
 * @author Michael
 */
public class IndicatorCalculationController {
    /**
     * Number of days used to calculate the EMA(10).
     */
    private static final int DAYS_EMA10 = 10;

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
     * Number of weeks used to calculate the Bollinger BandWidth(10,2).
     */
    private static final int WEEKS_BBW10 = 10;

    /**
     * The percent threshold for Bollinger BandWidth(10,2) values.
     */
    private static final int BBW_THRESHOLD_25_PERCENT = 25;

    /**
     * Number of days used for the long period of the volume differential.
     */
    private static final int DAYS_LONG_PERIOD_VOLUME_DIFFERENTIAL = 30;

    /**
     * Number of days used for 5-day period of the volume differential.
     */
    private static final int DAYS_VOLUME_DIFFERENTIAL_5 = 5;

    /**
     * Number of days used to calculate the Up/Down Volume Ratio.
     */
    private static final int DAYS_UP_DOWN_VOLUME_RATIO = 50;

    /**
     * Number of days used to calculate the 30-day Accumulation/Distribution Ratio.
     */
    private static final int DAYS_ACC_DIS_RATIO_30 = 30;

    /**
     * Number of days used to calculate the 63-day Accumulation/Distribution Ratio.
     */
    private static final int DAYS_ACC_DIS_RATIO_63 = 63;

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
     * Number of days used to calculate the Average True Range Percent.
     */
    private static final int DAYS_ATRP_20 = 20;

    /**
     * Calculator for Bollinger indicators.
     */
    private BollingerCalculator bollingerCalculator;

    /**
     * Calculator for Average True Range Percent.
     */
    private AverageTrueRangeCalculator averageTrueRangeCalculator;

    /**
     * Calculator for relative strength values.
     */
    private RelativeStrengthCalculator relativeStrengthCalculator;

    /**
     * Calculator for price performance.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Calculator for moving averages of price and volume.
     */
    private MovingAverageCalculator movingAverageCalculator;

    /**
     * Calculator for indicators.
     */
    private IndicatorCalculator indicatorCalculator;

    /**
     * Initializes the IndicatorCalculationController.
     */
    public IndicatorCalculationController() {
        this.bollingerCalculator = new BollingerCalculator();
        this.averageTrueRangeCalculator = new AverageTrueRangeCalculator();
        this.relativeStrengthCalculator = new RelativeStrengthCalculator();
        this.performanceCalculator = new PerformanceCalculator();
        this.movingAverageCalculator = new MovingAverageCalculator();
        this.indicatorCalculator = new IndicatorCalculator();
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

        if (mostRecent) {
            if (quotation.getIndicator() == null) {
                indicator = new Indicator();
                quotation.setIndicator(indicator);
            } else {
                indicator = quotation.getIndicator();
            }

            quotation.setRelativeStrengthData(new RelativeStrengthData());
            this.calculateMostRecentIndicators(indicator, quotation, sortedQuotations);
        }

        this.initMovingAverageData(quotation, sortedQuotations);
        this.calculateHistoricalIndicators(quotation, sortedQuotations);

        return quotation;
    }

    /**
     * Calculates the various RS numbers for each Quotation.
     *
     * @param quotations The quotations on which the calculation of the RS numbers is based.
     */
    public void calculateRsNumbers(final List<Quotation> quotations) {
        this.relativeStrengthCalculator.calculateRsNumber(quotations);
        this.relativeStrengthCalculator.calculateRsNumberDistanceTo52wHigh(quotations);
        this.relativeStrengthCalculator.calculateRsNumberAccDisRatio(quotations);
    }

    /**
     * Calculates the indicators that are only relevant for the most recent Quotation.
     *
     * @param indicator        The Indicator whose values are calculated.
     * @param sortedQuotations The quotations that build the trading history.
     * @param quotation        The Quotation for which indicators are calculated.
     */
    private void calculateMostRecentIndicators(final Indicator indicator, final Quotation quotation,
            final QuotationArray sortedQuotations) {

        QuotationArray weeklyQuotations = new QuotationArray(sortedQuotations.getWeeklyQuotations(quotation));
        weeklyQuotations.sortQuotationsByDate();

        quotation.getRelativeStrengthData()
                .setRsPercentSum(this.performanceCalculator.getRSPercentSum(quotation, sortedQuotations));
        indicator
                .setDistanceTo52WeekHigh(this.indicatorCalculator.getDistanceTo52WeekHigh(quotation, sortedQuotations));
        indicator.setDistanceTo52WeekLow(this.indicatorCalculator.getDistanceTo52WeekLow(quotation, sortedQuotations));
        indicator.setBollingerBandWidth10Days(
                this.bollingerCalculator.getBollingerBandWidth(DAYS_BBW10, 2, quotation, sortedQuotations));
        indicator.setBollingerBandWidth10Weeks(this.bollingerCalculator.getBollingerBandWidth(WEEKS_BBW10, 2,
                weeklyQuotations.getQuotations().get(0), weeklyQuotations));
        indicator.setBbw10Threshold25Percent(this.bollingerCalculator.getBollingerBandWidthThreshold(DAYS_BBW10, 2,
                BBW_THRESHOLD_25_PERCENT, quotation, sortedQuotations));
        indicator.setVolumeDifferential5Days(this.indicatorCalculator.getVolumeDifferential(
                DAYS_LONG_PERIOD_VOLUME_DIFFERENTIAL, DAYS_VOLUME_DIFFERENTIAL_5, quotation, sortedQuotations));
        indicator.setBaseLengthWeeks(this.indicatorCalculator.getBaseLengthWeeks(quotation, sortedQuotations));
        indicator.setUpDownVolumeRatio(
                this.indicatorCalculator.getUpDownVolumeRatio(DAYS_UP_DOWN_VOLUME_RATIO, quotation, sortedQuotations));
        indicator.setAccDisRatio30Days(this.indicatorCalculator.getAccumulationDistributionRatio(DAYS_ACC_DIS_RATIO_30,
                quotation, sortedQuotations));
        indicator.setAccDisRatio63Days(this.indicatorCalculator.getAccumulationDistributionRatio(DAYS_ACC_DIS_RATIO_63,
                quotation, sortedQuotations));
        indicator.setPerformance5Days(
                this.performanceCalculator.getPricePerformanceForDays(DAYS_PERFORMANCE_5, quotation, sortedQuotations));
        indicator.setLiquidity20Days(
                this.indicatorCalculator.getLiquidityForDays(DAYS_LIQUIDITY_20, quotation, sortedQuotations));
        indicator.setAverageTrueRangePercent20(
                this.averageTrueRangeCalculator.getAverageTrueRangePercent(DAYS_ATRP_20, quotation, sortedQuotations));
    }

    /**
     * Calculates the indicators that are relevant for historical quotations.
     *
     * @param quotation        The Quotation for which indicators are calculated.
     * @param sortedQuotations The quotations that build the trading history.
     */
    private void calculateHistoricalIndicators(final Quotation quotation, final QuotationArray sortedQuotations) {
        MovingAverageData maData = quotation.getMovingAverageData();

        if (maData == null) {
            return;
        }

        maData.setSma10(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA10, quotation, sortedQuotations));
        maData.setSma20(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA20, quotation, sortedQuotations));
        maData.setSma50(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA50, quotation, sortedQuotations));
        maData.setSma150(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA150, quotation, sortedQuotations));
        maData.setSma200(this.movingAverageCalculator.getSimpleMovingAverage(DAYS_SMA200, quotation, sortedQuotations));
        maData.setEma10(
                this.movingAverageCalculator.getExponentialMovingAverage(DAYS_EMA10, quotation, sortedQuotations));
        maData.setEma21(
                this.movingAverageCalculator.getExponentialMovingAverage(DAYS_EMA21, quotation, sortedQuotations));
        maData.setSma30Volume(this.movingAverageCalculator.getSimpleMovingAverageVolume(DAYS_SMA_VOLUME_30, quotation,
                sortedQuotations));
    }

    /**
     * Initializes the MovingAverageData of the Quotation based on the current quotation and the quotation history.
     * MovingAverageData are only initialized if the history is big enough to allow at least the calculation of the
     * shortest Simple Moving Average. In the actual case this is the SMA(10). Therefore at least 9 quotations have to
     * exist that are older than the given quotation.
     *
     * @param currentQuotation The current Quotation for which indicators are to be calculated.
     * @param sortedQuotations A list of quotations sorted by date that build the trading history
     */
    private void initMovingAverageData(final Quotation currentQuotation, final QuotationArray sortedQuotations) {
        int indexOfCurrentQuotation = sortedQuotations.getQuotations().indexOf(currentQuotation);
        int numberOfQuotations = sortedQuotations.getQuotations().size();
        final int minNumberOfQuotations = 10;

        if ((numberOfQuotations - indexOfCurrentQuotation) >= minNumberOfQuotations) {
            if (currentQuotation.getMovingAverageData() == null) {
                currentQuotation.setMovingAverageData(new MovingAverageData());
            }
        }
    }
}
