package backend.controller.instrumentCheck;

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
 * Controller that performs Instrument health checks in relation to moving averages. For example this can be a close
 * below the SMA(50).
 *
 * @author Michael
 */
public class InstrumentCheckAverageController {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

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

            if (previousDayQuotation.getIndicator() == null) {
                throw new Exception("No indicator is defined for Quotation with ID: " + previousDayQuotation.getId());
            }

            if (currentDayQuotation.getIndicator() == null) {
                throw new Exception("No indicator is defined for Quotation with ID: " + currentDayQuotation.getId());
            }

            if (previousDayQuotation.getClose().floatValue() >= previousDayQuotation.getIndicator().getSma50()
                    && currentDayQuotation.getClose().floatValue() < currentDayQuotation.getIndicator().getSma50()) {

                protocolEntry = new ProtocolEntry();
                protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
                protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentDayQuotation.getDate()));

                if (currentDayQuotation.getVolume() >= currentDayQuotation.getIndicator().getSma30Volume()) {
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

            if (previousDayQuotation.getIndicator() == null) {
                throw new Exception("No indicator is defined for Quotation with ID: " + previousDayQuotation.getId());
            }

            if (currentDayQuotation.getIndicator() == null) {
                throw new Exception("No indicator is defined for Quotation with ID: " + currentDayQuotation.getId());
            }

            if (previousDayQuotation.getClose().floatValue() >= previousDayQuotation.getIndicator().getEma21()
                    && currentDayQuotation.getClose().floatValue() < currentDayQuotation.getIndicator().getEma21()) {

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
     * Checks if the price is extended above the SMA(200) on a closing basis. The check begins at the start date and
     * goes up until the most recent Quotation.
     *
     * For each day on which price is extended above the SMA(200), a ProtocolEntry is provided with further information.
     *
     * @param startDate        The date at which the check starts.
     * @param sortedQuotations The quotations sorted by date that build the trading history.
     * @return List of ProtocolEntry, for all days on which the price is extended above the SMA(200).
     * @throws Exception The check failed because data are not fully available or corrupt.
     */
    public List<ProtocolEntry> checkExtendedAboveSma200(final Date startDate, final QuotationArray sortedQuotations)
            throws Exception {

        List<ProtocolEntry> protocolEntries = new ArrayList<>();

        return protocolEntries;
    }
}
