package backend.calculator;

import java.math.BigDecimal;

import backend.controller.instrumentCheck.PatternControllerHelper;
import backend.model.instrument.MovingAverageData;
import backend.model.instrument.Quotation;

/**
 * Calculates statistical values.
 *
 * @author Michael
 */
public class StatisticCalculator {
    /**
     * Helper class for pattern-related tasks.
     */
    private PatternControllerHelper patternControllerHelper;

    /**
     * Initializes the StatisticCalculator.
     */
    public StatisticCalculator() {
        this.patternControllerHelper = new PatternControllerHelper();
    }

    /**
     * Compares the price of the current and previous Quotation and checks if the Instrument has advanced.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if price of current Quotation is bigger than price of previous Quotation. 0 if not.
     */
    public int getNumberAdvance(final Quotation currentQuotation, final Quotation previousQuotation) {
        if (currentQuotation.getClose().compareTo(previousQuotation.getClose()) == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares the price of the current and previous Quotation and checks if the Instrument has declined.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if price of current Quotation is smaller than price of previous Quotation. 0 if not.
     */
    public int getNumberDecline(final Quotation currentQuotation, final Quotation previousQuotation) {
        if (currentQuotation.getClose().compareTo(previousQuotation.getClose()) == -1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares the price of the current Quotation with its SMA(50) and checks if the price is above its SMA(50).
     *
     * @param currentQuotation The current Quotation.
     * @return 1, if the price of the current Quotation is bigger than its SMA(50). 0, if it is less or equal.
     */
    public int getNumberAboveSma50(final Quotation currentQuotation) {
        BigDecimal sma50;
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        if (maData == null || maData.getSma50() == 0) {
            return 0;
        }

        sma50 = BigDecimal.valueOf(maData.getSma50());

        if (currentQuotation.getClose().compareTo(sma50) == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares the price of the current Quotation with its SMA(50) and checks if the price is below its SMA(50).
     *
     * @param currentQuotation The current Quotation.
     * @return 1, if the price of the current Quotation is at or below its SMA(50). 0, if it is bigger.
     */
    public int getNumberAtOrBelowSma50(final Quotation currentQuotation) {
        BigDecimal sma50;
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        if (maData == null || maData.getSma50() == 0) {
            return 0;
        }

        sma50 = BigDecimal.valueOf(maData.getSma50());

        if (currentQuotation.getClose().compareTo(sma50) == -1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares the price of the current Quotation with its SMA(200) and checks if the price is above its SMA(200).
     *
     * @param currentQuotation The current Quotation.
     * @return 1, if the price of the current Quotation is bigger than its SMA(200). 0, if it is less or equal.
     */
    public int getNumberAboveSma200(final Quotation currentQuotation) {
        BigDecimal sma200;
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        if (maData == null || maData.getSma200() == 0) {
            return 0;
        }

        sma200 = BigDecimal.valueOf(maData.getSma200());

        if (currentQuotation.getClose().compareTo(sma200) == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares the price of the current Quotation with its SMA(200) and checks if the price is below its SMA(200).
     *
     * @param currentQuotation The current Quotation.
     * @return 1, if the price of the current Quotation is at or below its SMA(200). 0, if it is bigger.
     */
    public int getNumberAtOrBelowSma200(final Quotation currentQuotation) {
        BigDecimal sma200;
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        if (maData == null || maData.getSma200() == 0) {
            return 0;
        }

        sma200 = BigDecimal.valueOf(maData.getSma200());

        if (currentQuotation.getClose().compareTo(sma200) == -1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Calculates the number of the Ritter Market Trend for the current Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if Quotation behaves bullish, -1 if it behaves bearish, 0 if behavior is neither bullish nor bearish.
     */
    public int getNumberRitterMarketTrend(final Quotation currentQuotation, final Quotation previousQuotation) {
        MovingAverageData maData = currentQuotation.getMovingAverageData();

        // The indicator can't be calculated if these values are not available.
        if (maData == null || maData.getSma30Volume() == 0) {
            return 0;
        }

        // Rising price.
        if (currentQuotation.getClose().compareTo(previousQuotation.getClose()) == 1) {
            if (currentQuotation.getVolume() >= maData.getSma30Volume()) {
                return 1;
            } else {
                return -1;
            }
        }

        // Falling price.
        if (currentQuotation.getClose().compareTo(previousQuotation.getClose()) == -1) {
            if (currentQuotation.getVolume() >= maData.getSma30Volume()) {
                return -1;
            } else {
                return 1;
            }
        }

        // Price unchanged.
        return 0;
    }

    /**
     * Calculates the "up on volume" number for the current Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if Quotation traded up on volume; 0, if not.
     */
    public int getNumberUpOnVolume(final Quotation currentQuotation, final Quotation previousQuotation) {
        boolean isUpOnVolume = false;

        try {
            isUpOnVolume = this.patternControllerHelper.isUpOnVolume(currentQuotation, previousQuotation);
        } catch (Exception e) {
            return 0;
        }

        if (isUpOnVolume) {
            return 1;
        }

        return 0;
    }

    /**
     * Calculates the "down on volume" number for the current Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if Quotation traded down on volume; 0, if not.
     */
    public int getNumberDownOnVolume(final Quotation currentQuotation, final Quotation previousQuotation) {
        boolean isDownOnVolume = false;

        try {
            isDownOnVolume = this.patternControllerHelper.isDownOnVolume(currentQuotation, previousQuotation);
        } catch (Exception e) {
            return 0;
        }

        if (isDownOnVolume) {
            return 1;
        }

        return 0;
    }

    /**
     * Calculates the "bearish high-volume reversal" number for the current Quotation.
     *
     * @param currentQuotation The current Quotation.
     * @return 1, if Quotation made a bearish reversal; 0, if not.
     */
    public int getNumberBearishReversal(final Quotation currentQuotation) {
        boolean isBearishReversal = false;

        try {
            isBearishReversal = this.patternControllerHelper.isBearishHighVolumeReversal(currentQuotation);
        } catch (Exception e) {
            return 0;
        }

        if (isBearishReversal) {
            return 1;
        }

        return 0;
    }

    /**
     * Calculates the "bullish high-volume reversal" number for the current Quotation.
     *
     * @param currentQuotation The current Quotation.
     * @return 1, if Quotation made a bullish reversal; 0, if not.
     */
    public int getNumberBullishReversal(final Quotation currentQuotation) {
        boolean isBullishReversal = false;

        try {
            isBullishReversal = this.patternControllerHelper.isBullishHighVolumeReversal(currentQuotation);
        } catch (Exception e) {
            return 0;
        }

        if (isBullishReversal) {
            return 1;
        }

        return 0;
    }

    /**
     * Calculates the "churning" number for the current Quotation.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if Quotation is churning; 0, if not.
     */
    public int getNumberChurning(final Quotation currentQuotation, final Quotation previousQuotation) {
        boolean isChurning = false;

        try {
            isChurning = this.patternControllerHelper.isChurning(currentQuotation, previousQuotation);
        } catch (Exception e) {
            return 0;
        }

        if (isChurning) {
            return 1;
        }

        return 0;
    }
}
