package backend.controller.instrumentCheck;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.QuotationArray;

/**
 * This controller provides helper methods that are used for counting-related Instrument checks.
 *
 * @author Michael
 */
public class CountingControllerHelper {
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
