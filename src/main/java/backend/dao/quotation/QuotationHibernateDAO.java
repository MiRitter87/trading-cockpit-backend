package backend.dao.quotation;

import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.model.instrument.Quotation;
import backend.webservice.ScanTemplate;

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
		
		//Use entity graphs to load data of referenced Instrument instances.
		EntityGraph<Quotation> graph = entityManager.createEntityGraph(Quotation.class);
		graph.addAttributeNodes("instrument");
		graph.addAttributeNodes("indicator");
		
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Quotation> criteriaQuery = criteriaBuilder.createQuery(Quotation.class);
			Root<Quotation> criteria = criteriaQuery.from(Quotation.class);
			criteriaQuery.select(criteria);
			criteriaQuery.where(criteriaBuilder.equal(criteria.get("instrument"), instrumentId));
			TypedQuery<Quotation> typedQuery = entityManager.createQuery(criteriaQuery);
			typedQuery.setHint("javax.persistence.loadgraph", graph);	//Also fetch all instrument and indicator data.
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


	@SuppressWarnings("unchecked")
	@Override
	public List<Quotation> getRecentQuotations() throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		List<Quotation> quotations;
		Query query;
		List<Object> quotationIdsWithMaxDate;
		
		try {
			entityManager.getTransaction().begin();
			
			/*
			 * The selection is split into two selects because JOINs with sub-queries are only possible in native SQL.
			 * But the needed JOIN FETCH is not possible in native SQL.
			 */
			query = this.getQueryForQuotationIdsWithMaxDate(entityManager);
			quotationIdsWithMaxDate = query.getResultList();
			
			//Now select final data using JOIN FETCH based on the Quotation IDs selected before.
			query = entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i "
					+ "WHERE quotation_id IN :quotationIds AND q.indicator IS NOT NULL");
			
			query.setParameter("quotationIds", quotationIdsWithMaxDate);
			quotations = query.getResultList();
			
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
		
		return quotations;
	}


	@Override
	public List<Quotation> getRecentQuotationsForList(backend.model.list.List list) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Quotation> getQuotationsByTemplate(ScanTemplate scanTemplate) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		List<Quotation> quotations;
		Query query;
		List<Object> quotationIdsWithMaxDate;

		try {
			entityManager.getTransaction().begin();
			
			/*
			 * The selection is split into two selects because JOINs with sub-queries are only possible in native SQL.
			 * But the needed JOIN FETCH is not possible in native SQL.
			 */
			query = this.getQueryForQuotationIdsWithMaxDate(entityManager);
			quotationIdsWithMaxDate = query.getResultList();
			
			//Find all quotations that have an Indicator defined and where the Quotation is the most recent one of an Instrument.
			//Also fetch the Instrument data of those records. Apply the criteria of the given template.
			switch(scanTemplate) {
				case MINERVINI_TREND_TEMPLATE:
					query = this.getQueryForMinerviniTrendTemplate(entityManager);
					break;
				case BREAKOUT_CANDIDATES:
					query = this.getQueryForBreakoutCandidatesTemplate(entityManager);
					break;
				case VOLATILITY_CONTRACTION_10_DAYS:
					query = this.getQueryForVolatilityContractionTemplate(entityManager);
					break;
				default:
					return null;
			}
			
			query.setParameter("quotationIds", quotationIdsWithMaxDate);
			quotations = query.getResultList();
			
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
		
		return quotations;
	}
	
	
	/**
	 * Provides a native Query that determines the IDs of the quotations with the newest date for each Instrument.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForQuotationIdsWithMaxDate(final EntityManager entityManager) {
		return entityManager.createNativeQuery("SELECT quotation_id FROM Quotation q "
				+ "INNER JOIN (SELECT max(date) AS maxdate, instrument_id FROM Quotation GROUP BY instrument_id) jointable "
				+ "ON q.instrument_id = jointable.instrument_id AND q.date = jointable.maxdate");
	}
	
	
	/**
	 * Provides the Query for the Minervini Trend Template.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForMinerviniTrendTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND q.price > r.sma50 "
				+ "AND r.sma50 > r.sma150 "
				+ "AND r.sma150 > r.sma200 "
				+ "AND r.distanceTo52WeekLow >= 30 "
				+ "AND r.distanceTo52WeekHigh >= -25 "
				+ "AND r.rsNumber >= 70");
	}
	
	
	/**
	 * Provides the Query for the Volatility Contraction Template.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForVolatilityContractionTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND r.volumeDifferential10Days < 0"
				+ "AND r.bollingerBandWidth < 10");
	}
	
	
	/**
	 * Provides the Query for the Breakout Candidates Template.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForBreakoutCandidatesTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND r.volumeDifferential10Days < 0"
				+ "AND r.bollingerBandWidth < 10"
				+ "AND r.baseLengthWeeks >= 3"
				+ "AND r.distanceTo52WeekHigh >= -10");
	}
}
