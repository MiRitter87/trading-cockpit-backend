package backend.model.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.LocalizedException;
import backend.model.StockExchange;
import backend.tools.test.ValidationMessageProvider;

/**
 * Tests the Instrument model.
 *
 * @author Michael
 */
public class InstrumentTest {
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
     * A sector/industry group ratio.
     */
    private Instrument sectorIgRatio;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        this.fixtureHelper = new InstrumentFixture();
        this.initializeQuotations();
        this.initializeInstruments();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.sectorIgRatio = null;
        this.industryGroup = null;
        this.sector = null;
        this.microsoftStock = null;
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

        this.microsoftStock = this.fixtureHelper.getMicrosoftStock();
        this.sector = this.fixtureHelper.getSector();
        this.industryGroup = this.fixtureHelper.getIndustryGroup();
        this.sectorIgRatio = this.fixtureHelper.getSectorIgRatio(this.sector, this.industryGroup);
    }

    @Test
    /**
     * Tests validation of an instrument whose ID is too low.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose symbol is null.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose symbol is not given.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose symbol is too long.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose type is null.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose stock exchange is null.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose name is too long.
     */
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

    @Test
    /**
     * Tests validation of an instrument whose name is null.
     */
    public void testNameIsNull() {
        this.instrument.setName(null);

        try {
            this.instrument.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests validation of an Instrument whose companyPathInvestingCom is too long.
     */
    public void testCompanyPathInvestingComTooLong() {
        ValidationMessageProvider messageProvider = new ValidationMessageProvider();
        this.instrument.setCompanyPathInvestingCom("denison-mines-corp?cid=24520kjndfkvnfkjgndffjkkfn11");

        String expectedErrorMessage = messageProvider.getSizeValidationMessage("instrument", "companyPathInvestingCom",
                String.valueOf(this.instrument.getCompanyPathInvestingCom().length()), "0", "50");
        String errorMessage = "";

        try {
            this.instrument.validate();
            fail("Validation should have failed because companyPathInvestingCom is too long.");
        } catch (Exception expected) {
            errorMessage = expected.getMessage();
        }

        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    /**
     * Tests validation of an Instrument whose companyNameInvestingCom is null.
     */
    public void testCompanyPathInvestingComIsNull() {
        this.instrument.setCompanyPathInvestingCom(null);

        try {
            this.instrument.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests getting a quotation by a given date. A quotation for the date exists.
     */
    public void testGetQuotationByDateExisting() {
        Quotation quotation;
        Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set(2022, 07, 26, 15, 30, 0); // Date of quotation1.

        quotation = this.instrument.getQuotationByDate(calendar.getTime());

        assertEquals(this.quotation1, quotation);
    }

    @Test
    /**
     * Tests getting a quotation by a given date. A quotation for the date does not exist.
     */
    public void testGetQuotationByDateNotExisting() {
        Quotation quotation;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 07, 25); // No quotation at that date exists.

        quotation = this.instrument.getQuotationByDate(calendar.getTime());

        assertNull(quotation);
    }

    @Test
    /**
     * Tests getting the quotations of an instrument as a list sorted by date.
     */
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

    @Test
    /**
     * Tests referencing the sector of an Instrument with another Instrument of type stock.
     */
    public void testReferenceSectorWithStock() {
        String expectedErrorMessage = this.resources.getString("instrument.wrongSectorReference");

        this.instrument.setSector(this.microsoftStock);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the sector is referenced with an Instrument that is not of type 'SECTOR'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    @Test
    /**
     * Tests referencing the industry group of an Instrument with another Instrument of type stock.
     */
    public void testReferenceIndustryGroupWithStock() {
        String expectedErrorMessage = this.resources.getString("instrument.wrongIndustryGroupReference");

        this.instrument.setIndustryGroup(this.microsoftStock);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the industry group is referenced with an Instrument that is not of type 'INDUSTRY_GROUP'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    @Test
    /**
     * Tests referencing an Instrument of type sector with another sector.
     */
    public void testReferenceSectorWithSector() {
        String expectedErrorMessage = this.resources.getString("instrument.sectorSectorReference");

        this.instrument.setType(InstrumentType.SECTOR);
        this.instrument.setSector(this.sector);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the sector is referenced with an Instrument that is of type 'SECTOR'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    @Test
    /**
     * Tests referencing an Instrument of type industry group with another industry group.
     */
    public void testReferenceIgWithIg() {
        String expectedErrorMessage = this.resources.getString("instrument.igIgReference");

        this.instrument.setType(InstrumentType.IND_GROUP);
        this.instrument.setIndustryGroup(this.industryGroup);

        try {
            this.instrument.validate();
            fail("Validation should have failed because the industry group is referenced with an Instrument that is of type 'IND_GROUP'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail("No general exception should have occurred. Just the LocalizedException.");
        }
    }

    @Test
    /**
     * Tests if the stock exchange is null, if the instrument type is set to 'RATIO'.
     */
    public void testExchangeNullOnTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.exchangeDefinedOnTypeRatio");
        ;

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

    @Test
    /**
     * Tests if the symbol is null, if the instrument type is set to 'RATIO'.
     */
    public void testSymbolNullOnTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.symbolDefinedOnTypeRatio");

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

    @Test
    /**
     * Tests if the symbol is an empty string, if the instrument type is set to 'RATIO'.
     */
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

    @Test
    /**
     * Tests if the dividend is null, if the instrument type is set to 'RATIO'.
     */
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

    @Test
    /**
     * Tests if the dividend is not null, if the instrument type is set to any other than 'RATIO'.
     */
    public void testDividendNotNullOnTypeNotRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.dividendDefinedOnTypeNotRatio");

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

    @Test
    /**
     * Tests if the divisor is null, if the instrument type is set to 'RATIO'.
     */
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

    @Test
    /**
     * Tests if the divisor is not null, if the instrument type is set to any other than 'RATIO'.
     */
    public void testDivisorNotNullOnTypeNotRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.divisorDefinedOnTypeNotRatio");

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

    @Test
    /**
     * Tests validation if the dividend of a ratio is of type RATIO.
     */
    public void testValidateDividendTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.dividendTypeRatio");
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

    @Test
    /**
     * Tests validation if the divisor of a ratio is of type RATIO.
     */
    public void testValidateDivisorTypeRatio() {
        String expectedErrorMessage = this.resources.getString("instrument.divisorTypeRatio");
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
}