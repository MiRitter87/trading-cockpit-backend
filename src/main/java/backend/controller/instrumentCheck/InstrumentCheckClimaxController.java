package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks with regard to climax moves.
 *
 * @author Michael
 */
public class InstrumentCheckClimaxController {
    /**
     * The performance threshold of a climax move within one week.
     */
    private static final float CLIMAX_ONE_WEEK_THRESHOLD = 25;

    /**
     * The performance threshold of a climax move within three weeks.
     */
    private static final float CLIMAX_THREE_WEEKS_THRESHOLD = 50;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Performance calculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Initializes the controller.
     */
    public InstrumentCheckClimaxController() {
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * Checks if the Instrument has a climax movement advancing at least 25% within a week.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which a climactic advance is given.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkClimaxMoveOneWeek(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentDayQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float performanceOneWeek = 0;
        final int daysInWeek = 5;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentDayQuotation = sortedQuotations.getQuotations().get(i);

            performanceOneWeek = this.performanceCalculator.getPricePerformanceForDays(daysInWeek, currentDayQuotation,
                    sortedQuotations);

            if (performanceOneWeek >= CLIMAX_ONE_WEEK_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
                protocolEntry.setText(
                        MessageFormat.format(this.resources.getString("protocol.climaxOneWeek"), performanceOneWeek));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the Instrument has a climax movement advancing at least 50% within three weeks.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which a climactic advance is given.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkClimaxMoveThreeWeeks(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentDayQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float performanceThreeWeeks = 0;
        final int daysInThreeWeeks = 15;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentDayQuotation = sortedQuotations.getQuotations().get(i);

            performanceThreeWeeks = this.performanceCalculator.getPricePerformanceForDays(daysInThreeWeeks,
                    currentDayQuotation, sortedQuotations);

            if (performanceThreeWeeks >= CLIMAX_THREE_WEEKS_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.climaxThreeWeeks"),
                        performanceThreeWeeks));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if there is a time-wise climax movement. A time-wise climax move is given, if at least 7 of the last 10
     * trading days are up-days.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which there is a time-wise climax movement.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkTimeClimax(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation currentQuotation;
        int startIndex;
        int numberOfUpDays;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        Map<String, Integer> upDownDaySums;
        final int thresholdNumberOfUpDays = 7;
        final int numberOfDaysCheckedForClimax = 10;
        CountingControllerHelper countingControllerHelper = new CountingControllerHelper();

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            // Skip this Quotation, if not at least 10 previous days of trading history exist.
            if ((i + numberOfDaysCheckedForClimax) >= sortedQuotations.getQuotations().size()) {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);

            upDownDaySums = countingControllerHelper.getNumberOfUpAndDownDays(
                    sortedQuotations.getQuotations().get(i + numberOfDaysCheckedForClimax - 1), currentQuotation,
                    sortedQuotations);
            numberOfUpDays = upDownDaySums.get(CountingControllerHelper.MAP_ENTRY_UP_DAYS);

            if (numberOfUpDays >= thresholdNumberOfUpDays) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.timeClimax"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }
}
