package backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.StockExchange;
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
	
	
	@Test
	/**
	 * Tests validation of a price alert whose symbol is null.
	 */
	public void testSymbolIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setSymbol(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "symbol");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because symbol is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose symbol is not given.
	 */
	public void testSymbolNotGiven() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setSymbol("");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("priceAlert", "symbol", 
				String.valueOf(this.priceAlert.getSymbol().length()), "1", "6");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because symbol is not given.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose symbol is too long.
	 */
	public void testDescriptionTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setSymbol("ABCDEFG");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("priceAlert", "symbol", 
				String.valueOf(this.priceAlert.getSymbol().length()), "1", "6");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because symbol is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose stock exchange is null.
	 */
	public void testStockExchangeIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setStockExchange(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "stockExchange");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because stock exchange is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose alert type is null.
	 */
	public void testAlertTypeIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setAlertType(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "alertType");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because alert type is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose price is null.
	 */
	public void testPriceIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setPrice(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "price");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because price is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose price is too low.
	 */
	public void testPriceTooLow() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setPrice(BigDecimal.valueOf(Double.valueOf(0.009)));
		
		String expectedErrorMessage = messageProvider.getDecimalMinValidationMessage("priceAlert", "price", "0.01");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because price is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert whose price is too high.
	 */
	public void testPriceTooHigh() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setPrice(BigDecimal.valueOf(Double.valueOf(100000.01)));
		
		String expectedErrorMessage = messageProvider.getMaxValidationMessage("priceAlert", "price", "100000");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because price is too high.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
}
