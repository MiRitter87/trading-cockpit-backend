package backend.dao;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import backend.exception.ObjectUnchangedException;
import backend.model.PriceAlert;

/**
 * Provides access to price alert database persistence using Hibernate.
 * 
 * @author Michael
 */
public class PriceAlertHibernateDAO implements PriceAlertDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public PriceAlertHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	@Override
	public void insertPriceAlert(PriceAlert priceAlert) throws Exception {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void deleteAccount(PriceAlert priceAlert) throws Exception {
		// TODO Auto-generated method stub
	}

	
	@Override
	public List<PriceAlert> getPriceAlerts() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public PriceAlert getPriceAlert(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void updatePriceAlert(PriceAlert priceAlert) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub
	}
}
