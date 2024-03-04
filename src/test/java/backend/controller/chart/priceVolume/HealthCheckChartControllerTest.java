package backend.controller.chart.priceVolume;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
     * A List of protocol entries used for tests.
     */
    private List<ProtocolEntry> protocolEntries;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        try {
            this.healthCheckChartController = new HealthCheckChartController();
            this.protocolEntries = new ArrayList<>();
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
        this.protocolEntries = null;
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
        this.protocolEntries.add(protocolEntry);

        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        this.protocolEntries.add(protocolEntry);

        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        this.protocolEntries.add(protocolEntry);

        // One violation
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        this.protocolEntries.add(protocolEntry);

        // One sell into strength
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.UNCERTAIN);
        this.protocolEntries.add(protocolEntry);

        // One violation yesterday
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(calendar.getTime());
        protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        this.protocolEntries.add(protocolEntry);
    }
}
