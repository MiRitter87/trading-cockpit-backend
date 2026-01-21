package backend.dao.priceAlert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.Currency;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.priceAlert.ConfirmationStatus;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.PriceAlertType;
import backend.model.priceAlert.TriggerStatus;

/**
 * Tests the Hibernate DAO for price alerts.
 *
 * @author Michael
 */
public class PriceAlertHibernateDAOTest {
    /**
     * The DAO to access price alerts.
     */
    private static PriceAlertDAO priceAlertDAO;

    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * A price alert for the Apple stock.
     */
    private PriceAlert appleAlert;

    /**
     * A price alert for the Microsoft stock.
     */
    private PriceAlert microsoftAlert;

    /**
     * A price alert for the NVIDIA stock.
     */
    private PriceAlert nvidiaAlert;

    /**
     * A price alert for the Netflix stock.
     */
    private PriceAlert netflixAlert;

    /**
     * Instrument of Apple stock.
     */
    private Instrument appleInstrument;

    /**
     * Instrument of Microsoft stock.
     */
    private Instrument microsoftInstrument;

    /**
     * Instrument of Netflix stock.
     */
    private Instrument netflixInstrument;

    /**
     * Instrument of NVidia stock.
     */
    private Instrument nvidiaInstrument;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
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
        this.createDummyInstruments();
        this.createDummyPriceAlerts();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteDummyPriceAlerts();
        this.deleteDummyInstruments();
    }

    /**
     * Initializes the database with dummy price alerts.
     */
    private void createDummyPriceAlerts() {
        Calendar lastStockQuote = Calendar.getInstance();
        final int days2 = 2;

        this.appleAlert = new PriceAlert();
        this.appleAlert.setInstrument(this.appleInstrument);
        this.appleAlert.setAlertType(PriceAlertType.GREATER_OR_EQUAL);
        this.appleAlert.setPrice(new BigDecimal("185.50"));
        this.appleAlert.setCurrency(Currency.USD);
        this.appleAlert.setLastStockQuoteTime(null);

        this.microsoftAlert = new PriceAlert();
        this.microsoftAlert.setInstrument(this.microsoftInstrument);
        this.microsoftAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        this.microsoftAlert.setPrice(new BigDecimal("250.00"));
        this.microsoftAlert.setCurrency(Currency.USD);
        lastStockQuote.add(Calendar.MINUTE, -1);
        this.microsoftAlert.setLastStockQuoteTime(lastStockQuote.getTime());

        this.nvidiaAlert = new PriceAlert();
        this.nvidiaAlert.setInstrument(this.nvidiaInstrument);
        this.nvidiaAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        this.nvidiaAlert.setPrice(new BigDecimal("180.00"));
        this.nvidiaAlert.setCurrency(Currency.USD);
        lastStockQuote.add(Calendar.MINUTE, -days2);
        this.nvidiaAlert.setLastStockQuoteTime(lastStockQuote.getTime());

        this.netflixAlert = new PriceAlert();
        this.netflixAlert.setInstrument(this.netflixInstrument);
        this.netflixAlert.setAlertType(PriceAlertType.LESS_OR_EQUAL);
        this.netflixAlert.setPrice(new BigDecimal("199.99"));
        this.netflixAlert.setCurrency(Currency.USD);
        this.netflixAlert.setLastStockQuoteTime(null);
        this.netflixAlert.setTriggerTime(new Date());

        try {
            priceAlertDAO.insertPriceAlert(this.appleAlert);
            priceAlertDAO.insertPriceAlert(this.microsoftAlert);
            priceAlertDAO.insertPriceAlert(this.nvidiaAlert);
            priceAlertDAO.insertPriceAlert(this.netflixAlert);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy price alerts from the database.
     */
    private void deleteDummyPriceAlerts() {
        try {
            priceAlertDAO.deletePriceAlert(this.netflixAlert);
            priceAlertDAO.deletePriceAlert(this.nvidiaAlert);
            priceAlertDAO.deletePriceAlert(this.microsoftAlert);
            priceAlertDAO.deletePriceAlert(this.appleAlert);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Initializes the database with dummy Instruments.
     */
    private void createDummyInstruments() {
        this.appleInstrument = this.getAppleInstrument();
        this.microsoftInstrument = this.getMicrosoftInstrument();
        this.netflixInstrument = this.getNetflixInstrument();
        this.nvidiaInstrument = this.getNvidiaInstrument();

        try {
            instrumentDAO.insertInstrument(this.appleInstrument);
            instrumentDAO.insertInstrument(this.microsoftInstrument);
            instrumentDAO.insertInstrument(this.netflixInstrument);
            instrumentDAO.insertInstrument(this.nvidiaInstrument);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the dummy Instruments from the database.
     */
    private void deleteDummyInstruments() {
        try {
            instrumentDAO.deleteInstrument(this.nvidiaInstrument);
            instrumentDAO.deleteInstrument(this.netflixInstrument);
            instrumentDAO.deleteInstrument(this.microsoftInstrument);
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
    private Instrument getAppleInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("AAPL");
        instrument.setName("Apple");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Microsoft stock.
     *
     * @return The Instrument of the Microsoft stock.
     */
    private Instrument getMicrosoftInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("MSFT");
        instrument.setName("Microsoft");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the Netflix stock.
     *
     * @return The Instrument of the Netflix stock.
     */
    private Instrument getNetflixInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("NFLX");
        instrument.setName("Netflix");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Gets the Instrument of the NVidia stock.
     *
     * @return The Instrument of the NVidia stock.
     */
    private Instrument getNvidiaInstrument() {
        Instrument instrument = new Instrument();

        instrument.setSymbol("NVDA");
        instrument.setName("NVidia");
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setType(InstrumentType.STOCK);

        return instrument;
    }

    /**
     * Tests getting all price alerts that have not been triggered, sorted by lastStockQuoteTime.
     */
    @Test
    public void testGetPriceAlertsNotTriggered() {
        List<PriceAlert> priceAlerts;
        PriceAlert priceAlert;
        final int expectedAlerts = 3;

        try {
            priceAlerts = priceAlertDAO.getPriceAlerts(PriceAlertOrderAttribute.LAST_STOCK_QUOTE_TIME,
                    TriggerStatus.NOT_TRIGGERED, ConfirmationStatus.ALL);

            // 3 price alerts have not been triggered and therefore should be returned.
            assertEquals(expectedAlerts, priceAlerts.size());

            // Assure correct sorting
            priceAlert = priceAlerts.get(0);
            assertEquals(priceAlert.getId(), this.appleAlert.getId());
            priceAlert = priceAlerts.get(1);
            assertEquals(priceAlert.getId(), this.nvidiaAlert.getId());
            priceAlert = priceAlerts.get(2);
            assertEquals(priceAlert.getId(), this.microsoftAlert.getId());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test getting all price alerts of a specific instrument, type, price and TriggerStatus combination.
     */
    @Test
    public void testGetPriceAlertsOfInstrument() {
        List<PriceAlert> priceAlerts;
        PriceAlert priceAlert;

        try {
            priceAlerts = priceAlertDAO.getPriceAlerts(this.appleInstrument.getId(), PriceAlertType.GREATER_OR_EQUAL,
                    new BigDecimal("185.50"));

            // Assure correct amount of alerts.
            assertEquals(1, priceAlerts.size());

            // Assure correct alert is selected.
            priceAlert = priceAlerts.get(0);
            assertEquals(this.appleAlert.getId(), priceAlert.getId());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
