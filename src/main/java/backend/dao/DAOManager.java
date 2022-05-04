package backend.dao;

import java.io.Closeable;
import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import backend.dao.priceAlert.PriceAlertDAO;
import backend.dao.priceAlert.PriceAlertHibernateDAO;
import backend.dao.stockQuote.StockQuoteDAO;
import backend.dao.stockQuote.StockQuoteYahooDAO;

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
	 * DAO to access stock quotes.
	 */
	private StockQuoteDAO stockQuoteDAO;
	
	
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
	 * Returns a DAO to manage price alert data.
	 * 
	 * @return The PriceAlertDAO.
	 */
	public PriceAlertDAO getPriceAlertDAO() {
		if(this.priceAlertDAO == null)
			this.priceAlertDAO = new PriceAlertHibernateDAO(this.sessionFactory);
		
		return this.priceAlertDAO;
	}
	
	
	/**
	 * Returns a DAO to access stock quote data.
	 * 
	 * @return The StockQuoteDAO.
	 */
	public StockQuoteDAO getStockQuoteDAO() {
		if(this.stockQuoteDAO == null)
			this.stockQuoteDAO = new StockQuoteYahooDAO();
		
		return this.stockQuoteDAO;
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
