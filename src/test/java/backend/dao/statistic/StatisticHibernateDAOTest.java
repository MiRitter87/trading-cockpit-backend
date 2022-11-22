package backend.dao.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import backend.dao.DAOManager;
import backend.dao.ObjectUnchangedException;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Tests the StatisticHibernateDAO.
 * 
 * @author Michael
 */
public class StatisticHibernateDAOTest {
	/**
	 * DAO to access Statistic data.
	 */
	private static StatisticDAO statisticDAO;
	
	/**
	 * Statistic of today.
	 */
	private Statistic statisticToday;
	
	/**
	 * Statistic of yesterday.
	 */
	private Statistic statisticYesterday;
	
	
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
	private void setUp() {
		this.createTestData();
	}
	
	
	@AfterEach
	/**
	 * Tasks to be performed after each test has been run.
	 */
	private void tearDown() {
		this.deleteTestData();
	}
	
	
	/**
	 * Initializes the database with the statistics.
	 */
	private void createTestData() {
		
		try {
			this.statisticToday = this.getStatisticToday();
			this.statisticYesterday = this.getStatisticYesterday();		
			
			statisticDAO.insertStatistic(this.statisticToday);
			statisticDAO.insertStatistic(this.statisticYesterday);		
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	
	/**
	 * Deletes the statistics from the database.
	 */
	private void deleteTestData() {
		try {
			statisticDAO.deleteStatistic(this.statisticToday);
			statisticDAO.deleteStatistic(this.statisticYesterday);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Gets the Statistic 'Today'.
	 * 
	 * @return The Statistic 'Today'.
	 */
	public Statistic getStatisticToday() {
		Statistic statistic;
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		statistic = new Statistic();
		statistic.setInstrumentType(InstrumentType.STOCK);
		statistic.setDate(calendar.getTime());
		statistic.setNumberAdvance(34);
		statistic.setNumberDecline(134);
		
		return statistic;
	}
	
	
	/**
	 * Gets the Statistic 'Yesterday'.
	 * 
	 * @return The Statistic 'Yesterday'.
	 */
	public Statistic getStatisticYesterday() {
		Statistic statistic;
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		statistic = new Statistic();
		statistic.setInstrumentType(InstrumentType.STOCK);
		statistic.setDate(calendar.getTime());
		statistic.setNumberAdvance(101);
		statistic.setNumberDecline(67);
		
		return statistic;
	}

	
	@Test
	/**
	 * Tests the retrieval of a Statistic with a given ID.
	 */
	public void testGetStatistic() {
		Statistic databaseStatistic;
		
		try {
			databaseStatistic = statisticDAO.getStatistic(this.statisticToday.getId());
			
			//Check the attributes of the database Statistic.
			assertEquals(this.statisticToday, databaseStatistic);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tests insertion of a Statistic.
	 */
	public void testInsertStatistic() {
		Calendar calendar = Calendar.getInstance();
		Statistic newStatistic = new Statistic();
		Statistic databaseStatistic;
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		
		newStatistic.setInstrumentType(InstrumentType.STOCK);
		newStatistic.setDate(calendar.getTime());
		newStatistic.setNumberAdvance(10);
		newStatistic.setNumberDecline(15);
		
		try {
			statisticDAO.insertStatistic(newStatistic);
			
			//Check if the Statistic has been correctly persisted.
			databaseStatistic = statisticDAO.getStatistic(newStatistic.getId());
			assertEquals(newStatistic, databaseStatistic);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Remove the Statistic to revert to the original database state.
			try {
				statisticDAO.deleteStatistic(newStatistic);
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
	
	
	@Test
	/**
	 * Tests deletion of a Statistic.
	 */
	public void testDeleteStatistic() {
		Statistic deletedStatistic;
		
		try {
			//Delete Statistic.
			statisticDAO.deleteStatistic(this.statisticToday);
			
			//Try to get the previously deleted Statistic.
			deletedStatistic = statisticDAO.getStatistic(this.statisticToday.getId());
			
			//Assure the Quotation does not exist anymore.
			assertNull(deletedStatistic);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Add the previously deleted Statistic to revert to the original database state.
			try {
				this.statisticToday.setId(null);
				statisticDAO.insertStatistic(this.statisticToday);
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
			//Change Statistic.
			statisticToday.setInstrumentType(InstrumentType.ETF);
			statisticDAO.updateStatistic(this.statisticToday);
			
			//Get the Statistic that should have been changed.
			databaseStatistic = statisticDAO.getStatistic(this.statisticToday.getId());
			
			//Check if the changes have been persisted.
			assertEquals(this.statisticToday, databaseStatistic);
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
			statisticDAO.updateStatistic(this.statisticToday);
			fail("The 'updateStatistic' method should have thrown an ObjectUnchangedException.");
		} catch (ObjectUnchangedException expected) {
			//All is well.
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	/*
	 * TODO Implement tests
	 * 
	 *  testCreateDuplicateStatistic (same date, same InstrumentType)
	 */
}
