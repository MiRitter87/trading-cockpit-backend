package backend.model.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the Statistic model.
 *
 * @author Michael
 */
public class StatisticTest {
    /**
     * The Statistic under test.
     */
    private Statistic statistic;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        this.statistic = new Statistic();
        this.statistic.setNumberAboveSma50(3);
        this.statistic.setNumberAtOrBelowSma50(2);
        this.statistic.setNumberAboveSma200(2);
        this.statistic.setNumberAtOrBelowSma200(3);
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.statistic = null;
    }

    @Test
    /**
     * Tests correct calculation of the percentage above SMA(50).
     */
    public void testGetPercentAboveSma50() {
        float percentAboveSma50 = this.statistic.getPercentAboveSma50();
        final int expectedPercentAboveSma50 = 60;

        assertEquals(expectedPercentAboveSma50, percentAboveSma50);
    }

    @Test
    /**
     * Tests correct calculation of the percentage above SMA(200).
     */
    public void testGetPercentAboveSma200() {
        float percentAboveSma200 = this.statistic.getPercentAboveSma200();
        final int expectedPercentAboveSma200 = 40;

        assertEquals(expectedPercentAboveSma200, percentAboveSma200);
    }
}
