package backend.controller.chart;

import java.awt.Color;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import backend.controller.instrumentCheck.NoQuotationsExistException;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.webservice.Indicator;

/**
 * Controller for the creation of a chart displaying an Instrument with price and volume.
 * The chart can be configured to add additional overlays and plots.
 * 
 * @author Michael
 */
public class PriceVolumeChartController extends ChartController {
	/**
	 * Gets a chart of an Instrument with volume information.
	 * 
	 * @param instrumentId The ID of the Instrument used for chart creation.
	 * @param withEma21 Show EMA(21) as overlay.
	 * @param withSma50 Show SMA(50) as overlay.
	 * @param withSma150 Show SMA(150) as overlay.
	 * @param withSma200 Show SMA(200) as overlay.
	 * @param withVolume Show volume information.
	 * @param withSma30Volume Show SMA(30) of volume.
	 * @param indicator The Indicator that is being displayed above the chart.
	 * @param rsInstrumentId The ID of the Instrument used to build the RS line (only used if type of Indicator is RS_LINE).
	 * @return The chart.
	 * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getPriceVolumeChart(final Integer instrumentId, final boolean withEma21, final boolean withSma50, 
			final boolean withSma150, final boolean withSma200, final boolean withVolume, final boolean withSma30Volume,
			final Indicator indicator, final Integer rsInstrumentId) throws NoQuotationsExistException, Exception {
		
		Instrument instrument = this.getInstrumentWithQuotations(instrumentId);
		JFreeChart chart;
		DateAxis dateAxis = this.getDateAxis(instrument);	//The shared time axis of all subplots.
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, dateAxis);
        XYPlot volumeSubplot = null;
        XYPlot indicatorSubplot = null;
        
        if(withVolume) {
        	volumeSubplot = this.getVolumePlot(instrument, dateAxis);
        	this.addMovingAverageVolume(instrument, withSma30Volume, volumeSubplot);        	
        	this.clipVolumeAt2TimesAverage(volumeSubplot, instrument);
        }
        
        indicatorSubplot = this.getIndicatorPlot(indicator, rsInstrumentId, instrument, dateAxis);
		
		this.addMovingAveragesPrice(instrument, withEma21, withSma50, withSma150, withSma200, candleStickSubplot);
		
		//Build combined plot based on subplots.
		combinedPlot.setDomainAxis(dateAxis);
		
		if(indicatorSubplot != null)
			combinedPlot.add(indicatorSubplot, 1);			//Indicator Plot takes 1 vertical size unit.
			
		combinedPlot.add(candleStickSubplot, 4);			//Price Plot takes 4 vertical size units.
		
		if(withVolume)
			combinedPlot.add(volumeSubplot, 1);				//Volume Plot takes 1 vertical size unit.
		
		//Build chart based on combined Plot.
		chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
		
		return chart;
	}
	
	
	/**
	 * Adds moving averages of the price to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param withEma21 Show EMA(21) as overlay.
	 * @param withSma50 Show SMA(50) as overlay.
	 * @param withSma150 Show SMA(150) as overlay.
	 * @param withSma200 Show SMA(200) as overlay.
	 * @param candleStickSubplot The Plot to which moving averages are added.
	 */
	private void addMovingAveragesPrice(final Instrument instrument, final boolean withEma21, final boolean withSma50, 
			final boolean withSma150, final boolean withSma200, XYPlot candleStickSubplot) {
		
		if(withEma21)
			this.addEma21(instrument, candleStickSubplot);
		
		if(withSma50)
			this.addSma50(instrument, candleStickSubplot);
		
		if(withSma150)
			this.addSma150(instrument, candleStickSubplot);
		
		if(withSma200)
			this.addSma200(instrument, candleStickSubplot);
	}
	
	
	/**
	 * Adds the EMA(21) to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param candleStickSubplot The Plot to which the EMA(21) is added.
	 */
	private void addEma21(final Instrument instrument, XYPlot candleStickSubplot) {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		TimeSeries ema21TimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesEma21Name"));
		int index = candleStickSubplot.getDatasetCount();
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			if(tempQuotation.getIndicator().getEma21() == 0)
				continue;
			
			ema21TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getEma21());
		}
		
		timeSeriesCollection.addSeries(ema21TimeSeries);
		
		candleStickSubplot.setDataset(index, timeSeriesCollection);
		candleStickSubplot.mapDatasetToRangeAxis(index, 0);
		
		XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
		smaRenderer.setSeriesPaint(0, Color.ORANGE);
		candleStickSubplot.setRenderer(index, smaRenderer);
		candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	/**
	 * Adds the SMA(50) to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param candleStickSubplot The Plot to which the SMA(50) is added.
	 */
	private void addSma50(final Instrument instrument, XYPlot candleStickSubplot) {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		TimeSeries sma50TimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesSma50Name"));
		int index = candleStickSubplot.getDatasetCount();
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			if(tempQuotation.getIndicator().getSma50() == 0)
				continue;
			
			sma50TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma50());
		}
		
		timeSeriesCollection.addSeries(sma50TimeSeries);
		
		candleStickSubplot.setDataset(index, timeSeriesCollection);
		candleStickSubplot.mapDatasetToRangeAxis(index, 0);
		
		XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
		smaRenderer.setSeriesPaint(0, Color.BLUE);
		candleStickSubplot.setRenderer(index, smaRenderer);
		candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	/**
	 * Adds the SMA(150) to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param candleStickSubplot The Plot to which the SMA(150) is added.
	 */
	private void addSma150(final Instrument instrument, XYPlot candleStickSubplot) {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		TimeSeries sma150TimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesSma150Name"));
		int index = candleStickSubplot.getDatasetCount();
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			if(tempQuotation.getIndicator().getSma150() == 0)
				continue;
			
			sma150TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma150());
		}
		
		timeSeriesCollection.addSeries(sma150TimeSeries);
		
		candleStickSubplot.setDataset(index, timeSeriesCollection);
		candleStickSubplot.mapDatasetToRangeAxis(index, 0);
		
		XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
		smaRenderer.setSeriesPaint(0, Color.RED);
		candleStickSubplot.setRenderer(index, smaRenderer);
		candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	/**
	 * Adds the SMA(200) to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param candleStickSubplot The Plot to which the SMA(200) is added.
	 */
	private void addSma200(final Instrument instrument, XYPlot candleStickSubplot) {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		TimeSeries sma200TimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesSma200Name"));
		int index = candleStickSubplot.getDatasetCount();
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			if(tempQuotation.getIndicator().getSma200() == 0)
				continue;
			
			sma200TimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma200());
		}
		
		timeSeriesCollection.addSeries(sma200TimeSeries);
		
		candleStickSubplot.setDataset(index, timeSeriesCollection);
		candleStickSubplot.mapDatasetToRangeAxis(index, 0);
		
		XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
		smaRenderer.setSeriesPaint(0, Color.GREEN);
		candleStickSubplot.setRenderer(index, smaRenderer);
		candleStickSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	/**
	 * Adds the SMA(30) of the volume to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param @param withSma30Volume Show SMA(30) of volume.
	 * @param volumeSubplot The Plot to which the SMA(30) of the volume is added.
	 */
	private void addMovingAverageVolume(final Instrument instrument, final boolean withSma30Volume, XYPlot volumeSubplot) {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		TimeSeries sma30VolumeTimeSeries = new TimeSeries(this.resources.getString("chart.priceVolume.timeSeriesSma30VolumeName"));
		int index = volumeSubplot.getDatasetCount();
		
		if(!withSma30Volume)
			return;
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			if(tempQuotation.getIndicator().getSma30Volume() == 0)
				continue;
			
			sma30VolumeTimeSeries.add(new Day(tempQuotation.getDate()), tempQuotation.getIndicator().getSma30Volume());
		}
		
		timeSeriesCollection.addSeries(sma30VolumeTimeSeries);
		
		volumeSubplot.setDataset(index, timeSeriesCollection);
		volumeSubplot.mapDatasetToRangeAxis(index, 0);
		
		XYItemRenderer smaRenderer = new XYLineAndShapeRenderer(true, false);
		smaRenderer.setSeriesPaint(0, Color.BLACK);
		volumeSubplot.setRenderer(index, smaRenderer);
		volumeSubplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	/**
	 * Determines the highest average volume within the trading history of the given Instrument.
	 * 
	 * @param instrument The Instrument.
	 * @return The highest average volume of the trading history.
	 */
	private long getHighestAverageVolume(final Instrument instrument) {
		long highestAverageVolume = 0;
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		for(Quotation tempQuotation : quotationsSortedByDate) {
			if(tempQuotation.getIndicator().getSma30Volume() > highestAverageVolume)
				highestAverageVolume = tempQuotation.getIndicator().getSma30Volume();
		}
		
		
		return highestAverageVolume;
	}
	
	
	/**
	 * Clips the volume bars of the volume plot at two times of the maximum average volume.
	 * This prevents a volume scale, where large "skyscraper" volume bars dominate the picture and the user is not able
	 * to properly read the average volume.
	 * 
	 * @param volumeSubplot The sub plot of the chart showing the volume.
	 * @param instrument The Instrument containing the trading history.
	 */
	private void clipVolumeAt2TimesAverage(XYPlot volumeSubplot, final Instrument instrument) {
        double upperVolumeAxisRange = this.getHighestAverageVolume(instrument) * 2;
        NumberAxis volumeAxis = (NumberAxis) volumeSubplot.getRangeAxis();
        
        if(upperVolumeAxisRange > 0)
        	volumeAxis.setRange(0, upperVolumeAxisRange);
	}
	
	
	/**
	 * Builds a plot to display Indicator data of an Instrument.
	 * 
	 * @param indicator The requested Indicator.
	 * @param rsInstrumentId The ID of the Instrument used to calculate the RS line.
	 * @param instrument The Instrument for which the indicator is being calculated.
	 * @param timeAxis The x-Axis (time).
	 * @return A XYPlot depicting Indicator data.
	 */
	private XYPlot getIndicatorPlot(final Indicator indicator, final Integer rsInstrumentId, final Instrument instrument, final ValueAxis timeAxis) {
		switch(indicator) {
			case RS_LINE:
				return this.getRsLinePlot(rsInstrumentId, instrument, timeAxis);
			case NONE:
				return null;
			default:
				return null;
		}
	}
	
	
	/**
	 * Builds a plot to display the RS line of an Instrument.
	 * 
	 * @param rsInstrumentId The ID of the Instrument used to calculate the RS line (the divisor of the price ratio).
	 * @param instrument The Instrument for which the RS line is being calculated.
	 * @param timeAxis The x-Axis (time).
	 * @return A XYPlot depicting the RS line.
	 */
	private XYPlot getRsLinePlot(final Integer rsInstrumentId, final Instrument instrument, final ValueAxis timeAxis) {
		TimeSeries timeSeries = new TimeSeries("");
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeZone);
		XYPlot rsLinePlot;
		NumberAxis valueAxis = new NumberAxis("");
		XYLineAndShapeRenderer rsLineRenderer = new XYLineAndShapeRenderer(true, false);
		
		//TODO Call Controller to calculate the Quotations making up the ratio. Use the ratio quotations then.
		for(Quotation quotation: instrument.getQuotationsSortedByDate()) {
			timeSeries.add(new Day(quotation.getDate()), quotation.getClose());
		}
		
		timeSeriesCollection.addSeries(timeSeries);
        timeSeriesCollection.setXPosition(TimePeriodAnchor.MIDDLE);
		
        //Do not begin y-Axis at zero. Use lowest value of provided dataset instead.
        valueAxis.setAutoRangeIncludesZero(false);
        
        rsLinePlot = new XYPlot(timeSeriesCollection, timeAxis, valueAxis, null);
        rsLinePlot.setRenderer(rsLineRenderer);
        
		return rsLinePlot;
	}
}
