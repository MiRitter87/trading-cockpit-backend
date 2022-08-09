package backend.dao.instrument;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import backend.dao.ObjectUnchangedException;
import backend.model.StockExchange;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentQuotationQueryParam;

/**
 * Provides access to Instrument database persistence using Hibernate.
 * 
 * @author Michael
 */
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
	public void insertInstrument(Instrument instrument) throws DuplicateInstrumentException, Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		this.checkInstrumentExistsCreate(instrument);
		
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
		Query deleteQuery;
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		Instrument deleteInstrument = entityManager.find(Instrument.class, instrument.getId());
		
		entityManager.getTransaction().begin();
		
		try {
			//Remove quotations of Instrument.
			deleteQuery = entityManager.createQuery("DELETE FROM Quotation WHERE INSTRUMENT_ID = :instrumentId");
			deleteQuery.setParameter("instrumentId", instrument.getId());
			deleteQuery.executeUpdate();
			
			//Remove Instrument.
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
	public List<Instrument> getInstruments(final InstrumentQuotationQueryParam instrumentQuotationQuery) throws Exception {
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
			
			if(instrumentQuotationQuery == InstrumentQuotationQueryParam.ALL) {
				//TODO Load quotations using additional JPA query
			}
			
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
	public Instrument getInstrument(Integer id, final boolean withQuotations) throws Exception {
		Instrument instrument = null;
		List<Instrument> instruments = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Instrument> criteriaQuery = criteriaBuilder.createQuery(Instrument.class);
			Root<Instrument> criteria = criteriaQuery.from(Instrument.class);
			criteriaQuery.select(criteria);
			criteriaQuery.where(criteriaBuilder.equal(criteria.get("id"), id));		//Only query the instrument with the given id.
			TypedQuery<Instrument> typedQuery = entityManager.createQuery(criteriaQuery);
			
			if(withQuotations) {
				//TODO Load quotations using additional JPA query
			}
			
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
		
		if(instruments.size() == 1)
			instrument = instruments.get(0);
		
		return instrument;
	}

	
	@Override
	public void updateInstrument(final Instrument instrument, final boolean updateQuotations) 
			throws ObjectUnchangedException, DuplicateInstrumentException, Exception {
		
		Instrument instrumentToUpdate;
		EntityManager entityManager;
		
		instrumentToUpdate = this.getInstrumentWithUpdatedQuotations(instrument, updateQuotations);
		this.checkInstrumentDataChanged(instrumentToUpdate);
		this.checkInstrumentExistsUpdate(instrumentToUpdate);
		
		entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(instrumentToUpdate);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	
	/**
	 * Checks if the data of the given instrument differ from the instrument that is persisted at database level.
	 * 
	 * @param instrument The instrument to be checked.
	 * @throws ObjectUnchangedException In case the instrument has not been changed.
	 * @throws Exception In case an error occurred during determination of the instrument stored at the database.
	 */
	private void checkInstrumentDataChanged(final Instrument instrument) throws ObjectUnchangedException, Exception {
		Instrument databaseInstrument = this.getInstrument(instrument.getId(), true);
		
		if(databaseInstrument.equals(instrument))
			throw new ObjectUnchangedException();
	}
	
	
	/**
	 * Checks if the database already contains an instrument with the given symbol / stock exchange combination.
	 * This check is used during instrument creation.
	 * 
	 * @param instrument The instrument to be checked.
	 * @throws DuplicateInstrumentException In case an instrument already exists.
	 * @throws Exception In case an error occurred during determination of the instrument stored at the database.
	 */
	private void checkInstrumentExistsCreate(final Instrument instrument) throws DuplicateInstrumentException, Exception {
		Instrument databaseInstrument = this.getInstrument(instrument.getSymbol(), instrument.getStockExchange());
		
		if(databaseInstrument != null)
			throw new DuplicateInstrumentException(databaseInstrument.getSymbol(), databaseInstrument.getStockExchange());
	}
	
	
	/**
	 * Checks if the database already contains an instrument with the given symbol / stock exchange combination.
	 * This check is used during instrument update.
	 * 
	 * @param instrument The instrument to be checked.
	 * @throws DuplicateInstrumentException In case an instrument already exists.
	 * @throws Exception In case an error occurred during determination of the instrument stored at the database.
	 */
	private void checkInstrumentExistsUpdate(final Instrument instrument) throws DuplicateInstrumentException, Exception {
		Instrument databaseInstrument = this.getInstrument(instrument.getSymbol(), instrument.getStockExchange());
		
		if(databaseInstrument != null && !databaseInstrument.getId().equals(instrument.getId()))
			throw new DuplicateInstrumentException(databaseInstrument.getSymbol(), databaseInstrument.getStockExchange());
	}
	
	
	/**
	 * Gets the instrument with the given symbol / stock exchange combination.
	 * 
	 * @param symbol The symbol.
	 * @param stockExchange The stock exchange.
	 * @return The instrument.
	 * @throws Exception In case an error occurred during determination of the instrument stored at the database.
	 */
	private Instrument getInstrument(final String symbol, final StockExchange stockExchange) throws Exception {
		List<Instrument> instruments = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			List<Predicate> predicates = new ArrayList<Predicate>();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Instrument> criteriaQuery = criteriaBuilder.createQuery(Instrument.class);
			Root<Instrument> criteria = criteriaQuery.from(Instrument.class);
			criteriaQuery.select(criteria);
			
			predicates.add(criteriaBuilder.equal(criteria.get("symbol"), symbol));
			predicates.add(criteriaBuilder.equal(criteria.get("stockExchange"), stockExchange));
			criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
			
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
		
		if(instruments.size() == 0)
			return null;
		else
			return instruments.get(0);
	}
	
	
	/**
	 * Initializes the quotations of the instrument with the database state, if no update of quotations is requested.
	 * If an update of quotations is requested the Instrument is returned 'as is'.
	 * 
	 * @param instrument The instrument to be updated.
	 * @param updateQuotations Indicator if quotations are to be updated.
	 * @return The instrument with correctly initialized quotations.
	 * @throws Exception Instrument quotations could not be updated.
	 */
	private Instrument getInstrumentWithUpdatedQuotations(final Instrument instrument, final boolean updateQuotations) throws Exception {
		Instrument updatedInstrument, databaseInstrument;
		
		if(!updateQuotations) {
			updatedInstrument = instrument;
			databaseInstrument = this.getInstrument(instrument.getId(), true);
			updatedInstrument.setQuotations(databaseInstrument.getQuotations());
			return updatedInstrument;
		}
		else {
			return instrument;			
		}
	}
}
