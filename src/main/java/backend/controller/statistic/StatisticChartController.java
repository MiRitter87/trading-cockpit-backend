package backend.controller.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;

import backend.controller.scan.StatisticCalculationController;
import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.quotation.QuotationDAO;
import backend.dao.statistic.StatisticDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;

/**
 * Provides methods that are collectively used for chart generation.
 * 
 * @author Michael
 */
public abstract class StatisticChartController {
	/**
	 * DAO to access Statistic data.
	 */
	StatisticDAO statisticDAO;
	
	/**
	 * DAO to access Instrument data.
	 */
	InstrumentDAO instrumentDAO;
	
	/**
	 * DAO to access Quotation data.
	 */
	QuotationDAO quotationDAO;
	
	/**
	 * Access to localized application resources.
	 */
	protected ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	
	/**
	 * Initializes the StatisticChartController.
	 */
	public StatisticChartController() {
		this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
	}
	
	
	/**
	 * Get the statistics for the given parameters.
	 * 
	 * @param instrumentType The InstrumentType.
	 * @param listId The ID of the list.
	 * @return Statistics for the given parameters.
	 * @throws Exception Determination of statistics failed.
	 */
	protected List<Statistic> getStatistics(final InstrumentType instrumentType, final Integer listId) throws Exception {
		ListDAO listDAO = DAOManager.getInstance().getListDAO();
		backend.model.list.List list;
		List<Instrument> instruments = new ArrayList<>();
		List<Statistic> statistics = new ArrayList<>();
		StatisticCalculationController statisticCalculationController = new StatisticCalculationController();
		
		if(listId != null) {
			list = listDAO.getList(listId);
			instruments.addAll(list.getInstruments());
			statistics = statisticCalculationController.calculateStatistics(instruments);
		}
		else {			
			statistics = statisticDAO.getStatistics(instrumentType);
		}
		
		return statistics;
	}
	
	
	/**
	 * Gets a dataset of OHLC prices for the given Instrument.
	 * 
	 * @param instrument The Instrument.
	 * @return A dataset of OHLC prices.
	 * @throws Exception Dataset creation failed
	 */
	protected OHLCDataset getInstrumentOHLCDataset(final Instrument instrument) throws Exception {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		int numberOfQuotations = instrument.getQuotations().size();
		int quotationIndex;
		
		Date[] date = new Date[numberOfQuotations];
		double[] open = new double[numberOfQuotations];
        double[] high = new double[numberOfQuotations];
        double[] low = new double[numberOfQuotations];
        double[] close = new double[numberOfQuotations];
        double[] volume = new double[numberOfQuotations];
        
		for(Quotation tempQuotation : quotationsSortedByDate) {
			quotationIndex = quotationsSortedByDate.indexOf(tempQuotation);
			
			date[quotationIndex] = tempQuotation.getDate();
			open[quotationIndex] = tempQuotation.getOpen().doubleValue();
			high[quotationIndex] = tempQuotation.getHigh().doubleValue();
			low[quotationIndex] = tempQuotation.getLow().doubleValue();
			close[quotationIndex] = tempQuotation.getClose().doubleValue();
			volume[quotationIndex] = tempQuotation.getVolume();
		}
		
		return new DefaultHighLowDataset(this.resources.getString("statistic.chartDistributionDays.timeSeriesPriceName"), 
				date, high, low, open, close, volume);
	}
	
	
	/**
	 * Builds a plot to display prices of an Instrument using a Candlestick chart.
	 * 
	 * @param instrument The Instrument.
	 * @param timeAxis The x-Axis (time).
	 * @return A XYPlot depicting prices using a Candlestick chart.
	 * @throws Exception Plot generation failed.
	 */
	protected XYPlot getCandlestickPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
		OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
		NumberAxis valueAxisCandlestick = new NumberAxis();
        CandlestickRenderer candlestickRenderer = new CandlestickRenderer();
        
        //Do not begin y-Axis at zero. Use lowest value of provided Dataset instead.
        valueAxisCandlestick.setAutoRangeIncludesZero(false);
		
		XYPlot candleStickSubplot = new XYPlot(instrumentPriceData, timeAxis, valueAxisCandlestick, null);
		candlestickRenderer.setDrawVolume(false);
		candleStickSubplot.setRenderer(candlestickRenderer);
		
		return candleStickSubplot;
	}
	
	
	/**
	 * Builds a plot to display volume data of an Instrument.
	 * 
	 * @param instrument The Instrument.
	 * @param timeAxis The x-Axis (time).
	 * @return A XYPlot depicting volume data.
	 * @throws Exception Plot generation failed.
	 */
	protected XYPlot getVolumePlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
		IntervalXYDataset volumeData = this.getInstrumentVolumeDataset(instrument);
		XYBarRenderer volumeRenderer = new XYBarRenderer();
        NumberAxis volumeAxis = new NumberAxis();
		
		XYPlot volumeSubplot = new XYPlot(volumeData, timeAxis, volumeAxis, volumeRenderer);
		
		return volumeSubplot;
	}
	
	
	/**
	 * Gets a dataset of volume information for the given Instrument.
	 * 
	 * @param instrument The Instrument.
	 * @return A dataset of volume information.
	 * @throws Exception Dataset creation failed
	 */
	private IntervalXYDataset getInstrumentVolumeDataset(final Instrument instrument) throws Exception {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries volumeTimeSeries = new TimeSeries(this.resources.getString("statistic.chartDistributionDays.timeSeriesVolumeName"));
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			volumeTimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getVolume());
		}
		
		dataset.addSeries(volumeTimeSeries);
		
        return dataset;

	}
}
