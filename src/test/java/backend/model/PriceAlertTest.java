package backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the price alert model.
 * 
 * @author Michael
 */
public class PriceAlertTest {
	/**
	 * The price alert under test.
	 */
	private PriceAlert priceAlert;
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.priceAlert = new PriceAlert();
		this.priceAlert.setId(Integer.valueOf(1));
		this.priceAlert.setSymbol("AAPL");
		this.priceAlert.setStockExchange(StockExchange.NYSE);
		this.priceAlert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
		this.priceAlert.setPrice(BigDecimal.valueOf(185.50));
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.priceAlert = null;
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose ID is too low.
	 */
	public void testIdTooLow() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setId(0);
		
		String expectedErrorMessage = messageProvider.getMinValidationMessage("priceAlert", "id", "1");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because Id is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
}
