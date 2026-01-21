package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.DataProvider;
import backend.model.StockExchange;

/**
 * Tests the functionality of the ScanController.
 *
 * @author Michael
 */
public class ScanControllerTest {
    /**
     * The ScanController under test.
     */
    private ScanController scanController;

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        try {
            this.scanController = new ScanController();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.scanController = null;
    }

    /**
     * Tests getting the query interval.
     */
    @Test
    public void testGetQueryInterval() {
        final int expectedQueryInterval = 5;
        final int actualQueryInterval = this.scanController.getQueryInterval();

        assertEquals(expectedQueryInterval, actualQueryInterval);
    }

    /**
     * Tests getting the data providers.
     */
    @Test
    public void testGetDataProviders() {
        final Map<StockExchange, DataProvider> dataProviders = this.scanController.getDataProviders();
        DataProvider dataProvider;

        dataProvider = dataProviders.get(StockExchange.NYSE);
        assertEquals(DataProvider.YAHOO, dataProvider);

        dataProvider = dataProviders.get(StockExchange.NDQ);
        assertEquals(DataProvider.YAHOO, dataProvider);

        dataProvider = dataProviders.get(StockExchange.AMEX);
        assertEquals(DataProvider.YAHOO, dataProvider);

        dataProvider = dataProviders.get(StockExchange.OTC);
        assertEquals(DataProvider.YAHOO, dataProvider);

        dataProvider = dataProviders.get(StockExchange.TSX);
        assertEquals(DataProvider.MARKETWATCH, dataProvider);

        dataProvider = dataProviders.get(StockExchange.TSXV);
        assertEquals(DataProvider.MARKETWATCH, dataProvider);

        dataProvider = dataProviders.get(StockExchange.CSE);
        assertEquals(DataProvider.MARKETWATCH, dataProvider);

        dataProvider = dataProviders.get(StockExchange.LSE);
        assertEquals(DataProvider.MARKETWATCH, dataProvider);
    }
}
