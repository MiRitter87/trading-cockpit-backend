package backend.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.exception.ObjectUnchangedException;
import backend.model.priceAlert.PriceAlert;

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
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(priceAlert);
			entityManager.flush();	//Assures, that the generated account ID is available.
			entityManager.getTransaction().commit();
		}
		catch(Exception exception) {
			//If something breaks a rollback is necessary!?
			if(entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			throw exception;
		}
		finally {
			entityManager.close();			
		}
	}

	
	@Override
	public void deletePriceAlert(PriceAlert priceAlert) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		PriceAlert deletePriceAlert = entityManager.find(PriceAlert.class, priceAlert.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			entityManager.remove(deletePriceAlert);
			entityManager.getTransaction().commit();			
		}
		catch(Exception exception) {
			//If something breaks a rollback is necessary.
			if(entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			throw exception;
		}
		finally {
			entityManager.close();			
		}
	}

	
	@Override
	public List<PriceAlert> getPriceAlerts() throws Exception {
		List<PriceAlert> priceAlerts = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<PriceAlert> criteriaQuery = criteriaBuilder.createQuery(PriceAlert.class);
			Root<PriceAlert> criteria = criteriaQuery.from(PriceAlert.class);
			criteriaQuery.select(criteria);
			criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id")));	//Order by id ascending
			TypedQuery<PriceAlert> typedQuery = entityManager.createQuery(criteriaQuery);
			priceAlerts = typedQuery.getResultList();
			
			entityManager.getTransaction().commit();			
		}
		catch(Exception exception) {
			//If something breaks a rollback is necessary.
			if(entityManager.getTransaction().isActive())
				entityManager.getTransaction().rollback();
			throw exception;
		}
		finally {
			entityManager.close();			
		}
		
		return priceAlerts;
	}

	
	@Override
	public PriceAlert getPriceAlert(Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		PriceAlert priceAlert = entityManager.find(PriceAlert.class, id);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return priceAlert;
	}

	
	@Override
	public void updatePriceAlert(PriceAlert priceAlert) throws ObjectUnchangedException, Exception {
		EntityManager entityManager;
		
		this.checkPriceAlertDataChanged(priceAlert);
		
		entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(priceAlert);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	
	/**
	 * Checks if the data of the given price alert differ from the price alert that is persisted at database level.
	 * 
	 * @param priceAlert The price alert to be checked.
	 * @throws ObjectUnchangedException In case the price alert has not been changed.
	 * @throws Exception In case an error occurred during determination of the price alert stored at the database.
	 */
	private void checkPriceAlertDataChanged(final PriceAlert priceAlert) throws ObjectUnchangedException, Exception {
		PriceAlert databasePriceAlert = this.getPriceAlert(priceAlert.getId());
		
		if(databasePriceAlert.equals(priceAlert))
			throw new ObjectUnchangedException();
	}
}
