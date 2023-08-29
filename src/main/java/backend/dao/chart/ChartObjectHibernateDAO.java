package backend.dao.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
    private EntityManagerFactory sessionFactory;

    /**
     * Default constructor.
     *
     * @param sessionFactory The database session factory.
     */
    public ChartObjectHibernateDAO(final EntityManagerFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Inserts a HorizontalLine.
     */
    @Override
    public void insertHorizontalLine(final HorizontalLine horizontalLine) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            entityManager.persist(horizontalLine);
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
     * Deletes a HorizontalLine.
     */
    @Override
    public void deleteHorizontalLine(final HorizontalLine horizontalLine) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // In order to successfully delete an entity, it first has to be fetched from the database.
        HorizontalLine deleteHorizontalLine = entityManager.find(HorizontalLine.class, horizontalLine.getId());

        entityManager.getTransaction().begin();

        try {
            entityManager.remove(deleteHorizontalLine);
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
    }

    /**
     * Gets all horizontal lines of the Instrument with the given ID.
     */
    @Override
    public List<HorizontalLine> getHorizontalLines(final Integer instrumentId) throws Exception {
        Predicate predicate;
        List<Predicate> predicates = new ArrayList<Predicate>();
        List<HorizontalLine> horizontalLines = null;
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // Use entity graphs to load data of referenced Instrument instances.
        EntityGraph<HorizontalLine> graph = entityManager.createEntityGraph(HorizontalLine.class);
        graph.addAttributeNodes("instrument");

        entityManager.getTransaction().begin();

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<HorizontalLine> criteriaQuery = criteriaBuilder.createQuery(HorizontalLine.class);
            Root<HorizontalLine> criteria = criteriaQuery.from(HorizontalLine.class);
            criteriaQuery.select(criteria);

            predicate = applyInstrumentIdParameter(instrumentId, criteriaBuilder, criteria);
            if (predicate != null) {
                predicates.add(predicate);
            }

            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

            criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id"))); // Order by id ascending
            TypedQuery<HorizontalLine> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setHint("jakarta.persistence.loadgraph", graph); // Also fetch all Instrument data.
            horizontalLines = typedQuery.getResultList();

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

        return horizontalLines;
    }

    /**
     * Gets the HorizontalLine with the given ID.
     */
    @Override
    public HorizontalLine getHorizontalLine(final Integer id) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // Use entity graphs to load data of referenced instrument instance.
        EntityGraph<HorizontalLine> graph = entityManager.createEntityGraph(HorizontalLine.class);
        graph.addAttributeNodes("instrument");
        Map<String, Object> hints = new HashMap<String, Object>();
        hints.put("jakarta.persistence.loadgraph", graph);

        entityManager.getTransaction().begin();
        HorizontalLine horizontalLine = entityManager.find(HorizontalLine.class, id, hints);
        entityManager.getTransaction().commit();
        entityManager.close();

        return horizontalLine;
    }

    /**
     * Updates a HorizontalLine.
     */
    @Override
    public void updateHorizontalLine(final HorizontalLine horizontalLine) throws ObjectUnchangedException, Exception {
        EntityManager entityManager;

        this.checkHorizontalLineDataChanged(horizontalLine);

        entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(horizontalLine);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * Checks if the data of the given HorizontalLine differ from the HorizontalLine that is persisted at database
     * level.
     *
     * @param horizontalLine The HorizontalLine to be checked.
     * @throws ObjectUnchangedException Object data did not change.
     * @throws Exception                In case an error occurred during determination of the HorizontalLine stored at
     *                                  the database.
     */
    private void checkHorizontalLineDataChanged(final HorizontalLine horizontalLine)
            throws ObjectUnchangedException, Exception {
        HorizontalLine databaseHorizontalLine = this.getHorizontalLine(horizontalLine.getId());

        if (databaseHorizontalLine.equals(horizontalLine)) {
            throw new ObjectUnchangedException();
        }
    }

    /**
     * Applies the Instrument id parameter to the horizontal lines query.
     *
     * @param instrumentId    The ID of the Instrument whose horizontal lines are requested.
     * @param criteriaBuilder The builder of criteria.
     * @param criteria        The root entity of the HorizontalLine that is being queried.
     * @return A predicate for the Instrument ID.
     */
    private Predicate applyInstrumentIdParameter(final Integer instrumentId, final CriteriaBuilder criteriaBuilder,
            final Root<HorizontalLine> criteria) {
        Predicate predicate;

        if (instrumentId != null) {
            predicate = criteriaBuilder.equal(criteria.get("instrument").get("id"), instrumentId);
        } else {
            predicate = null;
        }

        return predicate;
    }
}
