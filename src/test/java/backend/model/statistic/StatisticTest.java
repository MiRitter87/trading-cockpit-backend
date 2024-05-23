package backend.model.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.LocalizedException;

/**
 * Tests the Statistic model.
 *
 * @author Michael
 */
public class StatisticTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

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

    @Test
    /**
     * Tests validation of a Statistic where both the id of sector and industry group are defined.
     */
    public void testValidateSectorAndIgDefined() {
        String expectedErrorMessage = this.resources.getString("statistic.sectorAndIgDefined");
        String actualErrorMessage;

        this.statistic.setSectorId(1);
        this.statistic.setIndustryGroupId(2);

        try {
            this.statistic.validate();
            fail("Validation should have failed because statistic is referenced to both sector and IG.");
        } catch (LocalizedException expected) {
            actualErrorMessage = expected.getLocalizedMessage();
            assertEquals(expectedErrorMessage, actualErrorMessage);
        }
    }
}
