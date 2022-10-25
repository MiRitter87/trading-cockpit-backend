package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.DataProvider;

/**
 * Tests the functionality of the ScanController
 * 
 * @author Michael
 */
public class ScanControllerTest {
	/**
	 * The ScanController under test.
	 */
	private ScanController scanController;
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		try {
			this.scanController = new ScanController();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.scanController = null;
	}
	
	
	@Test
	/**
	 * Tests getting the query interval.
	 */
	public void testGetQueryInterval() {
		final int expectedQueryInterval = 5;
		final int actualQueryInterval = this.scanController.getQueryInterval();
		
		assertEquals(expectedQueryInterval, actualQueryInterval);
	}
	
	
	@Test
	/**
	 * Tests getting the data provider for historical data.
	 */
	public void testGetDataProviderHistory() {
		final DataProvider expectedDataProvider = DataProvider.MARKETWATCH;
		final DataProvider actualDataProvider = this.scanController.getDataProvider();
		
		assertEquals(expectedDataProvider, actualDataProvider);
	}
}
