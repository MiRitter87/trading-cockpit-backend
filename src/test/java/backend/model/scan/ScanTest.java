package backend.model.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.NoItemsException;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.list.List;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the scan model.
 *
 * @author Michael
 */
public class ScanTest {
    /**
     * The scan under test.
     */
    private Scan scan;

    /**
     * The list of the scan.
     */
    private List list;

    /**
     * The instrument of the list.
     */
    private Instrument instrument;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        this.instrument = new Instrument();
        this.instrument.setId(Integer.valueOf(1));
        this.instrument.setSymbol("AAPL");
        this.instrument.setType(InstrumentType.STOCK);
        this.instrument.setStockExchange(StockExchange.NDQ);
        this.instrument.setName("Apple");

        this.list = new List();
        this.list.setId(Integer.valueOf(1));
        this.list.setName("DJI");
        this.list.setName("Dow Jones Industrial Average");
        this.list.setDescription("All stocks of the Dow Jones Industrial Average Index.");
        this.list.addInstrument(this.instrument);

        this.scan = new Scan();
        this.scan.setName("Test scan");
        this.scan.setDescription("A simple scan of a single list.");
        this.scan.addList(this.list);
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.scan = null;
        this.list = null;
        this.instrument = null;
    }

    @Test
    /**
     * Tests validation of a scan whose ID is too low.
     */
    public void testIdTooLow() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setId(0);

        String expectedErrorMessage = messageProvider.getMinValidationMessage("scan", "id", "1");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because Id is too low.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose name is not given.
     */
    public void testNameNotGiven() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setName("");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("scan", "name",
                String.valueOf(this.scan.getName().length()), "1", "50");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because name is not given.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose name is too long.
     */
    public void testNameTooLong() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setName("This is a scan name that is way too long to be of use");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("scan", "name",
                String.valueOf(this.scan.getName().length()), "1", "50");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because name is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose name is null.
     */
    public void testNameIsNull() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setName(null);

        String expectedErrorMessage = messageProvider.getNotNullValidationMessage("scan", "name");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because name is null.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose description is not given.
     */
    public void testDescriptionNotGiven() {
        this.scan.setDescription("");

        try {
            this.list.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests validation of a scan whose description is too long.
     */
    public void testDescriptionTooLong() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();

        this.scan.setDescription(
                "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. "
                        + "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. "
                        + "Das ist ein Beschreibungstext. Das ist ein Beschreibungstext. Das");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("scan", "description",
                String.valueOf(this.scan.getDescription().length()), "0", "250");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because description is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose description is null.
     */
    public void testDescriptionIsNull() {
        this.scan.setDescription(null);

        try {
            this.scan.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests validation of a scan whose execution status is null.
     */
    public void testExecutionStatusIsNull() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setExecutionStatus(null);

        String expectedErrorMessage = messageProvider.getNotNullValidationMessage("scan", "executionStatus");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because execution status is null.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose completion status is null.
     */
    public void testCompletionStatusIsNull() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setCompletionStatus(null);

        String expectedErrorMessage = messageProvider.getNotNullValidationMessage("scan", "completionStatus");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because completion status is null.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan that has no lists given.
     */
    public void testNoListsGiven() {
        this.scan.getLists().clear();

        try {
            this.scan.validate();
            fail("Validation should have failed because scan has no lists defined.");
        } catch (NoItemsException expected) {
            // All is well.
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the NoItemsException.");
        }
    }

    @Test
    /**
     * Tests validation of a scan where duplicate lists have been added.
     */
    public void testScanContainsDuplicateLists() {
        List duplicateList = new List();

        duplicateList.setId(Integer.valueOf(1));
        duplicateList.setName("DJI");
        duplicateList.setName("Dow Jones Industrial Average");
        duplicateList.setDescription("All stocks of the Dow Jones Industrial Average Index.");
        duplicateList.addInstrument(this.instrument);

        this.scan.addList(duplicateList);

        assertEquals(1, this.list.getInstruments().size());
    }

    @Test
    /**
     * Tests validation of a scan whose progress attribute is too low.
     */
    public void testProgressTooLow() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setProgress(-1);

        String expectedErrorMessage = messageProvider.getMinValidationMessage("scan", "progress", "0");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because progress is too low.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose progress attribute is too high.
     */
    public void testProgressTooHigh() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setProgress(101);

        String expectedErrorMessage = messageProvider.getMaxValidationMessage("scan", "progress", "100");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because progress is too high.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of a scan whose progress attribute is null.
     */
    public void testProgressIsNull() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.scan.setProgress(null);

        String expectedErrorMessage = messageProvider.getNotNullValidationMessage("scan", "progress");
        String errorMessage = "";

        try {
            this.scan.validate();
            fail("Validation should have failed because progress is null.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }
}
