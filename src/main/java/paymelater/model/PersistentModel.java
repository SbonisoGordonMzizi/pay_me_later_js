package paymelater.model;

import com.google.common.base.MoreObjects;
import paymelater.WeShareException;
import paymelater.persistence.Persistent;

import java.util.Objects;

public abstract class PersistentModel<T> implements Persistent<T> {
    protected Integer id;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        if (!Objects.isNull(this.id))
            throw new WeShareException("Cannot modify the id of persisent object after it was assigned");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentModel<?> that = (PersistentModel<?>) o;
        return com.google.common.base.Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}
