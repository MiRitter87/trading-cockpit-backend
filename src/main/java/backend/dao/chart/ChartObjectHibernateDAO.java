package backend.dao.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import backend.dao.ObjectUnchangedException;
import backend.model.chart.HorizontalLine;

/**
 * Provides access to chart object database persistence using Hibernate.
 * 
 * @author Michael
 */
public class ChartObjectHibernateDAO implements ChartObjectDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public ChartObjectHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Override
	public void insertHorizontalLine(HorizontalLine horizontalLine) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(horizontalLine);
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
	public void deleteHorizontalLine(HorizontalLine horizontalLine) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		HorizontalLine deleteHorizontalLine = entityManager.find(HorizontalLine.class, horizontalLine.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			entityManager.remove(deleteHorizontalLine);
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
	public List<HorizontalLine> getHorizontalLines(Integer instrumentId) throws Exception {
		Predicate predicate;
		List<Predicate> predicates = new ArrayList<Predicate>();
		List<HorizontalLine> horizontalLines = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//Use entity graphs to load data of referenced Instrument instances.
		EntityGraph<HorizontalLine> graph = entityManager.createEntityGraph(HorizontalLine.class);
		graph.addAttributeNodes("instrument");
		
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<HorizontalLine> criteriaQuery = criteriaBuilder.createQuery(HorizontalLine.class);
			Root<HorizontalLine> criteria = criteriaQuery.from(HorizontalLine.class);
			criteriaQuery.select(criteria);
			
			predicate = applyInstrumentIdParameter(instrumentId, criteriaBuilder, criteria);
			if(predicate != null)
				predicates.add(predicate);
			
			criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
			
			criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id")));	//Order by id ascending
			TypedQuery<HorizontalLine> typedQuery = entityManager.createQuery(criteriaQuery);
			typedQuery.setHint("javax.persistence.loadgraph", graph);	//Also fetch all Instrument data.
			horizontalLines = typedQuery.getResultList();
			
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
		
		return horizontalLines;
	}

	
	@Override
	public HorizontalLine getHorizontalLine(Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//Use entity graphs to load data of referenced instrument instance.
		EntityGraph<HorizontalLine> graph = entityManager.createEntityGraph(HorizontalLine.class);
		graph.addAttributeNodes("instrument");
		Map<String, Object> hints = new HashMap<String, Object>();
		hints.put("javax.persistence.loadgraph", graph);
		
		entityManager.getTransaction().begin();
		HorizontalLine horizontalLine = entityManager.find(HorizontalLine.class, id, hints);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return horizontalLine;
	}

	
	@Override
	public void updateHorizontalLine(HorizontalLine horizontalLine) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * Applies the Instrument id parameter to the horizontal lines query.
	 * 
	 * @param instrumentId The ID of the Instrument whose horizontal lines are requested.
	 * @param criteriaBuilder The builder of criteria.
	 * @param criteria The root entity of the HorizontalLine that is being queried.
	 * @return A predicate for the Instrument ID.
	 */
	private Predicate applyInstrumentIdParameter(final Integer instrumentId, final CriteriaBuilder criteriaBuilder, final Root<HorizontalLine> criteria) {
		Predicate predicate;
		
		if(instrumentId != null)
			predicate = criteriaBuilder.equal(criteria.get("instrument"), instrumentId);
		else
			predicate = null;
		
		return predicate;
	}
}
