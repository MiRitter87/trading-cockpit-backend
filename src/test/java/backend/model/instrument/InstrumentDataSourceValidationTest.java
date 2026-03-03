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
 * Tests datasource-related validations of the Instrument model.
 *
 * @author MiRitter87
 */
public class InstrumentDataSourceValidationTest {
    /**
     * Class providing helper methods for fixture.
     */
    private InstrumentFixture fixtureHelper;

    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * Instrument that has a List as data source defined.
     */
    private Instrument instrumentWithDataSource;

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
        this.instrumentWithDataSource = null;

        this.fixtureHelper = null;
    }

    /**
     * Initializes the instruments.
     */
    private void initializeInstruments() {
        this.instrumentWithDataSource = this.fixtureHelper.getInstrumentWithDataSource();
    }

    /**
     * Tests validation of the dataSourceList attribute.
     */
    @Test
    public void testValidateDataSourceList() {
        String expectedErrorMessage = this.resources.getString("instrument.dataSourceList.wrongType");

        // Validation should fail because dataSourceList is not allowed if type is STOCK.
        try {
            this.instrumentWithDataSource.setType(InstrumentType.STOCK);
            this.instrumentWithDataSource.validate();
            fail("Validation should have failed because Instrument is of type 'STOCK'.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Validation should not fail because dataSourceList is allowed if type is ETF.
        try {
            this.instrumentWithDataSource.setType(InstrumentType.ETF);
            this.instrumentWithDataSource.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Validation should not fail because dataSourceList is allowed if type is sector.
        try {
            this.instrumentWithDataSource.setType(InstrumentType.SECTOR);
            this.instrumentWithDataSource.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // Validation should not fail because dataSourceList is allowed if type is industry group.
        try {
            this.instrumentWithDataSource.setType(InstrumentType.IND_GROUP);
            this.instrumentWithDataSource.validate();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation of the dataSourceList attribute while the symbol is defined.
     */
    @Test
    public void testValidateSymbolOnDataSourceList() {
        String expectedErrorMessage = this.resources.getString("instrument.symbol.dataSourceListDefined");

        // Validation should fail because no symbol can be defined if dataSourceList is set.
        try {
            this.instrumentWithDataSource.setSymbol("test");
            this.instrumentWithDataSource.validate();
            fail("Validation should have failed because Instrument has a symbol defined.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation of the dataSourceList attribute while the stock exchange is defined.
     */
    @Test
    public void testValidateExchangeOnDataSourceList() {
        String expectedErrorMessage = this.resources.getString("instrument.stockExchange.dataSourceListDefined");

        // Validation should fail because no stock exchange can be defined if dataSourceList is set.
        try {
            this.instrumentWithDataSource.setStockExchange(StockExchange.NYSE);
            this.instrumentWithDataSource.validate();
            fail("Validation should have failed because Instrument has a stock exchange defined.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests validation of the dataSourceList attribute while the investingId is defined.
     */
    @Test
    public void testValidateInvestingIdOnDataSourceList() {
        String expectedErrorMessage = this.resources.getString("instrument.investingId.dataSourceListeDefined");

        // Validation should fail because no investingId can be defined if dataSourceList is set.
        try {
            this.instrumentWithDataSource.setInvestingId("4711");
            this.instrumentWithDataSource.validate();
            fail("Validation should have failed because Instrument has an investingId defined.");
        } catch (LocalizedException expected) {
            assertEquals(expectedErrorMessage, expected.getLocalizedMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
