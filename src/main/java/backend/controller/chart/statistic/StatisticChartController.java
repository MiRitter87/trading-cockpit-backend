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
     * The InstrumentType of the instruments depicted in the charts.
     */
    private InstrumentType instrumentType;

    /**
     * The list used as basis for the statistical chart.
     */
    private backend.model.list.List list;

    /**
     * A List of statistics the chart is based on.
     */
    private List<Statistic> statistics;

    /**
     * Initializes the StatisticChartController.
     *
     * @param listId The ID of the list defining the instruments used for Statistic chart creation.
     * @throws Exception Failed to initialize data.
     */
    public StatisticChartController(final Integer listId) throws Exception {
        this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
        this.instrumentType = InstrumentType.STOCK;

        if (listId != null) {
            this.list = this.getListDAO().getList(listId);
        }

        this.initializeStatistics();
    }

    /**
     * Default constructor.
     */
    public StatisticChartController() {

    }

    /**
     * Calculates the statistics for the list with the given id. If no listId is specified, the general statistics are
     * loaded.
     *
     * @throws Exception Determination of statistics failed.
     */
    private void initializeStatistics() throws Exception {
        List<Instrument> instruments = new ArrayList<>();
        StatisticCalculationController statisticCalculationController = new StatisticCalculationController();

        this.statistics = new ArrayList<>();

        if (this.list != null) {
            instruments.addAll(this.list.getInstruments());
            this.statistics = statisticCalculationController.calculateStatistics(instruments, TRADING_DAYS_PER_YEAR);
        } else {
            this.statistics = this.statisticDAO.getStatistics(this.instrumentType, null, null);
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
     * @return the statistics
     */
    public List<Statistic> getStatistics() {
        return this.statistics;
    }

    /**
     * @param statistics the statistics to set
     */
    public void setStatistics(final List<Statistic> statistics) {
        this.statistics = statistics;
    }
}
