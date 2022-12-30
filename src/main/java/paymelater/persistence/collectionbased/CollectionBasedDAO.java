package paymelater.persistence.collectionbased;

import paymelater.persistence.DAO;
import paymelater.persistence.Persistent;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Generic DAO that uses an in-memory collection to store objects
 * @param <T> The Persistent model objects to store
 */
class CollectionBasedDAO<T extends Persistent<T>> implements DAO<T> {
    protected final Map<Integer, T> storage;
    private final AtomicInteger lastId;

    /**
     * Intialise the DAO with an empty collection
     */
    CollectionBasedDAO() {
        lastId = new AtomicInteger(0);
        storage = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T save(T model) {
        return model.hasNeverBeenSaved() ? insert(model) : update(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(Integer id) {
        return storage.containsKey(id) ? Optional.of(storage.get(id)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> findAll() {
        return storage.values().stream().collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer count() {
        return storage.size();
    }

    @Override
    public void delete(Integer id) {
        storage.remove(id);
    }

    private T update(T model) {
        storage.put(model.getId(), model);
        return model;
    }

    private T insert(T model) {
        model.setId(lastId.incrementAndGet());
        return update(model);
    }
}
