package backend.dao.quotation.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.model.Currency;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * Tests non-getter methods of the QuotationHibernateDAO.
 *
 * @author Michael
 */
public class QuotationHibernateDAOTest {
    /**
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private QuotationHibernateDAOFixture fixtureHelper;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The first Quotation of the Apple stock.
     */
    private Quotation appleQuotation1;

    /**
     * The second Quotation of the Apple stock.
     */
    private Quotation appleQuotation2;

    /**
     * Indicator of the Second Quotation of the Apple stock.
     */
    private Indicator appleQuotation2Indicator;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
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
        this.fixtureHelper = new QuotationHibernateDAOFixture();
        this.createTestData();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteTestData();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with instruments, quotations and indicators used as test data.
     */
    private void createTestData() {
        this.createInstruments();
        this.createQuotations();
        this.createIndicators();
    }

    /**
     * Creates Instrument data.
     */
    private void createInstruments() {
        this.appleStock = this.fixtureHelper.getAppleStock();

        try {
            instrumentDAO.insertInstrument(this.appleStock);
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates Quotation data.
     */
    private void createQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation1 = this.fixtureHelper.getAppleQuotation1(this.appleStock);
            quotations.add(this.appleQuotation1);

            this.appleQuotation2 = this.fixtureHelper.getAppleQuotation2(this.appleStock);
            quotations.add(this.appleQuotation2);

            quotationDAO.insertQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates Indicator data.
     */
    private void createIndicators() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation2Indicator = this.fixtureHelper.getAppleQuotation2Indicator();
            this.appleQuotation2.setIndicator(this.appleQuotation2Indicator);
            quotations.add(this.appleQuotation2);

            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the instruments, quotations and indicators used as test data from the database.
     */
    private void deleteTestData() {
        try {
            List<Quotation> quotations = new ArrayList<>();

            quotations.add(this.appleQuotation1);
            quotations.add(this.appleQuotation2);

            quotationDAO.deleteQuotations(quotations);
            instrumentDAO.deleteInstrument(this.appleStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests insertion of a Quotation using the 'insertQuotations' method.
     */
    @Test
    public void testInsertQuotations() {
        Calendar calendar = Calendar.getInstance();
        List<Quotation> quotations = new ArrayList<>();
        Quotation newQuotation;
        Quotation databaseQuotation;
        final long volume = 1184234;

        // Define a new Quotation to be added.
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        newQuotation = new Quotation();
        newQuotation.setDate(calendar.getTime());
        newQuotation.setClose(new BigDecimal("78.19"));
        newQuotation.setCurrency(Currency.USD);
        newQuotation.setVolume(volume);
        newQuotation.setInstrument(this.appleStock);
        quotations.add(newQuotation);

        try {
            // Add Quotation to database.
            quotationDAO.insertQuotations(quotations);

            // Check if Quotation has been correctly persisted.
            databaseQuotation = quotationDAO.getQuotation(newQuotation.getId());
            assertEquals(newQuotation.getId(), databaseQuotation.getId());
            assertEquals(newQuotation.getDate().getTime(), databaseQuotation.getDate().getTime());
            assertTrue(newQuotation.getClose().compareTo(databaseQuotation.getClose()) == 0);
            assertEquals(newQuotation.getCurrency(), databaseQuotation.getCurrency());
            assertEquals(newQuotation.getVolume(), databaseQuotation.getVolume());
            assertEquals(newQuotation.getInstrument().getId(), databaseQuotation.getInstrument().getId());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Remove the newly added quotation from the database.
            try {
                quotationDAO.deleteQuotations(quotations);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Tests deletion of a Quotation using the 'deleteQuotations' method.
     */
    @Test
    public void testDeleteQuotations() {
        List<Quotation> quotations = new ArrayList<>();
        Quotation deletedQuotation;

        try {
            // Delete Quotation.
            quotations.add(this.appleQuotation1);
            quotationDAO.deleteQuotations(quotations);

            // Try to get the previously deleted Quotation.
            deletedQuotation = quotationDAO.getQuotation(this.appleQuotation1.getId());

            // Assure the Quotation does not exist anymore.
            assertNull(deletedQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Add the previously deleted quotation back to the database.
            this.appleQuotation1.setId(null);
            quotations.clear();
            quotations.add(this.appleQuotation1);

            try {
                quotationDAO.insertQuotations(quotations);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Tests updating a Quotation adding a new Indicator relation.
     */
    @Test
    public void testUpdateQuotationWithNewIndicator() {
        List<Quotation> updateQuotations = new ArrayList<>();
        Quotation databaseQuotation;
        Indicator newIndicator = new Indicator();
        final int indicatorBaseLength = 3;

        // Define the new Indicator and add relation to existing Quotation.
        newIndicator.setBaseLengthWeeks(indicatorBaseLength);
        this.appleQuotation2.setIndicator(newIndicator);

        try {
            // Persist the new Quotation-Indicator relation.
            updateQuotations.add(this.appleQuotation2);
            quotationDAO.updateQuotations(updateQuotations);

            // Retrieve the updated Quotation from the database and check if the Indicator has been persisted.
            databaseQuotation = quotationDAO.getQuotation(this.appleQuotation2.getId());
            assertNotNull(databaseQuotation);
            assertNotNull(databaseQuotation.getIndicator());
            assertEquals(indicatorBaseLength, databaseQuotation.getIndicator().getBaseLengthWeeks());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            try {
                // Remove the newly created Indicator.
                this.appleQuotation2.setIndicator(null);
                updateQuotations.clear();
                updateQuotations.add(this.appleQuotation2);
                quotationDAO.updateQuotations(updateQuotations);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
}
