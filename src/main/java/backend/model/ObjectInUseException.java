package backend.model;

/**
 * Exception that indicates that an object is used by another object.
 *
 * @author Michael
 *
 */
public class ObjectInUseException extends Exception {
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -4022804719041134734L;

    /**
     * The ID of the object in use.
     */
    private Integer objectInUseId;

    /**
     * The ID of the using object.
     */
    private Integer usedById;

    /**
     * The using object.
     */
    private Object usedByObject;

    /**
     * Constructor.
     *
     * @param objectInUseId The ID of the object in use.
     * @param usedById      The ID of the using object.
     * @param usedByObject  The using object.
     */
    public ObjectInUseException(final Integer objectInUseId, final Integer usedById, final Object usedByObject) {
        this.objectInUseId = objectInUseId;
        this.usedById = usedById;
        this.usedByObject = usedByObject;
    }

    /**
     * @return the objectInUseId
     */
    public Integer getObjectInUseId() {
        return objectInUseId;
    }

    /**
     * @param objectInUseId the objectInUseId to set
     */
    public void setObjectInUseId(final Integer objectInUseId) {
        this.objectInUseId = objectInUseId;
    }

    /**
     * @return the usedById
     */
    public Integer getUsedById() {
        return usedById;
    }

    /**
     * @param usedById the usedById to set
     */
    public void setUsedById(final Integer usedById) {
        this.usedById = usedById;
    }

    /**
     * @return the usedByObject
     */
    public Object getUsedByObject() {
        return usedByObject;
    }

    /**
     * @param usedByObject the usedByObject to set
     */
    public void setUsedByObject(final Object usedByObject) {
        this.usedByObject = usedByObject;
    }
}
