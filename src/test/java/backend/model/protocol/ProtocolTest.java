package backend.model.protocol;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Tests the Protocol model.
 *
 * @author Michael
 */
public class ProtocolTest {
    /**
     * The Protocol under test.
     */
    private Protocol protocol;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        ProtocolEntry entry;
        Calendar calendar = Calendar.getInstance();

        this.protocol = new Protocol();

        entry = new ProtocolEntry();
        entry.setText("Entry 1");
        entry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        calendar.set(2023, 10, 3);
        entry.setDate(calendar.getTime());
        this.protocol.getProtocolEntries().add(entry);

        entry = new ProtocolEntry();
        entry.setText("Entry 2");
        entry.setCategory(ProtocolEntryCategory.CONFIRMATION);
        calendar.set(2023, 10, 2);
        entry.setDate(calendar.getTime());
        this.protocol.getProtocolEntries().add(entry);

        entry = new ProtocolEntry();
        entry.setText("Entry 3");
        entry.setCategory(ProtocolEntryCategory.UNCERTAIN);
        calendar.set(2023, 10, 2);
        entry.setDate(calendar.getTime());
        this.protocol.getProtocolEntries().add(entry);

        entry = new ProtocolEntry();
        entry.setText("Entry 4");
        entry.setCategory(ProtocolEntryCategory.VIOLATION);
        calendar.set(2023, 10, 2);
        entry.setDate(calendar.getTime());
        this.protocol.getProtocolEntries().add(entry);
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.protocol = null;
    }
}
