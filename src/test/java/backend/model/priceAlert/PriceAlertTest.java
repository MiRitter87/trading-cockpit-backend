package backend.model.priceAlert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the price alert model.
 * 
 * @author Michael
 */
public class PriceAlertTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");
	
	/**
	 * The price alert under test.
	 */
	private PriceAlert priceAlert;
	
	/**
	 * The Instrument of the PriceAlert.
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
		
		this.priceAlert = new PriceAlert();
		this.priceAlert.setId(Integer.valueOf(1));
		this.priceAlert.setInstrument(this.instrument);
		this.priceAlert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
		this.priceAlert.setPrice(BigDecimal.valueOf(185.50));
		this.priceAlert.setCurrency(Currency.USD);
		this.priceAlert.setSendMail(false);
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.priceAlert = null;
		this.instrument = null;
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
	 * Tests validation of a PriceAlert that has no Instrument defined.
	 */
	public void testNoInstrumentDefined() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();	
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "instrument");
		String actualErrorMessage = "";
		
		this.priceAlert.setInstrument(null);
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because no instrument is defined.");
		} catch (Exception expected) {
			actualErrorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, actualErrorMessage);
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
	
	
	@Test
	/**
	 * Tests validation of a price alert whose currency is null.
	 */
	public void testCurencyIsNull() {
		ValidationMessageProvider messageProvider = new ValidationMessageProvider();		
		this.priceAlert.setCurrency(null);
		
		String expectedErrorMessage = messageProvider.getNotNullValidationMessage("priceAlert", "currency");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because currency is null.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert if mail should be send but mail address is null.
	 */
	public void testMailAddressNullIfSendMailTrue() {
		this.priceAlert.setSendMail(true);
		this.priceAlert.setAlertMailAddress(null);
		
		String expectedErrorMessage = this.resources.getString("priceAlert.alertMailAddress.notNull.message");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because 'alertMailAddress' is null while 'sendMail' is true.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getLocalizedMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
	
	
	@Test
	/**
	 * Tests validation of a price alert if the mail address has the wrong length.
	 */
	public void testMailAddressWrongLength() {
		this.priceAlert.setSendMail(true);
		this.priceAlert.setAlertMailAddress("a@bc");
		
		String expectedErrorMessage = MessageFormat.format(this.resources.getString("priceAlert.alertMailAddress.size.message"),
				this.priceAlert.getAlertMailAddress().length(), "5", "254");
		String errorMessage = "";
		
		try {
			this.priceAlert.validate();
			fail("Validation should have failed because 'alertMailAddress' is too short.");
		} 
		catch (Exception expected) {
			errorMessage = expected.getLocalizedMessage();
		}
		
		assertEquals(expectedErrorMessage, errorMessage);
	}
}
