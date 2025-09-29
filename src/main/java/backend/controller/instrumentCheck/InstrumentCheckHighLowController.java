package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.util.ArrayList;
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
}
