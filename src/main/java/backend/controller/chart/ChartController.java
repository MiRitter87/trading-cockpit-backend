package backend.controller.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ResourceBundle;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.quotation.persistence.QuotationDAO;

/**
 * Provides methods that are collectively used for chart generation.
 *
 * @author Michael
 */
public abstract class ChartController {
    /**
     * The number of trading days per year.
     */
    public static final Integer TRADING_DAYS_PER_YEAR = 252;

    /**
     * DAO to access Instrument data.
     */
    private InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private QuotationDAO quotationDAO;

    /**
     * DAO to access List data.
     */
    private ListDAO listDAO;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Initializes the ChartController.
     */
    public ChartController() {
        this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
        this.listDAO = DAOManager.getInstance().getListDAO();
    }

    /**
     * @return the instrumentDAO
     */
    public InstrumentDAO getInstrumentDAO() {
        return instrumentDAO;
    }

    /**
     * @return the quotationDAO
     */
    public QuotationDAO getQuotationDAO() {
        return quotationDAO;
    }

    /**
     * @return the listDAO
     */
    public ListDAO getListDAO() {
        return listDAO;
    }

    /**
     * @return the resources
     */
    public ResourceBundle getResources() {
        return resources;
    }

    /**
     * Adds a horizontal line to the given XYPlot.
     *
     * @param plot                   The XYPlot to which the horizontal line is added.
     * @param horizontalLinePosition The value on the y-axis at which the horizontal line is being drawn.
     * @param color                  The Color of the line.
     */
    protected void addHorizontalLine(final XYPlot plot, final double horizontalLinePosition, final Color color) {
        ValueMarker valueMarker = new ValueMarker(horizontalLinePosition, color, new BasicStroke(2), null, null, 1.0f);
        plot.addRangeMarker(valueMarker);
    }

    /**
     * Applies a custom theme to the given chart used by statistical charts.
     *
     * @param chart The chart.
     */
    protected void applyStatisticalTheme(final JFreeChart chart) {
        XYPlot chartPlot;

        chart.setBackgroundPaint(Color.decode("#EEEEEE"));

        chartPlot = chart.getXYPlot();
        chartPlot.setBackgroundPaint(Color.WHITE);
        chartPlot.setDomainGridlinePaint(Color.BLACK);
        chartPlot.setRangeGridlinePaint(Color.BLACK);
    }

    /**
     * Applies a custom theme to the given chart used by Price Volume charts.
     *
     * @param chart The chart.
     */
    protected void applyPriceVolumeTheme(final JFreeChart chart) {
        chart.setBackgroundPaint(Color.decode("#EEEEEE"));
    }
}
