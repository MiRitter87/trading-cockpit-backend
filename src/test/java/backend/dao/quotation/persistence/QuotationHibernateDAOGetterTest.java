package backend.dao.quotation.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.instrument.DuplicateInstrumentException;
import backend.dao.instrument.InstrumentDAO;
import backend.model.instrument.Indicator;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.RelativeStrengthData;
import backend.webservice.ScanTemplate;

/**
 * Tests getter methods of the QuotationHibernateDAO.
 *
 * @author MiRitter87
 */
public class QuotationHibernateDAOGetterTest {
    /**
     * DAO to access Quotation data.
     */
    private static QuotationDAO quotationDAO;

    /**
     * DAO to access Instrument data.
     */
    private static InstrumentDAO instrumentDAO;

    /**
     * Class providing helper methods for fixture.
     */
    private QuotationHibernateDAOFixture fixtureHelper;

    /**
     * The stock of Apple.
     */
    private Instrument appleStock;

    /**
     * The stock of Microsoft.
     */
    private Instrument microsoftStock;

    /**
     * The ETF XLE.
     */
    private Instrument xleETF;

    /**
     * The Industrials sector.
     */
    private Instrument xliSector;

    /**
     * The copper industry group.
     */
    private Instrument copperIndustryGroup;

    /**
     * The first Quotation of the Apple stock.
     */
    private Quotation appleQuotation1;

    /**
     * The second Quotation of the Apple stock.
     */
    private Quotation appleQuotation2;

    /**
     * The first Quotation of the Microsoft stock.
     */
    private Quotation microsoftQuotation1;

    /**
     * The first Quotation of the XLE ETF.
     */
    private Quotation xleQuotation1;

    /**
     * The first Quotation of the XLI sector.
     */
    private Quotation xliSectorQuotation1;

    /**
     * The first Quotation of the copper industry group.
     */
    private Quotation copperIndustryGroupQuotation1;

    /**
     * Indicator of the Second Quotation of the Apple stock.
     */
    private Indicator appleQuotation2Indicator;

    /**
     * Indicator of the first Quotation of the XLE ETF.
     */
    private Indicator xleQuotation1Indicator;

    /**
     * Indicator of the first Quotation of the XLI sector.
     */
    private Indicator xliSectorQuotation1Indicator;

    /**
     * Indicator of the first Quotation of the copper industry group.
     */
    private Indicator copperIndustryGroupQuotation1Indicator;

    /**
     * Tasks to be performed once at startup of test class.
     */
    @BeforeAll
    public static void setUpClass() {
        instrumentDAO = DAOManager.getInstance().getInstrumentDAO();
        quotationDAO = DAOManager.getInstance().getQuotationDAO();
    }

    /**
     * Tasks to be performed once at end of test class.
     */
    @AfterAll
    public static void tearDownClass() {
        try {
            DAOManager.getInstance().close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tasks to be performed before each test is run.
     */
    @BeforeEach
    public void setUp() {
        this.fixtureHelper = new QuotationHibernateDAOFixture();
        this.createTestData();
    }

    /**
     * Tasks to be performed after each test has been run.
     */
    @AfterEach
    public void tearDown() {
        this.deleteTestData();
        this.fixtureHelper = null;
    }

    /**
     * Initializes the database with instruments, quotations and indicators used as test data.
     */
    private void createTestData() {
        this.createInstruments();
        this.createQuotations();
        this.createRelativeStrengthData();
        this.createIndicators();
    }

    /**
     * Creates Instrument data.
     */
    private void createInstruments() {
        this.xleETF = this.fixtureHelper.getXleEtf();
        this.xliSector = this.fixtureHelper.getXliSector();
        this.copperIndustryGroup = this.fixtureHelper.getCopperIndustryGroup();
        this.microsoftStock = this.fixtureHelper.getMicrosoftStock();

        this.appleStock = this.fixtureHelper.getAppleStock();
        this.appleStock.setSector(this.xliSector);
        this.appleStock.setIndustryGroup(this.copperIndustryGroup);

        try {

            instrumentDAO.insertInstrument(this.xleETF);
            instrumentDAO.insertInstrument(this.xliSector);
            instrumentDAO.insertInstrument(this.copperIndustryGroup);
            instrumentDAO.insertInstrument(this.microsoftStock);
            instrumentDAO.insertInstrument(this.appleStock);
        } catch (DuplicateInstrumentException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates Quotation data.
     */
    private void createQuotations() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.microsoftQuotation1 = this.fixtureHelper.getMicrosoftQuotation1(this.microsoftStock);
            quotations.add(this.microsoftQuotation1);

            this.appleQuotation1 = this.fixtureHelper.getAppleQuotation1(this.appleStock);
            quotations.add(this.appleQuotation1);

            this.appleQuotation2 = this.fixtureHelper.getAppleQuotation2(this.appleStock);
            quotations.add(this.appleQuotation2);

            this.xleQuotation1 = this.fixtureHelper.getXleQuotation1(this.xleETF);
            quotations.add(this.xleQuotation1);

            this.xliSectorQuotation1 = this.fixtureHelper.getXliSectorQuotation1(this.xliSector);
            quotations.add(this.xliSectorQuotation1);

            this.copperIndustryGroupQuotation1 = this.fixtureHelper
                    .getCopperIndustryGroupQuotation1(this.copperIndustryGroup);
            quotations.add(this.copperIndustryGroupQuotation1);

            quotationDAO.insertQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates Indicator data.
     */
    private void createIndicators() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation2Indicator = this.fixtureHelper.getAppleQuotation2Indicator();
            this.appleQuotation2.setIndicator(this.appleQuotation2Indicator);
            quotations.add(this.appleQuotation2);

            this.xleQuotation1Indicator = this.fixtureHelper.getXleQuotation1Indicator();
            this.xleQuotation1.setIndicator(this.xleQuotation1Indicator);
            quotations.add(this.xleQuotation1);

            this.xliSectorQuotation1Indicator = this.fixtureHelper.getXliSectorQuotation1Indicator();
            this.xliSectorQuotation1.setIndicator(this.xliSectorQuotation1Indicator);
            quotations.add(this.xliSectorQuotation1);

            this.copperIndustryGroupQuotation1Indicator = this.fixtureHelper
                    .getCopperIndustryGroupQuotation1Indicator();
            this.copperIndustryGroupQuotation1.setIndicator(this.copperIndustryGroupQuotation1Indicator);
            quotations.add(this.copperIndustryGroupQuotation1);

            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Creates relative strength data.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void createRelativeStrengthData() {
        List<Quotation> quotations = new ArrayList<>();

        try {
            this.appleQuotation2.setRelativeStrengthData(new RelativeStrengthData());
            this.appleQuotation2.getRelativeStrengthData().setRsNumber(24);
            this.appleQuotation2.getRelativeStrengthData().setRsNumberDistance52WeekHigh(87);
            this.appleQuotation2.getRelativeStrengthData().setRsNumberAccDisRatio(54);
            quotations.add(this.appleQuotation2);

            this.xliSectorQuotation1.setRelativeStrengthData(new RelativeStrengthData());
            this.xliSectorQuotation1.getRelativeStrengthData().setRsNumber(46);
            quotations.add(this.xliSectorQuotation1);

            this.copperIndustryGroupQuotation1.setRelativeStrengthData(new RelativeStrengthData());
            this.copperIndustryGroupQuotation1.getRelativeStrengthData().setRsNumber(12);
            quotations.add(this.copperIndustryGroupQuotation1);

            quotationDAO.updateQuotations(quotations);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the instruments, quotations and indicators used as test data from the database.
     */
    private void deleteTestData() {
        try {
            List<Quotation> quotations = new ArrayList<>();

            quotations.add(this.appleQuotation1);
            quotations.add(this.appleQuotation2);
            quotations.add(this.microsoftQuotation1);
            quotations.add(this.xleQuotation1);
            quotations.add(this.xliSectorQuotation1);
            quotations.add(this.copperIndustryGroupQuotation1);
            quotationDAO.deleteQuotations(quotations);

            instrumentDAO.deleteInstrument(this.appleStock);
            instrumentDAO.deleteInstrument(this.microsoftStock);
            instrumentDAO.deleteInstrument(this.copperIndustryGroup);
            instrumentDAO.deleteInstrument(this.xliSector);
            instrumentDAO.deleteInstrument(this.xleETF);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of a Quotation with a given ID.
     */
    @Test
    public void testGetQuotation() {
        Quotation databaseQuotation;

        try {
            databaseQuotation = quotationDAO.getQuotation(this.appleQuotation2.getId());

            // Check the attributes of the database Quotation.
            assertEquals(databaseQuotation.getId(), this.appleQuotation2.getId());
            assertEquals(databaseQuotation.getDate().getTime(), this.appleQuotation2.getDate().getTime());
            assertTrue(databaseQuotation.getClose().compareTo(this.appleQuotation2.getClose()) == 0);
            assertEquals(databaseQuotation.getCurrency(), this.appleQuotation2.getCurrency());
            assertEquals(databaseQuotation.getVolume(), this.appleQuotation2.getVolume());
            assertEquals(databaseQuotation.getInstrument().getId(), this.appleQuotation2.getInstrument().getId());

            assertNotNull(databaseQuotation.getIndicator());
            assertEquals(this.appleQuotation2Indicator.getBaseLengthWeeks(),
                    databaseQuotation.getIndicator().getBaseLengthWeeks());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of all quotations of an Instrument.
     */
    @Test
    public void testGetQuotationsOfInstrument() {
        List<Quotation> quotations;

        try {
            quotations = quotationDAO.getQuotationsOfInstrument(this.appleStock.getId());

            for (Quotation databaseQuotation : quotations) {
                if (databaseQuotation.getId().equals(appleQuotation1.getId())) {
                    assertEquals(this.appleQuotation1, databaseQuotation);
                } else if (databaseQuotation.getId().equals(this.appleQuotation2.getId())) {
                    assertEquals(this.appleQuotation2, databaseQuotation);
                } else {
                    fail("The method 'getQuotationsOfInstrument' has returned an unrelated quotation.");
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the most recent Quotation for each Instrument of a List.
     */
    @Test
    public void testGetRecentQuotationsForList() {
        backend.model.list.List list = new backend.model.list.List();
        List<Quotation> quotations;

        list.addInstrument(this.appleStock);
        list.addInstrument(this.microsoftStock);

        try {
            quotations = quotationDAO.getRecentQuotationsForList(list);

            // Assure one quotation for each Instrument is provided.
            assertEquals(2, quotations.size());

            // Assure the correct quotations are provided.
            for (Quotation databaseQuotation : quotations) {
                if (databaseQuotation.getId().equals(microsoftQuotation1.getId())) {
                    assertEquals(this.microsoftQuotation1, databaseQuotation);
                } else if (databaseQuotation.getId().equals(this.appleQuotation2.getId())) {
                    assertEquals(this.appleQuotation2, databaseQuotation);
                } else {
                    fail("The method 'getRecentQuotationsForList' has returned an unrelated quotation.");
                }
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the most recent Quotation of each Instrument of type STOCK.
     */
    @Test
    public void testGetRecentQuotationsTypeStock() {
        List<Quotation> quotations;
        Quotation databaseQuotation;

        try {
            quotations = quotationDAO.getRecentQuotations(InstrumentType.STOCK);

            // Assure one Quotation is returned.
            assertEquals(1, quotations.size());

            // Assure the correct Quotation is provided.
            databaseQuotation = quotations.get(0);
            assertEquals(this.appleQuotation2, databaseQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the most recent Quotation of each Instrument of type ETF.
     */
    @Test
    public void testGetRecentQuotationsTypeEtf() {
        List<Quotation> quotations;
        Quotation databaseQuotation;

        try {
            quotations = quotationDAO.getRecentQuotations(InstrumentType.ETF);

            // Assure one Quotation is returned.
            assertEquals(1, quotations.size());

            // Assure the correct Quotation is provided.
            databaseQuotation = quotations.get(0);
            assertEquals(this.xleQuotation1, databaseQuotation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the retrieval of the most recent Quotation of each Instrument of type STOCK. Especially the filling of
     * transient attributes is tested here.
     */
    @Test
    public void testGetQuotationsByTemplate() {
        List<Quotation> quotations;
        Quotation databaseQuotation;
        int expectedCompositeRsNumberIg;
        final int numComponents = 5;

        try {
            quotations = quotationDAO.getQuotationsByTemplate(ScanTemplate.ALL, InstrumentType.STOCK, null, null, null);

            // Assure one Quotation is returned.
            assertEquals(1, quotations.size());

            // Assure the correct Quotation is provided.
            databaseQuotation = quotations.get(0);
            assertEquals(this.appleQuotation2, databaseQuotation);

            // Assure the RS number of the sector and industry group is provided. These are transient attributes.
            assertEquals(this.xliSectorQuotation1.getRelativeStrengthData().getRsNumber(),
                    databaseQuotation.getRelativeStrengthData().getRsNumberSector());
            assertEquals(this.copperIndustryGroupQuotation1.getRelativeStrengthData().getRsNumber(),
                    databaseQuotation.getRelativeStrengthData().getRsNumberIndustryGroup());

            // Assure the composite RS number based on an Instrument and its industry group is provided.
            expectedCompositeRsNumberIg = this.appleQuotation2.getRelativeStrengthData().getRsNumber() * 2;
            expectedCompositeRsNumberIg += this.copperIndustryGroupQuotation1.getRelativeStrengthData().getRsNumber();
            expectedCompositeRsNumberIg += this.appleQuotation2.getRelativeStrengthData()
                    .getRsNumberDistance52WeekHigh();
            expectedCompositeRsNumberIg += this.appleQuotation2.getRelativeStrengthData().getRsNumberAccDisRatio();
            expectedCompositeRsNumberIg = (int) Math.ceil((double) expectedCompositeRsNumberIg / numComponents);

            assertEquals(expectedCompositeRsNumberIg,
                    databaseQuotation.getRelativeStrengthData().getRsNumberCompositeIg());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
