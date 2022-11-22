package backend.dao.statistic;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import backend.dao.ObjectUnchangedException;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Provides access to Statistic database persistence using Hibernate.
 * 
 * @author Michael
 */
public class StatisticHibernateDAO implements StatisticDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public StatisticHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Override
	public void insertStatistic(Statistic statistic) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(statistic);
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
	public void deleteStatistic(Statistic statistic) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		Statistic deleteStatistic = entityManager.find(Statistic.class, statistic.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			//Remove Instrument.
			entityManager.remove(deleteStatistic);
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
	public List<Statistic> getStatistics(InstrumentType instrumentType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Statistic getStatistic(Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		Statistic statistic;
		
		entityManager.getTransaction().begin();
		statistic = entityManager.find(Statistic.class, id);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return statistic;
	}

	
	@Override
	public void updateStatistic(Statistic statistic) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub

	}
}
