package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.controller.instrumentCheck.HealthCheckProfile;
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

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
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
    private void tearDown() {
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
        protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
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
     * Tests the determination of the event number using only confirmations.
     */
    public void testGetEventNumberConfirmations() {
        int actualEventNumber;
        final int expectedEventNumer = 3;
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol,
                HealthCheckProfile.CONFIRMATIONS, quotation);
        assertEquals(expectedEventNumer, actualEventNumber);
    }

    @Test
    /**
     * Tests the determination of the event number using only violations.
     */
    public void testGetEventNumberViolations() {
        int actualEventNumber;
        final int expectedEventNumer = 1;
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol,
                HealthCheckProfile.SELLING_INTO_WEAKNESS, quotation);
        assertEquals(expectedEventNumer, actualEventNumber);
    }

    @Test
    /**
     * Tests the determination of the event number using only uncertainties.
     */
    public void testGetEventNumberUncertainties() {
        int actualEventNumber;
        final int expectedEventNumer = 1;
        Quotation quotation = new Quotation();

        quotation.setDate(new Date());
        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol,
                HealthCheckProfile.SELLING_INTO_STRENGTH, quotation);
        assertEquals(expectedEventNumer, actualEventNumber);
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
        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol, HealthCheckProfile.ALL,
                quotation);
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

        actualEventNumber = this.healthCheckChartController.getEventNumber(this.protocol, HealthCheckProfile.ALL,
                quotation);
        assertEquals(expectedEventNumer, actualEventNumber);
    }
}
