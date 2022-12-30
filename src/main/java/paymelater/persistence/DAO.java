package paymelater.persistence;

import java.util.Collection;
import java.util.Optional;

/**
 * Generic operations for data access objects.
 * Basically CRUD operations.
 * @param <T> The model that is being persisted must implement Persistent interface
 */
public interface DAO<T extends Persistent<T>> {

    /**
     * Save the object.
     * If it does not yet exist in the store, insert it, otherwise update it.
     * @param model the object to save
     * @return the object that was saved with it's id if it was inserted.
     */
    T save(T model);

    /**
     * Retrieve an object by its id
     * @param id The id of the object
     * @return The objected wrapped in Optional. If the object was not found, the Optional is empty.
     */
    Optional<T> findById(Integer id);

    /**
     * Find all objects
     * @return A collection of objects, could be empty.
     */
    Collection<T> findAll();

    /**
     * Returns the number of objects in storage
     * @return the number of objects in storage
     */
    Integer count();

    /**
     * Delete an object
     */
     void delete(Integer id);
}
