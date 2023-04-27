package backend.controller.statistic;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataset;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Controller for the creation of a chart displaying an Instrument with Pocket Pivots.
 * 
 * @author Michael
 */
public class PocketPivotChartController extends StatisticChartController {
	/**
	 * Gets a chart of an Instrument marked with Pocket Pivots.
	 * 
	 * @param instrumentId The ID of the Instrument used for chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getPocketPivotsChart(final Integer instrumentId) throws Exception {
		Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);
		instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
		JFreeChart chart;
		ValueAxis timeAxis = new DateAxis();	//The shared time axis of all subplots.
        ChartTheme currentTheme = new StandardChartTheme("JFree");
        
        XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, timeAxis);
		XYPlot volumeSubplot = this.getVolumePlot(instrument, timeAxis);
		
		this.addAnnotationsToCandlestickPlot(candleStickSubplot, instrument);
		
		//Build combined plot based on subplots.
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
		combinedPlot.add(candleStickSubplot, 4);			//Price Plot takes 4 vertical size units.
		combinedPlot.add(volumeSubplot, 1);					//Volume Plot takes 1 vertical size unit.
		combinedPlot.setDomainAxis(timeAxis);
		
		//Build chart based on combined Plot.
		chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
		currentTheme.apply(chart);
		
		return chart;
	}
	
	
	/**
	 * Adds text annotations for Pocket Pivots to the given plot.
	 * 
	 * @param candlestickPlot The Plot to which annotations are added.
	 * @param instrument The Instrument whose price data are displayed.
	 * @throws Exception Annotation creation failed.
	 */
	private void addAnnotationsToCandlestickPlot(XYPlot candlestickPlot, final Instrument instrument) throws Exception {	
		OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
		XYTextAnnotation textAnnotation;
		List<Integer> indexOfPocketPivots = new ArrayList<>();
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
        indexOfPocketPivots = this.getIndexOfPocketPivots(quotationsSortedByDate);
        
		for(Integer indexOfPocketPivot:indexOfPocketPivots) {
			textAnnotation = new XYTextAnnotation("P", instrumentPriceData.getXValue(0, indexOfPocketPivot), 
					instrumentPriceData.getHighValue(0, indexOfPocketPivot) * 1.03);	//Show annotation 3 percent above high price.
			candlestickPlot.addAnnotation(textAnnotation);
		}
	}
	
	
	/**
	 * Determines a List of index numbers of quotations that constitute a Pocket Pivot.
	 * 
	 * @param quotationsSortedByDate A List of Quotations sorted by Date.
	 * @return A List of index numbers of the given List that constitute a Pocket Pivot.
	 */
	private List<Integer> getIndexOfPocketPivots(final List<Quotation> quotationsSortedByDate) {
//		List<Integer> indexOfDistributionDays = new ArrayList<>();
//		Quotation currentQuotation, previousQuotation;
//		boolean priceHasAdvanced, isDistributionDay;
//		
//		for(int i = 0; i < quotationsSortedByDate.size() - 1; i++) {
//			currentQuotation = quotationsSortedByDate.get(i);
//			previousQuotation = quotationsSortedByDate.get(i+1);
//			isDistributionDay = this.isDistributionDay(currentQuotation, previousQuotation);
//			priceHasAdvanced = this.hasPriceAdvancedPercent(quotationsSortedByDate, currentQuotation, 24, 5);
//			
//			if(isDistributionDay && !priceHasAdvanced)
//				indexOfDistributionDays.add(i);
//		}
//		
//		return indexOfDistributionDays;
		
		return null;
	}
}
