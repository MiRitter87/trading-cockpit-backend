package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import backend.model.instrument.Indicator;
import backend.model.instrument.Quotation;

/**
 * Tests the RelativeStrengthCalculator.
 *
 * @author Michael
 */
public class RelativeStrengthCalculatorTest {
    /**
     * A Quotation for testing.
     */
    private Quotation dmlQuotation1;

    /**
     * A Quotation for testing.
     */
    private Quotation dmlQuotation2;

    /**
     * A Quotation for testing.
     */
    private Quotation dmlQuotation3;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        try {
            this.initializeDmlQuotations();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.dmlQuotation3 = null;
        this.dmlQuotation2 = null;
        this.dmlQuotation1 = null;
    }

    /**
     * Initializes dummy quotations of the DML Instrument.
     */
    private void initializeDmlQuotations() {
        this.dmlQuotation1 = new Quotation();
        this.dmlQuotation1.setIndicator(new Indicator(true));
        this.dmlQuotation1.getIndicator().getRelativeStrengthData().setRsPercentSum((float) 34.5);
        this.dmlQuotation1.getIndicator().setDistanceTo52WeekHigh((float) -4.56);
        this.dmlQuotation1.getIndicator().setUpDownVolumeRatio((float) 2.44);

        this.dmlQuotation2 = new Quotation();
        this.dmlQuotation2.setIndicator(new Indicator(true));
        this.dmlQuotation2.getIndicator().getRelativeStrengthData().setRsPercentSum(-5);
        this.dmlQuotation2.getIndicator().setDistanceTo52WeekHigh((float) -0.56);
        this.dmlQuotation2.getIndicator().setUpDownVolumeRatio((float) 0.87);

        this.dmlQuotation3 = new Quotation();
        this.dmlQuotation3.setIndicator(new Indicator(true));
        this.dmlQuotation3.getIndicator().getRelativeStrengthData().setRsPercentSum((float) 12.35);
        this.dmlQuotation3.getIndicator().setDistanceTo52WeekHigh(-7);
        this.dmlQuotation3.getIndicator().setUpDownVolumeRatio((float) 1.01);
    }
}
