package algorithms.vericom.model;

import java.util.Objects;

/**
 * @author LinX
 */
public final class TaskWorkerId {
    private final int id;

    public TaskWorkerId( final String id ) {
        this( Integer.parseInt( id ) );
    }

    public TaskWorkerId( final int id ) {
        this.id = id;
    }

    public int toInt() {
        return this.id;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TaskWorkerId that = (TaskWorkerId) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id );
    }

    @Override
    public String toString() {
        return String.valueOf( this.id );
    }
}
