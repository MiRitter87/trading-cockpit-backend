package backend.model.instrument;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import backend.model.StockExchange;

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
}
