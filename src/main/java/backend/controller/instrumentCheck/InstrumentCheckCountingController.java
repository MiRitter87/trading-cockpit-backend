package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Helper class for counting-related tasks.
     */
    private CountingControllerHelper countingControllerHelper;

    /**
     * Default constructor.
     */
    public InstrumentCheckCountingController() {
        this.countingControllerHelper = new CountingControllerHelper();
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

            goodBadCloseSums = this.countingControllerHelper.getNumberOfGoodAndBadCloses(startQuotation,
                    currentQuotation, sortedQuotations);
            numberOfGoodCloses = goodBadCloseSums.get(CountingControllerHelper.MAP_ENTRY_GOOD_CLOSES);
            numberOfBadCloses = goodBadCloseSums.get(CountingControllerHelper.MAP_ENTRY_BAD_CLOSES);
            numberOfDaysTotal = goodBadCloseSums.get(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL);

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

            goodBadCloseSums = this.countingControllerHelper.getNumberOfGoodAndBadCloses(startQuotation,
                    currentQuotation, sortedQuotations);
            numberOfGoodCloses = goodBadCloseSums.get(CountingControllerHelper.MAP_ENTRY_GOOD_CLOSES);
            numberOfBadCloses = goodBadCloseSums.get(CountingControllerHelper.MAP_ENTRY_BAD_CLOSES);
            numberOfDaysTotal = goodBadCloseSums.get(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL);

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

            upDownDaySums = this.countingControllerHelper.getNumberOfUpAndDownDays(startQuotation, currentQuotation,
                    sortedQuotations);
            numberOfUpDays = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_UP_DAYS);
            numberOfDownDays = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_DOWN_DAYS);
            numberOfDaysTotal = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL);

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

            upDownDaySums = this.countingControllerHelper.getNumberOfUpAndDownDays(startQuotation, currentQuotation,
                    sortedQuotations);
            numberOfUpDays = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_UP_DAYS);
            numberOfDownDays = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_DOWN_DAYS);
            numberOfDaysTotal = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_DAYS_TOTAL);

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
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        final int thresholdDaysWithLowerLows = 3;
        boolean isThreeLowerCloses;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            // Skip this Quotation, if not at least 3 previous days of trading history exist.
            if ((i + thresholdDaysWithLowerLows) >= sortedQuotations.getQuotations().size()) {
                continue;
            }

            isThreeLowerCloses = this.countingControllerHelper.isThreeLowerCloses(thresholdDaysWithLowerLows,
                    sortedQuotations, i);

            if (isThreeLowerCloses) {
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
        int startIndex;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        final int thresholdDaysWithHigherCloses = 3;
        boolean isThreeHigherCloses;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            // Skip this Quotation, if not at least 3 previous days of trading history exist.
            if ((i + thresholdDaysWithHigherCloses) >= sortedQuotations.getQuotations().size()) {
                continue;
            }

            isThreeHigherCloses = this.countingControllerHelper.isThreeHigherCloses(thresholdDaysWithHigherCloses,
                    sortedQuotations, i);

            if (isThreeHigherCloses) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(
                        DateTools.getDateWithoutIntradayAttributes(sortedQuotations.getQuotations().get(i).getDate()));
                protocolEntry.setText(this.resources.getString("protocol.threeHigherCloses"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }
}
