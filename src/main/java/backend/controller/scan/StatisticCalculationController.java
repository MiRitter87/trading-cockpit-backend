package backend.controller.scan;

import java.util.ArrayList;
import java.util.List;

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
     * Calculator for statistical values.
     */
    private StatisticCalculator statisticCalculator;

    /**
     * Default constructor.
     */
    public StatisticCalculationController() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();

        this.statisticCalculator = new StatisticCalculator();
    }

    /**
     * Updates the statistic that is persisted in the database.
     *
     * @throws Exception Statistic calculation or database access failed.
     */
    public void updateStatistic() throws Exception {
        List<Instrument> stocks;
        List<Statistic> statisticNew;

        stocks = instrumentDAO.getInstruments(InstrumentType.STOCK);
        statisticNew = this.calculateEnhancedStatistics(stocks);
        this.persistStatistics(statisticNew);
    }

    /**
     * Calculates the statistics based on the given instruments. For each day only one Statistic is being created. The
     * referenced sector or industry group of an Instrument is not taken into account.
     *
     * @param instruments The instruments for which the statistics are to be calculated.
     * @return The statistics.
     * @throws Exception Statistic calculation failed.
     */
    public List<Statistic> calculateStatistics(final List<Instrument> instruments) throws Exception {
        StatisticArray statistics = new StatisticArray();
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
                this.calculateGeneralStatistic(statistics, currentQuotation, previousQuotation, instrument);
            }
        }

        return statistics.getStatisticsSortedByDate();
    }

    /**
     * This method determines statistics for the given Instrument that is either a sector or an industry group. If a
     * list is given, the statistics are being calculated using all instruments of the given List that are referenced to
     * the given Instrument as sector or industry group.
     *
     * @param instrument The instrument that is a sector or industry group.
     * @param list       A list to narrow down the instruments used for calculation (optional).
     * @return A List of statistics.
     * @throws Exception Statistic determination failed.
     */
    public List<Statistic> getStatisticsForSectorOrIg(final Instrument instrument, final backend.model.list.List list)
            throws Exception {
        List<Instrument> instruments = new ArrayList<>();
        List<Statistic> statistics = null;

        if (instrument == null) {
            throw new Exception("No instrument initialized for statistic retrieval.");
        }

        if (list == null) {
            if (instrument.getType() == InstrumentType.SECTOR) {
                statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, instrument.getId(), null);
            } else if (instrument.getType() == InstrumentType.IND_GROUP) {
                statistics = this.statisticDAO.getStatistics(InstrumentType.STOCK, null, instrument.getId());
            }
        } else {
            // Calculate statistics for all instruments of the list that are referenced to the instrument that is the
            // sector or industry group under investigation.
            for (Instrument tempInstrument : list.getInstruments()) {
                if (tempInstrument.getSector() != null
                        && tempInstrument.getSector().getId().equals(instrument.getId())) {
                    instruments.add(tempInstrument);
                }

                if (tempInstrument.getIndustryGroup() != null
                        && tempInstrument.getIndustryGroup().getId().equals(instrument.getId())) {
                    instruments.add(tempInstrument);
                }
            }

            statistics = this.calculateStatistics(instruments);
        }

        return statistics;
    }

    /**
     * Calculates the statistics based on the given instruments. If an Instrument has its industry group or sector
     * defined an additional Statistic is created for sector and / or industry group. Therefore one Instrument can
     * belong to three different kind of statistics per day: An overall Statistic with all instruments irrespective of
     * their sector or industry group, a Statistic for all instruments of an industry group, a Statistic for all
     * instruments of a sector.
     *
     * @param instruments The instruments for which the statistics are to be calculated.
     * @return The statistics.
     * @throws Exception Statistic calculation failed.
     */
    private List<Statistic> calculateEnhancedStatistics(final List<Instrument> instruments) throws Exception {
        StatisticArray statistics = new StatisticArray();
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

                this.calculateGeneralStatistic(statistics, currentQuotation, previousQuotation, instrument);
                this.calculateSectorStatistic(statistics, currentQuotation, previousQuotation, instrument);
                this.calculateIndustryGroupStatistic(statistics, currentQuotation, previousQuotation, instrument);
            }
        }

        return statistics.getStatisticsSortedByDate();
    }

    /**
     * Calculates the general Statistic irrespective of sector or industry group.
     *
     * @param statistics        The StatisticArray containing all statistics that have been calculated so far.
     * @param currentQuotation  The current Quotation for which the statistics are calculated.
     * @param previousQuotation The previous Quotation used for statistics calculation.
     * @param instrument        The Instrument whose statistics are calculated.
     */
    private void calculateGeneralStatistic(final StatisticArray statistics, final Quotation currentQuotation,
            final Quotation previousQuotation, final Instrument instrument) {

        Statistic statistic;

        statistic = statistics.getStatistic(currentQuotation.getDate(), null, null);

        if (statistic == null) {
            statistic = new Statistic();
            statistic.setInstrumentType(instrument.getType());
            statistic.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
            statistics.addStatistic(statistic);
        }

        this.calculateStatistics(statistic, currentQuotation, previousQuotation);
    }

    /**
     * Calculates the sector-specific Statistic.
     *
     * @param statistics        The StatisticArray containing all statistics that have been calculated so far.
     * @param currentQuotation  The current Quotation for which the statistics are calculated.
     * @param previousQuotation The previous Quotation used for statistics calculation.
     * @param instrument        The Instrument whose statistics are calculated.
     */
    private void calculateSectorStatistic(final StatisticArray statistics, final Quotation currentQuotation,
            final Quotation previousQuotation, final Instrument instrument) {

        Statistic statistic;

        if (instrument.getSector() == null) {
            return;
        }

        statistic = statistics.getStatistic(currentQuotation.getDate(), instrument.getSector().getId(), null);

        if (statistic == null) {
            statistic = new Statistic();
            statistic.setInstrumentType(instrument.getType());
            statistic.setSectorId(instrument.getSector().getId());
            statistic.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
            statistics.addStatistic(statistic);
        }

        this.calculateStatistics(statistic, currentQuotation, previousQuotation);
    }

    /**
     * Calculates the industry group specific Statistic.
     *
     * @param statistics        The StatisticArray containing all statistics that have been calculated so far.
     * @param currentQuotation  The current Quotation for which the statistics are calculated.
     * @param previousQuotation The previous Quotation used for statistics calculation.
     * @param instrument        The Instrument whose statistics are calculated.
     */
    private void calculateIndustryGroupStatistic(final StatisticArray statistics, final Quotation currentQuotation,
            final Quotation previousQuotation, final Instrument instrument) {

        Statistic statistic;

        if (instrument.getIndustryGroup() == null) {
            return;
        }

        statistic = statistics.getStatistic(currentQuotation.getDate(), null, instrument.getIndustryGroup().getId());

        if (statistic == null) {
            statistic = new Statistic();
            statistic.setInstrumentType(instrument.getType());
            statistic.setIndustryGroupId(instrument.getIndustryGroup().getId());
            statistic.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
            statistics.addStatistic(statistic);
        }

        this.calculateStatistics(statistic, currentQuotation, previousQuotation);
    }

    /**
     * Calculates the statistical values of the given Statistic.
     *
     * @param statistic         The Statistic whose values are calculated.
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     */
    private void calculateStatistics(final Statistic statistic, final Quotation currentQuotation,
            final Quotation previousQuotation) {
        statistic.setNumberOfInstruments(statistic.getNumberOfInstruments() + 1);
        statistic.setNumberAdvance(statistic.getNumberAdvance()
                + this.statisticCalculator.getNumberAdvance(currentQuotation, previousQuotation));
        statistic.setNumberDecline(statistic.getNumberDecline()
                + this.statisticCalculator.getNumberDecline(currentQuotation, previousQuotation));
        statistic.setNumberAboveSma50(
                statistic.getNumberAboveSma50() + this.statisticCalculator.getNumberAboveSma50(currentQuotation));
        statistic.setNumberAtOrBelowSma50(statistic.getNumberAtOrBelowSma50()
                + this.statisticCalculator.getNumberAtOrBelowSma50(currentQuotation));
        statistic.setNumberAboveSma200(
                statistic.getNumberAboveSma200() + this.statisticCalculator.getNumberAboveSma200(currentQuotation));
        statistic.setNumberAtOrBelowSma200(statistic.getNumberAtOrBelowSma200()
                + this.statisticCalculator.getNumberAtOrBelowSma200(currentQuotation));
        statistic.setNumberRitterMarketTrend(statistic.getNumberRitterMarketTrend()
                + this.statisticCalculator.getNumberRitterMarketTrend(currentQuotation, previousQuotation));
        statistic.setNumberUpOnVolume(statistic.getNumberUpOnVolume()
                + this.statisticCalculator.getNumberUpOnVolume(currentQuotation, previousQuotation));
        statistic.setNumberDownOnVolume(statistic.getNumberDownOnVolume()
                + this.statisticCalculator.getNumberDownOnVolume(currentQuotation, previousQuotation));
        statistic.setNumberBearishReversal(statistic.getNumberBearishReversal()
                + this.statisticCalculator.getNumberBearishReversal(currentQuotation));
        statistic.setNumberBullishReversal(statistic.getNumberBullishReversal()
                + this.statisticCalculator.getNumberBullishReversal(currentQuotation));
        statistic.setNumberChurning(statistic.getNumberChurning()
                + this.statisticCalculator.getNumberChurning(currentQuotation, previousQuotation));
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

        statisticsDatabase = statisticDAO.getStatisticsOfInstrumentType(InstrumentType.STOCK);

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
            databaseStatistic = databaseStatisticArray.getStatistic(newStatistic.getDate(), newStatistic.getSectorId(),
                    newStatistic.getIndustryGroupId());

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
            newStatistic = newStatisticsArray.getStatistic(databaseStatistic.getDate(), databaseStatistic.getSectorId(),
                    databaseStatistic.getIndustryGroupId());

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
            databaseStatistic = databaseStatisticArray.getStatistic(newStatistic.getDate(), newStatistic.getSectorId(),
                    newStatistic.getIndustryGroupId());

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
                databaseStatistic.setNumberBearishReversal(newStatistic.getNumberBearishReversal());
                databaseStatistic.setNumberBullishReversal(newStatistic.getNumberBullishReversal());
                databaseStatistic.setNumberChurning(newStatistic.getNumberChurning());
                statisticUpdate.add(databaseStatistic);
            }
        }

        return statisticUpdate;
    }
}
