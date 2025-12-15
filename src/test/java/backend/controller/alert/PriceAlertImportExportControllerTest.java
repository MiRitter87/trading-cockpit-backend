package backend.controller.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertOrderAttribute;
import backend.model.Currency;
import backend.model.LocalizedException;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;

/**
 * Test the PriceAlertImportExportController.
 *
 * @author MiRitter87
 */
public class PriceAlertImportExportControllerTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * The controller under test.
     */
    private PriceAlertImportExportController importExportController;

    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access price alert data.
     */
    private static PriceAlertDAO priceAlertDAO;

    /**
     * Instrument of Apple stock.
     */
    private Instrument appleInstrument;

    /**
     * The first Apple PriceAlert.
     */
    private PriceAlert appleAlert1;

    /**
     * The second Apple PriceAlert.
     */
    private PriceAlert appleAlert2;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
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
    public void setUp() {
        this.importExportController = new PriceAlertImportExportController();
        this.createDummyInstruments();
        this.createDummyPriceAlerts();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.deleteDummyPriceAlerts();
        this.deleteDummyInstruments();
        this.importExportController = null;
    }

    /**
     * Initializes the database with dummy Instruments.
     */
    private void createDummyInstruments() {
        this.appleInstrument = this.getAppleInstrument();

        try {
            instrumentDAO.insertInstrument(this.appleInstrument);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy Instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.appleInstrument);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Gets the Instrument of the Apple stock.
     *
     * @return The Instrument of the Apple stock.
     */
    public Instrument getAppleInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Initializes the database with dummy price alerts.
     */
    private void createDummyPriceAlerts() {
        this.appleAlert1 = this.getAppleAlert1();
        this.appleAlert2 = this.getAppleAlert2();

        try {
            priceAlertDAO.insertPriceAlert(this.appleAlert1);
            priceAlertDAO.insertPriceAlert(this.appleAlert2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy price alerts from the database.
     */
    private void deleteDummyPriceAlerts() {
        try {
            priceAlertDAO.deletePriceAlert(this.appleAlert2);
            priceAlertDAO.deletePriceAlert(this.appleAlert1);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Gets the first price alert for the Apple stock.
     *
     * @return A price alert for the Apple stock.
     */
    public PriceAlert getAppleAlert1() {
        PriceAlert alert = new PriceAlert();

        alert.setInstrument(this.appleInstrument);
        alert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(280.70));
        alert.setCurrency(Currency.USD);
        alert.setTriggerTime(new Date());
        alert.setTriggerDistancePercent(0.1f);
        alert.setConfirmationTime(new Date());
        alert.setSendMail(true);
        alert.setAlertMailAddress("test@mail.com");
        alert.setLastStockQuoteTime(new Date());
        alert.setMailTransmissionTime(new Date());

        return alert;
    }

    /**
     * Gets the second price alert for the Apple stock.
     *
     * @return A price alert for the Apple stock.
     */
    public PriceAlert getAppleAlert2() {
        PriceAlert alert = new PriceAlert();

        alert.setInstrument(this.appleInstrument);
        alert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
        alert.setPrice(BigDecimal.valueOf(300));
        alert.setCurrency(Currency.USD);

        return alert;
    }

    /**
     * Gets the Json String for PriceAlert import with the reference to an unknown Instrument.
     *
     * @return The Json String.
     * @throws IOException Failed to read Json String from file.
     */
    private String getJsonOfAlertWithUnknownInstrument() throws IOException {
        String jsonPath = "src/test/resources/PriceAlertImport/priceAlertsUnknownInstrument.json";
        String jsonAlerts = Files.readString(Paths.get(jsonPath));
        String oldIdString = "\"instrument\":{\"id\":33944";
        String newIdString = "\"instrument\":{\"id\":" + this.appleInstrument.getId() + "0";

        // Replace Instrument ID assuring that Instrument does not exist.
        jsonAlerts = jsonAlerts.replace(oldIdString, newIdString);

        return jsonAlerts;
    }

    /**
     * Gets the Json String for PriceAlert import with an alert that already exists.
     *
     * @return The Json String.
     * @throws IOException Failed to read Json String from file.
     */
    private String getJsonOfAlertAlreadyExisting() throws IOException {
        String jsonPath = "src/test/resources/PriceAlertImport/priceAlertsExisting.json";
        String jsonAlerts = Files.readString(Paths.get(jsonPath));
        String oldIdString = "\"instrument\":{\"id\":34547";
        String newIdString = "\"instrument\":{\"id\":" + this.appleInstrument.getId();

        // Replace Instrument ID from file with actual ID from the database.
        jsonAlerts = jsonAlerts.replace(oldIdString, newIdString);

        return jsonAlerts;
    }

    /**
     * Gets the Json String for PriceAlert import with an alert that is valid for import.
     *
     * @return The Json String.
     * @throws IOException Failed to read Json String from file.
     */
    private String getJsonOfAlertForImport() throws IOException {
        String jsonPath = "src/test/resources/PriceAlertImport/priceAlertValidImport.json";
        String jsonAlerts = Files.readString(Paths.get(jsonPath));
        String oldIdString = "\"instrument\":{\"id\":34547";
        String newIdString = "\"instrument\":{\"id\":" + this.appleInstrument.getId();

        // Replace Instrument ID from file with actual ID from the database.
        jsonAlerts = jsonAlerts.replace(oldIdString, newIdString);

        return jsonAlerts;
    }

    @Test
    /**
     * Tests the export of all price alerts.
     */
    public void testExportPriceAlerts() {
        try {
            String jsonAlerts = this.importExportController.exportPriceAlerts();
            List<PriceAlert> deserializedAlerts;
            ObjectMapper mapper = new ObjectMapper();

            // The price alerts are serialized to JSON. The JSON String is then being deserialized back to Java Objects.
            // The deserialized Java objects are then validated against the original objects.
            deserializedAlerts = mapper.readValue(jsonAlerts, new TypeReference<List<PriceAlert>>() {
            });
            assertTrue(deserializedAlerts.size() == 2);

            for (PriceAlert tempAlert : deserializedAlerts) {
                if (tempAlert.getId().equals(this.appleAlert1.getId())) {
                    assertEquals(this.appleAlert1, tempAlert);
                } else if (tempAlert.getId().equals(this.appleAlert2.getId())) {
                    assertEquals(this.appleAlert2, tempAlert);
                } else {
                    fail("Differences occurred during deserialization of price alerts.");
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests importing price alerts from an invalid JSON String.
     */
    public void testImportPriceAlertsInvalidString() {
        String invalidJson = "abcde";
        String expectedErrorMessage = this.resources.getString("priceAlert.importJsonMalformed");

        try {
            this.importExportController.importPriceAlerts(invalidJson);
            fail("Import should have failed because String is malformed.");
        } catch (Exception expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        }
    }

    @Test
    /**
     * Tests importing price alerts from a JSON String containing just an empty list.
     */
    public void testImportPriceAlertsEmptyList() {
        String emptyListJson = "[]";
        String expectedErrorMessage = this.resources.getString("priceAlert.importEmptyList");

        try {
            this.importExportController.importPriceAlerts(emptyListJson);
            fail("Import should have failed because empty list has been given.");
        } catch (LocalizedException e) {
            assertEquals(expectedErrorMessage, e.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the import of price alerts where an Instrument with an unknown ID is being referenced. No Price Alert is
     * being created, if the referenced Instrument is unknown.
     */
    public void testImportPriceAlertsUnknownInstrument() {
        int expectedNumberAlerts = 2;
        List<PriceAlert> priceAlerts;

        try {
            String importJson = this.getJsonOfAlertWithUnknownInstrument();

            this.importExportController.importPriceAlerts(importJson);

            // Verify, that no additional Price Alert was created by the import method.
            priceAlerts = priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.ID, TriggerStatus.ALL,
                    ConfirmationStatus.ALL);
            assertEquals(expectedNumberAlerts, priceAlerts.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Test the import of a valid PriceAlert.
     */
    public void testImportPriceAlertsValidImport() {
        int expectedNumberAlerts = 3;
        List<PriceAlert> priceAlerts = null;
        String importJson;

        try {
            importJson = this.getJsonOfAlertForImport();

            this.importExportController.importPriceAlerts(importJson);

            // Verify, that an additional Price Alert was created by the import method.
            priceAlerts = priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.ID, TriggerStatus.ALL,
                    ConfirmationStatus.ALL);
            assertEquals(expectedNumberAlerts, priceAlerts.size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Delete the imported PriceAlert.
            if (priceAlerts == null) {
                return;
            }

            for (PriceAlert tempAlert : priceAlerts) {
                if (!tempAlert.getId().equals(this.appleAlert1.getId())
                        && !tempAlert.getId().equals(this.appleAlert2.getId())) {
                    try {
                        priceAlertDAO.deletePriceAlert(tempAlert);
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                }
            }
        }
    }

    // @Test Activate this test after import has been implemented.
    /**
     * Tests the import of price alerts that already exist.
     */
    public void testImportPriceAlertsExisting() {
        int expectedNumberAlerts = 2;
        List<PriceAlert> priceAlerts;

        try {
            String importJson = this.getJsonOfAlertAlreadyExisting();

            this.importExportController.importPriceAlerts(importJson);

            // Verify, that no additional Price Alert was created by the import method.
            priceAlerts = priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.ID, TriggerStatus.ALL,
                    ConfirmationStatus.ALL);
            assertEquals(expectedNumberAlerts, priceAlerts.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * TODO Implement necessary tests
     *
     * -import price alerts that already exist
     */
}
