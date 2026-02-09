package backend.controller.chart.data;

import java.util.List;

import backend.calculator.BollingerCalculator;
import backend.calculator.RatioCalculator;
import backend.calculator.StochasticCalculator;
import backend.controller.scan.IndicatorCalculationController;
import backend.dao.DAOManager;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.instrument.RelativeStrengthData;

/**
 * Controller to provide data for the construction of a price/volume chart.
 *
 * @author Michael
 */
public class PriceVolumeDataController {
    /**
     * DAO for Quotation access.
     */
    private QuotationDAO quotationDAO;

    /**
     * Initializes the PriceVolumeDataController.
     */
    public PriceVolumeDataController() {
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    /**
     * Provides price and volume data as well as indicators to construct a price/volume chart.
     *
     * @param instrumentId The ID of the Instrument used for chart data creation.
     * @return The price/volume chart data.
     * @throws Exception Chart data generation failed.
     */
    public QuotationArray getPriceVolumeData(final Integer instrumentId) throws Exception {
        QuotationArray quotations = new QuotationArray(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
        final int rsLineEmaPeriod = 21;

        quotations.sortQuotationsByDate();
        this.calculateBBWData(quotations);
        this.calculateSlowStochasticData(quotations);
        this.calculateRsLineData(quotations);
        this.calculateRsLineEma(quotations, rsLineEmaPeriod);

        return quotations;
    }

    /**
     * Calculates the Bollinger BandWidth for the given quotations.
     *
     * @param quotations An array of quotations.
     */
    private void calculateBBWData(final QuotationArray quotations) {
        BollingerCalculator bollingerCalculator = new BollingerCalculator();
        float bollingerBandWidth;
        final int bbwPeriodDays = 10;

        for (Quotation quotation : quotations.getQuotations()) {
            bollingerBandWidth = bollingerCalculator.getBollingerBandWidth(bbwPeriodDays, 2, quotation, quotations);

            if (bollingerBandWidth == 0) {
                continue;
            }

            if (quotation.getIndicator() == null) {
                quotation.setIndicator(new Indicator());
            }

            quotation.getIndicator().setBollingerBandWidth10Days(bollingerBandWidth);
        }
    }

    /**
     * Calculates the Slow Stochastic for the given quotations.
     *
     * @param quotations An array of quotations.
     */
    private void calculateSlowStochasticData(final QuotationArray quotations) {
        StochasticCalculator stochasticCalculator = new StochasticCalculator();
        float slowStochastic;
        final int periodDays = 14;
        final int smoothingDays = 3;

        for (Quotation quotation : quotations.getQuotations()) {
            slowStochastic = stochasticCalculator.getSlowStochastic(periodDays, smoothingDays, quotation, quotations);

            if (slowStochastic == 0) {
                continue;
            }

            if (quotation.getIndicator() == null) {
                quotation.setIndicator(new Indicator());
            }

            quotation.getIndicator().setSlowStochastic14Days(slowStochastic);
        }
    }

    /**
     * Calculates the Relative Strength Line for the given quotations. The industry group of the Instrument is used for
     * calculation.
     *
     * @param quotations An array of quotations.
     * @throws Exception Failed to calculate ratio for RS-Line.
     */
    public void calculateRsLineData(final QuotationArray quotations) throws Exception {
        Quotation targetQuotation;
        int quotationIndex;
        List<Quotation> ratioQuotations;
        RatioCalculator ratioCalculator = new RatioCalculator();
        Instrument dividendInstrument = new Instrument();
        Instrument divisorInstrument = new Instrument();
        Instrument industryGroup = quotations.getQuotations().get(0).getInstrument().getIndustryGroup();

        if (industryGroup == null) {
            // The RS-Line can only be calculated if the Instrument is related to an industry group.
            return;
        }

        dividendInstrument.setQuotations(quotations.getQuotations());
        divisorInstrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(industryGroup.getId()));

        if (dividendInstrument.getQuotations().size() == 0 || divisorInstrument.getQuotations().size() == 0) {
            // The RS-Line can only be calculated if quotations exist for both dividend and divisor.
            return;
        }

        ratioQuotations = ratioCalculator.getRatios(dividendInstrument, divisorInstrument);

        for (Quotation quotation : ratioQuotations) {
            quotationIndex = quotations.getIndexOfQuotationWithDate(quotation.getDate());
            targetQuotation = quotations.getQuotations().get(quotationIndex);

            if (targetQuotation == null) {
                continue;
            }

            if (targetQuotation.getRelativeStrengthData() == null) {
                targetQuotation.setRelativeStrengthData(new RelativeStrengthData());
            }

            targetQuotation.getRelativeStrengthData().setRsLinePrice(quotation.getClose());
        }
    }

    /**
     * Calculates the EMA(21) of the RS-line for the given quotations.
     *
     * @param quotations The quotations.
     * @param period     Them EMA period.
     */
    private void calculateRsLineEma(final QuotationArray quotations, final int period) {
        IndicatorCalculationController indicatorCalculator = new IndicatorCalculationController();
        Instrument rsLineInstrument = new Instrument();
        QuotationArray rsLineQuotations = new QuotationArray();
        Quotation quotation;
        Quotation srcQuotation;
        Quotation targetQuotation;

        // Move RS-line price to closing price attribute for following EMA(21) calculation.
        for (int i = 0; i < quotations.getQuotations().size(); i++) {
            srcQuotation = quotations.getQuotations().get(i);

            if (srcQuotation.getRelativeStrengthData() != null) {
                targetQuotation = new Quotation();
                targetQuotation.setDate(srcQuotation.getDate());
                targetQuotation.setClose(srcQuotation.getRelativeStrengthData().getRsLinePrice());
                rsLineQuotations.getQuotations().add(targetQuotation);
            }
        }

        // Initialize new Instrument with RS-line values as closing prices.
        rsLineQuotations.sortQuotationsByDate();
        rsLineInstrument.setQuotations(rsLineQuotations.getQuotations());

        // Calculate the EMA(21) for the Instrument. This later becomes the EMA(21) of the RS-line.
        for (int i = 0; i < rsLineQuotations.getQuotations().size(); i++) {
            quotation = rsLineQuotations.getQuotations().get(i);

            // Calculate moving averages.
            indicatorCalculator.calculateIndicators(rsLineInstrument, quotation, false);
        }

        // Write calculated EMA(21) of RS-line to target attribute.
        for (int i = 0; i < quotations.getQuotations().size(); i++) {
            targetQuotation = quotations.getQuotations().get(i);
            srcQuotation = rsLineQuotations.getNewestQuotationOfDate(targetQuotation.getDate());

            if (srcQuotation != null && srcQuotation.getMovingAverageData() != null
                    && targetQuotation.getRelativeStrengthData() != null) {
                targetQuotation.getRelativeStrengthData()
                        .setRsLineEma21(srcQuotation.getMovingAverageData().getEma21());
            }
        }
    }
}
