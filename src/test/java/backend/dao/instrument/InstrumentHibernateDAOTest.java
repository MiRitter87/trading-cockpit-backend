package backend.dao.instrument;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.chart.ChartObjectDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.model.Currency;
import backend.model.LocalizedException;
import backend.model.StockExchange;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;

/**
 * Tests the InstrumentHibernateDAO.
 *
 * @author Michael
 */
public class InstrumentHibernateDAOTest {
    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * DAO to access chart object data.
     */
    private static ChartObjectDAO chartObjectDAO;

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
     * A HorizontalLine in a chart.
     */
    private HorizontalLine horizontalLine;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
        chartObjectDAO = DAOManager.getInstance().getChartObjectDAO();
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
        this.createTestData();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteTestData();
    }

    /**
     * Initializes the database with the apple stock and its quotations.
     */
    private void createTestData() {
        Calendar calendar = Calendar.getInstance();

        this.appleStock = new Instrument();
        this.horizontalLine = new HorizontalLine();

        try {
            this.appleStock.setSymbol("AAPL");
            this.appleStock.setName("Apple");
            this.appleStock.setStockExchange(StockExchange.NDQ);
            this.appleStock.setType(InstrumentType.STOCK);
            instrumentDAO.insertInstrument(this.appleStock);

            calendar.setTime(new Date());
            this.appleQuotation1 = new Quotation();
            this.appleQuotation1.setDate(calendar.getTime());
            this.appleQuotation1.setClose(BigDecimal.valueOf(78.54));
            this.appleQuotation1.setCurrency(Currency.USD);
            this.appleQuotation1.setVolume(6784544);
            this.appleQuotation1.setInstrument(this.appleStock);

            calendar.add(Calendar.DAY_OF_YEAR, 1);
            this.appleQuotation2 = new Quotation();
            this.appleQuotation2.setDate(calendar.getTime());
            this.appleQuotation2.setClose(BigDecimal.valueOf(79.14));
            this.appleQuotation2.setCurrency(Currency.USD);
            this.appleQuotation2.setVolume(4584544);
            this.appleQuotation2.setInstrument(this.appleStock);

            this.horizontalLine.setInstrument(this.appleStock);
            this.horizontalLine.setPrice(new BigDecimal("187.50"));
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the apple stock and its quotations from the database.
     */
    private void deleteTestData() {
        try {
            instrumentDAO.deleteInstrument(this.appleStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests deletion of an Instrument. An Instrument can't be deleted as long as quotations are referenced to the
     * Instrument.
     */
    public void testDeleteInstrumentWithReferencedQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        quotations.add(this.appleQuotation1);
        quotations.add(this.appleQuotation2);

        try {
            quotationDAO.insertQuotations(quotations);

            instrumentDAO.deleteInstrument(this.appleStock);
            fail("Delete should have failed because quotations are referenced to the instrument");
        } catch (LocalizedException expected) {
            // All is well.
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            try {
                quotationDAO.deleteQuotations(quotations);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests deletion of an Instrument. An Instrument can't be deleted as long as horizontal lines are referenced to the
     * Instrument.
     */
    public void testDeleteInstrumentWithReferencedHorizontalLines() {
        try {
            chartObjectDAO.insertHorizontalLine(this.horizontalLine);

            instrumentDAO.deleteInstrument(this.appleStock);
            fail("Delete should have failed because a horizontal line is referenced to the instrument");
        } catch (LocalizedException expected) {
            // All is well.
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            try {
                chartObjectDAO.deleteHorizontalLine(this.horizontalLine);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
}
