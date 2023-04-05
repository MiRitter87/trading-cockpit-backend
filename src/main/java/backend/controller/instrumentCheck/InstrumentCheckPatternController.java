package backend.controller.instrumentCheck;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.controller.scan.IndicatorCalculator;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Controller that performs Instrument health checks that are based on certain price and volume patterns.
 * For example this can be a high volume price reversal or a stalling in price accompanied by increased volume (churning).
 * 
 * @author Michael
 */
public class InstrumentCheckPatternController {
	/**
	 * The performance threshold of an "Up on Volume"-day.
	 */
	private static final float UP_PERFORMANCE_THRESHOLD = (float) 3.0;
	
	/**
	 * The performance threshold of an "Down on Volume"-day.
	 */
	private static final float DOWN_PERFORMANCE_THRESHOLD = (float) -3.0;
	
	/**
	 * The upwards performance threshold of a "Churning"-day.
	 */
	private static final float CHURNING_UP_THRESHOLD = (float) 1.0;
	
	/**
	 * The downwards performance threshold of a "Churning"-day.
	 */
	private static final float CHURNING_DOWN_THRESHOLD = (float) -1.0;
	
	/**
	 * The threshold of the daily price range for reversal calculation.
	 */
	private static final float REVERSAL_THRESHOLD = (float) 0.4;
	
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * Indicator calculator.
	 */
	private IndicatorCalculator indicatorCalculator;
	
	
	/**
	 * Default constructor.
	 */
	public InstrumentCheckPatternController() {
		this.indicatorCalculator = new IndicatorCalculator();
	}
	
	
	/**
	 * Checks for days on which the Instrument rises a certain amount on above-average volume.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the Instrument trades up on volume.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkUpOnVolume(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		int startIndex;
		Quotation currentQuotation, previousQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		float performance;

		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			if((i+1) < sortedQuotations.getQuotations().size()) {
				previousQuotation = sortedQuotations.getQuotations().get(i+1);
			}
			else {
				continue;
			}
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			if(previousQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +previousQuotation.getId());
			
			if(currentQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +currentQuotation.getId());
			
			performance = this.indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
			
			if(performance >= UP_PERFORMANCE_THRESHOLD && currentQuotation.getVolume() > currentQuotation.getIndicator().getSma30Volume()) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(this.resources.getString("protocol.upOnVolume"));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks for days on which the Instrument declines a certain amount on above-average volume.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the Instrument trades down on volume.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkDownOnVolume(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		int startIndex;
		Quotation currentQuotation, previousQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		float performance;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			if((i+1) < sortedQuotations.getQuotations().size()) {
				previousQuotation = sortedQuotations.getQuotations().get(i+1);
			}
			else {
				continue;
			}
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			if(previousQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +previousQuotation.getId());
			
			if(currentQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +currentQuotation.getId());
			
			performance = this.indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
			
			if(performance <= DOWN_PERFORMANCE_THRESHOLD && currentQuotation.getVolume() > currentQuotation.getIndicator().getSma30Volume()) {
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(this.resources.getString("protocol.downOnVolume"));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks for days on which the Instrument stalls in price on increased volume (churning).
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the Instrument is churning.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkChurning(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		int startIndex;
		Quotation currentQuotation, previousQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		float performance;
		
		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			if((i+1) < sortedQuotations.getQuotations().size()) {
				previousQuotation = sortedQuotations.getQuotations().get(i+1);
			}
			else {
				continue;
			}
			
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			if(previousQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +previousQuotation.getId());
			
			if(currentQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +currentQuotation.getId());
			
			performance = this.indicatorCalculator.getPerformance(currentQuotation, previousQuotation);
			
			if(performance <= CHURNING_UP_THRESHOLD && performance >= CHURNING_DOWN_THRESHOLD &&
					currentQuotation.getVolume() > currentQuotation.getIndicator().getSma30Volume()) {
				
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(this.resources.getString("protocol.churning"));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
	
	
	/**
	 * Checks for days on which the Instrument builds a reversal (open and close in lower third of candle on above-average volume).
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param sortedQuotations The quotations sorted by date that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the Instrument shows a reversal.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkHighVolumeReversal(final Date startDate, final QuotationArray sortedQuotations) throws Exception {
		int startIndex;
		Quotation currentQuotation;
		List<ProtocolEntry> protocolEntries = new ArrayList<>();
		ProtocolEntry protocolEntry;
		BigDecimal dailyPriceRange, reversalThresholdPrice;

		startIndex = sortedQuotations.getIndexOfQuotationWithDate(startDate);
		
		if(startIndex == -1)
			throw new Exception("Could not find a quotation at or after the given start date.");
		
		for(int i = startIndex; i >= 0; i--) {
			currentQuotation = sortedQuotations.getQuotations().get(i);
			
			if(currentQuotation.getIndicator() == null)
				throw new Exception("No indicator is defined for Quotation with ID: " +currentQuotation.getId());
			
			dailyPriceRange = currentQuotation.getHigh().subtract(currentQuotation.getLow());
			reversalThresholdPrice = currentQuotation.getLow().add(dailyPriceRange.multiply(new BigDecimal(REVERSAL_THRESHOLD)));
			
			if(currentQuotation.getOpen().compareTo(reversalThresholdPrice) <= 0 && 
					currentQuotation.getClose().compareTo(reversalThresholdPrice) <= 0 &&
					currentQuotation.getVolume() > currentQuotation.getIndicator().getSma30Volume()) {
				
				protocolEntry = new ProtocolEntry();
				protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
				protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(currentQuotation.getDate()));
				protocolEntry.setText(this.resources.getString("protocol.reversal"));
				protocolEntries.add(protocolEntry);
			}
		}
		
		return protocolEntries;
	}
}
