package backend.dao.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
		// TODO Auto-generated method stub
		return null;
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
}
