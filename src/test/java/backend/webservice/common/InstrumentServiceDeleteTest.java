package backend.webservice.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.PriceAlert;
import backend.model.webservice.WebServiceMessageType;
import backend.model.webservice.WebServiceResult;
import backend.tools.WebServiceTools;

/**
 * Tests the delete method of the InstrumentService.
 *
 * @author MiRitter87
 */
public class InstrumentServiceDeleteTest {
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
     * A Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation1;

    /**
     * The technology sector.
     */
    private Instrument technologySector;

    /**
     * Copper Miners Industry Group.
     */
    private Instrument copperIndustryGroup;

    /**
     * The ratio between Apple and Tesla.
     */
    private Instrument appleTeslaRatio;

    /**
     * A List of instruments.
     */
    private backend.model.list.List list;

    /**
     * A PriceAlert for the Apple stock.
     */
    private PriceAlert nvidiaAlert;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
        listDAO = DAOManager.getInstance().getListDAO();
        priceAlertDAO = DAOManager.getInstance().getPriceAlertDAO();
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
        this.createDummyLists();
        this.createDummyPriceAlerts();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteDummyPriceAlerts();
        this.deleteDummyLists();
        this.deleteDummyInstruments();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with dummy instruments.
     */
    private void createDummyInstruments() {
        List<Quotation> quotations = new ArrayList<>();

        this.technologySector = this.fixtureHelper.getTechnologySector();
        this.copperIndustryGroup = this.fixtureHelper.getCopperIndustryGroup();
        this.appleStock = this.fixtureHelper.getAppleStock(this.technologySector, this.copperIndustryGroup);
        this.microsoftStock = this.fixtureHelper.getMicrosoftStock();
        this.nvidiaStock = this.fixtureHelper.getNvidiaStock();
        this.teslaStock = this.fixtureHelper.getTeslaStock();
        this.appleTeslaRatio = this.fixtureHelper.getAppleTeslaRatio(this.appleStock, this.teslaStock);

        this.microsoftQuotation1 = this.fixtureHelper.getMicrosoftQuotation(this.microsoftStock);
        quotations.add(this.microsoftQuotation1);

        try {
            instrumentDAO.insertInstrument(this.technologySector);
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
            instrumentDAO.insertInstrument(this.appleStock);
            instrumentDAO.insertInstrument(this.microsoftStock);
            instrumentDAO.insertInstrument(this.nvidiaStock);
            instrumentDAO.insertInstrument(this.teslaStock);
            instrumentDAO.insertInstrument(this.appleTeslaRatio);

            quotationDAO.insertQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy instruments from the database.
     */
    private void deleteDummyInstruments() {
        List<Quotation> quotations = new ArrayList<>();
        quotations.add(this.microsoftQuotation1);

        try {
            quotationDAO.deleteQuotations(quotations);

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

    /**
     * Tests deletion of an instrument.
     */
    @Test
    public void testDeleteInstrument() {
        WebServiceResult deleteInstrumentResult;
        Instrument deletedInstrument;
        Quotation databaseQuotation;

        try {
            // Delete Microsoft Instrument using the service.
            InstrumentService service = new InstrumentService();
            deleteInstrumentResult = service.deleteInstrument(this.microsoftStock.getId());

            // There should be no error messages
            assertFalse(WebServiceTools.resultContainsErrorMessage(deleteInstrumentResult));

            // There should be a success message
            assertTrue(deleteInstrumentResult.getMessages().size() == 1);
            assertTrue(deleteInstrumentResult.getMessages().get(0).getType() == WebServiceMessageType.S);

            // Check if Microsoft Instrument is missing using the DAO.
            deletedInstrument = instrumentDAO.getInstrument(this.microsoftStock.getId());

            if (deletedInstrument != null) {
                fail("Microsoft instrument is still persisted but should have been deleted "
                        + "by the WebService operation 'deleteInstrument'.");
            }

            // The Quotation of the Microsoft stock should have been deleted too.
            databaseQuotation = quotationDAO.getQuotation(this.microsoftQuotation1.getId());
            if (databaseQuotation != null) {
                fail("Microsoft quotation is still persisted but should have been deleted "
                        + "by the WebService operation 'deleteInstrument'.");
            }
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

    /**
     * Tests deletion of an instrument with an unknown ID.
     */
    @Test
    public void testDeleteInstrumentWithUnknownId() {
        WebServiceResult deleteInstrumentResult;
        final Integer unknownInstrumentId = 0;
        String expectedErrorMessage;
        String actualErrorMessage;

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

    /**
     * Tests deletion of an Instrument that is used in a List.
     */
    @Test
    public void testDeleteInstrumentUsedInList() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage;
        String actualErrorMessage;

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

    /**
     * Tests deletion of an Instrument that is used in a PriceAlert.
     */
    @Test
    public void testDeleteInstrumentUsedInPriceAlert() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage;
        String actualErrorMessage;

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

    /**
     * Tests deletion of an Instrument that is used as sector of another Instrument.
     */
    @Test
    public void testDeleteInstrumentUsedAsSector() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage;
        String actualErrorMessage;
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

    /**
     * Tests deletion of an Instrument that is used as industry group of another Instrument.
     */
    @Test
    public void testDeleteInstrumentUsedAsIndustryGroup() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage;
        String actualErrorMessage;
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

    /**
     * Tests deletion of an Instrument that is used as dividend or divisor of another Instrument that constitutes a
     * ratio.
     */
    @Test
    public void testDeleteInstrumentUsedInRatio() {
        WebServiceResult deleteInstrumentResult;
        String expectedErrorMessage;
        String actualErrorMessage;
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
}
