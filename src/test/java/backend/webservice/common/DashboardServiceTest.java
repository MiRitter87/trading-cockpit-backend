package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Tests the DashboardService.
 *
 * @author Michael
 */
public class DashboardServiceTest {
    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * Industry Group: Copper Miners.
     */
    private Instrument copperIndustryGroup;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
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
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.copperIndustryGroup = null;
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        this.copperIndustryGroup = this.getCopperIndustryGroup();

        try {
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Gets the Instrument of the Copper Miners Industry Group.
     *
     * @return The Instrument of the Copper Miners Industry Group.
     */
    private Instrument getCopperIndustryGroup() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("COPX");
        instrument.setName("Global X Copper Miners ETF");
        instrument.setStockExchange(StockExchange.NYSE);
        instrument.setType(InstrumentType.IND_GROUP);

        return instrument;
    }
}
