package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.LocalizedException;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the validation of the Instrument model.
 *
 * @author MiRitter87
 */
public class InstrumentValidationTest {
    /**
     * Class providing helper methods for fixture.
     */
    private InstrumentFixture fixtureHelper;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * The instrument under test.
     */
    private Instrument instrument;

    /**
     * The Microsoft stock.
     */
    private Instrument microsoftStock;

    /**
     * A sector Instrument.
     */
    private Instrument sector;

    /**
     * An industry group Instrument.
     */
    private Instrument industryGroup;

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.fixtureHelper = new InstrumentFixture();
        this.initializeInstruments();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.industryGroup = null;
        this.sector = null;
        this.microsoftStock = null;
        this.instrument = null;

        this.fixtureHelper = null;
    }

    /**
     * Initializes the instruments.
     */
    private void initializeInstruments() {
        this.instrument = this.fixtureHelper.getAppleStock();
        this.microsoftStock = this.fixtureHelper.getMicrosoftStock();

        this.sector = this.fixtureHelper.getSector();
        this.industryGroup = this.fixtureHelper.getIndustryGroup();
    }

    /**
     * Tests validation of an instrument whose ID is too low.
     */
    @Test
    public void testIdTooLow() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.instrument.setId(0);

        String expectedErrorMessage = messageProvider.getMinValidationMessage("instrument", "id", "1");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because Id is too low.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose symbol is null.
     */
    @Test
    public void testSymbolIsNull() {
        this.instrument.setSymbol(null);

        String expectedErrorMessage = this.resources.getString("instrument.symbol.notNull.message");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because symbol is null.");
        } catch (Exception expected) {
            errorMessage = expected.getLocalizedMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose symbol is not given.
     */
    @Test
    public void testSymbolNotGiven() {
        this.instrument.setSymbol("");

        String expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.symbol.size.message"),
                this.instrument.getSymbol().length(), "1", "6");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because symbol is not given.");
        } catch (Exception expected) {
            errorMessage = expected.getLocalizedMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose symbol is too long.
     */
    @Test
    public void testSymbolTooLong() {
        this.instrument.setSymbol("ABCDEFG");

        String expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.symbol.size.message"),
                this.instrument.getSymbol().length(), "1", "6");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because symbol is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getLocalizedMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose type is null.
     */
    @Test
    public void testTypeIsNull() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.instrument.setType(null);

        String expectedErrorMessage = messageProvider.getNotNullValidationMessage("instrument", "type");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because type is null.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose stock exchange is null.
     */
    @Test
    public void testStockExchangeIsNull() {
        this.instrument.setStockExchange(null);

        String expectedErrorMessage = this.resources.getString("instrument.stockExchange.notNull.message");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because stock exchange is null.");
        } catch (Exception expected) {
            errorMessage = expected.getLocalizedMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose name is too long.
     */
    @Test
    public void testNameTooLong() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.instrument.setName("This is a company name that is way too long to be of use");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "name",
                String.valueOf(this.instrument.getName().length()), "0", "50");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because name is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an instrument whose name is null.
     */
    @Test
    public void testNameIsNull() {
        this.instrument.setName(null);

        try {
            this.instrument.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation of an Instrument whose investingId is too long.
     */
    @Test
    public void testInvestingIdTooLong() {
        String expectedErrorMessage;
        String errorMessage = "";

        this.instrument.setInvestingId("01234567890");
        expectedErrorMessage = MessageFormat.format(this.resources.getString("instrument.investingId.size.message"),
                String.valueOf(this.instrument.getInvestingId().length()), "0", "10");

        try {
            this.instrument.validate();
            fail("Validation should have failed because investingId is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getLocalizedMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    /**
     * Tests validation of an Instrument whose investingId is null.
     */
    @Test
    public void testInvestingIdIsNull() {
        this.instrument.setInvestingId(null);

        try {
            this.instrument.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests referencing the sector of an Instrument with another Instrument of type stock.
     */
    @Test
    public void testReferenceSectorWithStock() {
        String expectedErrorMessage = this.resources.getString("instrument.sector.wrongReference");

        this.instrument.setSector(this.microsoftStock);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the sector is referenced "
                    + "with an Instrument that is not of type 'SECTOR'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    /**
     * Tests referencing the industry group of an Instrument with another Instrument of type stock.
     */
    @Test
    public void testReferenceIndustryGroupWithStock() {
        String expectedErrorMessage = this.resources.getString("instrument.ig.wrongReference");

        this.instrument.setIndustryGroup(this.microsoftStock);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the industry group is referenced "
                    + "with an Instrument that is not of type 'INDUSTRY_GROUP'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    /**
     * Tests referencing an Instrument of type sector with another sector.
     */
    @Test
    public void testReferenceSectorWithSector() {
        String expectedErrorMessage = this.resources.getString("instrument.sector.sectorReference");

        this.instrument.setType(InstrumentType.SECTOR);
        this.instrument.setSector(this.sector);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the sector is referenced "
                    + "with an Instrument that is of type 'SECTOR'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    /**
     * Tests referencing an Instrument of type industry group with another industry group.
     */
    @Test
    public void testReferenceIgWithIg() {
        String expectedErrorMessage = this.resources.getString("instrument.ig.igReference");

        this.instrument.setType(InstrumentType.IND_GROUP);
        this.instrument.setIndustryGroup(this.industryGroup);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the industry group is referenced "
                    + "with an Instrument that is of type 'IND_GROUP'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    /**
     * Tests the orderly reference of an Instrument with a sector and an industry group.
     */
    @Test
    public void testSectorAndIGReferenced() {
        this.instrument.setSector(this.sector);
        this.instrument.setIndustryGroup(this.industryGroup);

        try {
            this.instrument.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if the dividend is not null, if the instrument type is set to any other than 'RATIO'.
     */
    @Test
    public void testDividendNotNullOnTypeNotRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.dividend.definedOnTypeNotRatio");

        this.instrument.setDividend(this.sector);

        try {
            this.instrument.validate();
            fail("Validation should have failed because Instrument is not of type 'RATIO' but dividend is defined.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if the divisor is not null, if the instrument type is set to any other than 'RATIO'.
     */
    @Test
    public void testDivisorNotNullOnTypeNotRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.divisor.definedOnTypeNotRatio");

        this.instrument.setDivisor(this.industryGroup);

        try {
            this.instrument.validate();
            fail("Validation should have failed because Instrument is not of type 'RATIO' but divisor is defined.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
