package backend.controller.scan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationDistanceTo52WeekHighComparator;
import backend.model.instrument.QuotationRsPercentSumComparator;
import backend.model.instrument.QuotationUpDownVolumeRatioComparator;
import backend.model.instrument.RelativeStrengthData;

/**
 * Performs calculations of multiple relative strength values based on the instruments quotations.
 *
 * @author Michael
 */
public class RelativeStrengthCalculator {
    /**
     * Factor used to format results as percent.
     */
    private static final int HUNDRED_PERCENT = 100;

    /**
     * Calculates the rsNumber.
     *
     * @param quotations The quotations on which the calculation of the rsNumber is based.
     */
    public void calculateRsNumber(final List<Quotation> quotations) {
        RelativeStrengthData rsData;
        BigDecimal rsNumber;
        BigDecimal dividend;
        BigDecimal numberOfElements;

        Collections.sort(quotations, new QuotationRsPercentSumComparator());
        numberOfElements = BigDecimal.valueOf(quotations.size());

        for (int i = 0; i < quotations.size(); i++) {
            dividend = numberOfElements.subtract(BigDecimal.valueOf(i));
            rsNumber = dividend.divide(numberOfElements, 2, RoundingMode.HALF_UP);
            rsNumber = rsNumber.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

            rsData = quotations.get(i).getIndicator().getRelativeStrengthData();
            if (rsData != null) {
                rsData.setRsNumber(rsNumber.intValue());
            }
        }
    }

    /**
     * Calculates the RS number that measures the distance to the 52-week high.
     *
     * @param quotations The quotations on which the calculation of the rsNumberDistance52WeekHigh is based.
     */
    public void calculateRsNumberDistanceTo52wHigh(final List<Quotation> quotations) {
        RelativeStrengthData rsData;
        BigDecimal rsNumber;
        BigDecimal dividend;
        BigDecimal numberOfElements;

        Collections.sort(quotations, new QuotationDistanceTo52WeekHighComparator());
        numberOfElements = BigDecimal.valueOf(quotations.size());

        for (int i = 0; i < quotations.size(); i++) {
            dividend = numberOfElements.subtract(BigDecimal.valueOf(i));
            rsNumber = dividend.divide(numberOfElements, 2, RoundingMode.HALF_UP);
            rsNumber = rsNumber.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

            rsData = quotations.get(i).getIndicator().getRelativeStrengthData();
            if (rsData != null) {
                rsData.setRsNumberDistance52WeekHigh(rsNumber.intValue());
            }
        }
    }

    /**
     * Calculates the RS number that measures the upDownVolumeRatio.
     *
     * @param quotations The quotations on which the calculation of the rsNumberUpDownVolumeRatio is based.
     */
    public void calculateRsNumberUpDownVolumeRatio(final List<Quotation> quotations) {
        RelativeStrengthData rsData;
        BigDecimal rsNumber;
        BigDecimal dividend;
        BigDecimal numberOfElements;

        Collections.sort(quotations, new QuotationUpDownVolumeRatioComparator());
        numberOfElements = BigDecimal.valueOf(quotations.size());

        for (int i = 0; i < quotations.size(); i++) {
            dividend = numberOfElements.subtract(BigDecimal.valueOf(i));
            rsNumber = dividend.divide(numberOfElements, 2, RoundingMode.HALF_UP);
            rsNumber = rsNumber.multiply(BigDecimal.valueOf(HUNDRED_PERCENT));

            rsData = quotations.get(i).getIndicator().getRelativeStrengthData();
            if (rsData != null) {
                rsData.setRsNumberUpDownVolumeRatio(rsNumber.intValue());
            }
        }
    }
}
