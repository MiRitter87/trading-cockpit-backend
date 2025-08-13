package backend.controller.instrumentCheck;

import java.util.Calendar;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.tools.DateTools;

/**
 * Test the ProtocolConverter.
 *
 * @author Michael
 */
public class ProtocolConverterTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * The ProtocolConverter being tested.
     */
    private ProtocolConverter protocolConverter;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        this.protocolConverter = new ProtocolConverter();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.protocolConverter = null;
    }

    /**
     * Provides a health check protocol with several entries.
     *
     * @return A health check protocol with several entries.
     */
    private Protocol getProtocolForTest() {
        Protocol protocol = new Protocol();
        ProtocolEntry protocolEntry;
        Calendar calendar = Calendar.getInstance();

        calendar.set(2025, 7, 13);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        protocolEntry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        protocolEntry.setText(this.resources.getString("protocol.upOnVolume"));
        protocol.getProtocolEntries().add(protocolEntry);

        calendar.set(2025, 7, 13);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        protocolEntry.setText(this.resources.getString("protocol.timeClimax"));
        protocol.getProtocolEntries().add(protocolEntry);

        calendar.set(2025, 7, 13);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        protocolEntry.setCategory(ProtocolEntryCategory.VIOLATION);
        protocolEntry.setText(this.resources.getString("protocol.downOnVolume"));
        protocol.getProtocolEntries().add(protocolEntry);

        calendar.set(2025, 7, 12);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        protocolEntry.setText(this.resources.getString("protocol.churning"));
        protocol.getProtocolEntries().add(protocolEntry);

        return protocol;
    }
}
