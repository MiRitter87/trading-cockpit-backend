package backend.dao.quotation.persistence;

import java.util.List;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import backend.model.instrument.InstrumentType;
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
    private EntityManagerFactory sessionFactory;

    /**
     * The size for database batch operations.
     */
    private static final int BATCH_SIZE = 20;

    /**
     * Processor that performs more complex tasks during the template-based query process.
     */
    private ScanTemplateProcessor scanTemplateProcessor;

    /**
     * Default constructor.
     *
     * @param sessionFactory The database session factory.
     */
    public QuotationHibernateDAO(final EntityManagerFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.scanTemplateProcessor = new ScanTemplateProcessor(this);
    }

    /**
     * Inserts a list of quotations.
     */
    @Override
    public void insertQuotations(final List<Quotation> quotations) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Quotation quotation;

        try {
            entityManager.getTransaction().begin();

            for (int i = 0; i < quotations.size(); i++) {
                if (i > 0 && i % BATCH_SIZE == 0) {
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                    entityManager.clear();
                }

                quotation = quotations.get(i);
                entityManager.persist(quotation);
            }

            entityManager.flush(); // Assures, that the generated ID is available.
            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary!?
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Deletes a list of quotations.
     */
    @Override
    public void deleteQuotations(final List<Quotation> quotations) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Quotation tempQuotation;
        Quotation databaseQuotation;

        try {
            entityManager.getTransaction().begin();

            for (int i = 0; i < quotations.size(); i++) {
                if (i > 0 && i % BATCH_SIZE == 0) {
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                    entityManager.clear();
                }

                tempQuotation = quotations.get(i);
                // In order to successfully delete an entity, it first has to be fetched from the database.
                databaseQuotation = entityManager.find(Quotation.class, tempQuotation.getId());
                if (databaseQuotation != null) {
                    entityManager.remove(databaseQuotation);
                }
            }

            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary!?
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Gets the Quotation with the given ID.
     */
    @Override
    public Quotation getQuotation(final Integer id) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        entityManager.getTransaction().begin();
        Quotation quotation = entityManager.find(Quotation.class, id);
        entityManager.getTransaction().commit();
        entityManager.close();

        return quotation;
    }

    /**
     * Gets a list of quotations of the Instrument with the given ID.
     */
    @Override
    public List<Quotation> getQuotationsOfInstrument(final Integer instrumentId) throws Exception {
        List<Quotation> quotations = null;
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // Use entity graphs to load data of referenced Instrument instances.
        EntityGraph<Quotation> graph = entityManager.createEntityGraph(Quotation.class);
        graph.addAttributeNodes("instrument");
        graph.addAttributeNodes("indicator");
        graph.addSubgraph("instrument").addAttributeNodes("sector");

        entityManager.getTransaction().begin();

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Quotation> criteriaQuery = criteriaBuilder.createQuery(Quotation.class);
            Root<Quotation> criteria = criteriaQuery.from(Quotation.class);
            criteriaQuery.select(criteria);
            criteriaQuery.where(criteriaBuilder.equal(criteria.get("instrument").get("id"), instrumentId));
            TypedQuery<Quotation> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setHint("jakarta.persistence.loadgraph", graph); // Also fetch all instrument and indicator data.
            quotations = typedQuery.getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary.
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }

        return quotations;
    }

    /**
     * Updates a list of quotations.
     */
    @Override
    public void updateQuotations(final List<Quotation> quotations) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Quotation quotation;

        try {
            entityManager.getTransaction().begin();

            for (int i = 0; i < quotations.size(); i++) {
                if (i > 0 && i % BATCH_SIZE == 0) {
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                    entityManager.clear();
                }

                quotation = quotations.get(i);
                entityManager.merge(quotation);
            }

            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary!?
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Gets the most recent Quotation of each Instrument of the given InstrumentType.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Quotation> getRecentQuotations(final InstrumentType instrumentType) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        List<Quotation> quotations;
        Query query;
        List<Object> quotationIdsWithMaxDate;
        QuotationQueryProvider quotationQueryProvider = new QuotationQueryProvider(entityManager);

        try {
            entityManager.getTransaction().begin();

            /*
             * The selection is split into two selects because JOINs with sub-queries are only possible in native SQL.
             * But the needed JOIN FETCH is not possible in native SQL.
             */
            query = quotationQueryProvider.getQueryForQuotationIdsWithMaxDate(instrumentType);
            quotationIdsWithMaxDate = query.getResultList();

            // Now select final data using JOIN FETCH based on the Quotation IDs selected before.
            query = quotationQueryProvider.getQueryForQuotationsWithInstrument(true);
            query.setParameter("quotationIds", quotationIdsWithMaxDate);
            quotations = query.getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary!?
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }

        return quotations;
    }

    /**
     * Gets the most recent Quotation of each Instrument of the given List.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Quotation> getRecentQuotationsForList(final backend.model.list.List list) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        List<Quotation> quotations;
        Query query;
        List<Object> quotationIdsWithMaxDate;
        QuotationQueryProvider quotationQueryProvider = new QuotationQueryProvider(entityManager);

        try {
            entityManager.getTransaction().begin();

            /*
             * The selection is split into two selects because JOINs with sub-queries are only possible in native SQL.
             * But the needed JOIN FETCH is not possible in native SQL.
             */
            query = quotationQueryProvider.getQueryForQuotationIdsWithMaxDateForList(list);
            quotationIdsWithMaxDate = query.getResultList();

            // Now select final data using JOIN FETCH based on the Quotation IDs selected before.
            query = quotationQueryProvider.getQueryForQuotationsWithInstrument(false);
            query.setParameter("quotationIds", quotationIdsWithMaxDate);
            quotations = query.getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary!?
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }

        return quotations;
    }

    /**
     * Gets the most recent Quotation of each Instrument of the given InstrumentType. Only those quotations are taken
     * into account that match the given ScanTemplate.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Quotation> getQuotationsByTemplate(final ScanTemplate scanTemplate, final InstrumentType instrumentType,
            final String startDate, final Float minLiquidity) throws Exception {

        EntityManager entityManager = this.sessionFactory.createEntityManager();
        List<Quotation> quotations;
        Query query;
        List<Object> quotationIdsWithMaxDate;
        QuotationQueryProvider quotationQueryProvider = new QuotationQueryProvider(entityManager);

        try {
            entityManager.getTransaction().begin();

            /*
             * The selection is split into two selects because JOINs with sub-queries are only possible in native SQL.
             * But the needed JOIN FETCH is not possible in native SQL.
             */
            query = quotationQueryProvider.getQueryForQuotationIdsWithMaxDate(instrumentType);
            quotationIdsWithMaxDate = query.getResultList();

            // Find all quotations that have an Indicator defined and where the Quotation is the most recent one of an
            // Instrument.
            // Also fetch the Instrument data of those records. Apply the criteria of the given template.
            switch (scanTemplate) {
            case MINERVINI_TREND_TEMPLATE:
                query = quotationQueryProvider.getQueryForMinerviniTrendTemplate();
                break;
            case BREAKOUT_CANDIDATES:
                query = quotationQueryProvider.getQueryForBreakoutCandidatesTemplate();
                break;
            case VOLATILITY_CONTRACTION_10_DAYS:
                query = quotationQueryProvider.getQueryForVolatilityContractionTemplate();
                break;
            case UP_ON_VOLUME:
                query = quotationQueryProvider.getQueryForUpOnVolumeTemplate();
                break;
            case DOWN_ON_VOLUME:
                query = quotationQueryProvider.getQueryForDownOnVolumeTemplate();
                break;
            case NEAR_52_WEEK_HIGH:
                query = quotationQueryProvider.getQueryForNear52WeekHighTemplate();
                break;
            case NEAR_52_WEEK_LOW:
                query = quotationQueryProvider.getQueryForNear52WeekLowTemplate();
                break;
            case HIGH_TIGHT_FLAG:
                query = quotationQueryProvider.getQueryForHighTightFlagTemplate();
                break;
            case SWING_TRADING_ENVIRONMENT:
                query = quotationQueryProvider.getQueryForSwingTradingEnvironmentTemplate();
                break;
            case ALL:
            case RS_SINCE_DATE:
            case THREE_WEEKS_TIGHT:
                query = quotationQueryProvider.getQueryForQuotationsWithInstrument(true);
                break;
            default:
                entityManager.getTransaction().commit();
                entityManager.close();
                return null;
            }

            query.setParameter("quotationIds", quotationIdsWithMaxDate);
            quotations = query.getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception exception) {
            // If something breaks a rollback is necessary!?
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }

        this.scanTemplateProcessor.applyFilters(minLiquidity, quotations);
        this.scanTemplateProcessor.fillTransientAttributes(instrumentType, quotations);
        this.scanTemplateProcessor.templateBasedPostProcessing(scanTemplate, startDate, quotations);

        return quotations;
    }
}
