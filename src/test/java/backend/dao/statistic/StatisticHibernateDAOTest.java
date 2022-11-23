package backend.dao.statistic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	 * Statistic of today for stocks.
	 */
	private Statistic statisticTodayStock;
	
	/**
	 * Statistic of yesterday for stocks.
	 */
	private Statistic statisticYesterdayStock;
	
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
			this.statisticTodayStock = this.getStatisticTodayStock();
			this.statisticYesterdayStock = this.getStatisticYesterdayStock();	
			this.statisticTodayETF = this.getStatisticTodayETF();
			
			statisticDAO.insertStatistic(this.statisticTodayStock);
			statisticDAO.insertStatistic(this.statisticYesterdayStock);		
			statisticDAO.insertStatistic(this.statisticTodayETF);
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}
	
	
	/**
	 * Deletes the statistics from the database.
	 */
	private void deleteTestData() {
		try {
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
	 * Gets the Statistic 'Yesterday' for stocks.
	 * 
	 * @return The Statistic 'Yesterday'.
	 */
	public Statistic getStatisticYesterdayStock() {
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
	
	
	/**
	 * Gets the Statistic 'Today' for ETFs.
	 * 
	 * @return The Statistic 'Today'.
	 */
	public Statistic getStatisticTodayETF() {
		Statistic statistic;
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		statistic = new Statistic();
		statistic.setInstrumentType(InstrumentType.ETF);
		statistic.setDate(calendar.getTime());
		statistic.setNumberAdvance(3);
		statistic.setNumberDecline(2);
		
		return statistic;
	}

	
	@Test
	/**
	 * Tests the retrieval of a Statistic with a given ID.
	 */
	public void testGetStatistic() {
		Statistic databaseStatistic;
		
		try {
			databaseStatistic = statisticDAO.getStatistic(this.statisticTodayStock.getId());
			
			//Check the attributes of the database Statistic.
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
	 * Tests the retrieval of all statistics of type STOCK.
	 */
	public void testGetStatisticsTypeStock() {
		List<Statistic> statistics;
		
		try {
			statistics = statisticDAO.getStatistics(InstrumentType.STOCK);
			
			//Assure two statistics are returned.
			assertEquals(2, statistics.size());
			
			//Assure that the correct statistics are returned in the correct order (newest Statistic first).
			assertEquals(this.statisticTodayStock, statistics.get(0));
			assertEquals(this.statisticYesterdayStock, statistics.get(1));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	/**
	 * Tries to insert a Statistic of a certain type and date for which a Statistic already exists.
	 */
	public void testInsertDuplicateStatistic() {
		Calendar calendar = Calendar.getInstance();
		Statistic newStatistic = new Statistic();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		newStatistic.setInstrumentType(InstrumentType.STOCK);
		newStatistic.setDate(calendar.getTime());
		newStatistic.setNumberAdvance(10);
		newStatistic.setNumberDecline(5);
		
		try {
			statisticDAO.insertStatistic(newStatistic);
			fail("The 'insertStatistic' method should have thrown a DuplicateStatisticException.");
		} 
		catch(DuplicateStatisticException expected) {
			//All is welll.
		}
		catch (Exception e) {
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
			//Delete Statistic.
			statisticDAO.deleteStatistic(this.statisticTodayStock);
			
			//Try to get the previously deleted Statistic.
			deletedStatistic = statisticDAO.getStatistic(this.statisticTodayStock.getId());
			
			//Assure the Quotation does not exist anymore.
			assertNull(deletedStatistic);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			//Add the previously deleted Statistic to revert to the original database state.
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
			//Change Statistic.
			statisticTodayStock.setInstrumentType(InstrumentType.ETF);
			statisticDAO.updateStatistic(this.statisticTodayStock);
			
			//Get the Statistic that should have been changed.
			databaseStatistic = statisticDAO.getStatistic(this.statisticTodayStock.getId());
			
			//Check if the changes have been persisted.
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
			//All is well.
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
