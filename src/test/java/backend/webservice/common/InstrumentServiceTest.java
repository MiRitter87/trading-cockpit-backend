package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentArray;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.PriceAlert;
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
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * DAO to access List data.
     */
    private static ListDAO listDAO;

    /**
     * DAO to access PriceAlert data.
     */
    private static PriceAlertDAO priceAlertDAO;

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
     * The stock of NVidia.
     */
    private Instrument nvidiaStock;

    /**
     * The stock of Tesla.
     */
    private Instrument teslaStock;

    /**
     * The ratio between Apple and Tesla.
     */
    private Instrument appleTeslaRatio;

    /**
     * A Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation1;

    /**
     * A List of instruments.
     */
    private backend.model.list.List list;

    /**
     * A PriceAlert for the Apple stock.
     */
    private PriceAlert nvidiaAlert;

    /**
     * The technology sector.
     */
    private Instrument technologySector;

    /**
     * Copper Miners Industry Group.
     */
    private Instrument copperIndustryGroup;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
        listDAO = DAOManager.getInstance().getListDAO();
        priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
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
        this.fixtureHelper = new InstrumentServiceFixture();
        this.createDummyInstruments();
        this.createDummyQuotations();
        this.createDummyLists();
        this.createDummyPriceAlerts();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteDummyPriceAlerts();
        this.deleteDummyLists();
        this.deleteDummyQuotations();
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
        this.nvidiaStock = this.fixtureHelper.getNvidiaStock();
        this.teslaStock = this.fixtureHelper.getTeslaStock();
        this.appleTeslaRatio = this.fixtureHelper.getAppleTeslaRatio(this.appleStock, this.teslaStock);

        try {
            instrumentDAO.insertInstrument(this.technologySector);
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
            instrumentDAO.insertInstrument(this.appleStock);
            instrumentDAO.insertInstrument(this.microsoftStock);
            instrumentDAO.insertInstrument(this.nvidiaStock);
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
            instrumentDAO.deleteInstrument(this.nvidiaStock);
            instrumentDAO.deleteInstrument(this.microsoftStock);
            instrumentDAO.deleteInstrument(this.appleStock);
            instrumentDAO.deleteInstrument(this.copperIndustryGroup);
            instrumentDAO.deleteInstrument(this.technologySector);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy quotations.
     */
    private void createDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();
        this.microsoftQuotation1 = this.fixtureHelper.getMicrosoftQuotation(this.microsoftStock);
        quotations.add(this.microsoftQuotation1);

        try {
            quotationDAO.insertQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy quotations from the database.
     */
    private void deleteDummyQuotations() {
        List<Quotation> quotations = new ArrayList<>();
        quotations.add(this.microsoftQuotation1);

        try {
            quotationDAO.deleteQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy lists.
     */
    private void createDummyLists() {
        this.list = this.fixtureHelper.getList();
        this.list.addInstrument(this.appleStock);

        try {
            listDAO.insertList(this.list);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy lists from the database.
     */
    private void deleteDummyLists() {
        try {
            listDAO.deleteList(this.list);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy price alerts.
     */
    private void createDummyPriceAlerts() {
        this.nvidiaAlert = this.fixtureHelper.getNvidiaAlert(this.nvidiaStock);

        try {
            priceAlertDAO.insertPriceAlert(this.nvidiaAlert);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy price alerts from the database.
     */
    private void deleteDummyPriceAlerts() {
        try {
            priceAlertDAO.deletePriceAlert(this.nvidiaAlert);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of an Instrument.
     */
    public void testGetInstrument() {
        WebServiceResult getInstrumentResult;
        Instrument instrument;

        // Get the instrument.
        InstrumentService service = new InstrumentService();
        getInstrumentResult = service.getInstrument(this.appleStock.getId());

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentResult) == false);

        // Assure that an instrument is returned
        assertTrue(getInstrumentResult.getData() instanceof Instrument);

        instrument = (Instrument) getInstrumentResult.getData();

        // Check each attribute of the instrument.
        assertEquals(this.appleStock, instrument);
    }

    @Test
    /**
     * Tests the retrieval of an Instrument that constitutes a ratio.
     */
    public void testGetInstrumentRatio() {
        WebServiceResult getInstrumentResult;
        Instrument instrument;

        // Get the instrument.
        InstrumentService service = new InstrumentService();
        getInstrumentResult = service.getInstrument(this.appleTeslaRatio.getId());

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentResult) == false);

        // Assure that an instrument is returned
        assertTrue(getInstrumentResult.getData() instanceof Instrument);

        instrument = (Instrument) getInstrumentResult.getData();

        // Check each attribute of the instrument.
        assertEquals(this.appleTeslaRatio, instrument);
    }

    @Test
    /**
     * Tests the retrieval of an instrument with an id that is unknown.
     */
    public void testGetInstrumentWithUnknownId() {
        WebServiceResult getInstrumentResult;
        final Integer unknownInstrumentId = 0;
        String expectedErrorMessage, actualErrorMessage;

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

    @Test
    /**
     * Tests the retrieval of all instruments without quotations.
     */
    public void testGetAllInstrumentsWithoutQuotations() {
        WebServiceResult getInstrumentsResult;
        InstrumentArray instruments;
        Instrument instrument;

        // Get the instruments.
        InstrumentService service = new InstrumentService();
        getInstrumentsResult = service.getInstruments(null);
        instruments = (InstrumentArray) getInstrumentsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult) == false);

        // Check if all instruments are returned.
        assertEquals(7, instruments.getInstruments().size());

        // Check all instruments by each attribute.
        instrument = instruments.getInstruments().get(0);
        assertEquals(this.technologySector, instrument);

        instrument = instruments.getInstruments().get(1);
        assertEquals(this.copperIndustryGroup, instrument);

        instrument = instruments.getInstruments().get(2);
        assertEquals(this.appleStock, instrument);

        instrument = instruments.getInstruments().get(3);
        assertEquals(this.microsoftStock, instrument);

        instrument = instruments.getInstruments().get(4);
        assertEquals(this.nvidiaStock, instrument);

        instrument = instruments.getInstruments().get(5);
        assertEquals(this.teslaStock, instrument);

        instrument = instruments.getInstruments().get(6);
        assertEquals(this.appleTeslaRatio, instrument);
    }

    @Test
    /**
     * Tests the retrieval of all instruments of type industry group.
     */
    public void testGetInstrumentsTypeIndustryGroup() {
        WebServiceResult getInstrumentsResult;
        InstrumentArray instruments;
        Instrument instrument;

        // Get the instruments.
        InstrumentService service = new InstrumentService();
        getInstrumentsResult = service.getInstruments(InstrumentType.IND_GROUP);
        instruments = (InstrumentArray) getInstrumentsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult) == false);

        // Check if one Instrument is returned.
        assertEquals(1, instruments.getInstruments().size());

        // Check the Instrument by each attribute.
        instrument = instruments.getInstruments().get(0);
        assertEquals(this.copperIndustryGroup, instrument);
    }

    @Test
    /**
     * Tests the retrieval of all instruments of type ratio. The test assures that dividend and divisor instances are
     * loaded.
     */
    public void testGetIntrumentsTypeRatio() {
        WebServiceResult getInstrumentsResult;
        InstrumentArray instruments;
        Instrument instrument;

        // Get the instruments.
        InstrumentService service = new InstrumentService();
        getInstrumentsResult = service.getInstruments(InstrumentType.RATIO);
        instruments = (InstrumentArray) getInstrumentsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getInstrumentsResult) == false);

        // Check if one Instrument is returned.
        assertEquals(1, instruments.getInstruments().size());

        // Check the Instrument by each attribute.
        instrument = instruments.getInstruments().get(0);
        assertEquals(this.appleTeslaRatio, instrument);
    }

    @Test
    /**
     * Tests deletion of an instrument.
     */
    public void testDeleteInstrument() {
        WebServiceResult deleteInstrumentResult;
        Instrument deletedInstrument;
        Quotation databaseQuotation;

        try {
            // Delete Microsoft Instrument using the service.
            InstrumentService service = new InstrumentService();
            deleteInstrumentResult = service.deleteInstrument(this.microsoftStock.getId());

            // There should be no error messages
            assertTrue(WebServiceTools.resultContainsErrorMessage(deleteInstrumentResult) == false);

            // There should be a success message
            assertTrue(deleteInstrumentResult.getMessages().size() == 1);
            assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // Check if Microsoft Instrument is missing using the DAO.
            deletedInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId());

            if (deletedInstrument != null)
                fail("Microsoft instrument is still persisted but should have been deleted by the WebService operation 'deleteInstrument'.");

            // The Quotation of the Microsoft stock should have been deleted too.
            databaseQuotation = quotationDAO.getQuotation(this.microsoftQuotation1.getId());
            if (databaseQuotation != null)
                fail("Microsoft quotation is still persisted but should have been deleted by the WebService operation 'deleteInstrument'.");
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Restore old database state by adding the instrument that has been deleted previously.
            try {
                this.microsoftStock = this.fixtureHelper.getMicrosoftStock();
                instrumentDAO.insertInstrument(this.microsoftStock);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests deletion of an instrument with an unknown ID.
     */
    public void testDeleteInstrumentWithUnknownId() {
        WebServiceResult deleteInstrumentResult;
        final Integer unknownInstrumentId = 0;
        String expectedErrorMessage, actualErrorMessage;

        // Delete the instrument.
        InstrumentService service = new InstrumentService();
        deleteInstrumentResult = service.deleteInstrument(unknownInstrumentId);

        // There should be a return message of type E.
        assertTrue(deleteInstrumentResult.getMessages().size() == 1);
        assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.notFound"),
                unknownInstrumentId);
        actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests deletion of an Instrument that is used in a List.
     */
    public void testDeleteInstrumentUsedInList() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage, actualErrorMessage;

        // Delete the instrument.
        InstrumentService service = new InstrumentService();
        deleteInstrumentResult = service.deleteInstrument(this.appleStock.getId());

        // There should be a return message of type E.
        assertTrue(deleteInstrumentResult.getMessages().size() == 1);
        assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.deleteUsedInList"),
                this.appleStock.getId(), this.list.getId());
        actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests deletion of an Instrument that is used in a PriceAlert.
     */
    public void testDeleteInstrumentUsedInPriceAlert() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage, actualErrorMessage;

        // Delete the instrument.
        InstrumentService service = new InstrumentService();
        deleteInstrumentResult = service.deleteInstrument(this.nvidiaStock.getId());

        // There should be a return message of type E.
        assertTrue(deleteInstrumentResult.getMessages().size() == 1);
        assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.deleteUsedInPriceAlert"),
                this.nvidiaStock.getId(), this.nvidiaAlert.getId());
        actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests deletion of an Instrument that is used as sector of another Instrument.
     */
    public void testDeleteInstrumentUsedAsSector() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage, actualErrorMessage;
        InstrumentService service = new InstrumentService();

        try {
            // Try to delete the sector.
            deleteInstrumentResult = service.deleteInstrument(this.technologySector.getId());

            // There should be a return message of type E.
            assertTrue(deleteInstrumentResult.getMessages().size() == 1);
            assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

            // Verify the expected error message.
            expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.deleteUsedInInstrument"),
                    this.technologySector.getId(), this.appleStock.getId());
            actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
            assertEquals(expectedErrorMessage, actualErrorMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Remove the sector reference in order to allow deletion in tearDown method.
            try {
                this.appleStock.setSector(null);
                instrumentDAO.updateInstrument(this.appleStock);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests deletion of an Instrument that is used as industry group of another Instrument.
     */
    public void testDeleteInstrumentUsedAsIndustryGroup() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage, actualErrorMessage;
        InstrumentService service = new InstrumentService();

        try {
            // Try to delete the industry group.
            deleteInstrumentResult = service.deleteInstrument(this.copperIndustryGroup.getId());

            // There should be a return message of type E.
            assertTrue(deleteInstrumentResult.getMessages().size() == 1);
            assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

            // Verify the expected error message.
            expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.deleteUsedInInstrument"),
                    this.copperIndustryGroup.getId(), this.appleStock.getId());
            actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
            assertEquals(expectedErrorMessage, actualErrorMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Remove the sector reference in order to allow deletion in tearDown method.
            try {
                this.appleStock.setIndustryGroup(null);
                instrumentDAO.updateInstrument(this.appleStock);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests deletion of an Instrument that is used as dividend or divisor of another Instrument that constitutes a
     * ratio.
     */
    public void testDeleteInstrumentUsedInRatio() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage, actualErrorMessage;
        InstrumentService service = new InstrumentService();

        try {
            // Try to delete Tesla stock that is used as divisor of a ratio.
            deleteInstrumentResult = service.deleteInstrument(this.teslaStock.getId());

            // There should be a return message of type E.
            assertTrue(deleteInstrumentResult.getMessages().size() == 1);
            assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.E);

            // Verify the expected error message.
            expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.deleteUsedInRatio"),
                    this.teslaStock.getId(), this.appleTeslaRatio.getId());
            actualErrorMessage = deleteInstrumentResult.getMessages().get(0).getText();
            assertEquals(expectedErrorMessage, actualErrorMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests updating an instrument with valid data.
     */
    public void testUpdateValidInstrument() {
        WebServiceResult updateInstrumentResult;
        Instrument updatedInstrument;
        InstrumentService service = new InstrumentService();

        // Update the name.
        this.appleStock.setName("Apple Inc.");
        updateInstrumentResult = service.updateInstrument(this.fixtureHelper.convertToWsInstrument(this.appleStock));

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(updateInstrumentResult) == false);

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

    @Test
    /**
     * Tests updating an instrument with invalid data.
     */
    public void testUpdateInvalidInstrument() {
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage, expectedErrorMessage;

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

    @Test
    /**
     * Tests updating an instrument without changing any data.
     */
    public void testUpdateUnchangedInstrument() {
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage, expectedErrorMessage;

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

    @Test
    /**
     * Tests updating an instrument where the update causes a duplicate instrument.
     */
    public void testUpdateCreatingDuplicate() {
        Instrument databaseInstrument;
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage, expectedErrorMessage;

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

    @Test
    /**
     * Tests updating an Instrument with a sector reference. The referenced Instrument is not of type 'SECTOR'.
     */
    public void testUpdateInstrumentWithWrongSector() {
        Instrument databaseInstrument;
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage, expectedErrorMessage;

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

    @Test
    /**
     * Tests updating an Instrument with an industry group reference. The referenced Instrument is not of type
     * 'INDUSTRY_GROUP'.
     */
    public void testUpdateInstrumentWithWrongIndustryGroup() {
        Instrument databaseInstrument;
        WebServiceResult updateInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage, expectedErrorMessage;

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

    @Test
    /**
     * Tests adding of a new instrument.
     */
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
        assertTrue(WebServiceTools.resultContainsErrorMessage(addInstrumentResult) == false);

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

    @Test
    /**
     * Tests adding of an invalid instrument.
     */
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

    @Test
    /**
     * Tests adding an instrument which already exists (Symbol / Stock Exchange combination has to be distinct).
     */
    public void testAddDuplicateInstrument() {
        Instrument newInstrument = new Instrument();
        WebServiceResult addInstrumentResult;
        InstrumentService service = new InstrumentService();
        String actualErrorMessage, expectedErrorMessage;

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
        actualErrorMessage = addInstrumentResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);

        // The new instrument should not have been persisted.
        assertNull(newInstrument.getId());
    }
}
