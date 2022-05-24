package backend.dao.instrument;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.exception.ObjectUnchangedException;
import backend.model.instrument.Instrument;

public class InstrumentHibernateDAO implements InstrumentDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public InstrumentHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	
	@Override
	public void insertInstrument(Instrument instrument) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(instrument);
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
	public void deleteInstrument(Instrument instrument) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		Instrument deleteInstrument = entityManager.find(Instrument.class, instrument.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			entityManager.remove(deleteInstrument);
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
	public List<Instrument> getInstruments() throws Exception {
		List<Instrument> instruments = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Instrument> criteriaQuery = criteriaBuilder.createQuery(Instrument.class);
			Root<Instrument> criteria = criteriaQuery.from(Instrument.class);
			criteriaQuery.select(criteria);
			criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id")));	//Order by id ascending
			TypedQuery<Instrument> typedQuery = entityManager.createQuery(criteriaQuery);
			instruments = typedQuery.getResultList();
			
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
		
		return instruments;
	}

	
	@Override
	public Instrument getInstrument(Integer id) throws Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		Instrument instrument = entityManager.find(Instrument.class, id);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return instrument;
	}

	
	@Override
	public void updateInstrument(Instrument instrument) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub

	}
}
