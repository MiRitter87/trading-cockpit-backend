package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks that are based on counting specific characteristics.
 *
 * @author Michael
 */
public class InstrumentCheckCountingController {
    /**
     * Key of the Map entry containing the number of up-days.
     */
    public static final String MAP_ENTRY_UP_DAYS = "NUMBER_UP_DAYS";

    /**
     * Key of the Map entry containing the number of down-days.
     */
    public static final String MAP_ENTRY_DOWN_DAYS = "NUMBER_DOWN_DAYS";

    /**
     * Key of the Map entry containing the total number of days.
     */
    public static final String MAP_ENTRY_DAYS_TOTAL = "DAYS_TOTAL";

    /**
     * Key of the Map entry containing the number of good closes.
     */
    public static final String MAP_ENTRY_GOOD_CLOSES = "NUMBER_GOOD_CLOSES";

    /**
     * Key of the Map entry containing the number of bad closes.
     */
    public static final String MAP_ENTRY_BAD_CLOSES = "NUMBER_BAD_CLOSES";

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Performance calculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Default constructor.
     */
    public InstrumentCheckCountingController() {
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * Checks if there are more bad closes than good closes. A close is considered 'bad' if it occurs in the lower half
     * of the days trading range. The check begins at the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the number of bad closes exceeds the number of good closes
     *         after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkMoreBadThanGoodCloses(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation startQuotation;
        Quotation currentQuotation;
        int startIndex;
        int numberOfGoodCloses;
        int numberOfBadCloses;
        int numberOfDaysTotal;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        Map<String, Integer> goodBadCloseSums;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        startQuotation = sortedQuotations.getQuotations().get(startIndex);

        for (int i = startIndex; i >= 0; i--) {
            // Skip the first day, because more bad than good closes can only be calculated for at least two quotations.
            if (i == startIndex) {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);

            goodBadCloseSums = this.getNumberOfGoodAndBadCloses(startQuotation, currentQuotation, sortedQuotations);
            numberOfGoodCloses = goodBadCloseSums.get(MAP_ENTRY_GOOD_CLOSES);
            numberOfBadCloses = goodBadCloseSums.get(MAP_ENTRY_BAD_CLOSES);
            numberOfDaysTotal = goodBadCloseSums.get(MAP_ENTRY_DAYS_TOTAL);

            if (numberOfBadCloses > numberOfGoodCloses) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreBadCloses"),
                        numberOfBadCloses, numberOfDaysTotal));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if there are more good closes than bad closes. A close is considered 'good' if it occurs in the upper half
     * of the days trading range. The check begins at the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the number of good closes exceeds the number of bad closes
     *         after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkMoreGoodThanBadCloses(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation startQuotation;
        Quotation currentQuotation;
        int startIndex;
        int numberOfGoodCloses;
        int numberOfBadCloses;
        int numberOfDaysTotal;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        Map<String, Integer> goodBadCloseSums;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        startQuotation = sortedQuotations.getQuotations().get(startIndex);

        for (int i = startIndex; i >= 0; i--) {
            // Skip the first day, because more good than bad closes can only be calculated for at least two quotations.
            if (i == startIndex) {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);

            goodBadCloseSums = this.getNumberOfGoodAndBadCloses(startQuotation, currentQuotation, sortedQuotations);
            numberOfGoodCloses = goodBadCloseSums.get(MAP_ENTRY_GOOD_CLOSES);
            numberOfBadCloses = goodBadCloseSums.get(MAP_ENTRY_BAD_CLOSES);
            numberOfDaysTotal = goodBadCloseSums.get(MAP_ENTRY_DAYS_TOTAL);

            if (numberOfGoodCloses > numberOfBadCloses) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreGoodCloses"),
                        numberOfGoodCloses, numberOfDaysTotal));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if there are more down-days than up-days. The check begins at the start date and goes up until the most
     * recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the number of down-days exceeds the number of up-days after
     *         the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkMoreDownThanUpDays(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation startQuotation;
        Quotation currentQuotation;
        int startIndex;
        int numberOfUpDays;
        int numberOfDownDays;
        int numberOfDaysTotal;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        Map<String, Integer> upDownDaySums;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        startQuotation = sortedQuotations.getQuotations().get(startIndex);

        for (int i = startIndex; i >= 0; i--) {
            // Skip the first day, because more down than up days can only be calculated for at least two quotations.
            if (i == startIndex) {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);

            upDownDaySums = this.getNumberOfUpAndDownDays(startQuotation, currentQuotation, sortedQuotations);
            numberOfUpDays = upDownDaySums.get(MAP_ENTRY_UP_DAYS);
            numberOfDownDays = upDownDaySums.get(MAP_ENTRY_DOWN_DAYS);
            numberOfDaysTotal = upDownDaySums.get(MAP_ENTRY_DAYS_TOTAL);

            if (numberOfDownDays > numberOfUpDays) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreDownDays"),
                        numberOfDownDays, numberOfDaysTotal));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if there are more up-days than down-days. The check begins at the start date and goes up until the most
     * recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the number of up-days exceeds the number of down-days after
     *         the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkMoreUpThanDownDays(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation startQuotation;
        Quotation currentQuotation;
        int startIndex;
        int numberOfUpDays;
        int numberOfDownDays;
        int numberOfDaysTotal;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        Map<String, Integer> upDownDaySums;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        startQuotation = sortedQuotations.getQuotations().get(startIndex);

        for (int i = startIndex; i >= 0; i--) {
            // Skip the first day, because more up than down days can only be calculated for at least two quotations.
            if (i == startIndex) {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);

            upDownDaySums = this.getNumberOfUpAndDownDays(startQuotation, currentQuotation, sortedQuotations);
            numberOfUpDays = upDownDaySums.get(MAP_ENTRY_UP_DAYS);
            numberOfDownDays = upDownDaySums.get(MAP_ENTRY_DOWN_DAYS);
            numberOfDaysTotal = upDownDaySums.get(MAP_ENTRY_DAYS_TOTAL);

            if (numberOfUpDays > numberOfDownDays) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.moreUpDays"),
                        numberOfUpDays, numberOfDaysTotal));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if three lower closes on above-average volume have occurred.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which at least three lower closes have occurred.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkThreeLowerCloses(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        int numberOfDownDays;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float performance;
        MovingAverageData maData;
        final int thresholdDaysWithLowerLows = 3;
        long downVolumeSum;
        long averageVolumeSum;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            // Skip this Quotation, if not at least 3 previous days of trading history exist.
            if ((i + thresholdDaysWithLowerLows) >= sortedQuotations.getQuotations().size()) {
                continue;
            }

            numberOfDownDays = 0;
            downVolumeSum = 0;
            averageVolumeSum = 0;

            // Count the number of lower closes within the last three trading days.
            for (int j = 0; j < thresholdDaysWithLowerLows; j++) {
                maData = sortedQuotations.getQuotations().get(i + j).getMovingAverageData();

                if (maData == null || maData.getSma30Volume() == 0) {
                    continue;
                }

                performance = this.performanceCalculator.getPerformance(sortedQuotations.getQuotations().get(i + j),
                        sortedQuotations.getQuotations().get(i + j + 1));

                if (performance < 0) {
                    numberOfDownDays++;
                    downVolumeSum = downVolumeSum + sortedQuotations.getQuotations().get(i + j).getVolume();
                    averageVolumeSum = averageVolumeSum + maData.getSma30Volume();
                }
            }

            if (numberOfDownDays == thresholdDaysWithLowerLows && downVolumeSum > averageVolumeSum) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(
                        DateTools.getDateWithoutIntradayAttributes(sortedQuotations.getQuotations().get(i).getDate()));
                protocolEntry.setText(this.resources.getString("protocol.threeLowerCloses"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if three higher closes on above-average volume have occurred.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which at least three higher closes have occurred.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkThreeHigherCloses(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        return null;
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
}
