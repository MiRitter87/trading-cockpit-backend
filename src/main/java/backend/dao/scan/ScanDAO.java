package backend.dao.scan;

import java.util.List;

import backend.dao.ObjectUnchangedException;
import backend.model.scan.Scan;

/**
 * Interface for scan persistence.
 *
 * @author Michael
 */
public interface ScanDAO {
    /**
     * Inserts a scan.
     *
     * @param scan The scan to be inserted.
     * @throws Exception Insertion failed.
     */
    void insertScan(Scan scan) throws Exception;

    /**
     * Deletes a scan.
     *
     * @param scan The scan to be deleted.
     * @throws Exception Deletion failed.
     */
    void deleteScan(Scan scan) throws Exception;

    /**
     * Gets all scans.
     *
     * @return All scans.
     * @throws Exception List retrieval failed.
     */
    List<Scan> getScans() throws Exception;

    /**
     * Gets the scan with the given id.
     *
     * @param id The id of the scan.
     * @return The scan with the given id.
     * @throws Exception Scan retrieval failed.
     */
    Scan getScan(Integer id) throws Exception;

    /**
     * Updates the given scan.
     *
     * @param scan The scan to be updated.
     * @throws ObjectUnchangedException In case the data of the scan have not been changed.
     * @throws Exception                Scan update failed.
     */
    void updateScan(Scan scan) throws ObjectUnchangedException, Exception;
}
