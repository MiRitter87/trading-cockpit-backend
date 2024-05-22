package backend.dao.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import backend.dao.ObjectUnchangedException;
import backend.model.LocalizedException;
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
    private EntityManagerFactory sessionFactory;

    /**
     * Default constructor.
     *
     * @param sessionFactory The database session factory.
     */
    public StatisticHibernateDAO(final EntityManagerFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Inserts a Statistic.
     */
    @Override
    public void insertStatistic(final Statistic statistic) throws DuplicateStatisticException, Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        this.checkStatisticExistsCreate(statistic);

        entityManager.getTransaction().begin();

        try {
            entityManager.persist(statistic);
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
     * Deletes a Statistic.
     */
    @Override
    public void deleteStatistic(final Statistic statistic) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // In order to successfully delete an entity, it first has to be fetched from the database.
        Statistic deleteStatistic = entityManager.find(Statistic.class, statistic.getId());

        entityManager.getTransaction().begin();

        try {
            // Remove Instrument.
            entityManager.remove(deleteStatistic);
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
     * Gets all statistics of the given InstrumentType.
     */
    @Override
    public List<Statistic> getStatisticsOfInstrumentType(final InstrumentType instrumentType) throws Exception {
        List<Statistic> statistics = null;
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            List<Predicate> predicates = new ArrayList<Predicate>();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Statistic> criteriaQuery = criteriaBuilder.createQuery(Statistic.class);
            Root<Statistic> criteria = criteriaQuery.from(Statistic.class);
            criteriaQuery.select(criteria);
            criteriaQuery.orderBy(criteriaBuilder.desc(criteria.get("date")));

            predicates.add(criteriaBuilder.equal(criteria.get("instrumentType"), instrumentType));
            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

            TypedQuery<Statistic> typedQuery = entityManager.createQuery(criteriaQuery);
            statistics = typedQuery.getResultList();

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

        return statistics;
    }

    /**
     * Gets all statistics of the given InstrumentType with relation to the given sector or industry group. If no ids
     * are provided, the general statistics are provided irrespective of sector or industry group.
     */
    @Override
    public List<Statistic> getStatistics(final InstrumentType instrumentType, final Integer sectorId,
            final Integer industryGroupId) throws LocalizedException, Exception {
        List<Statistic> statistics = null;
        EntityManager entityManager;
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (sectorId != null && industryGroupId != null) {
            throw new LocalizedException("statistic.errorOnSectorAndIgRequested");
        }

        entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Statistic> criteriaQuery = criteriaBuilder.createQuery(Statistic.class);
            Root<Statistic> criteria = criteriaQuery.from(Statistic.class);
            criteriaQuery.select(criteria);
            criteriaQuery.orderBy(criteriaBuilder.desc(criteria.get("date")));

            predicates.add(criteriaBuilder.equal(criteria.get("instrumentType"), instrumentType));

            if (sectorId == null) {
                predicates.add(criteriaBuilder.isNull(criteria.get("sectorId")));
            } else {
                predicates.add(criteriaBuilder.equal(criteria.get("sectorId"), sectorId));
            }

            if (industryGroupId == null) {
                predicates.add(criteriaBuilder.isNull(criteria.get("industryGroupId")));
            } else {
                predicates.add(criteriaBuilder.equal(criteria.get("industryGroupId"), industryGroupId));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

            TypedQuery<Statistic> typedQuery = entityManager.createQuery(criteriaQuery);
            statistics = typedQuery.getResultList();

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

        return statistics;
    }

    /**
     * Gets the Statistic with the given ID.
     */
    @Override
    public Statistic getStatistic(final Integer id) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Statistic statistic;

        entityManager.getTransaction().begin();
        statistic = entityManager.find(Statistic.class, id);
        entityManager.getTransaction().commit();
        entityManager.close();

        return statistic;
    }

    /**
     * Updates a Statistic.
     */
    @Override
    public void updateStatistic(final Statistic statistic) throws ObjectUnchangedException, Exception {
        EntityManager entityManager;

        this.checkStatisticDataChanged(statistic);

        entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(statistic);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * Gets the Statistic with the given InstrumentType / Date combination.
     *
     * @param instrumentType  The InstrumentType.
     * @param date            The Date.
     * @param sectorId        The id of the sector the Statistic belongs to.
     * @param industryGroupId The id of the industry group the Statistic belongs to.
     * @return The Statistic.
     * @throws Exception In case an error occurred during determination of the Statistic stored at the database.
     */
    private Statistic getStatistic(final InstrumentType instrumentType, final Date date, final Integer sectorId,
            final Integer industryGroupId) throws Exception {
        List<Statistic> statistics = null;
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            List<Predicate> predicates = new ArrayList<Predicate>();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Statistic> criteriaQuery = criteriaBuilder.createQuery(Statistic.class);
            Root<Statistic> criteria = criteriaQuery.from(Statistic.class);
            criteriaQuery.select(criteria);

            predicates.add(criteriaBuilder.equal(criteria.get("instrumentType"), instrumentType));
            predicates.add(criteriaBuilder.equal(criteria.get("date"), date));

            if (sectorId == null) {
                predicates.add(criteriaBuilder.isNull(criteria.get("sectorId")));
            } else {
                predicates.add(criteriaBuilder.equal(criteria.get("sectorId"), sectorId));
            }

            if (industryGroupId == null) {
                predicates.add(criteriaBuilder.isNull(criteria.get("industryGroupId")));
            } else {
                predicates.add(criteriaBuilder.equal(criteria.get("industryGroupId"), industryGroupId));
            }

            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

            TypedQuery<Statistic> typedQuery = entityManager.createQuery(criteriaQuery);
            statistics = typedQuery.getResultList();

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

        if (statistics.size() == 0) {
            return null;
        } else {
            return statistics.get(0);
        }
    }

    /**
     * Checks if the database already contains a statistic with the given InstrumentType / Date combination. This check
     * is used during Statistic creation.
     *
     * @param statistic The Statistic to be checked.
     * @throws DuplicateStatisticException In case an Statistic already exists.
     * @throws Exception                   In case an error occurred during determination of the Statistic stored at the
     *                                     database.
     */
    private void checkStatisticExistsCreate(final Statistic statistic) throws DuplicateStatisticException, Exception {
        Statistic databaseStatistic = this.getStatistic(statistic.getInstrumentType(), statistic.getDate(),
                statistic.getSectorId(), statistic.getIndustryGroupId());

        if (databaseStatistic != null) {
            throw new DuplicateStatisticException(databaseStatistic.getInstrumentType(), databaseStatistic.getDate());
        }
    }

    /**
     * Checks if the data of the given Statistic differ from the Statistic that is persisted at database level.
     *
     * @param statistic The Statistic to be checked.
     * @throws ObjectUnchangedException In case the Statistic has not been changed.
     * @throws Exception                In case an error occurred during determination of the Statistic stored at the
     *                                  database.
     */
    private void checkStatisticDataChanged(final Statistic statistic) throws ObjectUnchangedException, Exception {
        Statistic databaseStatistic = this.getStatistic(statistic.getId());

        if (databaseStatistic.equals(statistic)) {
            throw new ObjectUnchangedException();
        }
    }
}
