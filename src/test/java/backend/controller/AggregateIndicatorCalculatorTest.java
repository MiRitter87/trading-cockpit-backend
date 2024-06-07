package backend.controller;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import backend.dao.DAOManager;
import backend.dao.instrument.InstrumentDAO;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
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

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    private void setUp() {
        this.createUraInstrument();
        this.createUraQuotations();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    private void tearDown() {
        this.deleteUraInstrument();
    }

    /**
     * Creates the Instrument of the Uranium Industry Group.
     */
    private void createUraInstrument() {
        this.uraIndustryGroup = new Instrument();
        this.uraIndustryGroup.setSymbol("URA");
        this.uraIndustryGroup.setName("Uranium Industry Group");
        this.uraIndustryGroup.setType(InstrumentType.IND_GROUP);

    }

    /**
     * Deletes the Instrument of the Uranium Industry Group.
     */
    private void deleteUraInstrument() {
        this.uraIndustryGroup = null;
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
}
