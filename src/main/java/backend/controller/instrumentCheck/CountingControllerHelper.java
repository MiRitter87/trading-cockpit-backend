package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * This controller provides helper methods that are used for counting-related Instrument checks.
 *
 * @author Michael
 */
public class CountingControllerHelper {
    /**
     * Key of the Map entry containing the number of good closes.
     */
    public static final String MAP_ENTRY_GOOD_CLOSES = "NUMBER_GOOD_CLOSES";

    /**
     * Key of the Map entry containing the number of bad closes.
     */
    public static final String MAP_ENTRY_BAD_CLOSES = "NUMBER_BAD_CLOSES";

    /**
     * Key of the Map entry containing the total number of days.
     */
    public static final String MAP_ENTRY_DAYS_TOTAL = "DAYS_TOTAL";

    /**
     * Key of the Map entry containing the number of up-days.
     */
    public static final String MAP_ENTRY_UP_DAYS = "NUMBER_UP_DAYS";

    /**
     * Key of the Map entry containing the number of down-days.
     */
    public static final String MAP_ENTRY_DOWN_DAYS = "NUMBER_DOWN_DAYS";

    /**
     * Performance calculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Initializes the CountingControllerHelper.
     */
    public CountingControllerHelper() {
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * Counts the number of good and bad closes from startQuotation to endQuotation.
     *
     * @param startQuotation   The first Quotation used for counting.
     * @param endQuotation     The last Quotation used for counting.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return A Map containing the number of good and bad closes.
     */
    public Map<String, Integer> getNumberOfGoodAndBadCloses(final Quotation startQuotation,
            final Quotation endQuotation, final QuotationArray sortedQuotations) {

        int indexOfStartQuotation;
        int indexOfEndQuotation;
        int numberOfGoodCloses = 0;
        int numberOfBadCloses = 0;
        int numberOfDaysTotal = 0;
        Quotation currentQuotation;
        final int requiredMapSize = 3;
        Map<String, Integer> resultMap = new HashMap<>(requiredMapSize);
        boolean isGoodClose;

        indexOfStartQuotation = sortedQuotations.getQuotations().indexOf(startQuotation);
        indexOfEndQuotation = sortedQuotations.getQuotations().indexOf(endQuotation);

        for (int i = indexOfStartQuotation; i >= indexOfEndQuotation; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            isGoodClose = this.isGoodClose(currentQuotation);

            if (isGoodClose) {
                numberOfGoodCloses++;
            } else {
                numberOfBadCloses++;
            }

            numberOfDaysTotal++;
        }

        resultMap.put(MAP_ENTRY_GOOD_CLOSES, numberOfGoodCloses);
        resultMap.put(MAP_ENTRY_BAD_CLOSES, numberOfBadCloses);
        resultMap.put(MAP_ENTRY_DAYS_TOTAL, numberOfDaysTotal);

        return resultMap;
    }

    /**
     * Checks if the current Quotation constitutes a good close (close in the upper half of the daily trading range).
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation constitutes a good close; false, if not.
     */
    public boolean isGoodClose(final Quotation currentQuotation) {
        BigDecimal medianPrice;
        final int scale = 3;

        medianPrice = currentQuotation.getLow().add(currentQuotation.getHigh()).divide(new BigDecimal(2), scale,
                RoundingMode.HALF_UP);

        // A close exactly in the middle of the range is considered a bad close.
        if (currentQuotation.getClose().compareTo(medianPrice) == 1) {
            return true;
        }

        return false;
    }

    /**
     * Counts the number of up- and down-days from startQuotation to endQuotation.
     *
     * @param startQuotation   The first Quotation used for counting.
     * @param endQuotation     The last Quotation used for counting.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return A Map containing the number of up- and down-days.
     */
    public Map<String, Integer> getNumberOfUpAndDownDays(final Quotation startQuotation, final Quotation endQuotation,
            final QuotationArray sortedQuotations) {

        int indexOfStartQuotation;
        int indexOfEndQuotation;
        int numberOfUpDays = 0;
        int numberOfDownDays = 0;
        int numberOfDaysTotal = 0;
        Quotation currentQuotation;
        Quotation previousQuotation;
        float performance;
        final int requiredMapSize = 3;
        Map<String, Integer> resultMap = new HashMap<>(requiredMapSize);

        indexOfStartQuotation = sortedQuotations.getQuotations().indexOf(startQuotation);
        indexOfEndQuotation = sortedQuotations.getQuotations().indexOf(endQuotation);

        for (int i = indexOfStartQuotation; i >= indexOfEndQuotation; i--) {
            // Can't calculate performance for oldest Quotation because no previous Quotation exists for this one.
            if (sortedQuotations.getQuotations().size() <= (i + 1)) {
                continue;
            }

            previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            currentQuotation = sortedQuotations.getQuotations().get(i);
            performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

            if (performance > 0) {
                numberOfUpDays++;
            } else if (performance < 0) {
                numberOfDownDays++;
            }

            numberOfDaysTotal++;
        }

        resultMap.put(MAP_ENTRY_UP_DAYS, numberOfUpDays);
        resultMap.put(MAP_ENTRY_DOWN_DAYS, numberOfDownDays);
        resultMap.put(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL, numberOfDaysTotal);

        return resultMap;
    }

    /**
     * Checks if three lower closes on above-average volume are given.
     *
     * @param thresholdDaysWithLowerLows Number of days to check for successive lower closes.
     * @param sortedQuotations           The quotations sorted by date that build the trading history.
     * @param startIndex                 The index where to start the check for lower closes.
     * @return true, if three lower closes on above-average volume; false, if not.
     */
    public boolean isThreeLowerCloses(final int thresholdDaysWithLowerLows, final QuotationArray sortedQuotations,
            final int startIndex) {
        MovingAverageData maData;
        int numberOfDownDays = 0;
        long downVolumeSum = 0;
        long averageVolumeSum = 0;
        float performance;

        // Count the number of lower closes within the last trading days.
        for (int j = 0; j < thresholdDaysWithLowerLows; j++) {
            maData = sortedQuotations.getQuotations().get(startIndex + j).getMovingAverageData();

            if (maData == null || maData.getSma30Volume() == 0) {
                continue;
            }

            performance = this.performanceCalculator.getPerformance(
                    sortedQuotations.getQuotations().get(startIndex + j),
                    sortedQuotations.getQuotations().get(startIndex + j + 1));

            if (performance < 0) {
                numberOfDownDays++;
                downVolumeSum = downVolumeSum + sortedQuotations.getQuotations().get(startIndex + j).getVolume();
                averageVolumeSum = averageVolumeSum + maData.getSma30Volume();
            }
        }

        if (numberOfDownDays == thresholdDaysWithLowerLows && downVolumeSum > averageVolumeSum) {
            return true;
        }

        return false;
    }

    /**
     * Checks if three higher closes on above-average volume are given.
     *
     * @param thresholdDaysWithHigherCloses Number of days to check for successive higher closes.
     * @param sortedQuotations              The quotations sorted by date that build the trading history.
     * @param startIndex                    The index where to start the check for higher closes.
     * @return true, if three higher closes on above-average volume; false, if not.
     */
    public boolean isThreeHigherCloses(final int thresholdDaysWithHigherCloses, final QuotationArray sortedQuotations,
            final int startIndex) {
        MovingAverageData maData;
        int numberOfUpDays = 0;
        long upVolumeSum = 0;
        long averageVolumeSum = 0;
        float performance;

        // Count the number of higher closes within the last three trading days.
        for (int j = 0; j < thresholdDaysWithHigherCloses; j++) {
            maData = sortedQuotations.getQuotations().get(startIndex + j).getMovingAverageData();

            if (maData == null || maData.getSma30Volume() == 0) {
                continue;
            }

            performance = this.performanceCalculator.getPerformance(
                    sortedQuotations.getQuotations().get(startIndex + j),
                    sortedQuotations.getQuotations().get(startIndex + j + 1));

            if (performance > 0) {
                numberOfUpDays++;
                upVolumeSum = upVolumeSum + sortedQuotations.getQuotations().get(startIndex + j).getVolume();
                averageVolumeSum = averageVolumeSum + maData.getSma30Volume();
            }
        }

        if (numberOfUpDays == thresholdDaysWithHigherCloses && upVolumeSum > averageVolumeSum) {
            return true;
        }

        return false;
    }
}
