package backend.dao.priceAlert;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.exception.ObjectUnchangedException;
import backend.model.priceAlert.PriceAlert;
import backend.model.priceAlert.TriggerStatus;

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
	public List<PriceAlert> getPriceAlerts(final PriceAlertOrderAttribute priceAlertOrderAttribute, final TriggerStatus triggerStatus) throws Exception {
		List<PriceAlert> priceAlerts = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<PriceAlert> criteriaQuery = criteriaBuilder.createQuery(PriceAlert.class);
			Root<PriceAlert> criteria = criteriaQuery.from(PriceAlert.class);
			criteriaQuery.select(criteria);
			
			//TODO handle multiple where expressions using Predicates ArrayList
			//https://stackoverflow.com/questions/12199433/jpa-criteria-api-with-multiple-parameters
			this.applyTriggerStatusParameter(triggerStatus, criteriaQuery, criteriaBuilder, criteria);
			
			this.applyPriceAlertOrderAttribute(priceAlertOrderAttribute, criteriaQuery, criteriaBuilder, criteria);
			
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
	
	
	/**
	 * Applies the trigger status parameter to the price alert query.
	 * 
	 * @param triggerStatus The parameter for price alert triggerTime status.
	 * @param criteriaQuery The price alert criteria query.
	 * @param criteriaBuilder The builder of criterias.
	 * @param criteria The root entity of the price alert that is being queried.
	 */
	private void applyTriggerStatusParameter(final TriggerStatus triggerStatus, final CriteriaQuery<PriceAlert> criteriaQuery,
			final CriteriaBuilder criteriaBuilder, final Root<PriceAlert> criteria) {
		
		if(triggerStatus == null || triggerStatus == TriggerStatus.ALL)
			return;	//No further query restrictions needed.
		
		if(triggerStatus == TriggerStatus.NOT_TRIGGERED)
			criteriaQuery.where(criteriaBuilder.isNull(criteria.get("triggerTime")));
		
		if(triggerStatus == TriggerStatus.TRIGGERED)
			criteriaQuery.where(criteriaBuilder.isNotNull(criteria.get("triggerTime")));
	}
	
	
	/**
	 * Applies the orderAttribute to the price alert query.
	 * 
	 * @param priceAlertOrderAttribute The parameter for price alert ordering.
	 * @param criteriaQuery The price alert criteria query.
	 * @param criteriaBuilder The builder of criterias.
	 * @param criteria The root entity of the price alert that is being queried.
	 */
	private void applyPriceAlertOrderAttribute(final PriceAlertOrderAttribute priceAlertOrderAttribute, final CriteriaQuery<PriceAlert> criteriaQuery,
			final CriteriaBuilder criteriaBuilder, final Root<PriceAlert> criteria) {
		
		if(priceAlertOrderAttribute == PriceAlertOrderAttribute.ID)
			criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id")));
		else if(priceAlertOrderAttribute == PriceAlertOrderAttribute.LAST_STOCK_QUOTE_TIME)
			criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("lastStockQuoteTime")));
	}
}
