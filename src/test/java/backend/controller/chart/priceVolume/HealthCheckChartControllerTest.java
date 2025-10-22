package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.model.instrument.Quotation;
import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;

/**
 * Tests the HealthCheckChartController.
 *
 * @author Michael
 */
public class HealthCheckChartControllerTest {
    /**
     * The HealthCheckChartController under test.
     */
    private HealthCheckChartController healthCheckChartController;

    /**
     * A Protocol used for tests.
     */
    private Protocol protocol;

    @AfterAll
    /**
     * Tasks to be performed once at the end of the test class.
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
    public void setUp() {
        try {
            this.healthCheckChartController = new HealthCheckChartController();
            this.protocol = new Protocol();
            this.initializeProtocolEntries();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.healthCheckChartController = null;
        this.protocol = null;
    }

    /**
     * Initializes the List of protocol entries with test data.
     */
    private void initializeProtocolEntries() {
        Calendar calendar = Calendar.getInstance();
        ProtocolEntry protocolEntry;

        // Three confirmations
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        this.protocol.getProtocolEntries().add(protocolEntry);

        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        this.protocol.getProtocolEntries().add(protocolEntry);

        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        this.protocol.getProtocolEntries().add(protocolEntry);

        // One violation
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        this.protocol.getProtocolEntries().add(protocolEntry);

        // One sell into strength
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        this.protocol.getProtocolEntries().add(protocolEntry);

        // One violation yesterday
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        this.protocol.getProtocolEntries().add(protocolEntry);
    }

    @Test
    /**
     * Tests the determination of the event number using all entries.
     */
    public void testGetEventNumberAll() {
        int actualEventNumber;
        final int expectedEventNumer = 1;
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol, quotation);
        assertEquals(expectedEventNumer, actualEventNumber);
    }

    @Test
    /**
     * Tests the determination of the event number using all entries. The calculation is performed for the previous day
     * using a different constellation of protocol entries.
     */
    public void testGetEventNumberAllPreviousDay() {
        int actualEventNumber;
        final int expectedEventNumer = -1;
        Quotation quotation = new Quotation();
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        quotation.setDate(calendar.getTime());

        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol, quotation);
        assertEquals(expectedEventNumer, actualEventNumber);
    }
}
