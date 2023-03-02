package backend.controller.statistic;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Controller for the creation of a chart displaying an Instrument with Follow-Through Days.
 * 
 * @author Michael
 */
public class FollowThroughDaysChartController extends StatisticChartController {
	/**
	 * The performance threshold that defines a Follow-Through Day.
	 */
	private static final float FTD_PERCENT_THRESHOLD = (float) 1.7;
	
	
	/**
	 * Gets a chart of an Instrument marked with Follow-Through Days.
	 * 
	 * @param instrumentId The ID of the Instrument used for Statistic chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getFollowThroughDaysChart(final Integer instrumentId) throws Exception {
		Instrument instrument = this.instrumentDAO.getInstrument(instrumentId);
		instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrumentId));
		JFreeChart chart;
		ValueAxis timeAxis = new DateAxis();	//The shared time axis of all subplots.
        ChartTheme currentTheme = new StandardChartTheme("JFree");
        
		XYPlot candleStickSubplot = this.getCandlestickPlot(instrument, timeAxis);
		XYPlot volumeSubplot = this.getVolumePlot(instrument, timeAxis);
		XYPlot failedFTDSubplot = this.getFailedFTDPlot(instrument, timeAxis);
		
		this.addAnnotationsToCandlestickPlot(candleStickSubplot, instrument);
		
		//Build combined plot based on subplots.
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot();
		combinedPlot.add(failedFTDSubplot, 1);				//Failed FTD Plot takes 1 vertical size unit.
		combinedPlot.add(candleStickSubplot, 4);			//Price Plot takes 4 vertical size units.
		combinedPlot.add(volumeSubplot, 1);					//Volume Plot takes 1 vertical size unit.
		combinedPlot.setDomainAxis(timeAxis);
		
		//Build chart based on combined Plot.
		chart = new JFreeChart(instrument.getName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);
		currentTheme.apply(chart);
		
		return chart;
	}
	
	
	/**
	 * Adds text annotations for Follow-Through Days to the given plot.
	 * 
	 * @param candlestickPlot The Plot to which annotations are added.
	 * @param instrument The Instrument whose price data are displayed.
	 * @throws Exception Annotation creation failed.
	 */
	private void addAnnotationsToCandlestickPlot(XYPlot candlestickPlot, final Instrument instrument) 
			throws Exception {
		
		OHLCDataset instrumentPriceData = this.getInstrumentOHLCDataset(instrument);
		XYTextAnnotation textAnnotation;
		List<Integer> indexOfFollowThroughDays = new ArrayList<>();
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		
		indexOfFollowThroughDays = this.getIndexOfFollowThroughDays(quotationsSortedByDate);
        
		for(Integer indexOfFollowThroughDay:indexOfFollowThroughDays) {
			textAnnotation = new XYTextAnnotation("F", instrumentPriceData.getXValue(0, indexOfFollowThroughDay), 
					instrumentPriceData.getHighValue(0, indexOfFollowThroughDay) * 1.03);	//Show annotation 3 percent above high price.
			candlestickPlot.addAnnotation(textAnnotation);
		}
	}
	
	
	/**
	 * Determines a List of index numbers of quotations that constitute a Follow-Through Day.
	 * 
	 * @param quotationsSortedByDate A List of Quotations sorted by Date.
	 * @return A List of index numbers of the given List that constitute a Follow-Through Day.
	 */
	private List<Integer> getIndexOfFollowThroughDays(final List<Quotation> quotationsSortedByDate) {
		List<Integer> indexOfFollowThroughDays = new ArrayList<>();
		Quotation currentQuotation, previousQuotation;
		boolean isFollowThroughDay;
		
		for(int i = 0; i < quotationsSortedByDate.size() - 1; i++) {
			currentQuotation = quotationsSortedByDate.get(i);
			previousQuotation = quotationsSortedByDate.get(i+1);
			isFollowThroughDay = this.isFollowThroughDay(currentQuotation, previousQuotation);
			
			if(isFollowThroughDay)
				indexOfFollowThroughDays.add(i);
		}
		
		return indexOfFollowThroughDays;
	}
	
	
	/**
	 * Builds a plot to display failed Follow-Through Days.
	 * 
	 * @param instrument The Instrument.
	 * @param timeAxis The x-Axis (time).
	 * @return A XYPlot depicting failed Follow-Through Days.
	 * @throws Exception Plot generation failed.
	 */
	private XYPlot getFailedFTDPlot(final Instrument instrument, final ValueAxis timeAxis) throws Exception {
		IntervalXYDataset distributionDaySumData = this.getFailedFTDDataset(instrument);
        NumberAxis failedFTDAxis = new NumberAxis();
		
		XYBarRenderer failedFTDRenderer = new XYBarRenderer();
		XYPlot failedFTDSubplot = new XYPlot(distributionDaySumData, timeAxis, failedFTDAxis, failedFTDRenderer);
		
		return failedFTDSubplot;
	}
	
	
	
	/**
	 * Gets a dataset containing failed Follow-Through Days of the given Instrument.
	 * 
	 * @param instrument The Instrument.
	 * @return A dataset with the failed Follow-Through Days.
	 * @throws Exception Dataset creation failed.
	 */
	private IntervalXYDataset getFailedFTDDataset(final Instrument instrument) throws Exception {
		List<Quotation> quotationsSortedByDate = instrument.getQuotationsSortedByDate();
		List<Integer> indexOfFollowThroughDays = new ArrayList<>();
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries failedFTDTimeSeries = new TimeSeries(this.resources.getString("statistic.chartFollowThroughDays.timeSeriesFailedFTDName"));
		
		indexOfFollowThroughDays = this.getIndexOfFollowThroughDays(quotationsSortedByDate);
        
        //Determine failed Follow-Through Days.
        for(Quotation tempQuotation: quotationsSortedByDate) {        	
        	if(indexOfFollowThroughDays.contains(quotationsSortedByDate.indexOf(tempQuotation))) {
        		//A Follow-Through Day is considered failed if a Distribution Day follows during the next 5 trading days.
        		if(this.isDistributionDayFollowing(tempQuotation, quotationsSortedByDate, 5))    	
        			failedFTDTimeSeries.add(new Day(tempQuotation.getDate()), 1);  
        		
        		//TODO A Follow-Through Day is considered failed if the price undercuts the low established within 10 days before the FTD.
        	}
        }
        
        dataset.addSeries(failedFTDTimeSeries);
		
		return dataset;
	}
	
	
	/**
	 * Checks if a Distribution Day is following during the next days after the given Quotation.
	 * 
	 * @param quotation The Quotation which is reference point for the checkup.
	 * @param quotationsSortedByDate A sorted List of quotations building the trading history.
	 * @param numberDays The number of days checked after the given Quotation.
	 * @return true, if Distribution Day follows in the given number of days after the given Quotation. False, if not.
	 */
	private boolean isDistributionDayFollowing(final Quotation quotation, final List<Quotation> quotationsSortedByDate, final int numberDays) {
		Quotation currentQuotation, nextQuotation;
		int indexStart, indexEnd;
		
		indexStart = quotationsSortedByDate.indexOf(quotation);
    	
    	if((indexStart - numberDays) < 0) {
    		indexEnd = 0;
    	}
    	else {
    		indexEnd = indexStart - numberDays;    		
    	}
    	
    	indexStart--;	//Do not start Distribution Day check at the date of the current Quotation but a day later.
    	
    	if(indexStart < 0)
    		return false;	//quotation is already the newest price.
    		
		for(int i = indexStart; i >= indexEnd; i--) {
			currentQuotation = quotationsSortedByDate.get(i+1);
			nextQuotation = quotationsSortedByDate.get(i);
			
			if(this.isDistributionDay(nextQuotation, currentQuotation))
				return true;
		}

		return false;
	}
	
	
	/**
	 * Checks if the day of the current Quotation constitutes a Follow-Through Day.
	 * 
	 * @param currentQuotation The current Quotation.
	 * @param previousQuotation The previous Quotation.
	 * @return true, if day of current Quotation is Follow-Through Day; false, if not.
	 */
	private boolean isFollowThroughDay(final Quotation currentQuotation, final Quotation previousQuotation) {
		float performance;
		
		performance = currentQuotation.getClose().divide(previousQuotation.getClose(), 4, RoundingMode.HALF_UP).floatValue() - 1;
		performance = performance * 100;	//Get performance in percent.
		
		if(performance >= FTD_PERCENT_THRESHOLD && (currentQuotation.getVolume() > previousQuotation.getVolume()))
			return true;
		else
			return false;
	}
}
