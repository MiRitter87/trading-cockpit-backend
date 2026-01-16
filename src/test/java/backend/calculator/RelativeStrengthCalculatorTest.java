package backend.calculator;

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

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        try {
            this.relativeStrengthCalculator = new RelativeStrengthCalculator();
            this.initializeDmlQuotations();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.relativeStrengthCalculator = null;

        this.dmlQuotation3 = null;
        this.dmlQuotation2 = null;
        this.dmlQuotation1 = null;
    }

    /**
     * Initializes dummy quotations of the DML Instrument.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void initializeDmlQuotations() {
        this.dmlQuotation1 = new Quotation(true, false);
        this.dmlQuotation1.setIndicator(new Indicator());
        this.dmlQuotation1.getRelativeStrengthData().setRsPercentSum(34.5f);
        this.dmlQuotation1.getIndicator().setDistanceTo52WeekHigh(-4.56f);
        this.dmlQuotation1.getIndicator().setAccDisRatio63Days(2.44f);

        this.dmlQuotation2 = new Quotation(true, false);
        this.dmlQuotation2.setIndicator(new Indicator());
        this.dmlQuotation2.getRelativeStrengthData().setRsPercentSum(-5);
        this.dmlQuotation2.getIndicator().setDistanceTo52WeekHigh(-0.56f);
        this.dmlQuotation2.getIndicator().setAccDisRatio63Days(0.87f);

        this.dmlQuotation3 = new Quotation(true, false);
        this.dmlQuotation3.setIndicator(new Indicator());
        this.dmlQuotation3.getRelativeStrengthData().setRsPercentSum(12.35f);
        this.dmlQuotation3.getIndicator().setDistanceTo52WeekHigh(-7);
        this.dmlQuotation3.getIndicator().setAccDisRatio63Days(1.01f);
    }

    /**
     * Tests the calculation of RS numbers: rsNumber.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testCalculateRsNumber() {
        List<Quotation> quotations = new ArrayList<>();

        // Prepare all quotations on which the RS number is to be calculated.
        quotations.add(this.dmlQuotation1);
        quotations.add(this.dmlQuotation2);
        quotations.add(this.dmlQuotation3);

        // Calculate the RS numbers.
        this.relativeStrengthCalculator.calculateRsNumber(quotations);

        // Verify the correct calculation.
        assertEquals(33, this.dmlQuotation2.getRelativeStrengthData().getRsNumber());
        assertEquals(67, this.dmlQuotation3.getRelativeStrengthData().getRsNumber());
        assertEquals(100, this.dmlQuotation1.getRelativeStrengthData().getRsNumber());
    }

    /**
     * Tests the calculation of RS numbers: rsNumberDistance52WeekHigh.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testCalculateRsNumberDistanceTo52wHigh() {
        List<Quotation> quotations = new ArrayList<>();

        // Prepare all quotations on which the RS number is to be calculated.
        quotations.add(this.dmlQuotation1);
        quotations.add(this.dmlQuotation2);
        quotations.add(this.dmlQuotation3);

        // Calculate the RS numbers.
        this.relativeStrengthCalculator.calculateRsNumberDistanceTo52wHigh(quotations);

        // Verify the correct calculation.
        assertEquals(33, this.dmlQuotation3.getRelativeStrengthData().getRsNumberDistance52WeekHigh());
        assertEquals(67, this.dmlQuotation1.getRelativeStrengthData().getRsNumberDistance52WeekHigh());
        assertEquals(100, this.dmlQuotation2.getRelativeStrengthData().getRsNumberDistance52WeekHigh());
    }

    /**
     * Tests the calculation of RS numbers: rsNumberAccDisRatio.
     */
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public void testCalculateRsNumberAccDisRatio() {
        List<Quotation> quotations = new ArrayList<>();

        // Prepare all quotations on which the RS number is to be calculated.
        quotations.add(this.dmlQuotation1);
        quotations.add(this.dmlQuotation2);
        quotations.add(this.dmlQuotation3);

        // Calculate the RS numbers.
        this.relativeStrengthCalculator.calculateRsNumberAccDisRatio(quotations);

        // Verify the correct calculation.
        assertEquals(33, this.dmlQuotation2.getRelativeStrengthData().getRsNumberAccDisRatio());
        assertEquals(67, this.dmlQuotation3.getRelativeStrengthData().getRsNumberAccDisRatio());
        assertEquals(100, this.dmlQuotation1.getRelativeStrengthData().getRsNumberAccDisRatio());
    }
}
