package backend.dao.scan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import backend.dao.ObjectUnchangedException;
import backend.model.instrument.Instrument;
import backend.model.instrument.InstrumentArray;
import backend.model.scan.Scan;
import backend.model.scan.ScanExecutionStatus;

/**
 * Provides access to scan database persistence using Hibernate.
 *
 * @author Michael
 */
public class ScanHibernateDAO implements ScanDAO {
    /**
     * Factory for database session.
     */
    private EntityManagerFactory sessionFactory;

    /**
     * Default constructor.
     *
     * @param sessionFactory
     */
    public ScanHibernateDAO(final EntityManagerFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Inserts a Scan.
     */
    @Override
    public void insertScan(final Scan scan) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            entityManager.persist(scan);
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
     * Deletes a Scan.
     */
    @Override
    public void deleteScan(final Scan scan) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // In order to successfully delete an entity, it first has to be fetched from the database.
        Scan deleteScan = entityManager.find(Scan.class, scan.getId());

        entityManager.getTransaction().begin();

        try {
            entityManager.remove(deleteScan);
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
     * Gets all scans.
     */
    @Override
    public List<Scan> getScans() throws Exception {
        List<Scan> scans = null;
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // Use entity graphs to load data of referenced list instances.
        EntityGraph<Scan> graph = entityManager.createEntityGraph(Scan.class);
        graph.addAttributeNodes("lists");
        graph.addAttributeNodes("incompleteInstruments");

        entityManager.getTransaction().begin();

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Scan> criteriaQuery = criteriaBuilder.createQuery(Scan.class);
            Root<Scan> criteria = criteriaQuery.from(Scan.class);
            criteriaQuery.select(criteria);
            criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id"))); // Order by id ascending
            TypedQuery<Scan> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setHint("jakarta.persistence.loadgraph", graph); // Also fetch all instrument data.
            scans = typedQuery.getResultList();

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

        return scans;
    }

    /**
     * Gets the Scan with the given ID.
     */
    @Override
    public Scan getScan(final Integer id) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        // Use entity graphs to load data of referenced list instances.
        EntityGraph<Scan> graph = entityManager.createEntityGraph(Scan.class);
        graph.addAttributeNodes("lists");
        graph.addAttributeNodes("incompleteInstruments");
        Map<String, Object> hints = new HashMap<String, Object>();
        hints.put("jakarta.persistence.loadgraph", graph);

        entityManager.getTransaction().begin();
        Scan scan = entityManager.find(Scan.class, id, hints);
        entityManager.getTransaction().commit();
        entityManager.close();

        return scan;
    }

    /**
     * Updates a Scan.
     */
    @Override
    public void updateScan(final Scan scan) throws ObjectUnchangedException, ScanInProgressException, Exception {
        EntityManager entityManager;

        this.updateIncompleteInstruments(scan);
        this.checkScanDataChanged(scan);

        entityManager = this.sessionFactory.createEntityManager();

        this.checkForRunningScans(entityManager, scan);

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
     * @throws Exception                In case an error occurred during determination of the scan stored at the
     *                                  database.
     */
    private void checkScanDataChanged(final Scan scan) throws ObjectUnchangedException, Exception {
        Scan databaseScan = this.getScan(scan.getId());

        if (databaseScan.equals(scan)) {
            throw new ObjectUnchangedException();
        }
    }

    /**
     * Checks if a scan other than the given scan exists, that currently is in status "IN_PROGRESS".
     *
     * @param entityManager The EntityManager used for Query creation.
     * @param scan          The scan exempt from the checkup.
     * @throws ScanInProgressException In case a scan in status "IN_PROGRESS" exists.
     * @throws Exception               In case an error occurred during determination of the scans stored at the
     *                                 database.
     */
    @SuppressWarnings("unchecked")
    private void checkForRunningScans(final EntityManager entityManager, final Scan scan)
            throws ScanInProgressException, Exception {
        Query query;
        List<Object> idsOfRunningScans;

        if (scan.getExecutionStatus() != ScanExecutionStatus.IN_PROGRESS) {
            return;
        }

        query = entityManager
                .createQuery("SELECT id FROM Scan WHERE executionStatus = 'IN_PROGRESS' AND id != :scanId");
        query.setParameter("scanId", scan.getId());

        idsOfRunningScans = query.getResultList();

        if (idsOfRunningScans.size() > 0) {
            throw new ScanInProgressException((Integer) idsOfRunningScans.get(0));
        }
    }

    /**
     * Updates incomplete instruments of the Scan. Those incomplete instruments are removed that are not part of any
     * List within the scan.
     *
     * @param scan The scan to be updated.
     */
    private void updateIncompleteInstruments(final Scan scan) {
        InstrumentArray instrumentsOfLists = new InstrumentArray();
        Iterator<Instrument> instrumentIterator;
        Instrument incompleteInstrument;
        Instrument instrumentOfList;

        instrumentsOfLists.getInstruments().addAll(scan.getInstrumentsFromScanLists());

        // Check for each of the incomplete instruments if the corresponding Instrument is still defined in one of the
        // scans lists.
        instrumentIterator = scan.getIncompleteInstruments().iterator();

        while (instrumentIterator.hasNext()) {
            incompleteInstrument = instrumentIterator.next();
            instrumentOfList = instrumentsOfLists.getInstrumentById(incompleteInstrument.getId());

            if (instrumentOfList == null) {
                instrumentIterator.remove();
            }
        }
    }
}
