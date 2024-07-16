package backend.dao.list;

import java.util.HashMap;
import java.util.Map;

import backend.dao.ObjectUnchangedException;
import backend.model.ObjectInUseException;
import backend.model.list.List;
import backend.model.scan.Scan;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Provides access to list database persistence using Hibernate.
 *
 * @author Michael
 */
public class ListHibernateDAO implements ListDAO {
    /**
     * Factory for database session.
     */
    private EntityManagerFactory sessionFactory;

    /**
     * Default constructor.
     *
     * @param sessionFactory
     */
    public ListHibernateDAO(final EntityManagerFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Inserts a List.
     */
    @Override
    public void insertList(final List list) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            entityManager.persist(list);
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
     * Deletes a List.
     */
    @Override
    public void deleteList(final List list) throws ObjectInUseException, Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        this.checkListInUse(list, entityManager);

        // In order to successfully delete an entity, it first has to be fetched from the database.
        List deleteList = entityManager.find(List.class, list.getId());

        entityManager.getTransaction().begin();

        try {
            entityManager.remove(deleteList);
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
     * Gets all lists.
     */
    @Override
    public java.util.List<List> getLists() throws Exception {
        java.util.List<List> lists = null;
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        EntityGraph<List> graph = entityManager.createEntityGraph(List.class);

        this.addRequestedNodesToGraph(graph);

        entityManager.getTransaction().begin();

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<List> criteriaQuery = criteriaBuilder.createQuery(List.class);
            Root<List> criteria = criteriaQuery.from(List.class);
            criteriaQuery.select(criteria);
            criteriaQuery.orderBy(criteriaBuilder.asc(criteria.get("id"))); // Order by id ascending
            TypedQuery<List> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setHint("jakarta.persistence.loadgraph", graph); // Also fetch all instrument data.
            lists = typedQuery.getResultList();

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

        return lists;
    }

    /**
     * Gets the List with the given ID.
     */
    @Override
    public List getList(final Integer id) throws Exception {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        EntityGraph<List> graph = entityManager.createEntityGraph(List.class);
        Map<String, Object> hints = new HashMap<String, Object>();

        this.addRequestedNodesToGraph(graph);
        hints.put("jakarta.persistence.loadgraph", graph);

        entityManager.getTransaction().begin();
        List list = entityManager.find(List.class, id, hints);
        entityManager.getTransaction().commit();
        entityManager.close();

        return list;
    }

    /**
     * Updates a List.
     */
    @Override
    public void updateList(final List list) throws ObjectUnchangedException, Exception {
        EntityManager entityManager;

        this.checkListDataChanged(list);

        entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(list);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /**
     * Checks if the data of the given list differ from the list that is persisted at database level.
     *
     * @param list The list to be checked.
     * @throws ObjectUnchangedException In case the list has not been changed.
     * @throws Exception                In case an error occurred during determination of the list stored at the
     *                                  database.
     */
    private void checkListDataChanged(final List list) throws ObjectUnchangedException, Exception {
        List databaseList = this.getList(list.getId());

        if (databaseList.equals(list)) {
            throw new ObjectUnchangedException();
        }
    }

    /**
     * Checks if the List is referenced by another business object.
     *
     * @param list          The List which is checked.
     * @param entityManager The active EntityManager used for data access.
     * @throws ObjectInUseException In case the List is in use.
     */
    private void checkListInUse(final List list, final EntityManager entityManager) throws ObjectInUseException {
        this.checkListUsedInScan(list, entityManager);
    }

    /**
     * Checks if the List is referenced by any Scan.
     *
     * @param list          The List which is checked.
     * @param entityManager The EntityManager used to execute queries.
     * @throws ObjectInUseException In case the List is in use.
     */
    @SuppressWarnings("unchecked")
    private void checkListUsedInScan(final List list, final EntityManager entityManager) throws ObjectInUseException {
        Query query = entityManager.createQuery("SELECT s FROM Scan s INNER JOIN s.lists list WHERE list.id = :listId");
        java.util.List<Scan> scans;

        query.setParameter("listId", list.getId());
        scans = query.getResultList();

        if (scans.size() > 0) {
            throw new ObjectInUseException(list.getId(), scans.get(0).getId(), scans.get(0));
        }
    }

    /**
     * Adds nodes and subgraphs with nodes to the given EntityGraph. These represent object associations that are
     * eagerly loaded.
     *
     * @param graph The root EntityGraph of the requested instruments.
     */
    private void addRequestedNodesToGraph(final EntityGraph<List> graph) {
        graph.addAttributeNodes("instruments");
        graph.addSubgraph("instruments").addAttributeNodes("sector", "industryGroup");
    }
}
