package backend.controller.scan;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Performs calculations of indicators based on the instruments quotations.
 * 
 * @author Michael
 */
public class IndicatorCalculator {
	/**
	 * The number of trading days per month.
	 */
	private static final int TRADING_DAYS_PER_MONTH = 21;
	
	/**
	 * Calculates the percentage sum needed for calculation of the RS number.
	 * 
	 * @param instrument The instrument whose quotations are used for calculation.
	 * @param quotation The quotation of the date on which the percentage sum is calculated.
	 * @return The percentage sum.
	 */
	public float getRSPercentSum(final Instrument instrument, final Quotation quotation) {
		List<Quotation> sortedQuotations = instrument.getQuotationsSortedByDate();
		int indexOfQuotation = sortedQuotations.indexOf(quotation);
		BigDecimal rsPercentSum = BigDecimal.valueOf(0);
		
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 3));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 3));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 6));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 9));
		rsPercentSum = rsPercentSum.add(this.getPerformanceOfInterval(sortedQuotations, indexOfQuotation, 12));
		
		rsPercentSum.setScale(2);
		
		return rsPercentSum.floatValue();
	}
	
	
	/**
	 * Provides the performance of a given interval.
	 * 
	 * @param sortedQuotations The quotations containing date and price information for performance calculation.
	 * @param indexOfQuotation The starting point from which the performance is calculated.
	 * @param months The number of months for performance calculation.
	 * @return The performance of the given interval in percent.
	 */
	private BigDecimal getPerformanceOfInterval(final List<Quotation> sortedQuotations, final int indexOfQuotation, final int months) {
		BigDecimal divisionResult = BigDecimal.valueOf(0);
		int indexOfQuotationForInterval = indexOfQuotation + (TRADING_DAYS_PER_MONTH * months) -1;
		
		divisionResult = sortedQuotations.get(indexOfQuotation).getPrice().divide
				(sortedQuotations.get(indexOfQuotationForInterval).getPrice(), 4, RoundingMode.HALF_UP);
		divisionResult = divisionResult.subtract(BigDecimal.valueOf(1));
		divisionResult = divisionResult.multiply(BigDecimal.valueOf(100));
		
		return divisionResult;
	}
}
