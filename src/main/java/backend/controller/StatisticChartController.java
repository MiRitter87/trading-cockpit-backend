package backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

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
 * Controls the generation of statistical charts.
 * 
 * @author Michael
 */
public class StatisticChartController {
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
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	
	/**
	 * Initializes the StatisticChartController.
	 */
	public StatisticChartController() {
		this.statisticDAO = DAOManager.getInstance().getStatisticDAO();
		this.instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
		this.quotationDAO = DAOManager.getInstance().getQuotationDAO();
	}
	
	
	/**
	 * Gets a chart of the cumulative Advance/Decline Number.
	 * 
	 * @param instrumentType The InstrumentType for which the chart is created.
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getAdvanceDeclineNumberChart(final InstrumentType instrumentType, final Integer listId) throws Exception {
		List<Statistic> statistics = this.getStatistics(instrumentType, listId);
		XYDataset dataset = this.getAdvanceDeclineNumberDataset(statistics);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
			this.resources.getString("statistic.chartADNumber.titleName"),
			null, null,	dataset, true, true, false
		);
		
		return chart;
	}
	
	
	/**
	 * Gets a chart with the percentage of instruments trading above their SMA(50).
	 * 
	 * @param instrumentType The InstrumentType for which the chart is created.
	 * @param listId The ID of the list defining the instruments used for Statistic chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getInstrumentsAboveSma50Chart(final InstrumentType instrumentType, final Integer listId) throws Exception {
		List<Statistic> statistics = this.getStatistics(instrumentType, listId);
		XYDataset dataset = this.getInstrumentsAboveSma50Dataset(statistics);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
			this.resources.getString("statistic.chartAboveSma50.titleName"),
			null, null,	dataset, true, true, false
		);
		
		return chart;
	}
	
	
	/**
	 * Gets a chart of an Instrument marked with Distribution Days.
	 * 
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getDistributionDaysChart(final Integer instrumentId) throws Exception {
		OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrumentId);
		IntervalXYDataset volumeData = this.getInstrumentVolumeDataset(instrumentId);
		JFreeChart chart;
		ValueAxis timeAxisCandlestick = new DateAxis("Time");
        NumberAxis valueAxisCandlestick = new NumberAxis("!Preis");
        ChartTheme currentTheme = new StandardChartTheme("JFree");
        CandlestickRenderer candlestickRenderer = new CandlestickRenderer();
		
        //Do not begin y-Axis at zero. Use lowest value of provided Dataset instead.
        valueAxisCandlestick.setAutoRangeIncludesZero(false);
        
		//Build Candlestick Plot based on OHLC Dataset.
		XYPlot candleStickSubplot = new XYPlot(instrumentPriceData, timeAxisCandlestick, valueAxisCandlestick, null);
		candlestickRenderer.setDrawVolume(false);
		candleStickSubplot.setRenderer(candlestickRenderer);
		
		//Build Volume Plot.
		XYBarRenderer volumeRenderer = new XYBarRenderer();
		XYPlot volumeSubplot = new XYPlot(volumeData, timeAxisCandlestick, new NumberAxis("!Volumen"), volumeRenderer);
		
		//Add Candlestick Subplot to combined Plot
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
		combinedPlot.add(candleStickSubplot, 3);	//Price Plot takes 3 vertical size units.
		combinedPlot.add(volumeSubplot, 1);			//Volume Plot takes 1 vertical size unit.
		combinedPlot.setDomainAxis(timeAxisCandlestick);
		
		//Build chart based on combined Plot.
		chart = new JFreeChart("CombinedDomainXYPlot Demo",
				JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
		
		currentTheme.apply(chart);
		
		return chart;
	}
	
	
	/**
	 * Get the statistics for the given parameters.
	 * 
	 * @param instrumentType The InstrumentType.
	 * @param listId The ID of the list.
	 * @return Statistics for the given parameters.
	 * @throws Exception Determination of statistics failed.
	 */
	private List<Statistic> getStatistics(final InstrumentType instrumentType, final Integer listId) throws Exception {
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
	 * Constructs a XYDataset for the cumulative Advance/Decline number chart.
	 * 
	 * @param statistics The statistics for which the chart is calculated.
	 * @return The XYDataset.
	 * @throws Exception XYDataset creation failed.
	 */
	private XYDataset getAdvanceDeclineNumberDataset(final List<Statistic> statistics) throws Exception {
		Statistic statistic;
		TimeSeries timeSeries = new TimeSeries(this.resources.getString("statistic.chartADNumber.timeSeriesName"));
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
		ListIterator<Statistic> iterator;
		int cumulativeADNumber = 0;
		
		//Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
		iterator = statistics.listIterator(statistics.size());
		while(iterator.hasPrevious()) {
			statistic = iterator.previous();
			cumulativeADNumber += statistic.getAdvanceDeclineNumber();
			timeSeries.add(new Day(statistic.getDate()), cumulativeADNumber);
		}
		
        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);
        
        return timeSeriesColleciton;
	}
	
	
	/**
	 * Constructs a XYDataset for the percentage of instruments trading above their SMA(50) chart.
	 * 
	 * @param statistics The statistics for which the chart is calculated.
	 * @return The XYDataset.
	 * @throws Exception XYDataset creation failed.
	 */
	private XYDataset getInstrumentsAboveSma50Dataset(final List<Statistic> statistics) throws Exception {
		Statistic statistic;
		TimeSeries timeSeries = new TimeSeries(this.resources.getString("statistic.chartAboveSma50.timeSeriesName"));
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
		ListIterator<Statistic> iterator;
		
		//Iterate statistics backwards because XYDatasets are constructed from oldest to newest value.
		iterator = statistics.listIterator(statistics.size());
		while(iterator.hasPrevious()) {
			statistic = iterator.previous();
			timeSeries.add(new Day(statistic.getDate()), statistic.getPercentAboveSma50());
		}
		
        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);
        
        return timeSeriesColleciton;
	}
	
	
	/**
	 * Gets a dataset of OHLC prices for the Instrument with the given ID.
	 * 
	 * @param instrumentId The ID of the Instrument.
	 * @return A dataset of OHLC prices.
	 * @throws Exception dataset creation failed
	 */
	private OHLCDataset getInstrumentOHLCDataset(final Integer instrumentId) throws Exception {
		Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);
		List<Quotation> quotationsSortedByDate;
		
		instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
		int numberOfQuotations = instrument.getQuotations().size();
		int quotationIndex;
		
		Date[] date = new Date[numberOfQuotations];
		double[] open = new double[numberOfQuotations];
        double[] high = new double[numberOfQuotations];
        double[] low = new double[numberOfQuotations];
        double[] close = new double[numberOfQuotations];
        double[] volume = new double[numberOfQuotations];

        quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        
		for(Quotation tempQuotation : quotationsSortedByDate) {
			quotationIndex = quotationsSortedByDate.indexOf(tempQuotation);
			
			date[quotationIndex] = tempQuotation.getDate();
			open[quotationIndex] = tempQuotation.getOpen().doubleValue();
			high[quotationIndex] = tempQuotation.getHigh().doubleValue();
			low[quotationIndex] = tempQuotation.getLow().doubleValue();
			close[quotationIndex] = tempQuotation.getClose().doubleValue();
			volume[quotationIndex] = tempQuotation.getVolume();
		}
        
		return new DefaultHighLowDataset("!Series 1", date, high, low, open, close, volume);
	}
	
	
	/**
	 * Gets a dataset of volume information for the Instrument with the given ID.
	 * 
	 * @param instrumentId The ID of the Instrument.
	 * @return A dataset of volume information.
	 * @throws Exception dataset creation failed
	 */
	private IntervalXYDataset getInstrumentVolumeDataset(final Integer instrumentId) throws Exception {
		Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);
		List<Quotation> quotationsSortedByDate;
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries volumeTimeSeries = new TimeSeries("!Volume");
		
		instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
		quotationsSortedByDate = instrument.getQuotationsSortedByDate();
        
		for(Quotation tempQuotation : quotationsSortedByDate) {
			volumeTimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getVolume());
		}
		
		dataset.addSeries(volumeTimeSeries);
		
        return dataset;

	}
}
