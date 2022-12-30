package paymelater.persistence;

import java.util.Objects;

/**
 * This interface is used to turn any model class into an object that can be persisted.
 * @param <T> The type of the object to be persisted.
 */
public interface Persistent<T> {

    /**
     * Get the id of the object as assigned by the persistent store
     * @return the unique id of the object
     */
    Integer getId();

    /**
     * Set the id of object.
     * Once the id has been set, it should never be changed.
     * Ouch! Anyone can change it :-(
     * @param id the unique id of the object
     */
    void setId(Integer id);

    /**
     * Check if this object has ever been persisted
     * @return true if the object has never been persisted, at least once.
     */
    default boolean hasNeverBeenSaved() {
        return Objects.isNull(getId());
    }
}
