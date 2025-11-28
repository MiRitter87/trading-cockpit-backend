package backend.model.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.tools.DateTools;

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
    public void setUp() {
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
        entry.setCategory(ProtocolEntryCategory.WARNING);
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
    public void tearDown() {
        this.protocol = null;
    }

    @Test
    /**
     * Tests the retrieval of protocol entries by a given date.
     */
    public void testGetEntriesOfDate() {
        Calendar calendar = Calendar.getInstance();
        List<ProtocolEntry> protocolEntries;
        int expectedNumberOfEntries = 3;
        Date expectedDate;
        Date actualDate;

        calendar.set(2023, 10, 2);
        protocolEntries = this.protocol.getEntriesOfDate(calendar.getTime());

        assertEquals(expectedNumberOfEntries, protocolEntries.size());

        expectedDate = DateTools.getDateWithoutIntradayAttributes(calendar.getTime());

        for (ProtocolEntry entry : protocolEntries) {
            actualDate = DateTools.getDateWithoutIntradayAttributes(entry.getDate());
            assertEquals(expectedDate, actualDate);
        }
    }

    @Test
    /**
     * Tests the calculation of percentage values for confirmations, violations and warnings based on all protocol
     * entries.
     */
    public void testCalculatePercentages() {
        final int expectedConfirmationPercentage = 50;
        final int expectedViolationPercentage = 25;
        final int expectedWarningPercentage = 25;

        this.protocol.calculatePercentages();

        assertEquals(expectedConfirmationPercentage, this.protocol.getConfirmationPercentage());
        assertEquals(expectedViolationPercentage, this.protocol.getViolationPercentage());
        assertEquals(expectedWarningPercentage, this.protocol.getWarningPercentage());
    }
}
