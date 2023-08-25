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
 * Controller that performs Instrument health checks that are based on extreme daily price and volume behavior. For
 * example this can be the highest volume day of the year or the largest up- or down-day of the year.
 *
 * @author Michael
 */
public class InstrumentCheckExtremumController {
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
    public InstrumentCheckExtremumController() {
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * Checks for the largest down-day of the year. The check begins at the start date and goes up until the most recent
     * Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for the day of the largest down-day of the year after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkLargestDownDay(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation largestDownQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float largestDownDayPerformance;

        largestDownQuotation = this.getLargestDownDay(sortedQuotations);
        largestDownDayPerformance = this.performanceCalculator.getPricePerformanceForDays(1, largestDownQuotation,
                sortedQuotations);

        if (largestDownQuotation.getDate().getTime() >= startDate.getTime()) {
            protocolEntry = new ProtocolEntry();
            protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
            protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestDownQuotation.getDate()));
            protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"),
                    largestDownDayPerformance));
            protocolEntries.add(protocolEntry);
        }

        return protocolEntries;
    }

    /**
     * Checks for the largest up-day of the year. The check begins at the start date and goes up until the most recent
     * Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for the day of the largest up-day of the year after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkLargestUpDay(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        Quotation largestUpQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float largestUpDayPerformance;

        largestUpQuotation = this.getLargestUpDay(sortedQuotations);
        largestUpDayPerformance = this.performanceCalculator.getPricePerformanceForDays(1, largestUpQuotation,
                sortedQuotations);

        if (largestUpQuotation.getDate().getTime() >= startDate.getTime()) {
            protocolEntry = new ProtocolEntry();
            protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
            protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestUpQuotation.getDate()));
            protocolEntry.setText(
                    MessageFormat.format(this.resources.getString("protocol.largestUpDay"), largestUpDayPerformance));
            protocolEntries.add(protocolEntry);
        }

        return protocolEntries;
    }

    /**
     * Checks for the largest daily high/low-spread of the year. The check begins at the start date and goes up until
     * the most recent Quotation.
     *
     * @param startDate  The date at which the check starts.
     * @param quotations The quotations that build the trading history.
     * @return List of ProtocolEntry, for the day of the largest high/low-spread of the year after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkLargestDailySpread(final Date startDate, final QuotationArray quotations)
            throws Exception {
        Quotation largestSpreadQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;

        largestSpreadQuotation = this.getLargestDailySpread(quotations.getQuotations());

        if (largestSpreadQuotation.getDate().getTime() >= startDate.getTime()) {
            protocolEntry = new ProtocolEntry();
            protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
            protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestSpreadQuotation.getDate()));
            protocolEntry.setText(this.resources.getString("protocol.largestDailySpread"));
            protocolEntries.add(protocolEntry);
        }

        return protocolEntries;
    }

    /**
     * Checks for the largest daily volume of the year. The check begins at the start date and goes up until the most
     * recent Quotation.
     *
     * @param startDate  The date at which the check starts.
     * @param quotations The quotations that build the trading history.
     * @return List of ProtocolEntry, for the day of the largest volume of the year after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkLargestDailyVolume(final Date startDate, final QuotationArray quotations)
            throws Exception {
        Quotation largestVolumeQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;

        largestVolumeQuotation = this.getLargestDailyVolume(quotations.getQuotations());

        if (largestVolumeQuotation.getDate().getTime() >= startDate.getTime()) {
            protocolEntry = new ProtocolEntry();
            protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
            protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestVolumeQuotation.getDate()));
            protocolEntry.setText(this.resources.getString("protocol.largestDailyVolume"));
            protocolEntries.add(protocolEntry);
        }

        return protocolEntries;
    }

    /**
     * Determines the largest down-day of the given trading history.
     *
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return The Quotation of the largest down-day.
     */
    private Quotation getLargestDownDay(final QuotationArray sortedQuotations) {
        float largestDownPerformance = 0;
        float performance;
        Quotation largestDownQuotation = null;
        Quotation currentQuotation;
        Quotation previousQuotation;

        // Determine the Quotation with the largest negative performance.
        for (int i = 0; i < sortedQuotations.getQuotations().size() - 1; i++) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            previousQuotation = sortedQuotations.getQuotations().get(i + 1);

            performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

            if (performance < largestDownPerformance) {
                largestDownPerformance = performance;
                largestDownQuotation = currentQuotation;
            }
        }

        return largestDownQuotation;
    }

    /**
     * Determines the largest up-day of the given trading history.
     *
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return The Quotation of the largest up-day.
     */
    private Quotation getLargestUpDay(final QuotationArray sortedQuotations) {
        float largestUpPerformance = 0;
        float performance;
        Quotation largestUpQuotation = null;
        Quotation currentQuotation;
        Quotation previousQuotation;

        // Determine the Quotation with the largest positive performance.
        for (int i = 0; i < sortedQuotations.getQuotations().size() - 1; i++) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            previousQuotation = sortedQuotations.getQuotations().get(i + 1);

            performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

            if (performance > largestUpPerformance) {
                largestUpPerformance = performance;
                largestUpQuotation = currentQuotation;
            }
        }

        return largestUpQuotation;
    }

    /**
     * Determines the largest daily high/low-spread of the given trading history.
     *
     * @param quotations A list of quotations.
     * @return The Quotation with the largest daily high/low-spread.
     */
    private Quotation getLargestDailySpread(final List<Quotation> quotations) {
        float largestDailySpread = 0;
        float currentSpread;
        Quotation largestSpreadQuotation = null;

        // Determine the Quotation with the largest daily high/low-spread.
        for (Quotation currentQuotation : quotations) {
            currentSpread = currentQuotation.getHigh().floatValue() - currentQuotation.getLow().floatValue();

            if (currentSpread > largestDailySpread) {
                largestDailySpread = currentSpread;
                largestSpreadQuotation = currentQuotation;
            }
        }

        return largestSpreadQuotation;
    }

    /**
     * Determines the largest daily volume of the given trading history.
     *
     * @param quotations A list of quotations.
     * @return The Quotation with the largest daily volume.
     */
    private Quotation getLargestDailyVolume(final List<Quotation> quotations) {
        long largestDailyVolume = 0;
        Quotation largestVolumeQuotation = null;

        // Determine the Quotation with the largest daily volume.
        for (Quotation currentQuotation : quotations) {
            if (currentQuotation.getVolume() > largestDailyVolume) {
                largestDailyVolume = currentQuotation.getVolume();
                largestVolumeQuotation = currentQuotation;
            }
        }

        return largestVolumeQuotation;
    }
}
