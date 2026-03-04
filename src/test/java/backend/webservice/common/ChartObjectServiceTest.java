package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;
import backend.dao.instrument.InstrumentDAO;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the ChartObjectService except getter methods.
 *
 * @author Michael
 */
public class ChartObjectServiceTest {
    /**
     * Class providing helper methods for fixture.
     */
    private ChartObjectServiceFixture fixtureHelper;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

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
     * Instrument of Microsoft stock.
     */
    private Instrument microsoftInstrument;

    /**
     * A horizontal line of Apple.
     */
    private HorizontalLine horizontalLine1;

    /**
     * Another horizontal line of Apple.
     */
    private HorizontalLine horizontalLine2;

    /**
     * A horizontal line of Microsoft.
     */
    private HorizontalLine horizontalLine3;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
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
        this.fixtureHelper = new ChartObjectServiceFixture();
        this.createDummyInstruments();
        this.createDummyHorizontalLines();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteDummyHorizontalLines();
        this.deleteDummyInstruments();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with dummy Instruments.
     */
    private void createDummyInstruments() {
        this.appleInstrument = this.fixtureHelper.getAppleInstrument();
        this.microsoftInstrument = this.fixtureHelper.getMicrosoftInstrument();

        try {
            instrumentDAO.insertInstrument(this.appleInstrument);
            instrumentDAO.insertInstrument(this.microsoftInstrument);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy Instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.microsoftInstrument);
            instrumentDAO.deleteInstrument(this.appleInstrument);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy horizontal lines.
     */
    private void createDummyHorizontalLines() {
        this.horizontalLine1 = this.fixtureHelper.getHorizontalLine1(this.appleInstrument);
        this.horizontalLine2 = this.fixtureHelper.getHorizontalLine2(this.appleInstrument);
        this.horizontalLine3 = this.fixtureHelper.getHorizontalLine3(this.microsoftInstrument);

        try {
            chartObjectDAO.insertHorizontalLine(this.horizontalLine1);
            chartObjectDAO.insertHorizontalLine(this.horizontalLine2);
            chartObjectDAO.insertHorizontalLine(this.horizontalLine3);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy horizontal lines from the database.
     */
    private void deleteDummyHorizontalLines() {
        try {
            chartObjectDAO.deleteHorizontalLine(this.horizontalLine3);
            chartObjectDAO.deleteHorizontalLine(this.horizontalLine2);
            chartObjectDAO.deleteHorizontalLine(this.horizontalLine1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests deletion of a HorizontalLine.
     */
    @Test
    public void testDeleteHorizontalLine() {
        WebServiceResult deleteHorizontalLineResult;
        HorizontalLine deletedHorizontalLine;

        try {
            // Delete HorizontalLine of Microsoft stock using the service.
            ChartObjectService service = new ChartObjectService();
            deleteHorizontalLineResult = service.deleteHorizontalLine(this.horizontalLine3.getId());

            // There should be no error messages
            assertFalse(WebServiceTools.resultContainsErrorMessage(deleteHorizontalLineResult));

            // There should be a success message
            assertTrue(deleteHorizontalLineResult.getMessages().size() == 1);
            assertTrue(deleteHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // Check if previously deleted HorizontalLine is missing using the DAO.
            deletedHorizontalLine = chartObjectDAO.getHorizontalLine(this.horizontalLine3.getId());

            if (deletedHorizontalLine != null) {
                fail("Microsoft horizontal line is still persisted but should have been deleted "
                        + "by the WebService operation 'deleteHorizontalLine'.");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Restore old database state by adding the HorizontalLine that has been deleted previously.
            try {
                this.horizontalLine3 = this.fixtureHelper.getHorizontalLine3(this.microsoftInstrument);
                chartObjectDAO.insertHorizontalLine(this.horizontalLine3);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Tests deletion of a HorizontalLine with an unknown ID.
     */
    @Test
    public void testDeleteHorizontalLineUnknownId() {
        WebServiceResult deleteHorizontalLineResult;
        Integer unknownHorizontalLineId = 0;
        String expectedErrorMessage;
        String actualErrorMessage;

        ChartObjectService service = new ChartObjectService();
        deleteHorizontalLineResult = service.deleteHorizontalLine(unknownHorizontalLineId);

        // There should be a return message of type E.
        assertTrue(deleteHorizontalLineResult.getMessages().size() == 1);
        assertTrue(deleteHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("horizontalLine.notFound"),
                unknownHorizontalLineId);
        actualErrorMessage = deleteHorizontalLineResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    /**
     * Tests updating a HorizontalLine with valid data.
     */
    @Test
    public void testUpdateValidHorizontalLine() {
        WebServiceResult updateHorizontalLineResult;
        HorizontalLine updatedHorizontalLine;
        ChartObjectService service = new ChartObjectService();

        // Update the price.
        this.horizontalLine3.setPrice(new BigDecimal("300.00"));
        updateHorizontalLineResult = service
                .updateHorizontalLine(this.fixtureHelper.convertToWsHorizontalLine(this.horizontalLine3));

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(updateHorizontalLineResult));

        // There should be a success message
        assertTrue(updateHorizontalLineResult.getMessages().size() == 1);
        assertTrue(updateHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // Retrieve the updated HorizontalLine and check if the changes have been persisted.
        try {
            updatedHorizontalLine = chartObjectDAO.getHorizontalLine(this.horizontalLine3.getId());
            assertTrue(this.horizontalLine3.getPrice().compareTo(updatedHorizontalLine.getPrice()) == 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests updating a HorizontalLine with invalid data.
     */
    @Test
    public void testUpdateInvalidHorizontalLine() {
        WebServiceResult updateHorizontalLineResult;
        ChartObjectService service = new ChartObjectService();
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Remove the instrument.
        this.horizontalLine3.setInstrument(null);
        updateHorizontalLineResult = service
                .updateHorizontalLine(this.fixtureHelper.convertToWsHorizontalLine(this.horizontalLine3));

        // There should be a return message of type E.
        assertTrue(updateHorizontalLineResult.getMessages().size() == 1);
        assertTrue(updateHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = messageProvider.getNotNullValidationMessage("horizontalLine", "instrument");
        actualErrorMessage = updateHorizontalLineResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    /**
     * Tests updating a HorizontalLine without changing any data.
     */
    @Test
    public void testUpdateUnchangedHorizontalLine() {
        WebServiceResult updateHorizontalLineResult;
        ChartObjectService service = new ChartObjectService();
        String actualErrorMessage;
        String expectedErrorMessage;

        // Update HorizontalLine without changing any data.
        updateHorizontalLineResult = service
                .updateHorizontalLine(this.fixtureHelper.convertToWsHorizontalLine(this.horizontalLine3));

        // There should be a return message of type I
        assertTrue(updateHorizontalLineResult.getMessages().size() == 1);
        assertTrue(updateHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.I);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("horizontalLine.updateUnchanged"),
                this.horizontalLine3.getId());
        actualErrorMessage = updateHorizontalLineResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    /**
     * Tests adding of a new HorizontalLine.
     */
    @Test
    public void testAddValidHorizontalLine() {
        HorizontalLine newHorizontalLine = new HorizontalLine();
        HorizontalLine addedHorizontalLine;
        WebServiceResult addHorizontalLineResult;
        ChartObjectService service = new ChartObjectService();

        // Define the new HorizontalLine.
        newHorizontalLine.setInstrument(this.microsoftInstrument);
        newHorizontalLine.setPrice(new BigDecimal("323.00"));

        // Add a HorizontalLine to the database via WebService
        addHorizontalLineResult = service
                .addHorizontalLine(this.fixtureHelper.convertToWsHorizontalLine(newHorizontalLine));

        // Assure no error message exists
        assertFalse(WebServiceTools.resultContainsErrorMessage(addHorizontalLineResult));

        // There should be a success message
        assertTrue(addHorizontalLineResult.getMessages().size() == 1);
        assertTrue(addHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // The ID of the newly created HorizontalLine should be provided in the data part of the WebService return.
        assertNotNull(addHorizontalLineResult.getData());
        assertTrue(addHorizontalLineResult.getData() instanceof Integer);
        newHorizontalLine.setId((Integer) addHorizontalLineResult.getData());

        // Read the persisted HorizontalLine via DAO
        try {
            addedHorizontalLine = chartObjectDAO.getHorizontalLine(newHorizontalLine.getId());

            // Check if the HorizontalLine read by the DAO equals the HorizontalLine inserted using the WebService in
            // each attribute.
            assertEquals(newHorizontalLine, addedHorizontalLine);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Delete the newly added HorizontalLine.
            try {
                chartObjectDAO.deleteHorizontalLine(newHorizontalLine);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Tests adding of an invalid HorizontalLine.
     */
    @Test
    public void testAddInvalidHorizontalLine() {
        HorizontalLine newHorizontalLine = new HorizontalLine();
        WebServiceResult addHorizontalLineResult;
        ChartObjectService service = new ChartObjectService();

        // Define the new HorizontalLine without an Instrument.
        newHorizontalLine.setPrice(new BigDecimal("323.00"));

        // Add a new HorizontalLine to the database via WebService
        addHorizontalLineResult = service
                .addHorizontalLine(this.fixtureHelper.convertToWsHorizontalLine(newHorizontalLine));

        // There should be a return message of type E.
        assertTrue(addHorizontalLineResult.getMessages().size() == 1);
        assertTrue(addHorizontalLineResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // The new HorizontalLine should not have been persisted
        assertNull(newHorizontalLine.getId());
    }
}
