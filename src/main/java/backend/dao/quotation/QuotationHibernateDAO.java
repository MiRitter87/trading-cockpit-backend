package backend.dao.quotation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.model.StockExchange;
import backend.model.instrument.Quotation;

/**
 * Provides access to Quotation database persistence using Hibernate.
 * 
 * @author Michael
 */
public class QuotationHibernateDAO implements QuotationDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	/**
	 * The size for database batch operations.
	 */
	private static final int BATCH_SIZE = 20;

	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public QuotationHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	@Override
	public Quotation getCurrentQuotation(String symbol, StockExchange stockExchange) throws Exception {
		throw new Exception("Operation not supported.");
	}

	
	@Override
	public List<Quotation> getQuotationHistory(String symbol, StockExchange stockExchange, Integer years) throws Exception {
		throw new Exception("Operation not supported.");
	}

	
	@Override
	public void insertQuotations(final List<Quotation> quotations) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		Quotation quotation;
		
		try {
			entityManager.getTransaction().begin();
		 
		    for(int i = 0; i < quotations.size(); i++) {
		    	if(i > 0 && i % BATCH_SIZE == 0) {
		    		entityManager.getTransaction().commit();
		    		entityManager.getTransaction().begin();
		        	entityManager.clear();
		        }
		 
		    	quotation = quotations.get(i);
		        entityManager.persist(quotation);
		    }
		    
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
	public void deleteQuotations(final List<Quotation> quotations) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		Quotation tempQuotation, databaseQuotation;
		
		try {
			entityManager.getTransaction().begin();
		 
		    for(int i = 0; i < quotations.size(); i++) {
		    	if(i > 0 && i % BATCH_SIZE == 0) {
		    		entityManager.getTransaction().commit();
		    		entityManager.getTransaction().begin();
		        	entityManager.clear();
		        }
		 
		    	tempQuotation = quotations.get(i);
		    	//In order to successfully delete an entity, it first has to be fetched from the database.
		    	databaseQuotation = entityManager.find(Quotation.class, tempQuotation.getId());
		    	if(databaseQuotation != null)
		    		entityManager.remove(databaseQuotation);
		    }
		    
		    entityManager.flush();
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
	public Quotation getQuotation(final Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		Quotation quotation = entityManager.find(Quotation.class, id);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return quotation;
	}
	
	
	@Override
	public List<Quotation> getQuotationsOfInstrument(final Integer instrumentId) throws Exception {
		List<Quotation> quotations = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Quotation> criteriaQuery = criteriaBuilder.createQuery(Quotation.class);
			Root<Quotation> criteria = criteriaQuery.from(Quotation.class);
			criteriaQuery.select(criteria);
			criteriaQuery.where(criteriaBuilder.equal(criteria.get("instrument"), instrumentId));
			TypedQuery<Quotation> typedQuery = entityManager.createQuery(criteriaQuery);
			
			quotations = typedQuery.getResultList();		
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
		
		return quotations;
	}


	@Override
	public void updateQuotations(final List<Quotation> quotations) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		Quotation quotation;
		
		try {
			entityManager.getTransaction().begin();
		 
		    for(int i = 0; i < quotations.size(); i++) {
		    	if(i > 0 && i % BATCH_SIZE == 0) {
		    		entityManager.getTransaction().commit();
		    		entityManager.getTransaction().begin();
		        	entityManager.clear();
		        }
		 
		    	quotation = quotations.get(i);
		        entityManager.merge(quotation);
		    }
		    
		    entityManager.flush();
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
}
