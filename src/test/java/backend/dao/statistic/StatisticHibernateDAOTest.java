package backend.dao.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.model.LocalizedException;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Tests the StatisticHibernateDAO.
 *
 * @author Michael
 */
public class StatisticHibernateDAOTest {
    /**
     * Access to localized application resources.
     */
    private ResourceBundle resources = ResourceBundle.getBundle("backend");

    /**
     * DAO to access Statistic data.
     */
    private static StatisticDAO statisticDAO;

    /**
     * Statistic of today for stocks.
     */
    private Statistic statisticTodayStock;

    /**
     * Statistic of yesterday for stocks.
     */
    private Statistic statisticYesterdayStock;

    /**
     * Statistic with relation to an industry group.
     */
    private Statistic statisticWithIgRelation;

    /**
     * Statistic with relation to a sector.
     */
    private Statistic statisticWithSectorRelation;

    /**
     * Statistic of today for ETFs.
     */
    private Statistic statisticTodayETF;

    @BeforeAll
    /**
     * Tasks to be performed once at startup of test class.
     */
    public static void setUpClass() {
        statisticDAO = DAOManager.getInstance().getStatisticDAO();
    }

    @AfterAll
    /**
     * Tasks to be performed once at end of test class.
     */
    public static void tearDownClass() {
        try {
            DAOManager.getInstance().close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @BeforeEach
    /**
     * Tasks to be performed before each test is run.
     */
    public void setUp() {
        this.createTestData();
    }

    @AfterEach
    /**
     * Tasks to be performed after each test has been run.
     */
    public void tearDown() {
        this.deleteTestData();
    }

    /**
     * Initializes the database with the statistics.
     */
    private void createTestData() {

        try {
            this.statisticTodayStock = this.getStatisticTodayStock();
            this.statisticYesterdayStock = this.getStatisticYesterdayStock();
            this.statisticTodayETF = this.getStatisticTodayETF();
            this.statisticWithIgRelation = this.getStatisticWithIgRelation();
            this.statisticWithSectorRelation = this.getStatisticWithSectorRelation();

            statisticDAO.insertStatistic(this.statisticTodayStock);
            statisticDAO.insertStatistic(this.statisticYesterdayStock);
            statisticDAO.insertStatistic(this.statisticTodayETF);
            statisticDAO.insertStatistic(this.statisticWithIgRelation);
            statisticDAO.insertStatistic(this.statisticWithSectorRelation);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Deletes the statistics from the database.
     */
    private void deleteTestData() {
        try {
            statisticDAO.deleteStatistic(this.statisticWithSectorRelation);
            statisticDAO.deleteStatistic(this.statisticWithIgRelation);
            statisticDAO.deleteStatistic(this.statisticTodayETF);
            statisticDAO.deleteStatistic(this.statisticYesterdayStock);
            statisticDAO.deleteStatistic(this.statisticTodayStock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Gets the Statistic 'Today' for stocks.
     *
     * @return The Statistic 'Today'.
     */
    public Statistic getStatisticTodayStock() {
        Statistic statistic;
        Calendar calendar = this.getCalendarNow();

        statistic = new Statistic();
        statistic.setInstrumentType(InstrumentType.STOCK);
        statistic.setDate(calendar.getTime());
        statistic.setNumberAdvance(34);
        statistic.setNumberDecline(134);

        return statistic;
    }

    /**
     * Gets the Statistic 'Yesterday' for stocks.
     *
     * @return The Statistic 'Yesterday'.
     */
    public Statistic getStatisticYesterdayStock() {
        Statistic statistic;
        Calendar calendar = this.getCalendarNow();

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        statistic = new Statistic();
        statistic.setInstrumentType(InstrumentType.STOCK);
        statistic.setDate(calendar.getTime());
        statistic.setNumberAdvance(101);
        statistic.setNumberDecline(67);

        return statistic;
    }

    /**
     * Gets the Statistic 'Today' for ETFs.
     *
     * @return The Statistic 'Today'.
     */
    public Statistic getStatisticTodayETF() {
        Statistic statistic;
        Calendar calendar = this.getCalendarNow();

        statistic = new Statistic();
        statistic.setInstrumentType(InstrumentType.ETF);
        statistic.setDate(calendar.getTime());
        statistic.setNumberAdvance(3);
        statistic.setNumberDecline(2);

        return statistic;
    }

    /**
     * Gets the Statistic 'Today' with an industry group relation.
     *
     * @return The Statistic 'Today'.
     */
    public Statistic getStatisticWithIgRelation() {
        Statistic statistic;
        Calendar calendar = this.getCalendarNow();

        statistic = new Statistic();
        statistic.setInstrumentType(InstrumentType.STOCK);
        statistic.setDate(calendar.getTime());
        statistic.setIndustryGroupId(1);
        statistic.setNumberAdvance(1);
        statistic.setNumberDecline(0);

        return statistic;
    }

    /**
     * Gets the Statistic 'Today' with a sector relation.
     *
     * @return The Statistic 'Today'.
     */
    public Statistic getStatisticWithSectorRelation() {
        Statistic statistic;
        Calendar calendar = this.getCalendarNow();

        statistic = new Statistic();
        statistic.setInstrumentType(InstrumentType.STOCK);
        statistic.setDate(calendar.getTime());
        statistic.setSectorId(2);
        statistic.setNumberAdvance(1);
        statistic.setNumberDecline(1);

        return statistic;
    }

    /**
     * Creates a Calendar instance of the current day with intraday attributes of zero.
     *
     * @return A Calendar instance.
     */
    private Calendar getCalendarNow() {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    @Test
    /**
     * Tests the retrieval of a Statistic with a given ID.
     */
    public void testGetStatistic() {
        Statistic databaseStatistic;

        try {
            databaseStatistic = statisticDAO.getStatistic(this.statisticTodayStock.getId());

            // Check the attributes of the database Statistic.
            assertEquals(this.statisticTodayStock, databaseStatistic);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests insertion of a Statistic.
     */
    public void testInsertStatistic() {
        Calendar calendar = this.getCalendarNow();
        Statistic newStatistic = new Statistic();
        Statistic databaseStatistic;

        calendar.add(Calendar.DAY_OF_MONTH, -2);

        newStatistic.setInstrumentType(InstrumentType.STOCK);
        newStatistic.setDate(calendar.getTime());
        newStatistic.setNumberAdvance(10);
        newStatistic.setNumberDecline(15);

        try {
            statisticDAO.insertStatistic(newStatistic);

            // Check if the Statistic has been correctly persisted.
            databaseStatistic = statisticDAO.getStatistic(newStatistic.getId());
            assertEquals(newStatistic, databaseStatistic);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Remove the Statistic to revert to the original database state.
            try {
                statisticDAO.deleteStatistic(newStatistic);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests the retrieval of all statistics of type STOCK.
     */
    public void testGetAllStatisticsTypeStock() {
        List<Statistic> statistics;

        try {
            statistics = statisticDAO.getStatisticsOfInstrumentType(InstrumentType.STOCK);

            // Assure four statistics are returned.
            assertEquals(4, statistics.size());

            // Assure that the correct statistics are returned in the correct order (newest Statistic first).
            assertEquals(this.statisticYesterdayStock, statistics.get(3));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of all general statistics of type STOCK.
     */
    public void testGetStatisticsStockGeneral() {
        List<Statistic> statistics;

        try {
            statistics = statisticDAO.getStatistics(InstrumentType.STOCK, null, null);

            // Assure two statistics are returned.
            assertEquals(2, statistics.size());

            // Assure that the correct statistics are returned in the correct order (newest Statistic first).
            assertEquals(this.statisticTodayStock, statistics.get(0));
            assertEquals(this.statisticYesterdayStock, statistics.get(1));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of all industry group related statistics of type STOCK.
     */
    public void testGetStatisticsStockIndustryGroup() {
        List<Statistic> statistics;

        try {
            statistics = statisticDAO.getStatistics(InstrumentType.STOCK, null, 1);

            // Assure two statistics are returned.
            assertEquals(1, statistics.size());

            // Assure that the correct statistics are returned in the correct order (newest Statistic first).
            assertEquals(this.statisticWithIgRelation, statistics.get(0));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of all sector-related statistics of type STOCK.
     */
    public void testGetStatisticsStockSector() {
        List<Statistic> statistics;

        try {
            statistics = statisticDAO.getStatistics(InstrumentType.STOCK, 2, null);

            // Assure two statistics are returned.
            assertEquals(1, statistics.size());

            // Assure that the correct statistics are returned in the correct order (newest Statistic first).
            assertEquals(this.statisticWithSectorRelation, statistics.get(0));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests the retrieval of all statistics using the ids of both sector and industry group. An Exception is expected
     * because a statistic can either be related to a sector, industry group or none of both.
     */
    public void testGetStatisticsWithBothIds() {
        String expectedMessage = this.resources.getString("statistic.errorOnSectorAndIgRequested");
        String actualMessage = "";

        try {
            statisticDAO.getStatistics(InstrumentType.STOCK, 2, 1);
            fail("The 'getStatistics' method should have thrown a LocalizedException");
        } catch (LocalizedException expected) {
            actualMessage = expected.getLocalizedMessage();
            assertEquals(expectedMessage, actualMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tries to insert a Statistic of a certain type and date for which a Statistic already exists.
     */
    public void testInsertDuplicateStatistic() {
        Calendar calendar = this.getCalendarNow();
        Statistic newStatistic = new Statistic();

        newStatistic.setInstrumentType(InstrumentType.STOCK);
        newStatistic.setDate(calendar.getTime());
        newStatistic.setNumberAdvance(10);
        newStatistic.setNumberDecline(5);

        try {
            statisticDAO.insertStatistic(newStatistic);
            fail("The 'insertStatistic' method should have thrown a DuplicateStatisticException.");
        } catch (DuplicateStatisticException expected) {
            // All is well.
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests deletion of a Statistic.
     */
    public void testDeleteStatistic() {
        Statistic deletedStatistic;

        try {
            // Delete Statistic.
            statisticDAO.deleteStatistic(this.statisticTodayStock);

            // Try to get the previously deleted Statistic.
            deletedStatistic = statisticDAO.getStatistic(this.statisticTodayStock.getId());

            // Assure the Quotation does not exist anymore.
            assertNull(deletedStatistic);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            // Add the previously deleted Statistic to revert to the original database state.
            try {
                this.statisticTodayStock.setId(null);
                statisticDAO.insertStatistic(this.statisticTodayStock);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    /**
     * Tests updating of a Statistic.
     */
    public void testUpdateStatistic() {
        Statistic databaseStatistic;

        try {
            // Change Statistic.
            statisticTodayStock.setInstrumentType(InstrumentType.ETF);
            statisticDAO.updateStatistic(this.statisticTodayStock);

            // Get the Statistic that should have been changed.
            databaseStatistic = statisticDAO.getStatistic(this.statisticTodayStock.getId());

            // Check if the changes have been persisted.
            assertEquals(this.statisticTodayStock, databaseStatistic);
        } catch (ObjectUnchangedException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    /**
     * Tests updating of a Statistic without changing any data.
     */
    public void testUpdateUnchangedStatistic() {
        try {
            statisticDAO.updateStatistic(this.statisticTodayStock);
            fail("The 'updateStatistic' method should have thrown an ObjectUnchangedException.");
        } catch (ObjectUnchangedException expected) {
            // All is well.
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
