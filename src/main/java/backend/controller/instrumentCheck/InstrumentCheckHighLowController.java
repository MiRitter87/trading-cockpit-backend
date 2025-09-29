package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks that constitute new highs or new lows in price or some price
 * derivatives.
 *
 * @author Michael
 */
public class InstrumentCheckHighLowController {
    /**
     * The threshold of the daily price range that constitutes a "close near high".
     */
    private static final float CLOSE_NEAR_HIGH_THRESHOLD = (float) 0.9;

    /**
     * The threshold of the daily price range that constitutes a "close near low".
     */
    private static final float CLOSE_NEAR_LOW_THRESHOLD = (float) 0.1;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Checks if the instrument closed near its daily high price. The check begins at the start date and goes up until
     * the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument closes near the daily high price.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkCloseNearHigh(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {
        int startIndex;
        Quotation currentQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isCloseNearHigh;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            isCloseNearHigh = this.isCloseNearHigh(currentQuotation);

            if (isCloseNearHigh) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.closeNearHigh"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the instrument closed near its daily low price. The check begins at the start date and goes up until
     * the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument closes near the daily low price.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkCloseNearLow(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        Quotation currentQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        boolean isCloseNearLow;

        startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);

        if (startIndex == -1) {
            throw new Exception("Could not find a quotation at or after the given start date.");
        }

        for (int i = startIndex; i >= 0; i--) {
            currentQuotation = sortedQuotations.getQuotations().get(i);
            isCloseNearLow = this.isCloseNearLow(currentQuotation);

            if (isCloseNearLow) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.closeNearLow"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the instrument made a new 52-week high on a closing basis. The check begins at the start date and goes
     * up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the Instrument makes a new 52-week high.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkNew52WeekHigh(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float maxClosingHigh;

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
            maxClosingHigh = this.get52WeekHigh(sortedQuotations.getQuotations(), previousQuotation);

            if (currentQuotation.getClose().floatValue() > maxClosingHigh) {
                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.new52WeekHigh"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the RS-line of an instrument made a new 52-week high on a closing basis. The check begins at the start
     * date and goes up until the most recent Quotation.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for each day on which the RS-line of an Instrument makes a new 52-week high.
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkRsLineNew52WeekHigh(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        int startIndex;
        Quotation currentQuotation;
        Quotation previousQuotation;
        List<ProtocolEntry> protocolEntries = new ArrayList<>();
        ProtocolEntry protocolEntry;
        float maxRsLineHigh;

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

            maxRsLineHigh = this.getRsLine52WeekHigh(sortedQuotations.getQuotations(), previousQuotation);

            if (currentQuotation.getRelativeStrengthData() != null
                    && currentQuotation.getRelativeStrengthData().getRsLinePrice() != null
                    && currentQuotation.getRelativeStrengthData().getRsLinePrice().floatValue() > maxRsLineHigh) {

                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                protocolEntry.setText(this.resources.getString("protocol.rsLineNew52WeekHigh"));
                protocolEntries.add(protocolEntry);
            }
        }

        return protocolEntries;
    }

    /**
     * Checks if the current Quotation closes near its high price.
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation closes near its high price; false, if not.
     * @throws Exception Determination failed.
     */
    private boolean isCloseNearHigh(final Quotation currentQuotation) throws Exception {
        BigDecimal dailyPriceRange;
        BigDecimal nearHighThresholdPrice;

        dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
        nearHighThresholdPrice = currentQuotation.getLow()
                .add(dailyPriceRange.multiply(new BigDecimal(CLOSE_NEAR_HIGH_THRESHOLD)));

        if (currentQuotation.getClose().compareTo(nearHighThresholdPrice) >= 0) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the current Quotation closes near its low price.
     *
     * @param currentQuotation The current Quotation.
     * @return true, if currentQuotation closes near its low price; false, if not.
     * @throws Exception Determination failed.
     */
    private boolean isCloseNearLow(final Quotation currentQuotation) throws Exception {
        BigDecimal dailyPriceRange;
        BigDecimal nearLowThresholdPrice;

        dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
        nearLowThresholdPrice = currentQuotation.getLow()
                .add(dailyPriceRange.multiply(new BigDecimal(CLOSE_NEAR_LOW_THRESHOLD)));

        if (currentQuotation.getClose().compareTo(nearLowThresholdPrice) <= 0) {
            return true;
        }

        return false;
    }

    /**
     * Determines the 52-week high on a closing basis.
     *
     * @param quotations   A list of quotations sorted by date that build the trading history.
     * @param endQuotation The latest Quotation for which the check is executed.
     * @return The 52-week high on a closing basis.
     */
    private float get52WeekHigh(final List<Quotation> quotations, final Quotation endQuotation) {
        Quotation currentQuotation;
        float maxClosingPrice = 0;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(endQuotation.getDate());
        calendar.add(Calendar.YEAR, -1);

        // Determine the highest closing price of the last 52 weeks.
        for (int i = 0; i <= quotations.size() - 1; i++) {
            currentQuotation = quotations.get(i);

            // Ignore quotations newer than the date of the end quotation.
            if (currentQuotation.getDate().getTime() > endQuotation.getDate().getTime()) {
                continue;
            }

            // Ignore quotations more than one year older than the date of the end quotation.
            if (currentQuotation.getDate().getTime() < calendar.getTimeInMillis()) {
                continue;
            }

            if (currentQuotation.getClose().floatValue() > maxClosingPrice) {
                maxClosingPrice = currentQuotation.getClose().floatValue();
            }
        }

        return maxClosingPrice;
    }

    /**
     * Determines the 52-week high of the RS-line on a closing basis.
     *
     * @param quotations   A list of quotations sorted by date that build the trading history.
     * @param endQuotation The latest Quotation for which the check is executed.
     * @return The 52-week high of the RS-Line on a closing basis.
     */
    private float getRsLine52WeekHigh(final List<Quotation> quotations, final Quotation endQuotation) {
        Quotation currentQuotation;
        float maxRsLinePrice = 0;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(endQuotation.getDate());
        calendar.add(Calendar.YEAR, -1);

        // Determine the highest closing price of the RS-line of the last 52 weeks.
        for (int i = 0; i <= quotations.size() - 1; i++) {
            currentQuotation = quotations.get(i);

            if (currentQuotation.getRelativeStrengthData() == null
                    || currentQuotation.getRelativeStrengthData().getRsLinePrice() == null) {
                continue;
            }

            // Ignore quotations newer than the date of the end quotation.
            if (currentQuotation.getDate().getTime() > endQuotation.getDate().getTime()) {
                continue;
            }

            // Ignore quotations more than one year older than the date of the end quotation.
            if (currentQuotation.getDate().getTime() < calendar.getTimeInMillis()) {
                continue;
            }

            if (currentQuotation.getRelativeStrengthData().getRsLinePrice().floatValue() > maxRsLinePrice) {
                maxRsLinePrice = currentQuotation.getRelativeStrengthData().getRsLinePrice().floatValue();
            }
        }

        return maxRsLinePrice;
    }
}
