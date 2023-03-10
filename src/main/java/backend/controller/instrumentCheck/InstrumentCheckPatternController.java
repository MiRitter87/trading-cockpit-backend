package backend.controller.instrumentCheck;

import java.util.ResourceBundle;

import backend.controller.scan.IndicatorCalculator;

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
}
