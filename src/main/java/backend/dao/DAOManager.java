package backend.dao;

import java.io.Closeable;
import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import backend.dao.chart.ChartObjectDAO;
import backend.dao.chart.ChartObjectHibernateDAO;
import backend.dao.instrument.InstrumentDAO;
import backend.dao.instrument.InstrumentHibernateDAO;
import backend.dao.list.ListDAO;
import backend.dao.list.ListHibernateDAO;
import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertHibernateDAO;
import backend.dao.quotation.persistence.QuotationDAO;
import backend.dao.quotation.persistence.QuotationHibernateDAO;
import backend.dao.scan.ScanDAO;
import backend.dao.scan.ScanHibernateDAO;
import backend.dao.statistic.StatisticDAO;
import backend.dao.statistic.StatisticHibernateDAO;

/**
 * Manages a central database connection and provides DAOs for database access.
 * 
 * @author Michael
 */
public class DAOManager implements Closeable {
	/**
	 * Instance of this class.
	 */
	private static DAOManager instance;
	
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	/**
	 * DAO to manage price alert data.
	 */
	private PriceAlertDAO priceAlertDAO;
	
	/**
	 * DAO to access instruments.
	 */
	private InstrumentDAO instrumentDAO;
	
	/**
	 * DAO to access quotations.
	 */
	private QuotationDAO quotationDAO;
	
	/**
	 * DAO to access lists.
	 */
	private ListDAO listDAO;
	
	/**
	 * DAO to access scans.
	 */
	private ScanDAO scanDAO;
	
	/**
	 * DAO to access statistics.
	 */
	private StatisticDAO statisticDAO;
	
	/**
	 * DAO to access chart objects.
	 */
	private ChartObjectDAO chartObjectDAO;
	
	
	/**
	 * Initializes the DAOManager.
	 */
	private DAOManager() {
		this.sessionFactory = this.getSessionFactory();
	}
	
	
	/**
	 * Provides the instance of the DAOManager.
	 * 
	 * @return The instance of the DAOManager.
	 */
	public static DAOManager getInstance() {
		if(instance == null)
			instance = new DAOManager();
		
		return instance;
	}
	
	
	/**
	 * Builds a session factory for database access.
	 * 
	 * @return Session factory for database access.
	 */
	private EntityManagerFactory getSessionFactory() {
		//The given string must match with the persistence unit defined in the persistence.xml file.
		return Persistence.createEntityManagerFactory("my-persistence-unit");
	}
	
	
	/**
	 * Returns a DAO to manage PriceAlert data.
	 * 
	 * @return The PriceAlertDAO.
	 */
	public PriceAlertDAO getPriceAlertDAO() {
		if(this.priceAlertDAO == null)
			this.priceAlertDAO = new PriceAlertHibernateDAO(this.sessionFactory);
		
		return this.priceAlertDAO;
	}
	
	
	/**
	 * Returns a DAO to access Instrument data.
	 * 
	 * @return The InstrumentDAO.
	 */
	public InstrumentDAO getInstrumentDAO() {
		if(this.instrumentDAO == null)
			this.instrumentDAO = new InstrumentHibernateDAO(this.sessionFactory);
		
		return this.instrumentDAO;
	}
	
	
	/**
	 * Returns a DAO to access Quotation data.
	 * 
	 * @return The InstrumentDAO.
	 */
	public QuotationDAO getQuotationDAO() {
		if(this.quotationDAO == null)
			this.quotationDAO = new QuotationHibernateDAO(this.sessionFactory);
		
		return this.quotationDAO;
	}
	
	
	/**
	 * Returns a DAO to access List data.
	 * 
	 * @return The ListDAO.
	 */
	public ListDAO getListDAO() {
		if(this.listDAO == null)
			this.listDAO = new ListHibernateDAO(this.sessionFactory);
		
		return this.listDAO;
	}
	
	
	/**
	 * Returns a DAO to access Scan data.
	 * 
	 * @return The ScanDAO.
	 */
	public ScanDAO getScanDAO() {
		if(this.scanDAO == null)
			this.scanDAO = new ScanHibernateDAO(this.sessionFactory);
		
		return this.scanDAO;
	}
	
	
	/**
	 * Returns a DAO to access Statistic data.
	 * 
	 * @return The StatisticDAO.
	 */
	public StatisticDAO getStatisticDAO() {
		if(this.statisticDAO == null)
			this.statisticDAO = new StatisticHibernateDAO(this.sessionFactory);
		
		return this.statisticDAO;
	}
	
	
	/**
	 * Returns a DAO to access chart object data.
	 * 
	 * @return The ChartObjectDAO.
	 */
	public ChartObjectDAO getChartObjectDAO() {
		if(this.chartObjectDAO == null)
			this.chartObjectDAO = new ChartObjectHibernateDAO(this.sessionFactory);
		
		return this.chartObjectDAO;
	}

	
	@Override
	public void close() throws IOException {
		try {
			this.sessionFactory.close();
			instance = null;
		}
		catch(IllegalStateException exception) {
			throw new IOException(exception.getMessage());
		}
	}
}
