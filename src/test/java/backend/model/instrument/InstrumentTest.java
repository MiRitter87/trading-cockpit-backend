package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the instrument model.
 * 
 * @author Michael
 */
public class InstrumentTest {
	/**
	 * The instrument under test.
	 */
	private Instrument instrument;
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.instrument = new Instrument();
		this.instrument.setId(Integer.valueOf(1));
		this.instrument.setSymbol("AAPL");
		this.instrument.setType(InstrumentType.STOCK);
		this.instrument.setStockExchange(StockExchange.NYSE);
		this.instrument.setName("Apple");
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.instrument = null;
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose ID is too low.
	 */
	public void testIdTooLow() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setId(0);
		
		String expectedErrorMessage = messageProvider.getMinValidationMessage("instrument", "id", "1");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because Id is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose symbol is null.
	 */
	public void testSymbolIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setSymbol(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("instrument", "symbol");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because symbol is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose symbol is not given.
	 */
	public void testSymbolNotGiven() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setSymbol("");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "symbol", 
				String.valueOf(this.instrument.getSymbol().length()), "1", "6");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because symbol is not given.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose symbol is too long.
	 */
	public void testSymbolTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setSymbol("ABCDEFG");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("priceAlert", "symbol", 
				String.valueOf(this.instrument.getSymbol().length()), "1", "6");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because symbol is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose type is null.
	 */
	public void testTypeIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setType(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("instrument", "type");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because type is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose stock exchange is null.
	 */
	public void testStockExchangeIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setStockExchange(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("instrument", "stockExchange");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because stock exchange is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose name is too long.
	 */
	public void testNameTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.instrument.setName("This is a company name that is way too long to be of use");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "name", 
				String.valueOf(this.instrument.getName().length()), "0", "50");
		String errorMessage = "";
		
		try {
			this.instrument.validate();
			fail("Validation should have failed because name is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of an instrument whose name is null.
	 */
	public void testNameIsNull() {
		this.instrument.setName(null);
		
		try {
			this.instrument.validate();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
