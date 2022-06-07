package backend.model.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the List model.
 * 
 * @author Michael
 */
public class ListTest {
	/**
	 * The list under test.
	 */
	private List list;
	
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
		
		this.list = new List();
		this.list.setId(Integer.valueOf(1));
		this.list.setName("DJI");
		this.list.setName("Dow Jones Industrial Average");
		this.list.setDescription("All stocks of the Dow Jones Industrial Average Index.");
		this.list.addInstrument(this.instrument);
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.list = null;
		this.instrument = null;
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose ID is too low.
	 */
	public void testIdTooLow() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.list.setId(0);
		
		String expectedErrorMessage = messageProvider.getMinValidationMessage("list", "id", "1");
		String errorMessage = "";
		
		try {
			this.list.validate();
			fail("Validation should have failed because Id is too low.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose name is not given.
	 */
	public void testNameNotGiven() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.list.setName("");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("list", "name", 
				String.valueOf(this.list.getName().length()), "1", "50");
		String errorMessage = "";
		
		try {
			this.list.validate();
			fail("Validation should have failed because name is not given.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose name is too long.
	 */
	public void testNameTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.list.setName("This is a list name that is way too long to be of use");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("list", "name", 
				String.valueOf(this.list.getName().length()), "1", "50");
		String errorMessage = "";
		
		try {
			this.list.validate();
			fail("Validation should have failed because name is too long.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose name is null.
	 */
	public void testNameIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.list.setName(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("list", "name");
		String errorMessage = "";
		
		try {
			this.list.validate();
			fail("Validation should have failed because name is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose description is not given.
	 */
	public void testDescriptionNotGiven() {
		this.list.setDescription("");
		
		try {
			this.list.validate();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose description is too long.
	 */
	public void testDescriptionTooLong() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();
		
		this.list.setDescription("Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. "
				+ "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. "
				+ "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das");
		
		String expectedErrorMessage = messageProvider.getSizeValidationMessage("list", "description", 
				String.valueOf(this.list.getDescription().length()), "0", "250");
		String errorMessage = "";
		
		try {
			this.list.validate();
			fail("Validation should have failed because description is too long.");
		} catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a list whose description is null.
	 */
	public void testDescriptionIsNull() {
		this.list.setDescription(null);
		
		try {
			this.list.validate();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests validation of a list that has no items given.
	 */
	public void testNoItemsGiven() {
		this.list.getInstruments().clear();
		
		try {
			this.list.validate();
			fail("Validation should have failed because list has no items defined.");
		} catch (NoItemsException expected) {
			//All is well.
		} catch (Exception e) {
			fail("No general exception should have occurred. Just the NoItemsException.");
		}
	}
}
