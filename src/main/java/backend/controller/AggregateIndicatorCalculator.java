package backend.controller;

import java.util.ArrayList;
import java.util.List;

import backend.controller.scan.StochasticCalculator;
import backend.dao.DAOManager;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.statistic.Statistic;
import backend.model.statistic.StatisticArray;

/**
 * Calculates the Aggregate Indicator.
 *
 * @author Michael
 */
public class AggregateIndicatorCalculator {
    /**
     * DAO for Statistic access.
     */
    private StatisticDAO statisticDAO;

    /**
     * Initializes the AggregateIndicatorCalculator.
     */
    public AggregateIndicatorCalculator() {
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
    }

    /**
     * Determines the statistics for the given Instrument.
     *
     * @param instrument The Instrument that constitutes a sector or industry group.
     * @return The statistics.
     * @throws Exception Determination of statistics failed.
     */
    public List<Statistic> getStatistics(final Instrument instrument) throws Exception {
        List<Statistic> statistics = new ArrayList<>();

        if (instrument.getType() == InstrumentType.SECTOR) {
            statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, instrument.getId(), null);
        } else if (instrument.getType() == InstrumentType.IND_GROUP) {
            statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, null, instrument.getId());
        }

        return statistics;
    }

    /**
     * Calculates the Aggregate Indicator for the given Quotation.
     *
     * @param quotationsSortedByDate A List of quotations that build the trading history.
     * @param statistics             A List of statistics.
     * @param quotation              The Quotation for which the Aggregate Indicator is calculated.
     * @param instrument             The Instrument for which the Aggregate Indicator is calculated.
     * @return The value of the aggregate indicator or -1, if calculation failed.
     */
    public int getAggregateIndicator(final List<Quotation> quotationsSortedByDate, final List<Statistic> statistics,
            final Quotation quotation, final Instrument instrument) {

        float slowStochasticDaily = this.getSlowStochasticDaily(quotationsSortedByDate, quotation);
        float slowStochasticWeekly = this.getSlowStochasticWeekly(quotationsSortedByDate, quotation);
        float percentAboveSma50 = this.getSma10OfPercentAboveSma50(quotationsSortedByDate, statistics, quotation,
                instrument);
        float aggregateIndicator;
        final int threeComponents = 3;

        // If any component of the aggregate indicator is missing, the value can't be calculated.
        // -1 informs the frontend about an incomplete calculation.
        if (slowStochasticDaily == 0 || slowStochasticWeekly == 0 || percentAboveSma50 == -1) {
            return -1;
        }

        aggregateIndicator = slowStochasticDaily + slowStochasticWeekly + percentAboveSma50;
        aggregateIndicator = aggregateIndicator / threeComponents;

        return Math.round(aggregateIndicator);
    }

    /**
     * Determines the daily Slow Stochastic.
     *
     * @param quotationsSortedByDate A List of quotations that build the trading history.
     * @param quotation              The Quotation for which the daily Slow Stochastic is calculated.
     * @return The daily Slow Stochastic.
     */
    private float getSlowStochasticDaily(final List<Quotation> quotationsSortedByDate, final Quotation quotation) {
        QuotationArray quotations = new QuotationArray(quotationsSortedByDate);
        StochasticCalculator stochasticCalculator = new StochasticCalculator();
        final int slowStochasticPeriodDays = 14;
        final int smoothingPeriodDays = 3;
        float slowStochasticDaily;

        slowStochasticDaily = stochasticCalculator.getSlowStochastic(slowStochasticPeriodDays, smoothingPeriodDays,
                quotation, quotations);

        return slowStochasticDaily;
    }

    /**
     * Determines the weekly Slow Stochastic.
     *
     * @param quotationsSortedByDate A List of quotations that build the trading history.
     * @param quotation              The Quotation for which the weekly Slow Stochastic is calculated.
     * @return The weekly Slow Stochastic.
     */
    private float getSlowStochasticWeekly(final List<Quotation> quotationsSortedByDate, final Quotation quotation) {
        QuotationArray quotations = new QuotationArray(quotationsSortedByDate);
        QuotationArray weeklyQuotations = new QuotationArray(quotations.getWeeklyQuotations(quotation));
        StochasticCalculator stochasticCalculator = new StochasticCalculator();
        final int slowStochasticPeriodWeeks = 14;
        final int smoothingPeriodWeeks = 3;
        float slowStochasticWeekly;

        slowStochasticWeekly = stochasticCalculator.getSlowStochastic(slowStochasticPeriodWeeks, smoothingPeriodWeeks,
                weeklyQuotations.getQuotations().get(0), weeklyQuotations);

        return slowStochasticWeekly;
    }

    /**
     * Determines the SMA(10) of percentage of instruments above SMA(50).
     *
     * @param quotationsSortedByDate A List of quotations that build the trading history.
     * @param statistics             A List of statistics.
     * @param quotation              The Quotation for which the value is calculated.
     * @param instrument             The Instrument for which the Aggregate Indicator is calculated.
     * @return The SMA(10) of percentage of instruments above SMA(50).
     */
    private float getSma10OfPercentAboveSma50(final List<Quotation> quotationsSortedByDate,
            final List<Statistic> statistics, final Quotation quotation, final Instrument instrument) {

        StatisticArray statisticArray = new StatisticArray(statistics);
        Statistic statistic;
        Quotation currentQuotation;
        Integer sectorId = null;
        Integer industryGroupId = null;
        float percentAboveSma50 = 0;
        final int tenDays = 10;
        int startIndex = quotationsSortedByDate.indexOf(quotation);
        int endIndex = startIndex + tenDays;

        // At least 10 statistics have to exist in order to calculate the SMA(10).
        if (statistics.size() - tenDays - startIndex < 0) {
            return -1;
        }

        if (instrument.getType() == InstrumentType.SECTOR) {
            sectorId = instrument.getId();
        }

        if (instrument.getType() == InstrumentType.IND_GROUP) {
            industryGroupId = instrument.getId();
        }

        // calculate SMA(10) of the latest 'instruments above SMA(50)' metric.
        // The statistics of the last 10 trading days of the sector or industry group are used.
        for (int i = startIndex; i < endIndex; i++) {
            currentQuotation = quotationsSortedByDate.get(i);
            statistic = statisticArray.getStatistic(currentQuotation.getDate(), sectorId, industryGroupId);

            // If any statistic of the last 10 trading days is not available, the SMA(10) can't be calculated.
            if (statistic == null) {
                return -1;
            }

            percentAboveSma50 = percentAboveSma50 + statistic.getPercentAboveSma50();
        }

        percentAboveSma50 = percentAboveSma50 / tenDays;

        return percentAboveSma50;
    }
}
