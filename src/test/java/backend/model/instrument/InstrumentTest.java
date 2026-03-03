package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests non-validation related methods of the Instrument model.
 *
 * @author Michael
 */
public class InstrumentTest {
    /**
     * Class providing helper methods for fixture.
     */
    private InstrumentFixture fixtureHelper;

    /**
     * The instrument under test.
     */
    private Instrument instrument;

    /**
     * The first Quotation under test.
     */
    private Quotation quotation1;

    /**
     * The second Quotation under test.
     */
    private Quotation quotation2;

    /**
     * The third Quotation under test.
     */
    private Quotation quotation3;


    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.fixtureHelper = new InstrumentFixture();
        this.initializeQuotations();
        this.initializeInstruments();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.instrument = null;

        this.quotation3 = null;
        this.quotation2 = null;
        this.quotation1 = null;

        this.fixtureHelper = null;
    }

    /**
     * Initializes the quotations.
     */
    private void initializeQuotations() {
        this.quotation1 = this.fixtureHelper.getQuotation1();
        this.quotation2 = this.fixtureHelper.getQuotation2();
        this.quotation3 = this.fixtureHelper.getQuotation3();
    }

    /**
     * Initializes the instruments.
     */
    private void initializeInstruments() {
        this.instrument = this.fixtureHelper.getAppleStock();
        this.instrument.addQuotation(this.quotation1);
        this.instrument.addQuotation(this.quotation2);
        this.instrument.addQuotation(this.quotation3);
    }

    /**
     * Tests getting a quotation by a given date. A quotation for the date exists.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetQuotationByDateExisting() {
        Quotation quotation;
        Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set(2022, 07, 26, 15, 30, 0); // Date of quotation1.

        quotation = this.instrument.getQuotationByDate(calendar.getTime());

        assertEquals(this.quotation1, quotation);
    }

    /**
     * Tests getting a quotation by a given date. A quotation for the date does not exist.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetQuotationByDateNotExisting() {
        Quotation quotation;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 07, 25); // No quotation at that date exists.

        quotation = this.instrument.getQuotationByDate(calendar.getTime());

        assertNull(quotation);
    }

    /**
     * Tests getting the quotations of an instrument as a list sorted by date.
     */
    @Test
    public void testGetQuotationsSortedByDate() {
        List<Quotation> sortedQuotations = this.instrument.getQuotationsSortedByDate();
        Quotation quotation;

        assertNotNull(sortedQuotations);
        assertEquals(this.instrument.getQuotations().size(), sortedQuotations.size());

        // Assure correct sorting. Index 0 has to contain the quotation with the most recent date.
        quotation = sortedQuotations.get(0);
        assertEquals(this.quotation2.getDate().getTime(), quotation.getDate().getTime());

        quotation = sortedQuotations.get(1);
        assertEquals(this.quotation3.getDate().getTime(), quotation.getDate().getTime());

        quotation = sortedQuotations.get(2);
        assertEquals(this.quotation1.getDate().getTime(), quotation.getDate().getTime());
    }

    /**
     * Tests the determination of the newest Quotation. That is either the newest Quotation of the given day or the next
     * older one.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testGetNewestQuotation() {
        Quotation actualQuotation;
        Calendar calendar = Calendar.getInstance();

        // Get newest Quotation starting at 27.08.2022
        calendar.clear();
        calendar.set(2022, 07, 27, 0, 0, 0);
        actualQuotation = this.instrument.getNewestQuotation(calendar.getTime());
        assertEquals(this.quotation2, actualQuotation);

        // Get newest Quotation starting at 28.08.2022 (Should get previous days Quotation)
        calendar.clear();
        calendar.set(2022, 07, 28, 0, 0, 0);
        actualQuotation = this.instrument.getNewestQuotation(calendar.getTime());
        assertEquals(this.quotation2, actualQuotation);

        // Get newest Quotation starting at 25.08.2022 (None should be found)
        calendar.clear();
        calendar.set(2022, 07, 25, 0, 0, 0);
        actualQuotation = this.instrument.getNewestQuotation(calendar.getTime());
        assertNull(actualQuotation);
    }
}
