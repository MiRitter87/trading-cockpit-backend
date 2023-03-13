package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import backend.controller.scan.IndicatorCalculator;
import backend.dao.quotation.QuotationProviderYahooDAO;
import backend.dao.quotation.QuotationProviderYahooDAOStub;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the InstrumentCheckPatternController.
 * 
 * @author Michael
 */
public class InstrumentCheckPatternControllerTest {
	/**
	 * Access to localized application resources.
	 */
	private ResourceBundle resources = ResourceBundle.getBundle("backend");		
	
	/**
	 * DAO to access quotation data from Yahoo.
	 */
	private static QuotationProviderYahooDAO quotationProviderYahooDAO;
	
	/**
	 * A list of quotations of the DML stock.
	 */
	private List<Quotation> dmlQuotations;
	
	/**
	 * The controller for Instrument checks.
	 */
	private InstrumentCheckPatternController instrumentCheckPatternController;
	
	
	@BeforeAll
	/**
	 * Tasks to be performed once at startup of test class.
	 */
	public static void setUpClass() {
		quotationProviderYahooDAO = new QuotationProviderYahooDAOStub();
	}
	
	
	@AfterAll
	/**
	 * Tasks to be performed once at end of test class.
	 */
	public static void tearDownClass() {
		quotationProviderYahooDAO = null;
	}
	
	
	@BeforeEach
	/**
	 * Tasks to be performed before each test is run.
	 */
	private void setUp() {
		this.instrumentCheckPatternController = new InstrumentCheckPatternController();
		
		this.initializeDMLQuotations();
		this.initializeDMLIndicators();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.instrumentCheckPatternController = null;
		this.dmlQuotations = null;
	}
	
	
	/**
	 * Initializes quotations of the DML stock.
	 */
	private void initializeDMLQuotations() {
		try {
			dmlQuotations = quotationProviderYahooDAO.getQuotationHistory("DML", StockExchange.TSX, InstrumentType.STOCK, 1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the indicators of the DML stock.
	 */
	private void initializeDMLIndicators() {
		IndicatorCalculator indicatorCalculator = new IndicatorCalculator();
		List<Quotation> sortedQuotations;
		Instrument instrument = new Instrument();
		Quotation quotation;

		instrument.setQuotations(this.dmlQuotations);
		sortedQuotations = instrument.getQuotationsSortedByDate();
		
		for(int i = 0; i < sortedQuotations.size(); i++) {
			quotation = sortedQuotations.get(i);
			quotation = indicatorCalculator.calculateIndicators(instrument, quotation, true);
		}
	}
}