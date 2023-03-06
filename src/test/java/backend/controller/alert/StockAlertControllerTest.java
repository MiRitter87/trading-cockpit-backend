package backend.controller.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalTime;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.DataProvider;
import backend.model.StockExchange;

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
	
	
	@Test
	/**
	 * Tests getting the data providers.
	 */
	public void testGetDataProviders() {
		final Map<StockExchange, DataProvider> dataProviders = this.stockAlertController.getDataProviders();
		DataProvider dataProvider;
		
		dataProvider = dataProviders.get(StockExchange.NYSE);
		assertEquals(DataProvider.YAHOO, dataProvider);
		
		dataProvider = dataProviders.get(StockExchange.TSX);
		assertEquals(DataProvider.INVESTING, dataProvider);
		
		dataProvider = dataProviders.get(StockExchange.TSXV);
		assertEquals(DataProvider.GLOBEANDMAIL, dataProvider);
		
		dataProvider = dataProviders.get(StockExchange.CSE);
		assertEquals(DataProvider.YAHOO, dataProvider);
		
		dataProvider = dataProviders.get(StockExchange.LSE);
		assertEquals(DataProvider.CNBC, dataProvider);
	}
}
