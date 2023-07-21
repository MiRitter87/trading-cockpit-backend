package backend.model.chart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the HorizontalLine model.
 * 
 * @author Michael
 */
public class HorizontalLineTest {
	/**
	 * The HorizontalLine under test.
	 */
	private HorizontalLine horizontalLine;
	
	/**
	 * The referenced Instrument.
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
		this.instrument.setStockExchange(StockExchange.NDQ);
		this.instrument.setName("Apple");
		
		this.horizontalLine = new HorizontalLine();
		this.horizontalLine.setId(Integer.valueOf(1));
		this.horizontalLine.setPrice(new BigDecimal("185.27"));
		this.horizontalLine.setInstrument(this.instrument);
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.horizontalLine = null;
		this.instrument = null;
	}
	
	
	@Test
	/**
	 * Tests validation of a HorizontalLine whose ID is too low.
	 */
	public void testIdTooLow() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.horizontalLine.setId(0);
		
		String expectedErrorMessage = messageProvider.getMinValidationMessage("horizontalLine", "id", "1");
		String errorMessage = "";
		
		try {
			this.horizontalLine.validate();
			fail("Validation should have failed because Id is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a HorizontalLine that has no Instrument defined.
	 */
	public void testNoInstrumentDefined() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();	
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("horizontalLine", "instrument");
		String actualErrorMessage = "";
		
		this.horizontalLine.setInstrument(null);
		
		try {
			this.horizontalLine.validate();
			fail("Validation should have failed because no instrument is defined.");
		} catch (Exception expected) {
			actualErrorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, actualErrorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a HorizontalLine whose price is null.
	 */
	public void testPriceIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.horizontalLine.setPrice(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("horizontalLine", "price");
		String errorMessage = "";
		
		try {
			this.horizontalLine.validate();
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
		this.horizontalLine.setPrice(BigDecimal.valueOf(Double.valueOf(0.009)));
		
		String expectedErrorMessage = messageProvider.getDecimalMinValidationMessage("horizontalLine", "price", "0.01");
		String errorMessage = "";
		
		try {
			this.horizontalLine.validate();
			fail("Validation should have failed because price is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a HorizontalLine whose price is too high.
	 */
	public void testPriceTooHigh() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.horizontalLine.setPrice(BigDecimal.valueOf(Double.valueOf(100000.01)));
		
		String expectedErrorMessage = messageProvider.getMaxValidationMessage("horizontalLine", "price", "100000");
		String errorMessage = "";
		
		try {
			this.horizontalLine.validate();
			fail("Validation should have failed because price is too high.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
}
