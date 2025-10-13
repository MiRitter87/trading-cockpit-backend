package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        int startIndex;
        Quotation currentQuotation;
        Quotation largestDownQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float largestDownDayPerformance;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            largestDownQuotation = this.getLargestDownDay(sortedQuotations.getQuotations(), currentQuotation);

            if (largestDownQuotation == null) {
                continue;
            }

            largestDownDayPerformance = this.performanceCalculator.getPricePerformanceForDays(1, largestDownQuotation,
                    sortedQuotations);

            if (largestDownQuotation.equals(currentQuotation)) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestDownQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestDownDay"),
                        largestDownDayPerformance));
                protocolEntries.add(protocolEntry);
            }
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
        int startIndex;
        Quotation currentQuotation;
        Quotation largestUpQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float largestUpDayPerformance;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            largestUpQuotation = this.getLargestUpDay(sortedQuotations.getQuotations(), currentQuotation);

            if (largestUpQuotation == null) {
                continue;
            }

            largestUpDayPerformance = this.performanceCalculator.getPricePerformanceForDays(1, largestUpQuotation,
                    sortedQuotations);

            if (largestUpQuotation.equals(currentQuotation)) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestUpQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestUpDay"),
                        largestUpDayPerformance));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for the largest daily high/low-spread of the year. The check begins at the start date and goes up until
     * the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations that build the trading history.
     * @return List of ProtocolEntry, for the day of the largest high/low-spread of the year after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkLargestDailySpread(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        Quotation largestSpreadQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float spreadSizePercent;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            largestSpreadQuotation = this.getLargestDailySpread(sortedQuotations.getQuotations(), currentQuotation);
            spreadSizePercent = this.performanceCalculator.getPerformance(largestSpreadQuotation.getHigh().floatValue(),
                    largestSpreadQuotation.getLow().floatValue());

            if (largestSpreadQuotation.equals(currentQuotation)) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestSpreadQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.largestDailySpread"),
                        spreadSizePercent));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for the largest daily volume of the year. The check begins at the start date and goes up until the most
     * recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations that build the trading history.
     * @return List of ProtocolEntry, for the day of the largest volume of the year after the start date.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkLargestDailyVolume(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        Quotation largestVolumeQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            largestVolumeQuotation = this.getLargestDailyVolume(sortedQuotations.getQuotations(), currentQuotation);

            if (largestVolumeQuotation.equals(currentQuotation)) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(largestVolumeQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.largestDailyVolume"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Determines the largest down-day of the last 52 weeks.
     *
     * @param quotations   A list of quotations sorted by date that build the trading history.
     * @param endQuotation The latest Quotation for which the check is executed.
     * @return The Quotation of the largest down-day.
     */
    private Quotation getLargestDownDay(final List<Quotation> quotations, final Quotation endQuotation) {
        float largestDownPerformance = 0;
        float performance;
        Quotation largestDownQuotation = null;
        Quotation currentQuotation;
        Quotation previousQuotation;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(endQuotation.getDate());
        calendar.add(Calendar.YEAR, -1);

        // Determine the Quotation with the largest negative performance.
        for (int i = 0; i <= quotations.size() - 2; i++) {
            currentQuotation = quotations.get(i);
            previousQuotation = quotations.get(i + 1);

            // Ignore quotations newer than the date of the end quotation.
            if (currentQuotation.getDate().getTime() > endQuotation.getDate().getTime()) {
                continue;
            }

            // Ignore quotations more than one year older than the date of the end quotation.
            if (currentQuotation.getDate().getTime() < calendar.getTimeInMillis()) {
                continue;
            }

            performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

            if (performance < largestDownPerformance) {
                largestDownPerformance = performance;
                largestDownQuotation = currentQuotation;
            }
        }

        return largestDownQuotation;
    }

    /**
     * Determines the largest up-day of the last 52 weeks. The checks are performed up until the given endQuotation.
     * Quotations afterwards are not taken into account.
     *
     * @param quotations   A list of quotations sorted by date that build the trading history.
     * @param endQuotation The latest Quotation for which the check is executed.
     * @return The Quotation of the largest up-day.
     */
    private Quotation getLargestUpDay(final List<Quotation> quotations, final Quotation endQuotation) {
        float largestUpPerformance = 0;
        float performance;
        Quotation largestUpQuotation = null;
        Quotation currentQuotation;
        Quotation previousQuotation;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(endQuotation.getDate());
        calendar.add(Calendar.YEAR, -1);

        // Determine the Quotation with the largest positive performance.
        for (int i = 0; i <= quotations.size() - 2; i++) {
            currentQuotation = quotations.get(i);
            previousQuotation = quotations.get(i + 1);

            // Ignore quotations newer than the date of the end quotation.
            if (currentQuotation.getDate().getTime() > endQuotation.getDate().getTime()) {
                continue;
            }

            // Ignore quotations more than one year older than the date of the end quotation.
            if (currentQuotation.getDate().getTime() < calendar.getTimeInMillis()) {
                continue;
            }

            performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

            if (performance > largestUpPerformance) {
                largestUpPerformance = performance;
                largestUpQuotation = currentQuotation;
            }
        }

        return largestUpQuotation;
    }

    /**
     * Determines the largest daily high/low-spread of the last 52 weeks. The checks are performed up until the given
     * endQuotation. Quotations afterwards are not taken into account.
     *
     * @param quotations   A list of quotations sorted by date that build the trading history.
     * @param endQuotation The latest Quotation for which the check is executed.
     * @return The Quotation with the largest daily high/low-spread.
     */
    private Quotation getLargestDailySpread(final List<Quotation> quotations, final Quotation endQuotation) {
        float largestDailySpread = 0;
        float currentSpread;
        Quotation largestSpreadQuotation = null;
        final int hundredPercent = 100;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(endQuotation.getDate());
        calendar.add(Calendar.YEAR, -1);

        // Determine the Quotation with the largest daily high/low-spread.
        for (Quotation currentQuotation : quotations) {
            // Ignore quotations newer than the date of the end quotation.
            if (currentQuotation.getDate().getTime() > endQuotation.getDate().getTime()) {
                continue;
            }

            // Ignore quotations more than one year older than the date of the end quotation.
            if (currentQuotation.getDate().getTime() < calendar.getTimeInMillis()) {
                continue;
            }

            currentSpread = currentQuotation.getHigh().floatValue() / currentQuotation.getLow().floatValue();
            // Convert to percentage value
            currentSpread = (currentSpread - 1) * hundredPercent;

            if (currentSpread > largestDailySpread) {
                largestDailySpread = currentSpread;
                largestSpreadQuotation = currentQuotation;
            }
        }

        return largestSpreadQuotation;
    }

    /**
     * Determines the largest daily volume of the last 52 weeks. The checks are performed up until the given
     * endQuotation. Quotations afterwards are not taken into account.
     *
     * @param quotations   A list of quotations sorted by date that build the trading history.
     * @param endQuotation The latest Quotation for which the check is executed.
     * @return The Quotation with the largest daily volume.
     */
    private Quotation getLargestDailyVolume(final List<Quotation> quotations, final Quotation endQuotation) {
        long largestDailyVolume = 0;
        Quotation largestVolumeQuotation = null;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(endQuotation.getDate());
        calendar.add(Calendar.YEAR, -1);

        // Determine the Quotation with the largest daily volume.
        for (Quotation currentQuotation : quotations) {
            // Ignore quotations newer than the date of the end quotation.
            if (currentQuotation.getDate().getTime() > endQuotation.getDate().getTime()) {
                continue;
            }

            // Ignore quotations more than one year older than the date of the end quotation.
            if (currentQuotation.getDate().getTime() < calendar.getTimeInMillis()) {
                continue;
            }

            if (currentQuotation.getVolume() > largestDailyVolume) {
                largestDailyVolume = currentQuotation.getVolume();
                largestVolumeQuotation = currentQuotation;
            }
        }

        return largestVolumeQuotation;
    }
}
