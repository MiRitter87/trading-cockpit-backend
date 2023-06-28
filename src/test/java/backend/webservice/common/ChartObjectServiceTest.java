package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;
import backend.dao.instrument.InstrumentDAO;
import backend.model.StockExchange;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Tests the ChartObjectService.
 * 
 * @author Michael
 */
public class ChartObjectServiceTest {
	/**
	 * DAO to access chart object data.
	 */
	private static ChartObjectDAO chartObjectDAO;
	
	/**
	 * DAO to access Instrument data.
	 */
	private static InstrumentDAO instrumentDAO;
	
	/**
	 * Instrument of Apple stock.
	 */
	private Instrument appleInstrument;
	
	/**
	 * A horizontal line.
	 */
	private HorizontalLine horizontalLine1;
	
	/**
	 * Another horizontal line.
	 */
	private HorizontalLine horizontalLine2;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
		instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		try {
			DAOManager.getInstance().close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.createDummyInstruments();
		this.createDummyHorizontalLines();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteDummyHorizontalLines();
		this.deleteDummyInstruments();
	}
	
	
	/**
	 * Initializes the database with dummy Instruments.
	 */
	private void createDummyInstruments() {
		this.appleInstrument = this.getAppleInstrument();
		
		try {
			instrumentDAO.insertInstrument(this.appleInstrument);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy Instruments from the database.
	 */
	private void deleteDummyInstruments() {
		try {
			instrumentDAO.deleteInstrument(this.appleInstrument);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the database with dummy horizontal lines.
	 */
	private void createDummyHorizontalLines() {
		this.horizontalLine1 = this.getHorizontalLine1();
		this.horizontalLine2 = this.getHorizontalLine2();
		
		try {
			chartObjectDAO.insertHorizontalLine(this.horizontalLine1);
			chartObjectDAO.insertHorizontalLine(this.horizontalLine2);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Deletes the dummy horizontal lines from the database.
	 */
	private void deleteDummyHorizontalLines() {
		try {
			chartObjectDAO.deleteHorizontalLine(this.horizontalLine2);
			chartObjectDAO.deleteHorizontalLine(this.horizontalLine1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets the Instrument of the Apple stock.
	 * 
	 * @return The Instrument of the Apple stock.
	 */
	private Instrument getAppleInstrument() {
		Instrument instrument = new Instrument();
		
		instrument.setSymbol("AAPL");
		instrument.setName("Apple");
		instrument.setStockExchange(StockExchange.NYSE);
		instrument.setType(InstrumentType.STOCK);
		
		return instrument;
	}
	
	
	/**
	 * Gets the first horizontal line of the Apple stock.
	 * 
	 * @return The HorizontalLine.
	 */
	private HorizontalLine getHorizontalLine1() {
		HorizontalLine horizontalLine = new HorizontalLine();
		
		horizontalLine.setInstrument(this.appleInstrument);
		horizontalLine.setPrice(new BigDecimal("175.00"));
		
		return horizontalLine;
	}
	
	
	/**
	 * Gets the second horizontal line of the Apple stock.
	 * 
	 * @return The HorizontalLine.
	 */
	private HorizontalLine getHorizontalLine2() {
		HorizontalLine horizontalLine = new HorizontalLine();
		
		horizontalLine.setInstrument(this.appleInstrument);
		horizontalLine.setPrice(new BigDecimal("155.00"));
		
		return horizontalLine;
	}
}
