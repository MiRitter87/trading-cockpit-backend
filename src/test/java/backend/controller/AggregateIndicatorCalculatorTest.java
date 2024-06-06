package backend.controller;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;

/**
 * Tests the AggregateIndicatorCalculator.
 *
 * @author Michael
 */
public class AggregateIndicatorCalculatorTest {
    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * The stock of the Uranium Industry Group.
     */
    private Instrument uraIndustryGroup;

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
        this.createUraInstrument();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteUraInstrument();
    }

    /**
     * Creates the Instrument of the Uranium Industry Group.
     */
    private void createUraInstrument() {
        this.uraIndustryGroup = new Instrument();
        this.uraIndustryGroup.setSymbol("URA");
        this.uraIndustryGroup.setName("Uranium Industry Group");
        this.uraIndustryGroup.setType(InstrumentType.IND_GROUP);

        try {
            instrumentDAO.insertInstrument(this.uraIndustryGroup);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the Instrument of the Uranium Industry Group.
     */
    private void deleteUraInstrument() {
        try {
            instrumentDAO.deleteInstrument(this.uraIndustryGroup);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
