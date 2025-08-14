package backend.controller.instrumentCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.protocol.DateBasedProtocolArray;
import backend.model.protocol.DateBasedProtocolEntry;
import backend.model.protocol.Protocol;
import backend.model.protocol.ProtocolEntry;
import backend.model.protocol.ProtocolEntryCategory;
import backend.model.protocol.SimpleProtocolEntry;
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

        calendar.set(2025, 7, 12);
        protocolEntry = new ProtocolEntry();
        protocolEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));
        protocolEntry.setCategory(ProtocolEntryCategory.WARNING);
        protocolEntry.setText(this.resources.getString("protocol.churning"));
        protocol.getProtocolEntries().add(protocolEntry);

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

        return protocol;
    }

    @Test
    /**
     * Tests the conversion of a Protocol to a DateBasedProtocolArray. The entries are verified.
     */
    public void testConvertToDateBasedProtocolArrayEntries() {
        DateBasedProtocolArray actualDateBasedProtocolArray = this.protocolConverter
                .convertToDateBasedProtocolArray(this.getProtocolForTest());
        final int expectedEntries = 2;
        DateBasedProtocolEntry actualDateBasedEntry;
        DateBasedProtocolEntry expectedDateBasedEntry = new DateBasedProtocolEntry();

        SimpleProtocolEntry expectedSimpleEntry = new SimpleProtocolEntry();
        Calendar calendar = Calendar.getInstance();

        // Verify correct number of date-based protocol entries.
        assertEquals(expectedEntries, actualDateBasedProtocolArray.getDateBasedProtocolEntries().size());

        // Verify the first date-based protocol entry with its simple protocol entries.
        calendar.set(2025, 7, 13);
        expectedDateBasedEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));

        actualDateBasedEntry = actualDateBasedProtocolArray.getDateBasedProtocolEntries().get(0);
        assertEquals(expectedDateBasedEntry.getDate(), actualDateBasedEntry.getDate());

        for (SimpleProtocolEntry actualEntry : actualDateBasedEntry.getSimpleProtocolEntries()) {
            if (actualEntry.getCategory() == ProtocolEntryCategory.CONFIRMATION) {
                assertEquals(this.resources.getString("protocol.upOnVolume"), actualEntry.getText());
            } else if (actualEntry.getCategory() == ProtocolEntryCategory.WARNING) {
                assertEquals(this.resources.getString("protocol.timeClimax"), actualEntry.getText());
            } else if (actualEntry.getCategory() == ProtocolEntryCategory.VIOLATION) {
                assertEquals(this.resources.getString("protocol.downOnVolume"), actualEntry.getText());
            }
        }

        // Verify the second date-based protocol entry with its simple protocol entries.
        calendar.set(2025, 7, 12);
        expectedDateBasedEntry.setDate(DateTools.getDateWithoutIntradayAttributes(calendar.getTime()));

        actualDateBasedEntry = actualDateBasedProtocolArray.getDateBasedProtocolEntries().get(1);
        assertEquals(expectedDateBasedEntry.getDate(), actualDateBasedEntry.getDate());

        expectedSimpleEntry.setCategory(ProtocolEntryCategory.WARNING);
        expectedSimpleEntry.setText(this.resources.getString("protocol.churning"));
        assertEquals(expectedSimpleEntry, actualDateBasedEntry.getSimpleProtocolEntries().get(0));
    }

    @Test
    /**
     * Tests the conversion of a Protocol to a DateBasedProtocolArray. The correct sorting of the entries is verified.
     */
    public void testConvertToDateBasedProtocolArraySorting() {
        DateBasedProtocolArray actualDateBasedProtocolArray = this.protocolConverter
                .convertToDateBasedProtocolArray(this.getProtocolForTest());
        Calendar calendar = Calendar.getInstance();
        final Date expectedEntry1Date;
        final Date expectedEntry2Date;

        calendar.set(2025, 7, 13);
        expectedEntry1Date = DateTools.getDateWithoutIntradayAttributes(calendar.getTime());
        calendar.set(2025, 7, 12);
        expectedEntry2Date = DateTools.getDateWithoutIntradayAttributes(calendar.getTime());

        assertEquals(expectedEntry1Date, actualDateBasedProtocolArray.getDateBasedProtocolEntries().get(0).getDate());
        assertEquals(expectedEntry2Date, actualDateBasedProtocolArray.getDateBasedProtocolEntries().get(1).getDate());
    }

    @Test
    /**
     * Tests the conversion of a Protocol to a DateBasedProtocolArray. The correct percentage calculation of events for
     * each category is verified.
     */
    public void testConvertToDateBasedProtocolArrayCounting() {
        DateBasedProtocolArray dateBasedProtocolArray = this.protocolConverter
                .convertToDateBasedProtocolArray(this.getProtocolForTest());
        DateBasedProtocolEntry entry;
        final int expectedConfirmationPercent = 33;
        final int expectedWarningPercent = 33;
        final int expectedViolationPercent = 33;

        entry = dateBasedProtocolArray.getDateBasedProtocolEntries().get(0);
        assertEquals(expectedConfirmationPercent, entry.getConfirmationPercentage());
        assertEquals(expectedWarningPercent, entry.getWarningPercentage());
        assertEquals(expectedViolationPercent, entry.getViolationPercentage());
    }
}
