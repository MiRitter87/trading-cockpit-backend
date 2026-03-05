package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentArray;
import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Tests getter methods of the InstrumentService.
 *
 * @author MiRitter87
 */
public class InstrumentServiceGetterTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Class providing helper methods for fixture.
     */
    private InstrumentServiceFixture fixtureHelper;

    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The stock of Tesla.
     */
    private Instrument teslaStock;

    /**
     * The ratio between Apple and Tesla.
     */
    private Instrument appleTeslaRatio;

    /**
     * The technology sector.
     */
    private Instrument technologySector;

    /**
     * Copper Miners Industry Group.
     */
    private Instrument copperIndustryGroup;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
    }

    /**
     * Tasks to be performed once at end of test class.
     */
    @AfterAll
    public static void tearDownClass() {
        try {
            DAOManager.getInstance().close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.fixtureHelper = new InstrumentServiceFixture();
        this.createDummyInstruments();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteDummyInstruments();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        this.technologySector = this.fixtureHelper.getTechnologySector();
        this.copperIndustryGroup = this.fixtureHelper.getCopperIndustryGroup();
        this.appleStock = this.fixtureHelper.getAppleStock(this.technologySector, this.copperIndustryGroup);
        this.teslaStock = this.fixtureHelper.getTeslaStock();
        this.appleTeslaRatio = this.fixtureHelper.getAppleTeslaRatio(this.appleStock, this.teslaStock);

        try {
            instrumentDAO.insertInstrument(this.technologySector);
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
            instrumentDAO.insertInstrument(this.appleStock);
            instrumentDAO.insertInstrument(this.teslaStock);
            instrumentDAO.insertInstrument(this.appleTeslaRatio);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.appleTeslaRatio);
            instrumentDAO.deleteInstrument(this.teslaStock);
            instrumentDAO.deleteInstrument(this.appleStock);
            instrumentDAO.deleteInstrument(this.copperIndustryGroup);
            instrumentDAO.deleteInstrument(this.technologySector);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of an Instrument.
     */
    @Test
    public void testGetInstrument() {
        WebServiceResult getInstrumentResult;
        Instrument instrument;

        // Get the instrument.
        InstrumentService service = new InstrumentService();
        getInstrumentResult = service.getInstrument(this.appleStock.getId());

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(getInstrumentResult));

        // Assure that an instrument is returned
        assertTrue(getInstrumentResult.getData() instanceof Instrument);

        instrument = (Instrument) getInstrumentResult.getData();

        // Check each attribute of the instrument.
        assertEquals(this.appleStock, instrument);
    }

    /**
     * Tests the retrieval of an Instrument that constitutes a ratio.
     */
    @Test
    public void testGetInstrumentRatio() {
        WebServiceResult getInstrumentResult;
        Instrument instrument;

        // Get the instrument.
        InstrumentService service = new InstrumentService();
        getInstrumentResult = service.getInstrument(this.appleTeslaRatio.getId());

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(getInstrumentResult));

        // Assure that an instrument is returned
        assertTrue(getInstrumentResult.getData() instanceof Instrument);

        instrument = (Instrument) getInstrumentResult.getData();

        // Check each attribute of the instrument.
        assertEquals(this.appleTeslaRatio, instrument);
    }

    /**
     * Tests the retrieval of an instrument with an id that is unknown.
     */
    @Test
    public void testGetInstrumentWithUnknownId() {
        WebServiceResult getInstrumentResult;
        final Integer unknownInstrumentId = 0;
        String expectedErrorMessage;
        String actualErrorMessage;

        // Get the instrument.
        InstrumentService service = new InstrumentService();
        getInstrumentResult = service.getInstrument(unknownInstrumentId);

        // Assure that no instrument is returned
        assertNull(getInstrumentResult.getData());

        // There should be a return message of type E.
        assertTrue(getInstrumentResult.getMessages().size() == 1);
        assertTrue(getInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.notFound"),
                unknownInstrumentId);
        actualErrorMessage = getInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    /**
     * Tests the retrieval of all instruments without quotations.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetAllInstrumentsWithoutQuotations() {
        WebServiceResult getInstrumentsResult;
        InstrumentArray instruments;
        Instrument instrument;

        // Get the instruments.
        InstrumentService service = new InstrumentService();
        getInstrumentsResult = service.getInstruments(null);
        instruments = (InstrumentArray) getInstrumentsResult.getData();

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult));

        // Check if all instruments are returned.
        assertEquals(5, instruments.getInstruments().size());

        // Check all instruments by each attribute.
        instrument = instruments.getInstruments().get(0);
        assertEquals(this.technologySector, instrument);

        instrument = instruments.getInstruments().get(1);
        assertEquals(this.copperIndustryGroup, instrument);

        instrument = instruments.getInstruments().get(2);
        assertEquals(this.appleStock, instrument);

        instrument = instruments.getInstruments().get(3);
        assertEquals(this.teslaStock, instrument);

        instrument = instruments.getInstruments().get(4);
        assertEquals(this.appleTeslaRatio, instrument);
    }

    /**
     * Tests the retrieval of all instruments of type industry group.
     */
    @Test
    public void testGetInstrumentsTypeIndustryGroup() {
        WebServiceResult getInstrumentsResult;
        InstrumentArray instruments;
        Instrument instrument;

        // Get the instruments.
        InstrumentService service = new InstrumentService();
        getInstrumentsResult = service.getInstruments(InstrumentType.IND_GROUP);
        instruments = (InstrumentArray) getInstrumentsResult.getData();

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult));

        // Check if one Instrument is returned.
        assertEquals(1, instruments.getInstruments().size());

        // Check the Instrument by each attribute.
        instrument = instruments.getInstruments().get(0);
        assertEquals(this.copperIndustryGroup, instrument);
    }

    /**
     * Tests the retrieval of all instruments of type ratio. The test assures that dividend and divisor instances are
     * loaded.
     */
    @Test
    public void testGetIntrumentsTypeRatio() {
        WebServiceResult getInstrumentsResult;
        InstrumentArray instruments;
        Instrument instrument;

        // Get the instruments.
        InstrumentService service = new InstrumentService();
        getInstrumentsResult = service.getInstruments(InstrumentType.RATIO);
        instruments = (InstrumentArray) getInstrumentsResult.getData();

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult));

        // Check if one Instrument is returned.
        assertEquals(1, instruments.getInstruments().size());

        // Check the Instrument by each attribute.
        instrument = instruments.getInstruments().get(0);
        assertEquals(this.appleTeslaRatio, instrument);
    }
}
