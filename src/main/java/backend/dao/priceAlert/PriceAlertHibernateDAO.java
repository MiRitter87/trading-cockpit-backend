package backend.dao.priceAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import backend.dao.ObjectUnchangedException;
import backend.model.LocalizedException;
import backend.model.priceAlert.ConfirmationStatus;
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
			entityManager.flush();	//Assures, that the generated ID is available.
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
	public List<PriceAlert> getPriceAlerts(final PriceAlertOrderAttribute priceAlertOrderAttribute, 
			final TriggerStatus triggerStatus, final ConfirmationStatus confirmationStatus) throws Exception {
		
		List<PriceAlert> priceAlerts = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			Predicate predicate;
			List<Predicate> predicates = new ArrayList<Predicate>();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<PriceAlert> criteriaQuery = criteriaBuilder.createQuery(PriceAlert.class);
			Root<PriceAlert> criteria = criteriaQuery.from(PriceAlert.class);
			criteriaQuery.select(criteria);
			
			predicate = applyTriggerStatusParameter(triggerStatus, criteriaBuilder, criteria);
			if(predicate != null)
				predicates.add(predicate);
			
			predicate = applyConfirmationStatusParameter(confirmationStatus, criteriaBuilder, criteria);
			if(predicate != null)
				predicates.add(predicate);
			
			criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
			
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
	public void updatePriceAlert(PriceAlert priceAlert) throws ObjectUnchangedException, LocalizedException, Exception {
		EntityManager entityManager;
		
		this.checkPriceAlertDataChanged(priceAlert);
		this.checkValidChanges(priceAlert);
		
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
	 * @throws ObjectUnchangedException Object data did not change.
	 * @throws Exception In case an error occurred during determination of the price alert stored at the database.
	 */
	private void checkPriceAlertDataChanged(final PriceAlert priceAlert) throws ObjectUnchangedException, Exception {
		PriceAlert databasePriceAlert = this.getPriceAlert(priceAlert.getId());
		
		if(databasePriceAlert.equals(priceAlert))
			throw new ObjectUnchangedException();
	}
	
	
	/**
	 * Checks if the changes are valid.
	 * 
	 * @param priceAlert The price alert to be checked.
	 * @throws LocalizedException A general exception containing a localized message.
	 * @throws Exception In case an error occurred during determination of the price alert stored at the database.
	 */
	private void checkValidChanges(final PriceAlert priceAlert) throws LocalizedException, Exception {
		PriceAlert databasePriceAlert = this.getPriceAlert(priceAlert.getId());
		
		//No changes allowed at all after PriceAlert has been triggered and confirmed.
		if(databasePriceAlert.getTriggerTime() != null && databasePriceAlert.getConfirmationTime() != null)
			throw new LocalizedException("priceAlert.updateAfterTriggered");
		
		//No changes of the attributes below allowed after PriceAlert has been triggered.
		if(databasePriceAlert.getTriggerTime() != null && (
				!databasePriceAlert.getInstrument().equals(priceAlert.getInstrument()) || 
				databasePriceAlert.getAlertType() != priceAlert.getAlertType() || 
				databasePriceAlert.getPrice().compareTo(priceAlert.getPrice()) != 0 ||
				databasePriceAlert.isSendMail() != priceAlert.isSendMail() || 
				!Objects.equals(databasePriceAlert.getAlertMailAddress(), priceAlert.getAlertMailAddress()))) {
			
			throw new LocalizedException("priceAlert.updateAfterTriggered");
		}
	}
	
	
	/**
	 * Applies the trigger status parameter to the price alert query.
	 * 
	 * @param triggerStatus The parameter for price alert triggerTime status.
	 * @param criteriaBuilder The builder of criteria.
	 * @param criteria The root entity of the price alert that is being queried.
	 * @return A predicate for the trigger status.
	 */
	private Predicate applyTriggerStatusParameter(final TriggerStatus triggerStatus, final CriteriaBuilder criteriaBuilder, final Root<PriceAlert> criteria) {
		
		if(triggerStatus == TriggerStatus.NOT_TRIGGERED)
			return criteriaBuilder.isNull(criteria.get("triggerTime"));
		
		if(triggerStatus == TriggerStatus.TRIGGERED)
			return criteriaBuilder.isNotNull(criteria.get("triggerTime"));
		
		return null;
	}
	
	
	/**
	 * Applies the confirmation status parameter to the price alert query.
	 * 
	 * @param confirmationStatus The parameter for price alert confirmationTime status.
	 * @param criteriaBuilder The builder of criteria.
	 * @param criteria The root entity of the price alert that is being queried.
	 * @return A predicate for the trigger status.
	 */
	private Predicate applyConfirmationStatusParameter(final ConfirmationStatus confirmationStatus, 
			final CriteriaBuilder criteriaBuilder, final Root<PriceAlert> criteria) {
		
		if(confirmationStatus == ConfirmationStatus.NOT_CONFIRMED)
			return criteriaBuilder.isNull(criteria.get("confirmationTime"));
		
		if(confirmationStatus == ConfirmationStatus.CONFIRMED)
			return criteriaBuilder.isNotNull(criteria.get("confirmationTime"));
		
		return null;
	}
	
	
	/**
	 * Applies the orderAttribute to the price alert query.
	 * 
	 * @param priceAlertOrderAttribute The parameter for price alert ordering.
	 * @param criteriaQuery The price alert criteria query.
	 * @param criteriaBuilder The builder of criteria.
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
