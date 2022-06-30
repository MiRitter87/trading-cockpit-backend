package backend.dao.scan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.dao.ObjectUnchangedException;
import backend.model.scan.Scan;

/**
 * Provides access to scan database persistence using Hibernate.
 * 
 * @author Michael
 */
public class ScanHibernateDAO implements ScanDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory
	 */
	public ScanHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Override
	public void insertScan(Scan scan) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(scan);
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
	public void deleteScan(Scan scan) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		Scan deleteScan = entityManager.find(Scan.class, scan.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			entityManager.remove(deleteScan);
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
	public List<Scan> getScans() throws Exception {
		List<Scan> scans = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//Use entity graphs to load data of referenced list instances.
		EntityGraph<Scan> graph = entityManager.createEntityGraph(Scan.class);
		graph.addAttributeNodes("lists");
		
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Scan> criteriaQuery = criteriaBuilder.createQuery(Scan.class);
			Root<Scan> criteria = criteriaQuery.from(Scan.class);
			criteriaQuery.select(criteria);
			criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id")));	//Order by id ascending
			TypedQuery<Scan> typedQuery = entityManager.createQuery(criteriaQuery);
			typedQuery.setHint("javax.persistence.loadgraph", graph);	//Also fetch all instrument data.
			scans = typedQuery.getResultList();
			
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
		
		return scans;
	}

	
	@Override
	public Scan getScan(Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//Use entity graphs to load data of referenced list instances.
		EntityGraph<Scan> graph = entityManager.createEntityGraph(Scan.class);
		graph.addAttributeNodes("lists");
		Map<String, Object> hints = new HashMap<String, Object>();
		hints.put("javax.persistence.loadgraph", graph);
		
		entityManager.getTransaction().begin();
		Scan scan = entityManager.find(Scan.class, id, hints);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return scan;
	}

	
	@Override
	public void updateScan(Scan scan) throws ObjectUnchangedException, Exception {
		EntityManager entityManager;
		
		this.checkScanDataChanged(scan);
		
		entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(scan);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	
	/**
	 * Checks if the data of the given scan differ from the scan that is persisted at database level.
	 * 
	 * @param scan The scan to be checked.
	 * @throws ObjectUnchangedException In case the scan has not been changed.
	 * @throws Exception In case an error occurred during determination of the scan stored at the database.
	 */
	private void checkScanDataChanged(final Scan scan) throws ObjectUnchangedException, Exception {
		Scan databaseScan = this.getScan(scan.getId());
		
		if(databaseScan.equals(scan))
			throw new ObjectUnchangedException();
	}
}
