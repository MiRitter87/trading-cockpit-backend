package backend.dao.list;

import backend.dao.ObjectUnchangedException;
import backend.model.ObjectInUseException;
import backend.model.list.List;

/**
 * Interface for list persistence.
 *
 * @author Michael
 */
public interface ListDAO {
    /**
     * Inserts a list.
     *
     * @param list The list to be inserted.
     * @throws Exception Insertion failed.
     */
    void insertList(List list) throws Exception;

    /**
     * Deletes a list.
     *
     * @param list The list to be deleted.
     * @throws ObjectInUseException The List is still used by other objects and can't be deleted.
     * @throws Exception            Deletion failed.
     */
    void deleteList(List list) throws ObjectInUseException, Exception;

    /**
     * Gets all lists.
     *
     * @return All lists.
     * @throws Exception List retrieval failed.
     */
    java.util.List<List> getLists() throws Exception;

    /**
     * Gets the list with the given id.
     *
     * @param id The id of the list.
     * @return The list with the given id.
     * @throws Exception List retrieval failed.
     */
    List getList(Integer id) throws Exception;

    /**
     * Updates the given list.
     *
     * @param list The list to be updated.
     * @throws ObjectUnchangedException In case the data of the list have not been changed.
     * @throws Exception                List update failed.
     */
    void updateList(List list) throws ObjectUnchangedException, Exception;
}
