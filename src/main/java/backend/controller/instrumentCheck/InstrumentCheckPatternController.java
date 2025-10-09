package backend.controller.instrumentCheck;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.controller.chart.priceVolume.DistributionDaysChartController;
import backend.controller.chart.priceVolume.PocketPivotChartController;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks that are based on certain price and volume patterns. For example
 * this can be a high volume price reversal or a stalling in price accompanied by increased volume (churning).
 *
 * @author Michael
 */
public class InstrumentCheckPatternController {
    /**
     * The threshold of a gap up that constitutes an exhaustion gap.
     */
    private static final float EXHAUSTION_GAP_THRESHOLD = 1;

    /**
     * The threshold of a gap up that constitutes a bullish gap.
     */
    private static final float BULLISH_GAP_THRESHOLD = 1;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Helper class for pattern-related tasks.
     */
    private PatternControllerHelper patternControllerHelper;

    /**
     * Default constructor.
     */
    public InstrumentCheckPatternController() {
        this.patternControllerHelper = new PatternControllerHelper();
    }

    /**
     * Checks for days on which the Instrument rises a certain amount on above-average volume. The check begins at the
     * start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument trades up on volume.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkUpOnVolume(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isUpOnVolume;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            isUpOnVolume = this.patternControllerHelper.isUpOnVolume(currentQuotation, previousQuotation);

            if (isUpOnVolume) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.upOnVolume"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument declines a certain amount on above-average volume. The check begins at
     * the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument trades down on volume.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkDownOnVolume(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isDownOnVolume;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            isDownOnVolume = this.patternControllerHelper.isDownOnVolume(currentQuotation, previousQuotation);

            if (isDownOnVolume) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.downOnVolume"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument stalls in price on increased volume (churning). The check begins at the
     * start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument is churning.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkChurning(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isChurning;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            isChurning = this.patternControllerHelper.isChurning(currentQuotation, previousQuotation);

            if (isChurning) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.churning"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument builds a reversal (open and close in lower third of candle on
     * above-average volume). The check begins at the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument shows a reversal.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkHighVolumeReversal(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isBearishHighVolumeReversal;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            isBearishHighVolumeReversal = this.patternControllerHelper.isBearishHighVolumeReversal(currentQuotation);

            if (isBearishHighVolumeReversal) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.reversal"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument builds an exhaustion gap up (low of the current day is higher than the
     * high of the previous day). The check begins at the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument builds an exhaustion gap up.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkExhaustionGapUp(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float gapUpSize;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            gapUpSize = this.patternControllerHelper.getGapUpSize(currentQuotation, previousQuotation);

            if (gapUpSize >= EXHAUSTION_GAP_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(resources.getString("protocol.exhaustionGapUp"), gapUpSize));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument builds a bullish gap up (low of the current day is higher than the high
     * of the previous day). The check begins at the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument builds a bullish gap up.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkBullishGapUp(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float gapUpSize;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            gapUpSize = this.patternControllerHelper.getGapUpSize(currentQuotation, previousQuotation);

            if (gapUpSize >= BULLISH_GAP_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(resources.getString("protocol.bullishGapUp"), gapUpSize));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument builds a Distribution Day. The check begins at the start date and goes up
     * until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument builds a Distribution Day.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkDistributionDay(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        DistributionDaysChartController ddController = new DistributionDaysChartController();
        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isDistributionDay;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) < sortedQuotations.getQuotations().size()) {
                previousQuotation = sortedQuotations.getQuotations().get(i + 1);
            } else {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            isDistributionDay = ddController.isDistributionDay(currentQuotation, previousQuotation,
                    sortedQuotations.getQuotations());

            if (isDistributionDay) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.distributionDay"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks for days on which the Instrument builds a Pocket Pivot. The check begins at the start date and goes up
     * until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument builds a Pocket Pivot.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkPocketPivot(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        PocketPivotChartController ppController = new PocketPivotChartController();
        int startIndex;
        Quotation currentQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isPocketPivot;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            if ((i + 1) >= sortedQuotations.getQuotations().size()) {
                continue;
            }

            currentQuotation = sortedQuotations.getQuotations().get(i);
            isPocketPivot = ppController.isPocketPivot(sortedQuotations.getQuotations(), i);

            if (isPocketPivot) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.pocketPivot"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }
}
