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
     * Initializes the StatisticChartController.
     */
    public StatisticChartController() {
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
    }

    /**
     * Get the statistics for the given parameters.
     *
     * @param instrumentType The InstrumentType.
     * @param listId         The ID of the list.
     * @param maxNumber      The maximum number of statistics returned.
     * @return Statistics for the given parameters.
     * @throws Exception Determination of statistics failed.
     */
    protected List<Statistic> getStatistics(final InstrumentType instrumentType, final Integer listId,
            final Integer maxNumber) throws Exception {
        backend.model.list.List list;
        List<Instrument> instruments = new ArrayList<>();
        List<Statistic> statistics = new ArrayList<>();
        StatisticCalculationController statisticCalculationController = new StatisticCalculationController();

        if (listId != null) {
            list = this.getListDAO().getList(listId);
            instruments.addAll(list.getInstruments());
            statistics = statisticCalculationController.calculateStatistics(instruments);
        } else {
            statistics = statisticDAO.getStatistics(instrumentType, null, null);
        }

        if (statistics.size() > maxNumber) {
            statistics = statistics.subList(0, TRADING_DAYS_PER_YEAR);
        }

        return statistics;
    }
}
