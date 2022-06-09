package backend.dao.list;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import backend.dao.ObjectUnchangedException;
import backend.model.list.List;

/**
 * Provides access to list database persistence using Hibernate.
 * 
 * @author Michael
 */
public class ListHibernateDAO implements ListDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory
	 */
	public ListHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	@Override
	public void insertList(List list) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(list);
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
	public void deleteList(List list) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		List deleteList = entityManager.find(List.class, list.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			entityManager.remove(deleteList);
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
	public java.util.List<List> getLists() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public List getList(Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//Use entity graphs to load data of referenced instrument instances.
		EntityGraph<List> graph = entityManager.createEntityGraph(List.class);
		graph.addAttributeNodes("instruments");
		Map<String, Object> hints = new HashMap<String, Object>();
		hints.put("javax.persistence.loadgraph", graph);
		
		entityManager.getTransaction().begin();
		List list = entityManager.find(List.class, id, hints);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return list;
	}

	
	@Override
	public void updateList(List list) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub

	}
}
