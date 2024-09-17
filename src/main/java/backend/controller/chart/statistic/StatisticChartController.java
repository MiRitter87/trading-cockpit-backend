package backend.controller.chart.statistic;

import java.util.ArrayList;
import java.util.List;

import backend.controller.chart.ChartController;
import backend.controller.scan.StatisticCalculationController;
import backend.dao.DAOManager;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Provides methods that are used for generation of charts that are based on statistical data.
 *
 * @author Michael
 */
public class StatisticChartController extends ChartController {
    /**
     * DAO to access Statistic data.
     */
    private StatisticDAO statisticDAO;

    /**
     * The list used as basis for the statistical chart.
     */
    private backend.model.list.List list;

    /**
     * Initializes the StatisticChartController.
     *
     * @param listId The ID of the list defining the instruments used for Statistic chart creation.
     * @throws Exception Failed to initialize data.
     */
    public StatisticChartController(final Integer listId) throws Exception {
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();

        if (listId != null) {
            this.list = this.getListDAO().getList(listId);
        }
    }

    /**
     * @return the list
     */
    public backend.model.list.List getList() {
        return this.list;
    }

    /**
     * @param list the list to set
     */
    public void setList(final backend.model.list.List list) {
        this.list = list;
    }

    /**
     * Calculates the statistics for the list with the given id. If no listId is specified, the general statistics are
     * loaded.
     *
     * @param instrumentType The InstrumentType.
     * @param maxNumber      The maximum number of statistics returned.
     * @return Statistics for the given parameters.
     * @throws Exception Determination of statistics failed.
     */
    public List<Statistic> getStatisticsForList(final InstrumentType instrumentType, final Integer maxNumber)
            throws Exception {

        List<Instrument> instruments = new ArrayList<>();
        List<Statistic> statistics = new ArrayList<>();
        StatisticCalculationController statisticCalculationController = new StatisticCalculationController();

        if (this.list != null) {
            instruments.addAll(this.list.getInstruments());
            statistics = statisticCalculationController.calculateStatistics(instruments, TRADING_DAYS_PER_YEAR);
        } else {
            statistics = statisticDAO.getStatistics(instrumentType, null, null);
        }

        return statistics;
    }

    /**
     * Get the statistics for the given parameters.
     *
     * @param instrumentType  The InstrumentType.
     * @param sectorId        The ID of the sector the statistics belong to (can be null).
     * @param industryGroupId The ID of the industry group the statistics belong to (can be null).
     * @param maxNumber       The maximum number of statistics returned.
     * @return Statistics for the given parameters.
     * @throws Exception Determination of statistics failed.
     */
    protected List<Statistic> getStatistics(final InstrumentType instrumentType, final Integer sectorId,
            final Integer industryGroupId, final Integer maxNumber) throws Exception {

        List<Statistic> statistics = new ArrayList<>();

        statistics = statisticDAO.getStatistics(instrumentType, sectorId, industryGroupId);

        if (statistics.size() > maxNumber) {
            statistics = statistics.subList(0, TRADING_DAYS_PER_YEAR);
        }

        return statistics;
    }
}
