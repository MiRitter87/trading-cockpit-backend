package backend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;

/**
 * Calculates the price ratio between two instruments.
 *
 * @author Michael
 */
public class RatioCalculator {
    /**
     * Calculates the List of price ratios between the given dividend and divisor instruments.
     *
     * @param dividendInstrument Dividend Instrument with quotations for ratio calculation.
     * @param divisorInstrument  Divisor Instrument with quotations for ratio calculation.
     * @return A List of quotations that constitute the price ratio between dividend and divisor.
     * @throws Exception Ratio calculation failed.
     */
    public List<Quotation> getRatios(final Instrument dividendInstrument, final Instrument divisorInstrument)
            throws Exception {
        List<Quotation> ratios;

        this.checkQuotationsExistForRatio(dividendInstrument, divisorInstrument);
        ratios = this.calculateRatios(dividendInstrument, divisorInstrument);

        return ratios;
    }

    /**
     * Checks if quotations exist for both instruments of a ratio.
     *
     * @param dividendInstrument The Instrument being the dividend.
     * @param divisorInstrument  The Instrument being the divisor.
     * @throws Exception In case no quotations exist for at least one Instrument.
     */
    private void checkQuotationsExistForRatio(final Instrument dividendInstrument, final Instrument divisorInstrument)
            throws Exception {
        if (dividendInstrument.getQuotations().size() == 0) {
            throw new Exception("No quotations exist for Instrument with ID " + dividendInstrument.getId());
        }

        if (divisorInstrument.getQuotations().size() == 0) {
            throw new Exception("No quotations exist for Instrument with ID " + divisorInstrument.getId());
        }
    }

    /**
     * Calculates the ratio quotations.
     *
     * @param dividendInstrument Dividend Instrument with quotations for ratio calculation.
     * @param divisorInstrument  Divisor Instrument with quotations for ratio calculation.
     * @return A List of quotations that constitute the price ratio between dividend and divisor.
     */
    private List<Quotation> calculateRatios(final Instrument dividendInstrument, final Instrument divisorInstrument) {
        List<Quotation> ratios = new ArrayList<>();
        Quotation divisorQuotation;
        Quotation ratioQuotation;
        QuotationArray quotationArray = new QuotationArray();

        for (Quotation dividendQuotation : dividendInstrument.getQuotationsSortedByDate()) {
            quotationArray.setQuotations(divisorInstrument.getQuotations());
            divisorQuotation = quotationArray.getNewestQuotationOfDate(dividendQuotation.getDate());

            if (divisorQuotation == null) {
                continue;
            }

            ratioQuotation = this.getRatioQuotation(dividendQuotation, divisorQuotation);
            ratios.add(ratioQuotation);
        }

        return ratios;
    }

    /**
     * Calculates a Quotation as ratio between two quotation.
     *
     * @param dividendQuotation The dividend.
     * @param divisorQuotation  The divisor.
     * @return The Quotation as ratio between dividend and divisor.
     */
    private Quotation getRatioQuotation(final Quotation dividendQuotation, final Quotation divisorQuotation) {
        Quotation ratioQuotation = new Quotation();
        BigDecimal ratioPrice;
        final int scale = 3;

        ratioQuotation.setDate(dividendQuotation.getDate());
        ratioQuotation.setCurrency(dividendQuotation.getCurrency());

        // A calculated ratio can consist of lot of values below 1.
        // In this case rounding to two decimal places is not enough.
        ratioPrice = dividendQuotation.getOpen().divide(divisorQuotation.getOpen(), scale, RoundingMode.HALF_UP);
        ratioQuotation.setOpen(ratioPrice);

        ratioPrice = dividendQuotation.getHigh().divide(divisorQuotation.getHigh(), scale, RoundingMode.HALF_UP);
        ratioQuotation.setHigh(ratioPrice);

        ratioPrice = dividendQuotation.getLow().divide(divisorQuotation.getLow(), scale, RoundingMode.HALF_UP);
        ratioQuotation.setLow(ratioPrice);

        ratioPrice = dividendQuotation.getClose().divide(divisorQuotation.getClose(), scale, RoundingMode.HALF_UP);
        ratioQuotation.setClose(ratioPrice);

        return ratioQuotation;
    }
}
