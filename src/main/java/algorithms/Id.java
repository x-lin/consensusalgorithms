package algorithms;

import java.util.Objects;

/**
 * @author LinX
 */
public abstract class Id<T> {
    private final T id;

    protected Id( final T id ) {
        this.id = id;
    }

    public T getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.valueOf( this.id );
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Id<?> id1 = (Id<?>) o;
        return Objects.equals( this.id, id1.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id );
    }
}
