package algorithms.crowdtruth;

import java.util.Objects;

/**
 * @author LinX
 */
public final class MediaUnitId {
    private final String id;

    public MediaUnitId( String id ) {
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
        MediaUnitId mediaUnit = (MediaUnitId) o;
        return Objects.equals( this.id, mediaUnit.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id );
    }
}
