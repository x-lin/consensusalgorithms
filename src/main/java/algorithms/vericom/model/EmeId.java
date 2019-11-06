package algorithms.vericom.model;

import java.util.Objects;

/**
 * @author LinX
 */
public final class EmeId {
    public static final EmeId EMPTY = new EmeId( "" );

    private final String id;

    public EmeId( final String id ) {
        this.id = id;
    }

    @Override
    public boolean equals( final Object o ) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmeId emeId = (EmeId) o;
        return Objects.equals( this.id, emeId.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.id );
    }

    @Override
    public String toString() {
        return this.id;
    }
}
