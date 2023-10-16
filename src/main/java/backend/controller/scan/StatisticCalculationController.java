package backend.controller.scan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import backend.controller.instrumentCheck.InstrumentCheckPatternController;
import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;
import backend.model.statistic.StatisticArray;
import backend.tools.DateTools;

/**
 * Controls the creation and update of statistical data.
 *
 * @author Michael
 */
public class StatisticCalculationController {
    /**
     * DAO to access Instrument data.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private QuotationDAO quotationDAO;

    /**
     * DAO to access Statistic data.
     */
    private StatisticDAO statisticDAO;

    /**
     * Default constructor.
     */
    public StatisticCalculationController() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
    }

    /**
     * Updates the statistic.
     *
     * @throws Exception Statistic calculation or database access failed.
     */
    public void updateStatistic() throws Exception {
        List<Instrument> stocks;
        List<Statistic> statisticNew;

        stocks = instrumentDAO.getInstruments(InstrumentType.STOCK);
        statisticNew = this.calculateStatistics(stocks);
        this.persistStatistics(statisticNew);
    }

    /**
     * Calculates the statistics based on the given instruments.
     *
     * @param instruments The instruments for which the statistics are to be calculated.
     * @return The statistics.
     * @throws Exception Statistic calculation failed.
     */
    public List<Statistic> calculateStatistics(final List<Instrument> instruments) throws Exception {
        StatisticArray statistics = new StatisticArray();
        Statistic statistic;
        List<Quotation> quotationsSortedByDate;
        Quotation previousQuotation;
        int currentQuotationIndex;

        for (Instrument instrument : instruments) {
            instrument.setQuotations(quotationDAO.getQuotationsOfInstrument(instrument.getId()));
            quotationsSortedByDate = instrument.getQuotationsSortedByDate();

            for (Quotation currentQuotation : quotationsSortedByDate) {
                currentQuotationIndex = quotationsSortedByDate.indexOf(currentQuotation);

                // Stop Statistic calculation for the current Instrument if no previous Quotation exists.
                if (currentQuotationIndex == (quotationsSortedByDate.size() - 1)) {
                    break;
                }

                previousQuotation = quotationsSortedByDate.get(currentQuotationIndex + 1);
                statistic = statistics.getStatisticOfDate(currentQuotation.getDate());

                if (statistic == null) {
                    statistic = new Statistic();
                    statistic.setInstrumentType(instrument.getType());
                    statistic.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
                    statistics.addStatistic(statistic);
                }

                // Calculate statistical values.
                statistic.setNumberOfInstruments(statistic.getNumberOfInstruments() + 1);
                statistic.setNumberAdvance(
                        statistic.getNumberAdvance() + this.getNumberAdvance(currentQuotation, previousQuotation));
                statistic.setNumberDecline(
                        statistic.getNumberDecline() + this.getNumberDecline(currentQuotation, previousQuotation));
                statistic.setNumberAboveSma50(
                        statistic.getNumberAboveSma50() + this.getNumberAboveSma50(currentQuotation));
                statistic.setNumberAtOrBelowSma50(
                        statistic.getNumberAtOrBelowSma50() + this.getNumberAtOrBelowSma50(currentQuotation));
                statistic.setNumberAboveSma200(
                        statistic.getNumberAboveSma200() + this.getNumberAboveSma200(currentQuotation));
                statistic.setNumberAtOrBelowSma200(
                        statistic.getNumberAtOrBelowSma200() + this.getNumberAtOrBelowSma200(currentQuotation));
                statistic.setNumberRitterMarketTrend(statistic.getNumberRitterMarketTrend()
                        + this.getNumberRitterMarketTrend(currentQuotation, previousQuotation));
                statistic.setNumberUpOnVolume(statistic.getNumberUpOnVolume()
                        + this.getNumberUpOnVolume(currentQuotation, previousQuotation));
                statistic.setNumberDownOnVolume(statistic.getNumberDownOnVolume()
                        + this.getNumberDownOnVolume(currentQuotation, previousQuotation));
            }
        }

        return statistics.getStatisticsSortedByDate();
    }

    /**
     * Compares the price of the current and previous Quotation and checks if the Instrument has advanced.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return 1, if price of current Quotation is bigger than price of previous Quotation. 0 if not.
     */
    private int getNumberAdvance(final Quotation currentQuotation, final Quotation previousQuotation) {
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
    private int getNumberDecline(final Quotation currentQuotation, final Quotation previousQuotation) {
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
    private int getNumberAboveSma50(final Quotation currentQuotation) {
        BigDecimal sma50;

        if (currentQuotation.getIndicator() == null || currentQuotation.getIndicator().getSma50() == 0) {
            return 0;
        }

        sma50 = BigDecimal.valueOf(currentQuotation.getIndicator().getSma50());

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
    private int getNumberAtOrBelowSma50(final Quotation currentQuotation) {
        BigDecimal sma50;

        if (currentQuotation.getIndicator() == null || currentQuotation.getIndicator().getSma50() == 0) {
            return 0;
        }

        sma50 = BigDecimal.valueOf(currentQuotation.getIndicator().getSma50());

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
    private int getNumberAboveSma200(final Quotation currentQuotation) {
        BigDecimal sma200;

        if (currentQuotation.getIndicator() == null || currentQuotation.getIndicator().getSma200() == 0) {
            return 0;
        }

        sma200 = BigDecimal.valueOf(currentQuotation.getIndicator().getSma200());

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
    private int getNumberAtOrBelowSma200(final Quotation currentQuotation) {
        BigDecimal sma200;

        if (currentQuotation.getIndicator() == null || currentQuotation.getIndicator().getSma200() == 0) {
            return 0;
        }

        sma200 = BigDecimal.valueOf(currentQuotation.getIndicator().getSma200());

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
    private int getNumberRitterMarketTrend(final Quotation currentQuotation, final Quotation previousQuotation) {
        // The indicator can't be calculated if these values are not available.
        if (currentQuotation.getIndicator() == null || currentQuotation.getIndicator().getSma30Volume() == 0) {
            return 0;
        }

        // Rising price.
        if (currentQuotation.getClose().compareTo(previousQuotation.getClose()) == 1) {
            if (currentQuotation.getVolume() >= currentQuotation.getIndicator().getSma30Volume()) {
                return 1;
            } else {
                return -1;
            }
        }

        // Falling price.
        if (currentQuotation.getClose().compareTo(previousQuotation.getClose()) == -1) {
            if (currentQuotation.getVolume() >= currentQuotation.getIndicator().getSma30Volume()) {
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
    private int getNumberUpOnVolume(final Quotation currentQuotation, final Quotation previousQuotation) {
        InstrumentCheckPatternController patternController = new InstrumentCheckPatternController();
        boolean isUpOnVolume = false;

        try {
            isUpOnVolume = patternController.isUpOnVolume(currentQuotation, previousQuotation);
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
    private int getNumberDownOnVolume(final Quotation currentQuotation, final Quotation previousQuotation) {
        InstrumentCheckPatternController patternController = new InstrumentCheckPatternController();
        boolean isDownOnVolume = false;

        try {
            isDownOnVolume = patternController.isDownOnVolume(currentQuotation, previousQuotation);
        } catch (Exception e) {
            return 0;
        }

        if (isDownOnVolume) {
            return 1;
        }

        return 0;
    }

    /**
     * Persists statistics to the database.
     *
     * @param newStatistics The new statistics that have been calculated during runtime based on the current database
     *                      state of quotation data.
     * @throws Exception In case persisting failed.
     */
    private void persistStatistics(final List<Statistic> newStatistics) throws Exception {
        List<Statistic> statisticsDatabase;
        List<Statistic> statisticsForInsertion;
        List<Statistic> statisticsForUpdate;
        List<Statistic> statisticsForDeletion;

        statisticsDatabase = statisticDAO.getStatistics(InstrumentType.STOCK);

        statisticsForInsertion = this.getStatisticsForInsertion(newStatistics, statisticsDatabase);
        statisticsForDeletion = this.getStatisticsForDeletion(newStatistics, statisticsDatabase);
        statisticsForUpdate = this.getStatisticsForUpdate(newStatistics, statisticsDatabase);

        for (Statistic insertStatistic : statisticsForInsertion) {
            statisticDAO.insertStatistic(insertStatistic);
        }

        for (Statistic deleteStatistic : statisticsForDeletion) {
            statisticDAO.deleteStatistic(deleteStatistic);
        }

        for (Statistic updateStatistic : statisticsForUpdate) {
            try {
                statisticDAO.updateStatistic(updateStatistic);
            } catch (ObjectUnchangedException objectUnchangedException) {
                // No changes to be persisted. Just continue with the next Statistic.
            }
        }
    }

    /**
     * Compares the database state of the statistics and the newly calculated statistics. New statistics are returned,
     * that have yet to be persisted to the database.
     *
     * @param newStatistics      The new statistics that have been calculated during runtime based on the current
     *                           database state of quotation data.
     * @param databaseStatistics The database state of the statistics before the new statistic calculation has been
     *                           executed.
     * @return Statistics that have to be inserted into the database.
     */
    private List<Statistic> getStatisticsForInsertion(final List<Statistic> newStatistics,
            final List<Statistic> databaseStatistics) {
        List<Statistic> statisticInsert = new ArrayList<>();
        StatisticArray databaseStatisticArray = new StatisticArray(databaseStatistics);
        Statistic databaseStatistic;

        for (Statistic newStatistic : newStatistics) {
            databaseStatistic = databaseStatisticArray.getStatisticOfDate(newStatistic.getDate());

            if (databaseStatistic == null) {
                statisticInsert.add(newStatistic);
            }
        }

        return statisticInsert;
    }

    /**
     * Compares the database state of the statistics and the newly calculated statistics. Obsolete statistics are
     * returned, that have to be deleted from the database.
     *
     * @param newStatistics      The new statistics that have been calculated during runtime based on the current
     *                           database state of quotation data.
     * @param databaseStatistics The database state of the statistics before the new statistic calculation has been
     *                           executed.
     * @return Statistics that have to be deleted from the database.
     */
    private List<Statistic> getStatisticsForDeletion(final List<Statistic> newStatistics,
            final List<Statistic> databaseStatistics) {
        List<Statistic> statisticDelete = new ArrayList<>();
        StatisticArray newStatisticsArray = new StatisticArray(newStatistics);
        Statistic newStatistic;

        for (Statistic databaseStatistic : databaseStatistics) {
            newStatistic = newStatisticsArray.getStatisticOfDate(databaseStatistic.getDate());

            if (newStatistic == null) {
                statisticDelete.add(databaseStatistic);
            }
        }

        return statisticDelete;
    }

    /**
     * Compares the database state of the statistics and the newly calculated statistics. Those statistics are returned
     * that already existed at the database before but need to be updated with the newest data.
     *
     * @param newStatistics      The new statistics that have been calculated during runtime based on the current
     *                           database state of quotation data.
     * @param databaseStatistics The database state of the statistics before the new statistic calculation has been
     *                           executed.
     * @return Statistics that already exist but need to be updated.
     */
    private List<Statistic> getStatisticsForUpdate(final List<Statistic> newStatistics,
            final List<Statistic> databaseStatistics) {
        List<Statistic> statisticUpdate = new ArrayList<>();
        StatisticArray databaseStatisticArray = new StatisticArray(databaseStatistics);
        Statistic databaseStatistic;

        for (Statistic newStatistic : newStatistics) {
            databaseStatistic = databaseStatisticArray.getStatisticOfDate(newStatistic.getDate());

            if (databaseStatistic != null) {
                databaseStatistic.setNumberOfInstruments(newStatistic.getNumberOfInstruments());
                databaseStatistic.setNumberAdvance(newStatistic.getNumberAdvance());
                databaseStatistic.setNumberDecline(newStatistic.getNumberDecline());
                databaseStatistic.setNumberAboveSma50(newStatistic.getNumberAboveSma50());
                databaseStatistic.setNumberAtOrBelowSma50(newStatistic.getNumberAtOrBelowSma50());
                databaseStatistic.setNumberAboveSma200(newStatistic.getNumberAboveSma200());
                databaseStatistic.setNumberAtOrBelowSma200(newStatistic.getNumberAtOrBelowSma200());
                databaseStatistic.setNumberRitterMarketTrend(newStatistic.getNumberRitterMarketTrend());
                databaseStatistic.setNumberUpOnVolume(newStatistic.getNumberUpOnVolume());
                databaseStatistic.setNumberDownOnVolume(newStatistic.getNumberDownOnVolume());
                statisticUpdate.add(databaseStatistic);
            }
        }

        return statisticUpdate;
    }
}
