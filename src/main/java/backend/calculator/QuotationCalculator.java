package backend.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationDateComparator;
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
        List<Date> instrumentDates = this.getSortedDatesOfInstruments(instruments);
        List<Quotation> calculatedQuotations = new ArrayList<>();
        Quotation calculatedQuotation;

        for (Date date : instrumentDates) {
            calculatedQuotation = this.getQuotationOfDate(date, instruments);
            calculatedQuotations.add(calculatedQuotation);
        }

        Collections.sort(calculatedQuotations, new QuotationDateComparator());

        return calculatedQuotations;
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

    /**
     * Provides a sorted List of all Quotation dates. All quotations of every Instrument are taken into account. Each
     * date is only contained once. Intraday attributes of the dates are set to 0.
     *
     * @param instruments A List of instruments with their quotations.
     * @return A sorted List of all dates. The newest date has index 0.
     */
    private List<Date> getSortedDatesOfInstruments(final List<Instrument> instruments) {
        HashSet<Date> dates = this.getQuotationDates(instruments);
        List<Date> sortedDates = new ArrayList<>(dates);

        // Oldest date first.
        Collections.sort(sortedDates);

        // Newest date first.
        Collections.reverse(sortedDates);

        return sortedDates;
    }

    /**
     * Calculates the Quotation for the given date based on the given instruments.
     *
     * @param date        The date of the calculated Quotation.
     * @param instruments A List of instruments with their quotations.
     * @return The calculated Quotation.
     */
    private Quotation getQuotationOfDate(final Date date, final List<Instrument> instruments) {
        Quotation quotation;
        Quotation calculatedQuotation = new Quotation();
        int numberQuotations = 0;
        BigDecimal calculatedOpen = new BigDecimal(0);
        BigDecimal calculatedHigh = new BigDecimal(0);
        BigDecimal calculatedLow = new BigDecimal(0);
        BigDecimal calculatedClose = new BigDecimal(0);
        BigDecimal calculatedVolume = new BigDecimal(0);
        final int scale = 3;

        for (Instrument instrument : instruments) {
            quotation = instrument.getNewestQuotation(date);

            if (quotation == null) {
                continue;
            }

            calculatedOpen = calculatedOpen.add(quotation.getOpen());
            calculatedHigh = calculatedHigh.add(quotation.getHigh());
            calculatedLow = calculatedLow.add(quotation.getLow());
            calculatedClose = calculatedClose.add(quotation.getClose());
            calculatedVolume = calculatedVolume.add(new BigDecimal(quotation.getVolume()));
            numberQuotations++;
        }

        calculatedQuotation
                .setOpen(calculatedOpen.divide(new BigDecimal(numberQuotations), scale, RoundingMode.HALF_UP));

        calculatedQuotation
                .setHigh(calculatedHigh.divide(new BigDecimal(numberQuotations), scale, RoundingMode.HALF_UP));

        calculatedQuotation.setLow(calculatedLow.divide(new BigDecimal(numberQuotations), scale, RoundingMode.HALF_UP));

        calculatedQuotation
                .setClose(calculatedClose.divide(new BigDecimal(numberQuotations), scale, RoundingMode.HALF_UP));

        calculatedQuotation.setVolume(
                calculatedVolume.divide(new BigDecimal(numberQuotations), 0, RoundingMode.HALF_UP).longValue());

        calculatedQuotation.setDate(date);

        return calculatedQuotation;
    }
}
