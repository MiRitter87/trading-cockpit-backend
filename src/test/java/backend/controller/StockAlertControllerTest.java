package backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the functionality of the StockAlertController
 * 
 * @author Michael
 */
public class StockAlertControllerTest {
	/**
	 * The StockAlertController under test.
	 */
	private StockAlertController stockAlertController;
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		try {
			this.stockAlertController = new StockAlertController();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.stockAlertController = null;
	}
	
	
	@Test
	/**
	 * Tests getting the query interval.
	 */
	public void testGetQueryInterval() {
		final int expectedQueryInterval = 30;
		final int actualQueryInterval = this.stockAlertController.getQueryInterval();
		
		assertEquals(expectedQueryInterval, actualQueryInterval);
	}
	
	
	@Test
	/**
	 * Tests getting the start time.
	 */
	public void testGetStartTime() {
		final LocalTime expectedStartTime = LocalTime.of(15, 30);
		final LocalTime actualStartTime = this.stockAlertController.getStartTime();
		
		assertEquals(expectedStartTime, actualStartTime);		
	}
	
	
	@Test
	/**
	 * Tests getting the end time.
	 */
	public void testGetEndTime() {
		final LocalTime expectedEndTime = LocalTime.of(22, 0);
		final LocalTime actualEndTime = this.stockAlertController.getEndTime();
		
		assertEquals(expectedEndTime, actualEndTime);		
	}
}
