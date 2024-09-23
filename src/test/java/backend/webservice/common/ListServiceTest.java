package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.list.ListDAO;
import backend.dao.scan.ScanDAO;
import backend.model.instrument.Instrument;
import backend.model.list.List;
import backend.model.list.ListArray;
import backend.model.scan.Scan;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the list service.
 *
 * @author Michael
 */
public class ListServiceTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access List data.
     */
    private static ListDAO listDAO;

    /**
     * DAO to access Scan data.
     */
    private static ScanDAO scanDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private ListServiceFixture fixtureHelper;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

    /**
     * The stock of Amazon.
     */
    private Instrument amazonStock;

    /**
     * The technology sector.
     */
    private Instrument techSector;

    /**
     * A List containing a single instrument.
     */
    private List singleInstrumentList;

    /**
     * A List containing multiple instruments.
     */
    private List multiInstrumentList;

    /**
     * A Scan.
     */
    private Scan scan;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        listDAO = DAOManager.getInstance().getListDAO();
        scanDAO = DAOManager.getInstance().getScanDAO();
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
        this.fixtureHelper = new ListServiceFixture();
        this.createDummyInstruments();
        this.createDummyLists();
        this.createDummyScans();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteDummyScans();
        this.deleteDummyLists();
        this.deleteDummyInstruments();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        this.microsoftStock = this.fixtureHelper.getMicrosoftStock();
        this.amazonStock = this.fixtureHelper.getAmazonStock();
        this.techSector = this.fixtureHelper.getTechSector();

        try {
            instrumentDAO.insertInstrument(this.microsoftStock);
            instrumentDAO.insertInstrument(this.amazonStock);
            instrumentDAO.insertInstrument(this.techSector);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.techSector);
            instrumentDAO.deleteInstrument(this.amazonStock);
            instrumentDAO.deleteInstrument(this.microsoftStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy lists.
     */
    private void createDummyLists() {
        this.singleInstrumentList = this.fixtureHelper.getSingleInstrumentList(this.amazonStock);

        this.multiInstrumentList = this.fixtureHelper.getMultipleInstrumentList();
        this.multiInstrumentList.addInstrument(this.amazonStock);
        this.multiInstrumentList.addInstrument(this.microsoftStock);

        try {
            listDAO.insertList(this.singleInstrumentList);
            listDAO.insertList(this.multiInstrumentList);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy lists from the database.
     */
    private void deleteDummyLists() {
        try {
            listDAO.deleteList(this.multiInstrumentList);
            listDAO.deleteList(this.singleInstrumentList);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy scans.
     */
    private void createDummyScans() {
        this.scan = this.fixtureHelper.getScan(this.multiInstrumentList);

        try {
            scanDAO.insertScan(this.scan);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy scans from the database.
     */
    private void deleteDummyScans() {
        try {
            scanDAO.deleteScan(this.scan);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of a list.
     */
    public void testGetList() {
        WebServiceResult getListResult;
        List list;
        Instrument instrument;
        Iterator<Instrument> instrumentIterator;

        // Get the list.
        ListService service = new ListService();
        getListResult = service.getList(this.multiInstrumentList.getId());

        // Assure no error message exists.
        assertTrue(WebServiceTools.resultContainsErrorMessage(getListResult) == false);

        // Assure that a list is returned.
        assertTrue(getListResult.getData() instanceof List);

        list = (List) getListResult.getData();

        // Check each attribute of the list.
        assertEquals(this.multiInstrumentList, list);

        // Check the attributes of the instruments.
        instrumentIterator = list.getInstruments().iterator();
        while (instrumentIterator.hasNext()) {
            instrument = instrumentIterator.next();

            if (instrument.getId().equals(this.amazonStock.getId())) {
                assertEquals(this.amazonStock, instrument);
            } else if (instrument.getId().equals(this.microsoftStock.getId())) {
                assertEquals(this.microsoftStock, instrument);
            } else {
                fail("The list contains an unrelated instrument.");
            }
        }
    }

    @Test
    /**
     * Tests the retrieval of a list with an id that is unknown.
     */
    public void testGetListWithUnknownId() {
        WebServiceResult getListResult;
        final Integer unknownListId = 0;
        String expectedErrorMessage, actualErrorMessage;

        // Get the list.
        ListService service = new ListService();
        getListResult = service.getList(unknownListId);

        // Assure that no list is returned
        assertNull(getListResult.getData());

        // There should be a return message of type E.
        assertTrue(getListResult.getMessages().size() == 1);
        assertTrue(getListResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("list.notFound"), unknownListId);
        actualErrorMessage = getListResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests the retrieval of all lists.
     */
    public void testGetAllLists() {
        WebServiceResult getListsResult;
        ListArray lists;
        List list;

        // Get the lists.
        ListService service = new ListService();
        getListsResult = service.getLists();
        lists = (ListArray) getListsResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getListsResult) == false);

        // Check if two lists are returned.
        assertEquals(2, lists.getLists().size());

        // Check all lists by each attribute.
        // First list
        list = lists.getLists().get(0);
        assertEquals(this.singleInstrumentList, list);

        // Second list
        list = lists.getLists().get(1);
        assertEquals(this.multiInstrumentList, list);
    }

    @Test
    /**
     * Tests deletion of a list.
     */
    public void testDeleteList() {
        WebServiceResult deleteListResult;
        List deletedList;

        try {
            // Delete list using the service.
            ListService service = new ListService();
            deleteListResult = service.deleteList(this.singleInstrumentList.getId());

            // There should be no error messages
            assertTrue(WebServiceTools.resultContainsErrorMessage(deleteListResult) == false);

            // There should be a success message
            assertTrue(deleteListResult.getMessages().size() == 1);
            assertTrue(deleteListResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // Check if the list is missing using the DAO.
            deletedList = listDAO.getList(this.singleInstrumentList.getId());

            if (deletedList != null)
                fail("The single instrument list is still persisted but should have been deleted by the WebService operation 'deleteList'.");
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Restore old database state by adding the list that has been deleted previously.
            try {
                this.singleInstrumentList = this.fixtureHelper.getSingleInstrumentList(this.amazonStock);
                listDAO.insertList(this.singleInstrumentList);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests deletion of a list with an unknown ID.
     */
    public void testeDeleteListWithUnknownId() {
        WebServiceResult deleteListResult;
        final Integer unknownListId = 0;
        String expectedErrorMessage, actualErrorMessage;

        // Delete the list.
        ListService service = new ListService();
        deleteListResult = service.deleteList(unknownListId);

        // There should be a return message of type E.
        assertTrue(deleteListResult.getMessages().size() == 1);
        assertTrue(deleteListResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("list.notFound"), unknownListId);
        actualErrorMessage = deleteListResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests deletion of a List that is used by a Scan.
     */
    public void testDeleteListUsedInScan() {
        WebServiceResult deleteListResult;
        String expectedErrorMessage, actualErrorMessage;

        // Delete the List.
        ListService service = new ListService();
        deleteListResult = service.deleteList(this.multiInstrumentList.getId());

        // There should be a return message of type E.
        assertTrue(deleteListResult.getMessages().size() == 1);
        assertTrue(deleteListResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("list.deleteUsedInScan"),
                this.multiInstrumentList.getId(), this.scan.getId());
        actualErrorMessage = deleteListResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests deletion of a List that is used as data source for an Instrument.
     */
    public void testDeleteListUsedAsDataSource() {
        WebServiceResult deleteListResult;
        String expectedErrorMessage, actualErrorMessage;

        try {
            // At first add List relation to Instrument.
            this.techSector.setSymbol(null);
            this.techSector.setStockExchange(null);
            this.techSector.setDataSourceList(this.singleInstrumentList);
            instrumentDAO.updateInstrument(this.techSector);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Delete the List.
        ListService service = new ListService();
        deleteListResult = service.deleteList(this.singleInstrumentList.getId());

        // There should be a return message of type E.
        assertTrue(deleteListResult.getMessages().size() == 1);
        assertTrue(deleteListResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("list.deleteUsedAsDataSource"),
                this.singleInstrumentList.getId(), this.techSector.getId());
        actualErrorMessage = deleteListResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);

        try {
            // Delete list relation to allow deletion of Instrument.
            this.techSector.setDataSourceList(null);
            instrumentDAO.updateInstrument(this.techSector);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests updating a list with valid data.
     */
    public void testUpdateValidList() {
        WebServiceResult updateListResult;
        List updatedList;
        ListService service = new ListService();

        // Update the name.
        this.singleInstrumentList.setName("Single instrument - Updated name");
        updateListResult = service.updateList(this.fixtureHelper.convertToWsList(this.singleInstrumentList));

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(updateListResult) == false);

        // There should be a success message
        assertTrue(updateListResult.getMessages().size() == 1);
        assertTrue(updateListResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // Retrieve the updated list and check if the changes have been persisted.
        try {
            updatedList = listDAO.getList(this.singleInstrumentList.getId());
            assertEquals(this.singleInstrumentList.getName(), updatedList.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests updating a list with invalid data.
     */
    public void testUpdateInvalidList() {
        WebServiceResult updateListResult;
        ListService service = new ListService();
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        String actualErrorMessage, expectedErrorMessage;

        // No name is given.
        this.singleInstrumentList.setName(null);
        updateListResult = service.updateList(this.fixtureHelper.convertToWsList(this.singleInstrumentList));

        // There should be a return message of type E.
        assertTrue(updateListResult.getMessages().size() == 1);
        assertTrue(updateListResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = messageProvider.getNotNullValidationMessage("list", "name");
        actualErrorMessage = updateListResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests updating a list without changing any data.
     */
    public void testUpdateUnchangedList() {
        WebServiceResult updateListResult;
        ListService service = new ListService();
        String actualErrorMessage, expectedErrorMessage;

        // Update list without changing any data.
        updateListResult = service.updateList(this.fixtureHelper.convertToWsList(this.singleInstrumentList));

        // There should be a return message of type I
        assertTrue(updateListResult.getMessages().size() == 1);
        assertTrue(updateListResult.getMessages().get(0).getType() == WebServiceMessageType.I);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("list.updateUnchanged"),
                this.singleInstrumentList.getId());
        actualErrorMessage = updateListResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests adding of a new list.
     */
    public void testAddValidList() {
        List newList = new List();
        List addedList;
        WebServiceResult addListResult;
        ListService service = new ListService();

        // Define the new list.
        newList.setName("New List");
        newList.setDescription("A new list with a single instrument.");
        newList.addInstrument(this.microsoftStock);

        // Add the new list to the database via WebService
        addListResult = service.addList(this.fixtureHelper.convertToWsList(newList));

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(addListResult) == false);

        // There should be a success message
        assertTrue(addListResult.getMessages().size() == 1);
        assertTrue(addListResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // The ID of the newly created list should be provided in the data part of the WebService return.
        assertNotNull(addListResult.getData());
        assertTrue(addListResult.getData() instanceof Integer);
        newList.setId((Integer) addListResult.getData());

        // Read the persisted list via DAO
        try {
            addedList = listDAO.getList(newList.getId());

            // Check if the list read by the DAO equals the list inserted using the WebService in each attribute.
            assertEquals(newList, addedList);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Delete the newly added list.
            try {
                listDAO.deleteList(newList);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests adding of an invalid list.
     */
    public void testAddInvalidList() {
        List newList = new List();
        WebServiceResult addListResult;
        ListService service = new ListService();

        // Define the new list without an instrument.
        newList.setName("New List");
        newList.setDescription("A new list without an instrument.");

        // Add a new list to the database via WebService
        addListResult = service.addList(this.fixtureHelper.convertToWsList(newList));

        // There should be a return message of type E.
        assertTrue(addListResult.getMessages().size() == 1);
        assertTrue(addListResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // The new list should not have been persisted
        assertNull(newList.getId());
    }

    @Test
    /**
     * Tests the removal of a scans incomplete instruments if those instruments are removed from all scan-related lists.
     */
    public void testRemoveIncompleteInstrumentsOnListChange() {
        WebServiceResult updateListResult;
        ListService service = new ListService();
        Scan databaseScan;

        try {
            // Add incomplete Instrument to Scan.
            this.scan.addIncompleteInstrument(this.amazonStock);
            scanDAO.updateScan(this.scan);

            // Assure that the incomplete Instrument is persisted.
            databaseScan = scanDAO.getScan(this.scan.getId());
            assertEquals(1, databaseScan.getIncompleteInstruments().size());

            // Remove Instrument from List used in Scan.
            this.multiInstrumentList.removeInstrument(this.amazonStock);
            updateListResult = service.updateList(this.fixtureHelper.convertToWsList(this.multiInstrumentList));

            // Assure no error message exists
            assertTrue(WebServiceTools.resultContainsErrorMessage(updateListResult) == false);

            // There should be a success message
            assertTrue(updateListResult.getMessages().size() == 1);
            assertTrue(updateListResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // Check if incomplete Instrument has been removed from Scan.
            databaseScan = scanDAO.getScan(this.scan.getId());
            assertEquals(0, databaseScan.getIncompleteInstruments().size());
        } catch (ObjectUnchangedException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the removal of a scans incomplete instruments. If an Instrument is contained in multiple lists of a scan
     * and only removed from one of the lists, the incomplete Instrument of a scan should not be removed.
     */
    public void testDontRemoveIncompleteInstrumentOnListChange() {
        WebServiceResult updateListResult;
        ListService service = new ListService();
        Scan databaseScan;

        try {
            // Add another list and an incomplete Instrument to the Scan.
            this.scan.addList(this.singleInstrumentList);
            this.scan.addIncompleteInstrument(this.amazonStock);
            scanDAO.updateScan(this.scan);

            // Assure that the incomplete Instrument is persisted.
            databaseScan = scanDAO.getScan(this.scan.getId());
            assertEquals(1, databaseScan.getIncompleteInstruments().size());

            // Remove Instrument from List used in Scan.
            this.multiInstrumentList.removeInstrument(this.amazonStock);
            updateListResult = service.updateList(this.fixtureHelper.convertToWsList(this.multiInstrumentList));

            // Assure no error message exists
            assertTrue(WebServiceTools.resultContainsErrorMessage(updateListResult) == false);

            // There should be a success message
            assertTrue(updateListResult.getMessages().size() == 1);
            assertTrue(updateListResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // The incomplete Instrument should not have been removed because it is still part of the Scan via another
            // List.
            databaseScan = scanDAO.getScan(this.scan.getId());
            assertEquals(1, databaseScan.getIncompleteInstruments().size());
        } catch (ObjectUnchangedException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
