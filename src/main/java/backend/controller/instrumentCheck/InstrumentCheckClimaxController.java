package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
     * Checks if the Instrument has a climax movement advancing at least 25% within a week or 50% within three weeks.
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

            if (currentDayQuotation.getIndicator() == null) {
                throw new Exception("No indicator is defined for Quotation with ID: " + currentDayQuotation.getId());
            }

            performanceOneWeek = this.performanceCalculator.getPricePerformanceForDays(daysInWeek, currentDayQuotation,
                    sortedQuotations);

            if (performanceOneWeek >= CLIMAX_ONE_WEEK_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
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

            if (currentDayQuotation.getIndicator() == null) {
                throw new Exception("No indicator is defined for Quotation with ID: " + currentDayQuotation.getId());
            }

            performanceThreeWeeks = this.performanceCalculator.getPricePerformanceForDays(daysInThreeWeeks,
                    currentDayQuotation, sortedQuotations);

            if (performanceThreeWeeks >= CLIMAX_THREE_WEEKS_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.climaxThreeWeeks"),
                        performanceThreeWeeks));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }
}
