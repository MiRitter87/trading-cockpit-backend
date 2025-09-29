package backend.controller.instrumentCheck;

import java.math.BigDecimal;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;

/**
 * This controller provides helper methods that are used for pattern-related Instrument checks.
 *
 * @author Michael
 */
public class PatternControllerHelper {
    /**
     * The performance threshold of an "Up on Volume"-day.
     */
    private static final float UP_PERFORMANCE_THRESHOLD = (float) 3.0;

    /**
     * The performance threshold of an "Down on Volume"-day.
     */
    private static final float DOWN_PERFORMANCE_THRESHOLD = (float) -3.0;

    /**
     * The threshold of the daily price range for bearish reversal calculation.
     */
    private static final float REVERSAL_THRESHOLD_BEARISH = (float) 0.4;

    /**
     * The threshold of the daily price range for bullish reversal calculation.
     */
    private static final float REVERSAL_THRESHOLD_BULLISH = (float) 0.6;

    /**
     * The upwards performance threshold of a "Churning"-day.
     */
    private static final float CHURNING_UP_THRESHOLD = (float) 1.0;

    /**
     * The downwards performance threshold of a "Churning"-day.
     */
    private static final float CHURNING_DOWN_THRESHOLD = (float) -1.0;

    /**
     * Performance calculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Initializes the PatternControllerHelper.
     */
    public PatternControllerHelper() {
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * Checks if the current Quotation has traded up by at least 3% on above-average volume against the previous
     * Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if currentQuotation traded up on volume; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isUpOnVolume(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
        float performance;
        MovingAverageData currentDayMaData = currentQuotation.getMovingAverageData();

        if (currentDayMaData == null || currentDayMaData.getSma30Volume() == 0) {
            return false;
        }

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance >= UP_PERFORMANCE_THRESHOLD
                && currentQuotation.getVolume() > currentDayMaData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation has traded down by at least 3% on above-average volume on volume against the
     * previous Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if currentQuotation traded down on volume; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isDownOnVolume(final Quotation currentQuotation, final Quotation previousQuotation)
            throws Exception {
        float performance;
        MovingAverageData currentDayMaData = currentQuotation.getMovingAverageData();

        if (currentDayMaData == null || currentDayMaData.getSma30Volume() == 0) {
            return false;
        }

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance <= DOWN_PERFORMANCE_THRESHOLD
                && currentQuotation.getVolume() > currentDayMaData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation constitutes a bearish high-volume reversal.
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation constitutes a bearish high-volume reversal; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isBearishHighVolumeReversal(final Quotation currentQuotation) throws Exception {
        BigDecimal dailyPriceRange;
        BigDecimal reversalThresholdPrice;
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        if (maData == null || maData.getSma30Volume() == 0) {
            return false;
        }

        dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
        reversalThresholdPrice = currentQuotation.getLow()
                .add(dailyPriceRange.multiply(new BigDecimal(REVERSAL_THRESHOLD_BEARISH)));

        if (currentQuotation.getOpen().compareTo(reversalThresholdPrice) <= 0
                && currentQuotation.getClose().compareTo(reversalThresholdPrice) <= 0
                && currentQuotation.getVolume() > maData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation constitutes a bullish high-volume reversal.
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation constitutes a bullish high-volume reversal; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isBullishHighVolumeReversal(final Quotation currentQuotation) throws Exception {
        BigDecimal dailyPriceRange;
        BigDecimal reversalThresholdPrice;
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        if (maData == null || maData.getSma30Volume() == 0) {
            return false;
        }

        dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
        reversalThresholdPrice = currentQuotation.getLow()
                .add(dailyPriceRange.multiply(new BigDecimal(REVERSAL_THRESHOLD_BULLISH)));

        if (currentQuotation.getOpen().compareTo(reversalThresholdPrice) >= 0
                && currentQuotation.getClose().compareTo(reversalThresholdPrice) >= 0
                && currentQuotation.getVolume() > maData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation is churning.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if currentQuotation is churning; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isChurning(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
        float performance;
        MovingAverageData currentDayMaData = currentQuotation.getMovingAverageData();

        if (currentDayMaData == null || currentDayMaData.getSma30Volume() == 0) {
            return false;
        }

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance <= CHURNING_UP_THRESHOLD && performance >= CHURNING_DOWN_THRESHOLD
                && currentQuotation.getVolume() > currentDayMaData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Determines the size of a gap up in percent.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The percentage size of the gap up.
     * @throws Exception Determination failed.
     */
    public float getGapUpSize(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
        float gapSize;

        gapSize = this.performanceCalculator.getPerformance(currentQuotation.getLow().floatValue(),
                previousQuotation.getHigh().floatValue());

        return gapSize;
    }
}
