package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.LocalizedException;
import backend.model.StockExchange;

/**
 * Tests ratio-related validations of the Instrument model.
 *
 * @author MiRitter87
 */
public class InstrumentRatioValidationTest {
    /**
     * Class providing helper methods for fixture.
     */
    private InstrumentFixture fixtureHelper;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * A sector Instrument.
     */
    private Instrument sector;

    /**
     * An industry group Instrument.
     */
    private Instrument industryGroup;

    /**
     * A sector/industry group ratio.
     */
    private Instrument sectorIgRatio;

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
        this.sectorIgRatio = null;
        this.industryGroup = null;
        this.sector = null;

        this.fixtureHelper = null;
    }

    /**
     * Initializes the instruments.
     */
    private void initializeInstruments() {
        this.sector = this.fixtureHelper.getSector();
        this.industryGroup = this.fixtureHelper.getIndustryGroup();
        this.sectorIgRatio = this.fixtureHelper.getSectorIgRatio(this.sector, this.industryGroup);
    }

    /**
     * Tests if the stock exchange is null, if the instrument type is set to 'RATIO'.
     */
    @Test
    public void testExchangeNullOnTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.stockExchange.definedOnTypeRatio");

        this.sectorIgRatio.setStockExchange(StockExchange.NYSE);

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because Instrument is of type 'RATIO' and stock exchange is not null.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if the symbol is null, if the instrument type is set to 'RATIO'.
     */
    @Test
    public void testSymbolNullOnTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.symbol.definedOnTypeRatio");

        this.sectorIgRatio.setSymbol("ABC");

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because Instrument is of type 'RATIO' and symbol is not null.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if the symbol is an empty string, if the instrument type is set to 'RATIO'.
     */
    @Test
    public void testSymbolEmptyOnTypeRatio() {
        this.sectorIgRatio.setSymbol("");

        try {
            this.sectorIgRatio.validate();
        } catch (LocalizedException e) {
            fail(e.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if the dividend is null, if the instrument type is set to 'RATIO'.
     */
    @Test
    public void testDividendNullOnTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.dividend.notNull.message");

        this.sectorIgRatio.setDividend(null);

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because Instrument is of type 'RATIO' and dividend is null.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests if the divisor is null, if the instrument type is set to 'RATIO'.
     */
    @Test
    public void testDivisorNullOnTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.divisor.notNull.message");

        this.sectorIgRatio.setDivisor(null);

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because Instrument is of type 'RATIO' and divisor is null.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation if the dividend of a ratio is of type RATIO.
     */
    @Test
    public void testValidateDividendTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.dividend.typeRatio");
        Instrument testRatio = new Instrument();

        testRatio.setName("IG/Sector");
        testRatio.setDividend(this.industryGroup);
        testRatio.setDivisor(this.sector);
        testRatio.setType(InstrumentType.RATIO);

        this.sectorIgRatio.setDividend(testRatio);

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because dividend of Instrument is of type 'RATIO'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation if the divisor of a ratio is of type RATIO.
     */
    @Test
    public void testValidateDivisorTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.divisor.typeRatio");
        Instrument testRatio = new Instrument();

        testRatio.setName("IG/Sector");
        testRatio.setDividend(this.industryGroup);
        testRatio.setDivisor(this.sector);
        testRatio.setType(InstrumentType.RATIO);

        this.sectorIgRatio.setDivisor(testRatio);

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because divisor of Instrument is of type 'RATIO'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation of the investingId if Instrument is of type RATIO.
     */
    @Test
    public void testValidateInvestingIdOnRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.investingId.typeRatio");

        this.sectorIgRatio.setInvestingId("4711");

        try {
            this.sectorIgRatio.validate();
            fail("Validation should have failed because investingId of a RATIO is defined.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
