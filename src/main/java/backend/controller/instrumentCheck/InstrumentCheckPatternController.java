package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.controller.scan.PerformanceCalculator;
import backend.model.instrument.MovingAverageData;
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
     * The performance threshold of an "Up on Volume"-day.
     */
    private static final float UP_PERFORMANCE_THRESHOLD = (float) 3.0;

    /**
     * The performance threshold of an "Down on Volume"-day.
     */
    private static final float DOWN_PERFORMANCE_THRESHOLD = (float) -3.0;

    /**
     * The upwards performance threshold of a "Churning"-day.
     */
    private static final float CHURNING_UP_THRESHOLD = (float) 1.0;

    /**
     * The downwards performance threshold of a "Churning"-day.
     */
    private static final float CHURNING_DOWN_THRESHOLD = (float) -1.0;

    /**
     * The threshold of the daily price range for bearish reversal calculation.
     */
    private static final float REVERSAL_THRESHOLD_BEARISH = (float) 0.4;

    /**
     * The threshold of the daily price range for bullish reversal calculation.
     */
    private static final float REVERSAL_THRESHOLD_BULLISH = (float) 0.6;

    /**
     * The threshold of a gap up that constitutes an exhaustion gap.
     */
    private static final float EXHAUSTION_GAP_THRESHOLD = 1;

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
    public InstrumentCheckPatternController() {
        this.performanceCalculator = new PerformanceCalculator();
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
            isUpOnVolume = this.isUpOnVolume(currentQuotation, previousQuotation);

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
            isDownOnVolume = this.isDownOnVolume(currentQuotation, previousQuotation);

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
            isChurning = this.isChurning(currentQuotation, previousQuotation);

            if (isChurning) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
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
            isBearishHighVolumeReversal = this.isBearishHighVolumeReversal(currentQuotation);

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
     * Checks for days on which the Instrument builds a gap up (low of the current day is higher than the high of the
     * previous day). The check begins at the start date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument builds a gap up.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkGapUp(final Date startDate, final QuotationArray sortedQuotations)
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
            gapUpSize = this.getGapUpSize(currentQuotation, previousQuotation);

            if (gapUpSize >= EXHAUSTION_GAP_THRESHOLD) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(MessageFormat.format(resources.getString("protocol.gapUp"), gapUpSize));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the current Quotation has traded up on volume against the previous Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if currentQuotation traded up on volume; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isUpOnVolume(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
        float performance;
        MovingAverageData currentDayMaData = currentQuotation.getIndicator().getMovingAverageData();

        if (currentDayMaData == null || currentDayMaData.getSma30Volume() == 0) {
            return false;
        }

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance >= UP_PERFORMANCE_THRESHOLD
                && currentQuotation.getVolume() > currentDayMaData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation has traded down on volume against the previous Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if currentQuotation traded down on volume; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isDownOnVolume(final Quotation currentQuotation, final Quotation previousQuotation)
            throws Exception {
        float performance;
        MovingAverageData currentDayMaData = currentQuotation.getIndicator().getMovingAverageData();

        if (currentDayMaData == null || currentDayMaData.getSma30Volume() == 0) {
            return false;
        }

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance <= DOWN_PERFORMANCE_THRESHOLD
                && currentQuotation.getVolume() > currentDayMaData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation constitutes a bearish high-volume reversal.
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation constitutes a bearish high-volume reversal; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isBearishHighVolumeReversal(final Quotation currentQuotation) throws Exception {
        BigDecimal dailyPriceRange;
        BigDecimal reversalThresholdPrice;
        MovingAverageData maData = currentQuotation.getIndicator().getMovingAverageData();

        if (maData == null || maData.getSma30Volume() == 0) {
            return false;
        }

        dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
        reversalThresholdPrice = currentQuotation.getLow()
                .add(dailyPriceRange.multiply(new BigDecimal(REVERSAL_THRESHOLD_BEARISH)));

        if (currentQuotation.getOpen().compareTo(reversalThresholdPrice) <= 0
                && currentQuotation.getClose().compareTo(reversalThresholdPrice) <= 0
                && currentQuotation.getVolume() > maData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation constitutes a bullish high-volume reversal.
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation constitutes a bullish high-volume reversal; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isBullishHighVolumeReversal(final Quotation currentQuotation) throws Exception {
        BigDecimal dailyPriceRange;
        BigDecimal reversalThresholdPrice;
        MovingAverageData maData = currentQuotation.getIndicator().getMovingAverageData();

        if (maData == null || maData.getSma30Volume() == 0) {
            return false;
        }

        dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
        reversalThresholdPrice = currentQuotation.getLow()
                .add(dailyPriceRange.multiply(new BigDecimal(REVERSAL_THRESHOLD_BULLISH)));

        if (currentQuotation.getOpen().compareTo(reversalThresholdPrice) >= 0
                && currentQuotation.getClose().compareTo(reversalThresholdPrice) >= 0
                && currentQuotation.getVolume() > maData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation is churning.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if currentQuotation is churning; false, if not.
     * @throws Exception Determination failed.
     */
    public boolean isChurning(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
        float performance;
        MovingAverageData currentDayMaData = currentQuotation.getIndicator().getMovingAverageData();

        if (currentDayMaData == null || currentDayMaData.getSma30Volume() == 0) {
            return false;
        }

        performance = this.performanceCalculator.getPerformance(currentQuotation, previousQuotation);

        if (performance <= CHURNING_UP_THRESHOLD && performance >= CHURNING_DOWN_THRESHOLD
                && currentQuotation.getVolume() > currentDayMaData.getSma30Volume()) {
            return true;
        }

        return false;
    }

    /**
     * Determines the size of a gap up in percent.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The percentage size of the gap up.
     * @throws Exception Determination failed.
     */
    public float getGapUpSize(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
        float gapSize;

        gapSize = this.performanceCalculator.getPerformance(currentQuotation.getLow().floatValue(),
                previousQuotation.getHigh().floatValue());

        return gapSize;
    }
}
