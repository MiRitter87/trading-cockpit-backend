package backend.controller.scan;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.tools.DateTools;

/**
 * Calculates quotations based on existing quotations. A possible use-case is to calculate the quotations of an index
 * based on the index components quotations.
 *
 * @author Michael
 */
public class QuotationCalculator {
    /**
     * The number of trading days per year.
     */
    private static final int TRADING_DAYS_PER_YEAR = 252;

    /**
     * Provides a List of calculated quotations based on the given List of instruments with their quotations.
     *
     * @param instruments A List of instruments with their quotations.
     * @return A List of calculated quotations.
     */
    public List<Quotation> getCalculatedQuotations(final List<Instrument> instruments) {
        return null;
    }

    /**
     * Provides a HashSet of all dates for which quotations exist. The intraday-attributes of the dates are clipped.
     * Only date, month and year are stored for each date.
     *
     * @param instruments A List of instruments with their quotations.
     * @return A HashSet of all dates for which quotations exist.
     */
    public HashSet<Date> getQuotationDates(final List<Instrument> instruments) {
        HashSet<Date> dates = new HashSet<>(TRADING_DAYS_PER_YEAR);
        Date date;

        for (Instrument instrument : instruments) {
            for (Quotation quotation : instrument.getQuotations()) {
                date = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());

                if (!dates.contains(date)) {
                    dates.add(date);
                }
            }
        }

        return dates;
    }
}
