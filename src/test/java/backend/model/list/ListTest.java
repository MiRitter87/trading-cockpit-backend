package backend.model.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.NoItemsException;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the List model.
 *
 * @author Michael
 */
public class ListTest {
    /**
     * The list under test.
     */
    private List list;

    /**
     * The instrument under test.
     */
    private Instrument instrument;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        this.instrument = this.getAppleInstrument();
        this.list = this.getDJIList(this.instrument);
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.list = null;
        this.instrument = null;
    }

    /**
     * Gets an Apple Instrument.
     *
     * @return An Apple Instrument.
     */
    private Instrument getAppleInstrument() {
        Instrument instrument = new Instrument();

        instrument.setId(Integer.valueOf(1));
        instrument.setSymbol("AAPL");
        instrument.setType(InstrumentType.STOCK);
        instrument.setStockExchange(StockExchange.NDQ);
        instrument.setName("Apple");

        return instrument;
    }

    /**
     * Gets a List.
     *
     * @param instrument The Instrument to be added to the list.
     * @return The List.
     */
    private List getDJIList(final Instrument instrument) {
        List list = new List();
        list.setId(Integer.valueOf(1));
        list.setName("DJI");
        list.setName("Dow Jones Industrial Average");
        list.setDescription("All stocks of the Dow Jones Industrial Average Index.");
        list.addInstrument(instrument);

        return list;
    }

    @Test
    /**
     * Tests validation of a list whose ID is too low.
     */
    public void testIdTooLow() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.list.setId(0);

        String expectedErrorMessage = messageProvider.getMinValidationMessage("list", "id", "1");
        String errorMessage = "";

        try {
            this.list.validate();
            fail("Validation should have failed because Id is too low.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a list whose name is not given.
     */
    public void testNameNotGiven() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.list.setName("");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("list", "name",
                String.valueOf(this.list.getName().length()), "1", "50");
        String errorMessage = "";

        try {
            this.list.validate();
            fail("Validation should have failed because name is not given.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a list whose name is too long.
     */
    public void testNameTooLong() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.list.setName("This is a list name that is way too long to be of use");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("list", "name",
                String.valueOf(this.list.getName().length()), "1", "50");
        String errorMessage = "";

        try {
            this.list.validate();
            fail("Validation should have failed because name is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a list whose name is null.
     */
    public void testNameIsNull() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.list.setName(null);

        String expectedErrorMessage = messageProvider.getNotNullValidationMessage("list", "name");
        String errorMessage = "";

        try {
            this.list.validate();
            fail("Validation should have failed because name is null.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a list whose description is not given.
     */
    public void testDescriptionNotGiven() {
        this.list.setDescription("");

        try {
            this.list.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests validation of a list whose description is too long.
     */
    public void testDescriptionTooLong() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();

        this.list.setDescription(
                "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. "
                        + "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. "
                        + "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("list", "description",
                String.valueOf(this.list.getDescription().length()), "0", "250");
        String errorMessage = "";

        try {
            this.list.validate();
            fail("Validation should have failed because description is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a list whose description is null.
     */
    public void testDescriptionIsNull() {
        this.list.setDescription(null);

        try {
            this.list.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests validation of a list that has no items given.
     */
    public void testNoItemsGiven() {
        this.list.getInstruments().clear();

        try {
            this.list.validate();
            fail("Validation should have failed because list has no items defined.");
        } catch (NoItemsException expected) {
            // All is well.
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the NoItemsException.");
        }
    }

    @Test
    /**
     * Tests validation of a list where duplicate instruments have been added.
     */
    public void testListContainsDuplicateInstrument() {
        Instrument duplicateInstrument = new Instrument();
        duplicateInstrument.setId(Integer.valueOf(1));
        duplicateInstrument.setSymbol("AAPL");
        duplicateInstrument.setType(InstrumentType.STOCK);
        duplicateInstrument.setStockExchange(StockExchange.NDQ);
        duplicateInstrument.setName("Apple");

        this.list.addInstrument(duplicateInstrument);

        assertEquals(1, this.list.getInstruments().size());
    }

    @Test
    /**
     * Tests if two lists are equal
     */
    public void testEquals() {
        List secondList = this.getDJIList(this.getAppleInstrument());

        assertTrue(this.list.equals(secondList));
    }
}
