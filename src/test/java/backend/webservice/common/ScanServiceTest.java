package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import backend.dao.list.ListDAO;
import backend.dao.scan.ScanDAO;
import backend.model.instrument.Instrument;
import backend.model.list.List;
import backend.model.scan.Scan;
import backend.model.scan.ScanArray;
import backend.model.scan.ScanExecutionStatus;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the scan service.
 *
 * @author Michael
 */
public class ScanServiceTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * DAO to access instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access list data.
     */
    private static ListDAO listDAO;

    /**
     * DAO to access scan data.
     */
    private static ScanDAO scanDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private ScanServiceFixture fixtureHelper;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

    /**
     * The stock of Amazon.
     */
    private Instrument amazonStock;

    /**
     * A list containing a single instrument.
     */
    private List singleInstrumentList;

    /**
     * A list containing multiple instruments.
     */
    private List multiInstrumentList;

    /**
     * A scan containing a single list.
     */
    private Scan singleListScan;

    /**
     * A scan containing multiple lists.
     */
    private Scan multiListScan;

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
    public void setUp() {
        this.fixtureHelper = new ScanServiceFixture();
        this.createDummyInstruments();
        this.createDummyLists();
        this.createDummyScans();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
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

        try {
            instrumentDAO.insertInstrument(this.microsoftStock);
            instrumentDAO.insertInstrument(this.amazonStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
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
        this.singleListScan = this.fixtureHelper.getSingleListScan(this.singleInstrumentList);

        this.multiListScan = this.fixtureHelper.getMultipleListsScan();
        this.multiListScan.addList(this.singleInstrumentList);
        this.multiListScan.addList(this.multiInstrumentList);

        try {
            scanDAO.insertScan(this.singleListScan);
            scanDAO.insertScan(this.multiListScan);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy scans from the database.
     */
    private void deleteDummyScans() {
        try {
            scanDAO.deleteScan(this.multiListScan);
            scanDAO.deleteScan(this.singleListScan);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of a scan.
     */
    public void testGetScan() {
        WebServiceResult getScanResult;
        Scan scan;

        // Get the scan.
        ScanService service = new ScanService();
        getScanResult = service.getScan(this.multiListScan.getId());

        // Assure no error message exists.
        assertTrue(WebServiceTools.resultContainsErrorMessage(getScanResult) == false);

        // Assure that a scan is returned.
        assertTrue(getScanResult.getData() instanceof Scan);

        scan = (Scan) getScanResult.getData();

        // Check each attribute of the scan.
        assertEquals(this.multiListScan, scan);
    }

    @Test
    /**
     * Tests the retrieval of a scan with an id that is unknown.
     */
    public void testGetScanWithUnknownId() {
        WebServiceResult getScanResult;
        final Integer unknownScanId = 0;
        String expectedErrorMessage, actualErrorMessage;

        // Get the scan.
        ScanService service = new ScanService();
        getScanResult = service.getScan(unknownScanId);

        // Assure that no scan is returned
        assertNull(getScanResult.getData());

        // There should be a return message of type E.
        assertTrue(getScanResult.getMessages().size() == 1);
        assertTrue(getScanResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("scan.notFound"), unknownScanId);
        actualErrorMessage = getScanResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests the retrieval of all scans.
     */
    public void testGetAllScans() {
        WebServiceResult getScansResult;
        ScanArray scans;
        Scan scan;

        // Get the scans.
        ScanService service = new ScanService();
        getScansResult = service.getScans();
        scans = (ScanArray) getScansResult.getData();

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(getScansResult) == false);

        // Check if two scans are returned.
        assertEquals(2, scans.getScans().size());

        // Check all scans by each attribute.
        // First scan
        scan = scans.getScans().get(0);
        assertEquals(this.singleListScan, scan);

        // Second scan
        scan = scans.getScans().get(1);
        assertEquals(this.multiListScan, scan);
    }

    @Test
    /**
     * Tests deletion of a scan.
     */
    public void testDeleteScan() {
        WebServiceResult deleteScanResult;
        Scan deletedScan;

        try {
            // Delete scan using the service.
            ScanService service = new ScanService();
            deleteScanResult = service.deleteScan(this.singleListScan.getId());

            // There should be no error messages
            assertTrue(WebServiceTools.resultContainsErrorMessage(deleteScanResult) == false);

            // There should be a success message
            assertTrue(deleteScanResult.getMessages().size() == 1);
            assertTrue(deleteScanResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // Check if the scan is missing using the DAO.
            deletedScan = scanDAO.getScan(this.singleListScan.getId());

            if (deletedScan != null)
                fail("The single list scan is still persisted but should have been deleted by the WebService operation 'deleteScan'.");
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Restore old database state by adding the scan that has been deleted previously.
            try {
                this.singleListScan = this.fixtureHelper.getSingleListScan(this.singleInstrumentList);
                scanDAO.insertScan(this.singleListScan);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests deletion of a scan with an unknown ID.
     */
    public void testDeleteScanWithUnknownId() {
        WebServiceResult deleteScanResult;
        final Integer unknownScanId = 0;
        String expectedErrorMessage, actualErrorMessage;

        // Delete the scan.
        ScanService service = new ScanService();
        deleteScanResult = service.deleteScan(unknownScanId);

        // There should be a return message of type E.
        assertTrue(deleteScanResult.getMessages().size() == 1);
        assertTrue(deleteScanResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // Verify the expected error message.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("scan.notFound"), unknownScanId);
        actualErrorMessage = deleteScanResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests updating a scan with valid data.
     */
    public void testUpdateValidScan() {
        WebServiceResult updateScanResult;
        Scan updatedScan;
        ScanService service = new ScanService();

        // Update the name.
        this.singleListScan.setName("Single list - Updated name");
        updateScanResult = service.updateScan(this.fixtureHelper.convertToWsScan(this.singleListScan));

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(updateScanResult) == false);

        // There should be a success message
        assertTrue(updateScanResult.getMessages().size() == 1);
        assertTrue(updateScanResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // Retrieve the updated scan and check if the changes have been persisted.
        try {
            updatedScan = scanDAO.getScan(this.singleListScan.getId());
            assertEquals(this.singleListScan.getName(), updatedScan.getName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests updating a scan with invalid data.
     */
    public void testUpdateInvalidScan() {
        WebServiceResult updateScanResult;
        ScanService service = new ScanService();
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        String actualErrorMessage, expectedErrorMessage;

        // No name is given.
        this.singleListScan.setName(null);
        updateScanResult = service.updateScan(this.fixtureHelper.convertToWsScan(this.singleListScan));

        // There should be a return message of type E.
        assertTrue(updateScanResult.getMessages().size() == 1);
        assertTrue(updateScanResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // A proper message should be provided.
        expectedErrorMessage = messageProvider.getNotNullValidationMessage("scan", "name");
        actualErrorMessage = updateScanResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests updating a scan without changing any data.
     */
    public void testUpdateUnchangedScan() {
        WebServiceResult updateScanResult;
        ScanService service = new ScanService();
        String actualErrorMessage, expectedErrorMessage;

        // Update scan without changing any data.
        updateScanResult = service.updateScan(this.fixtureHelper.convertToWsScan(this.singleListScan));

        // There should be a return message of type I
        assertTrue(updateScanResult.getMessages().size() == 1);
        assertTrue(updateScanResult.getMessages().get(0).getType() == WebServiceMessageType.I);

        // A proper message should be provided.
        expectedErrorMessage = MessageFormat.format(this.resources.getString("scan.updateUnchanged"),
                this.singleListScan.getId());
        actualErrorMessage = updateScanResult.getMessages().get(0).getText();
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    /**
     * Tests changing a scan to execution status "IN_PROGRESS" while another scan is already in execution status
     * "IN_PROGRESS". Only one scan at a time can be in execution status "IN_PROGRESS".
     */
    public void testUpdateScanMultipleInProgress() {
        WebServiceResult updateScanResult;
        ScanService service = new ScanService();
        String actualErrorMessage, expectedErrorMessage;

        try {
            // Set one scan to execution status "IN_PROGRESS".
            this.singleListScan.setExecutionStatus(ScanExecutionStatus.IN_PROGRESS);
            scanDAO.updateScan(this.singleListScan);

            // Try to set a second scan to execution status "IN_PROGRESS".
            this.multiListScan.setExecutionStatus(ScanExecutionStatus.IN_PROGRESS);
            updateScanResult = service.updateScan(this.fixtureHelper.convertToWsScan(multiListScan));

            // There should be a return message of type E.
            assertTrue(updateScanResult.getMessages().size() == 1);
            assertTrue(updateScanResult.getMessages().get(0).getType() == WebServiceMessageType.I);

            // A proper message should be provided.
            expectedErrorMessage = MessageFormat.format(this.resources.getString("scan.updateScansInProgressExist"),
                    this.singleListScan.getId());
            actualErrorMessage = updateScanResult.getMessages().get(0).getText();
            assertEquals(expectedErrorMessage, actualErrorMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // The second scan should not have been set to execution status "IN_PROGRESS".
    }

    @Test
    /**
     * Tests removing a List from a Scan.
     */
    public void testUpdateScanRemoveList() {
        WebServiceResult updateScanResult;
        ScanService service = new ScanService();
        Scan updatedScan;

        // Remove List from Scan.
        this.multiListScan.getLists().remove(this.multiInstrumentList);
        updateScanResult = service.updateScan(this.fixtureHelper.convertToWsScan(this.multiListScan));

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(updateScanResult) == false);

        // There should be a success message
        assertTrue(updateScanResult.getMessages().size() == 1);
        assertTrue(updateScanResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // Retrieve the updated scan and check if the changes have been persisted.
        try {
            updatedScan = scanDAO.getScan(this.multiListScan.getId());
            assertEquals(this.multiListScan.getLists().size(), updatedScan.getLists().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests updating of a Scan that has multiple lists defined. The Scan also has incomplete instruments. A List is
     * removed that contains an Instrument with incomplete data. The incompleteInstruments entry of the Scan has to be
     * removed automatically on List removal.
     */
    public void testUpdateScanRemoveListIncompleteInstrument() {
        WebServiceResult updateScanResult;
        ScanService service = new ScanService();
        Scan databaseScan;

        try {
            assertEquals(0, this.multiListScan.getIncompleteInstruments().size());

            // At first add incomplete Instrument.
            this.multiListScan.addIncompleteInstrument(this.microsoftStock);
            scanDAO.updateScan(this.multiListScan);

            // Assure that incomplete Instrument is persisted.
            databaseScan = scanDAO.getScan(this.multiListScan.getId());
            assertEquals(1, databaseScan.getIncompleteInstruments().size());

            // Remove List from Scan that contains the incomplete Instrument.
            this.multiListScan.getLists().remove(this.multiInstrumentList);
            updateScanResult = service.updateScan(this.fixtureHelper.convertToWsScan(this.multiListScan));

            // Assure no error message exists
            assertTrue(WebServiceTools.resultContainsErrorMessage(updateScanResult) == false);

            // There should be a success message
            assertTrue(updateScanResult.getMessages().size() == 1);
            assertTrue(updateScanResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // The incomplete Instrument entry of the Scan should have been removed after the List containing the
            // incomplete Instrument has been removed.
            databaseScan = scanDAO.getScan(this.multiListScan.getId());
            assertEquals(this.multiListScan.getLists().size(), databaseScan.getLists().size());
            assertEquals(0, databaseScan.getIncompleteInstruments().size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests adding of a new scan.
     */
    public void testAddValidScan() {
        Scan newScan = new Scan();
        Scan addedScan;
        WebServiceResult addScanResult;
        ScanService service = new ScanService();

        // Define the new Scan.
        newScan.setName("New Scan");
        newScan.setDescription("A new scan with a single list.");
        newScan.addList(this.singleInstrumentList);

        // Add the new scan to the database via WebService
        addScanResult = service.addScan(this.fixtureHelper.convertToWsScan(newScan));

        // Assure no error message exists
        assertTrue(WebServiceTools.resultContainsErrorMessage(addScanResult) == false);

        // There should be a success message
        assertTrue(addScanResult.getMessages().size() == 1);
        assertTrue(addScanResult.getMessages().get(0).getType() == WebServiceMessageType.S);

        // The ID of the newly created scan should be provided in the data part of the WebService return.
        assertNotNull(addScanResult.getData());
        assertTrue(addScanResult.getData() instanceof Integer);
        newScan.setId((Integer) addScanResult.getData());

        // Read the persisted scan via DAO
        try {
            addedScan = scanDAO.getScan(newScan.getId());

            // Check if the scan read by the DAO equals the scan inserted using the WebService in each attribute.
            assertEquals(newScan, addedScan);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Delete the newly added scan.
            try {
                scanDAO.deleteScan(newScan);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests adding of an invalid scan.
     */
    public void testAddInvalidScan() {
        Scan newScan = new Scan();
        WebServiceResult addScanResult;
        ScanService service = new ScanService();

        // Define the new scan without a list.
        newScan.setName("New Scan");
        newScan.setDescription("A new scan without a list.");

        // Add a new scan to the database via WebService
        addScanResult = service.addScan(this.fixtureHelper.convertToWsScan(newScan));

        // There should be a return message of type E.
        assertTrue(addScanResult.getMessages().size() == 1);
        assertTrue(addScanResult.getMessages().get(0).getType() == WebServiceMessageType.E);

        // The new scan should not have been persisted
        assertNull(newScan.getId());
    }
}
