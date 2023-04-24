package backend.controller.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

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
		Map<Date, Float> patternIndicatorValues = this.getPatternIndicatorValues(instruments);
		
		return null;
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
	 */
	private Map<Date, Float> getPatternIndicatorValues(final List<Instrument> instruments) {
		List<Quotation> quotationsSortedByDate;
		Map<Date, Float> patternIndicatorValues = new HashMap<>(252);	//252 trading days per year.
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
				
				//TODO Apply check-up methods to get value of pattern indicator.
				
				patternIndicatorValues.put(currentQuotationDate, patternIndicatorValue);
			}
		}
		
		return patternIndicatorValues;
	}
}
