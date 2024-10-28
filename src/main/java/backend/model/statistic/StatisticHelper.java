package backend.model.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helper class of the Statistic model.
 *
 * @author Michael
 */
public class StatisticHelper {
    /**
     * Gets the percentage of instruments above the SMA(50).
     *
     * @param statistic The Statistic needed for calculation.
     * @return The percentage of instruments above the SMA(50).
     */
    public float getPercentAboveSma50(final Statistic statistic) {
        BigDecimal percentAboveSma50;
        BigDecimal numAboveSma50;
        BigDecimal totalNumber;
        final int hundredPercent = 100;

        if ((statistic.getNumberAboveSma50() + statistic.getNumberAtOrBelowSma50()) != 0) {
            numAboveSma50 = new BigDecimal(statistic.getNumberAboveSma50());
            totalNumber = new BigDecimal(statistic.getNumberAboveSma50() + statistic.getNumberAtOrBelowSma50());

            percentAboveSma50 = numAboveSma50.multiply(new BigDecimal(hundredPercent)).divide(totalNumber, 0,
                    RoundingMode.HALF_UP);
            return percentAboveSma50.floatValue();
        }

        return 0;
    }

    /**
     * Gets the percentage of instruments above the SMA(200).
     *
     * @param statistic The Statistic needed for calculation.
     * @return The percentage of instruments above the SMA(200).
     */
    public float getPercentAboveSma200(final Statistic statistic) {
        BigDecimal percentAboveSma200;
        BigDecimal numAboveSma200;
        BigDecimal totalNumber;
        final int hundredPercent = 100;

        if ((statistic.getNumberAboveSma200() + statistic.getNumberAtOrBelowSma200()) != 0) {
            numAboveSma200 = new BigDecimal(statistic.getNumberAboveSma200());
            totalNumber = new BigDecimal(statistic.getNumberAboveSma200() + statistic.getNumberAtOrBelowSma200());

            percentAboveSma200 = numAboveSma200.multiply(new BigDecimal(hundredPercent)).divide(totalNumber, 0,
                    RoundingMode.HALF_UP);
            return percentAboveSma200.floatValue();
        }

        return 0;
    }
}
