package backend.controller.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.instrument.Indicator;
import backend.model.instrument.Quotation;

/**
 * Tests the RelativeStrengthCalculator.
 *
 * @author Michael
 */
public class RelativeStrengthCalculatorTest {
    /**
     * The RelativeStrengthCalculator under test.
     */
    private RelativeStrengthCalculator relativeStrengthCalculator;

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
            this.relativeStrengthCalculator = new RelativeStrengthCalculator();
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
        this.relativeStrengthCalculator = null;

        this.dmlQuotation3 = null;
        this.dmlQuotation2 = null;
        this.dmlQuotation1 = null;
    }

    /**
     * Initializes dummy quotations of the DML Instrument.
     */
    private void initializeDmlQuotations() {
        this.dmlQuotation1 = new Quotation();
        this.dmlQuotation1.setIndicator(new Indicator(true, false));
        this.dmlQuotation1.getIndicator().getRelativeStrengthData().setRsPercentSum((float) 34.5);
        this.dmlQuotation1.getIndicator().setDistanceTo52WeekHigh((float) -4.56);
        this.dmlQuotation1.getIndicator().setUpDownVolumeRatio((float) 2.44);

        this.dmlQuotation2 = new Quotation();
        this.dmlQuotation2.setIndicator(new Indicator(true, false));
        this.dmlQuotation2.getIndicator().getRelativeStrengthData().setRsPercentSum(-5);
        this.dmlQuotation2.getIndicator().setDistanceTo52WeekHigh((float) -0.56);
        this.dmlQuotation2.getIndicator().setUpDownVolumeRatio((float) 0.87);

        this.dmlQuotation3 = new Quotation();
        this.dmlQuotation3.setIndicator(new Indicator(true, false));
        this.dmlQuotation3.getIndicator().getRelativeStrengthData().setRsPercentSum((float) 12.35);
        this.dmlQuotation3.getIndicator().setDistanceTo52WeekHigh(-7);
        this.dmlQuotation3.getIndicator().setUpDownVolumeRatio((float) 1.01);
    }

    @Test
    /**
     * Tests the calculation of RS numbers: rsNumber.
     */
    public void testCalculateRsNumber() {
        List<Quotation> quotations = new ArrayList<>();

        // Prepare all quotations on which the RS number is to be calculated.
        quotations.add(this.dmlQuotation1);
        quotations.add(this.dmlQuotation2);
        quotations.add(this.dmlQuotation3);

        // Calculate the RS numbers.
        this.relativeStrengthCalculator.calculateRsNumber(quotations);

        // Verify the correct calculation.
        assertEquals(33, this.dmlQuotation2.getIndicator().getRelativeStrengthData().getRsNumber());
        assertEquals(67, this.dmlQuotation3.getIndicator().getRelativeStrengthData().getRsNumber());
        assertEquals(100, this.dmlQuotation1.getIndicator().getRelativeStrengthData().getRsNumber());
    }

    @Test
    /**
     * Tests the calculation of RS numbers: rsNumberDistance52WeekHigh.
     */
    public void testCalculateRsNumberDistanceTo52wHigh() {
        List<Quotation> quotations = new ArrayList<>();

        // Prepare all quotations on which the RS number is to be calculated.
        quotations.add(this.dmlQuotation1);
        quotations.add(this.dmlQuotation2);
        quotations.add(this.dmlQuotation3);

        // Calculate the RS numbers.
        this.relativeStrengthCalculator.calculateRsNumberDistanceTo52wHigh(quotations);

        // Verify the correct calculation.
        assertEquals(33, this.dmlQuotation3.getIndicator().getRelativeStrengthData().getRsNumberDistance52WeekHigh());
        assertEquals(67, this.dmlQuotation1.getIndicator().getRelativeStrengthData().getRsNumberDistance52WeekHigh());
        assertEquals(100, this.dmlQuotation2.getIndicator().getRelativeStrengthData().getRsNumberDistance52WeekHigh());
    }

    @Test
    /**
     * Tests the calculation of RS numbers: rsNumberUpDownVolumeRatio.
     */
    public void testCalculateRsNumberUpDownVolumeRatio() {
        List<Quotation> quotations = new ArrayList<>();

        // Prepare all quotations on which the RS number is to be calculated.
        quotations.add(this.dmlQuotation1);
        quotations.add(this.dmlQuotation2);
        quotations.add(this.dmlQuotation3);

        // Calculate the RS numbers.
        this.relativeStrengthCalculator.calculateRsNumberUpDownVolumeRatio(quotations);

        // Verify the correct calculation.
        assertEquals(33, this.dmlQuotation2.getIndicator().getRelativeStrengthData().getRsNumberUpDownVolumeRatio());
        assertEquals(67, this.dmlQuotation3.getIndicator().getRelativeStrengthData().getRsNumberUpDownVolumeRatio());
        assertEquals(100, this.dmlQuotation1.getIndicator().getRelativeStrengthData().getRsNumberUpDownVolumeRatio());
    }
}
