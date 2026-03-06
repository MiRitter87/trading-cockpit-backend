package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Tests the instrument service.
 *
 * @author Michael
 */
public class InstrumentServiceTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private InstrumentServiceFixture fixtureHelper;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

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
        this.microsoftStock = this.fixtureHelper.getMicrosoftStock();

        try {
            instrumentDAO.insertInstrument(this.technologySector);
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
            instrumentDAO.insertInstrument(this.appleStock);
            instrumentDAO.insertInstrument(this.microsoftStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.microsoftStock);
            instrumentDAO.deleteInstrument(this.appleStock);
            instrumentDAO.deleteInstrument(this.copperIndustryGroup);
            instrumentDAO.deleteInstrument(this.technologySector);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests updating an instrument with valid data.
     */
    @Test
    public void testUpdateValidInstrument() {
        WebServiceResult updateInstrumentResult;
        Instrument updatedInstrument;
        InstrumentService service = new InstrumentService();

        // Update the name.
        this.appleStock.setName("Apple Inc.");
        updateInstrumentResult = service.updateInstrument(this.fixtureHelper.convertToWsInstrument(this.appleStock));

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(updateInstrumentResult));

        // There should be a success message
        assertTrue(updateInstrumentResult.getMessages().size() == 1);
        assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // Retrieve the updated instrument and check if the changes have been persisted.
        try {
            updatedInstrument = instrumentDAO.getInstrument(this.appleStock.getId());
            assertEquals(this.appleStock.getName(), updatedInstrument.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests updating an instrument with invalid data.
     */
    @Test
    public void testUpdateInvalidInstrument() {
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Remove the symbol.
        this.microsoftStock.setSymbol("");
        updateInstrumentResult = service
                .updateInstrument(this.fixtureHelper.convertToWsInstrument(this.microsoftStock));

        // There should be a return message of type E.
        assertTrue(updateInstrumentResult.getMessages().size() == 1);
        assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.symbol.size.message"),
                this.microsoftStock.getSymbol().length(), "1", "6");
        actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    /**
     * Tests updating an instrument without changing any data.
     */
    @Test
    public void testUpdateUnchangedInstrument() {
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Update instrument without changing any data.
        updateInstrumentResult = service
                .updateInstrument(this.fixtureHelper.convertToWsInstrument(this.microsoftStock));

        // There should be a return message of type I
        assertTrue(updateInstrumentResult.getMessages().size() == 1);
        assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.I);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.updateUnchanged"),
                this.microsoftStock.getId());
        actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    /**
     * Tests updating an instrument where the update causes a duplicate instrument.
     */
    @Test
    public void testUpdateCreatingDuplicate() {
        Instrument databaseInstrument;
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Change an existing instrument in a way that a duplicate instrument will be created.
        this.microsoftStock.setSymbol("AAPL");

        // Update the instrument at the database via WebService.
        updateInstrumentResult = service
                .updateInstrument(this.fixtureHelper.convertToWsInstrument(this.microsoftStock));

        // There should be a return message of type E.
        assertTrue(updateInstrumentResult.getMessages().size() == 1);
        assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.updateDuplicate"),
                this.appleStock.getSymbol(), this.appleStock.getStockExchange());
        actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);

        // The symbol change should not have been persisted.
        try {
            databaseInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId());
            assertEquals("MSFT", databaseInstrument.getSymbol());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests updating an Instrument with a sector reference. The referenced Instrument is not of type 'SECTOR'.
     */
    @Test
    public void testUpdateInstrumentWithWrongSector() {
        Instrument databaseInstrument;
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Set the sector of the instrument to another instrument of type 'STOCK'.
        this.microsoftStock.setSector(this.appleStock);

        // Update the instrument at the database via WebService.
        updateInstrumentResult = service
                .updateInstrument(this.fixtureHelper.convertToWsInstrument(this.microsoftStock));

        // There should be a return message of type E.
        assertTrue(updateInstrumentResult.getMessages().size() == 1);
        assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = this.resources.getString("instrument.sector.wrongReference");
        actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);

        // The sector change should not have been persisted.
        try {
            databaseInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId());
            assertNull(databaseInstrument.getSector());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests updating an Instrument with an industry group reference. The referenced Instrument is not of type
     * 'INDUSTRY_GROUP'.
     */
    @Test
    public void testUpdateInstrumentWithWrongIndustryGroup() {
        Instrument databaseInstrument;
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Set the industry group of the instrument to another instrument of type 'STOCK'.
        this.microsoftStock.setIndustryGroup(this.appleStock);

        // Update the instrument at the database via WebService.
        updateInstrumentResult = service
                .updateInstrument(this.fixtureHelper.convertToWsInstrument(this.microsoftStock));

        // There should be a return message of type E.
        assertTrue(updateInstrumentResult.getMessages().size() == 1);
        assertTrue(updateInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = this.resources.getString("instrument.ig.wrongReference");
        actualErrorMessage = updateInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);

        // The industry group change should not have been persisted.
        try {
            databaseInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId());
            assertNull(databaseInstrument.getIndustryGroup());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests adding of a new instrument.
     */
    @Test
    public void testAddValidInstrument() {
        Instrument newInstrument = new Instrument();
        Instrument addedInstrument;
        WebServiceResult addInstrumentResult;
        InstrumentService service = new InstrumentService();

        // Define the new instrument.
        newInstrument.setSymbol("AMZN");
        newInstrument.setName("Amazon");
        newInstrument.setStockExchange(StockExchange.NDQ);
        newInstrument.setType(InstrumentType.STOCK);

        // Add the new instrument to the database via WebService
        addInstrumentResult = service.addInstrument(this.fixtureHelper.convertToWsInstrument(newInstrument));

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(addInstrumentResult));

        // There should be a success message
        assertTrue(addInstrumentResult.getMessages().size() == 1);
        assertTrue(addInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // The ID of the newly created instrument should be provided in the data part of the WebService return.
        assertNotNull(addInstrumentResult.getData());
        assertTrue(addInstrumentResult.getData() instanceof Integer);
        newInstrument.setId((Integer) addInstrumentResult.getData());

        // Read the persisted instrument via DAO
        try {
            addedInstrument = instrumentDAO.getInstrument(newInstrument.getId());

            // Check if the instrument read by the DAO equals the instrument inserted using the WebService in each
            // attribute.
            assertEquals(newInstrument, addedInstrument);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Delete the newly added price alert.
            try {
                instrumentDAO.deleteInstrument(newInstrument);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Tests adding of an invalid instrument.
     */
    @Test
    public void testAddInvalidInstrument() {
        Instrument newInstrument = new Instrument();
        WebServiceResult addInstrumentResult;
        InstrumentService service = new InstrumentService();

        // Define the new instrument without a type.
        newInstrument.setSymbol("TSLA");
        newInstrument.setName("Tesla Inc.");
        newInstrument.setStockExchange(StockExchange.NDQ);

        // Add a new instrument to the database via WebService
        addInstrumentResult = service.addInstrument(this.fixtureHelper.convertToWsInstrument(newInstrument));

        // There should be a return message of type E.
        assertTrue(addInstrumentResult.getMessages().size() == 1);
        assertTrue(addInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // The new instrument should not have been persisted
        assertNull(newInstrument.getId());
    }

    /**
     * Tests adding an instrument which already exists (Symbol / Stock Exchange combination has to be distinct).
     */
    @Test
    public void testAddDuplicateInstrument() {
        Instrument newInstrument = new Instrument();
        WebServiceResult addInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessag;
        String expectedErrorMessage;

        // Define the new instrument without a type.
        newInstrument.setSymbol("AAPL");
        newInstrument.setName("Apple Computer");
        newInstrument.setStockExchange(StockExchange.NDQ);
        newInstrument.setType(InstrumentType.STOCK);

        // Add a new instrument to the database via WebService.
        addInstrumentResult = service.addInstrument(this.fixtureHelper.convertToWsInstrument(newInstrument));

        // There should be a return message of type E.
        assertTrue(addInstrumentResult.getMessages().size() == 1);
        assertTrue(addInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.createDuplicate"),
                this.appleStock.getSymbol(), this.appleStock.getStockExchange());
        actualErrorMessag = addInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessag);

        // The new instrument should not have been persisted.
        assertNull(newInstrument.getId());
    }
}
