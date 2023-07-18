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
import backend.model.ObjectInUseException;
import backend.model.StockExchange;
import backend.model.chart.HorizontalLine;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.priceAlert.PriceAlert;

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
	public void deleteInstrument(Instrument instrument) throws ObjectInUseException, Exception {
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		this.checkInstrumentInUse(instrument, entityManager);
		
		//In order to successfully delete an entity, it first has to be fetched from the database.
		Instrument deleteInstrument = entityManager.find(Instrument.class, instrument.getId());
		
		entityManager.getTransaction().begin();
		
		try {
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
	public List<Instrument> getInstruments(InstrumentType instrumentType) throws Exception {
		List<Instrument> instruments = null;
		EntityManager entityManager = this.sessionFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		
		try {
			Predicate predicate;
			List<Predicate> predicates = new ArrayList<Predicate>();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Instrument> criteriaQuery = criteriaBuilder.createQuery(Instrument.class);
			Root<Instrument> criteria = criteriaQuery.from(Instrument.class);
			criteriaQuery.select(criteria);
			
			predicate = this.applyInstrumentTypeParameter(instrumentType, criteriaBuilder, criteria);
			if(predicate != null)
				predicates.add(predicate);
			
			criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
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
	public void updateInstrument(final Instrument instrument) throws ObjectUnchangedException, DuplicateInstrumentException, Exception {
		EntityManager entityManager;
		
		this.checkInstrumentDataChanged(instrument);
		this.checkInstrumentExistsUpdate(instrument);
		
		entityManager = this.sessionFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(instrument);
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
		Instrument databaseInstrument = this.getInstrument(instrument.getId());
		
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
	 * Checks if the Instrument is referenced by another business object.
	 * 
	 * @param instrument The instrument which is checked.
	 * @throws ObjectInUseException In case the Instrument is in use.
	 */
	private void checkInstrumentInUse(final Instrument instrument, final EntityManager entityManager) throws ObjectInUseException {	
		this.checkQuotationsExist(instrument, entityManager);
		this.checkHorizontalLinesExist(instrument, entityManager);
		this.checkInstrumentUsedInList(instrument, entityManager);
		this.checkInstrumentUsedInPriceAlert(instrument, entityManager);
		this.checkInstrumentUsedAsSectorOrIg(instrument, entityManager);
	}
	
	
	/**
	 * Checks if the given Instrument has any Quotation referenced.
	 * 
	 * @param entityManager The EntityManager used to check for existing quotations.
	 * @param instrument The Instrument whose quotations are checked.
	 * @throws ObjectInUseException In case the Instrument has quotations referenced.
	 */
	@SuppressWarnings("unchecked")
	private void checkQuotationsExist(final Instrument instrument, final EntityManager entityManager) throws ObjectInUseException {
		Query query = entityManager.createQuery("SELECT q FROM Quotation q WHERE INSTRUMENT_ID = :instrumentId");
		List<Quotation> quotations;
		
		query.setParameter("instrumentId", instrument.getId());
		quotations = query.getResultList();
		
		if(quotations.size() > 0) {
			throw new ObjectInUseException(instrument.getId(), quotations.get(0).getId(), quotations.get(0));
		}
	}
	
	
	/**
	 * Checks if the given Instrument has any horizontal lines referenced.
	 * 
	 * @param entityManager The EntityManager used to check for existing horizontal lines.
	 * @param instrument The Instrument whose horizontal lines are checked.
	 * @throws ObjectInUseException In case the Instrument has horizontal lines referenced.
	 */
	@SuppressWarnings("unchecked")
	private void checkHorizontalLinesExist(final Instrument instrument, final EntityManager entityManager) throws ObjectInUseException {
		Query query = entityManager.createQuery("SELECT h FROM HorizontalLine h WHERE INSTRUMENT_ID = :instrumentId");
		List<HorizontalLine> horizontalLines;
		
		query.setParameter("instrumentId", instrument.getId());
		horizontalLines = query.getResultList();
		
		if(horizontalLines.size() > 0) {
			throw new ObjectInUseException(instrument.getId(), horizontalLines.get(0).getId(), horizontalLines.get(0));
		}
	}
	
	
	/**
	 * Checks if the Instrument is referenced by any List.
	 * 
	 * @param instrument The Instrument which is checked.
	 * @param entityManager The EntityManager used to execute queries.
	 * @throws ObjectInUseException In case the Instrument is in use.
	 */
	@SuppressWarnings("unchecked")
	private void checkInstrumentUsedInList(final Instrument instrument, final EntityManager entityManager) throws ObjectInUseException {
		Query query = entityManager.createQuery("SELECT l FROM List l INNER JOIN l.instruments instrument WHERE instrument.id = :instrumentId");
		List<backend.model.list.List> lists;
		
		query.setParameter("instrumentId", instrument.getId());
		lists = query.getResultList();
		
		if(lists.size() > 0) {
			throw new ObjectInUseException(instrument.getId(), lists.get(0).getId(), lists.get(0));
		}
	}
	
	
	/**
	 * Checks if the Instrument is referenced by any PriceAlert.
	 * 
	 * @param instrument The Instrument which is checked.
	 * @param entityManager The EntityManager used to execute queries.
	 * @throws ObjectInUseException In case the Instrument is in use.
	 */
	@SuppressWarnings("unchecked")
	private void checkInstrumentUsedInPriceAlert(final Instrument instrument, final EntityManager entityManager) throws ObjectInUseException {
		Query query = entityManager.createQuery("SELECT p FROM PriceAlert p INNER JOIN p.instrument i WHERE i.id = :instrumentId");
		List<PriceAlert> priceAlerts;
		
		query.setParameter("instrumentId", instrument.getId());
		priceAlerts = query.getResultList();
		
		if(priceAlerts.size() > 0) {
			throw new ObjectInUseException(instrument.getId(), priceAlerts.get(0).getId(), priceAlerts.get(0));
		}
	}
	
	
	/**
	 * Checks if the Instrument is used as sector or industry group in another Instrument.
	 * 
	 * @param instrument The Instrument which is checked.
	 * @param entityManager The EntityManager used to execute queries.
	 * @throws ObjectInUseException In case the Instrument is in use.
	 */
	@SuppressWarnings("unchecked")
	private void checkInstrumentUsedAsSectorOrIg(final Instrument instrument, final EntityManager entityManager) throws ObjectInUseException {
		Query query = entityManager.createQuery("Select i FROM Instrument i WHERE sector_id = :instrumentId OR industry_group_id = :instrumentId");
		List<Instrument> instruments;
		
		query.setParameter("instrumentId", instrument.getId());
		instruments = query.getResultList();
		
		if(instruments.size() > 0)
			throw new ObjectInUseException(instrument.getId(), instruments.get(0).getId(), instruments.get(0));
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
	 * Applies the InstrumentType parameter to the query.
	 * 
	 * @param instrumentType The parameter for InstrumentType.
	 * @param criteriaBuilder The builder of criterias.
	 * @param criteria The root entity of the Instrument that is being queried.
	 * @return A predicate for the InstrumentType.
	 */
	private Predicate applyInstrumentTypeParameter(final InstrumentType instrumentType, final CriteriaBuilder criteriaBuilder, 
			final Root<Instrument> criteria) {
		
		if(instrumentType != null)
			return criteriaBuilder.equal(criteria.get("type"), instrumentType);
		
		return null;
	}
}
