package backend.dao.quotation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentType;
import backend.model.instrument.Quotation;
import backend.model.instrument.QuotationArray;
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
	public List<Quotation> getRecentQuotations(final InstrumentType instrumentType) throws Exception {
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
			query = this.getQueryForQuotationIdsWithMaxDate(entityManager, instrumentType);
			quotationIdsWithMaxDate = query.getResultList();
			
			//Now select final data using JOIN FETCH based on the Quotation IDs selected before.
			query = this.getQueryForQuotationsWithInstrument(entityManager, quotationIdsWithMaxDate, true);
			quotations = query.getResultList();
			
			this.fillTransientAttributes(instrumentType, quotations);
			
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


	@SuppressWarnings("unchecked")
	@Override
	public List<Quotation> getRecentQuotationsForList(backend.model.list.List list) throws Exception {
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
			query = this.getQueryForQuotationIdsWithMaxDateForList(entityManager, list);
			quotationIdsWithMaxDate = query.getResultList();
			
			//Now select final data using JOIN FETCH based on the Quotation IDs selected before.
			query = this.getQueryForQuotationsWithInstrument(entityManager, quotationIdsWithMaxDate, false);
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Quotation> getQuotationsByTemplate(ScanTemplate scanTemplate, InstrumentType instrumentType) throws Exception {
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
			query = this.getQueryForQuotationIdsWithMaxDate(entityManager, instrumentType);
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
				case UP_ON_VOLUME:
					query = this.getQueryForUpOnVolumeTemplate(entityManager);
					break;
				case DOWN_ON_VOLUME:
					query = this.getQueryForDownOnVolumeTemplate(entityManager);
					break;
				case NEAR_52_WEEK_HIGH:
					query = this.getQueryForNear52WeekHighTemplate(entityManager);
					break;
				default:
					entityManager.getTransaction().commit();
					entityManager.close();
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
	 * Fills transient attributes of the given quotations.
	 * 
	 * @param instrumentType The InstrumentType of the given quotations.
	 * @param quotations The quotations with their referenced data whose transient attributes are to be filled.
	 * @throws Exception In case an error occurred during data determination.
	 */
	private void fillTransientAttributes(final InstrumentType instrumentType, List<Quotation> quotations) throws Exception {
		QuotationArray sectorQuotations = new QuotationArray();
		QuotationArray industryGroupQuotations = new QuotationArray();
		List<Quotation> quotationsOfInstrument;
		Quotation sectorQuotation, industryGroupQuotation;
		
		if(quotations.size() == 0 || instrumentType != InstrumentType.STOCK)
			return;
		
		sectorQuotations.setQuotations(this.getRecentQuotations(InstrumentType.SECTOR));
		industryGroupQuotations.setQuotations(this.getRecentQuotations(InstrumentType.IND_GROUP));

		//Determine and set the sector and industry group RS number for each quotation.
		for(Quotation quotation : quotations) {
			if(quotation.getInstrument().getSector() != null) {
				quotationsOfInstrument = sectorQuotations.getQuotationsByInstrumentId(quotation.getInstrument().getSector().getId());
				
				if(quotationsOfInstrument.size() == 1)
					sectorQuotation = quotationsOfInstrument.get(0);
				else
					sectorQuotation = null;
				
				if(sectorQuotation != null && this.areQuotationsOfSameDay(quotation, sectorQuotation))
					quotation.getIndicator().setRsNumberSector(sectorQuotation.getIndicator().getRsNumber());
			}
			
			if(quotation.getInstrument().getIndustryGroup() != null) {
				quotationsOfInstrument = industryGroupQuotations.getQuotationsByInstrumentId(quotation.getInstrument().getIndustryGroup().getId());
				
				if(quotationsOfInstrument.size() == 1)
					industryGroupQuotation = quotationsOfInstrument.get(0);
				else
					industryGroupQuotation = null;
				
				if(industryGroupQuotation != null && this.areQuotationsOfSameDay(quotation, industryGroupQuotation))
					quotation.getIndicator().setRsNumberIndustryGroup(industryGroupQuotation.getIndicator().getRsNumber());
			}
		}
	}
	
	
	/**
	 * Provides a native Query that determines the IDs of the quotations with the newest date for each Instrument.
	 * Only those instruments are taken into account that match the given InstrumentType.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @param instrumentType The InstrumentType.
	 * @return The Query.
	 */
	@SuppressWarnings("unchecked")
	private Query getQueryForQuotationIdsWithMaxDate(final EntityManager entityManager, final InstrumentType instrumentType) {
		List<Object> instrumentIdsOfGivenInstrumentType;
		Query query;
		
		//Get the IDs of all instruments of the given type.
		query = entityManager.createQuery("SELECT id FROM Instrument i WHERE i.type = :instrumentType");
		query.setParameter("instrumentType", instrumentType);
		instrumentIdsOfGivenInstrumentType = query.getResultList();
		
		//Get the IDs of all Quotations with the newest date for each Instrument.
		query = entityManager.createNativeQuery("SELECT quotation_id FROM Quotation q "
				+ "INNER JOIN (SELECT max(date) AS maxdate, instrument_id FROM Quotation GROUP BY instrument_id) jointable "
				+ "ON q.instrument_id = jointable.instrument_id AND q.date = jointable.maxdate WHERE q.instrument_id IN :instrumentIds");
		query.setParameter("instrumentIds", instrumentIdsOfGivenInstrumentType);
		
		return query;
	}
	
	
	/**
	 * Provides a native Query that determines the IDs of the quotations with the newest date for each Instrument of the given List.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @param list The List defining all instruments for which the quotations have to be determined.
	 * @return The Query.
	 */
	private Query getQueryForQuotationIdsWithMaxDateForList(final EntityManager entityManager, final backend.model.list.List list) {
		List<Object> instrumentIds = new ArrayList<>();
		Iterator<Instrument> instrumentIterator;
		Instrument instrument;
		Query query;
		
		instrumentIterator = list.getInstruments().iterator();
		
		//Get the IDs of all instruments of the given List.
		while(instrumentIterator.hasNext()) {
			instrument = instrumentIterator.next();
			instrumentIds.add(instrument.getId());
		}
		
		//Prepare Query for most recent Quotation of each Instrument.
		query = entityManager.createNativeQuery("SELECT quotation_id FROM Quotation q "
				+ "INNER JOIN (SELECT max(date) AS maxdate, instrument_id FROM Quotation GROUP BY instrument_id) jointable "
				+ "ON q.instrument_id = jointable.instrument_id AND q.date = jointable.maxdate AND jointable.instrument_id IN :instrumentIds");
		
		query.setParameter("instrumentIds", instrumentIds);
		
		return query;
	}
	
	
	/**
	 * Provides a Query that determines all quotations with their referenced Instrument (and Indicator) based on the given Quotation IDs.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @param quotationIdsWithMaxDate A List of Quotation IDs that are queried.
	 * @param withIndicatorNotNull Only those quotations are fetched that have Indicator data referenced, if set to true.
	 * @return The Query.
	 */
	private Query getQueryForQuotationsWithInstrument(final EntityManager entityManager, final List<Object> quotationIdsWithMaxDate,
			final boolean withIndicatorNotNull) {
		Query query;
		
		if(withIndicatorNotNull) {
			query = entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i "
					+ "WHERE quotation_id IN :quotationIds AND q.indicator IS NOT NULL");
		}
		else {
			query = entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i "
					+ "WHERE quotation_id IN :quotationIds");
		}
		
		query.setParameter("quotationIds", quotationIdsWithMaxDate);
		
		return query;
	}
	
	
	/**
	 * Provides the Query for the "Minervini Trend Template".
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForMinerviniTrendTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND q.close > r.sma50 "
				+ "AND r.sma50 > r.sma150 "
				+ "AND r.sma150 > r.sma200 "
				+ "AND r.distanceTo52WeekLow >= 30 "
				+ "AND r.distanceTo52WeekHigh >= -25 "
				+ "AND r.rsNumber >= 70");
	}
	
	
	/**
	 * Provides the Query for the "Volatility Contraction" Template.
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
	 * Provides the Query for the "Breakout Candidates" Template.
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
	
	
	/**
	 * Provides the Query for the "Up on Volume" Template.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForUpOnVolumeTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND r.volumeDifferential5Days >= 25"
				+ "AND r.performance5Days >= 10");
	}
	
	
	/**
	 * Provides the Query for the "Down on Volume" Template.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForDownOnVolumeTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND r.volumeDifferential5Days >= 25"
				+ "AND r.performance5Days <= -10");
	}
	
	
	/**
	 * Provides the Query for the "Near 52-week High" Template.
	 * 
	 * @param entityManager The EntityManager used for Query creation.
	 * @return The Query.
	 */
	private Query getQueryForNear52WeekHighTemplate(final EntityManager entityManager) {
		return entityManager.createQuery("SELECT q FROM Quotation q JOIN FETCH q.instrument i JOIN q.indicator r WHERE "
				+ "quotation_id IN :quotationIds "
				+ "AND q.indicator IS NOT NULL "
				+ "AND r.distanceTo52WeekHigh >= -5 ");
	}
	
	
	/**
	 * Checks if both quotations are of the same day.
	 * 
	 * @param quotation1 The first Quotation.
	 * @param quotation2 The second Quotation.
	 * @return true, if both quotations are of the same day.
	 */
	private boolean areQuotationsOfSameDay(final Quotation quotation1, final Quotation quotation2) {
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		
		date1.setTimeInMillis(quotation1.getDate().getTime());
		date2.setTimeInMillis(quotation2.getDate().getTime());
		
		if(	date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
			date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
			date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)) {
			
			return true;
		}
		else {
			return false;
		}
	}
}
