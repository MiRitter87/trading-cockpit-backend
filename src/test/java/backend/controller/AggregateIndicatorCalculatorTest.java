package backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.statistic.Statistic;
import backend.webservice.common.QuotationServiceFixture;

/**
 * Tests the AggregateIndicatorCalculator.
 *
 * @author Michael
 */
public class AggregateIndicatorCalculatorTest {
    /**
     * The stock of the Uranium Industry Group.
     */
    private Instrument uraIndustryGroup;

    /**
     * Statistics of the Uranium industry group.
     */
    private List<Statistic> statistics;

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        this.createUraInstrument();
        this.createUraQuotations();
        this.createStatistics();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.uraIndustryGroup = null;
        this.statistics = null;
    }

    /**
     * Creates the Instrument of the Uranium Industry Group.
     */
    private void createUraInstrument() {
        this.uraIndustryGroup = new Instrument();
        this.uraIndustryGroup.setId(1);
        this.uraIndustryGroup.setSymbol("URA");
        this.uraIndustryGroup.setName("Uranium Industry Group");
        this.uraIndustryGroup.setType(InstrumentType.IND_GROUP);

    }

    /**
     * Creates quotations and adds them to the URA Instrument.
     */
    private void createUraQuotations() {
        QuotationServiceFixture quotationServiceFixture = new QuotationServiceFixture();
        List<Quotation> quotationsWithoutIndicators = quotationServiceFixture
                .getDenisonMinesQuotationsWithoutIndicators(this.uraIndustryGroup);

        this.uraIndustryGroup.setQuotations(quotationServiceFixture
                .getDenisonMinesQuotationsWithIndicators(this.uraIndustryGroup, quotationsWithoutIndicators));
    }

    /**
     * Creates statistics.
     */
    private void createStatistics() {
        List<Quotation> quotationsSortedByDate = this.uraIndustryGroup.getQuotationsSortedByDate();
        Statistic newStatistic;
        Quotation currentQuotation;

        this.statistics = new ArrayList<>();

        // Create 11 statistics based on the 11 newest quotations of the industry group.
        for (int i = 0; i <= 10; i++) {
            currentQuotation = quotationsSortedByDate.get(i);

            newStatistic = new Statistic();
            newStatistic.setDate(currentQuotation.getDate());
            newStatistic.setIndustryGroupId(this.uraIndustryGroup.getId());
            newStatistic.setNumberOfInstruments(20);
            newStatistic.setNumberAboveSma50(i);
            newStatistic.setNumberAtOrBelowSma50(20 - i);

            this.statistics.add(newStatistic);
        }
    }

    @Test
    /**
     * Tests the calculation of the Aggregate Indicator.
     */
    public void testGetAggregateIndicator() {
        AggregateIndicatorCalculator calculator = new AggregateIndicatorCalculator();
        int expectedAggregateIndicator;
        int actualAggregateIndicator;
        List<Quotation> quotations = this.uraIndustryGroup.getQuotationsSortedByDate();
        Quotation currentQuotation;

        currentQuotation = quotations.get(0); // 22.07.22
        expectedAggregateIndicator = 37;
        actualAggregateIndicator = calculator.getAggregateIndicator(quotations, this.statistics, currentQuotation,
                this.uraIndustryGroup);
        assertEquals(expectedAggregateIndicator, actualAggregateIndicator);

        currentQuotation = quotations.get(1); // 21.07.22
        expectedAggregateIndicator = 45;
        actualAggregateIndicator = calculator.getAggregateIndicator(quotations, this.statistics, currentQuotation,
                this.uraIndustryGroup);
        assertEquals(expectedAggregateIndicator, actualAggregateIndicator);
    }
}
