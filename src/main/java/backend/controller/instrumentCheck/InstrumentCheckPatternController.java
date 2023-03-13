package backend.controller.instrumentCheck;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import backend.controller.scan.IndicatorCalculator;
import backend.model.instrument.Quotation;
import backend.model.protocol.ProtocolEntry;

/**
 * Controller that performs Instrument health checks that are based on certain price and volume patterns.
 * For example this can be a high volume price reversal or a stalling in price accompanied by increased volume (churning).
 * 
 * @author Michael
 */
public class InstrumentCheckPatternController {
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
	 * Checks for days on which the stock rises a certain amount on above-average volume.
	 * The check begins at the start date and goes up until the most recent Quotation.
	 * 
	 * @param startDate The date at which the check starts.
	 * @param quotations The quotations that build the trading history.
	 * @return List of ProtocolEntry, for each day on which the Instrument trades up on volume.
	 * @throws Exception The check failed because data are not fully available or corrupt.
	 */
	public List<ProtocolEntry> checkUpOnVolume(final Date startDate, final List<Quotation> quotations) throws Exception {
		return null;
	}
}
