package backend.controller.chart;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import backend.controller.instrumentCheck.NoQuotationsExistException;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

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
	 * @param withSma50 Show SMA(50) as overlay.
	 * @return The chart.
	 * @throws NoQuotationsExistException No quotations exist for the Quotation with the given ID.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getPriceVolumeChart(final Integer instrumentId, final boolean withSma50) throws NoQuotationsExistException, Exception {
		Instrument instrument = this.getInstrumentWithQuotations(instrumentId);
		JFreeChart chart;
		ValueAxis timeAxis = new DateAxis();	//The shared time axis of all subplots.
        
        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, timeAxis);
		XYPlot volumeSubplot = this.getVolumePlot(instrument, timeAxis);
		
		this.addMovingAveragesPrice(instrument, withSma50, candleStickSubplot);
		
		//Build combined plot based on subplots.
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
		combinedPlot.add(candleStickSubplot, 4);			//Price Plot takes 4 vertical size units.
		combinedPlot.add(volumeSubplot, 1);					//Volume Plot takes 1 vertical size unit.
		combinedPlot.setDomainAxis(timeAxis);
		
		//Build chart based on combined Plot.
		chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
		
		return chart;
	}
	
	
	/**
	 * Adds moving averages of the price to the chart.
	 * 
	 * @param instrument The Instrument whose price and volume data are displayed.
	 * @param withSma50 Show SMA(50) as overlay.
	 * @param candleStickSubplot The Plot to which moving averages are added.
	 */
	private void addMovingAveragesPrice(final Instrument instrument, final boolean withSma50, XYPlot candleStickSubplot) {
		if(withSma50)
			this.addSma50(instrument, candleStickSubplot);
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
}
