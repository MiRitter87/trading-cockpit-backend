package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.controller.chart.ChartController;
import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks in relation to moving averages. For example this can be a close
 * below the SMA(50).
 *
 * @author Michael
 */
public class InstrumentCheckAverageController {
    /**
     * The percentage threshold used to determine "extended above SMA(200)".
     */
    private static final float EXTENDED_ABOVE_SMA200_THRESHOLD = 100;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * The PerformanceCalculator.
     */
    private PerformanceCalculator performanceCalculator;

    /**
     * Initializes the InstrumentCheckAverageController.
     */
    public InstrumentCheckAverageController() {
        this.performanceCalculator = new PerformanceCalculator();
    }

    /**
     * Checks if the price has breached the SMA(50) on a closing basis. The check begins at the start date and goes up
     * until the most recent Quotation.
     *
     * For each day on which the SMA(50) has been breached, a ProtocolEntry is provided with further information.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which the SMA(50) was breached.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkCloseBelowSma50(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentDayQuotation;
        Quotation previousDayQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        MovingAverageData currentDayMaData;
        MovingAverageData previousDayMaData;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousDayQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentDayQuotation = sortedQuotations.getQuotations().get(i);
            currentDayMaData = currentDayQuotation.getMovingAverageData();
            previousDayMaData = previousDayQuotation.getMovingAverageData();

            if (previousDayMaData == null || currentDayMaData == null) {
                continue;
            }

            if (previousDayQuotation.getClose().floatValue() >= previousDayMaData.getSma50()
                    && currentDayQuotation.getClose().floatValue() < currentDayMaData.getSma50()) {

                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));

                if (currentDayQuotation.getVolume() >= currentDayMaData.getSma30Volume()) {
                    protocolEntry.setText(this.resources.getString("protocol.closeBelowSma50HighVolume"));
                } else {
                    protocolEntry.setText(this.resources.getString("protocol.closeBelowSma50LowVolume"));
                }

                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the price has breached the EMA(21) on a closing basis. The check begins at the start date and goes up
     * until the most recent Quotation.
     *
     * For each day on which the EMA(21) has been breached, a ProtocolEntry is provided with further information.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which the EMA(21) was breached.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkCloseBelowEma21(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentDayQuotation;
        Quotation previousDayQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        MovingAverageData currentDayMaData;
        MovingAverageData previousDayMaData;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousDayQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentDayQuotation = sortedQuotations.getQuotations().get(i);
            currentDayMaData = currentDayQuotation.getMovingAverageData();
            previousDayMaData = previousDayQuotation.getMovingAverageData();

            if (previousDayMaData == null || currentDayMaData == null) {
                continue;
            }

            if (previousDayQuotation.getClose().floatValue() >= previousDayMaData.getEma21()
                    && currentDayQuotation.getClose().floatValue() < currentDayMaData.getEma21()) {

                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.closeBelowEma21"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the price has jumped above the SMA(50) on a closing basis on above-average volume. The check begins at
     * the start date and goes up until the most recent Quotation.
     *
     * For each day on which the price has jumped above the SMA(50) on a closing basis on above-average volume, a
     * ProtocolEntry is provided with further information.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which the price jumped above the SMA(50).
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkCloseAboveSma50(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        return null;
    }

    /**
     * Checks if the price is extended above the SMA(200) on a closing basis. The check begins at the start date and
     * goes up until the most recent Quotation.
     *
     * For each day on which the price is extended above the SMA(200), a ProtocolEntry is provided with further
     * information.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which the price is extended above the SMA(200).
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkExtendedAboveSma200(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        float percentAboveSma200;
        Quotation currentDayQuotation;
        MovingAverageData currentDayMaData;
        ProtocolEntry protocolEntry;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentDayQuotation = sortedQuotations.getQuotations().get(i);
            currentDayMaData = currentDayQuotation.getMovingAverageData();

            if (currentDayMaData == null || currentDayMaData.getSma200() == 0) {
                continue; // Can't perform check if no SMA(200) is available.
            }

            percentAboveSma200 = this.performanceCalculator.getPerformance(currentDayQuotation.getClose().floatValue(),
                    currentDayMaData.getSma200());

            if (percentAboveSma200 >= EXTENDED_ABOVE_SMA200_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(this.resources.getString("protocol.extendedAboveSma200"),
                        percentAboveSma200));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the price is extended on a one year basis. The price is considered extended if its extension above the
     * SMA(50) belongs to the top 5% of all values. The check begins at the start date and goes up until the most recent
     * Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which the price is historically extended.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkExtendedOneYear(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        Quotation currentDayQuotation;
        MovingAverageData currentDayMaData;
        float percentAboveSma50;
        float thresholdExtended;
        ProtocolEntry protocolEntry;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentDayQuotation = sortedQuotations.getQuotations().get(i);
            currentDayMaData = currentDayQuotation.getMovingAverageData();

            if (currentDayMaData == null || currentDayMaData.getSma50() == 0) {
                continue; // Can't perform check if no SMA(50) is available.
            }

            percentAboveSma50 = this.performanceCalculator.getPerformance(currentDayQuotation.getClose().floatValue(),
                    currentDayMaData.getSma50());
            thresholdExtended = this.getExtendedAboveSma50Threshold(sortedQuotations, i,
                    ChartController.TRADING_DAYS_PER_YEAR);

            if (percentAboveSma50 > thresholdExtended) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.extendedOneYear"));
                protocolEntries.add(protocolEntry);
            }

        }

        return protocolEntries;
    }

    /**
     * Determines the percentage threshold that indicates price is extended above the SMA(50) historically.
     *
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @param index            The start index used for threshold calculation.
     * @param tradingDays      The lookback period of days taken into account.
     * @return The threshold percentage that indicates price is extended.
     */
    private float getExtendedAboveSma50Threshold(final QuotationArray sortedQuotations, final int index,
            final int tradingDays) {

        ArrayList<Float> deviationSma50Values = new ArrayList<>();
        int currentIndex;
        int thresholdIndex;
        float percentAboveSma50;
        final int percentThreshold = 95;
        final int hundredPercent = 100;

        for (Quotation currentQuotation : sortedQuotations.getQuotations()) {
            currentIndex = sortedQuotations.getQuotations().indexOf(currentQuotation);

            if (currentIndex < index || currentIndex >= index + tradingDays) {
                continue;
            }

            if (currentQuotation.getMovingAverageData() == null
                    || currentQuotation.getMovingAverageData().getSma50() == 0) {
                continue;
            }

            percentAboveSma50 = this.performanceCalculator.getPerformance(currentQuotation.getClose().floatValue(),
                    currentQuotation.getMovingAverageData().getSma50());
            deviationSma50Values.add(percentAboveSma50);
        }

        // Sort values descending.
        Collections.sort(deviationSma50Values);
        Collections.reverse(deviationSma50Values);

        // Get the index of the threshold value.
        thresholdIndex = deviationSma50Values.size() - (deviationSma50Values.size() * percentThreshold / hundredPercent)
                - 1;

        return deviationSma50Values.get(thresholdIndex);
    }
}
