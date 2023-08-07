package backend.dao.scan;

/**
 * Exception that indicates that a scan is already in status "IN_PROGRESS".
 *
 * @author Michael
 */
public class ScanInProgressException extends Exception {
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -2620710923251538749L;

    /**
     * The ID of the scan in status "IN_PROGRESS".
     */
    private Integer scanId;

    /**
     * Initializes the Exception.
     *
     * @param scanId The ID of the scan in status "IN_PROGRESS".
     */
    public ScanInProgressException(final Integer scanId) {
        this.scanId = scanId;
    }

    /**
     * @return the scanId
     */
    public Integer getScanId() {
        return scanId;
    }

    /**
     * @param scanId the scanId to set
     */
    public void setScanId(final Integer scanId) {
        this.scanId = scanId;
    }
}
