package algorithms.crowdtruth;

import java.util.Objects;

/**
 * @author LinX
 */
public final class WorkerId {
    private final String id;

    public WorkerId( String id ) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerId workerId = (WorkerId) o;
        return Objects.equals( this.id, workerId.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id );
    }
}
