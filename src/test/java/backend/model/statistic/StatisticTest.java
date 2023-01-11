package backend.model.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests the Statistic model.
 * 
 * @author Michael
 */
public class StatisticTest {
	/**
	 * The Statistic under test.
	 */
	private Statistic statistic;
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.statistic = new Statistic();
		this.statistic.setNumberAboveSma50(3);
		this.statistic.setNumberAtOrBelowSma50(2);
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.statistic = null;
	}
	
	
	@Test
	/**
	 * Tests correct calculation of the percentage above SMA(50).
	 */
	public void testGetPercentAboveSma50() {
		float percentAboveSma50 = this.statistic.getPercentAboveSma50();
		
		assertEquals(60, percentAboveSma50);
	}
}
