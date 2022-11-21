package backend.dao.statistic;

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
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		try {
			this.statisticToday = new Statistic();
			this.statisticToday.setInstrumentType(InstrumentType.STOCK);
			this.statisticToday.setDate(calendar.getTime());
			this.statisticToday.setNumberAdvance(34);
			this.statisticToday.setNumberDecline(134);
			this.statisticToday.setAdvanceDeclineSum(-100);
			
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			this.statisticYesterday = new Statistic();
			this.statisticYesterday.setInstrumentType(InstrumentType.STOCK);
			this.statisticYesterday.setDate(calendar.getTime());
			this.statisticYesterday.setNumberAdvance(101);
			this.statisticYesterday.setNumberDecline(67);
			this.statisticYesterday.setAdvanceDeclineSum(34);
			
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

	
	
	/*
	 * TODO Implement tests
	 * 
	 *  testCreateDuplicateStatistic (same date, same InstrumentType)
	 */
}
