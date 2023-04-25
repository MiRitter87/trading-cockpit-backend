package backend.controller.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import backend.controller.instrumentCheck.InstrumentCheckCountingController;
import backend.controller.instrumentCheck.InstrumentCheckPatternController;
import backend.controller.scan.IndicatorCalculator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.tools.DateTools;

/**
 * Controller for the creation of a chart displaying the Ritter Pattern Indicator.
 * 
 * @author Michael
 */
public class RitterPatternIndicatorChartController extends StatisticChartController {
	/**
	 * Gets a chart of the Ritter Pattern Indicator.
	 * 
	 * @param instrumentType The InstrumentType for which the chart is created.
	 * @param listId The ID of the list defining the instruments used for chart creation.
	 * @return The chart.
	 * @throws Exception Chart generation failed.
	 */
	public JFreeChart getRitterPatternIndicatorChart(final InstrumentType instrumentType, final Integer listId) throws Exception {
		List<Instrument> instruments = this.getAllInstrumentsWithQuotations(instrumentType, listId);
		TreeMap<Date, Float> patternIndicatorValues = this.getPatternIndicatorValues(instruments);
		XYDataset dataset = this.getRitterPatternIndicatorDataset(patternIndicatorValues);
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				this.resources.getString("statistic.chartRitterPatternIndicator.titleName"),
				null, null,	dataset, true, true, false
		);
			
		this.addHorizontalLine(chart.getXYPlot(), 0);
			
		return chart;
	}
	
	
	/**
	 * Retrieves all instruments with their quotations based on the given instrumentType or listId.
	 * 
	 * @param instrumentType The IntrumentType.
	 * @param listId The List id.
	 * @return All instruments with their quotations.
	 * @throws Exception Instrument or Quotation retrieval failed.
	 */
	private List<Instrument> getAllInstrumentsWithQuotations(final InstrumentType instrumentType, final Integer listId) throws Exception {
		List<Instrument> instruments = new ArrayList<>();
		backend.model.list.List list;
		
		//Initialize instruments.
		if(listId != null) {
			list = this.listDAO.getList(listId);
			instruments.addAll(list.getInstruments());
		}
		else {
			instruments.addAll(this.instrumentDAO.getInstruments(instrumentType));
		}
		
		//Initialize quotations of each Instrument.
		for(Instrument instrument: instruments) {
			instrument.setQuotations(this.quotationDAO.getQuotationsOfInstrument(instrument.getId()));
		}
			
		return instruments;
	}
	
	
	/**
	 * Determines the pattern indicator values for the given instruments.
	 * 
	 * @param instruments The instruments on which the pattern indicator values are calculated.
	 * @return The pattern indicator values.
	 * @throws Exception Indicator value determination failed.
	 */
	private TreeMap<Date, Float> getPatternIndicatorValues(final List<Instrument> instruments) throws Exception {
		List<Quotation> quotationsSortedByDate;
		TreeMap<Date, Float> patternIndicatorValues = new TreeMap<>();
		Quotation previousQuotation;
		int currentQuotationIndex;
		Float patternIndicatorValue;
		Date currentQuotationDate;
		
		for(Instrument instrument: instruments) {
			quotationsSortedByDate = instrument.getQuotationsSortedByDate();
			
			for(Quotation currentQuotation: quotationsSortedByDate) {
				currentQuotationIndex = quotationsSortedByDate.indexOf(currentQuotation);
				
				//Stop pattern indicator calculation for the current Instrument if no previous Quotation exists.
				if(currentQuotationIndex == (quotationsSortedByDate.size() - 1))
					break;
				
				previousQuotation = quotationsSortedByDate.get(currentQuotationIndex + 1);
				currentQuotationDate = DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate());
				
				//Check if pattern indicator of given day already exists.
				patternIndicatorValue = patternIndicatorValues.get(currentQuotationDate);
				
				if(patternIndicatorValue == null) {
					patternIndicatorValue = 0f;
				}
				
				patternIndicatorValue += this.getPatternIndicatorValue(currentQuotation, previousQuotation);
				patternIndicatorValues.put(currentQuotationDate, patternIndicatorValue);
			}
		}
		
		return patternIndicatorValues;
	}
	
	
	/**
	 * Calculates the value of the pattern indicator based on the given quotations.
	 * 
	 * @param currentQuotation The current Quotation.
	 * @param previousQuotation The previous Quotation.
	 * @return The value of the pattern indicator.
	 * @throws Exception Value determination failed.
	 */
	private float getPatternIndicatorValue(final Quotation currentQuotation, final Quotation previousQuotation) throws Exception {
		InstrumentCheckPatternController patternController = new InstrumentCheckPatternController();
		InstrumentCheckCountingController countingController = new InstrumentCheckCountingController();
		IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
		float patternIndicatorValue = 0, performance;
		boolean isGoodClose;
		
		performance = indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
		isGoodClose = countingController.isGoodClose(currentQuotation);
		
		//Bullish patterns
		if(patternController.isUpOnVolume(currentQuotation, previousQuotation))
			patternIndicatorValue++;
		
		if(patternController.isBullishHighVolumeReversal(currentQuotation))
			patternIndicatorValue++;
		
		if(isGoodClose)
			patternIndicatorValue++;
		
		if(performance > 0)
			patternIndicatorValue++;
		
		//Bearish patterns
		if(patternController.isDownOnVolume(currentQuotation, previousQuotation))
			patternIndicatorValue--;
		
		if(patternController.isBearishHighVolumeReversal(currentQuotation))
			patternIndicatorValue--;
		
		if(!isGoodClose)
			patternIndicatorValue--;
		
		if(performance < 0)
			patternIndicatorValue--;
		
		return patternIndicatorValue;
	}
	
	
	/**
	 * Constructs a XYDataset for the Ritter Pattern Indicator chart.
	 * 
	 * @param patternIndicatorValues The pattern indicator values for which the chart is calculated.
	 * @return The XYDataset.
	 */
	private XYDataset getRitterPatternIndicatorDataset(final TreeMap<Date, Float> patternIndicatorValues) {
		TimeSeries timeSeries = new TimeSeries(this.resources.getString("statistic.chartRitterPatternIndicator.timeSeriesName"));
		TimeZone timeZone = TimeZone.getDefault();
		TimeSeriesCollection timeSeriesColleciton = new TimeSeriesCollection(timeZone);
		float movingAverage;
		
		//Iterate patternIndicatorValues backwards because XYDatasets are constructed from oldest to newest value.
		for(Map.Entry<Date, Float> entry : patternIndicatorValues.entrySet()) {
			try {
				//movingAverage = this.getMovingAverageOfRitterMarketTrend(statistics, 10, i);
				timeSeries.add(new Day(entry.getKey()), entry.getValue());
			}
			catch(Exception exception) {
				continue;
			}
		}
		
        timeSeriesColleciton.addSeries(timeSeries);
        timeSeriesColleciton.setXPosition(TimePeriodAnchor.MIDDLE);
        
        return timeSeriesColleciton;
	}
}
